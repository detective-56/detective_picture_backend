package com.hjl.hjlpicturebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.hjl.hjlpicturebackend.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class HjlPictureBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(HjlPictureBackendApplication.class, args);
  }
}
