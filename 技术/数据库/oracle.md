## 用户的概念
　　用户，即user,通俗的讲就是访问oracle数据库的“人”。在`oracle`中，可以对用户的各种安全参数进行控制，以维护数据库的安全性，这些概念包括模式(schema)、权限、角色、存储设置、空间限额、存取资源限制、数据库审计等。每个用户都有一个口令，使用正确的用户/口令才能登录到数据库进行数据存取。

## 用户默认表空间
　　表空间是信息存储的最大逻辑单位、当用户连接到数据库进行资料存储时，若未指出数据的目标存储表空间时，则数据存储在用户的默认表空间中。
比如：`create table mytable(id varchar2(20),name varchar2(100));`这条语句创建了一个表`mytable`,并将其存储在当前用户的默认表空间中，
若要指定表空间，则：`create table mytable(id varchar2(20),name varchar2(100)) tablespace tbs1;`。
用户的默认表空间可以在创建用户时指定，也可以使用`aler user`命令进行指定，具体语法见后面的介绍。

## 用户临时表空间
　　临时表空间主要用于order by语句的排序以及其它一些中间操作。
在`oracle9i`之前，可以指定用户使用不同的临时表空间，从`9i`开始，临时表空间是通用的，所的用户都使用TEMP作为临时表空间。

## 用户资源文件
　　用户资源文件用来对用户的资源存取进行限制，包括：`cpu`使用时间限制、内存逻辑读个数限制、每个用户同时可以连接的会话数据限制、
一个会话的空间和时间限制、一个会话的持续时间限制、每次会话的专用`SGA`空间限制。

## 用户表空间限额
　　表空间存储限制是用户在某一个表空间中可以使用的存储空间总数。
在创建或修改用户时，可以由参数quota指出。若用户在向表空间存储数据时，超出了此限额，则会产生错误。
错误信息如：`ORA-01536:space quota exceeded for tablespace tablespacename..`。可以通过查询字典`dba_ts_quotas`查看表空间限额信息。

##  创建用户资源文件

创建用户资源文件的语法如下：
　　CREATE PROFILE filename LIMIT
　　SESSION_PER_USER integer
　　CPU_PER_SESSION integer
　　USER_PER_CALL integer
　　CONNECT_TIME integer
　　......
　　基中：
　　SESSION_PER_USER: 用户可以同时连接的会话数量限额;
　　CPU_PER_SESSION:用户在一次数据库会期间可占用的CPU时间总量限额，单位为百分之一秒;
　　USER_PER_CALL:用户一次SQL调用可用的CPU时间总量限额，单位为百分之一秒;
　　LOGICAL_READS_PER_SESSION：在一次数据库会话期间能够读取的数据库块的个数限额;
　　LOGICAL_READS_PER_CALL:一次SQL调用可以读取的数据库块数限额;
　　IDLE_TIME：用户连接到数据库后的可空闲时间限额，单位为分钟，若空闲时间超过此值，则连接被断开;
　　CONNECT_TIME:一次连接的时间总量限额，单位为分钟，连接时间超过此值时，连接被断开;
　　PRIVATE_SGA：用户么有的SGA区的大小，单位为数据库块，默认值为UNLIMITED;
　　COMPOSITE_LIMIT：这是一项由上述限制参数构成的组合资源项。
举例来说，假设资源设置如下：
　　IDLE_TIME 20
　　CONNECT_TIME 120
　　CPU_PER_CALL 750
　　COMPOSITE_LIMT 800
那么，当会话空间超过20分钟，或者连接时间超过120分钟，又或者执行一个SQL耗费超过7.5秒，再或者这几个资源限制加起来的总数超过800，
则系统自动终止会话。
　　FAILED_LOGIN_ATTEMPTS:用户登录时，允许用户名/密码校验失败致使用登录失败的次数限额，超过该次数，帐户被锁定;
　　PASSWORD_LIFE_TIME：口令有效时间，单位为天数，超过这一时间，拒绝登录，须重新设置口令，默认值为UNLIMITED;
　　PASSWORD_REUSE_TIME:一个失效口令经过多少天后才可重新利用，默认为UNLIMITED;
　　PASSWORD_REUSE_MAX：一个口令可重复使用的次数;
　　PASSWORD_LOCK_TIME:当登录失败达到FAILED_LOGIN_ATTEMPS时，帐户被锁定，该参数用于设定被锁定的天数;
下面举例如下：
1)创建一个用户资源文件
　　create profile tax_users limit
　　session_per_user 3
　　cpu_per_session UNLIMITED
　　connect_time 30
　　logical_reads_per_session DEFAULT
　　logical_reads_per_call 1000
　　private_sga 15K
　　composite_limit 500000
　　password_life_time 90
2)查询用户资源文件信息
　  select *from dba_profile where profile = 'tax_users'
3)指定用户资源文件给用户
　  alter user us1 profile tax_users

