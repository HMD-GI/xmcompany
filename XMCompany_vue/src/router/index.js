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
      { path: 'stock-operation', component: () => import('@/pages/Stock/StockOperationList.vue') },
      { path: 'purchase', component: () => import('@/pages/Purchase/PurchaseList.vue') },
      { path: 'purchase-request', component: () => import('@/pages/Purchase/PurchaseRequestList.vue') },
      { path: 'supplier', component: () => import('@/pages/Supplier/SupplierList.vue') },
      { path: 'production', component: () => import('@/pages/Production/ProductionList.vue') },
      { path: 'sale', component: () => import('@/pages/Sale/SaleList.vue') },
      { path: 'customer', component: () => import('@/pages/Customer/CustomerList.vue') },
      { path: 'employee', component: () => import('@/pages/Employee/EmployeeList.vue') },
      { path: 'leave', component: () => import('@/pages/Leave/LeaveList.vue') },
      { path: 'salary', component: () => import('@/pages/Salary/SalaryList.vue') },
      { path: 'ai', component: () => import('@/pages/AI/AIChat.vue') },
      { path: 'profile', component: () => import('@/pages/Profile/ProfilePage.vue') },
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
  const isLoggedIn = !!userStore.token

  if (to.path === '/login' && isLoggedIn) {
    // 如果用户已登录，但要访问登录页，则重定向到主页
    next({ path: '/' })
  } else if (to.path !== '/login' && !isLoggedIn) {
    // 如果用户未登录，且访问的不是登录页，则重定向到登录页
    next({ path: '/login' })
  } else {
    const adminOnlyRoutes = ['/employee', '/salary']
    if (isLoggedIn && adminOnlyRoutes.includes(to.path) && userStore.role !== 'ADMIN') {
      // 如果用户已登录但不是管理员，访问管理员专属页面则重定向到主页
      next({ path: '/' })
    } else {
      // 其他情况正常放行
      next()
    }
  }
})

export default router