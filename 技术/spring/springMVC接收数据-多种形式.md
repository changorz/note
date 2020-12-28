```java
/**
 * 表单数据提交，用map接收字段数据，用file接收文件
 * 缺点：如果某字段要提交一个map，无法自动转成map
 * @param map
 * @param flie
 */

@GetMapping("/getNean")
public void t1(@RequestParam Map map, @RequestParam(name = "file", required = false) MultipartFile flie){

};

/**
 * 这样可以通过pojo的bean接收表单参数
 * @param bean
 * @param flie
 */
@GetMapping("/t2")
public void t2(Object bean, @RequestParam(name = "file", required = false) MultipartFile flie){

};

// 接收json数据
// 实体类接收
@GetMapping("/t3")
public void t3(@RequestBody Object bean){
};

// map接收
@GetMapping("/t4")
public void t4(@RequestBody Map map){
};
```