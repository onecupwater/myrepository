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
                      placeholder="请输入问题名称" prefix-icon="el-icon-search" v-model="name"></el-input>
            <el-button type="primary" @click="load">搜索</el-button>
            <el-button type="warning" @click="reset">重置</el-button>
        </div>
        <el-table :data="tableData" border stripe header-cell-class-name="headerCellClass"
                  @selection-change="handleSelectionChange">

            <el-table-column align="center" type="selection" width="45"></el-table-column>
            <el-table-column align="center" prop="id" label="ID" width="45"></el-table-column>
            <el-table-column align="center" label="课程类型" >
                <template slot-scope="scope">
                    <span v-if="courses && courses.length">
                        {{courses.find(value => value.id === scope.row.courseId) ?
                            courses.find(value => value.id === scope.row.courseId).name : ''}}
                    </span>
                </template>
            </el-table-column>
            <el-table-column align="center" label="题目类型" >
                <template slot-scope="scope">
                    <span v-if="questionType && questionType.length">
                        {{questionType.find(value => value.id === scope.row.questionType) ?
                            questionType.find(value => value.id === scope.row.questionType).name : ''}}
                    </span>
                </template>
            </el-table-column>
            <el-table-column align="center" prop="name" label="题目" ></el-table-column>
            <el-table-column align="center" prop="a" label="选项A" ></el-table-column>
            <el-table-column align="center" prop="b" label="选项B" ></el-table-column>
            <el-table-column align="center" prop="c" label="选项C" ></el-table-column>
            <el-table-column align="center" prop="d" label="选项D"></el-table-column>
            <el-table-column align="center" prop="answer" label="答案" ></el-table-column>
            <el-table-column align="center" prop="analysis" label="解析"></el-table-column>
            <el-table-column align="center" prop="score" label="分数"></el-table-column>

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
        <el-dialog title="题目信息" :visible.sync="dialogFormVisible" width="50%">
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
                <el-form-item label="题目类型">
                    <el-select clearable v-model="form.questionType"
                               placeholder="请选择题目类型" style="width: 100%">
                        <el-option v-for="item in questionType"
                                   :key="item.id" :label="item.name" :value="item.id">
                            <span>{{ item.name }}</span>
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="题目">
                    <el-input type="textarea" v-model="form.name" autocomplete="off" ></el-input>
                </el-form-item>
                <el-form-item v-if="form.questionType === 1" label="选项A">
                    <el-input v-model="form.a" autocomplete="off" ></el-input>
                </el-form-item>
                <el-form-item v-if="form.questionType === 1" label="选项B">
                    <el-input v-model="form.b" autocomplete="off" ></el-input>
                </el-form-item>
                <el-form-item v-if="form.questionType === 1" label="选项C">
                    <el-input v-model="form.c" autocomplete="off" ></el-input>
                </el-form-item>
                <el-form-item v-if="form.questionType === 1" label="选项D">
                    <el-input v-model="form.d" autocomplete="off" ></el-input>
                </el-form-item>
                <el-form-item label="答案">
                    <el-input type="textarea" v-model="form.answer" autocomplete="off" ></el-input>
                </el-form-item>
                <el-form-item label="解析">
                    <el-input type="textarea" v-model="form.analysis" autocomplete="off" ></el-input>
                </el-form-item>
                <el-form-item label="分数">
                    <el-input v-model="form.score" autocomplete="off" ></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="handleAdd">确 定</el-button>
            </div>
        </el-dialog>

    </div>
</template>

<script>
    export default {
        name: "Question",
        data(){
            return {
                tableData: [],
                total: 0,
                pageNum: 1,
                pageSize: 2,
                name: "",
                form: [],
                dialogFormVisible: false,
                multipleSelection: [],
                courses: [],
                questionType: [{id: 1,name:"选择题"}, {id: 2,name:"判断题"}, {id: 3,name:"填空题"}],
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
                this.request.get("question/page",{
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
                this.request.post("/question/removeBatchByIds",ids).then(res=>{
                    if(res.code===200){
                        this.$message.success("批量删除成功");
                        this.load();
                    }else {
                        this.$message.error("批量删除失败");
                    }
                })
            },
            deleteById(id){
                this.request.delete("question/delete/" + id).then(res=>{
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
                this.request.post("question/save",this.form).then(res=>{
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
        }
    }
</script>

<style>
    .headerCellClass{
        background-color: lightgray !important;
    }
</style>