//将所有的数据操作从操纵前端本地模拟数组 this.reimbursements，
// 全部改造为对后端控制器接口的 API 异步请求（reimbursementApi.saveDraft 等），
// 达到将数据持久化到 MySQL 数据库的目的。
import { defineStore } from 'pinia';
import type { Reimbursement } from '../types';
import * as reimbursementApi from '../api/reimbursement';

export const useReimbursementStore = defineStore('reimbursement', {
  state: () => ({
    reimbursements: [] as Reimbursement[],
    total: 0, // 分页总条数
    currentDetail: null as Reimbursement | null
  }),

  actions: {
    // 调用后端 API 获取过滤与分页后的报销单列表
    async fetchList(params: {
      reimNo?: string;
      title?: string;
      reason?: string;
      companyId?: string;
      deptId?: string;
      reimburserId?: string;
      businessTypeId?: string;
      pageNum: number;
      pageSize: number;
    }) {
      const pageResult = await reimbursementApi.getReimbursementList(params);
      this.reimbursements = pageResult.records;
      this.total = pageResult.total;
    },

    // 详情加载
    async loadDetail(id: string) {
      const detail = await reimbursementApi.getReimbursementDetail(id);
      this.currentDetail = detail;
      return this.currentDetail;
    },

    // 初始化本地新建对象 (保留前端逻辑)
    initNewDetail() {
      this.currentDetail = {
        id: `NEW_${Date.now()}`,
        reimNo: '',
        title: '',
        reason: '',
        reimburserId: '',
        reimDepartmentId: '',
        reimCompanyId: '',
        businessTypeId: '',
        subsidyTotal: 0,
        mealSubsidyTotal: 0,
        transportSubsidyTotal: 0,
        commSubsidyTotal: 0,
        remark: '',
        status: 0, // 草稿
        trips: [],
        subsidies: [],
        allocations: []
      };
      return this.currentDetail;
    },

    // 保存草稿
    async saveDraft(data: Reimbursement) {
      const id = await reimbursementApi.saveDraft(data);
      return id;
    },

    // 提交报销单 (状态设为 1)
    async submitSheet(data: Reimbursement) {
      await reimbursementApi.submitSheet(data);
    },

    // 删除单据
    async deleteReimbursement(id: string) {
      await reimbursementApi.deleteReimbursement(id);
    },

    // 作废单据
    async voidReimbursement(id: string) {
      await reimbursementApi.cancelReimbursement(id);
    }
  }
});
