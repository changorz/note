# springBoot 集成 Mybatis

1. 导入依赖

   可以用IDEA创建的时候导入，也可以后期导入

   

```xml
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-data-jdbc</artifactId>
</dependency>
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
   <groupId>org.mybatis.spring.boot</groupId>
   <artifactId>mybatis-spring-boot-starter</artifactId>
   <version>2.1.1</version>
</dependency>

<dependency>
   <groupId>mysql</groupId>
   <artifactId>mysql-connector-java</artifactId>
   <scope>runtime</scope>
</dependency>
<dependency>
   <groupId>org.projectlombok</groupId>
   <artifactId>lombok</artifactId>
   <optional>true</optional>
</dependency>
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-test</artifactId>
   <scope>test</scope>
   <exclusions>
      <exclusion>
         <groupId>org.junit.vintage</groupId>
         <artifactId>junit-vintage-engine</artifactId>
      </exclusion>
   </exclusions>
</dependency>

<dependency>
   <groupId>com.alibaba</groupId>
   <artifactId>druid</artifactId>
   <version>1.1.21</version>
</dependency>
      <dependency>
          <groupId>junit</groupId>
          <artifactId>junit</artifactId>
          <scope>test</scope>
      </dependency>

<!-- log start -->
<dependency>
   <groupId>log4j</groupId>
   <artifactId>log4j</artifactId>
   <version>1.2.12</version>
</dependency>
<dependency>
   <groupId>org.slf4j</groupId>
   <artifactId>slf4j-api</artifactId>
</dependency>
<dependency>
   <groupId>org.slf4j</groupId>
   <artifactId>slf4j-log4j12</artifactId>
</dependency>
```



2. 配置连接池

   ```java
   @ConfigurationProperties(prefix = "spring.datasource")
   @Bean
   public DruidDataSource druidDataSource(){
       return new DruidDataSource();
   }
   ```

3. 用到的配置文件

   ```yaml
   server:
     port: 80
   
   
   spring:
     datasource:
       #数据源基本配置
       type: com.alibaba.druid.pool.DruidDataSource
       url: jdbc:mysql://localhost:3306/syxy?useSSL=true&useUnicode=true&characterEncoding=utf8&serverTimezone=UTC
       username: root
       password: root
       #数据源其他配置
       initialSize: 5
       minIdle: 5
       maxActive: 20
       maxWait: 60000
       timeBetweenEvictionRunsMillis: 60000
       minEvictableIdleTimeMillis: 300000
       validationQuery: SELECT 1 FROM DUAL
       testWhileIdle: true
       testOnBorrow: false
       testOnReturn: false
       poolPreparedStatements: true
       #配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
       filters: stat,wall
       maxPoolPreparedStatementPerConnectionSize: 20
       useGlobalDataSourceStat: true
       connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500
   
   #mybatis的配置
   mybatis:
     typeAliasesPackage: chang.pojo
     mapperLocations: classpath:mapper/*.xml
   # 这个包的日为debug，可以打印SQL语句
   logging:
     level:
       chang:
         mapper: debug
   ```

4. druid 的监控配置

   ```java
   /**
    * 配置监控服务器
    * @return 返回监控注册的servlet对象
    * @author SimpleWu
    */
   @Bean
   public ServletRegistrationBean statViewServlet() {
       ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
       // 添加IP白名单
       servletRegistrationBean.addInitParameter("allow", "127.0.0.1");
       // 添加IP黑名单，当白名单和黑名单重复时，黑名单优先级更高
       servletRegistrationBean.addInitParameter("deny", "127.0.0.2");
       // 添加控制台管理用户
       servletRegistrationBean.addInitParameter("loginUsername", "root");
       servletRegistrationBean.addInitParameter("loginPassword", "123456");
       // 是否能够重置数据
       servletRegistrationBean.addInitParameter("resetEnable", "false");
       return servletRegistrationBean;
   }
   
   /**
    * 配置服务过滤器
    *
    * @return 返回过滤器配置对象
    */
   @Bean
   public FilterRegistrationBean statFilter() {
       FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
       // 添加过滤规则
       filterRegistrationBean.addUrlPatterns("/*");
       // 忽略过滤格式
       filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*,");
       return filterRegistrationBean;
   }
   ```

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
```



参考博客：

**Druid配置**https://www.cnblogs.com/SimpleWu/p/10049825.html

**JDBCTemplate的使用** [https://blog.csdn.net/qq_23329167/article/details/81841996](https://blog.csdn.net/qq_23329167/article/details/81841996)





示例：

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="chang.dao.MenuItemMapper">

    <select id="findAllMenuItem" resultType="MenuItem">
        select * from menuitem
    </select>

    <insert id="addMenuItem" parameterType="MenuItem"  useGeneratedKeys="true" keyProperty="id">
        insert into menuitem(weight, title, icon, text, tourl) value (#{weight},#{title},#{icon},#{text},#{tourl})
    </insert>

    <delete id="deleteMenuItem" parameterType="int">
        delete from menuitem where id = #{id}
    </delete>

    <update id="updataMenuItem" parameterType="map">
        update menuitem
        <set>
            <if test="weight != null">weight = #{weight}</if>
            <if test="title != null">title = #{title}</if>
            <if test="icon != null">icon = #{icon}</if>
            <if test="text != null">text = #{text}</if>
            <if test="tourl != null">tourl = #{tourl}</if>
        </set>
        where id = #{id}
    </update>

</mapper>
```

