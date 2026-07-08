# 差旅费用报销系统 — WBS 工作分解结构

> 项目周期：2026-06-23 ～ 2026-07-10（计划 18 天，实际 15 天提前完成）  
> 团队人数：3 人（成员 A、成员 B、成员 C）  
> 分工特点：**全员全栈参与**，每人都有后端 + 前端的工作内容  
> 工作模式：三人全程并行，各有独立任务线，关键节点互相配合

---

## 一、项目总览

| 项目 | 内容 |
|------|------|
| 项目名称 | 差旅费用报销系统 |
| 技术栈 | Spring Boot 4.1 + MyBatis-Plus 3.5 + Vue 3.5 + TypeScript 6.0 + MySQL 8.x |
| 开发模式 | 前后端分离，Git 协作，三人全栈并行开发 |
| 计划工期 | 2026-06-23 ～ 2026-07-10（18 天） |
| 实际完成 | 2026-07-07（提前 3 天） |

---

## 二、人员分工总览（全栈交叉）

| 成员 | 后端负责 | 前端负责 | 角色定位 |
|------|---------|---------|---------|
| **成员 A**（主力） | 核心业务（ReimbursementController + DTO/VO + 业务Entity/Mapper + FkReimMainServiceImpl 540行 + 5个Service接口）+ pom.xml + 配置 | ReimbursementDetail.vue（520行）+ CostAllocationSection.vue（550行）+ DataTable.vue | 技术深度担当，负责最复杂的后端业务和最复杂的前端页面 |
| **成员 B** | 基础数据平台（BaseDataController + 6个BaseEntity + 6个BaseMapper + BaseDataServiceImpl 树形算法） | 前端架构（脚手架 + Router + Pinia + Axios + API + Types + CSS）+ 8个通用组件 | 基础设施担当，前后端的基础能力都是 B 搭建的 |
| **成员 C** | 公共模块（Result + GlobalExceptionHandler + MyBatisPlusConfig）+ 4个子表ServiceImpl（行程/补助/日历/分摊） | 工具函数（formatter/validator/subsidyCalculator）+ 列表页 + 详情页7个业务区块 + 弹窗 | 业务串联担当，前后端工具层和交互层都是 C 实现的 |

> **为什么这样分工？** 三个人的简历上都可以写"全栈开发：参与后端 Java 开发和前端 Vue/TypeScript 开发"。面试官问"你写过 Java 吗？"或"你写过 Vue 吗？"，每个人都能拿出具体文件作答。

---

## 三、WBS 工作分解（按阶段展开，每阶段三人并行）

### 阶段一：项目启动与需求对齐（2 天）

> 计划：6/23 ─ 6/24 ✅ 实际：6/23 ─ 6/24（按时）

| 编号 | 任务 | 负责人 | 交付物 | 技术栈 |
|------|------|--------|--------|--------|
| 1.1 | 三人集中讨论：梳理报销业务流程、确定技术选型 | A/B/C | 技术选型定稿 | — |
| 1.2 | 设计 11 张数据库表结构，编写建表 SQL | A | 建表脚本 | MySQL |
| 1.3 | 设计前后端接口契约（12 个 API 路径/参数/返回值） | **C 牵头**，A/B 参与 | 接口约定文档 | — |
| 1.4 | 初始化 Spring Boot 项目（pom.xml / 包结构） | A | 后端骨架 | Java |
| 1.5 | 初始化 Vue 3 + Vite + TypeScript 脚手架 + ESLint/Prettier | B | 前端骨架 | Vue/TS |
| 1.6 | 配置 Git 仓库、Vite 代理、统一 .gitignore | B | 开发环境就绪 | — |

> **并行关系**：A 做后端骨架和数据库，B 做前端脚手架，C 牵头脑暴业务流程和接口契约。三分工互不依赖。

---

### 阶段二：基础层搭建（4 天）

> 计划：6/24 ─ 6/28 ✅ 实际：6/24 ─ 6/27（提前 1 天）

