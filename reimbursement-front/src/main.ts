import { createApp } from 'vue'
import { createPinia } from 'pinia'
import './assets/styles/main.css'

import App from './App.vue'
import router from './router'

const app = createApp(App)

const pinia = createPinia()
app.use(pinia)
app.use(router)

app.mount('#app')

// 防止 Vite 热更新时 Pinia store 状态被重置
if (import.meta.hot) {
  import.meta.hot.accept(['./stores/baseData', './stores/reimbursement'], () => {})
}
