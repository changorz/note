## 线程池

### Executors

**通过Executtors的静态工厂方法可以创建三个线程池的包装对象：**

1. ForkJoinPool 
2. ThreadPoolExecutor
3. ScheduledThreadPoolExecutor

**Executtors的五个核心方法：**

```java
// 返回ForkJoinPool对象，把CPU数量设置为默认的并行度 Runtime.getRuntime().availableProcessors()
Executor pool1 = Executors.newWorkStealingPool();
// 创建一个可伸缩的线程池，如果任务增加，再次创建新线程处理任务，最大为Integer.MAX_VALUE,容易抛出OOM
Executor pool2 = Executors.newCachedThreadPool();
// 返回的是ScheduledExecutorService，与newCachedThreadPool差不多，区别是newScheduledThreadPool不回收工作线程，存在OOM风险
Executor pool3 = Executors.newScheduledThreadPool(3);
// 创建一个单线程的线程池，相当于单线程执行任务，保证按任务的提交顺序执行
Executor pool4 = Executors.newSingleThreadExecutor();
// 创建一个固定大小的线程池
Executor pool5 = Executors.newFixedThreadPool(3);
```

**拒绝策略**

```java
ThreadPoolExecutor.AbortPolicy:丢弃任务并抛出RejectedExecutionException异常。
ThreadPoolExecutor.DiscardPolicy：也是丢弃任务，但是不抛出异常。
ThreadPoolExecutor.DiscardOldestPolicy：丢弃队列最前面的任务，然后重新尝试执行任务（重复此过程）
ThreadPoolExecutor.CallerRunsPolicy：由调用线程处理该任务
```

**自定义线程池**

```java
// 自定义线程池
Executor pool6 = new ThreadPoolExecutor(
        3,     // core
        5,     // max
        5L,    // 线程空闲时间
        TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(5),  // 阻塞队列
        new UserThreadFactory("交易"), 
        new UserRejectHandler()
);
```

**自定义工厂类**

```java
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class UserThreadFactory implements ThreadFactory {
    private final String namePrefix;
    private final AtomicInteger nextId = new AtomicInteger();

    public UserThreadFactory(String group){
        namePrefix = "中国杭州-"+group+"线程-编号-";
    }

    @Override
    public Thread newThread(Runnable r) {
        String name = namePrefix + nextId.getAndIncrement();
        // Thread thread = new Thread(null, r, name, 0);
        // jdk11
        Thread thread = new Thread(null, r, name, 0,false);
        System.out.println(name+"启动成功！");
        return thread;
    }
}

class Task implements Runnable{

    @Override
    public void run() {
        System.out.println("hello world");
    }
}
```

**线程池的最大设置**

​    **计算密集型的任务**，个有*Ncpu*个处理器的系统通常通过使用一个*Ncpu* + 1个线程的线程池来获得最优的利用率

​    **IO 密集型** 

　   *Nthreads* = *Ncpu* x *Ucpu* x (1 + *W/C*)，其中

　　*Ncpu* = *CPU*核心数

　　*Ucpu* = *CPU*使用率，0~1

　　*W/C* = 等待时间与计算时间的比率



**线程池的5种状态**

```java
private static final int COUNT_BITS = 29;
// 得到左边三位 private static final int COUNT_MASK = (1 << COUNT_BITS) - 1
private static final int COUNT_MASK = 536870911;
// 此状态表示线程池还能接受新的任务 RUNNING = COUNT_BITS << -1
private static final int RUNNING = -536870912;
// 此状态表示不再接受新任务，但可以继续执行队列中的任务 SHUTDOWN = COUNT_BITS << 0
private static final int SHUTDOWN = 0;
// 此状态全面拒绝，并中断正在处理的任务 STOP = COUNT_BITS << 1
private static final int STOP = 536870912;
// 表示所有任务已经被终止 TIDYING = COUNT_BITS << 2
private static final int TIDYING = 1073741824;
// 表示已清理完现场 TIDYING = TERMINATED << 3
private static final int TERMINATED = 1610612736;
```



## ForkJoin

**创建**

​    使用ForkJoin框架，需要创建一个ForkJoin的任务，而ForkJoinTask是一个抽象类，我们不需要去继承ForkJoinTask进行使用。因为ForkJoin框架为我们提供了RecursiveAction和RecursiveTask。我们只需要继承ForkJoin为我们提供的抽象类的其中一个并且实现compute方法。

```java
package chang.test2;

import java.util.concurrent.RecursiveTask;

public class ForkJoinTest extends RecursiveTask<Long> {

    private Long start; // 1
    private Long end; // 1990900000
    // 临界值
    private Long temp = 10000L;

    public ForkJoinTest(Long start, Long end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        if ((end - start) < temp) {
            Long sum = 0L;
            for (Long i = start; i <= end; i++) {
                sum += i;
            }
            return sum;
        } else { // forkjoin 递归
            long middle = (start + end) / 2; // 中间值
            ForkJoinTest task1 = new ForkJoinTest(start, middle);
            task1.fork(); // 拆分任务，把任务压入线程队列
            ForkJoinTest task2 = new ForkJoinTest(middle + 1, end);
            task2.fork(); // 拆分任务，把任务压入线程队列
            return task1.join() + task2.join();
        }
    }
}
```

**使用ForkJoinPool进行执行**

​    task要通过ForkJoinPool来执行，分割的子任务也会添加到当前工作线程的双端队列中，
进入队列的头部。当一个工作线程中没有任务时，会从其他工作线程的队列尾部获取一个任务(工作窃取)。