##  创建、修改、删除用户
1.创建用户
　　创建用户的详细语法请查询oracle的官方参数文档，这里介绍典型的语法。语法如下：
```sql
　　CREATE USER username
　　IDENTIFIED BY password
　　DEFAULT TABLESPACE tablespace
　　TEMPORARY TABLESPACE tablespace
　　PROFILE profile
　　QUOTA integer|UNLIMITED ON tablespace
```
各选项含义如下：
```sql
　　IDENTIFIED BY password：用户口令;
    DEFAULT TABLESPACE tablespace:默认表空间;
　　TEMPORARY TABLESPACE tablespace：临时表空间;
　　PROFILE profile|DEFAULT：用户资源文件;
　　QUOTA integer[K|M]|UNLIMITED ON tablespace用户在表空间上的空间使用限额，可以指定多个表空间的限额。
举例：
　　CREATE USER us1 IDENTIFIED BY abc123
　　DEFAULT TABLESPACE user01
　　TEMPORARY TABLESPACE temp
　　PROFILE DEFAULT
　　QUOTA 1000M ON user01;
```
2.修改用户

​    Alter User 用户名
​    Identified 口令
​    Default Tablespace tablespace
​    Temporary Tablespace tablespace
​    Profile profile
​    Quota integer/unlimited on tablespace;

    1、修改口令字：
    Alter user acc01 identified by "12345";
    2、修改用户缺省表空间：
    Alter user acc01 default tablespace users;
    3、修改用户临时表空间
    Alter user acc01 temporary tablespace temp_data;
    4、强制用户修改口令字：
    Alter user acc01 password expire;
    5、将用户加锁
    Alter user Joanname account lock;  // 加锁
    Alter user Joanname account unlock;  // 解锁
3.删除用户
　　删除用户，是将用户及用户所创建的schema对象从数据库删除。如下：
　　`DROP USER us1;`
        若用户us1含有schema对象，则无上述语句将执行失败，须加入关键字CASCADE才能删除，意思是连并其对象一起删除，如下：
　　`DROP USER us1 CASCADE;`
4.查看用户信息
        ` select * from dba_users;`

## 常用命令

> 表空间创建

```sql
/*第1步：创建临时表空间  */
create temporary tablespace kc_temp
tempfile 'C:\app\Administrator\oradata\orcl\kc_temp.dbf' 
size 50m  
autoextend on  
next 50m maxsize 20480m  
extent management local;  

create temporary tablespace temp tempfile '/home/oracle/temp/temp.dbf' size 50m autoextend on next 50m maxsize 20480m extent management local;  
 
/*第2步：创建数据表空间  */
create tablespace kc  
logging  
datafile 'C:\app\Administrator\oradata\orcl\kc.dbf' 
size 50m  
autoextend on  
next 50m maxsize 20480m  
extent management local; 
```

> 查看所有表空间级大小

```sql
select tablespace_name, sum(bytes)/1024/1024 from dba_data_files group by tablespace_name;
```

> 创建用户

```sql
CREATE USER Tom IDENTIFIED BY Tom
DEFAULT TABLESPACE users
TEMPORARY TABLESPACE temp
PROFILE DEFAULT
QUOTA 10M ON USERS
QUOTA 50M ON BOOKTBS1;

CREATE USER chang IDENTIFIED BY root DEFAULT TABLESPACE data01 TEMPORARY TABLESPACE temp QUOTA 10M ON USERS QUOTA 20M ON BOOKTBS2;

CREATE USER chang IDENTIFIED BY root DEFAULT TABLESPACE data01 TEMPORARY TABLESPACE temp;
```

> 将用户加锁
```sql
Alter user 用户名 account lock;      // 加锁
Alter user Joan account unlock;     // 解锁
```

> 为方便数据库中用户 的登录， 为数据库中所有用户授予 `CREATESESSION`系统权限。

```sql
 grant create session to public;
```

> 用户登录

```sql
sqlplus /nolog
sqlplus / as sysdba

conn Joan/Joan@BOOKSALES
conn system/manager
```

> 查看sid

```sql
 select instance_name from v$instance;
```

