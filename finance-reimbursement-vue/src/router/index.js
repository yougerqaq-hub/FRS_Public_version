import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

const router = new Router({
    routes: [
        {
            path: '/',
            name: 'Login'
        },

        {
            path: '/home',
            component: () => import('../views/Home.vue'),

            children: [
                {
                    path: '',
                    name: 'Home',
                    component: () => import('../views/Dashboard.vue')
                },

                {
                    path: 'reimbursement',
                    name: 'Reimbursement',
                    component: () => import('../views/Reimbursement.vue')
                },

                {
                    path: 'audit',
                    name: 'Audit',
                    component: () => import('../views/Audit.vue')
                },

                {
                    path: 'budget',
                    name: 'Budget',
                    component: () => import('../views/Budget.vue')
                },

                {
                    path: 'warning',
                    name: 'Warning',
                    component: () => import('../views/Warning.vue')
                },

                {
                    path: 'user',
                    name: 'User',
                    component: () => import('../views/User.vue')
                }
            ]
        }
    ]
})

router.beforeEach((to, from, next) => {
    const loginUser = localStorage.getItem('loginUser')
    if (to.path === '/') {
        if (loginUser) {
            next('/home')
            return
        }
        next()
        return
    }
    if (!loginUser) {
        next('/')
        return
    }
    let user
    try {
        user = JSON.parse(loginUser)
    } catch (error) {
        localStorage.removeItem('loginUser')
        next('/')
        return
    }
    const role = user.role
    // 管理员权限
    if (to.path === '/home/user') {

        if (role !== 'ADMIN') {
            next('/home')
            return
        }

    }
    // 审核管理
    if (to.path === '/home/audit') {

        if (role !== 'ADMIN' && role !== 'FINANCE') {
            next('/home')
            return
        }

    }
    // 预算管理
    if (to.path === '/home/budget') {

        if (role !== 'ADMIN') {
            next('/home')
            return
        }

    }
    // 预算预警
    if (to.path === '/home/warning') {

        if (role !== 'ADMIN' && role !== 'FINANCE') {
            next('/home')
            return
        }

    }
    // 报销管理
    if (to.path === '/home/reimbursement') {

        if (
            role !== 'ADMIN'
            &&
            role !== 'FINANCE'
            &&
            role !== 'EMPLOYEE'
        ) {
            next('/home')
            return
        }
    }
    next()
})

export default router
