//以下是数据库连接和配置的代码示例，使用了 Spring Data MongoDB：
//这段代码演示了使用 Spring Data MongoDB 连接和配置数据库的基本步骤，您可以根据自己的需求进行调整和扩展。

spring.data.mongodb.uri=mongodb://localhost:27017/canteenApp
//application.properties 配置文件
//spring.data.mongodb.uri: 指定 MongoDB 数据库的连接 URI。
//mongodb://localhost:27017: MongoDB 数据库的地址和端口号。
//canteenApp: 要连接的数据库名称。


package com.example.canteenapp.config;                                                                //配置类 

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration                                                                                       //@Configuration: 标识该类为配置类。
public class MongoConfig {                                                                           //validatingMongoEventListener 和 validator 方法: 可选，用于配置 JSR-303 数据校验，在保存数据到数据库之前进行校验。

    // 可选：配置 JSR-303 数据校验
    @Bean
    public ValidatingMongoEventListener validatingMongoEventListener() {
        return new ValidatingMongoEventListener(validator());
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }
}

//您不需要手动创建 MongoTemplate 或 MongoClient 实例，Spring Boot 会自动为您创建并管理它们。
//您可以根据需要修改配置文件中的数据库连接信息。
//您可以添加其他配置，例如连接池、身份验证等。

//请确保您已经在项目中添加了 Spring Data MongoDB 的依赖。
//您需要根据实际情况修改数据库连接信息。
