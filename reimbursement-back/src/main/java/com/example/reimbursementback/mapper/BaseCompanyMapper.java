package com.example.reimbursementback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.reimbursementback.entity.base.BaseCompany;
import org.apache.ibatis.annotations.Mapper;

/**
 * 公司基础数据 Mapper接口
 * 继承 BaseMapper<BaseCompany> 即可获得基础 CRUD 能力
 *
 * 基础数据表通常只需要 selectList(null) 查询全部数据
 * 不需要额外自定义方法
 */
@Mapper
public interface BaseCompanyMapper extends BaseMapper<BaseCompany> {
    // 继承 BaseMapper 即可，基础数据表无需自定义SQL
}
