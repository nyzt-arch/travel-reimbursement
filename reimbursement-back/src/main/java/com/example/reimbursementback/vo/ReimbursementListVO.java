package com.example.reimbursementback.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ReimbursementListVO {
    private String id; // 主键ID
    private String reimNo; // 报销单号
    private Integer status; // 单据状态 (0:草稿, 1:已完成, 2:已作废)

    // 报销人
    private String reimburserId;
    private String reimburserNo;
    private String reimburserName;

    // 报销部门
    private String reimDepartmentId;
    private String reimDepartmentNo;
    private String reimDepartmentName;

    // 费用归属公司
    private String reimCompanyId;
    private String reimCompanyNo;
    private String reimCompanyName;

    // 业务类型
    private String businessTypeId;
    private String businessTypeNo;
    private String businessTypeName;


    private String title;
    private String reason;
    private BigDecimal subsidyTotal;//补助金额
    private String createTime;
    private String updateTime;
}