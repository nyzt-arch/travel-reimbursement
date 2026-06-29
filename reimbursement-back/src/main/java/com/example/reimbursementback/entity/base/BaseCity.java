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
@TableName("base_city")
public class BaseCity {
    @TableId(type = IdType.INPUT)
    private String cityNo;

    private String cityName;
    private String cityType;
}