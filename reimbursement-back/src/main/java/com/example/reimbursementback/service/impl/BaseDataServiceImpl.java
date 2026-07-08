package com.example.reimbursementback.service.impl;

import com.example.reimbursementback.entity.base.*;
import com.example.reimbursementback.mapper.*;
import com.example.reimbursementback.service.BaseDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BaseDataServiceImpl implements BaseDataService {

    @Autowired
    private BaseCompanyMapper baseCompanyMapper;

    @Autowired
    private BaseDepartmentMapper baseDepartmentMapper;

    @Autowired
    private BaseEmployeeMapper baseEmployeeMapper;

    @Autowired
    private BaseCityMapper baseCityMapper;

    @Autowired
    private BaseProjectMapper baseProjectMapper;

    @Autowired
    private BaseBusinessTypeMapper baseBusinessTypeMapper;

    @Override
    public List<BaseCompany> getAllCompanies() {
        return baseCompanyMapper.selectList(null);
    }

    @Override
    public List<BaseDepartment> getAllDepartments() {
        return baseDepartmentMapper.selectList(null);
    }

    @Override
    public List<BaseEmployee> getAllEmployees() {
        return baseEmployeeMapper.selectList(null);
    }

    @Override
    public List<BaseCity> getAllCities() {
        return baseCityMapper.selectList(null);
    }

    @Override
    public List<BaseProject> getAllProjects() {
        return baseProjectMapper.selectList(null);
    }

    @Override
    public List<BaseBusinessType> getBusinessTypeTree() {
        List<BaseBusinessType> all = baseBusinessTypeMapper.selectList(null);
        List<BaseBusinessType> roots = new ArrayList<>();
        Map<String, BaseBusinessType> map = new HashMap<>();

        // 1. 放入Map，初始化children列表
        for (BaseBusinessType type : all) {
            type.setChildren(new ArrayList<>());
            map.put(type.getBusinessTypeId(), type);
        }

        // 2. 组装树形结构
        for (BaseBusinessType type : all) {
            if ("none".equalsIgnoreCase(type.getSuperiorId())) {
                roots.add(type);
            } else {
                BaseBusinessType parent = map.get(type.getSuperiorId());
                if (parent != null) {
                    parent.getChildren().add(type);
                }
            }
        }
        return roots;
    }
}
