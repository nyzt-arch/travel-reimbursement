# 当前实现 vs 对接后端后的实现对比

---

## 问题说明

> 之前分析说「补录行程、补助日历编辑、分摊计算是纯前端内存操作，不调用后端」——这个说法**不完全准确**。
>
> 当前实现确实是内存操作（存在 Pinia Store 的 JS 变量里），**页面刷新就丢**。
> 真实开发中这些数据同样需要持久化到后端数据库。

---

## 当前实现的数据流（Mock 模式）

```
所有数据 ─── 存在 Pinia Store 的内存数组 ───► 页面刷新即丢失
             (reimbursements: Reimbursement[])

用户操作行程 ──► 更新 model.value.trips (JS内存)
用户改补助日历 ──► 更新 model.value.subsidies[].calendars (JS内存)
用户改分摊 ──► 更新 model.value.allocations (JS内存)

点击「保存草稿」──► saveDraft() ──► 写入 store.reimbursements[] (JS内存)
                                    ↑ 刷新就没了

点击「提交」──► submitSheet() ──► 同上，只是 status 改为 1
```

---

## 关键文件现状

### `stores/reimbursement.ts` — 当前

```typescript
// ❌ 状态：全部是内存数据，无任何网络请求
state: () => ({
  reimbursements: [
    { id: 'R001', ... },   // 硬编码 Mock 数据
    { id: 'R002', ... },
  ] as Reimbursement[],
  currentDetail: null
})

// ❌ getReimbursementList: 本地数组 filter（没有 API 调用）
getReimbursementList(filters) {
  return this.reimbursements.filter(item => { ... });
}

// ❌ loadDetail: 从本地数组查找（没有 API 调用）
loadDetail(id: string) {
  const item = this.reimbursements.find(r => r.id === id);
  this.currentDetail = JSON.parse(JSON.stringify(item));
}

// ❌ saveDraft: 改的是内存数组（没有 API 调用）
saveDraft(data: Reimbursement) {
  const idx = this.reimbursements.findIndex(r => r.id === data.id);
  if (idx !== -1) {
    this.reimbursements[idx] = toSave;  // 写内存
  } else {
    this.reimbursements.push(toSave);   // 写内存
  }
}

// ❌ 其他方法同样只操作内存
```

### `stores/baseData.ts` — 当前

```typescript
// ❌ 全部硬编码，没有任何 API 调用
companies: [
  { id: 'C001', no: 'COMP-001', name: '北京分公司' },
  ...
],
departments: [...],
employees: [...],
// ...
```

---

## 修改后的实现

改动涉及以下文件：

```
src/
├── api/                        ← 新建目录
│   ├── request.ts              ← 新建：Axios 实例
│   ├── reimbursement.ts        ← 新建：报销单 API 函数
│   └── baseData.ts             ← 新建：基础数据 API 函数
├── stores/
│   ├── reimbursement.ts        ← 修改：替换为真实 API 调用
│   └── baseData.ts             ← 修改：替换为真实 API 调用
└── views/
    └── ReimbursementDetail.vue ← 修改：处理异步 + loading 状态
```

---

## 新增文件 1：`src/api/request.ts`

```typescript
import axios from 'axios';

const request = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
});

// 响应拦截：统一处理 Result<T> 格式
request.interceptors.response.use(
  (response) => {
    const res = response.data;
    if (res.code === 200) {
      return res.data;  // 直接返回 data 字段
    }
    return Promise.reject(new Error(res.msg || '请求失败'));
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default request;
```

---

## 新增文件 2：`src/api/reimbursement.ts`

