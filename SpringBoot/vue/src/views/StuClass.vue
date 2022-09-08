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
            <el-table-column align="center" prop="name" label="班级名称" ></el-table-column>
            <el-table-column align="center" prop="majorId" label="专业" >
                <template slot-scope="scope">
                    <span v-if="majors && majors.length">
                        {{majors.find(value => value.id === scope.row.majorId) ?
                        majors.find(value => value.id === scope.row.majorId).name : ""}}
                    </span>
                </template>
            </el-table-column>
            <el-table-column align="center" prop="room" label="教室" ></el-table-column>
            <el-table-column align="center" label="任课老师" >
                <template slot-scope="scope">
                    <span v-if="teachers && teachers.length">
                        {{teachers.find(value => value.id === scope.row.teacherId) ?
                        teachers.find(value => value.id === scope.row.teacherId).nickname: ""}}
                    </span>
                </template>
            </el-table-column>
            <el-table-column align="center" prop="stuNum" label="学生人数" ></el-table-column>
            <el-table-column align="center" label="设置课程" width="150">
                <template slot-scope="scope">
                    <el-button type="primary" @click="openCourseDialog(scope.row)">设置课程</el-button>
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
        <el-dialog title="班级信息" :visible.sync="dialogFormVisible" width="30%">
            <el-form :model="form" label-width="70px">
                <el-form-item label="名称">
                    <el-input v-model="form.name" autocomplete="off" ></el-input>
                </el-form-item>
                <el-form-item label="专业">
                    <template slot-scope="scope">
                        <el-select clearable v-model="form.majorId" placeholder="请选择专业" style="width: 100%">
                            <el-option v-for="item in majors"
                                       :key="item.id" :label="item.name" :value="item.id">
                                <span>{{ item.name }}</span>
                            </el-option>
                        </el-select>
                    </template>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="handleAdd">确 定</el-button>
            </div>
        </el-dialog>

        <el-dialog title="课程信息" :visible.sync="dialogFormCourseVisible" width="80%">
            <div style="padding-bottom: 10px">
                <el-button type="primary"
                           icon="el-icon-circle-plus-outline"
                           @click="addCourse">新增课程
                </el-button>
            </div>
            <el-table :data="tableCourse" border stripe header-cell-class-name="headerCellClass">
                <el-table-column align="center" label="班级名称">
                    <template slot-scope="scope">
                        <span v-if="stuClasses && stuClasses.length">
                            {{stuClasses.find(value => value.id === scope.row.stuClassId) ?
                            stuClasses.find(value => value.id === scope.row.stuClassId).name : ""}}
                        </span>
                    </template>
                </el-table-column>
                <el-table-column align="center" label="课程名称">
                    <template slot-scope="scope">
                        <el-select clearable v-model="scope.row.courseId" placeholder="请选择课程" style="width: 100%">
                            <el-option v-for="item in courses"
                                       :key="item.id" :label="item.name" :value="item.id">
                                <span>{{ item.name }}</span>
                            </el-option>
                        </el-select>
                    </template>
                </el-table-column>
                <el-table-column align="center" label="星期几">
                    <template slot-scope="scope">
                        <el-select clearable v-model="scope.row.weekDay" placeholder="请选择工作日" style="width: 100%">
                            <el-option v-for="item in weekDay"
                                       :key="item" :label="item" :value="item">
                            </el-option>
                        </el-select>
                    </template>
                </el-table-column>
                <el-table-column align="center" label="第几大节">
                    <template slot-scope="scope">
                        <el-select clearable v-model="scope.row.section" placeholder="请选择第几大节" style="width: 100%">
                            <el-option v-for="item in section"
                                       :key="item" :label="item" :value="item">
                            </el-option>
                        </el-select>
                    </template>
                </el-table-column>
                <el-table-column align="center" label="任课老师">
                    <template slot-scope="scope">
                        <el-select clearable v-model="scope.row.teacherId" placeholder="请选择任课老师" style="width: 100%">
                            <el-option v-for="item in teachers"
                                       :key="item.id" :label="item.nickname" :value="item.id">
                            </el-option>
                        </el-select>
                    </template>
                </el-table-column>
                <el-table-column align="center" label="教室">
                    <template slot-scope="scope">
                        <el-input v-model="scope.row.room" placeholder="请输入教室号" autocomplete="off"></el-input>
                    </template>
                </el-table-column>
                <!--操作-->
                <el-table-column align="center" label="操作">
                    <template slot-scope="scope">
                        <el-button type="danger" @click="removeCurrentCourse(scope.row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormCourseVisible = false">取 消</el-button>
                <el-button type="primary" @click="submitCourses">确 定</el-button>
            </div>
        </el-dialog>

    </div>
</template>

