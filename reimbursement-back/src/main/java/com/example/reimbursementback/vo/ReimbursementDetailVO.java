package com.example.reimbursementback.vo;

import com.example.reimbursementback.dto.ReimbursementSaveDTO;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ReimbursementDetailVO {
    private String id; // 主键ID
    private String reimNo;
    private Integer status;
    private String createTime;
    private String updateTime;

    // 基础信息
    private String title;
    private String reimburserId;
    private String reimDepartmentId;
    private String reimCompanyId;
    private String businessTypeId;
    private String reason;

    private List<TripVO> trips;
    private List<SubsidyVO> subsidies;
    private List<CostAllocationVO> allocations;

    //费用合计
    private BigDecimal subsidyTotal;
    private BigDecimal mealSubsidyTotal;
    private BigDecimal transportSubsidyTotal;
    private BigDecimal commSubsidyTotal;//通讯补助

    //备注
    private String remark;

    // 补录行程
    @Data
    public static class TripVO {
        private String id; // 行程ID
        private String reimId; // 关联主表ID
        private String travelerId;
        private String departDate;
        private String arriveDate;
        private String departCityNo;
        private String arriveCityNo;
        private String tripDesc;
    }

    // 补助信息
    @Data
    public static class SubsidyVO {
        private String id; // 补助明细ID
        private String reimId; // 关联主表ID
        private String tripId; // 关联行程单明细的id
        private String travelerId;
        private String startDate;
        private String endDate;
        private Integer subsidyDays; // 补助天数
        private String departCityNo; // 出发城市
        private String arriveCityNo;
        private String subsidyCityNo; // 补助城市
        private BigDecimal applyAmount; // 申请金额
        private BigDecimal subsidyAmount;
        private List<CalendarVO> calendars; // 补助日历明细
    }

    // 补助日历
    @Data
    public static class CalendarVO {
        private String id; // 日历明细ID
        private String subsidyInfoId; // 关联补助明细ID
        private String subsidyDate; // 出差日期
        private String weekDay; // 星期
        private String cityNo; // 补助城市
        private Integer mealChecked; // 餐费补助 (勾选框)
        private BigDecimal mealStandard;
        private BigDecimal mealAmount;
        private Integer transportChecked; // 交通补助 (勾选框)
        private BigDecimal transportStandard;
        private BigDecimal transportAmount;
        private Integer commChecked; // 通讯补助 (勾选框)
        private BigDecimal commStandard;
        private BigDecimal commAmount;
    }

    // 费用分摊
    @Data
    public static class CostAllocationVO {
        private String id; // 分摊明细ID
        private String reimId; // 关联主表ID
        private String companyId; //费用归属
        private String projectId;
        private BigDecimal allocRatio;
        private BigDecimal allocAmount;
        private Integer sortOrder;
    }
}