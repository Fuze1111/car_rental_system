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
      path: '/register',
      name: 'register',
      component: () => import('../views/Register.vue')
    },
    {
      path: '/user',
      component: () => import('../layout/UserLayout.vue'),
      redirect: '/user/cars',
      children: [
        {
          path: 'cars',
          name: 'user-cars',
          component: () => import('../views/user/CarSelection.vue')
        },
        {
          path: 'profile',
          name: 'user-profile',
          component: () => import('../views/user/UserProfile.vue')
        }
      ]
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
          name: 'CarManagement',
          component: () => import('../views/admin/CarManagement.vue'),
          meta: { title: '车辆列表' }
        },
        // car-types merged into cars
        {
          path: 'maintenance',
          name: 'CarMaintenanceManagement',
          component: () => import('../views/admin/CarMaintenanceManagement.vue'),
          meta: { title: '维修管理' }
        },
        {
          path: 'users',
          name: 'admin-users',
          component: () => import('../views/admin/UserManagement.vue'),
          meta: { title: '用户管理' }
        },
        {
          path: 'orders',
          name: 'admin-orders',
          component: () => import('../views/admin/OrderManagement.vue'),
          meta: { title: '订单管理' }
        },
        {
          path: 'settings',
          name: 'admin-settings',
          component: () => import('../views/admin/Settings.vue'),
          meta: { title: '系统设置' }
        }
      ]
    },
    {
      path: '/driver',
      component: () => import('../layout/DriverLayout.vue'),
      redirect: '/driver/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'driver-dashboard',
          component: () => import('../views/driver/DriverDashboard.vue'),
          meta: { title: '司机工作台' }
        }
      ]
    }
  ]
})

export default router
