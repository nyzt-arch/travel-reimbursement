package com.example.reimbursementback.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reimbursementback.dto.ReimbursementSaveDTO;
import com.example.reimbursementback.entity.business.FkReimSubsidy;
import java.util.List;

public interface FkReimSubsidyService extends IService<FkReimSubsidy> {
    List<FkReimSubsidy> getSubsidiesByReimId(String reimId);
    void deleteSubsidiesByReimId(String reimId);
    void saveSubsidiesAndCalendars(String reimId, List<ReimbursementSaveDTO.SubsidyDTO> subsidies, boolean isNew);
}