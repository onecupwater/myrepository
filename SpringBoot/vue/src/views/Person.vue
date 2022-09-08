<template>
    <div>
        <el-card style="height: 20vh; margin: 25px 50px">
            <div style="display: flex">
                <img :src="user.avatar" style="width: 120px; height: 120px;  margin-left: 20px; ">
                <div style="width: 500px; margin-left: 15px; margin-top: 10px; font-size: 25px;">
                    {{user.nickname}}
                    <div style="width: 700px;font-size: 15px; margin-top: 30px; display: flex">
                        <span style="width: 100px;">个性签名:</span>
                        <span>{{user.sign}}</span>
                    </div>
                </div>
                <div>
                    <el-button style="margin-left: 300px;font-size: 15px" type="primary" plain @click="$router.push('/personEdit')">编辑</el-button>
                </div>
            </div>
        </el-card>
        <el-card style="height: 60vh; margin: 25px 50px; padding: 0 30px;">
            <div style="margin-top: 10px;margin-bottom: 25px; margin-left: 5px">
                资料信息
            </div>
            <div style="border-bottom: 1px solid #ccc; margin-bottom: 20px"></div>
            <div >
                <div style="display: flex ;text-align: center; margin: 5px">
                    <el-card style="width: 60vh; margin-right: 40px">
                        用户名
                    </el-card>
                    <el-card style="width: 65vh">
                        {{user.name}}
                    </el-card>
                </div>
                <div style="display: flex ;text-align: center; margin: 5px">
                    <el-card style="width: 60vh; margin-right: 40px">
                        昵称
                    </el-card>
                    <el-card style="width: 65vh">
                        {{user.nickname}}
                    </el-card>
                </div>
                <div style="display: flex ;text-align: center; margin: 5px">
                    <el-card style="width: 60vh; margin-right: 40px">
                        电话
                    </el-card>
                    <el-card style="width: 65vh">
                        {{user.phone}}
                    </el-card>
                </div>
                <div style="display: flex ;text-align: center; margin: 5px">
                    <el-card style="width: 60vh; margin-right: 40px">
                        邮箱
                    </el-card>
                    <el-card style="width: 65vh">
                        {{user.email}}
                    </el-card>
                </div>
                <div style="display: flex ;text-align: center; margin: 5px">
                    <el-card style="width: 60vh; margin-right: 40px">
                        地址
                    </el-card>
                    <el-card style="width: 65vh">
                        {{user.address}}
                    </el-card>
                </div>
            </div>
        </el-card>
    </div>
</template>

<script>
    export default {
        name: "Person",
        data(){
            return{
                user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {}
            }
        }
    }
</script>

<style scoped>

</style>