```typescript
import request from './request';
import type { Reimbursement } from '../types';

export interface QueryParams {
  reimNo?: string;
  title?: string;
  reason?: string;
  companyId?: string;
  deptId?: string;
  reimburserId?: string;
  businessTypeId?: string;
  pageNum?: number;
  pageSize?: number;
}

export interface PageResult<T> {
  total: number;
  list: T[];
}

// 对应后端 GET /api/reimbursement/list
export function getReimbursementList(params: QueryParams) {
  return request.get<any, PageResult<Reimbursement>>('/reimbursement/list', { params });
}

// 对应后端 GET /api/reimbursement/detail/{id}
export function getReimbursementDetail(id: string) {
  return request.get<any, Reimbursement>(`/reimbursement/detail/${id}`);
}

// 对应后端 POST /api/reimbursement/save
export function saveReimbursement(data: Reimbursement) {
  return request.post<any, string>('/reimbursement/save', data);
}

// 对应后端 POST /api/reimbursement/submit
export function submitReimbursement(data: Reimbursement) {
  return request.post<any, void>('/reimbursement/submit', data);
}

// 对应后端 DELETE /api/reimbursement/delete/{id}
export function deleteReimbursement(id: string) {
  return request.delete<any, void>(`/reimbursement/delete/${id}`);
}

// 对应后端 PUT /api/reimbursement/cancel/{id}
export function cancelReimbursement(id: string) {
  return request.put<any, void>(`/reimbursement/cancel/${id}`);
}
```

---

## 新增文件 3：`src/api/baseData.ts`

```typescript
import request from './request';
import type { Company, Department, Employee, City, Project, BusinessType } from '../types';

// 对应后端 GET /api/base/companies
export function getCompanies() {
  return request.get<any, Company[]>('/base/companies');
}

// 对应后端 GET /api/base/departments
export function getDepartments() {
  return request.get<any, Department[]>('/base/departments');
}

// 对应后端 GET /api/base/employees
export function getEmployees() {
  return request.get<any, Employee[]>('/base/employees');
}

// 对应后端 GET /api/base/businessTypes
export function getBusinessTypes() {
  return request.get<any, BusinessType[]>('/base/businessTypes');
}

// 对应后端 GET /api/base/cities
export function getCities() {
  return request.get<any, City[]>('/base/cities');
}

// 对应后端 GET /api/base/projects
export function getProjects() {
  return request.get<any, Project[]>('/base/projects');
}
```

---

## 修改文件 1：`src/stores/baseData.ts`

### 当前（❌ 硬编码）

```typescript
export const useBaseDataStore = defineStore('baseData', {
  state: () => ({
    companies: [
      { id: 'C001', no: 'COMP-001', name: '北京分公司' },
      // ... 硬编码数据
    ] as Company[],
    // ...
  }),
  // 没有 actions，数据从不更新
```

### 修改后（✅ 真实 API）

```typescript
import { getCompanies, getDepartments, getEmployees,
         getCities, getProjects, getBusinessTypes } from '../api/baseData';

export const useBaseDataStore = defineStore('baseData', {
  state: () => ({
    // 初始为空，由 loadAll() 填充
    companies: [] as Company[],
    departments: [] as Department[],
    employees: [] as Employee[],
    cities: [] as City[],
    projects: [] as Project[],
    businessTypes: [] as BusinessType[],
    loaded: false,   // 防止重复加载
  }),

  actions: {
    // 在 App.vue 或路由守卫中调用一次
    async loadAll() {
      if (this.loaded) return;
      const [companies, departments, employees, cities, projects, businessTypes] =
        await Promise.all([
          getCompanies(),
          getDepartments(),
          getEmployees(),
          getCities(),
          getProjects(),
          getBusinessTypes(),
        ]);
      this.companies = companies;
      this.departments = departments;
      this.employees = employees;
      this.cities = cities;
      this.projects = projects;
      this.businessTypes = businessTypes;
      this.loaded = true;
    },
  },

  getters: {
    // getters 保持不变
    getCompanyNameById: (state) => (id: string) => { ... },
    // ...
  }
});
```

> 同时需要在 `App.vue` 中调用 `loadAll()`：
> ```typescript
> // App.vue
> import { useBaseDataStore } from './stores/baseData';
> const baseDataStore = useBaseDataStore();
> baseDataStore.loadAll();  // 应用启动时加载一次基础数据
> ```

