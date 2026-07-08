package com.example.reimbursementback.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reimbursementback.common.Result;
import com.example.reimbursementback.dto.ReimbursementQueryDTO;
import com.example.reimbursementback.dto.ReimbursementSaveDTO;
import com.example.reimbursementback.service.FkReimMainService;
import com.example.reimbursementback.vo.ReimbursementDetailVO;
import com.example.reimbursementback.vo.ReimbursementListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reimbursement")
public class ReimbursementController {

    @Autowired
    private FkReimMainService fkReimMainService;

    @GetMapping("/list")
    public Result<Page<ReimbursementListVO>> list(ReimbursementQueryDTO queryDTO) {
        Page<ReimbursementListVO> page = fkReimMainService.queryList(queryDTO);
        return Result.success(page);
    }

    @GetMapping("/detail/{id}")
    public Result<ReimbursementDetailVO> detail(@PathVariable("id") String id) {
        ReimbursementDetailVO detail = fkReimMainService.getDetail(id);
        return Result.success(detail);
    }

    @PostMapping("/save")
    public Result<String> save(@RequestBody ReimbursementSaveDTO saveDTO) {
        synchronized (ReimbursementController.class) {
            String id = fkReimMainService.saveDraft(saveDTO);
            return Result.success(id);
        }
    }

    @PostMapping("/submit")
    public Result<Void> submit(@RequestBody ReimbursementSaveDTO saveDTO) {
        synchronized (ReimbursementController.class) {
            fkReimMainService.submitReimbursement(saveDTO);
            return Result.success();
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable("id") String id) {
        fkReimMainService.deleteReimbursement(id);
        return Result.success();
    }

    @PutMapping("/cancel/{id}")
    public Result<Void> cancel(@PathVariable("id") String id) {
        fkReimMainService.cancelReimbursement(id);
        return Result.success();
    }
}