export interface Company {
  id: string; // 公司唯一标识 ID (主键)
  no: string; // 公司编号 (例如: "072001")
  name: string; // 公司名称 (对应 UI 的 "费用归属公司", 例如: "胜意科技杭州分公司")
}

export interface Department {
  id: string; // 部门唯一标识 ID (主键)
  no: string; // 部门编号 (例如: "072001")
  name: string; // 部门名称 (对应 UI 的 "报销部门")
}

export interface Employee {
  id: string; // 员工唯一标识 ID (主键，用于 travelerId / reimburserId 等关联字段)
  no: string; // 员工工号 (例如: "74541")
  name: string; // 员工姓名 (对应 UI 的 "报销人" / "出行人员" / "出行人")
}

export interface City {
  cityNo: string; // 城市编号 (唯一标识，例如: "BJ")
  cityName: string; // 城市名称 (对应 UI 的 "出发城市" / "到达城市" / "补助城市")
  cityType: '1' | '2' | '3'; // 城市类别: 1-一线城市 2-二线城市 3-三线城市 (用于匹配不同的补助标准)
}

export interface Project {
  id: string; // 项目唯一标识 ID (主键)
  no: string; // 项目编号
  name: string; // 项目名称
}

export interface BusinessType {
  id: string; // 业务类型唯一标识 ID (主键)
  no: string; // 业务类型编码
  name: string; // 业务类型名称 (对应 UI 的 "业务类型")
  thereSubordinateNode: '0' | '1'; // 是否有下级节点: '0' 否, '1' 是
  superiorId: string; // 上级业务类型 ID (用于树形结构交互与级联选择)
  children?: BusinessType[]; // 子级业务类型列表
}

export interface Trip {
  id: string; // 行程记录的唯一标识 ID (主键)
  reimId: string; // 关联的报销单 ID (对应 Reimbursement.id)，用于确定该行程属于哪张报销单
  travelerId: string; // 出行人员 ID (对应 Employee.id)，用于关联具体出差的员工
  departCityNo: string; // 出发城市编号 (对应 City.cityNo)
  arriveCityNo: string; // 到达城市编号 (对应 City.cityNo)
  departDate: string; // 出发日期 (YYYY-MM-DD，对应 UI "出发日期")
  arriveDate: string; // 到达日期 (YYYY-MM-DD，对应 UI "到达日期")
  tripDesc: string; // 行程说明 (对应 UI "行程说明")
  createTime?: string; // 创建时间
}

export interface SubsidyCalendar {
  id: string; // 每日补助明细的唯一标识 ID (主键)
  subsidyInfoId: string; // 关联的补助汇总信息 ID (对应 SubsidyInfo.id)
  subsidyDate: string; // 补助日期 (YYYY-MM-DD)
  weekDay: string; // 星期几 (星期一到星期日)
  cityNo: string; // 补助城市编号 (对应 City.cityNo)
  mealChecked: number; // 伙食补助是否勾选: 0-否, 1-是 (对应 UI 补助日历的勾选状态)
  mealStandard: number; // 伙食补助标准金额/天
  mealAmount: number; // 伙食补助实际申请金额
  transportChecked: number; // 交通补助是否勾选: 0-否, 1-是
  transportStandard: number; // 交通补助标准金额/天
  transportAmount: number; // 交通补助实际申请金额
  commChecked: number; // 通讯补助是否勾选: 0-否, 1-是
  commStandard: number; // 通讯补助标准金额/天
  commAmount: number; // 通讯补助实际申请金额
}

export interface SubsidyInfo {
  id: string; // 补助汇总信息的唯一标识 ID (主键)
  reimId: string; // 关联的报销单 ID (对应 Reimbursement.id)
  tripId: string; // 关联的行程 ID (对应 Trip.id)，由此行程自动计算生成的补助汇总
  travelerId: string; // 出行人员 ID (对应 Employee.id)
  startDate: string; // 补助开始日期 (对应 Trip.departDate)
  endDate: string; // 补助结束日期 (对应 Trip.arriveDate)
  subsidyDays: number; // 补助天数 (对应 UI "补助天数")
  subsidyCityNo: string; // 补助城市编号 (对应 City.cityNo)
  applyAmount: number; // 申请总金额 (对应 UI "申请金额")
  subsidyAmount: number; // 补助计算总金额 (对应 UI "补助金额")
  calendars?: SubsidyCalendar[]; // 对应的每日补助日历明细列表
  createTime?: string; // 创建时间
}

export interface CostAllocation {
  id: string; // 费用分摊记录的唯一标识 ID (主键)
  reimId: string; // 关联的报销单 ID (对应 Reimbursement.id)
  companyId: string; // 分摊目标公司 ID (对应 Company.id)
  projectId: string; // 分摊目标项目 ID (对应 Project.id)
  allocRatio: number; // 分摊比例 (范围 0-1)
  allocAmount: number; // 分摊金额
  sortOrder: number; // 排序号
  createTime?: string; // 创建时间
}

export interface Reimbursement {
  id: string; // 报销单的唯一标识 ID (主键，用于在 Trip / SubsidyInfo / CostAllocation 中作为 reimId 关联)
  reimNo: string; // 报销单号 (自动生成的单号，例如: "RCBX20260701007")
  title: string; // 报销标题 (对应 UI "报销标题")
  reason: string; // 出差事由 (对应 UI "出差事由")
  reimburserId: string; // 报销人 ID (对应 Employee.id)
  reimDepartmentId: string; // 报销部门 ID (对应 Department.id)
  reimCompanyId: string; // 费用归属公司 ID (对应 Company.id)
  businessTypeId: string; // 业务类型 ID (对应 BusinessType.id)
  subsidyTotal: number; // 补助总金额 (所有明细补助总和，对应 UI 的 "补助金额" 列)
  mealSubsidyTotal: number; // 伙食补助总计
  transportSubsidyTotal: number; // 交通补助总计
  commSubsidyTotal: number; // 通讯补助总计
  remark: string; // 备注
  status: number; // 报销单状态: 0-草稿, 1-已完成, 2-已作废
  createTime?: string; // 创建时间
  updateTime?: string; // 更新时间
  trips?: Trip[]; // 报销单包含的行程明细列表
  subsidies?: SubsidyInfo[]; // 报销单包含的补助明细列表
  allocations?: CostAllocation[]; // 报销单包含的费用分摊列表
}
