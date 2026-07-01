package com.example.reimbursementback.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ReimbursementSaveDTO {

    private String id; // 主键ID

    private String reimNo; // 报销单号

    private Integer status; // 单据状态 (0:草稿, 1:已完成, 2:已作废)

    private String createTime; // 创建时间

    private String updateTime; // 更新时间


    // ==========================================
    // 基础信息
    // ==========================================

    private String title; // 报销标题
    
    private String reimburserId; // 报销人
    
    private String reimDepartmentId; // 报销部门
    
    private String reimCompanyId; // 费用归属公司
    
    private String businessTypeId; // 业务类型
    
    private String reason; // 出差事由


    // ==========================================
    // 明细集合
    // ==========================================

    private List<TripDTO> trips; // 补录行程明细

    private List<SubsidyDTO> subsidies; // 补助信息明细

    private List<CostAllocationDTO> allocations; // 费用归属及分摊明细


    // ==========================================
    // 费用合计
    // ==========================================

    private BigDecimal subsidyTotal; // 补助总金额

    private BigDecimal mealSubsidyTotal; // 餐费补助合计

    private BigDecimal transportSubsidyTotal; // 交通补助合计

    private BigDecimal commSubsidyTotal; // 通讯补助合计


    // ==========================================
    // 备注信息
    // ==========================================

    private String remark; // 备注说明内容


    // ==========================================
    // 内部类定义（子表明细 DTO）
    // ==========================================

    // 补录行程
    @Data
    public static class TripDTO {

        private String id; // 行程ID

        private String reimId; // 关联主表ID

        private String travelerId; // 出行人员

        private String departDate; // 出发日期

        private String arriveDate; // 到达日期

        private String departCityNo; // 出发城市

        private String arriveCityNo; // 到达城市

        private String tripDesc; // 行程说明
    }

    // 补助信息
    @Data
    public static class SubsidyDTO {

        private String id; // 补助明细ID

        private String reimId; // 关联主表ID

        private String tripId; // 关联行程单明细的id

        private String travelerId; // 出行人

        private String startDate; // 出差日期(起)

        private String endDate; // 出差日期(止)

        private Integer subsidyDays; // 补助天数

        private String departCityNo; // 出发城市

        private String arriveCityNo; // 到达城市

        private String subsidyCityNo; // 补助城市/行程目的地

        private BigDecimal applyAmount; // 申请金额

        private BigDecimal subsidyAmount; // 补助金额

        private List<CalendarDTO> calendars; // 补助日历明细
    }

    // 补助日历
    @Data
    public static class CalendarDTO {

        private String id; // 日历明细ID

        private String subsidyInfoId; // 关联补助明细ID

        private String subsidyDate; // 出差日期

        private String weekDay; // 星期

        private String cityNo; // 补助城市

        private Integer mealChecked; // 餐费补助 (勾选框)

        private BigDecimal mealStandard; // 餐费补助 (标准金额)

        private BigDecimal mealAmount; // 餐费补助 (实发金额输入框)

        private Integer transportChecked; // 交通补助 (勾选框)

        private BigDecimal transportStandard; // 交通补助 (标准金额)

        private BigDecimal transportAmount; // 交通补助 (实发金额输入框)

        private Integer commChecked; // 通讯补助 (勾选框)

        private BigDecimal commStandard; // 通讯补助 (标准金额)

        private BigDecimal commAmount; // 通讯补助 (实发金额输入框)
    }

    // 费用分摊
    @Data
    public static class CostAllocationDTO {

        private String id; // 分摊明细ID

        private String reimId; // 关联主表ID

        private String companyId; // 费用归属

        private String projectId; // 项目

        private BigDecimal allocRatio; // 分摊比例 %

        private BigDecimal allocAmount; // 分摊金额

        private Integer sortOrder; // 排序值，第几行分摊
    }
}
