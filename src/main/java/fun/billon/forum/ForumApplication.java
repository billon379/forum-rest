package fun.billon.forum;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * springboot启动类
 * 1.EnableEurekaClient 注册Eureka客户端
 * 2.MapperScan 扫描mapper文件
 *
 * @author billon
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootApplication
@EnableEurekaClient
@MapperScan("fun.billon.forum.dao")
@EnableFeignClients(basePackages = {"fun.billon.member.api.feign"})
public class ForumApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForumApplication.class, args);
    }

}