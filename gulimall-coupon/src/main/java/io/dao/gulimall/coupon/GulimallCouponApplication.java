package io.dao.gulimall.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 1. add alibaba-nacos-config dependency
 * 2. create a new bootstrap.properties
 * 	with spring.application.name and spring.cloud.nacos.config.server-addr
 * 3. add data id e.g. xxxx.properties to nacos config server
 * 4. add properties to xxxx.properties file
 * 5. dynamic refresh config properties (@RefreshScope, @Value)
 */
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallCouponApplication {

	public static void main(String[] args) {
		SpringApplication.run(GulimallCouponApplication.class, args);
	}

}
