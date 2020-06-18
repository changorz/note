#### Resource 子类

 **ClassPathResource**可用来获取类路径下的资源文件

 **FileSystemResource**可用来获取文件系统里面的资源。FileSystemResource还可以往对应的资源文件里面写内容，当然前提是当前资源文件是可写的，这可以通过其isWritable()方法来判断。FileSystemResource对外开放了对应资源文件的输出流，可以通过getOutputStream()方法获取到。

 **UrlResource**可用来代表URL对应的资源，它对URL做了一个简单的封装。

 **ByteArrayResource**是针对于字节数组封装的资源，它的构建需要一个字节数组。

**ServletContextResource**是针对于ServletContext封装的资源，用于访问ServletContext环境下的资源。

 **InputStreamResource**是针对于输入流封装的资源，它的构建需要一个输入流。