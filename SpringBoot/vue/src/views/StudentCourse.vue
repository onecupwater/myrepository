<template>
    <div>
        <div style="margin-top: 30px">
            <el-button type="primary" style="width: 205px; height: 40px" @click="returnCourseList">返回选课列表</el-button>
            <div style="margin-top: 30px">
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
                    <el-table-column align="center" label="操作">
                        <template slot-scope="scope">
                            <el-popconfirm
                                    confirm-button-text='确定'
                                    cancel-button-text='取消'
                                    icon="el-icon-info"
                                    icon-color="red"
                                    title="您确定取消选课吗？"
                                    @confirm="deleteCourseIdAndStudentId(scope.row.id)"
                            >
                                <el-button type="danger" plain icon="el-icon-delete" slot="reference">取消选课</el-button>
                            </el-popconfirm>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
        </div>
    </div>
</template>

<script>
    export default {
        name: "StudentCourse",
        data(){
            return{
                courses : [],
                user : localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
            }
        },
        created() {
            this.load();
        },
        methods :{
            load(){
                this.request.get("user/showStudentCourse/"+this.user.id).then(res=>{
                    this.courses = res.data;
                });
            },
            deleteCourseIdAndStudentId(courseId){
                this.request.delete("course/deleteCourseIdAndStudentId/"+this.user.id+"/"+courseId)
                    .then(res=>{
                        if(res.code===200){
                            this.$message.success(res.msg);
                            this.load();
                        }else {
                            this.$message.error(res.msg);
                        }
                    });
            },
            returnCourseList(){
                this.$router.push("/course");
            }
        }
    }
</script>

<style>
    .headerCellClass{
        background-color: lightgray !important;
    }
</style>