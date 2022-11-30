package io.dao.gulimall.seckill.config;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import io.dao.gulimall.seckill.sentinel.GulimallBlockExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeckillSentinelConfig {

    public SeckillSentinelConfig() {
        // WebCallbackManager was not supported in v1.7.1, using BlockExceptionHandler instead
    }

    @Bean
    public BlockExceptionHandler blockExceptionHandler() {
        return new GulimallBlockExceptionHandler();
    }

}