---

## 修改文件 2：`src/stores/reimbursement.ts`

### 当前（❌ 操作内存数组）

```typescript
state: () => ({
  reimbursements: [ /* 硬编码 Mock */ ] as Reimbursement[],
  currentDetail: null as Reimbursement | null
}),

actions: {
  getReimbursementList(filters) {
    return this.reimbursements.filter(...);  // ❌ 本地 filter
  },
  loadDetail(id: string) {
    this.currentDetail = this.reimbursements.find(...); // ❌ 本地查找
  },
  saveDraft(data) {
    this.reimbursements[idx] = data;  // ❌ 写内存
  },
  deleteReimbursement(id) {
    this.reimbursements = this.reimbursements.filter(...); // ❌ 删内存
  }
}
```

### 修改后（✅ 调用真实 API）

```typescript
import { getReimbursementList, getReimbursementDetail, saveReimbursement,
         submitReimbursement, deleteReimbursement, cancelReimbursement } from '../api/reimbursement';

export const useReimbursementStore = defineStore('reimbursement', {
  state: () => ({
    // 列表页数据
    reimbursementList: [] as Reimbursement[],
    totalCount: 0,
    // 详情页数据
    currentDetail: null as Reimbursement | null,
    // 加载状态
    listLoading: false,
    detailLoading: false,
  }),

  actions: {
    // ✅ 列表查询 → 调用后端 GET /api/reimbursement/list
    async fetchList(filters: QueryParams) {
      this.listLoading = true;
      try {
        const result = await getReimbursementList(filters);
        this.reimbursementList = result.list;
        this.totalCount = result.total;
      } finally {
        this.listLoading = false;
      }
    },

    // ✅ 加载详情 → 调用后端 GET /api/reimbursement/detail/{id}
    async loadDetail(id: string) {
      this.detailLoading = true;
      try {
        const detail = await getReimbursementDetail(id);
        this.currentDetail = detail;
        return detail;
      } finally {
        this.detailLoading = false;
      }
    },

    // ✅ 初始化新建（不调用 API，创建空对象）
    initNewDetail() {
      this.currentDetail = {
        id: '',   // 空 id，后端新增时生成
        reimNo: '',
        title: '',
        // ... 其余字段保持不变
      };
      return this.currentDetail;
    },

    // ✅ 保存草稿 → 调用后端 POST /api/reimbursement/save
    // 后端返回报销单 ID（新增时生成，更新时返回原 ID）
    async saveDraft(data: Reimbursement) {
      const returnedId = await saveReimbursement(data);
      // 更新本地 id（新增时后端分配的 id）
      if (this.currentDetail) {
        this.currentDetail.id = returnedId;
      }
      return returnedId;
    },

    // ✅ 提交 → 调用后端 POST /api/reimbursement/submit
    async submitSheet(data: Reimbursement) {
      await submitReimbursement(data);
    },

    // ✅ 删除 → 调用后端 DELETE /api/reimbursement/delete/{id}
    async deleteReimbursement(id: string) {
      await deleteReimbursement(id);
      // 从本地列表中移除（避免重新拉取）
      this.reimbursementList = this.reimbursementList.filter(r => r.id !== id);
    },

    // ✅ 作废 → 调用后端 PUT /api/reimbursement/cancel/{id}
    async voidReimbursement(id: string) {
      await cancelReimbursement(id);
      // 更新本地列表的状态
      const item = this.reimbursementList.find(r => r.id === id);
      if (item) item.status = 2;
    },

    // ✅ 复制 → 先获取详情，再调用 save（新增一条草稿）
    async copyReimbursement(id: string) {
      const original = await getReimbursementDetail(id);
      const copy: Reimbursement = {
        ...original,
        id: '',                           // 清空 id，让后端重新分配
        reimNo: '',
        title: `${original.title} (复制)`,
        status: 0,
        // 子表 id 也清空，让后端重新分配
        trips: original.trips?.map(t => ({ ...t, id: '', reimId: '' })) ?? [],
        subsidies: original.subsidies?.map(s => ({
          ...s, id: '', reimId: '',
          calendars: s.calendars?.map(c => ({ ...c, id: '', subsidyInfoId: '' })) ?? []
        })) ?? [],
        allocations: original.allocations?.map(a => ({ ...a, id: '', reimId: '' })) ?? [],
      };
      await saveReimbursement(copy);
      // 刷新列表
      await this.fetchList({});
    },
  }
});
```

