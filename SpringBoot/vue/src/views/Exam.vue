<template>
    <div>
        <div style="margin: 15px 0px">
            <el-button style="margin-right: 9px" type="primary" icon="el-icon-circle-plus-outline"
                       @click="handleDialog">新增</el-button>
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
            <el-table-column align="center" prop="name" label="考试名称" ></el-table-column>
            <el-table-column align="center" label="课程">
                <template slot-scope="scope">
                    <span v-if="courses && courses.length">
                        {{courses.find(value => value.id === scope.row.courseId) ?
                            courses.find(value => value.id === scope.row.courseId).name : ''}}
                    </span>
                </template>
            </el-table-column>
            <el-table-column align="center" prop="teacher" label="监考老师" ></el-table-column>
            <el-table-column align="center" prop="startTime" label="开始时间" width="90px"></el-table-column>
            <el-table-column align="center" prop="state" label="考试状态"></el-table-column>
            <el-table-column align="center" prop="paperName" label="试卷名称"></el-table-column>
            <el-table-column align="center" label="设置试卷">
                <template slot-scope="scope">
                    <el-button type="primary" @click="openExamDialog(scope.row)">选择试卷</el-button>
                </template>
            </el-table-column>

            <el-table-column label="操作" align="center" width="180px">
                <template slot-scope="scope">
                    <el-button type="success" style="margin-right: 8px" plain icon="el-icon-edit"
                               @click="handleEdit(scope.row)">修改</el-button>
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
        <el-dialog title="考试信息" :visible.sync="dialogFormVisible" width="30%">
            <el-form :model="form" label-width="70px">
                <el-form-item label="课程类型">
                    <el-select clearable v-model="form.courseId"
                               placeholder="请选择课程" style="width: 100%">
                        <el-option v-for="item in courses"
                                   :key="item.id" :label="item.name" :value="item.id">
                            <span>{{ item.name }}</span>
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="考试名称">
                    <el-input v-model="form.name" autocomplete="off" ></el-input>
                </el-form-item>
                <el-form-item label="监考老师">
                    <el-input v-model="form.teacher" autocomplete="off" ></el-input>
                </el-form-item>
                <el-form-item label="开始时间">
                    <el-date-picker
                            v-model="form.startTime"
                            type="datetime"
                            value-format="yyyy-MM-dd HH:mm:ss"
                            style="width: 100%"
                            placeholder="选择日期时间">
                    </el-date-picker>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="handleAdd">确 定</el-button>
            </div>
        </el-dialog>

        <el-dialog title="选择试卷" :visible.sync="dialogChoicePaperVisible" width="30%">
            <el-form :model="examPaperForm" label-width="70px">
                <el-form-item label="试卷">
                    <el-select clearable v-model="examPaperForm.paperId" placeholder="请选择试卷" style="width: 100%">
                        <el-option v-for="item in papers"
                                   :key="item.id" :label="item.name" :value="item.id">
                            <span>{{ item.name }}</span>
                        </el-option>
                    </el-select>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogChoicePaperVisible = false">取 消</el-button>
                <el-button type="primary" @click="submitExamPaperForm">确 定</el-button>
            </div>
        </el-dialog>

    </div>
</template>

<script>
    export default {
        name: "Exam",
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
                courses: [],
                dialogChoicePaperVisible: false,
                papers: [],
                examPaperForm: {},
            }
        },
        created() {
            this.load();
            this.request.get("course").then(res=>{
                if(res.code===200){
                    this.courses = res.data;
                }
            });
        },
        methods: {
            load(){
                //使用axios来获取后端数据，首先在vue前端项目的目录下安装axios,添加utils/request.js文件，内容可以参考vue项目
                this.request.get("exam/page",{
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
                this.request.post("/exam/removeBatchByIds",ids).then(res=>{
                    if(res.code===200){
                        this.$message.success("批量删除成功");
                        this.load();
                    }else {
                        this.$message.error("批量删除失败");
                    }
                })
            },
            deleteById(id){
                this.request.delete("exam/delete/" + id).then(res=>{
                    if(res.code===200){
                        this.$message.success("删除成功");
                        this.load();
                    }else {
                        this.$message.error("删除失败")
                    }
                })
            },
            handleEdit(row){
                this.dialogFormVisible = true;
                this.form = row;
            },
            handleAdd(){
                this.request.post("exam/save",this.form).then(res=>{
                    if(res.code===200){
                        this.$message.success("保存成功");
                        this.dialogFormVisible = false;
                        this.load();
                    }else {
                        this.$message.error("保存失败");
                    }
                })
            },
            handleDialog(){
                this.dialogFormVisible = true;
                this.form = {};
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
            openExamDialog(currentExam){
                if(!currentExam.courseId){
                    this.$message.error("先为‘当前考试’选择‘课程’，再选择‘试卷’");
                    return;
                }

                //在点击选择试卷时才去获取当前课程类型的所有试卷，比如物理的所有试卷，
                // 为什么不在created做这一步呢？因为created无法获得课程id，
                // 所以如果在created做这一步的话，那就是获取所有的试卷了,
                this.request.get("/paper/findPaperByCourseId/" + currentExam.courseId).then(res=>{
                    if(res.code===200){
                        this.examPaperForm = currentExam;
                        this.papers = res.data;
                        this.dialogChoicePaperVisible = true;
                    }
                });

            },
            submitExamPaperForm(){
                if(!this.examPaperForm.paperId){
                    this.$message.error("请选择试卷");
                    return;
                }
                this.request.post("/exam/save", this.examPaperForm).then(res=>{
                    if(res.code===200){
                        this.$message.success("选择成功");
                        this.dialogChoicePaperVisible = false;
                        this.load();
                    }else {
                        this.$message.error("选择失败");
                    }
                });
            },
        }
    }
</script>

<style>
    .headerCellClass{
        background-color: lightgray !important;
    }
</style>