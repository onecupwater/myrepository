<template>
    <div>
        <div style="margin: 15px 0px">
            <el-button style="margin-right: 9px" type="primary" icon="el-icon-circle-plus-outline" @click="handleDialog">新增</el-button>
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
            <el-upload
                    style="display: inline-block"
                    action="http://localhost:9090/user/import"
                    :show-file-list="false"
                    accept=".xlsx"
                    :on-success="handleImportSuccess"
                    :on-error="handleImportError"
                   >
                <el-button type="primary" icon="el-icon-download" style="margin-left: 9px">导入</el-button>
            </el-upload>
            <el-popconfirm
                    confirm-button-text='确定'
                    cancel-button-text='取消'
                    icon="el-icon-info"
                    icon-color="red"
                    title="您确定导出所有数据吗？"
                    @confirm="handleExport"
            >
                <el-button type="primary" icon="el-icon-upload2" slot="reference" style="margin-left: 9px">导出</el-button>
            </el-popconfirm>
            <el-input style="width: 200px; margin-left: 400px; margin-top: 6px; margin-right: 10px"
                      placeholder="请输入姓名" prefix-icon="el-icon-search" v-model="name"></el-input>
            <el-button type="primary" @click="load">搜索</el-button>
            <el-button type="warning" @click="reset">重置</el-button>
        </div>
        <el-table :data="tableData" border stripe header-cell-class-name="headerCellClass"
                  @selection-change="handleSelectionChange">
            <el-table-column align="center" type="selection" width="45"></el-table-column>
            <el-table-column align="center" prop="id" label="ID" width="45"></el-table-column>
            <el-table-column align="center" prop="name" label="用户名" width="70"></el-table-column>
            <el-table-column align="center" prop="nickname" label="昵称" width="70"></el-table-column>
            <!--<el-table-column align="center" prop="role" label="角色" width="100"></el-table-column>-->
            <el-table-column align="center" prop="roleName" label="角色" ></el-table-column>
            <el-table-column align="center" prop="phone" label="电话"></el-table-column>
            <el-table-column align="center" prop="email" label="邮箱" ></el-table-column>
            <el-table-column align="center" prop="address" label="地址" ></el-table-column>
            <el-table-column align="center" prop="createDate" label="创建日期" width="90"></el-table-column>
            <el-table-column align="center" prop="updateDate" label="修改日期" width="90"></el-table-column>
            <el-table-column label="操作" align="center" width="355">
                <template slot-scope="scope">
                    <el-button type="primary" plain v-if="isStudent(scope.row.role)"
                               @click="showStudentCourse(scope.row.id)">查看选课</el-button>
                    <el-button type="warning" plain v-if="isTeacher(scope.row.role)"
                               @click="showTeacherCourse(scope.row.id)">查看任课</el-button>
                    <el-button type="success" style="margin-right: 8px" plain icon="el-icon-edit" @click="handleEdit(scope.row)">修改</el-button>
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
        <el-dialog title="用户信息" :visible.sync="dialogFormVisible" width="30%">
            <el-form :model="form" label-width="70px">
                <el-form-item label="用户名">
                    <el-input v-model="form.name" autocomplete="off" ></el-input>
                </el-form-item>
                <el-form-item label="昵称">
                    <el-input v-model="form.nickname" autocomplete="off" ></el-input>
                </el-form-item>
                <el-form-item label="角色">
                    <!--el-select绑定的还是user表的role，roleName是没有在user表的，
                    value对应role表的flag，也对应user表的role-->
                    <el-select clearable v-model="form.role" placeholder="请选择角色" style="width: 100%">
                        <el-option v-for="item in roles"
                                   :key="item.id" :label="item.name" :value="item.flag">
                            <span>{{ item.name }}</span>
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="电话">
                    <el-input v-model="form.phone" autocomplete="off" ></el-input>
                </el-form-item>
                <el-form-item label="邮箱">
                    <el-input v-model="form.email" autocomplete="off" ></el-input>
                </el-form-item>
                <el-form-item label="地址">
                    <el-input type="textarea" v-model="form.address" autocomplete="off" ></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="handleAdd">确 定</el-button>
            </div>
        </el-dialog>

        <el-dialog title="课程信息" :visible.sync="dialogCourseVisible" width="40%">
            <el-table :data="courses" border stripe header-cell-class-name="headerCellClass">
                <el-table-column align="center" prop="id" label="ID"></el-table-column>
                <el-table-column align="center" prop="name" label="课程名称"></el-table-column>
                <el-table-column align="center" prop="credit" label="学分"></el-table-column>
                <el-table-column align="center" prop="lessonHour" label="总课时"></el-table-column>
                <el-table-column align="center" prop="state" label="是否开课">
                    <template slot-scope="scope">
                        <el-tag type="primary" v-if="scope.row.state">开课中</el-tag>
                        <el-tag type="warning" v-if="!scope.row.state">休课</el-tag>
                    </template>
                </el-table-column>
            </el-table>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "User",
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
                roles : [],
                dialogCourseVisible : false,
                courses :[],
            }
        },
        created() {
            this.load();
            this.isTeacher();
            this.isStudent();

            //获取角色列表
            this.request.get("role/findAll").then(res=>{
                this.roles = res;
            });
        },
        methods: {
            load(){
                //使用axios来获取后端数据，首先在vue前端项目的目录下安装axios,添加utils/request.js文件，内容可以参考vue项目
                this.request.get("user/page",{
                    params: {
                        pageNum: this.pageNum,
                        pageSize: this.pageSize,
                        name: this.name
                    }
                }).then(res=>{
                    this.tableData = res.records;
                    this.total = res.total;
                });
            },
            handleImportError(){
                this.$message.error("上传失败");
                this.load();
            },
            handleImportSuccess(){
                this.$message.success("上传成功");
                this.load();
            },
            handleExport(){
                window.open("http://localhost:9090/user/export");
            },
            handleSelectionChange(val){
                this.multipleSelection = val;
            },
            deleteByBatchIds(){
                let ids = this.multipleSelection.map(value => value.id);
                this.request.post("/user/removeBatchByIds",ids).then(res=>{
                    if(res){
                        this.$message.success("批量删除成功");
                        this.load();
                    }else {
                        this.$message.error("批量删除失败");
                    }
                })
            },
            deleteById(id){
                this.request.delete("user/delete/" + id).then(res=>{
                    if(res){
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
                this.request.post("user/save",this.form).then(res=>{
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
            isTeacher(role){
                return role==="ROLE_TEACHER" || role==="ROLE_ADMIN";
            },
            isStudent(role){
                return role==="ROLE_STUDENT" || role==="ROLE_ADMIN";
            },
            showStudentCourse(studentId){
                this.courses = [];
                this.dialogCourseVisible = true;
                this.request.get("user/showStudentCourse/"+studentId).then(res=>{
                    this.courses = res.data;
                });
            },
            showTeacherCourse(teacherId){
                this.courses = [];
                this.dialogCourseVisible = true;
                this.request.get("user/showTeacherCourse/"+teacherId).then(res=>{
                    this.courses = res.data;
                });
            }
        }
    }
</script>

<style>
    .headerCellClass{
        background-color: lightgray !important;
    }
</style>