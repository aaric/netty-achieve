package com.incarcloud.boar;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 *
 * @author Aaric, created on 2020-05-06T18:02.
 * @version 1.3.0-SNAPSHOT
 */
@Slf4j
@SpringBootApplication
public class App {

    /**
     * 挂载`com.incarcloud.boar.datapack`包下面的解析器
     */
//    static {
//        DataParserManager.loadClassOfSamePackage();
//    }

    /**
     * Main
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
