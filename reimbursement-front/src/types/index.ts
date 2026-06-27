export interface Company {
  id: string;
  no: string;
  name: string;
}

export interface Department {
  id: string;
  no: string;
  name: string;
}

export interface Employee {
  id: string;
  no: string;
  name: string;
}

export interface City {
  cityNo: string;
  cityName: string;
  cityType: '1' | '2' | '3'; // 1-一线 2-二线 3-三线
}

export interface Project {
  id: string;
  no: string;
  name: string;
}

export interface BusinessType {
  id: string;
  no: string;
  name: string;
  thereSubordinateNode: '0' | '1'; // 0否 1是
  superiorId: string;
  children?: BusinessType[];
}

export interface Trip {
  id: string;
  reimId: string;
  travelerId: string;
  departCityNo: string;
  arriveCityNo: string;
  departDate: string;
  arriveDate: string;
  tripDesc: string;
  createTime?: string;
}

export interface SubsidyCalendar {
  id: string;
  subsidyInfoId: string;
  subsidyDate: string;
  weekDay: string;
  cityNo: string;
  mealChecked: number; // 0否 1是
  mealStandard: number;
  mealAmount: number;
  transportChecked: number;
  transportStandard: number;
  transportAmount: number;
  commChecked: number;
  commStandard: number;
  commAmount: number;
}

export interface SubsidyInfo {
  id: string;
  reimId: string;
  tripId: string;
  travelerId: string;
  startDate: string;
  endDate: string;
  subsidyDays: number;
  subsidyCityNo: string;
  applyAmount: number;
  subsidyAmount: number;
  calendars?: SubsidyCalendar[];
  createTime?: string;
}

export interface CostAllocation {
  id: string;
  reimId: string;
  companyId: string;
  projectId: string;
  allocRatio: number; // 0-1
  allocAmount: number;
  sortOrder: number;
  createTime?: string;
}

export interface Reimbursement {
  id: string;
  reimNo: string;
  title: string;
  reason: string;
  reimburserId: string;
  reimDepartmentId: string;
  reimCompanyId: string;
  businessTypeId: string;
  subsidyTotal: number;
  mealSubsidyTotal: number;
  transportSubsidyTotal: number;
  commSubsidyTotal: number;
  remark: string;
  status: number; // 0草稿 1已完成 2已作废
  createTime?: string;
  updateTime?: string;
  trips?: Trip[];
  subsidies?: SubsidyInfo[];
  allocations?: CostAllocation[];
}
