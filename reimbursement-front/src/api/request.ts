// request.ts (网络请求核心配置文件)
// 路径：src/api/request.ts
// 作用：
// 初始化网络请求库（如 Axios），配置全局参数（例如请求的基础路径 baseURL 和超时时间 timeout）。
// 配置请求/响应拦截器（Interceptors）。例如：在响应拦截器中对后端返回的统一格式 Result<T> 进行解包，直接提取出 data 部分，并在此处做全局错误捕获或 Token 登录状态失效处理。
