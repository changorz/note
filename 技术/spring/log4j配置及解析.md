### log4j配置文件

**示例:**

```properties
# Set root category priority to INFO and its only appender to CONSOLE.
#log4j.rootCategory=INFO, CONSOLE            debug   info   warn error fatal
log4j.rootCategory=debug, CONSOLE, LOGFILE

# Set the enterprise logger category to FATAL and its only appender to CONSOLE.
log4j.logger.org.apache.axis.enterprise=FATAL, CONSOLE

# CONSOLE is set to be a ConsoleAppender using a PatternLayout.
log4j.appender.CONSOLE=org.apache.log4j.ConsoleAppender
log4j.appender.CONSOLE.layout=org.apache.log4j.PatternLayout
log4j.appender.CONSOLE.layout.ConversionPattern=%d{ISO8601} %-6r [%15.15t] %-5p %30.30c %x - %m%n

LOGFILE is set to be a File appender using a PatternLayout.
log4j.appender.LOGFILE=org.apache.log4j.FileAppender
log4j.appender.LOGFILE.File=d:\axis.log
log4j.appender.LOGFILE.Append=true
log4j.appender.LOGFILE.layout=org.apache.log4j.PatternLayout
log4j.appender.LOGFILE.layout.ConversionPattern=%d{ISO8601} %-6r [%15.15t] %-5p %30.30c %x - %m%n

```



**1、log4j.rootCategory=INFO, stdout , R ** `(重点)`

