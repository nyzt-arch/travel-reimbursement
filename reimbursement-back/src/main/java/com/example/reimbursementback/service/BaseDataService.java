package com.example.reimbursementback.service;

import com.example.reimbursementback.entity.base.*;
import java.util.List;

public interface BaseDataService {
    List<BaseCompany> getAllCompanies();
    List<BaseDepartment> getAllDepartments();
    List<BaseEmployee> getAllEmployees();
    List<BaseCity> getAllCities();
    List<BaseProject> getAllProjects();
    List<BaseBusinessType> getBusinessTypeTree();
}
