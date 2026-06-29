package com.example.reimbursementback.entity.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("base_business_type")
public class BaseBusinessType {
    @TableId(type = IdType.INPUT)
    private String businessTypeId;

    private String businessTypeNo;
    private String businessTypeName;
    private String thereSubordinateNode;
    private String superiorId;

    /**
     * 【可选】子节点集合，用于构建树形结构返回前端
     * exist = false 表示数据库表中无该字段，仅用于业务封装
     */
    @TableField(exist = false)
    private List<BaseBusinessType> children;
}