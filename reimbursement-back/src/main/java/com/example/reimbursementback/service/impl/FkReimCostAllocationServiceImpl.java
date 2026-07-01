package com.example.reimbursementback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reimbursementback.entity.business.FkReimCostAllocation;
import com.example.reimbursementback.mapper.FkReimCostAllocationMapper;
import com.example.reimbursementback.service.FkReimCostAllocationService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FkReimCostAllocationServiceImpl extends ServiceImpl<FkReimCostAllocationMapper, FkReimCostAllocation> implements FkReimCostAllocationService {

    @Override
    public List<FkReimCostAllocation> getAllocationsByReimId(String reimId) {
        return this.list(new LambdaQueryWrapper<FkReimCostAllocation>()
                .eq(FkReimCostAllocation::getReimId, reimId)
                .orderByAsc(FkReimCostAllocation::getSortOrder));
    }

    @Override
    public void deleteAllocationsByReimId(String reimId) {
        this.remove(new LambdaQueryWrapper<FkReimCostAllocation>().eq(FkReimCostAllocation::getReimId, reimId));
    }
}
