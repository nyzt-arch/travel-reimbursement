package com.example.reimbursementback.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reimbursementback.entity.business.FkReimCostAllocation;
import java.util.List;

public interface FkReimCostAllocationService extends IService<FkReimCostAllocation> {
    List<FkReimCostAllocation> getAllocationsByReimId(String reimId);
    void deleteAllocationsByReimId(String reimId);
}
