package com.example.reimbursementback.controller;

import com.example.reimbursementback.common.Result;
import com.example.reimbursementback.entity.base.*;
import com.example.reimbursementback.service.BaseDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/base_data")
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
