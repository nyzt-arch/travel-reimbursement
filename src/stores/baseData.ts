import { defineStore } from 'pinia';
import type { Company, Department, Employee, City, Project, BusinessType } from '../types';

export const useBaseDataStore = defineStore('baseData', {
  state: () => ({
    companies: [
      { id: 'C001', no: 'COMP-001', name: '北京分公司' },
      { id: 'C002', no: 'COMP-002', name: '上海分公司' },
      { id: 'C003', no: 'COMP-003', name: '武汉分公司' },
      { id: 'C004', no: 'COMP-004', name: '杭州分公司' },
      { id: 'C005', no: 'COMP-005', name: '荆州分公司' }
    ] as Company[],

    departments: [
      { id: 'D001', no: 'CS001', name: '测试部' },
      { id: 'D002', no: 'DEV01', name: '开发部' },
      { id: 'D003', no: 'PD001', name: '产品部' },
      { id: 'D004', no: 'FIN01', name: '财务部' },
      { id: 'D005', no: 'CSM01', name: '客户成功部' },
      { id: 'D006', no: 'TRV01', name: '航旅事业部' }
    ] as Department[],

    employees: [
      { id: 'E001', no: '202101497', name: '徐年年' },
      { id: 'E002', no: '202101498', name: '张三' },
      { id: 'E003', no: '202101499', name: '李四' },
      { id: 'E004', no: '202101500', name: '王五' },
      { id: 'E005', no: '202101501', name: '赵六' },
      { id: 'E006', no: '202101502', name: '钱七' }
    ] as Employee[],

    cities: [
      { cityNo: '110000', cityName: '北京', cityType: '1' },
      { cityNo: '310000', cityName: '上海', cityType: '1' },
      { cityNo: '420100', cityName: '武汉', cityType: '2' },
      { cityNo: '330100', cityName: '杭州', cityType: '2' },
      { cityNo: '421000', cityName: '荆州', cityType: '3' }
    ] as City[],

    projects: [
      { id: 'P001', no: 'PROJ-升级', name: '移动办公升级项目' },
      { id: 'P002', no: 'PROJ-提效', name: '研发提效工程' },
      { id: 'P003', no: 'PROJ-满意', name: '客户满意度提升项目' }
    ] as Project[],

    businessTypes: [
      {
        id: 'B001',
        no: 'BT-01',
        name: '员工差旅活动',
        thereSubordinateNode: '1',
        superiorId: 'none',
        children: [
          {
            id: 'B00101',
            no: 'BT-01-01',
            name: '境内出差',
            thereSubordinateNode: '1',
            superiorId: 'B001',
            children: [
              { id: 'B0010101', no: 'BT-01-01-01', name: '项目出差', thereSubordinateNode: '0', superiorId: 'B00101' },
              { id: 'B0010102', no: 'BT-01-01-02', name: '日常出差', thereSubordinateNode: '0', superiorId: 'B00101' }
            ]
          },
          {
            id: 'B00102',
            no: 'BT-01-02',
            name: '境外出差',
            thereSubordinateNode: '1',
            superiorId: 'B001',
            children: [
              { id: 'B0010201', no: 'BT-01-02-01', name: '商务考察', thereSubordinateNode: '0', superiorId: 'B00102' },
              { id: 'B0010202', no: 'BT-01-02-02', name: '学术会议', thereSubordinateNode: '0', superiorId: 'B00102' }
            ]
          }
        ]
      },
      {
        id: 'B002',
        no: 'BT-02',
        name: '费用报销大类',
        thereSubordinateNode: '1',
        superiorId: 'none',
        children: [
          { id: 'B00201', no: 'BT-02-01', name: '办公费用', thereSubordinateNode: '0', superiorId: 'B002' },
          {
            id: 'B00202',
            no: 'BT-02-02',
            name: '市场费用',
            thereSubordinateNode: '1',
            superiorId: 'B002',
            children: [
              { id: 'B0020201', no: 'BT-02-02-01', name: '广告宣传', thereSubordinateNode: '0', superiorId: 'B00202' }
            ]
          }
        ]
      }
    ] as BusinessType[]
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
  }
});
