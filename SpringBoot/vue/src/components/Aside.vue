<template>
    <el-menu :default-openeds="openAll" style="height: 100% ; overflow-x: hidden"
             background-color="rgb(48, 65, 86)"
             text-color="#fff"
             active-text-color="#ffd04e"
             :collapse-transition="false"
             :collapse="isCollapse"
             router>
        <div style="height: 60px; line-height: 60px; text-align: center">
            <img src="../assets/logo.png" style="height: 21px; margin-right: 1.5px; position: relative; top: 5px">
            <b style="color: white" v-show="logoTextShow">后台管理系统</b>
        </div>

<!--           下面是动态侧边栏，会随登录用户的不同而改变                -->

        <div v-for="item in menus">
            <div v-if="item.path">
                <el-menu-item :index="item.path">
                    <i :class="item.icon"></i>
                    <span slot="title">{{item.name}}</span>
                </el-menu-item>
            </div>
            <div v-else>
                <el-submenu :index="item.id+''">
                    <template slot="title">
                        <i :class="item.icon"></i>
                        <span slot="title" v-show="logoTextShow">{{item.name}}</span>
                    </template>
                    <div v-for="subItem in item.children">
                        <el-menu-item :index="subItem.path">
                            <i :class="subItem.icon"></i>
                            <span slot="title">{{subItem.name}}</span>
                        </el-menu-item>
                    </div>
                </el-submenu>
            </div>
        </div>

 <!-------------静态侧边栏，不会随用户的不同而改变------------------------->

        <div>
            <el-menu-item index="/front">
                <i class="el-icon-house"></i>
                <span slot="title">前往前台系统</span>
            </el-menu-item>
        </div>

<!--                   下面是静态侧边栏，不会随用户的不同而改变                    -->
<!--        <el-submenu index="/">
            <template slot="title">
                <i class="el-icon-house"></i>
                <span slot="title">主页</span>
            </template>
        </el-submenu>
        <el-submenu index="2">
            <template slot="title">
                <i class="el-icon-menu"></i>
                <span slot="title">系统管理</span>
            </template>
            <el-menu-item index="/user">
                <i class="el-icon-user"></i>
                <span slot="title">用户管理</span>
            </el-menu-item>
            <el-menu-item index="/file">
                <i class="el-icon-document"></i>
                <span slot="title">文件管理</span>
            </el-menu-item>
            <el-menu-item index="/menu">
                <i class="el-icon-menu"></i>
                <span slot="title">菜单管理</span>
            </el-menu-item>
            <el-menu-item index="/role">
                <i class="el-icon-coordinate"></i>
                <span slot="title">角色管理</span>
            </el-menu-item>
        </el-submenu>-->
    </el-menu>
</template>

<script>
    export default {
        name: "Aside",
        props: {
            isCollapse: Boolean,
            logoTextShow: Boolean
        },
        data(){
            return {
                user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : [],
                menus: [],
                openAll: []
            }
        },
        created() {
            this.menus = this.user.menus;
            this.openAll = this.user.menus.map(value=>value.id+'');
        }
    }
</script>

<style scoped>

</style>