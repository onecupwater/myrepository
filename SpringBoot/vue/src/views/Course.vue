<template>
    <div>
        <div style="margin: 15px 0px">
            <el-button style="margin-right: 9px" type="primary" icon="el-icon-circle-plus-outline"
                       v-if="user.role!=='ROLE_STUDENT'"
                       @click="handleDialog">新增课程</el-button>
            <el-popconfirm
                    confirm-button-text='确定'
                    cancel-button-text='取消'
                    icon="el-icon-info"
                    icon-color="red"
                    title="您确定批量删除吗？"
                    @confirm="deleteByBatchIds()"
            >
                <el-button type="danger" icon="el-icon-delete"
                           v-if="user.role!=='ROLE_STUDENT'"
                           slot="reference">批量删除</el-button>
            </el-popconfirm>
            <el-input style="width: 200px; margin-left: 564px; margin-top: 6px; margin-right: 10px"
                      placeholder="请输入课程名称" prefix-icon="el-icon-search" v-model="name"></el-input>
            <el-button type="primary" @click="load">搜索</el-button>
            <el-button type="warning" @click="reset">重置</el-button>
        </div>
        <el-table :data="tableData" border stripe header-cell-class-name="headerCellClass"
                  @selection-change="handleSelectionChange">
            <el-table-column align="center" type="selection" width="55"></el-table-column>
            <el-table-column align="center" prop="id" label="ID" width="55"></el-table-column>
            <el-table-column align="center" prop="name" label="课程名称"></el-table-column>
            <el-table-column align="center" prop="credit"  label="学分"></el-table-column>
            <el-table-column align="center" prop="lessonHour"  label="总课时"></el-table-column>
            <el-table-column align="center" prop="isRequired"  label="是否必修"></el-table-column>
            <el-table-column align="center" prop="state" label="是否开课">
                <template slot-scope="scope">
                    <el-tag type="primary" v-if="scope.row.state">开课中</el-tag>
                    <el-tag type="warning" v-if="!scope.row.state">休课</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作" align="center" width="350">
                <template slot-scope="scope">
                    <el-button type="primary" plain
                               v-if="user.role!=='ROLE_TEACHER'"
                               icon="el-icon-thumb"
                               @click="saveStudentIdCourseId(scope.row.id)">选课</el-button>

                    <el-button type="primary" plain
                               v-if="user.role!=='ROLE_STUDENT'"
                               icon="el-icon-thumb"
                               @click="saveTeacherIdCourseId(scope.row.id)">任课</el-button>

                    <el-button type="success" style="margin-right: 8px" plain icon="el-icon-edit"
                               v-if="user.role!=='ROLE_STUDENT'"
                               @click="handleEdit(scope.row)">修改</el-button>

                    <el-popconfirm
                            confirm-button-text='确定'
                            cancel-button-text='取消'
                            icon="el-icon-info"
                            icon-color="red"
                            title="您确定删除吗？"
                            @confirm="deleteById(scope.row.id)"
                    >
                        <el-button type="danger" plain icon="el-icon-delete"
                                   v-if="user.role!=='ROLE_STUDENT'"
                                   slot="reference">删除</el-button>
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
        <div style="text-align: right">
            <el-button type="primary" style="width: 150px; height: 40px; font-size: 15px"
                       v-if="user.role!=='ROLE_STUDENT'"
                       @click="goTeacherCourse">前往'我的任课'</el-button>
        </div>
        <div style="margin-top: 5px; text-align: right">
            <el-button type="primary" style="width: 150px; height: 40px; font-size: 15px"
                       v-if="user.role!=='ROLE_TEACHER'"
                       @click="goStudentCourse">前往'我的选课'</el-button>
        </div>
        <el-dialog title="课程信息" :visible.sync="dialogFormVisible" width="30%">
            <el-form :model="form" label-width="70px">
                <el-form-item label="课程名称">
                    <el-input v-model="form.name" autocomplete="off" ></el-input>
                </el-form-item>
                <el-form-item label="学分">
                    <el-input v-model="form.credit" autocomplete="off" ></el-input>
                </el-form-item>
                <el-form-item label="总课时">
                    <el-input v-model="form.lessonHour" autocomplete="off" ></el-input>
                </el-form-item>
                <el-form-item label="是否必修">
                    <el-radio v-model="form.isRequired" label="是">是</el-radio>
                    <el-radio v-model="form.isRequired" label="否">否</el-radio>
                </el-form-item>
                <el-form-item label="是否开课" >
                    <el-switch
                            v-model="switchValue"
                            active-color="#13ce66"
                            inactive-color="#ff4949">
                    </el-switch>
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
        name: "Course",
        data(){
            return {
                tableData: [],
                icons: [],
                total: 0,
                pageNum: 1,
                pageSize: 10,
                name: "",
                form: [],
                switchValue : true,
                dialogFormVisible: false,
                multipleSelection: [],
                user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},

            }
        },
        created() {
            this.load();
        },
        methods: {
            load(){
                //使用axios来获取后端数据，首先在vue前端项目的目录下安装axios,添加utils/request.js文件，内容可以参考vue项目
                this.request.get("course/page",{
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
                this.request.post("/course/removeBatchByIds",ids).then(res=>{
                    if(res.code===200){
                        this.$message.success("批量删除成功");
                        this.load();
                    }else {
                        this.$message.error("批量删除失败");
                    }
                })
            },
            deleteById(id){
                this.request.delete("course/delete/" + id).then(res=>{
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
                this.switchValue = row.state;
            },
            handleAdd(){
                //点击‘新增课程’按钮触发
                //将switchValue赋值给form，以便一起传到后台
                this.form.state = this.switchValue;
                this.request.post("course/save",this.form).then(res=>{
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
                //重置form
                this.form = {};
                //重置switch开关
                this.switchValue = true;
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
            saveTeacherIdCourseId(courseId){
                this.request.post("course/saveTeacherIdCourseId/"+this.user.id+"/"+courseId)
                    .then(res=>{
                    if(res.code===200){
                        this.$message.success(res.msg);
                    }else {
                        this.$message.error(res.msg);
                    }
                });
            },
            saveStudentIdCourseId(courseId){
                this.request.post("course/saveStudentIdCourseId/"+this.user.id+"/"+courseId)
                    .then(res=>{
                        if(res.code===200){
                            this.$message.success(res.msg);
                        }else {
                            this.$message.error(res.msg);
                        }
                    });
            },
            goTeacherCourse(){
                this.$router.push("/teachercourse");
            },
            goStudentCourse(){
                this.$router.push("/studentcourse");
            }
        }
    }
</script>

<style>
    .headerCellClass{
        background-color: lightgray !important;
    }
</style>