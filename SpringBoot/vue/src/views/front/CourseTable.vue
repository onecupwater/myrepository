<template>
    <div style="padding-top: 70px; padding-left: 207px; padding-right: 210px">
        <div v-if="!user.majorId">
            <h1 style="text-align: center">您不是学生，没有课程表</h1>
        </div>

        <!--展示课程表-->
        <div v-if="user.majorId && user.stuClassId">
            <h3 style="padding-bottom: 20px">
                您的专业：{{majorName}}
            </h3>

            <el-table :data="tableData" border stripe header-cell-class-name="headerCellClass">
                <el-table-column label="节数/时间" align="center">
                    <template slot-scope="scope">
                        <div>{{scope.row.sectionTime.section}}</div>
                        <div>{{scope.row.sectionTime.time}}</div>
                    </template>
                </el-table-column>

                <el-table-column label="星期一" align="center">
                    <template slot-scope="scope">
                        <div v-if="scope.row.Monday">
                            <h3 style="padding-top: 10px">{{scope.row.Monday.courseName}}</h3>
                            <div style="color: #8c939d">{{scope.row.Monday.room}}</div>
                            <div style="color: #8c939d">{{scope.row.Monday.teacher}}</div>
                        </div>
                    </template>
                </el-table-column>

                <el-table-column label="星期二" align="center">
                    <template slot-scope="scope">
                        <div v-if="scope.row.Tuesday">
                            <h3 style="padding-top: 10px">{{scope.row.Tuesday.courseName}}</h3>
                            <div style="color: #8c939d">{{scope.row.Tuesday.room}}</div>
                            <div style="color: #8c939d">{{scope.row.Tuesday.teacher}}</div>
                        </div>
                    </template>
                </el-table-column>

                <el-table-column label="星期三" align="center">
                    <template slot-scope="scope">
                        <div v-if="scope.row.Wednesday">
                            <h3 style="padding-top: 10px">{{scope.row.Wednesday.courseName}}</h3>
                            <div style="color: #8c939d">{{scope.row.Wednesday.room}}</div>
                            <div style="color: #8c939d">{{scope.row.Wednesday.teacher}}</div>
                        </div>
                    </template>
                </el-table-column>

                <el-table-column label="星期四" align="center">
                    <template slot-scope="scope">
                        <div v-if="scope.row.Thursday">
                            <h3 style="padding-top: 10px">{{scope.row.Thursday.courseName}}</h3>
                            <div style="color: #8c939d">{{scope.row.Thursday.room}}</div>
                            <div style="color: #8c939d">{{scope.row.Thursday.teacher}}</div>
                        </div>
                    </template>
                </el-table-column>

                <el-table-column label="星期五" align="center">
                    <template slot-scope="scope">
                        <div v-if="scope.row.Friday">
                            <h3 style="padding-top: 10px">{{scope.row.Friday.courseName}}</h3>
                            <div style="color: #8c939d">{{scope.row.Friday.room}}</div>
                            <div style="color: #8c939d">{{scope.row.Friday.teacher}}</div>
                        </div>
                    </template>
                </el-table-column>

                <el-table-column label="星期六" align="center">
                    <template slot-scope="scope">
                        <div v-if="scope.row.Saturday">
                            <h3 style="padding-top: 10px">{{scope.row.Saturday.courseName}}</h3>
                            <div style="color: #8c939d">{{scope.row.Saturday.room}}</div>
                            <div style="color: #8c939d">{{scope.row.Saturday.teacher}}</div>
                        </div>
                    </template>
                </el-table-column>

                <el-table-column label="星期日" align="center">
                    <template slot-scope="scope">
                        <div v-if="scope.row.Sunday">
                            <h3 style="padding-top: 10px">{{scope.row.Sunday.courseName}}</h3>
                            <div style="color: #8c939d">{{scope.row.Sunday.room}}</div>
                            <div style="color: #8c939d">{{scope.row.Sunday.teacher}}</div>
                        </div>
                    </template>
                </el-table-column>

            </el-table>
        </div>

    </div>
</template>

<script>
    export default {
        name: "CourseTable",
        data(){
            return{
                user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
                tableData: [
                    /*{"sectionTime":{"section": "第一大节", "time": "08:00-09:35"},
                        "Monday":{"courseName": "大学物理", "room": "A-101","teacher": "张之洞"},
                        "Tuesday":{"courseName": "大学化学", "room": "A-103","teacher": "邓小平"},
                        "Thursday":{"courseName": "高等数学", "room": "A-104","teacher": "刘一桃"},
                    },
                    {"sectionTime":{"section": "第二大节", "time": "10:05-11:40"},
                        "Tuesday":{"courseName": "大学物理", "room": "A-101","teacher": "张之洞"},
                        "Friday":{"courseName": "大学化学", "room": "A-103","teacher": "邓小平"},
                        "Thursday":{"courseName": "高等数学", "room": "A-104","teacher": "刘一桃"},
                    },
                    {"sectionTime":{"section": "第三大节", "time": "14:00-15:35"},
                        "":{"courseName": "大学物理", "room": "A-101","teacher": "张之洞"},
                        "Tuesday":{"courseName": "大学化学", "room": "A-103","teacher": "邓小平"},
                        "Thursday":{"courseName": "高等数学", "room": "A-104","teacher": "刘一桃"},
                    },
                    {"sectionTime":{"section": "第四大节", "time": "16:05-17:40"},
                        "Wednesday":{"courseName": "大学物理", "room": "A-101","teacher": "张之洞"},
                        "Friday":{"courseName": "大学化学", "room": "A-103","teacher": "邓小平"},
                        "Sunday":{"courseName": "高等数学", "room": "A-104","teacher": "刘一桃"},
                    },
                    {"sectionTime":{"section": "第五大节", "time": "19:00-20:35"},
                        "Monday":{"courseName": "大学物理", "room": "A-101","teacher": "张之洞"},
                        "Saturday":{"courseName": "大学化学", "room": "A-103","teacher": "邓小平"},
                        "Thursday":{"courseName": "高等数学", "room": "A-104","teacher": "刘一桃"},
                    },*/
                    ],
                majorName: "",
            }
        },
        created() {
            this.request.get("/major/" + this.user.majorId).then(res=>{
                if(res.code===200){
                    this.majorName = res.data.name;
                }
            });
            this.request.get("/stuClass/getCourseTableByStuClassId/" + this.user.stuClassId).then(res=>{
                if(res.code===200){
                    this.tableData = res.data;
                }
            });
        },
    }
</script>

<style>
    .headerCellClass{
        background-color: lightgray !important;
    }
</style>