package com.example.reimbursementback.entity.business;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("fk_reim_cost_allocation")
public class FkReimCostAllocation {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String reimId;

    private String companyId;

    private String companyNo;

    private String companyName;

    private String projectId;

    private String projectNo;

    private String projectName;

    private BigDecimal ratio;

    private BigDecimal amount;

    private Integer sortOrder;

    private LocalDateTime creationTime;

    private LocalDateTime updateTime;
}