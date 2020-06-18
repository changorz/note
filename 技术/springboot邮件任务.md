### 邮件任务

	#### 1.引入依赖

```xml
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

#### 2. 配置文件

```yaml
spring:
  mail:
    # 发件人邮箱
    username: 158****746@qq.com
    # 生成的授权码
    password: ao*********cbafh
    # QQ的SMIP地址
    host: smtp.qq.com
    properties:
      mail:
        smtp:
          ssl:
            enable: true
```

#### 3.测试

```java
package chang.service;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.internet.MimeMessage;
import java.io.File;

@RunWith(SpringRunner.class)
@SpringBootTest
public class mailTest {


    //注入邮件发送器
    @Autowired
    JavaMailSenderImpl javaMailSender;

    /**
     * 简单邮件测试
     */
    @Test
    public void contextLoads1() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setText("今晚7点钟开会");
        message.setSubject("通知-开会");
        //发送者邮箱
        message.setFrom("1584541746@qq.com");
        //发送到哪个邮箱
        message.setTo("1584541746@qq.com");
        javaMailSender.send(message);
    }

    /**
     * 复杂邮件测试
     */
    @Test
    public void contextLoads2() throws Exception{
        //1.创建一个复杂的消息邮件
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        //第二个参数  是否需要上传附件
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
        //邮件设置
        //这里可以使用html标签样式
        helper.setText("<b style='color:red'>今晚7点钟开会</b>",true);
        helper.setSubject("通知-开会");
        //发送者邮箱
        helper.setFrom("1584541746@qq.com");
        //发送到哪个邮箱
        helper.setTo("1584541746@qq.com");
        //上传附件 附件名，路径
        helper.addAttachment("1.jpg",new File("C:\\1.jpg"));
        javaMailSender.send(mimeMessage);
    }


}
```

