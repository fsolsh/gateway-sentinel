spring cloud gateway with alibaba-sentinel demo

实现了以下内容：

1、通用的网关层异常处理

2、基于sentinel的限流

3、网关层实现基于dubbo rpc的token校验


Note：

1、基于nacos的服务注册和发现

2、基于dubbo-jwt的token校验

3、基于open-feign的服务实现

4、基于sentinel-dashboard https://github.com/alibaba/Sentinel/releases

`java -Dserver.port=8080 -Dcsp.sentinel.dashboard.server=localhost:8080 -Dproject.name=sentinel-dashboard -jar sentinel-dashboard-1.8.2.jar`

请求链路：

`request -> gateway(如需token校验，-> dubbo-jwt) -> open-feign`