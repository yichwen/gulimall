package io.dao.gulimall.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 1. add open-feign dependency
 * 2. create an interface, tell Spring Cloud this interface is used for remote service
 * 	- annotate remote service and specific http url
 * 3. enable feign client
 */
@EnableRedisHttpSession
@EnableFeignClients(basePackages = "io.dao.gulimall.member.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallMemberApplication {

	public static void main(String[] args) {
		SpringApplication.run(GulimallMemberApplication.class, args);
	}

}
