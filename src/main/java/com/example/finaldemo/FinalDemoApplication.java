package com.example.finaldemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.example.finaldemo.dao")
@EnableAspectJAutoProxy(exposeProxy = true)

public class FinalDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinalDemoApplication.class, args);
    }

}
