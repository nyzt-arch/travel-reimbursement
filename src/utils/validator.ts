import type { Reimbursement, Trip } from '../types';

export interface ValidationErrors {
  title?: string;
  reason?: string;
  reimburserId?: string;
  reimDepartmentId?: string;
  reimCompanyId?: string;
  businessTypeId?: string;
  trips?: string;
  allocations?: string;
}

// Check if two date ranges overlap
export function areDatesOverlapping(start1: string, end1: string, start2: string, end2: string): boolean {
  const s1 = new Date(start1).getTime();
  const e1 = new Date(end1).getTime();
  const s2 = new Date(start2).getTime();
  const e2 = new Date(end2).getTime();
  
  return s1 <= e2 && e1 >= s2;
}

// Full sheet validation before submit
export function validateReimbursement(data: Reimbursement): { isValid: boolean; errors: ValidationErrors } {
  const errors: ValidationErrors = {};
  let isValid = true;

  // 1. Basic Info
  if (!data.title?.trim()) {
    errors.title = '报销标题不能为空';
    isValid = false;
  }
  if (!data.reason?.trim()) {
    errors.reason = '出差事由不能为空';
    isValid = false;
  }
  if (!data.reimburserId) {
    errors.reimburserId = '请选择报销人';
    isValid = false;
  }
  if (!data.reimDepartmentId) {
    errors.reimDepartmentId = '请选择报销部门';
    isValid = false;
  }
  if (!data.reimCompanyId) {
    errors.reimCompanyId = '请选择费用归属公司';
    isValid = false;
  }
  if (!data.businessTypeId) {
    errors.businessTypeId = '请选择业务类型';
    isValid = false;
  }

  // 2. Trip validation
  const trips = data.trips || [];
  if (trips.length === 0) {
    errors.trips = '请至少录入一条行程信息';
    isValid = false;
  } else {
    // Check required fields inside each trip
    for (const t of trips) {
      if (!t.travelerId || !t.departCityNo || !t.arriveCityNo || !t.departDate || !t.arriveDate || !t.tripDesc?.trim()) {
        errors.trips = '所有已录入行程的必填项均不可为空';
        isValid = false;
        break;
      }
    }
    
    // Check overlapping dates for the same traveler
    if (isValid) {
      const travelerGroups: { [key: string]: Trip[] } = {};
      trips.forEach(t => {
        if (!travelerGroups[t.travelerId]) {
          travelerGroups[t.travelerId] = [];
        }
        const group = travelerGroups[t.travelerId];
        if (group) {
          group.push(t);
        }
      });
      
      for (const travelerId in travelerGroups) {
        const tList = travelerGroups[travelerId];
        if (!tList) continue;
        for (let i = 0; i < tList.length; i++) {
          const tripI = tList[i];
          if (!tripI) continue;
          for (let j = i + 1; j < tList.length; j++) {
            const tripJ = tList[j];
            if (!tripJ) continue;
            if (areDatesOverlapping(tripI.departDate, tripI.arriveDate, tripJ.departDate, tripJ.arriveDate)) {
              errors.trips = '同一出行人员的多条行程日期范围不可有任何重叠';
              isValid = false;
              break;
            }
          }
          if (!isValid) break;
        }
        if (!isValid) break;
      }
    }
  }

  // 3. Allocations validation
  const allocs = data.allocations || [];
  if (allocs.length === 0) {
    errors.allocations = '请至少保留一条费用归属分摊数据';
    isValid = false;
  } else {
    // Check company and ratio fields
    let ratioSum = 0;
    let amountSum = 0;
    let hasEmptyCompany = false;
    
    for (const a of allocs) {
      if (!a.companyId) {
        hasEmptyCompany = true;
      }
      ratioSum += a.allocRatio;
      amountSum += a.allocAmount;
    }
    
    if (hasEmptyCompany) {
      errors.allocations = '分摊行中的费用归属公司不能为空';
      isValid = false;
    } else {
      // Check ratio sum = 100% (with small floating precision allowance, e.g. 0.99999 to 1.00001)
      if (Math.abs(ratioSum - 1.0) > 0.0001) {
        errors.allocations = `分摊比例合计必须等于 100% (当前为 ${(ratioSum * 100).toFixed(2)}%)`;
        isValid = false;
      } else if (Math.abs(amountSum - data.subsidyTotal) > 0.05) {
        errors.allocations = `分摊金额合计必须等于补助总金额 (当前差值: CNY ${(amountSum - data.subsidyTotal).toFixed(2)})`;
        isValid = false;
      }
    }
  }

  return { isValid, errors };
}
