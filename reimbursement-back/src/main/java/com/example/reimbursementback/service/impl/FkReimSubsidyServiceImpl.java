package com.example.reimbursementback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reimbursementback.dto.ReimbursementSaveDTO;
import com.example.reimbursementback.entity.base.BaseCity;
import com.example.reimbursementback.entity.base.BaseEmployee;
import com.example.reimbursementback.entity.business.FkReimSubsidy;
import com.example.reimbursementback.entity.business.FkReimSubsidyCalendar;
import com.example.reimbursementback.mapper.BaseCityMapper;
import com.example.reimbursementback.mapper.BaseEmployeeMapper;
import com.example.reimbursementback.mapper.FkReimSubsidyMapper;
import com.example.reimbursementback.service.FkReimSubsidyCalendarService;
import com.example.reimbursementback.service.FkReimSubsidyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
//补助信息服务实现类
@Service
public class FkReimSubsidyServiceImpl extends ServiceImpl<FkReimSubsidyMapper, FkReimSubsidy> implements FkReimSubsidyService {

    @Autowired
    private FkReimSubsidyCalendarService calendarService;

    @Autowired
    private BaseEmployeeMapper baseEmployeeMapper;

    @Autowired
    private BaseCityMapper baseCityMapper;

    @Override
    public List<FkReimSubsidy> getSubsidiesByReimId(String reimId) {
        return this.list(new LambdaQueryWrapper<FkReimSubsidy>().eq(FkReimSubsidy::getReimId, reimId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSubsidiesByReimId(String reimId) {
        calendarService.deleteCalendarsByReimId(reimId);
        this.remove(new LambdaQueryWrapper<FkReimSubsidy>().eq(FkReimSubsidy::getReimId, reimId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSubsidiesAndCalendars(String reimId, List<ReimbursementSaveDTO.SubsidyDTO> subsidies, boolean isNew) {
        if (subsidies == null || subsidies.isEmpty()) {
            return;
        } //实现了批量保存逻辑，每段行程对应多条补助记录，使用批量保存可以显著减少数据库交互次数，提升性能

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<FkReimSubsidyCalendar> calendarsToSave = new ArrayList<>();

        for (ReimbursementSaveDTO.SubsidyDTO subDTO : subsidies) {
            FkReimSubsidy subsidy = new FkReimSubsidy();
            
            // 处理 ID 占位符
            if (isNew || subDTO.getId() == null || subDTO.getId().trim().isEmpty() 
                    || subDTO.getId().startsWith("NEW_") 
                    || subDTO.getId().startsWith("COPY_") 
                    || subDTO.getId().startsWith("S_")) {
                subsidy.setId(null);
            } else {
                subsidy.setId(subDTO.getId());
            }

            subsidy.setReimId(reimId);
            subsidy.setTripId(subDTO.getTripId());
            subsidy.setPersonId(subDTO.getTravelerId());

            // 冗余出行人姓名
            if (subDTO.getTravelerId() != null) {
                BaseEmployee emp = baseEmployeeMapper.selectById(subDTO.getTravelerId());
                if (emp != null) {
                    subsidy.setPersonName(emp.getReimburserName());
                }
            }

            subsidy.setStartDate(subDTO.getStartDate() != null ? LocalDate.parse(subDTO.getStartDate(), formatter) : null);
            subsidy.setEndDate(subDTO.getEndDate() != null ? LocalDate.parse(subDTO.getEndDate(), formatter) : null);
            subsidy.setDays(subDTO.getSubsidyDays());

            // 冗余补助城市与城市类型
            if (subDTO.getSubsidyCityNo() != null) {
                BaseCity city = baseCityMapper.selectById(subDTO.getSubsidyCityNo());
                if (city != null) {
                    subsidy.setSubsidyCity(city.getCityName());
                    subsidy.setCityType(city.getCityType());
                }
            }

            subsidy.setApplyAmount(subDTO.getApplyAmount());
            subsidy.setSubsidyAmount(subDTO.getSubsidyAmount());
            subsidy.setCreationTime(LocalDateTime.now());
            subsidy.setUpdateTime(LocalDateTime.now());

            // 计算实际勾选的餐费、交通和通讯补助小计
            BigDecimal mealTotal = BigDecimal.ZERO;
            BigDecimal transportTotal = BigDecimal.ZERO;
            BigDecimal phoneTotal = BigDecimal.ZERO;

            if (subDTO.getCalendars() != null) {
                for (ReimbursementSaveDTO.CalendarDTO calDTO : subDTO.getCalendars()) {
                    if (calDTO.getMealChecked() != null && calDTO.getMealChecked() == 1) {
                        mealTotal = mealTotal.add(calDTO.getMealAmount() != null ? calDTO.getMealAmount() : BigDecimal.ZERO);
                    }
                    if (calDTO.getTransportChecked() != null && calDTO.getTransportChecked() == 1) {
                        transportTotal = transportTotal.add(calDTO.getTransportAmount() != null ? calDTO.getTransportAmount() : BigDecimal.ZERO);
                    }
                    if (calDTO.getCommChecked() != null && calDTO.getCommChecked() == 1) {
                        phoneTotal = phoneTotal.add(calDTO.getCommAmount() != null ? calDTO.getCommAmount() : BigDecimal.ZERO);
                    }
                }
            }

            subsidy.setMealAmount(mealTotal);
            subsidy.setTransportAmount(transportTotal);
            subsidy.setPhoneAmount(phoneTotal);

            // 保存补助明细表以生成或保留ID
            this.saveOrUpdate(subsidy);

            // 处理日历明细
            if (subDTO.getCalendars() != null) {
                for (ReimbursementSaveDTO.CalendarDTO calDTO : subDTO.getCalendars()) {
                    FkReimSubsidyCalendar cal = new FkReimSubsidyCalendar();
                    
                    if (isNew || calDTO.getId() == null || calDTO.getId().trim().isEmpty() 
                            || calDTO.getId().startsWith("NEW_") 
                            || calDTO.getId().startsWith("COPY_") 
                            || calDTO.getId().startsWith("C_")) {
                        cal.setId(null);
                    } else {
                        cal.setId(calDTO.getId());
                    }

                    cal.setReimId(reimId);
                    cal.setSubsidyId(subsidy.getId());
                    cal.setTripId(subDTO.getTripId());
                    cal.setCalendarDate(calDTO.getSubsidyDate() != null ? LocalDate.parse(calDTO.getSubsidyDate(), formatter) : null);
                    cal.setWeekDay(calDTO.getWeekDay());
                    
                    // 日历表中冗余城市名称
                    if (calDTO.getCityNo() != null) {
                        BaseCity city = baseCityMapper.selectById(calDTO.getCityNo());
                        cal.setCityName(city != null ? city.getCityName() : calDTO.getCityNo());
                    }

                    cal.setMealChecked(calDTO.getMealChecked() != null ? calDTO.getMealChecked().toString() : "0");
                    cal.setMealStandard(calDTO.getMealStandard());
                    cal.setMealAmount(calDTO.getMealAmount());
                    
                    cal.setTransportChecked(calDTO.getTransportChecked() != null ? calDTO.getTransportChecked().toString() : "0");
                    cal.setTransportStandard(calDTO.getTransportStandard());
                    cal.setTransportAmount(calDTO.getTransportAmount());
                    
                    cal.setPhoneChecked(calDTO.getCommChecked() != null ? calDTO.getCommChecked().toString() : "0");
                    cal.setPhoneStandard(calDTO.getCommStandard());
                    cal.setPhoneAmount(calDTO.getCommAmount());
                    
                    cal.setCreationTime(LocalDateTime.now());
                    
                    calendarsToSave.add(cal);
                }
            }
        }

        if (!calendarsToSave.isEmpty()) {
            calendarService.saveOrUpdateBatch(calendarsToSave);
        }
    }
}
