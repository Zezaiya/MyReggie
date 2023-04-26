package com.zezai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j   //lombok提供给我们的注释,我们可以通过它提供的log对象对日志进行操作
@SpringBootApplication
@ServletComponentScan  //负责扫描拦截器
@EnableTransactionManagement
public class ReggieApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class, args);
        log.info("Project run successfully!");   //输出日志
    }

}
