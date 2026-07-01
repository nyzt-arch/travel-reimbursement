package com.example.reimbursementback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.reimbursementback.entity.business.FkReimMain;
import org.apache.ibatis.annotations.Mapper;

/**
 * 报销单主表 Mapper接口
 * 继承 BaseMapper<FkReimMain> 即可获得基础 CRUD 能力：
 *   - insert(entity)         插入一条记录
 *   - deleteById(id)         根据ID删除
 *   - updateById(entity)     根据ID更新
 *   - selectById(id)         根据ID查询
 *   - selectList(wrapper)    条件查询列表
 *   - selectPage(page, wrapper)  分页查询（需配合分页插件）
 *
 * 如有复杂 SQL 需求，可在 resources/mapper/FkReimMainMapper.xml 中扩展
 */
@Mapper
public interface FkReimMainMapper extends BaseMapper<FkReimMain> {
    // MyBatis-Plus 的 BaseMapper 已提供全部基础 CRUD 方法
    // 如需自定义SQL，可在此添加方法声明，并在对应的 XML 文件中编写SQL
}
