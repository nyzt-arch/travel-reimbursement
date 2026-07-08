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
@TableName("base_company")
public class BaseCompany {
    @TableId(type = IdType.INPUT)
    private String reimCompanyId;

    private String reimCompanyNo;
    private String reimCompanyName;
}
