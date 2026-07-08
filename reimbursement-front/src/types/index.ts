export interface Company {
  id: string
  no: string
  name: string
}

export interface Department {
  id: string
  no: string
  name: string
}

export interface Employee {
  id: string
  no: string
  name: string
}

export interface City {
  cityNo: string
  cityName: string
  cityType: '1' | '2' | '3'  // 城市级别：1=一线城市, 2=二线城市, 3=三线城市
}

export interface Project {
  id: string
  no: string
  name: string
}

export interface BusinessType {
  id: string
  no: string
  name: string
  thereSubordinateNode: '0' | '1'
  superiorId: string
  children?: BusinessType[]
}

export interface Trip {
  id: string
  reimId: string // 关联的报销单 ID (对应 Reimbursement.id)，用于确定该行程属于哪张报销单
  travelerId: string
  departCityNo: string
  arriveCityNo: string
  departDate: string
  arriveDate: string
  tripDesc: string
  createTime?: string
}

export interface SubsidyCalendar {
  id: string
  subsidyInfoId: string // 关联的补助汇总信息 ID (对应 SubsidyInfo.id)
  subsidyDate: string
  weekDay: string
  cityNo: string
  mealChecked: number
  mealStandard: number
  mealAmount: number
  transportChecked: number
  transportStandard: number
  transportAmount: number
  commChecked: number
  commStandard: number
  commAmount: number
}

export interface SubsidyInfo {
  id: string
  reimId: string // 关联的报销单 ID (对应 Reimbursement.id)
  tripId: string
  travelerId: string
  startDate: string
  endDate: string
  subsidyDays: number
  subsidyCityNo: string
  applyAmount: number
  subsidyAmount: number
  calendars?: SubsidyCalendar[]
  createTime?: string
}

export interface CostAllocation {
  id: string
  reimId: string // 关联的报销单 ID (对应 Reimbursement.id)
  companyId: string
  projectId: string
  allocRatio: number
  allocAmount: number
  sortOrder: number
  createTime?: string
}

export interface Reimbursement {
  id: string
  reimNo: string
  title: string
  reason: string
  reimburserId: string
  reimburserNo?: string
  reimburserName?: string
  reimDepartmentId: string
  reimDepartmentNo?: string
  reimDepartmentName?: string
  reimCompanyId: string
  reimCompanyNo?: string
  reimCompanyName?: string
  businessTypeId: string
  businessTypeNo?: string
  businessTypeName?: string
  subsidyTotal: number
  mealSubsidyTotal: number
  transportSubsidyTotal: number
  commSubsidyTotal: number
  remark: string
  status: number
  createTime?: string
  updateTime?: string
  trips?: Trip[] // 报销单包含的行程明细列表
  subsidies?: SubsidyInfo[] // 报销单包含的补助明细列表
  allocations?: CostAllocation[]  // 报销单包含的费用分摊列表
}
