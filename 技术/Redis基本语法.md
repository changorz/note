## springboot配置
### Maven

```xml
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

### 配置

```yaml
spring:
  redis:
    database: 0
    host: 106.13.168.109
    port: 6379
    password: dcw@1234567890
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        max-wait: -1ms
        min-idle: 0
```



```java
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        // 创建RedisTemplate<String, Object>对象
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 配置连接工厂
        template.setConnectionFactory(factory);
        // 定义Jackson2JsonRedisSerializer序列化对象
        Jackson2JsonRedisSerializer<Object> jacksonSeial = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会报异常
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jacksonSeial.setObjectMapper(om);
        StringRedisSerializer stringSerial = new StringRedisSerializer();
        // redis key 序列化方式使用stringSerial
        template.setKeySerializer(stringSerial);
        // redis value 序列化方式使用jackson
        template.setValueSerializer(jacksonSeial);
        // redis hash key 序列化方式使用stringSerial
        template.setHashKeySerializer(stringSerial);
        // redis hash value 序列化方式使用jackson
        template.setHashValueSerializer(jacksonSeial);
        template.afterPropertiesSet();
        return template;
    }

}
```




## Redis基本语法

### 存储字符串string

字符串类型是Redis中最为基础的数据存储类型，它在Redis中是二进制安全的，这便意味着该类型可以接受任何格式的数据，如JPEG图像数据或Json对象描述信息等。在Redis中字符串类型的Value最多可以容纳的数据长度是512M。

`set key value`：设定key持有指定的字符串value，如果该key存在则进行覆盖操作。总是返回”OK”

`get key`：获取key的value。如果与该key关联的value不是String类型，redis将返回错误信息，因为get命令只能用于获取String value；如果该key不存在，返回null。

`getset key value`：先获取(输出)该key的值，然后在设置该key的值。

`del key`  :  删除key的值 

`incr key`：将指定的key的value原子性的递增1。如果该key不存在，其初始值为0，在incr之后其值为1。如果value的值不能转成整型，如”hello”，该操作将执行失败并返回相应的错误信息。

`decr key`：将指定的key的value原子性的递减1。如果该key不存在，其初始值为0，在incr之后其值为-1。如果value的值不能转成整型，如”hello”，该操作将执行失败并返回相应的错误信息。

`incrby key increment`：将指定的key的value原子性增加increment。如果该key不存在，初始值为0，在incrby之后，该值为increment。如果该值不能转成整型，如hello则失败并返回错误信息。

`decrby key decrement`：将指定的key的value原子性减少decrement。如果该key不存在，初始值为0，在decrby之后，该值为decrement。如果该值不能转成整型，如hello则失败并返回错误信息。

`append key value`：如果该key存在，则在原有的value后追加该值（即拼接子字符串）。如果该 key不存在，则重新创建一个key/value



### 存储hash

Redis中的Hashes类型可以看成具有String Key和String Value的map容器。所以该类型非常适合于存储值对象的信息。如Username、Password和Age等。如果 Hash中包含很少的字段，那么该类型的数据也将仅占用很少的磁盘空间。每一个Hash可以存储4294967295个键值对。

`hset key field value`：为指定的key设定field/value对（键值对）。

`hgetall key`：获取key中的所有filed-vaule。

`hget key field`：返回指定的key中的field的值。

`hmset key fields`：设置key中的多个filed/value。

`hmget key fileds`：获取key中的多个filed的值。

`hdel key filed`：删除key中的指定filed

`del key`：删除key的hash值

`hincrby key field increment`：设置key中filed的值增加increment

`hexists key field`：判断指定的key中的filed是否存在

`hlen key`：获取key所包含的field的数量

`hkeys key`：获取key中所有fieldname值

`hvals key`：获取key中所有fieldvalue值



### 存储list
在Redis中，List类型是按照插入顺序排序的字符串链表。和数据结构中的普通链表一样，我们可以在其头部(left)和尾部(right)添加新的元素。在插入时，如果该键并不存在，Redis将为该键创建一个新的链表。与此相反，如果链表中所有的元素均被移除，那么该键也将会被从数据库中删除。List中可以包含的最大元素数量是 4294967295。从元素插入和删除的效率视角来看，如果我们是在链表的两头插入或删除元素，这将会是非常高效的操作，即使链表中已经存储了百万条记录，该操作也可以在常量时间内完成。然而需要说明的是，如果元素插入或删除操作是作用于链表中间，那将会是非常低效的。相信对于有良好数据结构基础的开发者而言，这一点并不难理解。



`lpush key value1 value2…`：在指定的key所关联的list的头部插入所有的values，如果该key不存在，该命令在插入的之前创建一个与该key关联的空链表，之后再向该链表的头部插入数据。插入成功，返回元素的个数。[注意：一个一个都从左边插入，如ipush key value1 value2，则插入结果是value2 value1]

`rpush key value1 value2…`：在该list的尾部添加元素

`lrange key start end`：获取链表中从start到end的元素的值（从0开始），start、end可为负数，若为-1则表示链表尾部的元素，-2则表示倒数第二个，依次类推…

`lpushx key value`：仅当参数中指定的key存在时（如果与key管理的list中没有值时，则该key不会创建）在指定的key所关联的list的头部插入value

`rpushx key value`：在该list的尾部添加元素

`lpop key`：返回并弹出指定的key关联的链表中的第一个元素，即头部元素

`rpop key`：从尾部弹出元素

`rpoplpush resource destination`：将resource集合中的尾部元素弹出并添加到destination集合头部

`llen key`：返回指定的key关联的链表中的元素的数量

`lrem key count value`：删除count个值为value的元素，如果count大于0，从头向尾遍历并删除count个值为value的元素；如果count小于0，则从尾向头遍历并删除。如果count等于0，则删除链表中所有等于value的元素。

`lset key index value`：设置链表中的index的脚标的元素值（相当于替换），0代表链表的头元素，-1代表链表的尾元素。

`linsert key before|after pivot value`：在pivot元素前或者后插入value这个元素。



### 存储set

在Redis中，我们可以将Set类型看作为没有排序的字符集合，和List类型一样，我们也可以在该类型的数据值上执行添加、删除或判断某一元素是否存在等操作。需要说明的是，这些操作的时间是常量时间。Set可包含的最大元素数是4294967295。和List类型不同的是，Set集合中不允许出现重复的元素。和List类型相比，Set类型在功能上还存在着一个非常重要的特性，即在服务器端完成多个Sets之间的聚合计算操作，如unions、intersections和differences。由于这些操作均在服务端完成，因此效率极高，而且也节省了大量的网络IO开销。



`sadd key value1 value2…`：向set中添加数据，如果该key的值已有则不会重复添加

`smembers key`：获取set中所有的成员

`srem key member1 member2…`：删除set中指定的成员

`sismember key member`：判断参数中指定的成员是否在该set中，1表示存在，0表示不存在或者该key本身就不存在

`scard key`：获取set中成员的数量

`sdiff key1 key2`：返回key1与key2中相差的成员（key1-key2），而且与key的顺序有关。即返回差集

`sdiffstore destination key1 key2`：将key1、key2相差的成员存储在 destination上

`sinter key1 key2`：返回key1和key2的交集

`sinterstore destination key1 key2`：将返回的交集存储在destination上

`sunion key1 key2`：返回并集

`sunionstore destination key1 key2`：将返回的并集存储在destination上

`srandmember key`：随机返回set中的一个成员



### 存储sortedset

Sorted-Sets和Sets类型极为相似，它们都是字符串的集合，都不允许重复的成员出现在一个Set中。它们之间的主要差别是Sorted-Sets中的每一个成员都会有一个分数(score)与之关联，Redis正是通过分数来为集合中的成员进行从小到大的排序。然而需要额外指出的是，尽管Sorted-Sets中的成员必须是唯一的，但是分数(score) 却是可以重复的。

在Sorted-Set中添加、删除或更新一个成员都是非常快速的操作，其时间复杂度为集合中成员数量的对数。由于Sorted-Sets中的成员在集合中的位置是有序的，因此，即便是访问位于集合中部的成员也仍然是非常高效的。事实上，Redis所具有的这一特征在很多其它类型的数据库中是很难实现的，换句话说，在该点上要想达到和Redis同样的高效，在其它数据库中进行建模是非常困难的。



`zadd key score member score2 member2 … `：将所有成员以及该成员的权重分数存放到sorted-set中

`zrange key start end [withscores]`：获取集合中脚标为start-end的成员，[withscores]参数表明返回的成员包含其分数。

`zrevrange key start end [withscores]`：倒序获取集合中脚标为start-end的成员，[withscores]参数表明返回的成员包含其分数。

zrangebyscore key min max [withscores][limit offset count]：返回分数在[min,max]的成员并按照分数从低到高排序。[withscores]：显示分数；[limit offset count]：offset，表明从脚标为offset的元素开始并返回count个成员

`zcount key min max`：获取分数在[min,max]之间的成员数量

`zcard key`：获取集合中的成员数量

`zscore key member`：返回指定成员的分数

`zrem key member[member…]`：移除集合中指定的成员，可以指定多个成员

`zremrangebyrank key start end`：按照排名范围(从小到大排名)删除元素（start和end为排名位置）

`zremrangebyscore key min max`：按照分数(从小到大排名)删除元素（min和max为分数）

`zincrby key increment member`：设置指定成员的增加的分数，返回值是更改后的分数

`zrank key member`：返回成员在集合中的位置

`zrevrank key member`：逆序返回成员在集合中的位置



**keys的通用操作**
`keys pattern`：获取所有与pattern匹配的key，但会所有与该key匹配的keys，*表示任意一个或多个字符，？表示任意一个字符

`del key1 key2…`：删除指定的key

`exists key`：判断key是否存在，1代表存在，0代表不存在

`rename key newkey`：为当前的key重命名

`expire key time`：为key设置过期时间，单位秒

ttl key：获取该key所剩的超时时间，如果没有设置超时返回-1，如果返回-2表示超时了（不存在），如果没超时还存在则返回剩余时间

type key：获取指定key的类型，该命令将以字符串的格式返回。返回的字符串为string、list、set、hash和zset。如果key不存在返回none



**参考博客**：https://blog.csdn.net/qq_34829447/article/details/81942202





## Redis RedisTemplate 使用总结

### Redis的String数据结构 

- void set(K key, V value);

```java
redisTemplate.opsForValue().set("num","123");
redisTemplate.opsForValue().get("num")  输出结果为123
```

- void set(K key, V value, long timeout, TimeUnit unit); 

```java
redisTemplate.opsForValue().set("num","123",10, TimeUnit.SECONDS);
redisTemplate.opsForValue().get("num")设置的是10秒失效，十秒之内查询有结果，十秒之后返回为null
```

- void set(K key, V value, long offset);  覆写(overwrite)给定 key 所储存的字符串值，从偏移量 offset 开始

```java
template.opsForValue().set("key","hello world");
template.opsForValue().set("key","redis", 6);
System.out.println("***************"+template.opsForValue().get("key"));
结果：***************hello redis
```

- V getAndSet(K key, V value);   设置键的字符串值并返回其旧值

```java
template.opsForValue().set("getSetTest","test");
System.out.println(template.opsForValue().getAndSet("getSetTest","test2"));
结果：test
```

- Integer append(K key, String value);    如果key已经存在并且是一个字符串，则该命令将该值追加到字符串的末尾。如果键不存在，则它被创建并设置为空字符串，因此APPEND在这种特殊情况下将类似于SET。

```java
template.opsForValue().append("test","Hello");
System.out.println(template.opsForValue().get("test"));
template.opsForValue().append("test","world");
System.out.println(template.opsForValue().get("test"));
Hello
Helloworld
```



### Redis的List数据结构

- Long size(K key);返回存储在键中的列表的长度。如果键不存在，则将其解释为空列表，并返回0。当key存储的值不是列表时返回错误。

```java
System.out.println(template.opsForList().size("list"));
6
```

- Long leftPush(K key, V value);
  将所有指定的值插入存储在键的列表的头部。如果键不存在，则在执行推送操作之前将其创建为空列表。（从左边插入）

```java
template.opsForList().leftPush("list","java");
template.opsForList().leftPush("list","python");
template.opsForList().leftPush("list","c++");
返回的结果为推送操作后的列表的长度
1
2
3
```

- Long leftPushAll(K key, V... values);   批量把一个数组插入到列表中

```java 
String[] strs = new String[]{"1","2","3"};
template.opsForList().leftPushAll("list",strs);
System.out.println(template.opsForList().range("list",0,-1));
[3, 2, 1]
```

- Long rightPush(K key, V value);      将所有指定的值插入存储在键的列表的头部。如果键不存在，则在执行推送操作之前将其创建为空列表。（从右边插入）

```java
template.opsForList().rightPush("listRight","java");
template.opsForList().rightPush("listRight","python");
template.opsForList().rightPush("listRight","c++");
1
2
3
```

- Long rightPushAll(K key, V... values);

```java
String[] strs = new String[]{"1","2","3"};
template.opsForList().rightPushAll("list",strs);
//range 打印0到最后的值
System.out.println(template.opsForList().range("list",0,-1));
[1, 2, 3]
```

- Long rightPush(K key, V value);

```java
template.opsForList().rightPush("listRight","java");
template.opsForList().rightPush("listRight","python");
template.opsForList().rightPush("listRight","c++");
1
2
3
```

- Long rightPushAll(K key, V... values);

```java
String[] strs = new String[]{"1","2","3"};
template.opsForList().rightPushAll("list",strs);
System.out.println(template.opsForList().range("list",0,-1));
[1, 2, 3]
```

- void set(K key, long index, V value);   在列表中index的位置设置value值

```java
System.out.println(template.opsForList().range("listRight",0,-1));
template.opsForList().set("listRight",1,"setValue");
System.out.println(template.opsForList().range("listRight",0,-1));
[java, python, oc, c++]
[java, setValue, oc, c++]
```

- Long remove(K key, long count, Object value);
  从存储在键中的列表中删除等于值的元素的第一个计数事件。
  计数参数以下列方式影响操作：
  `count> 0`：删除等于从头到尾移动的值的元素。
  `count <0`：删除等于从尾到头移动的值的元素。
  `count = 0`：删除等于value的所有元素。

```java
System.out.println(template.opsForList().range("listRight",0,-1));
template.opsForList().remove("listRight",1,"setValue");//将删除列表中存储的列表中第一次次出现的“setValue”。
System.out.println(template.opsForList().range("listRight",0,-1));
[java, setValue, oc, c++]
[java, oc, c++]
```

- V index(K key, long index);   根据下表获取列表中的值，下标是从0开始的

```java
System.out.println(template.opsForList().range("listRight",0,-1));
System.out.println(template.opsForList().index("listRight",2));
[java, oc, c++]
c++
```

- V leftPop(K key);  弹出最左边的元素，弹出之后该值在列表中将不复存在

```java
System.out.println(template.opsForList().range("list",0,-1));
System.out.println(template.opsForList().leftPop("list"));
System.out.println(template.opsForList().range("list",0,-1));
[c++, python, oc, java, c#, c#]
c++
[python, oc, java, c#, c#]
```

- V rightPop(K key); 弹出最右边的元素，弹出之后该值在列表中将不复存在

```java
System.out.println(template.opsForList().range("list",0,-1));
System.out.println(template.opsForList().rightPop("list"));
System.out.println(template.opsForList().range("list",0,-1));
[python, oc, java, c#, c#]
c#
[python, oc, java, c#]
```



### Redis的Hash数据机构

- Long delete(H key, Object... hashKeys);  删除给定的哈希hashKeys

```java
System.out.println(template.opsForHash().delete("redisHash","name"));
//entries 打印这个哈希表
System.out.println(template.opsForHash().entries("redisHash"));
1
{class=6, age=28.1}
```

- Boolean hasKey(H key, Object hashKey);  确定哈希hashKey是否存在

```java
System.out.println(template.opsForHash().hasKey("redisHash","666"));
System.out.println(template.opsForHash().hasKey("redisHash","777"));
true
false
```

- Object get(H key, Object hashKey);

```java
System.out.println(template.opsForHash().get("redisHash","age"));
26
```

- Set<HK> keys(H key); 获取key所对应的散列表的key

```java
System.out.println(template.opsForHash().keys("redisHash"));
//redisHash所对应的散列表为{class=1, name=666, age=27}
[name, class, age]
```

- Long size(H key);  获取key所对应的散列表的大小个数

```java
System.out.println(template.opsForHash().size("redisHash"));
//redisHash所对应的散列表为{class=1, name=666, age=27}
3
```

- void putAll(H key, Map<? extends HK, ? extends HV> m);  使用m中提供的多个散列字段设置到key对应的散列表中

```java
Map<String,Object> testMap = new HashMap();
testMap.put("name","666");
testMap.put("age",27);
testMap.put("class","1");
template.opsForHash().putAll("redisHash1",testMap);
System.out.println(template.opsForHash().entries("redisHash1"));
{class=1, name=jack, age=27}
```

-  void put(H key, HK hashKey, HV value);  **设置散列hashKey的值**

 ```java
template.opsForHash().put("redisHash","name","666");
template.opsForHash().put("redisHash","age",26);
template.opsForHash().put("redisHash","class","6");
System.out.println(template.opsForHash().entries("redisHash"));
{age=26, class=6, name=666}
 ```

-  List<HV> values(H key);

```java
System.out.println(template.opsForHash().values("redisHash"));
[tom, 26, 6]
```

-  Map<HK, HV> entries(H key);  **获取整个哈希存储根据密钥**

```java
System.out.println(template.opsForHash().entries("redisHash"));
{age=26, class=6, name=tom}
```

- List<HV> values(H key)  获取整个哈希存储的值根据密钥

```java
System.out.println(template.opsForHash().values("redisHash"));
[tom, 26, 6]
```

- Map<HK, HV> entries(H key);  获取整个哈希存储根据密钥

```java
System.out.println(template.opsForHash().entries("redisHash"));
{age=26, class=6, name=tom}
```

- Cursor<Map.Entry<HK, HV>> scan(H key, ScanOptions options);
  使用Cursor在key的hash中迭代，相当于迭代器。

```java
Cursor<Map.Entry<Object, Object>> curosr = template.opsForHash().scan("redisHash", 
  ScanOptions.ScanOptions.NONE);
    while(curosr.hasNext()){
        Map.Entry<Object, Object> entry = curosr.next();
        System.out.println(entry.getKey()+":"+entry.getValue());
    }
age:27
class:6
name:666
```



### Redis的Set数据结构

- Long add(K key, V... values);   无序集合中添加元素，返回添加个数
  也可以直接在add里面添加多个值 如：template.opsForSet().add("setTest","aaa","bbb")

```java
String[] strs= new String[]{"str1","str2"};
System.out.println(template.opsForSet().add("setTest", strs));
2
```

- Long remove(K key, Object... values);  移除集合中一个或多个成员

```java
String[] strs = new String[]{"str1","str2"};
System.out.println(template.opsForSet().remove("setTest",strs));
2
```

- V pop(K key);  移除并返回集合中的一个随机元素

```java
System.out.println(template.opsForSet().pop("setTest"));
System.out.println(template.opsForSet().members("setTest"));
bbb
[aaa, ccc]
```

- Boolean move(K key, V value, K destKey);  将 member 元素从 source 集合移动到 destination 集合

```java
template.opsForSet().move("setTest","aaa","setTest2");
System.out.println(template.opsForSet().members("setTest"));
System.out.println(template.opsForSet().members("setTest2"));
[ccc]
[aaa]
```

- Long size(K key); 无序集合的大小长度

```java
System.out.println(template.opsForSet().size("setTest"));
1
```



- Set<V> members(K key);    返回集合中的所有成员

```java
System.out.println(template.opsForSet().members("setTest"));
[ddd, bbb, aaa, ccc]
```

-  Cursor<V> scan(K key, ScanOptions options);  遍历set

```java
Cursor<Object> curosr = template.opsForSet().scan("setTest", ScanOptions.NONE);
  while(curosr.hasNext()){
     System.out.println(curosr.next());
  }
ddd
bbb
aaa
ccc
```

### Redis的ZSet数据结构 

- Boolean add(K key, V value, double score);    新增一个有序集合，存在的话为false，不存在的话为true

```java
System.out.println(template.opsForZSet().add("zset1","zset-1",1.0));
true
```

- Long add(K key, Set<TypedTuple<V>> tuples);   新增一个有序集合

```java
ZSetOperations.TypedTuple<Object> objectTypedTuple1 = new DefaultTypedTuple<>("zset-5",9.6);
ZSetOperations.TypedTuple<Object> objectTypedTuple2 = new DefaultTypedTuple<>("zset-6",9.9);
Set<ZSetOperations.TypedTuple<Object>> tuples = new HashSet<ZSetOperations.TypedTuple<Object>>();
tuples.add(objectTypedTuple1);
tuples.add(objectTypedTuple2);
System.out.println(template.opsForZSet().add("zset1",tuples));
System.out.println(template.opsForZSet().range("zset1",0,-1));
[zset-1, zset-2, zset-3, zset-4, zset-5, zset-6]
```

- Long remove(K key, Object... values);      从有序集合中移除一个或者多个元素

```java
System.out.println(template.opsForZSet().range("zset1",0,-1));
System.out.println(template.opsForZSet().remove("zset1","zset-6"));
System.out.println(template.opsForZSet().range("zset1",0,-1));
[zset-1, zset-2, zset-3, zset-4, zset-5, zset-6]
1
[zset-1, zset-2, zset-3, zset-4, zset-5]
```

- Long rank(K key, Object o);
  返回有序集中指定成员的排名，其中有序集成员按分数值递增(从小到大)顺序排列

```java
System.out.println(template.opsForZSet().range("zset1",0,-1));
System.out.println(template.opsForZSet().rank("zset1","zset-2"));
[zset-2, zset-1, zset-3, zset-4, zset-5]
0   //表明排名第一
```

- Set<V> range(K key, long start, long end);      通过索引区间返回有序集合成指定区间内的成员，其中有序集成员按分数值递增(从小到大)顺序排列

```java
System.out.println(template.opsForZSet().range("zset1",0,-1));
[zset-2, zset-1, zset-3, zset-4, zset-5]
```

- Long count(K key, double min, double max);      通过分数返回有序集合指定区间内的成员个数

```java
System.out.println(template.opsForZSet().rangeByScore("zset1",0,5));
System.out.println(template.opsForZSet().count("zset1",0,5));
[zset-2, zset-1, zset-3]
3
```

- Long size(K key);     获取有序集合的成员数，内部调用的就是zCard方法

```java
System.out.println(template.opsForZSet().size("zset1"));
6
```

- Double score(K key, Object o);     获取指定成员的score值

```java
System.out.println(template.opsForZSet().score("zset1","zset-1"));
2.2
```

- Long removeRange(K key, long start, long end);
  移除指定索引位置的成员，其中有序集成员按分数值递增(从小到大)顺序排列

```java
System.out.println(template.opsForZSet().range("zset2",0,-1));
System.out.println(template.opsForZSet().removeRange("zset2",1,2));
System.out.println(template.opsForZSet().range("zset2",0,-1));
[zset-1, zset-2, zset-3, zset-4]
2
[zset-1, zset-4]
```

- Cursor<TypedTuple<V>> scan(K key, ScanOptions options);      遍历zset

```java
Cursor<ZSetOperations.TypedTuple<Object>> cursor = template.opsForZSet().scan("zzset1", ScanOptions.NONE);
    while (cursor.hasNext()){
       ZSetOperations.TypedTuple<Object> item = cursor.next();
       System.out.println(item.getValue() + ":" + item.getScore());
    }
zset-1:1.0
zset-2:2.0
zset-3:3.0
zset-4:6.0
```



参考博客：[https://blog.csdn.net/javaxiaibai0414/article/details/88666453](https://blog.csdn.net/javaxiaibai0414/article/details/88666453)



### 工具类

```java

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

// 在我们真实的分发中，或者你们在公司，一般都可以看到一个公司自己封装RedisUtil
@Component
public final class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    // =============================common============================
    /**
     * 指定缓存失效时间
     * @param key  键
     * @param time 时间(秒)
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }


    /**
     * 判断key是否存在
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 删除缓存
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }


    // ============================String=============================

    /**
     * 普通缓存获取
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }
    
    /**
     * 普通缓存放入
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */

    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 普通缓存放入并设置时间
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */

    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 递增
     * @param key   键
     * @param delta 要增加几(大于0)
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }


    /**
     * 递减
     * @param key   键
     * @param delta 要减少几(小于0)
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }


    // ================================Map=================================

    /**
     * HashGet
     * @param key  键 不能为null
     * @param item 项 不能为null
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }
    
    /**
     * 获取hashKey对应的所有键值
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }
    
    /**
     * HashSet
     * @param key 键
     * @param map 对应多个键值
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * HashSet 并设置时间
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }


    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }


    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     */
    public double hincr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }


    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     */
    public double hdecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }


    // ============================set=============================

    /**
     * 根据key获取Set中的所有值
     * @param key 键
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0)
                expire(key, time);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * 获取set缓存的长度
     *
     * @param key 键
     */
    public long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */

    public long setRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // ===============================list=================================
    
    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 获取list缓存的长度
     *
     * @param key 键
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 将list放入缓存
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0)
                expire(key, time);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0)
                expire(key, time);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */

    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */

    public long lRemove(String key, long count, Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }

}
```



