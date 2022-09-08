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
            <el-table-column align="center" label="课程类型" >
                <template slot-scope="scope">
                    <span v-if="courses && courses.length">
                        {{courses.find(value => value.id === scope.row.courseId) ?
                            courses.find(value => value.id === scope.row.courseId).name : ''}}
                    </span>
                </template>
            </el-table-column>
            <el-table-column align="center" prop="name" label="试卷名称" ></el-table-column>
            <el-table-column align="center" prop="duration" label="考试时长（分）" ></el-table-column>
            <el-table-column align="center" prop="score" label="总分" ></el-table-column>
            <el-table-column align="center" label="查看试卷" >
                <template slot-scope="scope">
                <el-button type="success" @click="viewPaper(scope.row.id)">查看试卷</el-button>
                </template>
            </el-table-column>

            <el-table-column label="操作" align="center" width="360px">
                <template slot-scope="scope">
                    <el-button type="warning" plain
                               @click="openTransferDialog(scope.row)">手动组卷</el-button>
                    <el-button type="primary" plain
                               @click="openQuestionNumDialog(scope.row)">随机组卷</el-button>
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
        <el-dialog title="试卷信息" :visible.sync="dialogFormVisible" width="40%">
            <el-form :model="form" label-width="100px">
                <el-form-item label="课程类型">
                    <el-select clearable v-model="form.courseId"
                               placeholder="请选择课程" style="width: 100%">
                        <el-option v-for="item in courses"
                                   :key="item.id" :label="item.name" :value="item.id">
                            <span>{{ item.name }}</span>
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="试卷名称">
                    <el-input v-model="form.name" autocomplete="off" ></el-input>
                </el-form-item>
                <el-form-item label="考试时长(分)">
                    <el-input v-model="form.duration" autocomplete="off" ></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="handleAdd">确 定</el-button>
            </div>
        </el-dialog>

        <el-dialog title="请输入题型数量" :visible.sync="dialogQuestionNumFormVisible" width="30%">
            <el-form :model="questionNumForm" label-width="100px">
                <el-form-item label="选择题数量：">
                    <el-input v-model="questionNumForm.choiceNum" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="判断题数量：">
                    <el-input v-model="questionNumForm.judgeNum" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="填空题数量：">
                    <el-input v-model="questionNumForm.fillNum" autocomplete="off"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogQuestionNumFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="autoGenerate">确 定</el-button>
            </div>
        </el-dialog>

        <el-dialog title="试卷内容" :visible.sync="dialogQuestionContentVisible" width="60%">
            <el-card>
                <div v-for="(item, index) in questionContent" :key="item.id">
                    <div style="margin: 20px 0">
                        <div style="font-size: 16px; font-weight: bolder">
                            <span>{{index + 1}}. </span>
                            <span>{{item.name}}</span>
                        </div>
                        <div v-if="item.questionType === 1" style="padding-top: 10px">
                            <span>{{item.a}}</span>
                            <span style="padding-left: 50px">{{item.b}}</span>
                            <span style="padding-left: 50px">{{item.c}}</span>
                            <span style="padding-left: 50px">{{item.d}}</span>
                        </div>
                        <div style="padding-top: 10px">
                            <span style="font-weight: bolder">答案： </span>
                            <span>{{item.answer}}</span>
                        </div>
                        <div style="padding-top: 10px" v-if="item.questionType !== 2">
                            <span style="font-weight: bolder">解析： </span>
                            <span>{{item.analysis}}</span>
                        </div>
                    </div>
                </div>
            </el-card>
        </el-dialog>

        <el-dialog title="手动组卷" :visible.sync="dialogHandleQuestionVisible" width="80%">

            <div class="edit_dev">
                <el-transfer
                        filterable
                        :filter-method="filterMethod"
                        filter-placeholder="请输入题目类型，1代表选择题，2代表判断题，3代表填空题"
                        :titles="['题目列表', '已选中']"
                        :props="{key:'id',label: 'name'}"
                        v-model="questionsAfterHandle.questionIds"
                        :data="questions">
                </el-transfer>
            </div>

            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogHandleQuestionVisible = false">取 消</el-button>
                <el-button type="primary" @click="handleGenerate">确 定</el-button>
            </div>
        </el-dialog>

    </div>
</template>

<script>
    export default {
        name: "Paper",
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
                questionNumForm: {choiceNum: 4, judgeNum: 4, fillNum: 2},
                dialogQuestionNumFormVisible: false,
                questionContent: [],
                dialogQuestionContentVisible: false,
                questions: [],   //el-transfer表左边列表展示的数据
                questionsAfterHandle: {},  //el-transfer表右边列表提交的数据
                dialogHandleQuestionVisible: false,
                paper: "",

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
                this.request.get("paper/page",{
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
                this.request.post("/paper/removeBatchByIds",ids).then(res=>{
                    if(res.code===200){
                        this.$message.success("批量删除成功");
                        this.load();
                    }else {
                        this.$message.error("批量删除失败");
                    }
                })
            },
            deleteById(id){
                this.request.delete("paper/delete/" + id).then(res=>{
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
                this.request.post("paper/save",this.form).then(res=>{
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
            openQuestionNumDialog(paper){
                this.questionNumForm.paperId = paper.id;
                this.questionNumForm.courseId = paper.courseId;
                this.dialogQuestionNumFormVisible = true;
            },
            autoGenerate(){
                this.request.post("/paper/autoGenerate/",this.questionNumForm).then(res=>{
                    if(res.code===200){
                        this.$message.success("自动组卷成功");
                        this.dialogQuestionNumFormVisible = false;
                    }else {
                        this.$message.error(res.msg);
                    }
                });
            },
            viewPaper(paperId){
                this.request.get("/paper/findPaperByQuestionIds/" + paperId).then(res=>{
                    if(res.code===200){
                        this.questionContent = res.data;
                        this.dialogQuestionContentVisible = true;
                    }else{
                        this.$message.error(res.msg);
                    }
                });
            },
            //根据当前行的courseId获取question
            openTransferDialog(paper){
                this.paper = paper;
                this.request.get("/question/findQuestionsByCourseId/" + paper.courseId).then(res=>{
                    if(res.code===200){
                        this.questions = res.data;
                        this.dialogHandleQuestionVisible = true;
                    }
                });
            },
            filterMethod(query, item){
                return !query || query == item.questionType;
            },
            handleGenerate(){
                this.questionsAfterHandle.paperId = this.paper.id;
                this.request.post("/paper/handleGenerate", this.questionsAfterHandle).then(res=>{
                    if(res.code===200){
                        this.$message.success("手动组卷成功");
                        this.dialogHandleQuestionVisible = false;
                    }else {
                        this.$message.error(res.msg);
                    }
                });
            },
        }
    }
</script>

<style scoped>.edit_dev >>> .el-transfer-panel {
    width:450px;
}
.headerCellClass{
    background-color: lightgray !important;
}

</style>
