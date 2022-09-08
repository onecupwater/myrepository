<template>
    <div style="padding-top: 30px; padding-left: 207px; padding-right: 210px">
        <div v-for="item in articles" :key="item.id" style="margin: 1px 0">
            <el-card>
                <span>{{item.publisher}}</span>
                <span style="padding-left: 10px">
                    <i class="el-icon-time"></i>
                    {{item.publishTime}}
                </span>
                <h3 style="padding-top: 10px; cursor: pointer"
                    @click="goArticleDetail(item.id)">{{item.name}}</h3>
            </el-card>
        </div>
        <div style="padding: 10px 0">
            <el-pagination
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                    :current-page="pageNum"
                    :page-sizes="[2, 5, 10, 15, 20]"
                    :page-size="pageSize"
                    layout="total, prev, pager, next"
                    :total="total">
            </el-pagination>
        </div>
    </div>
</template>

<script>
    export default {
        name: "Article",
        data(){
            return {
                articles: [],
                total: 0,
                pageNum: 1,
                pageSize: 10,
            }
        },
        created() {
            this.request.get("article/page",{
                params: {
                    pageNum: this.pageNum,
                    pageSize: this.pageSize,
                }
            }).then(res=>{
                this.articles = res.records;
                this.total = res.total;
            });
        },
        methods:{
            goArticleDetail(id){
                this.$router.push({path:"/front/articleDetail",query:{"itemId" : id}})
            },
            handleSizeChange(pageSize){
                this.pageSize = pageSize;
                this.load();
            },
            handleCurrentChange(pageNum){
                this.pageNum = pageNum;
                this.load();
            },
        }
    }
</script>

<style scoped>

</style>