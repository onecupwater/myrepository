import Vue from 'vue'
import VueRouter from 'vue-router'
import store from "@/store";

Vue.use(VueRouter)

const routes = [

    //静态路由的写法，没有角色权限的用户可以通过地址栏输入准确的地址去访问需要角色权限的页面，不安全
  /*{
    path: '/',
    name: 'Manage',
    component: () => import('../views/Manage.vue'),
    redirect: "home",
    children: [
        {path: 'home', name: '主页', component: () => import('../views/Home.vue')},
        {path: 'user', name: '用户管理', component: () => import('../views/User.vue')},
        {path: 'file', name: '文件管理', component: () => import('../views/File.vue')},
        {path: 'menu', name: '菜单管理', component: () => import('../views/Menu.vue')},
        {path: 'role', name: '角色管理', component: () => import('../views/Role.vue')},
        {path: 'person', name: '个人信息', component: () => import('../views/Person.vue')},
        {path: 'personEdit', name: '个人信息编辑', component: () => import('../views/PersonEdit.vue')}
        ]
  },*/
    {
        path: '/register',
        name: '注册',
        component: () => import('../views/Register.vue')
    },
    {
        path: '/login',
        name: '登录',
        component: () => import('../views/Login.vue')
    },
    {
        path: '/notFound',
        name: 'NotFound',
        component: () => import('../views/NotFound.vue')
    },
    {
        path: '/front',   //path的内容，第一个‘/’必须用，最后写不写‘/’都可以，如：‘/front’ = ‘/front/’
        name: 'Front',
        component: () => import('../views/front/Front.vue'),
        redirect: "front/frontHome",
        children: [
            //前台的person和personEdit一定要和后台的分开，哪怕是复制两份相同的，前台后台不能共用同一个person页面
            // import的路径也要有所不同，不然就会报‘双重’路由警告
            {path: 'person', name: '前台个人信息', component: () => import('../views/front/Person.vue')},
            {path: 'personEdit', name: '前台个人信息编辑', component: () => import('../views/front/PersonEdit.vue')},
            {path: 'frontHome', name: '前台主页', component: () => import('../views/front/FrontHome.vue')},
            {path: 'comic', name: '漫画图片', component: () => import('../views/front/Comic.vue')},
            {path: 'video', name: '视频播放', component: () => import('../views/front/Video.vue')},
            {path: 'videoDetail', name: '视频播放详情', component: () => import('../views/front/VideoDetail.vue')},
            {path: 'article', name: '文章', component: () => import('../views/front/Article.vue')},
            {path: 'articleDetail', name: '文章详情', component: () => import('../views/front/ArticleDetail.vue')},
            {path: 'chatOnline', name: '在线聊天', component: () => import('../views/front/Im.vue')},
            {path: 'notice', name: '公告', component: () => import('../views/front/Notice.vue')},
            {path: 'goodsDetail', name: '商品详情', component: () => import('../views/front/GoodsDetail.vue')},
            {path: 'cart', name: '购物车', component: () => import('../views/front/Cart.vue')},
            {path: 'checkOrderFromCart', name: '检查来自购物车的订单', component: () => import('../views/front/CheckOrderFromCart.vue')},
            {path: 'orders', name: '订单', component: () => import('../views/front/Orders.vue')},
            {path: 'ordersDetail', name: '订单详情', component: () => import('../views/front/OrdersDetail.vue')},
            {path: 'checkOrderFromPurchase', name: '检查来自直接购买的订单', component: () => import('../views/front/CheckOrderFromPurchase.vue')},
            {path: 'exam', name: '考试', component: () => import('../views/front/Exam.vue')},
            {path: 'paperDetail', name: '开始考试', component: () => import('../views/front/PaperDetail.vue')},
            {path: 'courseTable', name: '课程表', component: () => import('../views/front/CourseTable.vue')},
        ]
    }

]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
});

