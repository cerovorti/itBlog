package com.itblog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.itblog.mapper")
public class ItBlogApplication {
    public static void main(String[] args) {
        SpringApplication.run(ItBlogApplication.class, args);
    }
}