> 此句为将等级为INFO的日志信息输出到stdout和R这两个目的地，stdout和R的定义在下面的代码，`可以任意起名`。等级可分为OFF、FATAL、ERROR、WARN、INFO、[DEBUG](https://baike.baidu.com/item/DEBUG)、ALL，如果配置OFF则不打出任何信息，如果配置为[INFO](https://baike.baidu.com/item/INFO)这样只显示INFO、WARN、ERROR的[log](https://baike.baidu.com/item/log)信息，而DEBUG信息不会被显示，具体讲解可参照第三部分定义配置文件中的logger。



**2、log4j.appender.stdout=org.apache.log4j.ConsoleAppender**

> 此句为定义名为`[stdout]`的输出端是哪种类型，可以是
>
> org.apache.log4j.ConsoleAppender（控制台），
>
> org.apache.log4j.FileAppender（文件），
>
> org.apache.log4j.DailyRollingFileAppender（每天产生一个日志文件），
>
> org.apache.log4j.RollingFileAppender（文件大小到达指定尺寸的时候产生一个新的文件）
>
> org.apache.log4j.WriterAppender（将日志信息以流格式发送到任意指定的地方）



**3、log4j.appender.`stdout`.layout=org.apache.log4j.PatternLayout**

> 此句为定义名为stdout的输出端的layout是哪种类型，可以是
>
> org.apache.log4j.HTMLLayout（以[HTML](https://baike.baidu.com/item/HTML)表格形式布局），
>
> org.apache.log4j.PatternLayout（可以灵活地指定布局模式），
>
> org.apache.log4j.SimpleLayout（包含日志信息的级别和信息[字符串](https://baike.baidu.com/item/字符串)），
>
> org.apache.log4j.TTCCLayout（包含日志产生的时间、[线程](https://baike.baidu.com/item/线程)、类别等等信息）
>
> 具体讲解可参照第三部分定义配置文件中的Layout。



**4、log4j.appender.`stdout`.layout.ConversionPattern= [QC] %p [%t] %C.%M(%L) | %m%n**

> 如果使用pattern布局就要指定的打印信息的具体格式ConversionPattern，打印参数如下：
>
> %m 输出代码中指定的消息；
>
> %M 输出打印该条日志的方法名；
>
> %p 输出优先级，即DEBUG，[INFO](https://baike.baidu.com/item/INFO)，WARN，ERROR，FATAL；
>
> %r 输出自应用启动到输出该log信息耗费的毫秒数；
>
> %c 输出所属的类目，通常就是所在类的全名；
>
> %t 输出产生该日志事件的线程名；
>
> %n 输出一个回车换行符，Windows平台为"rn”，Unix平台为"n”；
>
> %d 输出日志时间点的日期或时间，默认格式为ISO8601，也可以在其后指定格式，比如：%d{yyyy-MM-dd HH:mm:ss,SSS}，输出类似：2002-10-18 22:10:28,921；
>
> %l 输出日志事件的发生位置，及在代码中的行数；
>
> [QC]是log信息的开头，可以为任意字符，一般为项目简称。
>
> 输出的信息
>
> [TS] DEBUG [main] AbstractBeanFactory.getBean(189) | Returning cached instance of singleton bean 'MyAutoProxy'
>
> 具体讲解可参照第三部分定义配置文件中的格式化日志信息。



**5、log4j.appender.`R`=org.apache.log4j.DailyRollingFileAppender**

> 定义名为R的输出端的类型为每天产生一个日志文件。



**6、log4j.appender.`R`.File=D:\\Tomcat 5.5\\logs\\qc.log**

> 此句为定义名为R的输出端的文件名为D:\\Tomcat 5.5\\logs\\qc.log可以自行修改。



**7、log4j.appender.`R`.layout=org.apache.log4j.PatternLayout**

> 输出的日志布局



**8、log4j.appender.`R`.layout.ConversionPattern=%d-[TS] %p %t %c - %m%n**

> 自定义格式输出，参考4点的格式



**9、log4j.logger.com. neusoft =DEBUG**

> 指定com.neusoft包下的所有类的等级为DEBUG。



**10、log4j.logger.com.opensymphony.oscache=ERROR**

**11、log4j.logger.net.sf.navigator=ERROR**

> 这两句是把这两个包下出现的错误的等级设为ERROR，如果项目中没有配置EHCache(二级缓存)，则不需要这两句。



**12、log4j.logger.org.apache.commons=ERROR**

**13、log4j.logger.org.apache.struts=WARN**

> 这两句是struts的包。



**14、log4j.logger.org.displaytag=ERROR**

> 这句是displaytag的包。（QC问题列表页面所用）



**15、log4j.logger.org.springframework=DEBUG**

> 此句为Spring的包。



**16、log4j.logger.org.hibernate.ps.PreparedStatementCache=WARN**

**17、log4j.logger.org.hibernate=DEBUG**

> 此两句是hibernate的包。
>
> 以上这些包的设置可根据项目的实际情况而自行定制。





------------------

#### 2020/12/18

> 配置文件

Log4J配置文件的基本格式如下：

```properties
#配置根Logger
log4j.rootLogger  =   [ level ]   ,  appenderName ,  appenderName1 ,  …

#配置日志信息输出目的地Appender及Appender选项
log4j.appender.appenderName = fully.qualified.name.of.appender.class 　　
log4j.appender.appenderName.option = value1
 　　　　… 　　
log4j.appender.appenderName.optionN = valueN 
#配置日志信息的格式（布局）及格式布局选项 
appender.appenderName.layout = fully.log4j.qualified.name.of.layout.class
log4j.appender.appenderName.layout.option1 = value1
 　　　　… 　　
log4j.appender.appenderName.layout.optionN = valueN
```

- 其中 [ level ] 是日志输出级别：`ERROR`、`WARN`、`INFO`、`DEBUG`
  `RROR `为严重错误 主要是程序的错误
  `WARN `为一般警告，比如session丢失
  `INFO `为一般要显示的信息，比如登录登出
  `DEBUG` 为程序的调试信息
- ==appenderName==是日志输出位置的配置的命名



> log4j.appender.`appenderName` = `XXXX`中`XXXX`应换上以下信息输出的目的地：

```java
org.apache.log4j.ConsoleAppender（控制台）
org.apache.log4j.FileAppender（文件）
org.apache.log4j.DailyRollingFileAppender（每天产生一个日志文件）
org.apache.log4j.RollingFileAppender（文件大小到达指定尺寸的时候产生一个新的文件）
org.apache.log4j.WriterAppender（将日志信息以流格式发送到任意指定的地方）
```

> appender.`appenderName`.`layout` = `XXXX`中`XXXX`格式布局应换上以下信息：

```java
org.apache.log4j.HTMLLayout（以HTML表格形式布局）
org.apache.log4j.PatternLayout（可以灵活地指定布局模式）
org.apache.log4j.SimpleLayout（包含日志信息的级别和信息字符串）
org.apache.log4j.TTCCLayout（包含日志产生的时间、线程、类别等等信息）
```

> **其他：**log4j.appender.`appenderName`.`option`中`option`应替换的属性/选项

```java
1.ConsoleAppender控制台选项
　　　　Threshold=DEBUG:指定日志消息的输出最低层次。
　　　　ImmediateFlush=true:默认值是true,意味着所有的消息都会被立即输出。
　　　　Target=System.err：默认情况下是：System.out,指定输出控制台

2.FileAppender 文件选项
　　　　Threshold=DEBUF:指定日志消息的输出最低层次。
　　　　ImmediateFlush=true:默认值是true,意谓着所有的消息都会被立即输出。
　　　　File=mylog.txt:指定消息输出到mylog.txt文件。
　　　　Append=false:默认值是true,即将消息增加到指定文件中，false指将消息覆盖指定的文件内容。

3.RollingFileAppender 每天生成一个文件选项
　　　　Threshold=DEBUG:指定日志消息的输出最低层次。
　　　　ImmediateFlush=true:默认值是true,意谓着所有的消息都会被立即输出。
　　　　File=mylog.txt:指定消息输出到mylog.txt文件。
　　　　Append=false:默认值是true,即将消息增加到指定文件中，false指将消息覆盖指定的文件内容。
　　　　MaxFileSize=100KB: 后缀可以是KB, MB 或者是 GB. 在日志文件到达该大小时，将会自动滚动，即将原来的内容移到mylog.log.1文件。
　　　　MaxBackupIndex=2:指定可以产生的滚动文件的最大数。
        
```



#### 参考代码

```properties
### 配置根 ###
log4j.rootLogger = debug,console ,fileAppender,dailyRollingFile,ROLLING_FILE,MAIL,DATABASE

### 设置输出sql的级别，其中logger后面的内容全部为jar包中所包含的包名 ###
log4j.logger.org.apache=dubug
log4j.logger.java.sql.Connection=dubug
log4j.logger.java.sql.Statement=dubug
log4j.logger.java.sql.PreparedStatement=dubug
log4j.logger.java.sql.ResultSet=dubug
### 配置输出到控制台 ###
log4j.appender.console = org.apache.log4j.ConsoleAppender
log4j.appender.console.Target = System.out
log4j.appender.console.layout = org.apache.log4j.PatternLayout
log4j.appender.console.layout.ConversionPattern =  %d{ABSOLUTE} %5p %c{ 1 }:%L - %m%n

### 配置输出到文件 ###
log4j.appender.fileAppender = org.apache.log4j.FileAppender
log4j.appender.fileAppender.File = logs/log.log
log4j.appender.fileAppender.Append = true
log4j.appender.fileAppender.Threshold = DEBUG
log4j.appender.fileAppender.layout = org.apache.log4j.PatternLayout
log4j.appender.fileAppender.layout.ConversionPattern = %-d{yyyy-MM-dd HH:mm:ss}  [ %t:%r ] - [ %p ]  %m%n

### 配置输出到文件，并且每天都创建一个文件 ###
log4j.appender.dailyRollingFile = org.apache.log4j.DailyRollingFileAppender
log4j.appender.dailyRollingFile.File = logs/log.log
log4j.appender.dailyRollingFile.Append = true
log4j.appender.dailyRollingFile.Threshold = DEBUG
log4j.appender.dailyRollingFile.layout = org.apache.log4j.PatternLayout
log4j.appender.dailyRollingFile.layout.ConversionPattern = %-d{yyyy-MM-dd HH:mm:ss}  [ %t:%r ] - [ %p ]  %m%n### 配置输出到文件，且大小到达指定尺寸的时候产生一个新的文件 ###log4j.appender.ROLLING_FILE=org.apache.log4j.RollingFileAppender log4j.appender.ROLLING_FILE.Threshold=ERROR log4j.appender.ROLLING_FILE.File=rolling.log log4j.appender.ROLLING_FILE.Append=true log4j.appender.ROLLING_FILE.MaxFileSize=10KB log4j.appender.ROLLING_FILE.MaxBackupIndex=1 log4j.appender.ROLLING_FILE.layout=org.apache.log4j.PatternLayout log4j.appender.ROLLING_FILE.layout.ConversionPattern=[framework] %d - %c -%-4r [%t] %-5p %c %x - %m%n

### 配置输出到邮件 ###
log4j.appender.MAIL=org.apache.log4j.net.SMTPAppender
log4j.appender.MAIL.Threshold=FATAL
log4j.appender.MAIL.BufferSize=10
log4j.appender.MAIL.From=chenyl@yeqiangwei.com
log4j.appender.MAIL.SMTPHost=mail.hollycrm.com
log4j.appender.MAIL.Subject=Log4J Message
log4j.appender.MAIL.To=chenyl@yeqiangwei.com
log4j.appender.MAIL.layout=org.apache.log4j.PatternLayout
log4j.appender.MAIL.layout.ConversionPattern=[framework] %d - %c -%-4r [%t] %-5p %c %x - %m%n

### 配置输出到数据库 ###
log4j.appender.DATABASE=org.apache.log4j.jdbc.JDBCAppender
log4j.appender.DATABASE.URL=jdbc:mysql://localhost:3306/test
log4j.appender.DATABASE.driver=com.mysql.jdbc.Driver
log4j.appender.DATABASE.user=root
log4j.appender.DATABASE.password=
log4j.appender.DATABASE.sql=INSERT INTO LOG4J (Message) VALUES ('[framework] %d - %c -%-4r [%t] %-5p %c %x - %m%n')
log4j.appender.DATABASE.layout=org.apache.log4j.PatternLayout
log4j.appender.DATABASE.layout.ConversionPattern=[framework] %d - %c -%-4r [%t] %-5p %c %x - %m%n
log4j.appender.A1=org.apache.log4j.DailyRollingFileAppender
log4j.appender.A1.File=SampleMessages.log4j
log4j.appender.A1.DatePattern=yyyyMMdd-HH'.log4j'
log4j.appender.A1.layout=org.apache.log4j.xml.XMLLayout
```

```
ConversionPattern 日志信息，符号所代表的含义：

 -X号: X信息输出时左对齐；
 %p: 输出日志信息优先级，即DEBUG，INFO，WARN，ERROR，FATAL,
 %d: 输出日志时间点的日期或时间，默认格式为ISO8601，也可以在其后指定格式，比如：%d{yyy MMM dd HH:mm:ss,SSS}，输出类似：2002年10月18日 22：10：28，921
 %r: 输出自应用启动到输出该log信息耗费的毫秒数
 %c: 输出日志信息所属的类目，通常就是所在类的全名
 %t: 输出产生该日志事件的线程名
 %l: 输出日志事件的发生位置，相当于%C.%M(%F:%L)的组合,包括类目名、发生的线程，以及在代码中的行数。举例：Testlog4.main (TestLog4.java:10)
 %x: 输出和当前线程相关联的NDC(嵌套诊断环境),尤其用到像java servlets这样的多客户多线程的应用中。
 %%: 输出一个"%"字符
 %F: 输出日志消息产生时所在的文件名称
 %L: 输出代码中的行号
 %m: 输出代码中指定的消息,产生的日志具体信息
 %n: 输出一个回车换行符，Windows平台为"\r\n"，Unix平台为"\n"输出日志信息换行
 可以在%与模式字符之间加上修饰符来控制其最小宽度、最大宽度、和文本的对齐方式。如：
 1)%20c：指定输出category的名称，最小的宽度是20，如果category的名称小于20的话，默认的情况下右对齐。
 2)%-20c:指定输出category的名称，最小的宽度是20，如果category的名称小于20的话，"-"号指定左对齐。
 3)%.30c:指定输出category的名称，最大的宽度是30，如果category的名称大于30的话，就会将左边多出的字符截掉，但小于30的话也不会有空格。
 4)%20.30c:如果category的名称小于20就补空格，并且右对齐，如果其名称长于30字符，就从左边较远输出的字符截掉。
```

