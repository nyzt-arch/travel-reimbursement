/**
 * 基础数据 Store
 * 统一管理系统中的基础字典数据（公司、部门、员工、城市、项目、业务类型）
 * 页面启动时加载一次，全局共享，避免各组件重复请求
 *
 * Pinia Store 三要素：
 *   state   = 数据（类似 data）
 *   getters = 计算属性（类似 computed），根据 ID 查名称
 *   actions = 异步操作（发 HTTP 请求，改变 state）
 */

import { defineStore } from 'pinia'
import type { Company, Department, Employee, City, Project, BusinessType } from '../types'
import * as baseDataApi from '../api/baseData'

// 后端返回的原始字段名和前端不一样，先定义后端的数据形状，拿到后做字段映射
interface BackendCompany {
  reimCompanyId: string
  reimCompanyNo: string
  reimCompanyName: string
}

interface BackendDepartment {
  reimDepartmentId: string
  reimDepartmentNo: string
  reimDepartmentName: string
}

interface BackendEmployee {
  reimburserId: string
  reimburserNo: string
  reimburserName: string
}

// 树形结构：一个业务类型下可以有子类型（如 办公用品 → 纸张 → A4纸）
interface BackendBusinessType {
  businessTypeId: string
  businessTypeNo: string
  businessTypeName: string
  thereSubordinateNode: '0' | '1'  // 是否有下级节点
  superiorId: string               // 上级节点 ID
  children?: BackendBusinessType[] // 子节点，类型是自身 → 递归结构
}

interface BackendProject {
  projectId: string
  projectNo: string
  projectName: string
}

export const useBaseDataStore = defineStore('baseData', {
  state: () => ({
    companies: [] as Company[],
    departments: [] as Department[],
    employees: [] as Employee[],
    businessTypes: [] as BusinessType[],
    cities: [] as City[],
    projects: [] as Project[],
    isLoaded: false, // 标记是否已加载，防止重复请求
  }),

  getters: {
    // 以下 getter 都是"根据 ID 查名称"，供组件显示用

    getCompanyNameById: (state) => (id: string) => {
      // find() 找第一个匹配项 → ?.name 安全取值 → || '' 兜底
      return state.companies.find((c) => c.id === id)?.name || ''
    },

    getDeptNameById: (state) => (id: string) => {
      const dept = state.departments.find((d) => d.id === id)
      return dept ? `[${dept.no}]${dept.name}` : ''
    },

    getEmployeeNameById: (state) => (id: string) => {
      const emp = state.employees.find((e) => e.id === id)
      return emp ? `${emp.name}[${emp.no}]` : ''
    },

    getCityNameByNo: (state) => (no: string) => {
      return state.cities.find((c) => c.cityNo === no)?.cityName || ''
    },

    getProjectNameById: (state) => (id: string) => {
      return state.projects.find((p) => p.id === id)?.name || ''
    },

    /**
     * 递归搜索树形结构
     * 因为业务类型是多层嵌套的（树），不能用简单的 find，需要递归深入每一层
     * 深度优先搜索（DFS）：沿一条路径走到底，找不到再回溯
     */
    getBusinessTypeNameById: (state) => {
      return (id: string): string => {
        const findInTree = (nodes: BusinessType[]): string => {
          for (const node of nodes) {
            if (node.id === id) return node.name
            if (node.children) {
              const res = findInTree(node.children) // 递归调用自己，深入下一层
              if (res) return res
            }
          }
          return ''
        }
        return findInTree(state.businessTypes)
      }
    },
  },

  // 异步操作：发 HTTP 请求，改变 state
  actions: {
    async loadAllBaseData() {
      // 防重复加载：已加载且数据确实存在就跳过
      // 防止 HMR（热更新）重置状态后误判导致重新请求0
      if (this.isLoaded && this.cities.length > 0) return

      try {
        // Promise.all：6 个请求同时发出（并行），而非一个接一个（串行）
        // 总耗时 ≈ 最慢那个请求的时间，而非 6 个之和
        const [companies, departments, employees, businessTypes, cities, projects] =
          await Promise.all([
            baseDataApi.getCompanies(),
            baseDataApi.getDepartments(),
            baseDataApi.getEmployees(),
            baseDataApi.getBusinessTypes(),
            baseDataApi.getCities(),
            baseDataApi.getProjects(),
          ])

        // 字段映射：后端字段名 → 前端字段名
        // as unknown as BackendXxx[]：告诉 TS 实际返回结构和声明不同，跳过类型检查
        this.companies = (companies as unknown as BackendCompany[]).map((item) => ({
          id: item.reimCompanyId,
          no: item.reimCompanyNo,
          name: item.reimCompanyName,
        }))

        this.departments = (departments as unknown as BackendDepartment[]).map((item) => ({
          id: item.reimDepartmentId,
          no: item.reimDepartmentNo,
          name: item.reimDepartmentName,
        }))

        this.employees = (employees as unknown as BackendEmployee[]).map((item) => ({
          id: item.reimburserId,
          no: item.reimburserNo,
          name: item.reimburserName,
        }))

        // 递归映射树形结构：每个节点的 children 也执行同样的转换
        function mapBusinessType(item: BackendBusinessType): BusinessType {
          return {
            id: item.businessTypeId,
            no: item.businessTypeNo,
            name: item.businessTypeName,
            thereSubordinateNode: item.thereSubordinateNode,
            superiorId: item.superiorId,
            children:
            // 如果有子节点就递归映射，否则返回 undefined
              item.children && item.children.length
                ? item.children.map(mapBusinessType) // 递归：子节点也走同样的映射
                : undefined,
          }
        }
        this.businessTypes = (businessTypes as unknown as BackendBusinessType[]).map(
          mapBusinessType,
        )

        this.cities = cities // 城市字段名已匹配，无需映射

        this.projects = (projects as unknown as BackendProject[]).map((item) => ({
          id: item.projectId,
          no: item.projectNo,
          name: item.projectName,
        }))

        this.isLoaded = true
      } catch (error) {
        console.error('基础数据加载失败:', error)
      }
    },
  },
})
