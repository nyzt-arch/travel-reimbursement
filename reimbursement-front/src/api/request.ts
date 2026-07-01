import axios from 'axios'

// axios 实例
const request = axios.create({
  baseURL: '/api', // 对应 vite.config.ts 配置的代理路径前缀
  timeout: 10000, // 超时时间
})

// 请求拦截器
request.interceptors.request.use(
  (config) => config,
  (error) => Promise.reject(error),
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const res = response.data
    // 后端统一用 Result 结构返回，这里在拦截器统一解包
    if (res.code !== 200) {
      alert(res.msg || '服务器接口发生异常')
      return Promise.reject(new Error(res.msg || 'Error'))
    }
    return res.data // 返回 Result.data 内的真实业务对象
  },
  (error) => {
    alert(error.message || '网络连接超时或服务器异常')
    return Promise.reject(error)
  },
)

export default request
