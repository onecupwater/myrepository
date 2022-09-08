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
        </div>
        <el-table :data="tableData" border stripe header-cell-class-name="headerCellClass"
                  @selection-change="handleSelectionChange">

            <el-table-column align="center" type="selection" width="45"></el-table-column>
            <el-table-column align="center" prop="id" label="ID" width="45"></el-table-column>
            <el-table-column align="center" prop="userId" label="学生名称" >
                <template slot-scope="scope">
                    <span v-if="users && users.length">
                        {{users.find(value => value.id === scope.row.userId) ?
                        users.find(value => value.id === scope.row.userId).nickname : ""}}
                    </span>
                </template>
            </el-table-column>
            <el-table-column align="center" prop="examId" label="考试名称" >
                <template slot-scope="scope">
                    <span v-if="exams && exams.length">
                        {{exams.find(value => value.id === scope.row.examId)?
                        exams.find(value => value.id === scope.row.examId).name : ""}}</span>
                </template>
            </el-table-column>
            <el-table-column align="center" prop="state" label="审核状态" ></el-table-column>
            <el-table-column align="center" label="审核">
                <template slot-scope="scope">
                    <el-button type="success" @click="changeState(scope.row,'审核通过')">审核通过</el-button>
                    <el-button type="danger" @click="changeState(scope.row,'审核不通过')">审核不通过</el-button>
                </template>
            </el-table-column>

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
        name: "Sign",
        data(){
            return {
                tableData: [],
                total: 0,
                pageNum: 1,
                pageSize: 10,
                name: "",
                form: {},
                multipleSelection: [],
                users: [],
                exams: [],
            }
        },
        created() {
            this.load();
            //偷懒，不做3表的关联查询
            this.request.get("user").then(res=>{
                this.users = res;
            });
            this.request.get("exam").then(res=>{
                if(res.code===200){
                    this.exams = res.data;
                }
            });

        },
        methods: {
            load(){
                //使用axios来获取后端数据，首先在vue前端项目的目录下安装axios,添加utils/request.js文件，内容可以参考vue项目
                this.request.get("sign/page",{
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
                this.request.post("/sign/removeBatchByIds",ids).then(res=>{
                    if(res.code===200){
                        this.$message.success("批量删除成功");
                        this.load();
                    }else {
                        this.$message.error("批量删除失败");
                    }
                })
            },
            deleteById(id){
                this.request.delete("sign/delete/" + id).then(res=>{
                    if(res.code===200){
                        this.$message.success("删除成功");
                        this.load();
                    }else {
                        this.$message.error("删除失败")
                    }
                })
            },
            handleAdd(){
                this.request.post("sign/save",this.form).then(res=>{
                    if(res.code===200){
                        this.$message.success("保存成功");
                        this.load();
                    }else {
                        this.$message.error("保存失败");
                    }
                })
            },
            handleSizeChange(pageSize){
                this.pageSize = pageSize;
                this.load();
            },
            handleCurrentChange(pageNum){
                this.pageNum = pageNum;
                this.load();
            },
            changeState(row,state){
                this.form = JSON.parse(JSON.stringify(row));
                this.form.state = state;
                this.handleAdd();
            },
        }
    }
</script>

<style>
    .headerCellClass{
        background-color: lightgray !important;
    }
</style>