package com.example.reimbursementback.entity.business;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("fk_reim_trip")
public class FkReimTrip {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String reimId;

    private String personId;

    private String personNo;

    private String personName;

    private String departureCityNo;

    private String departureCityName;

    private String arrivalCityNo;

    private String arrivalCityName;

    private LocalDate startDate;

    private LocalDate endDate;

    private String tripDesc;

    private Integer sortOrder;

    private LocalDateTime creationTime;

    private LocalDateTime updateTime;
}