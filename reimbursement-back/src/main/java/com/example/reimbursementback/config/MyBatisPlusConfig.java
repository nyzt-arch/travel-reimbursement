package com.example.reimbursementback.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.example.reimbursementback.mapper")
public class MyBatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加 MyBatis-Plus 分页插件，确保底层 MySQL 自动生成 LIMIT 分页语句
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
//1.添加 @MapperScan 可以让 Spring 扫描到 mapper 包
//底下的全部数据交互接口，不用在每个 Mapper 上手动写多余的扫描配置。
//2.MyBatis-Plus 分页查询接口 selectPage()
//必须依赖 PaginationInnerInterceptor 分页拦截器才能在 MySQL 中执行物理分页，
//否则会进行内存分页或无法分页。