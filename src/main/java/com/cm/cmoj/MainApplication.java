package com.cm.cmoj;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 主类（项目启动入口）
 *
 * @author
 * @from
 */
// todo 如需开启 Redis，须移除 exclude 中的内容
@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class MainApplication {

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(MainApplication.class);
        logger.info("项目接口文档访问地址："+"http://localhost:8121/api/doc.html");
        SpringApplication.run(MainApplication.class, args);
    }

}
