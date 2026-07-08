package com.example.reimbursementback.dto;

import lombok.Data;

@Data
public class ReimbursementQueryDTO {
    private String reimNo;
    private String title;
    private String reason;
    private String companyId;
    private String deptId;
    private String reimburserId;
    private String businessTypeId;
    private Integer pageNum = 1;    //默认第一页
    private Integer pageSize = 10;  //每页10条
}