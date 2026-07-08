package com.example.reimbursementback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.reimbursementback.entity.business.FkReimMain;
/**
 * 继承 BaseMapper<FkReimMain> 基础 CRUD 能力：
 *   - insert(entity)         插入一条记录
 *   - deleteById(id)         根据ID删除
 *   - updateById(entity)     根据ID更新
 *   - selectById(id)         根据ID查询
 *   - selectList(wrapper)    条件查询列表
 *   - selectPage(page, wrapper)  分页查询（需配合分页插件）
 * 如有复杂 SQL 需求，可在 resources/mapper/FkReimMainMapper.xml 中扩展
 */
public interface FkReimMainMapper extends BaseMapper<FkReimMain> {
}