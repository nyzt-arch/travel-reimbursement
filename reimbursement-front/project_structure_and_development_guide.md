# 差旅费用报销系统前端项目结构与前后端联调指南

本文档包含两部分：
1. **项目文件与文件夹说明**：详细介绍 `reimbursement-front` 根目录下及 `src` 文件夹中所有文件/文件夹的作用与职责。
2. **前后端及数据库联调开发指南**：详细说明在接入真实后端与数据库时，前端需要修改哪些文件，以及如何添加与设计数据库交互的接口。

---

## 第一部分：前端项目文件与文件夹说明

### 1.1 根目录配置文件说明

在项目根目录下，包含了一系列工程化和编译配置文件，各文件的主要作用如下：

| 文件 / 文件夹 | 作用与职责说明 |
| :--- | :--- |
| **`.vscode/`** | 存放 Visual Studio Code 的工作区配置，如推荐的插件列表（Volar、TypeScript等）及编辑器专有行为设置。 |
| **`dist/`** | 执行打包命令（`npm run build`）后生成的生产环境产物文件夹，包含压缩混淆后的 HTML、JS、CSS 静态文件，可直接部署到服务器。 |
| **`node_modules/`** | 存放通过 npm 安装的所有依赖包和第三方库。 |
| **`public/`** | 存放纯静态资源，此目录中的资源在构建时会被原封不动地复制到 `dist` 根目录下（如浏览器标签页的图标 `favicon.ico`）。 |
| **`src/`** | 前端核心源代码开发目录，所有 Vue 页面、组件、状态管理等都存放于此。 |
| **`.editorconfig`** | 跨编辑器/IDE 的代码格式控制配置文件，用于统一不同开发者的缩进风格（如使用 2 空格缩进）、换行符和文件末尾换行等。 |
| **`.eslintcache`** | ESLint 运行时的缓存文件，用于记录已通过校验的文件，加快后续执行 `npm run lint` 时的校验速度（已被加入 `.gitignore`，无需提交）。 |
| **`.gitattributes`** | Git 属性配置文件，常用于强制指定特定文件的换行符（如强制使用 LF 换行符以防止 Windows/Linux 环境冲突）。|
| **`.gitignore`** | 规定哪些文件和文件夹不需要提交到 Git 仓库（如 `node_modules`、`dist`、本地环境变量文件 `.env.local` 等）。 |
| **`.oxlintrc.json`** | 极速代码校验工具 `Oxlint`（基于 Rust 编写）的配置文件，配置了相应的语法校验规则，提供比传统 ESLint 更快的初步诊断。 |
| **`.prettierrc.json`** | 代码格式化工具 Prettier 的配置文件，规定格式化细节，如是否使用单引号、末尾是否加分号、单行最大字符数等。 |
| **`env.d.ts`** | 全局类型声明文件，为 Vite 提供的一些环境变量（如 `import.meta.env.BASE_URL`）提供 TypeScript 类型提示。 |
| **`eslint.config.ts`** | 最新版 ESLint 的扁平配置文件（Flat Config），用于规范 JavaScript/TypeScript 代码风格，检测潜在的语法错误和不规范写法。 |
| **`index.html`** | 单页面应用（SPA）的入口 HTML 模板，Vite 在编译时会将打包后的 JS 文件自动注入到此文件，并通过 `<div id="app"></div>` 挂载 Vue 实例。 |
| **`package-lock.json`** | 记录当前安装的每一个依赖包的绝对精确版本号以及依赖树关系锁，确保团队中所有人安装的第三方库完全一致。 |
| **`package.json`** | 前端项目的元数据和配置文件，定义了项目名称、版本号、运行脚本命令（如 `dev`、`build`、`type-check`、`lint`）以及项目的开发和运行依赖。 |
| **`tsconfig.app.json`** | 专门面向 Vue 应用程序代码（`src` 目录下的源文件）的 TypeScript 编译器配置文件，配置了路径别名（如 `@/*`）和严格类型检查。 |
| **`tsconfig.json`** | 根 TypeScript 配置文件，采用 `references` 机制将配置拆分为对 `tsconfig.app.json` 和 `tsconfig.node.json` 的引用。 |
| **`tsconfig.node.json`** | 专门面向构建工具脚本（如 `vite.config.ts` 等 Node.js 运行环境文件）的 TypeScript 编译器配置文件。 |
| **`vite.config.ts`** | 构建工具 Vite 的配置文件，可在其中配置插件（Vue、JSX支持）、路径解析别名（`@` 指向 `src`）、本地代理跨域等。 |

---

### 1.2 `src/` 内部目录及核心代码说明

`src/` 是前端业务的主要编写地，包含以下模块：

