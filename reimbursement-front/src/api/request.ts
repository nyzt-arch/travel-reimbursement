// 修改原因：配置统一的 API 请求类。
// 后端返回的数据外面套着一层 Result，
// 在这里通过 Axios 拦截器对其统一进行解包、剥离外壳，
// 并全局处理可能出现的网络异常或校验错误。
import axios from 'axios';

// 创建 axios 实例
const request = axios.create({
  baseURL: '/api', // 对应 vite.config.ts 配置的代理路径前缀
  timeout: 10000,   // 超时时间
});

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const res = response.data;
    // 后端统一用 Result 结构返回，这里在拦截器统一解包
    if (res.code !== 200) {
      alert(res.msg || '服务器接口发生异常');
      return Promise.reject(new Error(res.msg || 'Error'));
    }
    return res.data; // 返回 Result.data 内的真实业务对象
  },
  (error) => {
    alert(error.message || '网络连接超时或服务器异常');
    return Promise.reject(error);
  }
);

export default request;
