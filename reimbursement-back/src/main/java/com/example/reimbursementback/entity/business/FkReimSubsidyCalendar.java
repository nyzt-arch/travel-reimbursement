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
@TableName("fk_reim_subsidy_calendar")
public class FkReimSubsidyCalendar {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String reimId;
    private String subsidyId;
    private String tripId;

    private LocalDate calendarDate;
    private String weekDay;
    private String cityName;

    private String mealChecked;
    private BigDecimal mealStandard;
    private BigDecimal mealAmount;

    private String transportChecked;
    private BigDecimal transportStandard;
    private BigDecimal transportAmount;

    private String phoneChecked;
    private BigDecimal phoneStandard;
    private BigDecimal phoneAmount;

    private LocalDateTime creationTime;
}