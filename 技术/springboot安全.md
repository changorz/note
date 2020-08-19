## 前言

​		利用SpringSecurity实现jwt，主要是实现两个拦截器，一个附则登录的拦截，一个负责不是登录的拦截。然后在配置文件添加这个拦截器即可。

​		==登录==拦截继承 `UsernamePasswordAuthenticationFilter类`，重写`attemptAuthentication方法`，也有的教程实现的是`UsernamePasswordAuthenticationFilter类`的父类，`AbstractAuthenticationProcessingFilter`重写这个方法。然后还要需重写==登录成功==和==登录失败==的方法。

​		==授权==拦截器是工具携带的token给用户授权，这个我们一般继承`BasicAuthenticationFilter类`，重写`doFilterInternal方法`。网上也有教程是实现的`OncePerRequestFilter类`

​		其实认证过程就只需要实现两个过滤器，一个的负责登录的UsernamePasswordAuthenticationFilter，一个是负责认证的AbstractAuthenticationProcessingFilter，网上有的教程是实现的他们是父类，其实是一样的。



## 认证流程

**用户认证**:使用UsernamePasswordAuthenticationFilter过滤器中attemptAuthentication方法实现认证功能，该过滤
器父类中successfulAuthentication方法实现认证成功后的操作。

```java
import chang.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class JWTLoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private RsaKeyProperties  prop;

    public JWTLoginAuthenticationFilter(String path, AuthenticationManager authenticationManager, RsaKeyProperties  prop){
        this.authenticationManager = authenticationManager;
        this.prop = prop;
        super.setFilterProcessesUrl(path);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            Map<String, String> map = map = new ObjectMapper().readValue(request.getInputStream(), Map.class);
            String username = map.get("username");
            String password = map.get("password");
            if (username == null) {
                username = "";
            }
            if (password == null) {
                password = "";
            }
            username = username.trim();
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
            return authenticationManager.authenticate(authRequest);
        } catch (IOException e) {
            // 设置返回的状态信息
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            try {
                // 设置返回的内容
                PrintWriter out = response.getWriter();
                HashMap<String, Object> resultMap = new HashMap<>();
                resultMap.put("code", HttpServletResponse.SC_UNAUTHORIZED);
                resultMap.put("msg", "登录失败，用户名或者密码错误！");
                out.write(new ObjectMapper().writeValueAsString(resultMap));
                out.flush();
                out.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            throw new RuntimeException();
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String username = authResult.getName();
        Map<String, Object> jwtMap =  new HashMap<>();
        jwtMap.put("username", username);
        String token = JwtUtils.generateTokenExpireInSeconds(jwtMap, prop.getPrivateKey(), 7200);
        // 返回消息
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        try {
            // 设置返回的内容
            PrintWriter out = response.getWriter();
            HashMap<String, Object> resultMap = new HashMap<>();
            resultMap.put("code", HttpServletResponse.SC_OK);
            resultMap.put("msg", "登录成功！");
            resultMap.put("token", token);
            out.write(new ObjectMapper().writeValueAsString(resultMap));
            out.flush();
            out.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        super.successfulAuthentication(request, response, chain, authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        super.unsuccessfulAuthentication(request, response, failed);
    }
}
```



**身份校验**：使用BasicAuthenticationFilter过滤器中doFilterInternal方法验证是否登录，以决定能否进入后续过滤器。

