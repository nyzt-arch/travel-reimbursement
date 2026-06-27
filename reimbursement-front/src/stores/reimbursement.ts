import { defineStore } from 'pinia';
import type { Reimbursement, Trip, SubsidyInfo, SubsidyCalendar, CostAllocation } from '../types';

function generateReimNo(index: number): string {
  const dateStr = new Date().toISOString().slice(0, 10).replace(/-/g, '');
  return `RCBX${dateStr}${String(index).padStart(3, '0')}`;
}

export const useReimbursementStore = defineStore('reimbursement', {
  state: () => {
    // Generate some initial mock detail data
    const mockTrip1: Trip[] = [
      {
        id: 'T001',
        reimId: 'R001',
        travelerId: 'E001', // 徐年年
        departCityNo: '110000', // 北京
        arriveCityNo: '310000', // 上海
        departDate: '2026-06-20',
        arriveDate: '2026-06-24',
        tripDesc: '华东区大客户交付与部署服务'
      }
    ];

    const mockCalendar1: SubsidyCalendar[] = [];
    const dates = ['2026-06-20', '2026-06-21', '2026-06-22', '2026-06-23', '2026-06-24'];
    const weekdays = ['星期六', '星期日', '星期一', '星期二', '星期三'];
    
    dates.forEach((date, i) => {
      mockCalendar1.push({
        id: `C_M1_${i}`,
        subsidyInfoId: 'S001',
        subsidyDate: date,
        weekDay: weekdays[i] || '',
        cityNo: '310000', // 上海 (一线) -> 餐100, 交40, 通40
        mealChecked: 1,
        mealStandard: 100,
        mealAmount: 100,
        transportChecked: 1,
        transportStandard: 40,
        transportAmount: 40,
        commChecked: 1,
        commStandard: 40,
        commAmount: 40
      });
    });

    const mockSubsidy1: SubsidyInfo[] = [
      {
        id: 'S001',
        reimId: 'R001',
        tripId: 'T001',
        travelerId: 'E001',
        startDate: '2026-06-20',
        endDate: '2026-06-24',
        subsidyDays: 5,
        subsidyCityNo: '310000',
        applyAmount: 900, // 5 * 180 = 900
        subsidyAmount: 900,
        calendars: mockCalendar1
      }
    ];

    const mockAlloc1: CostAllocation[] = [
      {
        id: 'A001',
        reimId: 'R001',
        companyId: 'C002', // 上海分公司
        projectId: 'P001', // 移动办公升级项目
        allocRatio: 0.6,
        allocAmount: 540,
        sortOrder: 1
      },
      {
        id: 'A002',
        reimId: 'R001',
        companyId: 'C001', // 北京分公司
        projectId: 'P002', // 研发提效工程
        allocRatio: 0.4,
        allocAmount: 360,
        sortOrder: 2
      }
    ];

    const mockTrip2: Trip[] = [
      {
        id: 'T002',
        reimId: 'R002',
        travelerId: 'E002', // 张三
        departCityNo: '420100', // 武汉
        arriveCityNo: '110000', // 北京
        departDate: '2026-06-25',
        arriveDate: '2026-06-29',
        tripDesc: '华中地区日常技术支持与外部会议对接'
      }
    ];

    const mockCalendar2: SubsidyCalendar[] = [];
    const dates2 = ['2026-06-25', '2026-06-26', '2026-06-27', '2026-06-28', '2026-06-29'];
    const weekdays2 = ['星期四', '星期五', '星期六', '星期日', '星期一'];
    
    dates2.forEach((date, i) => {
      mockCalendar2.push({
        id: `C_M2_${i}`,
        subsidyInfoId: 'S002',
        subsidyDate: date,
        weekDay: weekdays2[i] || '',
        cityNo: '110000', // 北京 (一线) -> 餐100, 交40, 通40
        mealChecked: 1,
        mealStandard: 100,
        mealAmount: 100,
        transportChecked: 1,
        transportStandard: 40,
        transportAmount: 40,
        commChecked: 1,
        commStandard: 40,
        commAmount: 40
      });
    });

    const mockSubsidy2: SubsidyInfo[] = [
      {
        id: 'S002',
        reimId: 'R002',
        tripId: 'T002',
        travelerId: 'E002',
        startDate: '2026-06-25',
        endDate: '2026-06-29',
        subsidyDays: 5,
        subsidyCityNo: '110000',
        applyAmount: 900,
        subsidyAmount: 900,
        calendars: mockCalendar2
      }
    ];

    return {
      reimbursements: [
        {
          id: 'R001',
          reimNo: 'RCBX20260620001',
          title: '徐年年2026年6月上海客户部署差旅报销',
          reason: '前往上海分公司协助大客户完成系统版本升级与定制化模块部署。',
          reimburserId: 'E001',
          reimDepartmentId: 'D001',
          reimCompanyId: 'C002',
          businessTypeId: 'B0010101', // 项目出差
          subsidyTotal: 900,
          mealSubsidyTotal: 500,
          transportSubsidyTotal: 200,
          commSubsidyTotal: 200,
          remark: '本次出差完成华东区大客户交付任务，过程顺利。',
          status: 1, // 已完成
          createTime: '2026-06-20 14:32:00',
          updateTime: '2026-06-20 15:00:00',
          trips: mockTrip1,
          subsidies: mockSubsidy1,
          allocations: mockAlloc1
        },
        {
          id: 'R002',
          reimNo: 'RCBX20260625001',
          title: '张三日常办公与外部技术支持差旅（草稿）',
          reason: '华中地区日常技术支持以及与当地服务商进行会议对接。',
          reimburserId: 'E002',
          reimDepartmentId: 'D002',
          reimCompanyId: 'C003', // 武汉分公司
          businessTypeId: 'B0010102', // 日常出差
          subsidyTotal: 900,
          mealSubsidyTotal: 500,
          transportSubsidyTotal: 200,
          commSubsidyTotal: 200,
          remark: '',
          status: 0, // 草稿
          createTime: '2026-06-25 09:12:00',
          updateTime: '2026-06-25 09:12:00',
          trips: mockTrip2,
          subsidies: mockSubsidy2,
          allocations: [
            {
              id: 'A003',
              reimId: 'R002',
              companyId: 'C003',
              projectId: 'P002',
              allocRatio: 1.0,
              allocAmount: 900,
              sortOrder: 1
            }
          ]
        }
      ] as Reimbursement[],
      
      currentDetail: null as Reimbursement | null
    };
  },
  
  actions: {
    // Read list with search queries (simulated pagination & filters)
    getReimbursementList(filters: {
      reimNo?: string;
      title?: string;
      reason?: string;
      companyId?: string;
      deptId?: string;
      reimburserId?: string;
      businessTypeId?: string;
    }) {
      return this.reimbursements.filter(item => {
        if (filters.reimNo && !item.reimNo.toLowerCase().includes(filters.reimNo.toLowerCase())) return false;
        if (filters.title && !item.title.toLowerCase().includes(filters.title.toLowerCase())) return false;
        if (filters.reason && !item.reason.toLowerCase().includes(filters.reason.toLowerCase())) return false;
        if (filters.companyId && item.reimCompanyId !== filters.companyId) return false;
        if (filters.deptId && item.reimDepartmentId !== filters.deptId) return false;
        if (filters.reimburserId && item.reimburserId !== filters.reimburserId) return false;
        if (filters.businessTypeId && item.businessTypeId !== filters.businessTypeId) return false;
        return true;
      });
    },

    // Load detailed sheet
    loadDetail(id: string) {
      const item = this.reimbursements.find(r => r.id === id);
      if (item) {
        // Deep copy to prevent mutating the store list before save/submit
        this.currentDetail = JSON.parse(JSON.stringify(item));
      } else {
        this.currentDetail = null;
      }
      return this.currentDetail;
    },

    // Init new empty sheet
    initNewDetail() {
      this.currentDetail = {
        id: `NEW_${Date.now()}`,
        reimNo: '', // backend or store creates it on save/submit
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

    // Save Draft
    saveDraft(data: Reimbursement) {
      const idx = this.reimbursements.findIndex(r => r.id === data.id);
      
      const toSave = JSON.parse(JSON.stringify(data)) as Reimbursement;
      toSave.updateTime = new Date().toISOString().replace('T', ' ').substring(0, 19);
      
      if (idx !== -1) {
        // Update existing draft
        this.reimbursements[idx] = toSave;
      } else {
        // Create new draft
        toSave.reimNo = generateReimNo(this.reimbursements.length + 1);
        toSave.createTime = toSave.updateTime;
        this.reimbursements.push(toSave);
      }
    },

    // Submit Sheet (flips status to 1)
    submitSheet(data: Reimbursement) {
      const idx = this.reimbursements.findIndex(r => r.id === data.id);
      
      const toSave = JSON.parse(JSON.stringify(data)) as Reimbursement;
      toSave.status = 1; // 已完成
      toSave.updateTime = new Date().toISOString().replace('T', ' ').substring(0, 19);
      
      if (idx !== -1) {
        this.reimbursements[idx] = toSave;
      } else {
        toSave.reimNo = generateReimNo(this.reimbursements.length + 1);
        toSave.createTime = toSave.updateTime;
        this.reimbursements.push(toSave);
      }
    },

    // Delete record
    deleteReimbursement(id: string) {
      this.reimbursements = this.reimbursements.filter(r => r.id !== id);
    },

    // Void record (status = 2)
    voidReimbursement(id: string) {
      const item = this.reimbursements.find(r => r.id === id);
      if (item) {
        item.status = 2; // 已作废
        item.updateTime = new Date().toISOString().replace('T', ' ').substring(0, 19);
      }
    },

    // Copy record
    copyReimbursement(id: string) {
      const item = this.reimbursements.find(r => r.id === id);
      if (item) {
        const copy: Reimbursement = JSON.parse(JSON.stringify(item));
        copy.id = `COPY_${Date.now()}`;
        copy.reimNo = generateReimNo(this.reimbursements.length + 1);
        copy.title = `${copy.title} (复制)`;
        copy.status = 0; // copy is always draft
        copy.createTime = new Date().toISOString().replace('T', ' ').substring(0, 19);
        copy.updateTime = copy.createTime;
        
        // update IDs of linked records to avoid duplicates
        copy.trips?.forEach((t, i) => { t.id = `T_COPY_${Date.now()}_${i}`; t.reimId = copy.id; });
        copy.subsidies?.forEach((s, i) => { 
          s.id = `S_COPY_${Date.now()}_${i}`; 
          s.reimId = copy.id; 
          s.calendars?.forEach((c, j) => {
            c.id = `C_COPY_${Date.now()}_${i}_${j}`;
            c.subsidyInfoId = s.id;
          });
        });
        copy.allocations?.forEach((a, i) => { a.id = `A_COPY_${Date.now()}_${i}`; a.reimId = copy.id; });
        
        this.reimbursements.push(copy);
      }
    }
  }
});
