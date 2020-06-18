## 注解

```java
Override                 //复写方法
Depression               //不推荐使用，过时的方法
SuppressWarnings         //镇压警告 
    
4个元注解
    Target// 注解作用位置
    Retention  //表示在什么级别保存该注解信息。可选的参数值在枚举类型 RetentionPolicy 中
    Inherited  // 子类可以继承父类的注解
    Documented //，它代表着此注解会被javadoc工具提取成文档
    
```



## 反射

**获取**

```java
　　// 第一种：通过类名获得
　　Class<?> class = ClassName.class;

　　// 第二种：通过类名全路径获得：
　　Class<?> class = Class.forName("类名全路径");

　　// 第三种：通过实例对象获得：
　　Class<?> class = object.getClass();
```



与Java反射相关的类如下：

| 类名          | 用途                                             |
| ------------- | ------------------------------------------------ |
| Class类       | 代表类的实体，在运行的Java应用程序中表示类和接口 |
| Field类       | 代表类的成员变量（成员变量也称为类的属性）       |
| Method类      | 代表类的方法                                     |
| Constructor类 | 代表类的构造方法                                 |

阅读博客：[https://www.jianshu.com/p/9be58ee20dee](https://www.jianshu.com/p/9be58ee20dee)

