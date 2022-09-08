<template>
    <div>
        <el-card style="height: 20vh; margin: 25px 150px">
            <div style="display: flex">
                <el-upload
                        class="avatar-uploader"
                        action="http://localhost:9090/file/upload"
                        :show-file-list="false"
                        :on-success="handleAvatarSuccess"
                >
                    <img v-if="personForm.avatar" :src="personForm.avatar" class="avatar">
                    <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                </el-upload>
                <!--<img :src="user.avatar" style="width: 120px;  margin-left: 20px; ">-->
                <div style="width: 500px; margin-left: 15px; margin-top: 10px; font-size: 25px;">
                    {{personForm.nickname}}
                    <div style="width: 700px;font-size: 15px; margin-top: 30px; display: flex">
                        <span style="margin-top:5px; width: 100px;">个性签名:</span>
                        <el-input v-model="personForm.sign" autocomplete="off"></el-input>
                    </div>
                </div>
                <div>
                    <el-button style="margin-top:70px; margin-left: 300px;font-size: 15px" type="primary" plain @click="editInfo('personForm')">保存</el-button>
                </div>
            </div>
        </el-card>
        <el-card style="height: 60vh; margin: 25px 150px; padding: 0 30px;">
            <div style="margin-top: 10px;margin-bottom: 25px; margin-left: 5px">
                资料编辑
            </div>
            <div style="border-bottom: 1px solid #ccc; margin-bottom: 20px"></div>
            <div style="margin-top: 50px">

                <el-form :model="personForm" ref="personForm" label-width="60px" style="margin: 0 150px">
                    <el-form-item label="账号">
                        <el-input v-model="personForm.name" disabled autocomplete="off"  ></el-input>
                    </el-form-item>

                    <el-form-item label="昵称"
                                  prop="nickname"
                                  :rules="[
                              { required: true, message: '昵称不能为空'},
                              { min: 1, max: 10, message: '长度在 1 到 10 个字符'}
                              ]"
                    >
                        <el-input v-model="personForm.nickname" autocomplete="off"></el-input>
                    </el-form-item>

                    <el-form-item label="电话">
                        <el-input v-model="personForm.phone" autocomplete="off" ></el-input>
                    </el-form-item>
                    <el-form-item label="邮箱">
                        <el-input v-model="personForm.email" autocomplete="off" ></el-input>
                    </el-form-item>
                    <el-form-item label="地址">
                        <el-input type="textarea" v-model="personForm.address" autocomplete="off" ></el-input>
                    </el-form-item>
                </el-form>
            </div>
        </el-card>
    </div>
</template>

<script>
    export default {
        name: "PersonEdit",
        data(){
            return{
                personForm: {
                    nickname: '',
                },
                user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
                newUser :{}
            }
        },
        created() {
            this.personForm = this.user;
        },
        methods:{
            editInfo(personForm){

                this.$refs[personForm].validate((valid) => {
                    if(valid){
                        this.request.post("user/save",this.personForm).then(res=>{
                            if(res.code===200){
                                /**
                                 * personForm对象只是存储，准备发送数据给后台user/save之前的数据包
                                 * user对象只是存储登录时后台第一时间返回给前端的旧数据，里面的token非常重要
                                 * newUser对象就是点击保存时请求后台user/save之后，后台返回的新数据，
                                 * 但是这个新数据里没有token和menus，所以需要将旧数据的token和menus赋值给新数据，保证store的localStorage里的user变量依旧有token和menus
                                 * 因为在这一步操作里，点击‘保存’时，要及时更新header.vue的头像数据，所以localStorage才在这里remove旧数据，set新数据，
                                 * 这样才能保证header.vue能显示最新的user数据
                                 */
                                let token = this.personForm.token;
                                this.newUser = res.data;
                                this.newUser.token = token;
                                localStorage.removeItem("user");
                                let newData = JSON.stringify(this.newUser);
                                localStorage.setItem("user",newData);
                                this.$router.push('/front/person');
                                window.location.reload(); //刷新浏览器来更新header的显示新名字
                                this.$message.success("保存成功");
                            }else {
                                this.$message.error("保存失败");
                            }
                        })
                    }else {
                        return false;
                    }
                });
            },
            handleAvatarSuccess(res){
                this.personForm.avatar = res;
            }
        }
    }
</script>

<style>
    .avatar-uploader .el-upload {
        border: 1px dashed #d9d9d9;
        border-radius: 6px;
        cursor: pointer;
        position: relative;
        overflow: hidden;
        margin-left: 20px;
    }
    .avatar-uploader .el-upload:hover {
        border-color: #409EFF;
    }
    .avatar-uploader-icon {
        font-size: 28px;
        color: #8c939d;
        width: 120px;
        height: 120px;
        line-height: 120px;
        text-align: center;
    }
    .avatar {
        width: 120px;
        height: 120px;
        display: block;
    }
</style>