<template>
    <div style="padding-top: 30px; padding-bottom: 100px; padding-left: 207px; padding-right: 210px">

        <el-table :data="tableData" border stripe header-cell-class-name="headerCellClass">
            <el-table-column align="center" prop="name" label="订单名称" ></el-table-column>
            <el-table-column align="center" prop="no" label="订单号" width="140px" ></el-table-column>
            <el-table-column align="center" prop="amount" label="支付金额" ></el-table-column>
            <el-table-column align="center" prop="state" label="支付状态"></el-table-column>
            <el-table-column align="center" prop="orderTime" label="下单时间" width="90px" ></el-table-column>
            <el-table-column align="center" prop="payTime" label="支付时间" width="90px" ></el-table-column>
            <el-table-column align="center" label="订单详情" >
                <template slot-scope="scope">
                    <el-button type="primary"
                               @click="viewOrderDetail(scope.row.id)">查看订单</el-button>
                </template>
            </el-table-column>

            <el-table-column label="操作" align="center" width="180px">
                <template slot-scope="scope">
                    <el-popconfirm
                            confirm-button-text='确定'
                            cancel-button-text='取消'
                            icon="el-icon-info"
                            icon-color="red"
                            title="您确定删除吗？"
                            @confirm="deleteById(scope.row.id)"
                    >
                        <el-button type="danger" plain icon="el-icon-delete" slot="reference">删除</el-button>
                    </el-popconfirm>
                </template>
            </el-table-column>
        </el-table>
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
        name: "Orders",
        data(){
            return{
                tableData: [],
                total: 0,
                pageNum: 1,
                pageSize: 10,
                no: "",     //订单号
            }
        },
        created() {
            this.load();
        },
        methods:{
            load(){
                //使用axios来获取后端数据，首先在vue前端项目的目录下安装axios,添加utils/request.js文件，内容可以参考vue项目
                this.request.get("orders/page",{
                    params: {
                        pageNum: this.pageNum,
                        pageSize: this.pageSize,
                        no: this.no
                    }
                }).then(res=>{
                    this.tableData = res.data.records;
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
            //跳转订单详情页
            viewOrderDetail(ordersId){
                this.$router.push({path:"/front/ordersDetail",query:{"ordersId": ordersId}});
            },
            deleteById(id){
                this.request.delete("orders/delete/" + id).then(res=>{
                    if(res.code===200){
                        this.$message.success("删除成功");
                        this.load();
                    }else {
                        this.$message.error("删除失败")
                    }
                })
            },
        }
    }
</script>

<style scoped>

</style>