## 安装

1. 执行脚本

vim install_docker.sh

```sh
#!/bin/bash
echo -e '\033[1;32m 安装Docker&docker-compose \033[0m'
echo -e '\033[1;32m 1.安装Docker \033[0m'
echo -e '\033[1;32m 添加Docker源 \033[0m'
wget https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo -O /etc/yum.repos.d/docker-ce.repo
echo -e '\033[1;32m 安装docker-ce \033[0m'
yum install -y docker-ce-18.06.1.ce-3.el7
echo -e '\033[1;32m 设置Docker开机自启动 \033[0m'
systemctl enable docker
echo -e '\033[1;32m 启动docker \033[0m'
systemctl start docker 
echo -e '\033[1;32m 查看docker服务启动状态 \033[0m'
systemctl status docker 
echo -e '\033[1;32m 查看docker版本 \033[0m'
docker --version
echo -e '\033[1;32m 给docker换阿里源 \033[0m'
cat <<EOF > /etc/docker/daemon.json
{
"registry-mirrors": ["https://b9pmyelo.mirror.aliyuncs.com"]
}
EOF
# 若想更换docker网卡的网段，则json内容如下
#{
#"default-address-pools": [ 
#       { 
#               "base": "198.18.0.0/16",
#               "size": 24
#               }
#       ],
#  "registry-mirrors": ["https://b9pmyelo.mirror.aliyuncs.com"]
#}
echo -e '\033[1;32m 重启docker服务 \033[0m'
systemctl restart docker
echo -e '\033[1;32m 查看docker服务启动状态 \033[0m'
systemctl status docker

echo -e '\033[1;32m 查看docker信息 \033[0m'
docker info 

echo -e '\033[1;32m 2.安装docker-compose \033[0m'
echo -e '\033[1;32m 下载docker-compose \033[0m'
yum -y install docker-compose
#curl -L https://github.com/docker/compose/releases/download/1.16.0-rc2/docker-compose-`uname -s`-`uname -m` > /usr/local/bin/docker-compose
#echo -e '\033[1;32m 添加执行权限 \033[0m'
#chmod +x /usr/local/bin/docker-compose
echo -e '\033[1;32m 查看docker-compose版本 \033[0m'
docker-compose version

# echo -e '\033[1;32m 3.安装ctop工具 \033[0m'
# wget -c https://github.com/bcicen/ctop/releases/download/v0.7.2/ctop-0.7.2-linux-amd64 -O /usr/local/bin/ctop
chmod +x /usr/local/bin/ctop
echo -e "\033[1;32m 清除yum安装包 \033[0m"
yum -y clean all
exit
```

2. 给执行权限    chmod 777 install_docker.sh

3. 执行 脚本      ./install_docker.sh




```bash
# 配置阿里镜像
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": ["https://ogus8f4f.mirror.aliyuncs.com"]
}
EOF
sudo systemctl daemon-reload
sudo systemctl restart docker
```





## 安装Nginx检测

```bash
docker run -d -p 3366:80 --name nginx-01 -v ~/nginx/www:/usr/share/nginx/html -v ~/nginx/conf/nginx.conf:/etc/nginx/nginx.conf -v ~/nginx/logs:/var/log/nginx nginx
```

```
docker run -d -p 3366:80 --name nginx-01 -v ~/nginx/www:/usr/share/nginx/html -v ~/nginx/logs:/var/log/nginx nginx
```



## docker 帮助文档

```
[root@iZm5e4psug6hqjmtd9z7moZ ~]# docker --help
Usage:	docker [OPTIONS] COMMAND
A self-sufficient runtime for containers
Options:
      --config string      Location of client config files (default "/root/.docker")
  -D, --debug              Enable debug mode
  -H, --host list          Daemon socket(s) to connect to
  -l, --log-level string   Set the logging level ("debug"|"info"|"warn"|"error"|"fatal") (default "info")
      --tls                Use TLS; implied by --tlsverify
      --tlscacert string   Trust certs signed only by this CA (default "/root/.docker/ca.pem")
      --tlscert string     Path to TLS certificate file (default "/root/.docker/cert.pem")
      --tlskey string      Path to TLS key file (default "/root/.docker/key.pem")
      --tlsverify          Use TLS and verify the remote
  -v, --version            Print version information and quit

Management Commands:
  config      Manage Docker configs
  container   Manage containers
  image       Manage images
  network     Manage networks
  node        Manage Swarm nodes
  plugin      Manage plugins
  secret      Manage Docker secrets
  service     Manage services
  stack       Manage Docker stacks
  swarm       Manage Swarm
  system      Manage Docker
  trust       Manage trust on Docker images
  volume      Manage volumes

Commands:
  attach      Attach local standard input, output, and error streams to a running container
  build       Build an image from a Dockerfile
  commit      Create a new image from a container's changes
  cp          Copy files/folders between a container and the local filesystem
  create      Create a new container
  diff        Inspect changes to files or directories on a container's filesystem
  events      Get real time events from the server
  exec        Run a command in a running container
  export      Export a container's filesystem as a tar archive
  history     Show the history of an image
  images      List images
  import      Import the contents from a tarball to create a filesystem image
  info        Display system-wide information
  inspect     Return low-level information on Docker objects
  kill        Kill one or more running containers
  load        Load an image from a tar archive or STDIN
  login       Log in to a Docker registry
  logout      Log out from a Docker registry
  logs        Fetch the logs of a container
  pause       Pause all processes within one or more containers
  port        List port mappings or a specific mapping for the container
  ps          List containers
  pull        Pull an image or a repository from a registry
  push        Push an image or a repository to a registry
  rename      Rename a container
  restart     Restart one or more containers
  rm          Remove one or more containers
  rmi         Remove one or more images
  run         Run a command in a new container
  save        Save one or more images to a tar archive (streamed to STDOUT by default)
  search      Search the Docker Hub for images
  start       Start one or more stopped containers
  stats       Display a live stream of container(s) resource usage statistics
  stop        Stop one or more running containers
  tag         Create a tag TARGET_IMAGE that refers to SOURCE_IMAGE
  top         Display the running processes of a container
  unpause     Unpause all processes within one or more containers
  update      Update configuration of one or more containers
  version     Show the Docker version information
  wait        Block until one or more containers stop, then print their exit codes

```



## Dockerfile

**Test**

```dockerfile
FROM centos
RUN yum install wget \
    && wget -O redis.tar.gz "http://download.redis.io/releases/redis-5.0.3.tar.gz" \
    && tar -xvf redis.tar.gz
```

在 Dockerfile 文件的存放目录下，执行构建动作。

以下示例，通过目录下的 Dockerfile 构建一个 nginx:v3（镜像名称:镜像标签）。

**注**：最后的 **.** 代表本次执行的上下文路径，下一节会介绍。

```bash
 docker build -t redis:v1 .
```









