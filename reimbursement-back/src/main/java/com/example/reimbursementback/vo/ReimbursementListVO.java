package com.example.reimbursementback.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ReimbursementListVO {

    private String id; // 操作列关联键

    private String reimNo; // 报销单号

    private Integer status; // 单据状态 (0:草稿, 1:已完成, 2:已作废)


    // ==========================================
    // 报销人信息
    // ==========================================

    private String reimburserId; // 报销人ID

    private String reimburserNo; // 报销人工号

    private String reimburserName; // 报销人姓名


    // ==========================================
    // 报销部门信息
    // ==========================================

    private String reimDepartmentId; // 报销部门ID

    private String reimDepartmentNo; // 报销部门编号

    private String reimDepartmentName; // 报销部门名称


    // ==========================================
    // 费用归属公司信息
    // ==========================================

    private String reimCompanyId; // 费用归属公司ID

    private String reimCompanyNo; // 费用归属公司编号

    private String reimCompanyName; // 费用归属公司名称


    // ==========================================
    // 业务类型信息
    // ==========================================

    private String businessTypeId; // 业务类型ID

    private String businessTypeNo; // 业务类型编号

    private String businessTypeName; // 业务类型名称


    // ==========================================
    // 标题、事由、金额与时间
    // ==========================================

    private String title; // 报销标题

    private String reason; // 报销事由

    private BigDecimal subsidyTotal; // 补助金额

    private String createTime; // 创建时间

    private String updateTime; // 更新时间
}
