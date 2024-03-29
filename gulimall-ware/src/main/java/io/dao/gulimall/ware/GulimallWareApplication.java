package io.dao.gulimall.ware;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableRabbit
@EnableFeignClients(basePackages = "io.dao.gulimall.ware.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallWareApplication {
	public static void main(String[] args) {
		SpringApplication.run(GulimallWareApplication.class, args);
	}
}
