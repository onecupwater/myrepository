<template>
    <div>
        <div style="margin: 15px 0px">
            <el-popconfirm
                    confirm-button-text='确定'
                    cancel-button-text='取消'
                    icon="el-icon-info"
                    icon-color="red"
                    title="您确定批量删除吗？"
                    @confirm="deleteByBatchIds()"
            >
                <el-button type="danger" icon="el-icon-delete" slot="reference">批量删除</el-button>
            </el-popconfirm>


            <el-input style="width: 200px; margin-left: 400px; margin-top: 6px; margin-right: 10px"
                      placeholder="请输入名称" prefix-icon="el-icon-search" v-model="name"></el-input>
            <el-button type="primary" @click="load">搜索</el-button>
            <el-button type="warning" @click="reset">重置</el-button>
        </div>
        <el-table :data="tableData" border stripe header-cell-class-name="headerCellClass"
                  @selection-change="handleSelectionChange">

            <el-table-column align="center" type="selection" width="45"></el-table-column>
            <el-table-column align="center" prop="id" label="ID" width="45"></el-table-column>
            <el-table-column align="center" prop="username" label="用户账号" ></el-table-column>
            <el-table-column align="center" prop="nickname" label="用户昵称" ></el-table-column>
            <el-table-column align="center" prop="goodsName" label="商品名称" width="250" ></el-table-column>
            <el-table-column align="center" prop="code" label="商品编号" width="120"></el-table-column>
            <el-table-column align="center" label="商品图片" width="120">
                <template slot-scope="scope">
                    <img :src="scope.row.picture" style="width: 70px">
                </template>
            </el-table-column>
            <el-table-column align="center" prop="price" label="商品单价（元）"></el-table-column>
            <el-table-column align="center" prop="num" label="购买数量" ></el-table-column>
            <el-table-column align="center" prop="subtotal" label="小计（元）"></el-table-column>

            <el-table-column label="操作" align="center">
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
                    layout="total, sizes, prev, pager, next, jumper"
                    :total="total">
            </el-pagination>
        </div>

    </div>
</template>

<script>
    export default {
        name: "Cart",
        data(){
            return {
                tableData: [],
                total: 0,
                pageNum: 1,
                pageSize: 10,
                name: "",
                form: [],
                multipleSelection: [],
            }
        },
        created() {
            this.load();
        },
        methods: {
            load(){
                //使用axios来获取后端数据，首先在vue前端项目的目录下安装axios,添加utils/request.js文件，内容可以参考vue项目
                this.request.get("cart/page",{
                    params: {
                        pageNum: this.pageNum,
                        pageSize: this.pageSize,
                        name: this.name
                    }
                }).then(res=>{
                    this.tableData = res.data.records;
                    this.total = res.data.total;
                });
            },
            handleSelectionChange(val){
                this.multipleSelection = val;
            },
            deleteByBatchIds(){
                let ids = this.multipleSelection.map(value => value.id);
                this.request.post("/cart/removeBatchByIds",ids).then(res=>{
                    if(res.code===200){
                        this.$message.success("批量删除成功");
                        this.load();
                    }else {
                        this.$message.error("批量删除失败");
                    }
                })
            },
            deleteById(id){
                this.request.delete("cart/delete/" + id).then(res=>{
                    if(res.code===200){
                        this.$message.success("删除成功");
                        this.load();
                    }else {
                        this.$message.error("删除失败")
                    }
                })
            },
            reset(){
                this.name = '';
                this.load();
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

<style>
    .headerCellClass{
        background-color: lightgray !important;
    }
</style>