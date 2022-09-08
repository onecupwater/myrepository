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
            <el-table-column align="center" label="学生名称" >
                <template slot-scope="scope">
                    <span v-if="users && users.length">
                        {{users.find(value => value.id === scope.row.userId)?
                        users.find(value => value.id === scope.row.userId).nickname : ""}}
                    </span>
                </template>
            </el-table-column>
            <el-table-column align="center" label="考试名称" >
                <template slot-scope="scope">
                    <span v-if="exams && exams.length">
                        {{exams.find(value => value.id === scope.row.examId)?
                        exams.find(value => value.id === scope.row.examId).name : ""}}
                    </span>
                </template>
            </el-table-column>
            <el-table-column align="center" prop="score" label="总分" ></el-table-column>
            <el-table-column align="center" prop="time" label="提交时间" width="90px"></el-table-column>
            <el-table-column align="center" label="试卷内容" >
                <template slot-scope="scope">
                    <el-button type="primary" @click="markPaper(scope.row.id)">阅卷</el-button>
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
        name: "StudentPaper",
        data(){
            return {
                tableData: [],
                total: 0,
                pageNum: 1,
                pageSize: 10,
                name: "",
                form: [],
                dialogFormVisible: false,
                multipleSelection: [],
                users: [],
                exams: [],
            }
        },
        created() {
            this.load();
        },
        methods: {
            load(){
                //使用axios来获取后端数据，首先在vue前端项目的目录下安装axios,添加utils/request.js文件，内容可以参考vue项目
                this.request.get("studentPaper/page",{
                    params: {
                        pageNum: this.pageNum,
                        pageSize: this.pageSize,
                        name: this.name
                    }
                }).then(res=>{
                    this.tableData = res.data.records;
                    this.total = res.data.total;
                });
                //获取所有的user
                this.request.get("/user").then(res=>{
                    this.users = res;
                });
                //获取所有的考试
                this.request.get("/exam").then(res=>{
                    this.exams = res.data;
                });
            },
            handleSelectionChange(val){
                this.multipleSelection = val;
            },
            deleteByBatchIds(){
                let ids = this.multipleSelection.map(value => value.id);
                this.request.post("/studentPaper/removeBatchByIds",ids).then(res=>{
                    if(res.code===200){
                        this.$message.success("批量删除成功");
                        this.load();
                    }else {
                        this.$message.error("批量删除失败");
                    }
                })
            },
            deleteById(id){
                this.request.delete("studentPaper/delete/" + id).then(res=>{
                    if(res.code===200){
                        this.$message.success("删除成功");
                        this.load();
                    }else {
                        this.$message.error("删除失败")
                    }
                })
            },
            handleAdd(){
                this.request.post("studentPaper/save",this.form).then(res=>{
                    if(res.code===200){
                        this.$message.success("保存成功");
                        this.dialogFormVisible = false;
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
            markPaper(studentPaperId){
                this.$router.push({path:"/studentPaperDetail",query:{"studentPaperId": studentPaperId}});
            },
        }
    }
</script>

<style>
    .headerCellClass{
        background-color: lightgray !important;
    }
</style>