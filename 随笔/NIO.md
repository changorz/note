## NIO

### 个人理解

非阻塞IO，单线程解决多个客户端访问，BIO中的accept和read方法都会阻塞

原有的 IO 是面向流的、阻塞的，NIO 则是面向块的、非阻塞的。

### NIO的核心实现

通道Channel

缓存Buffer

Selector：解决轮询效率慢



### 客户端

```java
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class Main0 {
    public static void main(String[] args) throws IOException {
        String host = "localhost";
        int prot = 9898;
        // tcp 创建链接
        Socket socket = new Socket(host, prot);

        OutputStream out = socket.getOutputStream();
        BufferedReader sc = new BufferedReader(new InputStreamReader(System.in));
        String str = sc.readLine();
        out.write(str.getBytes("utf-8"));

        out.close();
        socket.close();
    }
}
```

### 轮询方式解决阻塞

```java
import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class Main3 {

    public static ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    public static List<SocketChannel> socketChannelList = new ArrayList<>();
    public static void main(String[] args) throws IOException {
        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        socketChannel.bind(new InetSocketAddress(9696));
        // 给 accept 方法解阻塞
        socketChannel.configureBlocking(false);

        while(true){
            // 先遍历list
            socketChannelList.forEach(socket->{
                try {
                    // 读取数据
                    int read = socket.read(byteBuffer);
                    if(read > 0){
                        System.out.println("read ---size--- " + read);
                        byteBuffer.flip();
                        byte[] bt = new byte[read];
                        byteBuffer.get(bt); 
                        byteBuffer.clear();

                        System.out.println(new String(bt,0,read,"utf-8"));
                        System.out.println("读取成功");
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            });

            SocketChannel socket= socketChannel.accept();
            if(socket != null){
                System.out.println("连接成功,加入list");
                // read 解阻塞 要是没有发送内容，会跳过，所以要加入list
                socket.configureBlocking(false);
                socketChannelList.add(socket);
            }
        }
    }
}
```

### NIO简单http服务器

```java
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Iterator;

public class Main {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(9898));
        serverSocket.configureBlocking(false);
        // Selector
        Selector selector = Selector.open();
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        while( true ){
            if( selector.select(3000) == 0 ){
                System.out.print(".");
                continue;
            }
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while(iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if(key.isAcceptable()) {
                    // 接收就绪，获取客户端连接
                    System.out.println("==>1");
                    SocketChannel socket = serverSocket.accept();
                    socket.configureBlocking(false);
                    socket.register(selector, SelectionKey.OP_READ);
                } else if (key.isConnectable()) {
                    System.out.println("==>2");
                    // a connection was established with a remote server.
                } else if (key.isReadable()) {
                    System.out.println("==>3");
                    // a channel is ready for reading
                    // 获取读就绪通道
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    int len;
                    /*
                    最后一种情况就是客户端的数据发送完毕了（注意看后面的程序里有这样子的代码），这个时候客户端想获取服务端的反馈调用了recv函数，若服务端继续read，这个时候就会返回0。
                    实际写代码过程中观察发现，如果客户端发送数据后不关闭channel，同时服务端收到数据后反倒再次发给客户端，那么此时客户端read方法永远返回0.

                    总结：当客户端发送的是文件，而且大小未知的情况，服务端如何判断对方已经发送完毕。如单纯的判断是否等于0，可能会导致客户端发送的数据不完整。所以，
                    这里加了一个检测0出现次数的判断，来判断客户端是否确实是数据发送完毕了，当然这个方法是比较笨拙的方法，大家若有更好的方法，期待大家给我答案。
                     */
                    int count = 0;
                    while((len = channel.read(buffer)) != -1){
                        buffer.flip();
                        System.out.println(new String(buffer.array(),0,len,"utf-8"));
                        buffer.clear();
                        if(len == 0){
                            count++;
                            if (count == 10){
                                break;
                            }
                        }
                    }
                    channel.register(selector,SelectionKey.OP_WRITE);
                } else if (key.isWritable()) {
                    // a channel is ready for writing
                    System.out.println("==4");
                    SocketChannel channel = (SocketChannel) key.channel();
                    FileChannel file = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
                    StringBuffer sb = new StringBuffer();
                    sb.append("HTTP/1.1 200 OK\n");
                    // sb.append("Content-Type: text/html;charset=UTF-8\n");
                    sb.append("Content-Type: image/jpeg;charset=UTF-8\n");
                    sb.append("Server: nginx\n");
                    sb.append("Date: " + LocalDateTime.now() + "\n\n");
//                    sb.append("  <html>\n" +
//                            "          <head>\n" +
//                            "            <title>$Title$</title>\n" +
//                            "          </head>\n" +
//                            "          <body>\n" +
//                            "          hello , response\n" +
//                            "          </body>\n" +
//                            "        </html>");
                    ByteBuffer buffer = ByteBuffer.allocate(sb.toString().getBytes("utf-8").length);
                    buffer.put(sb.toString().getBytes("utf-8"));
                    buffer.flip();
                    channel.write(buffer);
                    file.transferTo(0,file.size(),channel);
                    channel.close();
                }
                iterator.remove();
            }
        }
    }
}
```



参考博客：[https://www.cnblogs.com/juniorMa/p/5887796.html](https://www.cnblogs.com/juniorMa/p/5887796.html)

[https://blog.csdn.net/forezp/article/details/88414741](https://blog.csdn.net/forezp/article/details/88414741)