* **`main.ts`**：前端应用的初始化入口文件。实例化 Vue App，挂载 Pinia 状态管理库和路由（Vue Router），导入全局样式，并将其挂载到 HTML 的 `#app` 节点。
* **`App.vue`**：根组件，定义了系统的全局布局骨架，包括顶部吸顶的“企业差旅费用报销系统”导航栏以及渲染各页面的 `<router-view />`。
* **`assets/`**：存放静态资源。目前包含 `assets/styles/main.css`，定义了系统的全局 CSS 变量（色彩体系、间距、圆角等）和主设计样式。
* **`types/`**：包含 TypeScript 的接口定义（`types/index.ts`），规范了公司、部门、员工、城市、项目、行程、补助、费用分摊和报销单等实体的数据结构。
* **`utils/`**：通用辅助工具函数目录：
  * `formatter.ts`：货币格式化（加千分位并保留2位小数）、日期截取、百分比显示工具。
  * `subsidyCalculator.ts`：处理具体的差旅补助计算逻辑，包括一/二/三线城市标准匹配，根据出发/到达日期计算天数以及逐日明细计算。
  * `validator.ts`：单据提交前的校验类，确保基础信息完整、出行人日期区间不重叠、费用分摊比例合计必须刚好等于 100%、分摊金额与补助总额一致。
* **`router/`**：路由配置文件（`router/index.ts`），定义页面路径跳转规则：
  * `/` 映射列表页面 `ReimbursementList.vue`
  * `/detail/new` 映射新建报销单页面
  * `/detail/:id` 映射编辑报销单页面
* **`stores/`**：Pinia 状态管理（在未接入后端前，模拟了本地内存数据库的角色）：
  * `baseData.ts`：存放下拉选择控件所需的元数据（如公司列表、部门列表、员工列表、城市、项目、树状业务类型）。
  * `reimbursement.ts`：存放 Mock 的报销单据列表和单据详情，并实现数据的新增（Save）、提交（Submit）、删除（Delete）、作废（Void）和复制（Copy）等本地内存操作。
* **`components/`**：页面细分组件目录：
  * `SearchBar.vue`：列表页顶部多条件查询栏。
  * `DataTable.vue`：报销单列表表格展示组件，包括各种状态徽章以及复制、编辑、作废、手工推送的操作菜单。
  * `Pagination.vue`：表格底部分页控件。
  * `ConfirmDialog.vue`：全局复用的弹窗提醒和二次确认框。
  * `TreeSelect.vue`：专为“业务类型”设计的三级树状下拉选择器（仅限选择叶子节点）。
  * `detail/` (报销单详情/编辑页的六个折叠分区组件)：
    * `BasicInfoSection.vue`（基础信息表单区）
    * `TripSection.vue`（补录行程列表及弹框区）
    * `SubsidyInfoSection.vue`（补助逐日明细列表及弹框编辑区）
    * `CostSummarySection.vue`（费用汇总明细展示区）
    * `CostAllocationSection.vue`（费用归属公司与项目比例分摊区，支持一键均摊）
    * `RemarkSection.vue`（备注表单区）
    * `ActionBar.vue`（底部保存/提交/关闭操作悬浮条）
* **`views/`**：页面视图层目录：
  * `ReimbursementList.vue`：差旅报销单的管理主界面。
  * `ReimbursementDetail.vue`：用于处理新增、详情查看、草稿编辑的核心页面。

-------------------

## 第二部分：前后端及数据库联调开发指南

目前，项目采用的是**纯前端内存操作（Mock 模式）**。如果需要与后端（Java Spring Boot等）和数据库进行对接开发，前端数据流必须从同步内存操作改造为**异步接口请求**。以下是必须修改的文件和接口开发计划：

### 2.1 引入网络请求层（新增）

在 `src` 下新建 `api` 目录，通过 Axios 等库实现前后端的数据交互：

1. **新建 `src/api/request.ts`**：
   * 创建 Axios 实例，配置 `baseURL`（如 `http://localhost:8080/api`）和 `timeout`。
   * 编写响应拦截器，统一解包后端返回的 `Result<T>` 格式。若 `code === 200`，直接返回里面的 `data`；若遇到 401/403/500 等错误，抛出异常或统一提示。
2. **新建 `src/api/baseData.ts`**：
   * 提供获取基础数据的接口，对应后端基础元数据查询：
     * `getCompanies()`: `GET /api/base/companies`
     * `getDepartments()`: `GET /api/base/departments`
     * `getEmployees()`: `GET /api/base/employees`
     * `getBusinessTypes()`: `GET /api/base/businessTypes`
     * `getCities()`: `GET /api/base/cities`
     * `getProjects()`: `GET /api/base/projects`
3. **新建 `src/api/reimbursement.ts`**：
   * 提供报销单生命周期相关的接口：
     * `getReimbursementList(params)`: `GET /api/reimbursement/list` (传入筛选及分页参数)
     * `getReimbursementDetail(id)`: `GET /api/reimbursement/detail/{id}`
     * `saveReimbursement(data)`: `POST /api/reimbursement/save` (保存草稿，传入完整的报销实体，后端判断 ID 若为空则进行 Insert，并返回新生成的业务 ID)
     * `submitReimbursement(data)`: `POST /api/reimbursement/submit` (提交报销单进行流程流转)
     * `deleteReimbursement(id)`: `DELETE /api/reimbursement/delete/{id}`
     * `cancelReimbursement(id)`: `PUT /api/reimbursement/cancel/{id}` (作废单据)

### 2.2 修改 Pinia 状态管理层

