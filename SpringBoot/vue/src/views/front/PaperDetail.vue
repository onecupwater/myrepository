<template>
    <div style="padding-top: 30px; padding-bottom: 100px; padding-left: 207px; padding-right: 210px">
        <div>
            <!--考试信息-->
            <div style="padding-bottom: 10px">
                <el-card style="padding: 0 20px">
                    <div style="text-align: center;font-size: 20px; font-weight: bolder">{{exam.name}}</div>
                    <div style="padding-top: 15px">
                        <span style="padding-right: 180px; color: #8c939d">
                            监考老师：{{exam.teacher}}
                        </span>
                        <span style="padding-right: 180px; color: #8c939d">
                            考试开始时间：{{exam.startTime}}
                        </span>
                        <span style="color: #8c939d">考试时长：{{paper.duration}} 分钟</span>
                    </div>
                </el-card>
            </div>
            <!--试卷内容-->
            <el-card style="padding: 0 20px">
                <div v-for="(item, index) in questionContent" :key="item.id">
                    <div style="margin: 20px 0">
                        <div style="font-size: 16px; font-weight: bolder">
                            <span>{{index + 1}}. </span>
                            <span>{{item.name}}</span>
                        </div>
                        <div style="font-size: 20px">
                            <div v-if="item.questionType === 1" style="padding-top: 10px">
                                <span><el-radio v-model="item.studentAnswer" label="A">{{item.a}}</el-radio></span>
                                <span style="padding-left: 50px">
                                <el-radio v-model="item.studentAnswer" label="B">{{item.b}}</el-radio>
                            </span>
                                <span style="padding-left: 50px">
                                <el-radio v-model="item.studentAnswer" label="C">{{item.c}}</el-radio>
                            </span>
                                <span style="padding-left: 50px">
                                <el-radio v-model="item.studentAnswer" label="D">{{item.d}}</el-radio>
                            </span>
                            </div>
                            <div v-if="item.questionType === 2" style="padding-top: 10px">
                            <span>
                                <el-radio v-model="item.studentAnswer" label="对">对</el-radio>
                            </span>
                                <span style="padding-left: 50px">
                                <el-radio v-model="item.studentAnswer" label="错">错</el-radio>
                            </span>
                            </div>
                            <div v-if="item.questionType === 3" style="padding-top: 10px">
                                <el-input type="textarea" v-model="item.studentAnswer"></el-input>
                            </div>
                        </div>
                        <!--<div style="padding-top: 10px">
                            <span style="font-weight: bolder">答案： </span>
                            <span>{{item.answer}}</span>
                        </div>-->
                        <!--<div style="padding-top: 10px" v-if="item.questionType !== 2">
                            <span style="font-weight: bolder">解析： </span>
                            <span>{{item.analysis}}</span>
                        </div>-->
                    </div>
                </div>
                <!--提交试卷-->
                <div style="padding:20px 0; text-align: center">
                    <el-button size="big" type="primary" @click="submitPaper">提 交</el-button>
                </div>
            </el-card>
        </div>
    </div>
</template>

<script>
    export default {
        name: "PaperDetail",
        data(){
            return{
                examId: this.$route.query.examId,
                exam: {},
                paper: {},
                questionContent: [],
                form: {},
            }
        },
        created() {
            this.findExam();
        },
        methods:{
            findExam(){
                //获取考试信息
                this.request.get("exam/" + this.examId).then(res=>{
                    this.exam = res.data;
                    if(res.code===200){
                        //获取试卷内容
                        this.request.get("/paper/findPaperByQuestionIds/" + res.data.paperId).then(res=>{
                            if(res.code===200){
                                this.questionContent = res.data;
                            }else{
                                this.$message.error(res.msg);
                            }
                        });
                        //获取试卷信息
                        this.request.get("paper/" + res.data.paperId).then(res=>{
                            if(res.code===200){
                                this.paper = res.data;
                            }
                        });
                    }
                });
            },
            submitPaper(){
                this.form.paperContent = JSON.stringify(this.questionContent);
                this.form.examId = this.examId;
                this.request.post("/studentPaper/save", this.form).then(res=>{
                    if(res.code===200){
                        this.$message.success("提交成功");
                    }else {
                        this.$message.error(res.msg);
                    }
                });
            }
        }
    }
</script>

<style scoped>

</style>