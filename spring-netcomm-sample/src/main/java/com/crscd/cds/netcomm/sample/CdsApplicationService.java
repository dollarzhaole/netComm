package com.crscd.cds.netcomm.sample;

import com.crscd.cds.spring.netcomm.annotation.EnableNetComm;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created with IntelliJ IDEA. Description: 为了增加对依赖项common模块的扫描，需要自定义@ComponentScan
 * 此时@SpringBootApplication默认扫描会失效
 *
 * @author lzy
 * @create 2020-03-12 下午12:31
 */
@SpringBootApplication
@EnableNetComm
@EnableScheduling
public class CdsApplicationService {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(CdsApplicationService.class);

        app.run(args);
    }
}
