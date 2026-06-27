# reimbursement-front

## 一、项目说明

本项目为**前后端分离的纯前端部分**，无登录模块。用户通过下拉框从数据库中选择目标用户，为其新增报销记录。数据持久化存储于后端数据库，刷新页面或重新启动开发服务器后数据不会丢失。

## 二、项目根目录结构

```
reimbursement-front/
├── index.html          # HTML 入口模板
├── package.json        # 依赖管理与脚本命令
├── tsconfig.json       # TypeScript 编译配置
├── vite.config.ts      # Vite 构建配置（代理、路径别名、打包优化）
└── README.md           # 项目说明文档
```

## 三、src 核心目录详细结构

### 1. src/api 接口请求层

按业务模块拆分接口文件，统一管理所有后端请求，与后端接口定义一一对应。

```
src/api/
├── request.ts      # axios 统一封装（请求/响应拦截、错误处理、超时）
├── reimburse.ts   # 报销单业务接口（列表、详情、保存、提交、删除、行程管理）
└── baseData.ts    # 基础数据接口（公司、部门、员工、城市、业务类型、项目）
```

### 2. src/views 路由级页面（业务核心）

按业务模块组织，每个模块独立目录，页面级组件 + 模块内私有组件内聚，对应需求中的所有功能页面。

```
src/views/
├── layout/                         # 全局布局组件（页面框架、内容区）
│   └── MainLayout.vue
└── reimburse/                     # 差旅报销单业务模块
    ├── ReimburseList.vue          # 报销单列表页（对应需求：报销单列表管理）
    ├── ReimburseDetail.vue        # 报销单详情/编辑页（对应需求：单据全生命周期管理）
    └── components/                # 报销模块私有组件（仅本模块使用，不对外复用）
        ├── SearchForm.vue         # 列表页搜索表单（7类查询条件）
        ├── BillTable.vue          # 列表页表格组件（字段展示、操作列）
        ├── BillHeader.vue         # 单据页头部（固定标题、提单日期）
        ├── BaseInfoSection.vue    # 基础信息分区（展开收起、字段联动、字数校验）
        ├── TripSection.vue        # 补录行程分区（行程列表、增删改复制）
        ├── TripEditModal.vue      # 补录行程编辑弹窗（新增/编辑/复制、日期校验）
        ├── SubsidySection.vue     # 补助信息分区（补助列表、规则提示）
        ├── SubsidyCalendarModal.vue  # 补助日历弹窗（按天勾选、三类补助、金额计算）
        ├── FeeTotalSection.vue    # 费用合计分区（四类金额自动汇总）
        ├── FeeShareSection.vue    # 费用分摊分区（均摊、比例联动、校验）
        ├── RemarkSection.vue      # 备注信息分区（字数限制、删除确认）
        └── BillFooter.vue         # 单据底部按钮栏（固定悬浮、关闭/提交）
```

### 3. src/components 全局公共组件

存放全项目通用、无强业务属性的组件，多页面复用。

```
src/components/
├── common/                    # 基础通用组件
│   ├── PageHeader.vue        # 页面通用头部
│   ├── StatusTag.vue         # 状态标签（单据状态色值映射）
│   ├── DictSelect.vue        # 字典下拉选择组件（封装基础数据加载）
│   ├── TreeDictSelect.vue    # 树形字典选择（业务类型树）
│   ├── ConfirmModal.vue      # 通用确认弹窗（删除、关闭等二次确认）
│   └── Pagination.vue        # 通用分页组件
└── business/                 # 跨模块业务通用组件（预留扩展）
```

### 4. src/router 路由配置

按模块拆分路由，避免单文件膨胀，便于后续扩展。

```
src/router/
├── index.ts              # 路由入口
└── modules/
    └── reimburse.ts     # 报销模块路由（列表页、详情页路由注册）
```

### 5. src/stores Pinia 状态管理

按功能模块拆分 store，仅存放全局共享数据，页面内临时状态由组件自行维护。

```
src/stores/
├── index.ts              # Pinia 实例入口
└── modules/
    ├── user.ts          # 当前选中用户（用于创建报销记录时关联用户）
    └── baseData.ts      # 基础数据缓存（公司、部门、城市等字典数据）
```

> **说明**：基础字典数据全局缓存，避免每个页面重复请求接口，提升页面加载速度。

### 6. src/types TypeScript 类型定义

统一管理所有类型，按业务模块拆分，实现全链路类型约束。

```
src/types/
├── common.type.ts        # 通用类型（分页参数、接口通用返回、选项结构）
├── api.type.ts          # 接口请求/响应通用类型
├── reimburse.type.ts    # 报销单业务类型（单据、行程、补助日历、分摊项）
└── baseData.type.ts     # 基础数据类型（公司、部门、员工、城市等）
```

> **示例**：`reimburse.type.ts` 中定义 `BillListItem`、`TripItem`、`SubsidyCalendarItem`、`FeeShareItem` 等接口，与数据库表结构、接口字段一一对应。

### 7. src/hooks 组合式函数

抽离可复用的业务逻辑，以 `use` 开头命名，减少重复代码。

```
src/hooks/
├── usePageList.ts       # 列表分页查询通用逻辑（加载、分页、搜索、重置）
├── useDict.ts          # 字典数据获取与缓存逻辑
├── useFormValidate.ts  # 表单通用校验逻辑
└── useAmount.ts        # 金额计算、格式化相关逻辑
```

### 8. src/utils 工具函数

```
src/utils/
├── format.ts           # 格式化工具（日期、金额千分位、文本省略）
├── validate.ts         # 校验规则（日期范围合法性、金额范围、比例校验）
├── storage.ts          # 本地存储封装（localStorage/sessionStorage）
└── business.ts         # 业务工具（单据号生成规则等）
```

### 9. src/styles 全局样式

```
src/styles/
├── index.scss          # 全局样式入口
├── reset.scss          # 浏览器默认样式重置
├── variables.scss      # 全局 SCSS 变量（主题色、字号、间距、圆角）
└── common.scss         # 通用工具类（flex 布局、文本省略、对齐方式）
```

### 10. 其他目录

```
src/assets/             # 静态资源（会被 webpack/vite 打包）
├── images/             # 图片资源
└── icons/              # 图标文件

src/directives/         # 全局自定义指令
├── permission.ts       # 按钮级权限指令
└── inputLimit.ts       # 输入限制（仅数字、金额精度限制）

src/App.vue             # 应用根组件
src/main.ts             # 项目入口文件（注册插件、挂载应用）
```
