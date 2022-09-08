<template>
    <div style="font-size: 12px; border-bottom: 1px solid #ccc; line-height: 60px; display: flex">
        <div style="flex: 1;font-size: 23px">
            <span :class="collapseBtnClass" style="cursor: pointer" @click="collapse"></span>

            <el-breadcrumb separator="/" style="display: inline-block; margin-left: 20px; font-size: 18px">
                <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
                <el-breadcrumb-item>{{currentPathName}}</el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <el-dropdown style="width: 300px; cursor: pointer; text-align: right">
            <div>
                <img :src="user.avatar"
                     style="width: 38px; height: 38px; border-radius: 50px; position: relative; top: 10px; right: 5px">
                <span>{{user.nickname}}</span><i class = "el-icon-arrow-down" style="margin-left: 5px"></i>
            </div>
            <el-dropdown-menu slot="dropdown" style="text-align: center; ">
                <el-dropdown-item>
                    <span @click="$router.push('/person')">个人信息</span>
                </el-dropdown-item>
                <el-dropdown-item>
                    <span @click="logout">退出</span>
                </el-dropdown-item>
            </el-dropdown-menu>
        </el-dropdown>
    </div>
</template>

<script>
    export default {
        name: "Header",
        props: {
            collapseBtnClass: String,
            collapse: Function,
        },
        computed:{
            currentPathName(){
                return this.$store.state.currentPathName;
            }
        },
        data(){
            return{
                user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
            }
        },
        methods:{
            logout(){
                this.$store.commit("logout");
                this.$message.success("退出成功");
            }
        }
    }
</script>

<style scoped>

</style>