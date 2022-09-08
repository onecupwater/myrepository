import Vue from 'vue'
import Vuex from 'vuex'
import router, {resetRouter} from "@/router";

Vue.use(Vuex)

const store = new Vuex.Store({
    state:{
        currentPathName: ''
    },
    mutations:{
        setPath(state){
            state.currentPathName = localStorage.getItem("currentPathName")
        },
        //所有vue页面需要调退出方法的可以集体使用这里的方法
        logout(){
            //清除缓存
            localStorage.removeItem("user");
            //重置路由
            resetRouter();
            //跳转登录页面
            router.push("/login");
        }
    }
})

export default store