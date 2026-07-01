//修改原因：将获取各个静态主数据的网络请求封装为函数，供 Pinia 状态管理库调用。
import request from './request';
import type { Company, Department, Employee, City, Project, BusinessType } from '../types';

export function getCompanies(): Promise<Company[]> {
  return request.get('/base_data/companies');
}

export function getDepartments(): Promise<Department[]> {
  return request.get('/base_data/departments');
}

export function getEmployees(): Promise<Employee[]> {
  return request.get('/base_data/employees');
}

export function getBusinessTypes(): Promise<BusinessType[]> {
  return request.get('/base_data/businessTypes');
}

export function getCities(): Promise<City[]> {
  return request.get('/base_data/cities');
}

export function getProjects(): Promise<Project[]> {
  return request.get('/base_data/projects');
}
