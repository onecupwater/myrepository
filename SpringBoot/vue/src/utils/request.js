import axios from 'axios';
import router from '../router/index.js';
import { Message } from "element-ui";

const request = axios.create({
    baseURL: 'http://localhost:9090',  // 注意！！ 这里是全局统一加上了 后端接口前缀 前缀，后端必须进行跨域配置！
    timeout: 10000
})

// request 拦截器
// 可以自请求发送前对请求做一些处理
// 比如统一加token，对请求参数统一加密
request.interceptors.request.use(config => {
    config.headers['Content-Type'] = 'application/json;charset=utf-8';

    // 进入从login.vue这个系统后，所有对系统再次访问的请求的header都会在这里被带上user里的token值
    // 被带上token的请求再次访问后端时，会被后端拦截器拦截，具体去看后端的token拦截器
    let user = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : null;
    if(user){
        config.headers['token'] = user.token;  // 设置请求头
    }

    return config
}, error => {
    return Promise.reject(error)
});

// response 拦截器
// 可以在接口响应后统一处理结果
request.interceptors.response.use(
    response => {

        let res = response.data;

        //如果res.msg=token不存在，请重新登录,跳转登录页面
        if(res.code===300 && res.msg==="token不存在，请重新登录"){
            router.push("/login");
            //当然这句代码可以不要，只是演示在js文件中如何使用this.$message();
            Message.error("token不存在，请重新登录");
        }
        //如果res.msg=用户不存在，请重新登录,跳转登录页面
        if(res.code===300 && res.msg==="用户不存在，请重新登录"){
            router.push("/login");
            //当然这句代码可以不要，只是演示在js文件中如何使用this.$message();
            Message.error("用户不存在，请重新登录")
        }
        //如果res.msg=token验证不通过，请重新登录,跳转登录页面
        if(res.code===300 && res.msg==="token验证不通过，请重新登录"){
            router.push("/login");
            //当然这句代码可以不要，只是演示在js文件中如何使用this.$message();
            Message.error("token验证不通过，请重新登录")
        }

        // 如果是返回的文件
        if (response.config.responseType === 'blob') {
            return res
        }
        // 兼容服务端返回的字符串数据
        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res
        }
        return res;
    },
    error => {
        console.log('err' + error) // for debug
        return Promise.reject(error)
    }
)


export default request

