# 工程简介

## balawo-oauth2 鉴权服务暂未配置和使用

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
