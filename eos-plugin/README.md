# Eos服务一键发布 使用说明

## 1.插件安装配置
### 1.1 插件安装

如下图进行插件的安装后重启idea

![插件安装](doc/img/install.png)

### 1.2 插件配置

![插件配置](doc/img/config.png)  
项目id 查看如下：  
![插件配置](doc/img/appid.png)  
钉钉群自定义机器人配置：  
![自定义机器人](doc/img/webhok1.png)  
![自定义机器人](doc/img/webhok2.png)  
接下来按照提示下一步，就可以获取到如下图的webhok  
![自定义机器人](doc/img/webhok3.png)
## 二、服务一键发布
![一键发布](doc/img/publish.png)
## 注意目前1.0.1版本只支持
 服务注解如下的版本 （eos 3.2.5之后的包） ：
```
@EosService(desc="地址服务",module="通用")
```
## 其中 desc 为服务的中文名称，module 为管理员为此项目建立的模块名称，如有不知请咨询管理员。
