# springboot swagger2 使用

## 依赖

```xml
<!--swagger2-->
<dependency>
   <groupId>io.springfox</groupId>
   <artifactId>springfox-swagger2</artifactId>
   <version>2.9.2</version>
</dependency>

<dependency>
   <groupId>io.springfox</groupId>
   <artifactId>springfox-swagger-ui</artifactId>
   <version>2.9.2</version>
</dependency>
```



## springboot配置

```java
package chang.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class ChangSwagger2Config {
    /**
     * 通过 createRestApi函数来构建一个DocketBean
     * 函数名,可以随意命名,喜欢什么命名就什么命名
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())//调用apiInfo方法,创建一个ApiInfo实例,里面是展示在文档页面信息内容
                .select()
                //控制暴露出去的路径下的实例
                //如果某个接口不想暴露,可以使用以下注解
                //@ApiIgnore 这样,该接口就不会暴露在 swagger2 的页面下
                .apis(RequestHandlerSelectors.basePackage("chang.controller"))
                .paths(PathSelectors.any())
                .build();
    }
    //构建 api文档的详细信息函数
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                //页面标题
                .title("Spring Boot Swagger2 构建RESTful API")
                //条款地址
                .termsOfServiceUrl("http://despairyoke.github.io/")
                .contact("zwd")
                .version("1.0")
                //描述
                .description("API 描述")
                .build();
    }
}
```

**多组配置**

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2Config {
    /**
     * 通过 createRestApi函数来构建一个DocketBean
     * 函数名,可以随意命名,喜欢什么命名就什么命名
     */
    @Bean
    public Docket createRestApi1() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo1())//调用apiInfo方法,创建一个ApiInfo实例,里面是展示在文档页面信息内容
                .enable(true)
                .groupName("后台")
                .select()
                //控制暴露出去的路径下的实例
                //如果某个接口不想暴露,可以使用以下注解
                //@ApiIgnore 这样,该接口就不会暴露在 swagger2 的页面下
                .apis(RequestHandlerSelectors.basePackage("chang.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    public Docket createRestApi2() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo2())//调用apiInfo方法,创建一个ApiInfo实例,里面是展示在文档页面信息内容
                .enable(true)
                .groupName("微信相关")
                .select()
                .apis(RequestHandlerSelectors.basePackage("chang.controllerWx"))
                .paths(PathSelectors.any())
                .build();
    }

    //构建 api文档的详细信息函数
    private ApiInfo apiInfo1() {
        return new ApiInfoBuilder()
                //页面标题
                .title("教务后台api")
                .contact("chang")
                .version("1.0")
                //简介
                .description("API接口描述")
                .build();
    }

    //构建 api文档的详细信息函数
    private ApiInfo apiInfo2() {
        return new ApiInfoBuilder()
                //页面标题
                .title("教务后台api")
                .contact("chang")
                .version("1.1")
                //简介
                .description("微信接口描述")
                .build();
    }
}

```

## 注解解释

```java
@Api：用在请求的类上，表示对类的说明
    tags="说明该类的作用，可以在UI界面上看到的注解"
    value="该参数没什么意义，在UI界面上也看到，所以不需要配置"

@ApiOperation：用在请求的方法上，说明方法的用途、作用
    value="说明方法的用途、作用"
    notes="方法的备注说明"

@ApiImplicitParams：用在请求的方法上，表示一组参数说明
    @ApiImplicitParam：用在@ApiImplicitParams注解中，指定一个请求参数的各个方面
        name：参数名
        value：参数的汉字说明、解释
        required：参数是否必须传
        paramType：参数放在哪个地方
            · header --> 请求参数的获取：@RequestHeader
            · query --> 请求参数的获取：@RequestParam
            · path（用于restful接口）--> 请求参数的获取：@PathVariable
            · body（不常用）
            · form（不常用）    
        dataType：参数类型，默认String，其它值dataType="Integer"       
        defaultValue：参数的默认值

@ApiResponses：用在请求的方法上，表示一组响应
    @ApiResponse：用在@ApiResponses中，一般用于表达一个错误的响应信息
        code：数字，例如400
        message：信息，例如"请求参数没填好"
        response：抛出异常的类

@ApiModel：用于响应类上，表示一个返回响应数据的信息
            （这种一般用在post创建的时候，使用@RequestBody这样的场景，
            请求参数无法使用@ApiImplicitParam注解进行描述的时候）
    @ApiModelProperty：用在属性上，描述响应类的属性
```

**参考博客：**[https://www.jianshu.com/p/f30e0c646c63](https://www.jianshu.com/p/f30e0c646c63)



## 演示

```java
package chang.controller;

import chang.pojo.User;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Test测试" ,description="用于用户接口新增、修改、删除以及查询！")
@RestController
@RequestMapping("/test")
public class TestController {


    @GetMapping("/test1")
    @ApiOperation("这个是:/test/test1")
    public String test1(){
        return "test1";
    }

    @PostMapping("/test2")
    @ApiOperation("这个是:/test/test2")
    @ApiImplicitParams({
            @ApiImplicitParam(name="msg",value="传入信息",required=true,paramType="query")
    })
    public String test2(String msg){
        return msg;
    }

    @GetMapping("/test3")
    @ApiOperation("这个是:/test/test3")
    @ApiImplicitParams({
            @ApiImplicitParam(name="msg",value="传入信息",required=true,paramType="query")
    })
    @ApiResponses({
            @ApiResponse(code=400,message="请求参数没填好"),
            @ApiResponse(code=404,message="请求路径没有或页面跳转路径不对")
    })
    public String test3(String msg){
        return msg+"哈哈";
    }


    @GetMapping("/test4")
    @ApiOperation(value = "这个是:/test/test4",notes = "这个是备注")
    public User test4(){
        return new User();
    }


}
```

```java
package chang.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value="用户实体类",description="作为系统中的用户实体，是系统的核心操作人员")
public class User implements Serializable {

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty(value = "年龄",allowEmptyValue = true)
    private String age;

}
```

## 效果

![image-20200228205130175](https://ae01.alicdn.com/kf/H715f5424717a494cb25c767b97c86ab3D.png)

![image-20200228205140003](https://ae01.alicdn.com/kf/H5c75e16805624f17a9dc291974bcd9214.png)

![image-20200228205157717](https://ae01.alicdn.com/kf/H8d3b0dd5951549b5911896aadae48aedE.png)

![image-20200228205206524](https://ae01.alicdn.com/kf/Ha4e3d1d59143474fb6dd5bd5e1f1316a7.png)

---------------------------------------------分割线------------------------------------------------

### 安利一个好看的框架

参考博客：[https://blog.csdn.net/minkeyto/article/details/104541942?depth_1-utm_source=distribute.pc_relevant.none-task&utm_source=distribute.pc_relevant.none-task](https://blog.csdn.net/minkeyto/article/details/104541942?depth_1-utm_source=distribute.pc_relevant.none-task&utm_source=distribute.pc_relevant.none-task)



1.替换Maven （替换第一步那两个依赖）

```xml
<dependency>
   <groupId>com.github.xiaoymin</groupId>
   <artifactId>knife4j-spring-boot-starter</artifactId>
   <version>2.0.1</version>
</dependency>
```

2.其他的都不要变

访问：

http://localhost:8080/doc.html