需要将 `src/stores` 下的两个文件由硬编码数据源修改为**调用网络 API 并更新 state**：

#### 1. 修改 `src/stores/baseData.ts`
* **状态修改**：将初始的硬编码数组（`companies` 等）全部改为**空数组**，并新增一个 `loaded: false` 的状态属性防止重复拉取。
* **新增异步 Actions `loadAll()`**：
  ```typescript
  async loadAll() {
    if (this.loaded) return;
    try {
      const [companies, departments, employees, cities, projects, businessTypes] = await Promise.all([
        getCompanies(),
        getDepartments(),
        getEmployees(),
        getCities(),
        getProjects(),
        getBusinessTypes()
      ]);
      this.companies = companies;
      this.departments = departments;
      this.employees = employees;
      this.cities = cities;
      this.projects = projects;
      this.businessTypes = businessTypes;
      this.loaded = true;
    } catch (err) {
      console.error('初始化基础数据失败:', err);
    }
  }
  ```

#### 2. 修改 `src/stores/reimbursement.ts`
* **状态修改**：删除写死的 Mock 数组。将状态更改为：
  ```typescript
  state: () => ({
    reimbursementList: [] as Reimbursement[], // 列表页数据
    totalCount: 0,                           // 总条数，供分页器使用
    currentDetail: null as Reimbursement | null,
    listLoading: false,
    detailLoading: false
  })
  ```
* **Actions 修改**（全部改为 async 并调用异步 API）：
  * `fetchList(filters)`：调用 `getReimbursementList`，拉取后端经过数据库查询并分页后的数据集。
  * `loadDetail(id)`：调用 `getReimbursementDetail`，拉取关联了子表行程、补助、分摊比例的完整聚合实体。
  * `saveDraft(data)`：调用 `saveReimbursement(data)`。
    * *关键逻辑*：如果单据是新建的（ID为空），保存草稿后，后端会把生成的数据库主键 ID 返回给前端。前端需要立即将该 ID 赋值给本地的 `model.value.id`。这样后续的操作（如继续修改或添加行程）将会被识别为“更新操作”（Update），而不会在数据库中产生多条脏数据。
  * `submitSheet(data)`：调用 `submitReimbursement`。
  * `deleteReimbursement(id)`：调用 `deleteReimbursement`。
  * `voidReimbursement(id)`：调用 `cancelReimbursement`。
  * `copyReimbursement(id)`：先调用 `getReimbursementDetail` 拉取详情，清空所有 ID (包括子表的 `tripId`、`subsidyId` 等)，生成新标题后调用接口保存为一条新草稿。

### 2.3 修改视图组件层

#### 1. 修改 `src/App.vue`
* 在入口挂载时，调用一次 `baseDataStore.loadAll()`，保证在加载页面前所有下拉选项（员工、部门、公司）的数据已就绪。

#### 2. 修改 `src/views/ReimbursementList.vue`
* 移除依靠前端计算属性过滤的 `filteredItems` 和 `pagedItems` 逻辑。
* 编写 `fetchData` 异步函数：
  ```typescript
  const fetchData = async () => {
    loading.value = true;
    await reimbursementStore.fetchList({
      ...searchFilters,
      pageNum: currentPage.value,
      pageSize: pageSize.value
    });
    loading.value = false;
  };
  ```
* 利用 `onMounted` 钩子以及对 `currentPage`、`pageSize`、`searchFilters` 的监听，在查询条件变化或翻页时触发 `fetchData`。

#### 3. 修改 `src/views/ReimbursementDetail.vue`
* **异步加载**：在 `onMounted` 钩子中，如果是修改单据，调用 `await reimbursementStore.loadDetail(id)` 异步填充表单。
* **添加 Loading 体验**：为保存按钮添加 `saving.value`，为提交按钮添加 `submitting.value`，在请求未返回时置灰防止表单重复提交。
* **设计“自动保存草稿”的持久化机制**：
  * 原型设计要求：由于行程补录、补助日历的调整非常繁琐，页面一旦意外关闭或刷新将导致全盘丢失。为了提升体验，系统在行程添加/删除、补助日历变更或费用分摊调整时，除了在前端进行内存联动外，应当**立刻异步静默调用一次 `saveDraft`**，将最新的数据包实时写入数据库的草稿表中。
  * 改造办法：在 `handleUpdateTrips`、`handleUpdateSubsidy`、`handleUpdateAllocations` 完成计算重算后，在函数末尾追加 `await reimbursementStore.saveDraft(model.value)` 调用。

### 2.4 配置本地跨域代理

由于本地开发时，前端资源服务通常在 `http://localhost:5173`，而后端接口服务通常在 `http://localhost:8080`（存在跨域限制），需要修改 `reimbursement-front/vite.config.ts`：

```typescript
import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import vueDevTools from 'vite-plugin-vue-devtools'

export default defineConfig({
  plugins: [
    vue(),
    vueJsx(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 指向本地 Java 服务端地址
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})
```
通过该配置，前端在调用接口时只需请求 `/api/reimbursement/list`，Vite 会自动帮我们无缝代理转发到后端的服务器，规避了浏览器的跨域问题。
