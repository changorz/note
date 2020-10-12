## CentOS安装Maven

> 下载镜像

进入官网http://maven.apache.org/download.cgi

点击此处下载https://www-eu.apache.org/dist/maven/maven-3/3.6.2/binaries/apache-maven-3.6.2-bin.tar.gz

> 解压安装

下载完成后开始解压 tar -zxvf apache-maven-3.6.2-bin.tar.gz

> 配置环境变量

```bash
 vim /etc/profile
 
 export M2_HOME=/usr/local/maven/apache-maven-3.6.3 //本地maven安装home目录
 export PATH=$PATH:$M2_HOME/bin
 
 # 重新加载配置文件
 source /etc/profile
 
 # 检测环境
 mvn --version
```

> 配置本地仓库与阿里云镜像

```bash
# 进入Maven安装目录，找到conf目录
# 修改文件
vim  settings.xml

<!-- localRepository
     The path to the local repository maven will use to store artifacts.
     Default: ${user.home}/.m2/repository
     <localRepository>/path/to/local/repo</localRepository>
-->
# 修改本地仓库地址 /usr/local/maven/maven-bdck
 <localRepository>/usr/local/maven/maven-bdck</localRepository>

# 配置maven阿里云仓库地址
<mirrors>
<!-- mirror
   Specifies a repository mirror site to use instead of a given repository. The repository that
   this mirror serves has an ID that matches the mirrorOf element of this mirror. IDs are used
   for inheritance and direct lookup purposes, and must be unique across the set of mirrors.
    
    <mirror>
      <id>mirrorId</id>
      <mirrorOf>repositoryId</mirrorOf>
      <name>Human Readable Name for this Mirror.</name>
      <url>http://my.repository.com/repo/path</url>
    </mirror>
-->
	# 修改的地方
	<mirror>
      <id>nexus-aliyun</id>
      <mirrorOf>central</mirrorOf>
      <name>Nexus aliyun</name>
      <url>http://maven.aliyun.com/nexus/content/groups/public</url>
    </mirror>
  </mirrors>

```

> 修改本地仓库权限

```bash
chown -R jenkins:jenkins maven-bdck
```

参考博客：https://www.cnblogs.com/dszazhy/p/11528276.html**













