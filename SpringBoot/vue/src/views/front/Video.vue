<template>
    <div style="padding-top: 30px; padding-left: 207px; padding-right: 210px">
        <div style="padding-top: 10px">
            <el-card >
                <el-row :gutter="10">
                    <el-col :span="6" v-for="item in videos">
                        <el-card class="item">
                            <img :src="item.cover" width="100%" height="195.75px"
                                 @click="goVideoDetail(item.id)"
                                 style="padding-bottom: 10px; cursor: pointer">
                            <div style="text-align: center; padding-bottom: 5px;
                                 cursor: pointer" @click="goVideoDetail(item.id)" >{{item.name}}</div>
                            <div style="padding-bottom: 5px">状态：{{item.state}}</div>
                            <div style="padding-bottom: 5px">类型：{{item.sort}}</div>
                            <div>更新：{{item.updateTime}}</div>
                        </el-card>
                        <div style="padding-bottom: 10px"></div>
                    </el-col>
                </el-row>

                <!--分页-->
                <div style="padding: 10px 0">
                    <el-pagination
                            @size-change="handleSizeChange"
                            @current-change="handleCurrentChange"
                            :current-page="pageNum"
                            :page-sizes="[2, 5, 10, 15, 20]"
                            :page-size="pageSize"
                            layout="total, sizes, prev, pager, next, jumper"
                            :total="total">
                    </el-pagination>
                </div>
            </el-card>
        </div>
    </div>
</template>

<script>
    export default {
        name: "Video",
        data(){
            return {
                videos : [],
                total: 0,
                pageNum: 1,
                pageSize: 20,
            }
        },
        created() {
            this.load();
        },
        methods: {
            load(){
                //使用axios来获取后端数据，首先在vue前端项目的目录下安装axios,添加utils/request.js文件，内容可以参考vue项目
                this.request.get("video/page",{
                    params: {
                        pageNum: this.pageNum,
                        pageSize: this.pageSize,
                    }
                }).then(res=>{
                    this.videos = res.data.records;
                    this.total = res.data.total;
                });
            },
            handleSizeChange(pageSize){
                this.pageSize = pageSize;
                this.load();
            },
            handleCurrentChange(pageNum){
                this.pageNum = pageNum;
                this.load();
            },
            goVideoDetail(id){
                this.$router.push({path:"/front/videoDetail",query:{"itemId":id}});
            }
        }
    }
</script>

<style scoped>
.item:hover{
    background-color: #DDDDFF;
}
</style>