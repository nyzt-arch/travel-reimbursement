package com.example.reimbursementback.entity.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("base_department")
public class BaseDepartment {
    @TableId(type = IdType.INPUT)
    private String reimDepartmentId;

    private String reimDepartmentNo;
    private String reimDepartmentName;
}
