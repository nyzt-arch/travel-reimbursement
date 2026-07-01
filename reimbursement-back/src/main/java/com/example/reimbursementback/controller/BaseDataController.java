package com.example.reimbursementback.controller;

import com.example.reimbursementback.common.Result;
import com.example.reimbursementback.entity.base.*;
import com.example.reimbursementback.service.BaseDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/base_data") // 对应前端 Vite 开发服务器代理重写后的路径（去掉 /api）
public class BaseDataController {

    @Autowired
    private BaseDataService basedataService;

    @GetMapping("/companies")
    public Result<List<BaseCompany>> getCompanies() {
        return Result.success(basedataService.getAllCompanies());
    }

    @GetMapping("/departments")
    public Result<List<BaseDepartment>> getDepartments() {
        return Result.success(basedataService.getAllDepartments());
    }

    @GetMapping("/employees")
    public Result<List<BaseEmployee>> getEmployees() {
        return Result.success(basedataService.getAllEmployees());
    }

    @GetMapping("/businessTypes")
    public Result<List<BaseBusinessType>> getBusinessTypes() {
        return Result.success(basedataService.getBusinessTypeTree());
    }

    @GetMapping("/cities")
    public Result<List<BaseCity>> getCities() {
        return Result.success(basedataService.getAllCities());
    }

    @GetMapping("/projects")
    public Result<List<BaseProject>> getProjects() {
        return Result.success(basedataService.getAllProjects());
    }
}
//1.将 @RequestMapping("/api/base_data") 改为 /base_data。
//因为前端 vite.config.ts 中的代理配置 rewrite: (path) => path.replace(/^\/api/, '')
//在转发时去掉了 /api 前缀，后端 Controller 的映射需要保持一致。
//2.将 6 个获取基础字典项的接口打通，
//使得前端下拉框（报销人、部门、归属公司、项目等）能从数据库加载真实的行数据。
