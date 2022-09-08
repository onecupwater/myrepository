<template>
    <div style="padding-top: 30px; padding-bottom: 100px; padding-left: 207px; padding-right: 210px">
        <div style="display: flex">
            <!--el-card循环的空间-->
            <div>
                <div v-for="item in tableData" :key="item.id" style="width: 850px">
                    <el-card>
                        <div style="font-size: 16px; font-weight: bolder; padding-bottom: 10px">
                            {{item.name}}
                        </div>
                        <div>
                            <span style="color: #8c939d; padding-right: 20px">
                                监考老师：{{item.teacher}}
                            </span>
                            <span style="color: #8c939d; padding-right: 20px">
                                考试时间：{{item.startTime}}
                            </span>
                            <span style="color: #8c939d">
                                考试状态：{{item.state}}
                            </span>
                        </div>
                        <div style="padding-top: 10px">
                            <el-button  plain type="primary" @click="sign(item.id)">报名</el-button>
                            <el-button  plain type="primary"
                                        v-if="item.enable && item.state === '进行中'"
                                        @click="doPaper(item.id)">开始考试</el-button>
                        </div>
                    </el-card>
                    <div style="padding: 5px 0"></div>
                </div>
            </div>
            <!--下拉框的空间-->
            <div style="width:173px; text-align: right; padding-left: 10px">
                <el-form v-model="courses" >
                    <el-form-item>
                        <el-select clearable
                                   v-model="courses.id"
                                   @change="selectPaper"
                                   size="big"
                                   placeholder="筛选考试"
                                   style="width: 100%;">
                            <el-option v-for="item in courses"
                                       :key="item.id" :label="item.name" :value="item.id">
                                <span>{{ item.name }}</span>
                            </el-option>
                        </el-select>
                    </el-form-item>
                </el-form>
            </div>
        </div>

        <div style="padding: 10px 0">
            <el-pagination
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                    :current-page="pageNum"
                    :page-sizes="[2, 5, 10, 15, 20]"
                    :page-size="pageSize"
                    layout="total, prev, pager, next"
                    :total="total">
            </el-pagination>
        </div>
    </div>
</template>

<script>
    export default {
        name: "Exam",
        data(){
            return{
                tableData: [],
                total: 0,
                pageNum: 1,
                pageSize: 5,
                name: "",
                courses: [],
                user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
                form: {},
            }
        },
        created() {
            this.load();
        },
        methods:{
            load(){
                //使用axios来获取后端数据，首先在vue前端项目的目录下安装axios,添加utils/request.js文件，内容可以参考vue项目
                this.request.get("exam/findPageFront",{
                    params: {
                        pageNum: this.pageNum,
                        pageSize: this.pageSize,
                        name: this.name,
                    }
                }).then(res=>{
                    this.tableData = res.data.records;
                    this.total = res.data.total;
                });

                this.request.get("/course").then(res=>{
                    if(res.code===200){
                        this.courses = res.data;
                    }
                });
            },
            handleSizeChange(pageSize){
                this.pageSize = pageSize;
                this.load();
            },
            handleCurrentChange(pageNum){
                this.pageNum = pageNum;
                this.load();
            },
            //下拉框出现鼠标选中时，val是被选中的值
            selectPaper(courseId){
                this.request.get("exam/findPageFront",{
                    params: {
                        pageNum: this.pageNum,
                        pageSize: this.pageSize,
                        name: this.name,
                        courseId: courseId,
                    }
                }).then(res=>{
                    this.tableData = res.data.records;
                    this.total = res.data.total;
                });
            },
            sign(examId){
                if(!this.user.id){
                    this.$message.error("请登录后再报名");
                    return;
                }
                this.form.userId = this.user.id;
                this.form.examId = examId;
                //避免重复报名
                this.request.post("sign/findSign", this.form).then(res=>{
                    if(res.code===200){
                        if(res.data.length){
                            this.$message.error("您已经报名，请不要重复报名");
                        }else{
                            //插入报名数据
                            this.request.post("sign/save", this.form).then(res=>{
                                if(res.code===200){
                                    this.$message.success("报名成功");
                                }else {
                                    this.$message.error(res.msg);
                                }
                            });
                        }
                    }
                });
            },
            doPaper(examId){
                this.$router.push({path:'/front/paperDetail',query:{'examId':examId}})
            },
        }
    }
</script>

<style scoped>

</style>