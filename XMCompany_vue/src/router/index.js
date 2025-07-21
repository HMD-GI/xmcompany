import { createRouter, createWebHistory } from 'vue-router'
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import EmptyLayout from '@/layouts/EmptyLayout.vue'

const routes = [
  {
    path: '/',
    component: DefaultLayout,
    children: [
      { path: '', redirect: '/stock' },
      { path: 'stock', component: () => import('@/pages/Stock/StockList.vue') },
      { path: 'purchase', component: () => import('@/pages/Purchase/PurchaseList.vue') },
      { path: 'customer', component: () => import('@/pages/Customer/CustomerList.vue') },
      { path: 'employee', component: () => import('@/pages/Employee/EmployeeList.vue') },
      { path: 'leave', component: () => import('@/pages/Leave/LeaveList.vue') },
      { path: 'salary', component: () => import('@/pages/Salary/SalaryList.vue') },
      { path: 'ai', component: () => import('@/pages/AI/AIChat.vue') },
    ]
  },
  {
    path: '/login',
    component: EmptyLayout,
    children: [
      { path: '', component: () => import('@/pages/Login/LoginPage.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：未登录强制跳转到登录页
import { useUserStore } from '@/store/user'
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.path === '/login') {
    next()
    return
  }
  if (!userStore.token) {
    next('/login')
    return
  }
  next()
})

export default router