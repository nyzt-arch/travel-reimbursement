package com.example.reimbursementback.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reimbursementback.entity.business.FkReimTrip;
import java.util.List;

public interface FkReimTripService extends IService<FkReimTrip> {
    List<FkReimTrip> getTripsByReimId(String reimId);
    void deleteTripsByReimId(String reimId);
}
