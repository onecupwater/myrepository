<template>
    <div>
        <!--这里头部-->
        <div style="display: flex; height: 60px; margin: 0px 10px">
            <!--这里logo-->
            <div style="width: 200px; display: flex">
                <div>
                    <img src="../../assets/dongman.jpg"
                         width="60px"
                         style="border-radius: 20px;"/>
                </div>
                <div style="flex: 1; text-align: center; line-height: 60px; font-family: 楷体;
                font-size: 25px; font-weight: bolder; font-style:italic; color: darkorchid">
                    二次元
                </div>
            </div>
            <!--这里导航栏-->
            <div style="width: 1040px">
                <el-menu
                        router
                        :default-active="'/front'"
                        class="el-menu-demo"
                        mode="horizontal"
                        background-color="#545c64"
                        text-color="#fff"
                        active-text-color="#ffd04b">
                    <el-menu-item v-if="user.role==='ROLE_ADMIN'" index="/home">返回后台</el-menu-item>
                    <el-menu-item style="margin-left: 100px"
                                  index="/front" >主页</el-menu-item>
                    <el-submenu index="2">
                        <template slot="title">看好看的</template>
                        <el-menu-item index="/front/comic">漫画图片</el-menu-item>
                        <el-menu-item index="/front/video">视频播放</el-menu-item>
                    </el-submenu>
                    <el-menu-item index="/front/article">文章</el-menu-item>
                    <el-menu-item index="/front/chatOnline">在线聊天</el-menu-item>
                    <el-menu-item index="/front/notice">公告</el-menu-item>
                    <el-menu-item index="/front/cart">我的购物车</el-menu-item>
                    <el-menu-item index="/front/orders">我的订单</el-menu-item>
                    <el-menu-item index="/front/exam">考试</el-menu-item>
                    <el-menu-item index="/front/courseTable">课程表</el-menu-item>
                </el-menu>
            </div>
            <!--这里用户信息-->
            <div>
                <div v-if="!user.name">
                    <div style="width: 200px; line-height: 60px; text-align: center">
                        <el-button type="primary" plain @click="$router.push('/login')">登录</el-button>
                        <el-button type="primary" plain @click="$router.push('/register')">注册</el-button>
                    </div>
                </div>

                <div v-else>
                    <el-dropdown style="width: 200px; cursor: pointer; text-align: right">
                        <div>
                            <img :src="user.avatar"
                                 style="width: 38px; height: 38px; border-radius: 50px; position: relative; top: 10px; right: 5px">
                            <span>{{user.nickname}}</span><i class = "el-icon-arrow-down" style="margin-left: 5px"></i>
                        </div>
                        <el-dropdown-menu slot="dropdown" style="text-align: center; ">
                            <el-dropdown-item>
                                <span @click="$router.push('/front/person')">个人信息</span>
                            </el-dropdown-item>
                            <el-dropdown-item>
                                <span @click="logout">退出</span>
                            </el-dropdown-item>
                        </el-dropdown-menu>
                    </el-dropdown>
                </div>

            </div>
        </div>

        <!--这里是主体展示部分-->
        <router-view/>
    </div>
</template>

<script>
    export default {
        name: "Front",
        data(){
            return{
                user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
            }
        },
        methods :{
            logout(){
                this.$store.commit("logout");
                this.$message.success("退出成功");
            }
        }
    }
</script>

<style scoped>

</style>