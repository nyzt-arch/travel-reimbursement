package com.example.reimbursementback.entity.business;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("fk_reim_subsidy")
public class FkReimSubsidy {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String reimId;
    private String tripId;

    private String personId;
    private String personName;

    private LocalDate startDate;
    private LocalDate endDate;
    private Integer days;

    private String tripDesc;

    private String subsidyCity;
    private String cityType;

    private BigDecimal applyAmount;
    private BigDecimal subsidyAmount;

    private BigDecimal mealAmount;
    private BigDecimal transportAmount;
    private BigDecimal phoneAmount;

    private LocalDateTime creationTime;
    private LocalDateTime updateTime;
}