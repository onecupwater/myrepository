<template>
    <div style="height: 100vh;background-image: linear-gradient(to top right,#909399,#E4E7ED);overflow: hidden">
        <div style="margin: 160px auto; padding: 20px 30px; background-color: snow; height: 420px; width: 400px; border-radius: 20px">

            <div style="text-align: center; font-size: 25px; color: #909399" >
                注册
            </div>

            <el-form :model="user" ref="user">
                <el-form-item style="margin-top: 25px;"
                              prop="name"
                              :rules="[
                              { required: true, message: '用户名不能为空'},
                              { min: 2, max: 10, message: '长度在 2 到 10 个字符'}
                              ]"
                >
                    <el-input  placeholder="请输入用户名" prefix-icon="el-icon-s-check" v-model="user.name" size="large" autocomplete="off"></el-input>
                </el-form-item>

                <el-form-item style="margin-top: 25px"
                              prop="password"
                              :rules="[
                              { required: true, message: '密码不能为空'},
                              { type: 'number', message: '必须为数字值'}
                              ]"
                >
                    <el-input  placeholder="请输入密码" prefix-icon="el-icon-lock" v-model.number="user.password" show-password size="large" autocomplete="off"></el-input>
                </el-form-item>

                <el-form-item style="margin-top: 25px"
                              prop="confirmPassword"
                              :rules="[
                              { required: true, message: '密码不能为空'},
                              { type: 'number', message: '必须为数字值'}
                              ]"
                >
                    <el-input  placeholder="请确认密码" prefix-icon="el-icon-lock" v-model.number="user.confirmPassword" show-password size="large" autocomplete="off"></el-input>
                </el-form-item>

                <el-form-item style="margin-top: 25px">
                    <el-button type="primary" round size="large" style="width: 340px; font-size: 17px" @click="submitForm('user')">注册</el-button>
                </el-form-item>

                <el-form-item style="margin-top: 25px">
                    <el-button type="danger" round size="large" style="width: 340px; font-size: 17px" @click="$router.push('/login')">返回登录</el-button>
                </el-form-item>
            </el-form>

        </div>
    </div>
</template>

<script>
    export default {
        name: "Register",
        data(){
            return{
                user: {
                    name: '',
                    password: '',
                    confirmPassword: ''
                },
            }
        },
        methods:{

            submitForm(user){
                if(this.user.password!==this.user.confirmPassword){
                    this.$message.error("两次密码不相同");
                    return false;
                }
                this.$refs[user].validate((valid) => {
                    if(valid){
                        this.request.post("/user/register",this.user).then(res=>{
                            if(res.code!==200){
                                this.$message.error(res.msg);
                            }else {
                                this.$message.success(res.msg);
                            }
                        });
                    }else {
                        return false;
                    }
                });
            }
        }
    }
</script>

<style scoped>

</style>