---

## 修改文件 3：`src/views/ReimbursementList.vue`

### 当前（❌ 同步调用，数据来自内存）

```typescript
const filteredItems = computed(() => {
  return reimbursementStore.getReimbursementList(searchFilters);  // 同步
});
```

### 修改后（✅ 异步调用，数据来自后端）

```typescript
const loading = ref(false);
const total = ref(0);

// 改为异步函数，调用后端
const fetchData = async () => {
  loading.value = true;
  await reimbursementStore.fetchList({
    ...searchFilters,
    pageNum: currentPage.value,
    pageSize: pageSize.value,
  });
  total.value = reimbursementStore.totalCount;
  loading.value = false;
};

// 页面挂载时拉取
onMounted(() => fetchData());

// 搜索触发
const handleSearch = (newFilters) => {
  Object.assign(searchFilters, newFilters);
  currentPage.value = 1;
  fetchData();  // 重新拉取
};

// 翻页触发
watch([currentPage, pageSize], () => fetchData());

// 数据来自 store，不再是 computed filter
const tableData = computed(() => reimbursementStore.reimbursementList);
```

---

## 修改文件 4：`src/views/ReimbursementDetail.vue`

### 当前（❌ 同步，无 loading）

```typescript
onMounted(() => {
  const id = route.params.id as string;
  if (!id || id === 'new') {
    model.value = reimbursementStore.initNewDetail();
  } else {
    model.value = reimbursementStore.loadDetail(id);  // 同步
  }
});

const handleSaveDraft = () => {
  reimbursementStore.saveDraft(model.value);  // 同步
  router.push('/');
};
```

### 修改后（✅ 异步 + loading 状态）

```typescript
const pageLoading = ref(false);
const saving = ref(false);
const submitting = ref(false);

onMounted(async () => {
  const id = route.params.id as string;
  if (!id || id === 'new') {
    model.value = reimbursementStore.initNewDetail();
  } else {
    pageLoading.value = true;
    try {
      model.value = await reimbursementStore.loadDetail(id);  // ✅ 异步
    } catch (e) {
      router.push('/');
    } finally {
      pageLoading.value = false;
    }
  }
});

const handleSaveDraft = async () => {
  if (!model.value?.title?.trim()) {
    firstErrorMsg.value = '保存草稿时，报销标题不能为空';
    showValidationError.value = true;
    return;
  }
  saving.value = true;
  try {
    await reimbursementStore.saveDraft(model.value);  // ✅ 异步，持久化到数据库
    router.push('/');
  } catch (e: any) {
    firstErrorMsg.value = e.message || '保存失败，请重试';
    showValidationError.value = true;
  } finally {
    saving.value = false;
  }
};

const handleSubmit = async () => {
  // 前端校验逻辑不变
  const res = validateReimbursement(model.value);
  if (!res.isValid) { /* 展示错误 */ return; }

  submitting.value = true;
  try {
    await reimbursementStore.submitSheet(model.value);  // ✅ 异步，后端再次校验
    showSubmitSuccess.value = true;
  } catch (e: any) {
    // 后端校验失败（如分摊比例不足100%）
    firstErrorMsg.value = e.message || '提交失败，请重试';
    showValidationError.value = true;
  } finally {
    submitting.value = false;
  }
};
```

---

## 关于「补录行程 / 补助日历 / 分摊」的正确理解

用户的质疑是对的。正确的实现逻辑应该是：