```java
import chang.mapper.AuthorityMapper;
import chang.pojo.Payload;
import chang.utils.JwtUtils;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private RsaKeyProperties  prop;
    private AuthorityMapper  authorityMapper;

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager, RsaKeyProperties  prop, AuthorityMapper authorityMapper) {
        super(authenticationManager);
        this.prop = prop;
        this.authorityMapper = authorityMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        String tokenHeader = request.getHeader(JwtUtils.TOKEN_HEADER);
        // 如果请求头中没有Authorization信息则直接放行了
        if (tokenHeader == null || !tokenHeader.startsWith(JwtUtils.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }
        // 如果请求头中有token，则进行解析，并且设置认证信息
        try {
            SecurityContextHolder.getContext().setAuthentication(getAuthentication(tokenHeader));
        } catch (Exception e) {
            //返回json形式的错误信息
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            // 设置返回的内容
            PrintWriter out = response.getWriter();
            HashMap<String, Object> resultMap = new HashMap<>();
            resultMap.put("code", HttpServletResponse.SC_FORBIDDEN);
            resultMap.put("token", "-1");
            out.write(new ObjectMapper().writeValueAsString(resultMap));
            out.flush();
            out.close();
            return;
        }
        super.doFilterInternal(request, response, chain);
    }

    // 这里从token中获取用户信息并新建一个token
    private UsernamePasswordAuthenticationToken getAuthentication(String tokenHeader) throws Exception {
        String token = tokenHeader.replace(JwtUtils.TOKEN_PREFIX, "");
        Payload<Map> infoFromToken = JwtUtils.getInfoFromToken(token, prop.getPublicKey(), Map.class);
        String username = JSON.parseObject(infoFromToken.getUserInfo().toString()).get("username").toString();
        List<String> authoritys = authorityMapper.getAuthorityByUsername(username);
        ArrayList<SimpleGrantedAuthority> simpleGrantedAuthorities = new ArrayList<>();
        authoritys.forEach(e -> simpleGrantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + e.toString())));
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, null, simpleGrantedAuthorities);
        return usernamePasswordAuthenticationToken;
    }
}
```

参考：https://www.cnblogs.com/dalianpai/p/12416487.html



**配置类**

```java
package chang.config;

import chang.mapper.AuthorityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@RequiredArgsConstructor
public class ChangWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final RsaKeyProperties rsaKeyProperties;
    private final AuthorityMapper authorityMapper;

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/admin/**").access("hasRole('admin')")
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated()   // 任何请求,登录后可以访问
                .and()
                .addFilter(new JWTLoginAuthenticationFilter("/login", authenticationManager(), rsaKeyProperties))
                .addFilter(new JWTAuthorizationFilter(authenticationManager(), rsaKeyProperties, authorityMapper));
    }
}
```



**Maven**

```java
		<dependency>
			<groupId>p6spy</groupId>
			<artifactId>p6spy</artifactId>
			<version>3.9.1</version>
		</dependency>

		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt</artifactId>
			<version>0.9.0</version>
		</dependency>

		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>fastjson</artifactId>
			<version>1.2.70</version>
		</dependency>
```



**其他文件**

```java
package chang.utils;

import chang.pojo.Payload;
import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * @author: chang
 * 生成token以及校验token相关方法
 */
public class JwtUtils {

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * iss: jwt签发者
     * sub: jwt所面向的用户
     * aud: 接收jwt的一方
     * exp: jwt的过期时间，这个过期时间必须要大于签发时间
     * nbf: 定义在什么时间之前，该jwt都是不可用的.
     * iat: jwt的签发时间
     * jti: jwt的唯一身份标识，主要用来作为一次性token,从而回避重放攻击。
     */


    private static final String JWT_PAYLOAD_USER_KEY = "userInfo";

    /**
     * 私钥加密token
     *
     * @param userInfo   载荷中的数据
     * @param privateKey 私钥
     * @param expire     过期时间，单位秒
     * @return JWT
     */
    public static String generateTokenExpireInSeconds(Object userInfo, PrivateKey privateKey, int expire) {
        Map<String, Object> map = new HashMap<>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");
        return Jwts.builder()
                .setHeader(map)
                .claim(JWT_PAYLOAD_USER_KEY, JSON.toJSONString(userInfo))
                .setId(createJTI())
                .setExpiration(Date.from(LocalDateTime.now().plusSeconds(expire).atZone( ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .setAudience("APP")
                .compact();
    }

    /**
     * 公钥解析token
     *
     * @param token     用户请求中的token
     * @param publicKey 公钥
     * @return Jws<Claims>
     */
    private static Jws<Claims> parserToken(String token, PublicKey publicKey) {
        return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token);
    }

    private static String createJTI() {
        return new String(Base64.getEncoder().encode(UUID.randomUUID().toString().getBytes()));
    }

    /**
     * 获取token中的用户信息
     *
     * @param token     用户请求中的令牌
     * @param publicKey 公钥
     * @return 用户信息
     */
    public static <T> Payload<T> getInfoFromToken(String token, PublicKey publicKey, Class<T> userType) {
        Jws<Claims> claimsJws = parserToken(token, publicKey);
        Claims body = claimsJws.getBody();
        Payload<T> claims = new Payload<>();
        claims.setId(body.getId());
        claims.setUserInfo((T)JSON.parse(body.get(JWT_PAYLOAD_USER_KEY).toString()));
        claims.setExpiration(body.getExpiration());
        return claims;
    }

    /**
     * 获取token中的载荷信息
     *
     * @param token     用户请求中的令牌
     * @param publicKey 公钥
     * @return 用户信息
     */
    public static <T> Payload<T> getInfoFromToken(String token, PublicKey publicKey) {
        Jws<Claims> claimsJws = parserToken(token, publicKey);
        Claims body = claimsJws.getBody();
        Payload<T> claims = new Payload<>();
        claims.setId(body.getId());
        claims.setExpiration(body.getExpiration());
        return claims;
    }
}
```