| 编号 | 任务 | 负责人 | 交付物 | 技术栈 |
|------|------|--------|--------|--------|
| **2-A1** | MyBatis-Plus 分页插件配置 | A | MyBatisPlusConfig.java（实际由C写，A审） | Java |
| **2-A2** | 5 个业务 Entity（FkReimMain / Trip / Subsidy / Calendar / Allocation） | A | entity/business/*.java | Java |
| **2-A3** | 5 个业务 Mapper | A | mapper/FkReim*.java | Java |
| **2-A4** | DTO + VO 定义（ReimbursementSaveDTO / DetailVO / ListVO 含内部类） | A | dto/*.java / vo/*.java | Java |
| **2-A5** | ReimbursementController 接口框架（6 个方法签名） | A | ReimbursementController.java | Java |
| **2-B1** | 6 个基础数据 Entity（BaseCompany / BaseDepartment 等） | B | entity/base/*.java | Java |
| **2-B2** | 6 个基础数据 Mapper | B | mapper/Base*.java | Java |
| **2-B3** | BaseDataService + Impl（含树形结构 HashMap+双循环 算法） | B | BaseDataServiceImpl.java | Java |
| **2-B4** | BaseDataController（6 个 GET 接口） | B | BaseDataController.java | Java |
| **2-B5** | TypeScript 完整类型定义（7 个 interface） | B | types/index.ts | TS |
| **2-B6** | Axios 实例封装 + 请求/响应拦截器 | B | api/request.ts | TS |
| **2-C1** | Result 统一响应格式（泛型 + 静态工厂方法） | C | Result.java | Java |
| **2-C2** | GlobalExceptionHandler 全局异常处理 | C | GlobalExceptionHandler.java | Java |
| **2-C3** | 编写金额/日期/百分比格式化函数 | C | formatter.ts | TS |
| **2-C4** | 编写补助计算函数（城市等级标准 + 日期范围生成） | C | subsidyCalculator.ts | TS |
| **2-C5** | 编写前端校验函数（必填/日期重叠/分摊容差） | C | validator.ts | TS |

> **并行关系**：三人同时在写代码——A 写后端业务实体和 DTO/VO，B 写后端基础数据 + 前端类型/API 层，C 写后端公共模块 + 前端三个工具函数。每个人都在前后端之间切换，效率很高。

---

### 阶段三：核心功能开发（5 天）

> 计划：6/28 ─ 7/03 ✅ 实际：6/28 ─ 7/02（提前 1 天）

| 编号 | 任务 | 负责人 | 交付物 | 技术栈 |
|------|------|--------|--------|--------|
| **3-A1** | 列表分页查询（LambdaQueryWrapper 动态条件 + 分页插件 + Entity→VO 转换） | A | queryList() | Java |
| **3-A2** | 详情查询（Stream 三层嵌套：trips→subsidies→calendars） | A | getDetail() | Java |
| **3-A3** | 保存草稿（单号生成 RCBX+日期+序号 + 冗余字段自动补全） | A | saveDraft() | Java |
| **3-A4** | 提交报销单（BigDecimal 校验 + 状态流转 + @Transactional） | A | submitReimbursement() | Java |
| **3-A5** | 级联删除 + 作废接口 | A | delete/cancel | Java |
| **3-A6** | ReimbursementController 完善（synchronized 并发控制） | A | ReimbursementController.java | Java |
| **3-B1** | API 层封装（12 个函数 + 泛型 PageResult） | B | api/reimbursement.ts / baseData.ts | TS |
| **3-B2** | Vue Router 路由配置（3 条路由） | B | router/index.ts | TS |
| **3-B3** | Pinia baseDataStore（Promise.all / isLoaded / 字段映射 / 递归Getter） | B | stores/baseData.ts | TS |
| **3-B4** | Pinia reimbursementStore（fetchList/loadDetail/saveDraft/submit/delete/void） | B | stores/reimbursement.ts | TS |
| **3-B5** | CSS 设计系统（CSS Variables + 全局样式 + 按钮/徽章/表格/弹窗） | B | main.css | CSS |
| **3-B6** | FlatSelect + TreeSelect + Pagination + SearchBar + ConfirmDialog | B | 5 个通用组件 | Vue |
| **3-C1** | 四个子表 Service 实现（行程/补助/日历/分摊的查询+删除） | C | *ServiceImpl.java（4个） | Java |
| **3-C2** | ReimbursementList.vue（搜索栏 + 表格 + 分页 组件集成 + 操作按钮逻辑） | C | ReimbursementList.vue | Vue |
| **3-C3** | ReimbursementDetail.vue 框架（6 区块折叠 + 联动骨架 + 金额重算） | C | ReimbursementDetail.vue（框架） | Vue |

> **并行关系**：A 全力攻后端核心 Service（最复杂，540 行），B 完成前端 Store + 第一批通用组件，C 写后端子表 Service + 集成列表页。C 依赖 B 的 Store 和组件，但 B 在 6/28-6/30 交付了 Store，C 7/01 开始写列表页，无缝衔接。

---

### 阶段四：前端业务页面与组件完善（4 天）

> 计划：7/01 ─ 7/05 ✅ 实际：7/01 ─ 7/04（提前 1 天）

| 编号 | 任务 | 负责人 | 交付物 | 技术栈 |
|------|------|--------|--------|--------|
| **4-A1** | ReimbursementDetail.vue 完善（handleUpdateTrips 行程→补助联动 + handleSubmit 提交流程） | A | ReimbursementDetail.vue | Vue |
| **4-A2** | CostAllocationSection.vue（分摊表格 + rebalanceAllocations 剩余补齐 + 均摊 + ATM输入） | A | CostAllocationSection.vue | Vue |
| **4-A3** | DataTable.vue（操作列 SVG 图标 + Teleport 下拉菜单 + getBoundingClientRect 定位） | A | DataTable.vue | Vue |
| **4-A4** | 后端全接口 Postman 自测 + 边界情况验证 | A | 12 个接口自查通过 | Java |
| **4-B1** | SectionHeader 折叠区块标题 + FormField 表单字段容器 | B | common/*.vue | Vue |
| **4-B2** | App.vue 根组件（布局 + 固定定位 + CSS） | B | App.vue | Vue |
| **4-B3** | 全局样式补充 + 各组件样式统一调优 | B | CSS 体系完善 | CSS |
| **4-B4** | 基础数据接口联调（6 个下拉框数据加载验证） | B + A | 下拉数据正确 | 联调 |
| **4-C1** | BasicInfoSection（5 行表单 + 必填校验提示 + TreeSelect/FlatSelect 集成） | C | BasicInfoSection.vue | Vue |
| **4-C2** | TripSection + TripDialog（行程表格 CRUD + 编辑弹窗 + 表单校验） | C | TripSection.vue / TripDialog.vue | Vue |
| **4-C3** | SubsidyInfoSection + SubsidyCalendarDialog（补助列表 + 日历矩阵 + 全选/列选/行选） | C | SubsidyInfoSection.vue / SubsidyCalendarDialog.vue | Vue |
| **4-C4** | CostSummarySection + RemarkSection + ActionBar | C | 3 个组件 | Vue |

> **并行关系**：A 写完 3 个最复杂的前端页面，B 完成剩余组件和样式调优，C 集中写 7 个业务区块。三人都在写 Vue 组件，可以随时交流样式和交互规范。B 这边 7/02 开始联调基础数据接口，与此同时 C 还在写组件，互不干扰。

---

### 阶段五：前后端联调与集成测试（3 天）

> 计划：7/04 ─ 7/07 ✅ 实际：7/04 ─ 7/06（提前 1 天）

| 编号 | 任务 | 负责人 | 交付物 |
|------|------|--------|--------|
| 5.1 | 基础数据接口联调（6 个下拉框从数据库正确加载） | B + A | 下拉数据正确 |
| 5.2 | 列表分页查询联调（搜索/翻页/跳转全链路） | C + A | 列表功能通过 |
| 5.3 | 新建 → 保存草稿 → 编辑 → 再保存 全流程 | C + A | 草稿保存链路通过 |
| 5.4 | 提交报销单全链路（前端校验 → 后端校验 → 状态变更） | C + A | 双重校验通过 |
| 5.5 | 复制/删除/作废 操作联调 | C + A | 操作链路通过 |
| 5.6 | 行程→补助联动（新增行程自动生成补助日历） | A + C | 联动计算正确 |
| 5.7 | 补助日历编辑（全选/列选/行选/金额修改/保存） | C + A | 日历数据正确 |
| 5.8 | 费用分摊（比例联动金额 / 金额联动比例 / 均摊 / 精度验证） | A + C | 分摊精度通过 |
| 5.9 | 边界测试（空数据/超长文本/非法输入/重复提交/日期越界） | A/B/C | 边界测试通过 |

> **联调策略**：B 负责基础数据链路，C 负责全部业务操作链路，A 负责复杂联动链路。遇到任何接口问题 A 当场修复。

---

### 阶段六：代码审查与文档编写（2 天）

> 计划：7/06 ─ 7/08 ✅ 实际：7/06 ─ 7/07（提前 1 天）

| 编号 | 任务 | 负责人 |
|------|------|--------|
| 6.1 | 后端代码审查：去掉英文注释、统一中文注释、清理冗余 | A（审自己 + C 的后端代码） |
| 6.2 | 前端代码审查：去掉英文注释、消除过度分步注释、简化写法 | C（审自己 + A/B 的前端代码） |
| 6.3 | B 的代码自查 + 前后端注释风格统一 | B |
| 6.4 | 项目技术点全面梳理文档（10 章，含面试题） | **A 主笔**，B/C 补充 |
| 6.5 | 三人分工汇报稿（含过渡语 + 时间控制 + 问答准备） | **B 主笔**，A/C 补充 |
| 6.6 | 代码位置对照表 + 全员文件归属对照表 | **C 主笔**，A/B 补充 |
| 6.7 | WBS 工作分解结构 | A |

> **分工策略**：A 审后端代码 + 写技术文档，B 审自己代码 + 写分工文档，C 审前端代码 + 写对照表。每人主笔一份文档，效率最高。

---

### 阶段七：汇报演练与正式汇报（2 天）

> 计划：7/08 ─ 7/10 ✅ 实际：7/08 ─ 7/09（提前 1 天）

| 编号 | 任务 | 负责人 |
|------|------|--------|
| 7.1 | 各成员按汇报稿准备个人讲稿（标注每段话对应的代码文件） | A/B/C |
| 7.2 | 组内第一次排练（串流程 + 计时） | A/B/C |
| 7.3 | 根据排练调整讲稿 + 互补内容避免重复 | A/B/C |
| 7.4 | 组内第二次排练（模拟评委提问，按代码位置对照表快速定位） | A/B/C |
| 7.5 | 环境检查（IDE 打开关键文件 / 项目可正常运行演示） | A |
| 7.6 | **正式汇报** | A/B/C 🎯 |

---

## 四、里程碑节点

| 里程碑 | 计划 | 实际 | 状态 | 关键标志 |
|--------|------|------|------|---------|
| M1：项目启动 | 6/24 | 6/24 | ✅ 按时 | 前后端骨架可运行，数据库建表完成，接口契约定稿 |
| M2：基础层打通 | 6/28 | 6/27 | ✅ 提前1天 | 后端：基础数据接口可用 + 公共模块就绪；前端：类型/API/工具函数/Store 就绪 |
| M3：后端全部接口完成 | 7/03 | 7/02 | ✅ 提前1天 | 12 个 API 全部实现，Postman 自测通过 |
| M4：前端组件全部完成 | 7/05 | 7/04 | ✅ 提前1天 | 8 个通用组件 + 10 个业务组件全部可渲染 |
| M5：联调通过 | 7/07 | 7/06 | ✅ 提前1天 | 全业务流程可跑通，边界测试通过 |
| M6：文档交付 | 7/08 | 7/07 | ✅ 提前1天 | 5 份文档全部完成 |
| M7：汇报就绪 | 7/10 | 7/09 | ✅ 提前1天 | 两次排练完成，讲稿定稿 |

---

## 五、工作量统计

### 按成员统计

| 成员 | 后端文件 | 前端文件 | 总计 | 最复杂产出 |
|------|---------|---------|------|-----------|
| **A**（主力） | 24 个 | 3 个 | 27 个 | FkReimMainServiceImpl（540行）+ ReimbursementDetail.vue（520行）+ CostAllocationSection.vue（550行） |
| **B** | 15 个 | 23 个 | 38 个 | BaseDataServiceImpl（树形构建）+ stores/baseData.ts（Promise.all/字段映射/递归Getter） |
| **C** | 7 个 | 14 个 | 21 个 | validator.ts（三重校验逻辑）+ SubsidyCalendarDialog.vue（日历矩阵） |

> A 文件数量不是最多，但每个都是大头——一个 FkReimMainServiceImpl 就 540 行。B 文件最多但有不少是模板代码（Entity/Mapper）。C 的前端业务组件逻辑密度高。

### 技术栈覆盖（每人都有）

| 成员 | Java | Spring Boot | MyBatis-Plus | Vue 3 | TypeScript | CSS |
|------|------|------------|-------------|-------|-----------|-----|
| **A** | ✅✅✅ | ✅✅✅ | ✅✅✅ | ✅✅ | ✅ | ✅ |
| **B** | ✅✅ | ✅✅ | ✅✅ | ✅✅✅ | ✅✅✅ | ✅✅✅ |
| **C** | ✅✅ | ✅✅ | ✅✅ | ✅✅✅ | ✅✅✅ | ✅✅ |

---

## 六、三人协作时序图

```
日期      6/23  6/25  6/27  6/29  7/01  7/03  7/05  7/07  7/09
         ├─────┼─────┼─────┼─────┼─────┼─────┼─────┼─────┼─────┤
成员 A    ████████████████████████████████████████░░░░░░░░░░░░░░░
         启动 业务Entity 核心Service实现(540行)    前端3页面 联调 文档 演练
              DTO/VO    LambdaQW/单号/事务       Detail/Alloc/Table

成员 B    ████████████████████████████████████████░░░░░░░░░░░░░░░
         脚手架 BaseEntity API/Store 通用组件   样式完善 联调 文档 演练
                BaseData   Router   Flat/Tree/Pagination

成员 C    ████████████████████████████████████████░░░░░░░░░░░░░░░
         接口契约 Result    子表Service 7个业务区块  联调   文档 演练
         需求梳理 工具函数  列表页      日历/分摊/行程
```

> █ = 高强度开发期，░ = 收尾/文档/演练期。**每个人都在后端和前端之间来回切换**——比如 A 在 6/28-7/02 主要写 Java，7/01-7/04 主要写 Vue；C 在 6/24-6/28 同时写 Java（Result/Exception）和 TypeScript（工具函数）。

---

## 七、风险与应对

| 风险 | 等级 | 应对 | 实际 |
|------|------|------|------|
| B 的 Store/组件交付延期影响 A 和 C 开发页面 | 中 | B 优先交付 Pinia Store（6/30前）+ 核心组件（7/02前）；A 和 C 先写不依赖组件的逻辑 | B 按时交付，A/C 无缝衔接 |
| 前后端接口字段名不一致 | 中 | C 在阶段一就写出接口契约文档，A 严格按约定实现 | 仅 baseDataStore 一处做了字段映射 |
| 三人前端代码风格不统一 | 低 | B 先定 CSS 变量体系和组件模板，A 和 C 遵循统一规范 | 代码审查时统一修正 |
| 补助联动计算三方理解偏差 | 中 | C 先写 subsidyCalculator.ts 作为"标准答案"，A 参照实现后端 | 联调一次通过 |
| 分摊浮点精度前后端不一致 | 低 | 前端用容差 0.0001，后端用 BigDecimal.compareTo() | 验证通过 |

---

> **总结**：本项目最大的特色是**三人全栈参与**——每个人都写了 Java 后端代码，也写了 Vue/TypeScript 前端代码。A 攻最深的技术点（核心业务逻辑 + 最复杂页面），B 搭最广的基础能力（前后端基础设施），C 做最密的业务串联（工具层 + 交互层）。三条线全程并行，最终提前 3 天完成全部开发和文档。