```java
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        ForkJoinTest task = new ForkJoinTest(0L, 10_0000_0000L);
        ForkJoinTask<Long> submit = forkJoinPool.submit(task);// 提交任务
        Long sum = submit.get();
        System.out.println(sum);
    }

}
```

## 异步回调

**创建**

```java
CompletableFuture<Void> future = CompletableFuture.runAsync(()->{})
CompletableFuture<U> uCompletableFuture = CompletableFuture.supplyAsync(() -> {});
```

**使用**

```java
future.get()
```

**有返回值**

```java
CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
      System.out.println("==>异步方法");
      // 运行正确时get获取的是这个返回值
      // int i = 1 / 0;
      return 200;
});

System.out.println(future.whenComplete((t, u) -> {
   System.out.println("t:" + t); // 正常的返回结果   发生错误时为null，执行下面的exceptionally
            System.out.println("u:" + u); // 错误信息： 正确时为null
        }).exceptionally((e) -> {
            System.out.println("e:  " + e);
            // 运行错误时get获取的是这个返回值
            return 500;
        }).get());
```



## JMM

### 请你谈谈你对 Volatile 的理解

Volatile 是 Java 虚拟机提供**轻量级的同步机制**

​	1、保证可见性
​	2、==不保证原子性==
​	3、禁止指令重排



### 什么是JMM

JMM ： Java内存模型，不存在的东西



### 关于JMM的一些同步的约定：

  1、线程解锁前，必须把共享变量**立刻刷回主存**。
  2、线程加锁前，必须**读取**主存中的最新值到工作内存中！
  3、加锁和解锁是同一把锁



![](https://pic.gksec.com/2020/04/05/79d0234842a91/20200405215041.png)



**内存交互操作有8种，虚拟机实现必须保证每一个操作都是原子的，不可在分的（对于double和long类**
**型的变量来说，load、store、read和write操作在某些平台上允许例外）**

- lock （锁定）：作用于主内存的变量，把一个变量标识为线程独占状态
- unlock （解锁）：作用于主内存的变量，它把一个处于锁定状态的变量释放出来，释放后的变量
  才可以被其他线程锁定
- read （读取）：作用于主内存变量，它把一个变量的值从主内存传输到线程的工作内存中，以便
  随后的load动作使用
- load （载入）：作用于工作内存的变量，它把read操作从主存中变量放入工作内存中
- use （使用）：作用于工作内存中的变量，它把工作内存中的变量传输给执行引擎，每当虚拟机
  遇到一个需要使用到变量的值，就会使用到这个指令
- assign （赋值）：作用于工作内存中的变量，它把一个从执行引擎中接受到的值放入工作内存的变
  量副本中
- store （存储）：作用于主内存中的变量，它把一个从工作内存中一个变量的值传送到主内存中，
  以便后续的write使用
- write （写入）：作用于主内存中的变量，它把store操作从工作内存中得到的变量的值放入主内
  存的变量中
==JMM对这八种指令的使用，制定了如下规则：==
- 不允许read和load、store和write操作之一单独出现。即使用了read必须load，使用了store必须
  write
- 不允许线程丢弃他最近的assign操作，即工作变量的数据改变了之后，必须告知主存
- 不允许一个线程将没有assign的数据从工作内存同步回主内存
- 一个新的变量必须在主内存中诞生，不允许工作内存直接使用一个未被初始化的变量。就是怼变量
  实施use、store操作之前，必须经过assign和load操作
  一个变量同一时间只有一个线程能对其进行lock。多次lock后，必须执行相同次数的unlock才能解
  锁
- 如果对一个变量进行lock操作，会清空所有工作内存中此变量的值，在执行引擎使用这个变量前，
  必须重新load或assign操作初始化变量的值
- 如果一个变量没有被lock，就不能对其进行unlock操作。也不能unlock一个被其他线程锁住的变量
- 对一个变量进行unlock操作之前，必须把此变量同步回主内存



## CAS

```java
public static void main(String[] args) {
    AtomicInteger atomicInteger = new AtomicInteger(2020);
    // 加一操作
    atomicInteger.getAndIncrement();
    System.out.println(atomicInteger.get());
    // cas操作 期望、更新
    // 如果我期望的值达到了，那么就更新，否则，就不更新
    atomicInteger.compareAndSet(2021, 666);
    System.out.println(atomicInteger);

}
```

**ABA问题**

```java
        // CAS compareAndSet : 比较并交换！
        public static void main(String[] args) {
            AtomicInteger atomicInteger = new AtomicInteger(2020);
            // 期望、更新
            // public final boolean compareAndSet(int expect, int update)
            // 如果我期望的值达到了，那么就更新，否则，就不更新, CAS 是CPU的并发原语！
            // ============== 捣乱的线程 ==================
            System.out.println(atomicInteger.compareAndSet(2020, 2021));
            System.out.println(atomicInteger.get());
            System.out.println(atomicInteger.compareAndSet(2021, 2020));
            System.out.println(atomicInteger.get());
            // ============== 期望的线程 ==================
            System.out.println(atomicInteger.compareAndSet(2020, 6666));
            System.out.println(atomicInteger.get());
        }
```

**原子引用**

//  AtomicStampedReference 注意，如果泛型是一个包装类，注意对象的引用问题



**AtomicStampedReference与AtomicReference的区别：**

https://blog.csdn.net/zxl1148377834/article/details/90079882



## 博客阅读

浅谈volatile在i++情况下失效：[点我](https://blog.csdn.net/liuzhixiong_521/article/details/85246543?depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromBaidu-1&utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromBaidu-1)



