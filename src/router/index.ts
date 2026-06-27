import { createRouter, createWebHistory } from 'vue-router';
import ReimbursementList from '../views/ReimbursementList.vue';
import ReimbursementDetail from '../views/ReimbursementDetail.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'list',
      component: ReimbursementList
    },
    {
      path: '/detail/new',
      name: 'detail-new',
      component: ReimbursementDetail
    },
    {
      path: '/detail/:id',
      name: 'detail-edit',
      component: ReimbursementDetail
    }
  ]
});

export default router;