```
用户在弹框中填写行程 → 点「保存」
       ↓
 更新前端 model.value.trips（内存）
       ↓
 立即自动调用 POST /api/reimbursement/save
（将当前完整 model 发给后端保存）
       ↓
 后端持久化到数据库（草稿状态）
       ↓
 此时刷新页面 → 重新请求 GET /api/reimbursement/detail/{id} → 数据仍在
```

**实现方案**：在 `ReimbursementDetail.vue` 的 `handleUpdateTrips()`、`handleUpdateSubsidy()`、`handleUpdateAllocations()` 事件处理函数中，**在更新 model 后，自动触发一次 `saveDraft()`**：

### 当前（❌ 只更新内存）

```typescript
const handleUpdateTrips = (newTrips: Trip[], deletedTripId?: string | null) => {
  if (!model.value) return;
  model.value.trips = newTrips;
  // ... 联动补助计算
  recalculateTotals();
  // 结束，没有调用后端
};
```

### 修改后（✅ 更新内存后自动保存到后端）

```typescript
const handleUpdateTrips = async (newTrips: Trip[], deletedTripId?: string | null) => {
  if (!model.value) return;
  model.value.trips = newTrips;
  // ... 联动补助计算逻辑保持不变
  recalculateTotals();

  // ✅ 新增：自动调用后端保存（草稿），持久化到数据库
  // 若是新建单据（id 为空），会由后端分配 id 并返回
  try {
    const returnedId = await reimbursementStore.saveDraft(model.value);
    if (!model.value.id && returnedId) {
      model.value.id = returnedId;  // 首次保存后，更新本地 id
    }
  } catch (e) {
    console.error('自动保存失败', e);
    // 可以显示一个轻提示，不打断用户操作
  }
};

// handleUpdateSubsidy、handleUpdateAllocations 同理追加自动保存
const handleUpdateSubsidy = async (updatedSub: SubsidyInfo) => {
  if (!model.value?.subsidies) return;
  const idx = model.value.subsidies.findIndex(s => s.id === updatedSub.id);
  if (idx !== -1) {
    model.value.subsidies[idx] = updatedSub;
    recalculateTotals();
    await reimbursementStore.saveDraft(model.value);  // ✅ 自动保存
  }
};
```

---

## 修改后的完整数据流

```
用户操作行程弹框「保存」
       ↓
 更新 model.value.trips (内存)
       ↓
 联动更新 model.value.subsidies + 重算金额
       ↓
 自动调用 POST /api/reimbursement/save ──► 后端写入数据库（草稿状态）
       ↓
 刷新页面仍可恢复 ✅

用户点击「💾 保存草稿」（显式）
       ↓
 同上，POST /api/reimbursement/save ──► 后端写入数据库
       ↓
 跳转回列表页

用户点击「🚀 确认提交」
       ↓
 前端校验（validator.ts）
       ↓
 POST /api/reimbursement/submit ──► 后端再次校验 + 写入 status=1
```

---

## 文件改动汇总

| 文件 | 改动类型 | 关键变化 |
|------|---------|---------|
| `src/api/request.ts` | **新建** | Axios 实例 + 响应拦截 |
| `src/api/reimbursement.ts` | **新建** | 6 个 API 调用函数 |
| `src/api/baseData.ts` | **新建** | 6 个基础数据 API 函数 |
| `src/stores/baseData.ts` | **修改** | 硬编码 → 调用 `getCompanies()` 等 + `loadAll()` action |
| `src/stores/reimbursement.ts` | **修改** | 内存操作 → 调用 `saveReimbursement()` 等，所有 action 变为 async |
| `src/views/ReimbursementList.vue` | **修改** | `computed filter` → `async fetchList()` + `onMounted` + `watch` 翻页 |
| `src/views/ReimbursementDetail.vue` | **修改** | `loadDetail`/`saveDraft`/`submitSheet` 变为 async + 行程/补助/分摊编辑后自动保存 |
| `src/App.vue` | **修改** | 添加 `baseDataStore.loadAll()` 调用 |
