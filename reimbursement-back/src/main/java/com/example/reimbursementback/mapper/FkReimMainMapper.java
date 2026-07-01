package com.example.reimbursementback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.reimbursementback.entity.business.FkReimMain;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FkReimMainMapper extends BaseMapper<FkReimMain> {
    // MyBatis-Plus 的 BaseMapper 已提供全部基础 CRUD 方法
    // 如需自定义SQL，可在此添加方法声明，并在对应的 XML 文件中编写SQL
}
