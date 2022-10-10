# 工程简介
## springcloud项目访问

### 启动工程后，访问：http://localhost:9001/，可以看到eureka后台管理界面
### http://localhost:9000是 gateway网关项目


## v1版本功能
### 创建了4个微服务 balawo-eureka、balawo-eureka、balawo-order、balawo-oauth2
### 1、配置了eureka
### 2、配置了 gateway
### 3、配置了 balawo-order
### 4、balawo-oauth2 鉴权服务暂未配置和使用

#### 例如访问 balawo-order项目路径则是 http://localhost:9000/order/orders/index

##  v2版本功能 oauth2 鉴权


* ## balawo-oauth2微服务鉴权服务

## 1、获取token

### http://localhost:9004/oauth/token  `post`

获取token 模式是password

body参数：

```
{
grant_type: password,
client_id: client_id1,
client_secret: client_secret1,
username: u1,
password: p1,
scope: all
}
```

response:

```
{
"access_token": "b4e87a18-60a9-4e00-8d0b-253013cad45f",
"token_type": "bearer",
"refresh_token": "b88367d6-ce3c-4a3a-80f0-ba073fb02154",
"expires_in": 3266,
"scope": "all"
}
```
