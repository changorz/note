> 下载 MySQL yum包

```
wget http:``//repo.mysql.com/mysql57-community-release-el7-10.noarch.rpm
```

> 安装MySQL源

```
rpm -Uvh mysql57-community-release-el7-10.noarch.rpm
```

> 安装MySQL服务端,需要等待一些时间

```
yum install -y mysql-community-server
```

> 启动MySQL

```
systemctl start mysqld.service
```

> 检查是否启动成功

```
systemctl status mysqld.service
```

> 获取临时密码，MySQL5.7为root用户随机生成了一个密码

```
grep 'temporary password' /var/log/mysqld.log 
```

> 通过临时密码登录MySQL，进行修改密码操作

```
mysql -uroot -p
```

使用临时密码登录后，不能进行其他的操作，否则会报错，这时候我们进行修改密码操作

> 因为MySQL的密码规则需要很复杂，我们一般自己设置的不会设置成这样，所以我们全局修改一下

```sql
mysql> set global validate_password_policy=0;
mysql> set global validate_password_length=1;
```

> 这时候我们就可以自己设置想要的密码了

```
ALTER USER 'root'@'localhost' IDENTIFIED BY 'yourpassword';
ALTER USER 'root'@'localhost' IDENTIFIED BY 'root';
```

> 授权其他机器远程登录

```
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'yourpassword' WITH GRANT OPTION;
 
FLUSH PRIVILEGES;
```

> 开启开机自启动

先退出mysql命令行，然后输入以下命令

```
systemctl enable mysqld
systemctl daemon-reload
```

> 设置MySQL的字符集为UTF-8，令其支持中文

```
vim /etc/my.cnf
```

改成如下,然后保存

```
# For advice on how to change settings please see
# http://dev.mysql.com/doc/refman/5.7/en/server-configuration-defaults.html
 
[mysql]
default-character-set=utf8
 
[mysqld]
datadir=/var/lib/mysql
socket=/var/lib/mysql/mysql.sock
default-storage-engine=INNODB
character_set_server=utf8
 
symbolic-links=0
 
log-error=/var/log/mysqld.log
pid-file=/var/run/mysqld/mysqld.pid
```



> 重启一下MySQL,令配置生效

```
service mysqld restart
```

 

> 防火墙开放3306端口

```
firewall-cmd --state
firewall-cmd --zone=public --add-port=3306/tcp --permanent
firewall-cmd --reload
```



**报错：**

```bash
CentOS Access denied for user 'root'@'106.13.168.109' (using password: YES)
```

> 修改配置文件  vim /etc/my.cnf

```bash
[mysql]
default-character-set=utf8
 
[mysqld]
# 加了这个
skip-grant-tables

datadir=/var/lib/mysql
socket=/var/lib/mysql/mysql.sock
default-storage-engine=INNODB
character_set_server=utf8
 
symbolic-links=0
 
log-error=/var/log/mysqld.log
pid-file=/var/run/mysqld/mysqld.pid
```





参考文档：https://www.cnblogs.com/jinghuyue/p/11565564.html