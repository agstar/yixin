package com.imooc.yixin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = "com.imooc.yixin.mapper")
@org.mybatis.spring.annotation.MapperScan(basePackages = "com.imooc.yixin.mapper")
@ComponentScan(basePackages = {"com.imooc.yixin", "org.n3r.idworker"})
public class YixinApplication {

    public static void main(String[] args) {
        SpringApplication.run(YixinApplication.class, args);
    }
}
