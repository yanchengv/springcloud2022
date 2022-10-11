# 工程简介

## balawo-oauth2 鉴权服务

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

### 2、访问订单接口

### http://localhost:9004/users/index  `get`

注：因为oauth2没有写过滤器filter，所以通过网关访问 `http://localhost:9000/order/orders/index` 会没有权限。

header参数

```
{
    Authorization: Bearer 41ff9874-1881-4ae0-bae9-e27c0f0ba7dc
}
```

response: 因为当前用户有admin权限，所以能正常访问此接口



## balawo-oauth2和balawo-gateway两个微服务中使用了common.model包中的LoginUser类，目的是防止SpringSecurityOAuth2 redis 反序列失败问题。

当在认证服务同时进行token获取以及token资源认证可以使用的时候。然后把资源服务整合到其他微服务，在这同时又自定义是实现了UserDetails并重写了该接口方法，自定义实现了UserDetailsService，并重写了接口方法，返回使用的是自定义实现的UserDetails。在资源服务器
认证token的时候就会出现redis序列化失败的问题。

balawo-oauth2创建token保存到redis时 会把LoginUser 的进行了序列化存储，包会是comom.model中的LoginUser。示例：
```

  @Service
  public class MyUserDetailsService implements UserDetailsService {
  @Autowired
  private OauthUserDetailsService oauthUserDetailsService;
  
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
  LoginUser userDetails = oauthUserDetailsService.loadUserByUsername(username);
  return userDetails;
  
  }
  }
```

balawo-gateway项目中获取redis的token时会反序列化 示例：
```
        //获取前端传来的token
        token = token.replace("Bearer","").trim();
        logger.info("访问的token:{}",token);
        //根据token查询tokenStore（redis存储）中对应的用户和权限信息
        OAuth2Authentication auth = tokenStore.readAuthentication(token);
```

如果balawo-gateway中不存在comom.model.LoginUser，会导致反序列失败，所以两个项目中都要创建同包名下的LoginUser。

导致问题的原因是，在生成 token 保存到redis的时候使用了自定义的 UserDetails 的进行了 token 序列化，从redis中获取token反序列化的时候（tokenStore.readAuthentication(token);）根据token查询tokenStore（redis存储）中对应的用户和权限信息，在新项目中找不到自定义的实现，只能使用默认的实现来，所以会报错。 

解决的方案是就是把自定义实现的 实现UserDetails的loginUser类在balawo-gateway项目和balawo-autho2项目同一个包路径下定义一份 。

