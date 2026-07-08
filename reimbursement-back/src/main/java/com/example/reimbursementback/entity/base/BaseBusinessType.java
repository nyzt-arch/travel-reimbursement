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
     * 子节点列表，用于拼装树形结构返回给前端
     * 这个字段在数据库表中不存在，只用来做业务上的数据组装
     */
    @TableField(exist = false)
    private List<BaseBusinessType> children;
}