package com.example.reimbursementback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.reimbursementback.entity.base.BaseCompany;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BaseCompanyMapper extends BaseMapper<BaseCompany> {
    // 继承 BaseMapper 即可，基础数据表无需自定义SQL
}
