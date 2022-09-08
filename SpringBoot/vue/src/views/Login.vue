<template>
    <div style="height: 100vh;background-image: linear-gradient(to top right,#E4E7ED,#909399);overflow: hidden">

        <div style="margin: 100px auto; width: 600px; background-color: white; border-radius: 20px; overflow: hidden">
            <el-tabs v-model="activeName" type="card">
                <el-tab-pane name="first" label="账号密码登录">
                    <div style="margin: 50px auto;
                    padding: 20px 30px;
                    background-color: snow;
                    height: 350px;
                    width: 400px;
                    border-radius: 20px">
                        <el-form :model="user" ref="user">
                            <el-form-item style="margin-top: 25px;"
                                          prop="name"
                                          :rules="[
                              { required: true, message: '用户名不能为空'},
                              { min: 1, max: 10, message: '长度在 1 到 10 个字符'}
                              ]"
                            >
                                <el-input  placeholder="用户名" prefix-icon="el-icon-s-check" v-model="user.name" size="large" autocomplete="off"></el-input>
                            </el-form-item>

                            <el-form-item style="margin-top: 25px"
                                          prop="password"
                                          :rules="[
                              { required: true, message: '密码不能为空'},
                              { type: 'number', message: '必须为数字值'}
                              ]"
                            >
                                <el-input  placeholder="密码" prefix-icon="el-icon-lock" v-model.number="user.password" show-password size="large" autocomplete="off"></el-input>
                            </el-form-item>

                            <el-form-item style="margin-top: 25px">
                                <el-button type="primary" round size="large" style="width: 340px; font-size: 17px" @click="submitForm('user')">登录</el-button>
                            </el-form-item>

                            <el-form-item style="margin-top: 25px">
                                <el-button type="danger" round size="large" style="width: 340px; font-size: 17px" @click="$router.push('/register')">注册</el-button>
                            </el-form-item>
                            <div style="text-align: center; cursor: pointer" class="div1"
                                 @click="openForgetPassDialog">
                                忘记密码
                            </div>
                        </el-form>

                    </div>
                </el-tab-pane>
                <el-tab-pane name="second" label="邮箱登录">
                    <div style="margin: 50px auto;
                    padding: 20px 30px;
                    background-color: snow;
                    height: 350px;
                    width: 400px;
                    border-radius: 20px">

                        <el-form :model="userForEmailLogin" ref="userForEmailLogin">
                            <el-form-item style="margin-top: 25px;"
                                          prop="email"
                                          :rules="[
                              { required: true, message: '邮箱不能为空'},
                              { min: 1, max: 50, message: '长度在 1 到 50 个字符'}
                              ]"
                            >
                                <el-input  placeholder="邮箱" prefix-icon="el-icon-message"
                                           v-model="userForEmailLogin.email" size="large" autocomplete="off"></el-input>
                            </el-form-item>

                            <el-form-item style="margin-top: 25px"
                                          prop="code"
                                          :rules="[
                              { required: true, message: '验证码不能为空'},
                              { type: 'number', message: '必须为数字值'}
                              ]"
                            >
                                <el-input  placeholder="验证码" prefix-icon="el-icon-lock" style="width: 217px"
                                           v-model.number="userForEmailLogin.code" size="large" autocomplete="off"></el-input>
                                <el-button style="margin-left: 10px" size="large" type="primary"
                                           @click="getEmailCode(userForEmailLogin,1)">获取验证码</el-button>
                            </el-form-item>

                            <el-form-item style="margin-top: 25px">
                                <el-button type="primary" round size="large"
                                           style="width: 340px; font-size: 17px"
                                           @click="loginByEmail('userForEmailLogin')">登录</el-button>
                            </el-form-item>

                            <el-form-item style="margin-top: 25px">
                                <el-button type="danger" round size="large"
                                           style="width: 340px; font-size: 17px"
                                           @click="$router.push('/register')">注册</el-button>
                            </el-form-item>
                        </el-form>

                    </div>
                </el-tab-pane>
            </el-tabs>
        </div>
        <!--忘记密码弹框-->
        <el-dialog title="忘记密码" :visible.sync="dialogForgetPassVisible" width="35%">
            <h3 style="text-align: center; padding-bottom: 20px">邮箱验证</h3>
            <div style="margin-right: 50px">
                <el-form :model="emailPass" label-width="70px">
                    <el-form-item label="邮箱：">
                        <el-input prefix-icon="el-icon-message" v-model="emailPass.email"></el-input>
                    </el-form-item>
                    <el-form-item label="验证码：">
                        <el-input  prefix-icon="el-icon-lock" style="width: 242px"
                                   v-model="emailPass.code"></el-input>
                        <el-button style="margin-left: 10px" type="primary"
                                   @click="getEmailCode(emailPass,2)">获取验证码</el-button>
                    </el-form-item>
                </el-form>
            </div>
            <div slot="footer" class="dialog-footer" style="margin-right: 50px; margin-bottom: 30px">
                <el-button @click="dialogForgetPassVisible = false">取 消</el-button>
                <el-button type="primary" @click="resetPass">重置密码</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
    import {setRoutes} from "../router";

    export default {
        name: "Login",
        data(){
            return{
                activeName: 'first',
                user: {
                    name: '',
                    password: '',
                },
                userForEmailLogin:{
                    email: '',
                    code: '',
                },
                dialogForgetPassVisible: false,
                emailPass: {},
            }
        },
        methods:{
            //账号密码登录
            submitForm(user){
                this.$refs[user].validate((valid) => {
                    if(valid){
                        this.request.post("/user/login",this.user).then(res=>{
                            if(res.code!==200){
                                this.$message.error(res.msg);
                            }else {
                                localStorage.setItem("user",JSON.stringify(res.data));
                                //如果返回的用户对象里的menus属性不为空，则登录后跳转后台
                                if(res.data.menus!==null){
                                    //后台用户登录后设置路由
                                    setRoutes();
                                    this.$router.push("/");
                                    this.$message.success(res.msg);
                                }else{
                                    //menus属性为空，则登录后跳转前台
                                    this.$router.push("/front");
                                    this.$message.success(res.msg);
                                }

                            }
                        });
                    }else {
                        return false;
                    }
                });
            },
            //邮箱登录
            loginByEmail(userForEmailLogin){
                this.$refs[userForEmailLogin].validate((valid) => {
                    if(valid){
                        this.request.post("/user/loginByEmail",this.userForEmailLogin).then(res=>{
                            if(res.code!==200){
                                this.$message.error(res.msg);
                            }else {
                                localStorage.setItem("user",JSON.stringify(res.data));
                                //如果返回的用户对象里的menus属性不为空，则登录后跳转后台
                                if(res.data.menus!==null){
                                    //后台用户登录后设置路由
                                    setRoutes();
                                    this.$router.push("/");
                                    this.$message.success(res.msg);
                                }else{
                                    //menus属性为空，则登录后跳转前台
                                    this.$router.push("/front");
                                    this.$message.success(res.msg);
                                }

                            }
                        });
                    }else {
                        return false;
                    }
                });
            },
            //获取验证码
            getEmailCode(form,type){
                if(!form.email){
                    this.$message.warning("请输入邮箱");
                    return;
                }
                //验证邮箱合法性
                if(!/^\w+((.\w+)|(-\w+))@[A-Za-z0-9]+((.|-)[A-Za-z0-9]+).[A-Za-z0-9]+$/.test(form.email)){
                    this.$message.warning("请输入正确格式的邮箱");
                    return;
                }
                this.request.get("emailVerification/sendEmailCode/" + form.email +"/" + type).then(res=>{
                    if(res.code===200){
                        this.$message.success("验证码发送成功，请留意邮箱信息");
                    }else {
                        this.$message.error(res.msg);
                    }
                });
            },
            //打开忘记密码弹框
            openForgetPassDialog(){
                this.dialogForgetPassVisible = true;
            },
            resetPass(){
                if(!this.emailPass.email){
                    this.$message.warning("请输入邮箱");
                    return;
                }
                if(!this.emailPass.code){
                    this.$message.warning("请输入验证码");
                    return;
                }
                this.request.post("user/resetPass",this.emailPass).then(res=>{
                    if(res.code===200){
                        this.$message.success(res.msg);
                        this.dialogForgetPassVisible = false;
                    }else {
                        this.$message.error(res.msg);
                    }
                });
            },
        }
    }
</script>

<style>
    .div1:hover{
        color: #8c939d;
    }
</style>