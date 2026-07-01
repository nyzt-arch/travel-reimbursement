package com.example.reimbursementback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reimbursementback.entity.business.FkReimTrip;
import com.example.reimbursementback.mapper.FkReimTripMapper;
import com.example.reimbursementback.service.FkReimTripService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FkReimTripServiceImpl extends ServiceImpl<FkReimTripMapper, FkReimTrip> implements FkReimTripService {

    @Override
    public List<FkReimTrip> getTripsByReimId(String reimId) {
        return this.list(new LambdaQueryWrapper<FkReimTrip>()
                .eq(FkReimTrip::getReimId, reimId)
                .orderByAsc(FkReimTrip::getSortOrder));
    }

    @Override
    public void deleteTripsByReimId(String reimId) {
        this.remove(new LambdaQueryWrapper<FkReimTrip>().eq(FkReimTrip::getReimId, reimId));
    }
}