```java
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RsaUtils {

    private static final int DEFAULT_KEY_SIZE = 2048;

    /**
     * 从文件中读取公钥
     *
     * @throws Exception
     */
    public static PublicKey getPublicKey(String path) throws Exception {
        byte[] bytes = readFile(path);
        return getPublicKey(bytes);
    }

    /**
     * 从文件中读取私钥
     *
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String path) throws Exception {
        byte[] bytes = readFile(path);
        return getPrivateKey(bytes);
    }

    /**
     * 获取公钥
     *
     * @param bytes 公钥的字节形式
     * @return
     * @throws Exception
     */
    private static PublicKey getPublicKey(byte[] bytes) throws Exception {
        bytes = Base64.getDecoder().decode(bytes);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePublic(spec);
    }

    /**
     * 获取密钥
     *
     * @param bytes 私钥的字节形式
     * @return
     * @throws Exception
     */
    private static PrivateKey getPrivateKey(byte[] bytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        bytes = Base64.getDecoder().decode(bytes);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePrivate(spec);
    }


    // ==============================================================================================================

    /**
     * 秘钥，SECRET不为空自动生成公钥和私钥
     */
    private String SECRET = "chang";
    /**
     * 自动生成公钥和私钥时的路径
     */
    private String PATH_NAME = "F:\\jwt";

    public static void main(String[] args) {
        // 生成文件
        try {
            new RsaUtils().generateKey();
            System.out.println("创建成功！");
        } catch (Exception e) {
            System.out.println("创建失败！！！");
            e.printStackTrace();
        }
    }

    /**
     * 根据密文，生存rsa公钥和私钥,并写入指定文件
     *
     *  publicKeyFilename  公钥文件路径
     *  privateKeyFilename 私钥文件路径
     *  secret             生成密钥的密文
     */
    public void generateKey() throws Exception {
        String publicKeyFilename = this.PATH_NAME + File.separator +"private.key";
        String privateKeyFilename = this.PATH_NAME + File.separator + "rsa_public.pub";
        String secret = this.SECRET;
        int keySize = secret.length();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom secureRandom = new SecureRandom(secret.getBytes());
        keyPairGenerator.initialize(Math.max(keySize, DEFAULT_KEY_SIZE), secureRandom);
        KeyPair keyPair = keyPairGenerator.genKeyPair();
        // 获取公钥并写出
        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
        publicKeyBytes = Base64.getEncoder().encode(publicKeyBytes);
        writeFile(publicKeyFilename, publicKeyBytes);
        // 获取私钥并写出
        byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
        privateKeyBytes = Base64.getEncoder().encode(privateKeyBytes);
        writeFile(privateKeyFilename, privateKeyBytes);
    }

    private static byte[] readFile(String fileName) throws Exception {
        return Files.readAllBytes(new File(fileName).toPath());
    }

    private static void writeFile(String destPath, byte[] bytes) throws IOException {
        File dest = new File(destPath);
        if (!dest.exists()) {
            dest.createNewFile();
        }
        Files.write(dest.toPath(), bytes);
    }
}
```

```java

import chang.utils.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.PrivateKey;
import java.security.PublicKey;

@Data
@Component
@ConfigurationProperties(prefix = "rsa.key")
public class RsaKeyProperties {
    private String publicKeyFile;
    private String privateKeyFile;

    private PublicKey publicKey;
    private PrivateKey privateKey;

    @PostConstruct
    public void createRsaKey() throws Exception {
        publicKey = RsaUtils.getPublicKey(publicKeyFile);
        privateKey = RsaUtils.getPrivateKey(privateKeyFile);
    }
}

```



## 分布式认证

听说分布式认证最优方案是`springCloud ` + `springSecurity`  +  `OAuth2.0`

