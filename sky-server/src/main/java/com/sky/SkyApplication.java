package com.sky;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement //开启注解方式的事务管理
@EnableCaching//开启spring cache注解功能
@Slf4j
public class SkyApplication {
    public static void main(String[] args) {

        // 1. 加载.env文件（默认会找项目根目录的.env）
        Dotenv dotenv = Dotenv.load();

        // 2. 读取密钥并打印日志（辅助排查）
        String accessKeyId = dotenv.get("ALIYUN_ACCESS_KEY_ID");
        String accessKeySecret = dotenv.get("ALIYUN_ACCESS_KEY_SECRET");
        System.out.println("读取到的AccessKeyID：" + accessKeyId);  // 启动时会打印到控制台
        System.out.println("读取到的AccessKeySecret：" + accessKeySecret);

        // 3. 非空判断，避免NullPointerException
        if (accessKeyId == null || accessKeySecret == null) {
            throw new RuntimeException("请在.env文件中配置正确的阿里云OSS密钥！");
        }

        // 4. 设置系统环境变量
        System.setProperty("ALIYUN_ACCESS_KEY_ID", accessKeyId);
        System.setProperty("ALIYUN_ACCESS_KEY_SECRET", accessKeySecret);

        SpringApplication.run(SkyApplication.class, args);
        log.info("server started");
    }
}
