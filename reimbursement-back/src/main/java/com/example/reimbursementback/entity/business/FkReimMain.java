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
@TableName("fk_reim_main")
public class FkReimMain {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String reimNo;

    private LocalDateTime creationTime;

    private LocalDateTime updateTime;

    private String reimbursementTitle;

    private String reimburserId;

    private String reimburserNo;

    private String reimburserName;

    private String reimDepartmentId;

    private String reimDepartmentNo;

    private String reimDepartmentName;

    private String reimCompanyId;

    private String reimCompanyNo;

    private String reimCompanyName;

    private String businessTypeId;

    private String businessTypeNo;

    private String businessTypeName;

    private String businessTripReason;

    private BigDecimal subsidyTotal;

    private BigDecimal mealAllowance;

    private BigDecimal transportationAllowance;

    private BigDecimal phoneAllowance;

    private String remarks;

    private String status;

    private String createUserId;

    private String createUserName;
}