# eos-parameter-valid

## 一、简介
该模块为扩展功能，提供对参数校验的支持。功能类似spring mvc @valid @validate

## 使用

1. 引入该JAR
2. 配置项目支持spring aop，自动扫描com.sunsharing.eos.support.ValidSupportAspect
3. 配置validator，上述aop拦截类作为注入。

         @Bean
         public Validator validator() {
             return new LocalValidatorFactoryBean();
         }
4. 接口方法定义中必须包含@Version，参数包含@valid或者@validate