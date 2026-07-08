package com.example.reimbursementback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reimbursementback.entity.business.FkReimTrip;
import com.example.reimbursementback.mapper.FkReimTripMapper;
import com.example.reimbursementback.service.FkReimTripService;
import org.springframework.stereotype.Service;
import java.util.List;
//行程服务实现类
@Service
public class FkReimTripServiceImpl extends ServiceImpl<FkReimTripMapper, FkReimTrip> implements FkReimTripService {

    @Override
    public List<FkReimTrip> getTripsByReimId(String reimId) {
        return this.list(new LambdaQueryWrapper<FkReimTrip>()
                .eq(FkReimTrip::getReimId, reimId)
                .orderByAsc(FkReimTrip::getSortOrder));  /*使用了 .orderByAsc(FkReimTrip::getSortOrder)
                                                          确保前端展示的行程顺序与用户录入顺序一致。 */
    }
    @Override
    public void deleteTripsByReimId(String reimId) {
        this.remove(new LambdaQueryWrapper<FkReimTrip>().eq(FkReimTrip::getReimId, reimId));
    }
}
