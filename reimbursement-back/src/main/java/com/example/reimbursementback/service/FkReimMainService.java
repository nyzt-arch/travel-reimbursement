package com.example.reimbursementback.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reimbursementback.dto.ReimbursementQueryDTO;
import com.example.reimbursementback.dto.ReimbursementSaveDTO;
import com.example.reimbursementback.entity.business.FkReimMain;
import com.example.reimbursementback.vo.ReimbursementDetailVO;
import com.example.reimbursementback.vo.ReimbursementListVO;

public interface FkReimMainService extends IService<FkReimMain> {
    Page<ReimbursementListVO> queryList(ReimbursementQueryDTO queryDTO);
    ReimbursementDetailVO getDetail(String id);
    String saveDraft(ReimbursementSaveDTO saveDTO);
    void submitReimbursement(ReimbursementSaveDTO saveDTO);
    void deleteReimbursement(String id);
    void cancelReimbursement(String id);
}
