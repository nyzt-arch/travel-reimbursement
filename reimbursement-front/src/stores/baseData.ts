import { defineStore } from 'pinia';
import type { Company, Department, Employee, City, Project, BusinessType } from '../types';

export const useBaseDataStore = defineStore('baseData', {
  state: () => ({
    //首页费用归属公司
    companies: [
      { id: '1C54557F1782E000', no: '0407', name: '胜意科技北京分公司' },
      { id: '19218A262C976000', no: '0408', name: '胜意科技上海分公司' },
      { id: '1C61686865DA8000', no: '0409', name: '胜意科技武汉分公司' },
      { id: '1717271D1DA15000', no: '0410', name: '胜意科技杭州分公司' },
      { id: '16AE93CC7EF92002', no: '0411', name: '胜意科技荆州分公司' }
    ] as Company[],

    //首页报销部门
    departments: [
      { id: '13AB8D7B52A9B002', no: '072001', name: '客户成功事业部' },
      { id: '13BFD31C6029A002', no: '072002', name: '企业消费事业部' },
      { id: '14515BB4BFB92003', no: '072003', name: '企业费控事业部' },
      { id: '19206611C47A6000', no: '072004', name: '集采事业部' },
      { id: '19D32F9FE9647000', no: '072005', name: '航旅事业部' },
      { id: '13C7E2BAE0393001', no: '072006', name: '运营事业部' },
      { id: '14055D22BB808001', no: '072007', name: '营销事业部' }
    ] as Department[],

    //首页报销人
    employees: [
      { id: '13AB3A3F72409002', no: '74541', name: '徐年年' },
      { id: '13AB498CC6409002', no: '74008', name: '郑雨雪' },
      { id: '13AB4A56BB009002', no: '21552', name: '邹薇' },
      { id: '13AB591FE8009002', no: '80681', name: '王成军' },
      { id: '13AB77281A408001', no: '89899', name: '潘展飞' },
      { id: '13AB7925EB808001', no: '10503', name: '姜林' }
    ] as Employee[],

    //首页业务内容
    businessTypes: [
      {
        id: '18F0916A8C2C4000',
        no: '1001001',
        name: '员工差旅活动',
        thereSubordinateNode: '1',
        superiorId: 'none',
        children: [
          {
            id: '18F091913EEC4000',
            no: '100100101',
            name: '境内出差',
            thereSubordinateNode: '1',
            superiorId: '18F0916A8C2C4000',
            children: [
              { id: '1B5FEB7DD4396000', no: '10010010101', name: '项目出差', thereSubordinateNode: '0', superiorId: '18F091913EEC4000' },
              { id: '1A92E43082EFC000', no: '10010010102', name: '市场拓展出差', thereSubordinateNode: '0', superiorId: '18F091913EEC4000' }
            ]
          },
          {
            id: '13AB3A4138008001',
            no: '100100102',
            name: '境外出差',
            thereSubordinateNode: '1',
            superiorId: '18F0916A8C2C4000',
            children: [
              { id: '13AB3A4248008002', no: '10010010201', name: '国外考察', thereSubordinateNode: '0', superiorId: '13AB3A4138008001' },
              { id: '13AB3A4154008001', no: '10010010202', name: '售后维护出差', thereSubordinateNode: '0', superiorId: '13AB3A4138008001' }
            ]
          }
        ]
      },
      {
        id: '13AB3A4172008001',
        no: '1001002',
        name: '人力资源',
        thereSubordinateNode: '1',
        superiorId: 'none',
        children: [
          { id: '13AB3A418F808001', no: '100100201', name: '个人团队培训', thereSubordinateNode: '0', superiorId: '13AB3A4172008001' },
          { id: '13AB3A41AC408001', no: '100100202', name: '招聘会', thereSubordinateNode: '0', superiorId: '13AB3A4172008001' }
        ]
      },
      {
        id: '13AB3A41CD808002',
        no: '1001003',
        name: '员工福利',
        thereSubordinateNode: '1',
        superiorId: 'none',
        children: [
          { id: '13AB3A41ED408002', no: '100100301', name: '员工旅游', thereSubordinateNode: '0', superiorId: '13AB3A41CD808002' },
          { id: '13AB3A420CC08002', no: '100100302', name: '员工团建', thereSubordinateNode: '0', superiorId: '13AB3A41CD808002' },
          { id: '13AB3A422A808001', no: '100100303', name: '员工体检', thereSubordinateNode: '0', superiorId: '13AB3A41CD808002' }
        ]
      }
    ] as BusinessType[],
    cities: [
      { cityNo: '10119', cityName: '北京', cityType: '1' },
      { cityNo: '10621', cityName: '上海', cityType: '1' },
      { cityNo: '10458', cityName: '武汉', cityType: '2' },
      { cityNo: '10216', cityName: '杭州', cityType: '2' },
      { cityNo: '10455', cityName: '荆州', cityType: '3' }
    ] as City[],

    projects: [
      { id: '12BC248B25083001', no: 'nonProjectRelated', name: '非项目类费用归集' },
      { id: '1C811ABF96195000', no: 'centralChina', name: '华中客户定制化项目' },
      { id: '1C5931735AC4A000', no: 'southChina', name: '华南客户定制化项目' },
      { id: '1771EC45F2443000', no: 'northChina', name: '华北客户定制化项目' },
      { id: '1762792DB4E9A002', no: 'eastChina', name: '华东客户定制化项目' },
      { id: '17071065FC29A002', no: 'southWest', name: '西南客户定制化项目' },
      { id: '162664EBE9ABE001', no: 'northWest', name: '西北客户定制化项目' },
      { id: '162664B8526BE002', no: 'northEast', name: '东北客户定制化项目' }
    ] as Project[],
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
