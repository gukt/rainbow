import Vue from 'vue'
import Router from 'vue-router'
import Layout from '../views/layout/Layout'

Vue.use(Router)

/**
 * hidden: true                   if `hidden:true` will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu, whatever its child routes length
 *                                if not set alwaysShow, only more than one route under the children
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noredirect           if `redirect:noredirect` will no redirct in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    title: 'title'               the name show in submenu and breadcrumb (recommend set)
    icon: 'svg-name'             the icon show in the sidebar,
  }
 **/
export const routes = [
  // 登陆
  {
    path: '/login',
    hidden: true,
    component: () => import('../views/login/index')
  },
  // 404
  {
    path: '/404',
    hidden: true,
    component: () => import('../views/404')
  },
  // 匹配不到路由的路径，全都 redirect 到 404
  {
    path: '*',
    hidden: true,
    redirect: '/404'
  },
  // 根目录，跳转到房源列表
  {
    meta: { title: '主页', icon: 'table' },
    component: Layout,
    path: '/',
    // redirect: '/house-list',
    name: 'Dashboard',
    // hidden: true,
    admin: true,
    children: [
      {
        meta: { title: '仪表盘', icon: 'table' },
        path: 'dashboard',
        component: () => import('@/views/dashboard/index')
      }
    ]
  },
  {
    meta: { title: '用户管理', icon: 'table' },
    path: '/',
    component: Layout,
    admin: true,
    children: [
      {
        meta: { title: '用户列表', icon: 'table' },
        name: 'UserList',
        path: 'user-list',
        component: () => import('../views/user/user-list')
      },
      {
        meta: { title: '角色列表', icon: 'table' },
        name: 'RoleList',
        path: 'role-list',
        component: () => import('../views/role/role-list')
      }
    ]
  },
  {
    meta: { title: '服务器管理', icon: 'table' },
    path: '/',
    component: Layout,
    admin: true,
    children: [
      {
        meta: { title: '服务器列表', icon: 'table' },
        name: 'ServerList',
        path: 'server-list',
        component: () => import('../views/server/server-list')
      }
    ]
  },
  {
    meta: { title: '全局设置', icon: 'table' },
    path: '/settings',
    component: Layout,
    admin: true,
    children: [
      {
        meta: { title: '基本设置', icon: 'table' },
        name: 'Settings',
        path: '/basic',
        component: () => import('../views/settings')
      }
    ]
  }
]

export default new Router({
  // mode: 'history', //后端支持可开
  scrollBehavior: () => ({ y: 0 }),
  routes
})

