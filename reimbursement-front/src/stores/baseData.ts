//修改原因：清空了前端原先硬编码的模拟公司、部门、员工信息。
// 实现了一个并行的 loadAllBaseData() 请求，
// 这样前端页面在挂载时会调用该 action 从数据库
// 查询真实的静态主数据字典并缓存至 Pinia，
// 从而避免了脏数据与前后端不一致的情况。
import { defineStore } from 'pinia';
import type { Company, Department, Employee, City, Project, BusinessType } from '../types';
import * as baseDataApi from '../api/baseData';

interface BackendCompany {
  reimCompanyId: string;
  reimCompanyNo: string;
  reimCompanyName: string;
}

interface BackendDepartment {
  reimDepartmentId: string;
  reimDepartmentNo: string;
  reimDepartmentName: string;
}

interface BackendEmployee {
  reimburserId: string;
  reimburserNo: string;
  reimburserName: string;
}

interface BackendBusinessType {
  businessTypeId: string;
  businessTypeNo: string;
  businessTypeName: string;
  thereSubordinateNode: '0' | '1';
  superiorId: string;
  children?: BackendBusinessType[];
}

interface BackendProject {
  projectId: string;
  projectNo: string;
  projectName: string;
}

export const useBaseDataStore = defineStore('baseData', {
  state: () => ({
    companies: [] as Company[],
    departments: [] as Department[],
    employees: [] as Employee[],
    businessTypes: [] as BusinessType[],
    cities: [] as City[],
    projects: [] as Project[],
    isLoaded: false
  }),
  getters: {
    getCompanyNameById: (state) => (id: string) => {
      return state.companies.find(c => c.id === id)?.name || '';
    },
    getDeptNameById: (state) => (id: string) => {
      const dept = state.departments.find(d => d.id === id);
      return dept ? `[${dept.no}]${dept.name}` : '';
    },
    getEmployeeNameById: (state) => (id: string) => {
      const emp = state.employees.find(e => e.id === id);
      return emp ? `${emp.name}[${emp.no}]` : '';
    },
    getCityNameByNo: (state) => (no: string) => {
      return state.cities.find(c => c.cityNo === no)?.cityName || '';
    },
    getProjectNameById: (state) => (id: string) => {
      return state.projects.find(p => p.id === id)?.name || '';
    },
    getBusinessTypeNameById: (state) => {
      return (id: string): string => {
        const findInTree = (nodes: BusinessType[]): string => {
          for (const node of nodes) {
            if (node.id === id) return node.name;
            if (node.children) {
              const res = findInTree(node.children);
              if (res) return res;
            }
          }
          return '';
        };
        return findInTree(state.businessTypes);
      };
    }
  },
  actions: {
    async loadAllBaseData() {
      if (this.isLoaded) return;
      try {
        const [companies, departments, employees, businessTypes, cities, projects] = await Promise.all([
          baseDataApi.getCompanies(),
          baseDataApi.getDepartments(),
          baseDataApi.getEmployees(),
          baseDataApi.getBusinessTypes(),
          baseDataApi.getCities(),
          baseDataApi.getProjects(),
        ]);
        
        // 后端返回实体类的属性名称与前端接口字段不一致，进行属性适配映射
        this.companies = (companies as unknown as BackendCompany[]).map(item => ({
          id: item.reimCompanyId,
          no: item.reimCompanyNo,
          name: item.reimCompanyName
        }));
        
        this.departments = (departments as unknown as BackendDepartment[]).map(item => ({
          id: item.reimDepartmentId,
          no: item.reimDepartmentNo,
          name: item.reimDepartmentName
        }));
        
        this.employees = (employees as unknown as BackendEmployee[]).map(item => ({
          id: item.reimburserId,
          no: item.reimburserNo,
          name: item.reimburserName
        }));
        
        function mapBusinessType(item: BackendBusinessType): BusinessType {
          return {
            id: item.businessTypeId,
            no: item.businessTypeNo,
            name: item.businessTypeName,
            thereSubordinateNode: item.thereSubordinateNode,
            superiorId: item.superiorId,
            children: item.children && item.children.length ? item.children.map(mapBusinessType) : undefined
          };
        }
        this.businessTypes = (businessTypes as unknown as BackendBusinessType[]).map(mapBusinessType);
        
        this.cities = cities;
        
        this.projects = (projects as unknown as BackendProject[]).map(item => ({
          id: item.projectId,
          no: item.projectNo,
          name: item.projectName
        }));
        this.isLoaded = true;
      } catch (error) {
        console.error('Failed to load base data from server:', error);
      }
    }
  }
});
