package io.dao.gulimall.product.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@MapperScan("io.dao.gulimall.product.dao")
public class MyBatisConfig {

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // 设置请求的页面大于最大页后操作，true调回首页，false继续请求，默认false
        paginationInterceptor.setOverflow(true);
        // 设置最大单页限制数量，默认500条，-1不受限制
        paginationInterceptor.setLimit(500);
        return paginationInterceptor;
    }

}