<script>
    export default {
        name: "StuClass",
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
                majors: [],
                tableCourse: [],
                dialogFormCourseVisible: false,
                stuClass: {},
                courses: [],
                weekDay: ["星期一","星期二","星期三","星期四","星期五","星期六","星期日"],
                section: ["第一大节","第二大节","第三大节","第四大节","第五大节"],
                stuClasses: [],
                teachers: [],
            }
        },
        created() {
            this.load();
        },
        methods: {
            load(){
                //使用axios来获取后端数据，首先在vue前端项目的目录下安装axios,添加utils/request.js文件，内容可以参考vue项目
                this.request.get("stuClass/page",{
                    params: {
                        pageNum: this.pageNum,
                        pageSize: this.pageSize,
                        name: this.name
                    }
                }).then(res=>{
                    this.tableData = res.data.records;
                    this.total = res.data.total;
                });
                //获取所有专业
                this.request.get("major").then(res=>{
                    if(res.code===200){
                        this.majors = res.data;
                    }
                });
                //获取所有必修课程
                this.request.get("course").then(res=>{
                    if(res.code===200){
                        this.courses = res.data.filter(v => v.isRequired === "是");
                    }
                });
                //获取所有班级
                this.request.get("stuClass").then(res=>{
                    if(res.code===200){
                        this.stuClasses = res.data;
                    }
                });
                //获取所有老师
                this.request.get("user").then(res=>{
                    this.teachers = res.filter(value => value.role === "ROLE_TEACHER");
                });
            },
            handleSelectionChange(val){
                this.multipleSelection = val;
            },
            deleteByBatchIds(){
                let ids = this.multipleSelection.map(value => value.id);
                this.request.post("/stuClass/removeBatchByIds",ids).then(res=>{
                    if(res.code===200){
                        this.$message.success("批量删除成功");
                        this.load();
                    }else {
                        this.$message.error("批量删除失败");
                    }
                })
            },
            deleteById(id){
                this.request.delete("stuClass/delete/" + id).then(res=>{
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
                if(!this.form.majorId){
                    this.$message.warning("请选择专业");
                    return;
                }
                this.request.post("stuClass/save",this.form).then(res=>{
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
            openCourseDialog(stuClass){
                this.stuClass = stuClass;
                //保存过一次数据后，再次打开对话框，可以回显之前保存的数据
                this.request.get("stuClass/findStuClassCourseByStuClassId/" + stuClass.id).then(res=>{
                    if(res.code===200){
                        this.tableCourse = res.data;
                    }
                });
                this.dialogFormCourseVisible = true;
            },
            addCourse(){
                this.tableCourse.push({stuClassId: this.stuClass.id,
                    stuClassName: this.stuClass.name});
            },
            removeCurrentCourse(currentCourse){
                //从tableCourse中删掉当前行对象，先找当前行的下标，也就是索引，集合的索引是从0开始算的
                // 再调用splice方法删除，索引第一个参数是从哪个索引开始删除，
                // 如果没有第二个参数，那就是删到最后，有第二个参数，那就是从哪个索引开始删除，删多少个
                let currentCourseIndex = this.tableCourse
                    .findIndex(value => value === currentCourse);
                this.tableCourse.splice(currentCourseIndex,1);
            },
            submitCourses(){
                //对本次提交的tableCourse检验是否有重复
                //先把tableCourse的每个对象的属性组成字符串，有多少个对象就有多少个字符串
                //然后用new Set对这些字符串进行去重，
                // 去重后的Set的size如果不等于原本tableCourse集合的length，
                // 就说明本次提交的tableCourse有重复，可以提示用户
                if(new Set(this.tableCourse
                    .map(value => value.weekDay + value.section)).size
                    !== this.tableCourse.length){
                    this.$message.warning("课程安排时间出现冲突！");
                    return;
                }
                //往stuClassCourse表写入数据，为什么还要传stuClassId呢？
                // 因为允许用户保存空数据进数据库，因为stuClassCourse关系表的后台逻辑是先删后增，
                // 如果不允许用户保存空数据进数据库的话，会导致用户想删掉班级的所有课程时，永远都无法删掉，
                // 因为stuClassCourse表的删除和写入，都在一个dialog对话框里完成，
                // 当用户保存空数据时，后端根据先删后增的逻辑，会删掉之前的所有数据，再保存空数据，
                // 但是由于用户保存的是空数据，会导致后端无法拿到stuClassId，再根据stuClassId去删除，
                // 所以才要给后端传stuClassId
                this.request.post("stuClass/saveFromStuClassCourse/" + this.stuClass.id, this.tableCourse).then(res=>{
                    if(res.code===200){
                        this.$message.success("保存成功");
                        this.dialogFormCourseVisible = false;
                    }else{
                        this.$message.error(res.msg);
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