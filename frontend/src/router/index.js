import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: Login
    },
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/admin',
      component: () => import('../layout/AdminLayout.vue'),
      redirect: '/admin/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'admin-dashboard',
          component: () => import('../views/admin/Dashboard.vue'),
          meta: { title: '控制台' }
        },
        {
          path: 'cars',
          name: 'admin-cars',
          component: () => import('../views/admin/CarManagement.vue'),
          meta: { title: '车辆管理' }
        },
        {
          path: 'users',
          name: 'admin-users',
          component: () => import('../views/admin/UserManagement.vue'),
          meta: { title: '用户管理' }
        },
        // TODO: 后面添加订单管理等路由
      ]
    }
  ]
})

export default router