//因为router没有重置路由的方法，用下面的方法可以视作重置路由
//导出给其他页面使用就行了，使用这个重置路由的动机：
//因为系统使用了动态路由，那系统的路由的生成就只在用户登录的那一刻在生成，哦，刷新页面也会生成，但是你把
//刷新页面也重新生成路由的道路给堵住了，不信，看下面的代码就明白了，当时堵住了是为了不让在登录时发生页面跳转
//导致重复设置了两次路由，所以当出现下面的场景时，旧的路由就不能满足现状，现状如下：
//之所以要重置路由，场景是：在菜单栏新建一个业务页面，views包也新建了一个真的业务页面，
//接着管理员在角色栏给管理员角色分配新的菜单,点击‘确定’后，系统会让管理员重新登录，登录后管理员点击侧边栏新增的目录，
//但由于路由一直是旧的，没有把新增的页面写进旧的路由，就会导致点击新目录而跳到notfound页面。
//所以需要重置旧的路由，在logout的时候,logout只是一个方法，写在store.js里的，
//任何页面在业务需要的时候都可以去调用logout方法
export const resetRouter = () => {
    router.matcher = new VueRouter({
        mode: 'history',
        base: process.env.BASE_URL,
        routes
    });
}

 //动态路由，没有权限的用户不能在地址栏输入准确的地址去访问需要权限的页面
export const setRoutes = () =>{
    const user = localStorage.getItem("user");
    if(user){
        //判断一下有没有Manage路由，没有才执行后面的操作
        let allRouteNames = router.getRoutes().map(value => value.name);
        if(!allRouteNames.includes("Manage")){

            const menus = JSON.parse(user).menus;
            const manageRoute = { path: '/', name: 'Manage', component: () => import('../views/Manage.vue'),
                redirect: "home", children: [
                    {path: 'person', name: '个人信息', component: () => import('../views/Person.vue')},
                    {path: 'personEdit', name: '个人信息编辑', component: () => import('../views/PersonEdit.vue')},
                    {path: 'studentPaperDetail', name: '阅卷', component: () => import('../views/StudentPaperDetail.vue')}
                ]};
            menus.forEach(menu => {
                if(menu.path){
                    let menusRoutes = {path: menu.path.replace("/",""), name: menu.name, component: () => import('../views/' + menu.pagePath + '.vue')};
                    manageRoute.children.push(menusRoutes);
                }
                if(menu.children.length){
                    menu.children.forEach(menuChildren =>{
                        let childrenMenusRoutes = {path: menuChildren.path.replace("/",""), name: menuChildren.name, component: () => import('../views/'+menuChildren.pagePath+'.vue')};
                        manageRoute.children.push(childrenMenusRoutes);
                    });
                }
            });
            router.addRoute(manageRoute);
        }
    }
};

//作用是：防止登录后，浏览器刷新页面，当前登录状态的路由消失，导致页面显示空白
//但是这会出现另一个问题: 浏览器会报‘路由重复’的警告，因为登录的时候就已经设置过一次路由，跳转页面虽然没触发浏览器刷新，
//但会触发这个方法，因为登录成功后设置了跳转到首页，所以触发了这个方法，也就是再设置了一次动态路由
//解决这个问题，就是在设置路由的时候判断一下有没有叫Manage名字的路由，没有的时候再设置。
// Manage是整个前端后台最根本的页面
// 后续新增功能：功能描述，除了前端后台页面，还加了前端前台页面，
// setRoutes()是有权限访问后台页面的用户，每次刷新页面都会调用到的加载动态路由的方法，
// 没有权限的用户会访问到前台页面，没有权限的用户也会对前台页面进行刷新，如果没有对setRoutes()进行下面的条件判断，
// 就会导致不管是没有权限的用户还是有权限的用户每次刷新页面都会执行setRoutes()方法，导致没有权限的用户每次刷新都报错，
// 因为setRoutes()里需要用户的menus属性，进行下面的判断后，
// 没权限的用户每次刷新页面就不会执行setRoutes()，而是去找静态路由
// store.js的全局常量为什么不能传给setRoutes()方法，我也不知道，
// 如果把setRoutes()里的user去掉，setRoutes()就没法调用，代码只能是这么写了！
const user = localStorage.getItem("user");
if(user){
    const menus = JSON.parse(user).menus;
    if(menus){
        setRoutes();
    }
}

//路由守卫
router.beforeEach((to, from, next) => {
  localStorage.setItem("currentPathName",to.name) //设置当前路由名称
  store.commit("setPath") //触发store的数据更新

    //未找到路由的情况
    if(to.matched.length===0){
        const storeUser = localStorage.getItem("user");
        if(storeUser){
            //如果有storeUser，用户在地址栏乱输路径，就都跳到notFound页面
            next("/notFound")
        }else {
            //如果没有，说明还没登录，就跳到登录页面
            next("/login")
        }
    }

  next() //放行当前路由
})

export default router
