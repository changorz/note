## 升级后的替换版本

1. 服务注册中心 Eureka(挂了) 可替换为(Zookeeper) 或Consul(不推荐使用) 或Nacos(阿里巴巴的推荐使用)
2. 服务调用 Ribbon(可以用但不更新) 可替换为LoadBalancer
3. 服务调用 Feign(挂了) 可替换为OpenFeign
4. 服务降级熔断限流 Hystrix(挂了但是现在在大规模使用) 可替换为resilience4j(国内用的很少) 可替换为 Sentinel(阿里巴巴的推荐使用)
5. 服务网关 Zuul(挂了) Gateway(重点)
6. 服务配置 Config(不推荐使用) Apolo或者Nacos
7. 服务总线 Bus 推荐使用阿里巴巴的Nacos

参考文献：https://zhuanlan.zhihu.com/p/170280026

![](https://pic.gksec.com/2020/08/30/f1ecef260d233/a43655b80a2ec3f4764d9120d8adc7e.jpg)

## Spring Cloud Alibaba 新一代微服务解决方案

参考文章：https://zhuanlan.zhihu.com/p/98874444





































