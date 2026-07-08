package com.example.reimbursementback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reimbursementback.entity.business.FkReimSubsidyCalendar;
import com.example.reimbursementback.mapper.FkReimSubsidyCalendarMapper;
import com.example.reimbursementback.service.FkReimSubsidyCalendarService;
import org.springframework.stereotype.Service;
import java.util.List;
//补助日历服务实现类
@Service
public class FkReimSubsidyCalendarServiceImpl extends ServiceImpl<FkReimSubsidyCalendarMapper, FkReimSubsidyCalendar> implements FkReimSubsidyCalendarService {

    @Override
    public List<FkReimSubsidyCalendar> getCalendarsBySubsidyId(String subsidyId) {
        return this.list(new LambdaQueryWrapper<FkReimSubsidyCalendar>()
                .eq(FkReimSubsidyCalendar::getSubsidyId, subsidyId)
                .orderByAsc(FkReimSubsidyCalendar::getCalendarDate));
    } //通过 .orderByAsc对日期进行了排序：前端在渲染日历矩阵时，就能按时间轴正确展示每天的补助明细

    @Override
    public List<FkReimSubsidyCalendar> getCalendarsByReimId(String reimId) {
        return this.list(new LambdaQueryWrapper<FkReimSubsidyCalendar>()
                .eq(FkReimSubsidyCalendar::getReimId, reimId)
                .orderByAsc(FkReimSubsidyCalendar::getCalendarDate));
    }

    @Override
    public void deleteCalendarsByReimId(String reimId) {
        this.remove(new LambdaQueryWrapper<FkReimSubsidyCalendar>().eq(FkReimSubsidyCalendar::getReimId, reimId));
    }
}
