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
            <el-input style="width: 200px; margin-left: 400px; margin-top: 6px; margin-right: 10px"
                      placeholder="请输入姓名" prefix-icon="el-icon-search" v-model="nickname"></el-input>
            <el-button type="primary" @click="load">搜索</el-button>
            <el-button type="warning" @click="reset">重置</el-button>
        </div>
        <el-table :data="tableData" border stripe header-cell-class-name="headerCellClass"
                  @selection-change="handleSelectionChange">
            <el-table-column align="center" type="selection" width="45"></el-table-column>
            <el-table-column align="center" prop="id" label="ID" width="45"></el-table-column>
            <el-table-column align="center" prop="name" label="学生账号"></el-table-column>
            <el-table-column align="center" prop="nickname" label="学生姓名" width="70"></el-table-column>
            <el-table-column align="center" prop="phone" label="电话"></el-table-column>
            <el-table-column align="center" prop="email" label="邮箱" ></el-table-column>
            <el-table-column align="center" prop="address" label="地址" ></el-table-column>
            <el-table-column align="center" label="专业" >
                <template slot-scope="scope">
                    <span v-if="majors && majors.length">
                    {{majors.find(value => value.id === scope.row.majorId)?
                        majors.find(value => value.id === scope.row.majorId).name : ""}}
                </span>
                </template>
            </el-table-column>
            <el-table-column align="center" label="班级" >
                <template slot-scope="scope">
                    <span v-if="allStuClasses && allStuClasses.length">
                    {{allStuClasses.find(value => value.id === scope.row.stuClassId)?
                        allStuClasses.find(value => value.id === scope.row.stuClassId).name : ""}}
                </span>
                </template>
            </el-table-column>
            <el-table-column align="center" prop="createDate" label="创建日期" width="90"></el-table-column>
            <el-table-column align="center" prop="updateDate" label="修改日期" width="90"></el-table-column>
            <el-table-column label="操作" align="center" width="200">
                <template slot-scope="scope">
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
        <el-dialog title="学生信息" :visible.sync="dialogFormVisible" width="30%">
            <el-form :model="form" label-width="70px">
                <el-form-item label="学生账号">
                    <el-input v-model="form.name" autocomplete="off" ></el-input>
                </el-form-item>
                <el-form-item label="昵称">
                    <el-input v-model="form.nickname" autocomplete="off" ></el-input>
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
                <el-form-item label="专业">
                    <el-select clearable v-model="form.majorId" placeholder="请选择专业" style="width: 100%">
                        <el-option v-for="item in majors"
                                   :key="item.id" :label="item.name" :value="item.id">
                            <span>{{ item.name }}</span>
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="班级">
                    <el-select clearable
                               v-model="form.stuClassId"
                               placeholder="请选择班级"
                               @focus="selectStuClassByMajor(form.majorId)"
                               style="width: 100%">
                        <el-option v-for="item in diffStuClasses"
                                   :key="item.id" :label="item.name" :value="item.id">
                            <span>{{ item.name }}</span>
                        </el-option>
                    </el-select>
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
        name: "Student",
        data(){
            return {
                tableData: [],
                total: 0,
                pageNum: 1,
                pageSize: 10,
                nickname: "",
                form: [],
                dialogFormVisible: false,
                multipleSelection: [],
                roles : [],
                dialogCourseVisible : false,
                courses :[],
                majors: [],
                allStuClasses: [],
                diffStuClasses: [],
            }
        },
        created() {
            this.load();
        },
        methods: {
            load(){
                //使用axios来获取后端数据，首先在vue前端项目的目录下安装axios,添加utils/request.js文件，内容可以参考vue项目
                this.request.get("user/findStudentPage",{
                    params: {
                        pageNum: this.pageNum,
                        pageSize: this.pageSize,
                        nickname: this.nickname
                    }
                }).then(res=>{
                    this.tableData = res.data.records;
                    this.total = res.data.total;
                });
                //找出所有专业
                this.request.get("major").then(res=>{
                    if(res.code===200){
                        this.majors = res.data;
                    }
                });
                //找出所有班级
                this.request.get("stuClass").then(res=>{
                    if(res.code===200){
                        this.allStuClasses = res.data;
                    }
                });
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
                //防止回显数据时，班级显示的还是stuClassId
                this.request.get("stuClass/findStuClassesByMajorId/" + row.majorId).then(res=>{
                    if(res.code===200){
                        this.diffStuClasses = res.data;
                    }
                });
            },
            handleAdd(){
                if(!this.form.majorId){
                    this.$message.warning("请选择专业");
                    return;
                }
                if(!this.form.stuClassId){
                    this.$message.warning("请选择班级");
                    return;
                }
                this.form.role = "ROLE_STUDENT";
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
                this.form = {}; //清空表单
                this.diffStuClasses = [];  //清空stuClassId下拉框
                this.dialogFormVisible = true;
            },
            reset(){
                this.nickname = '';
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
            selectStuClassByMajor(majorId){
                this.typeStuClasses = [];
                if(!majorId){
                   this.$message.warning("请先选择专业！");
                   return;
                }
                this.request.get("stuClass/findStuClassesByMajorId/" + majorId).then(res=>{
                    if(res.code===200){
                        this.diffStuClasses = res.data;
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