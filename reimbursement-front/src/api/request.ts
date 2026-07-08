import axios from 'axios'

// axios 实例
const request = axios.create({
  baseURL: '/api', // 对应 vite.config.ts 配置的代理路径前缀
  timeout: 10000, // 超时时间
})

// 请求拦截器：将 ISO 8601 日期时间格式截取为日期部分 (yyyy-MM-dd)，适配后端 Java LocalDate 解析
request.interceptors.request.use(
  (config) => {
    if (config.data) {
      const normalized = JSON.stringify(config.data).replace(
        /"(\d{4}-\d{2}-\d{2})T\d{2}:\d{2}(:\d{2})?"/g,
        '"$1"',
      )
      config.data = JSON.parse(normalized)
    }
    return config
  },
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
    // 将后端返回的日期时间格式 "YYYY-MM-DD HH:mm:ss" 转换为 ISO 8601 "YYYY-MM-DDTHH:mm:ss"
    // 确保前端 datetime-local 输入框能正确回显
    const normalized = JSON.stringify(res.data).replace(
      /"(\d{4}-\d{2}-\d{2}) (\d{2}:\d{2}(:\d{2})?)"/g,
      '"$1T$2"',
    )
    return JSON.parse(normalized)
  },
  (error) => {
    alert(error.message || '网络连接超时或服务器异常')
    return Promise.reject(error)
  },
)

export default request
