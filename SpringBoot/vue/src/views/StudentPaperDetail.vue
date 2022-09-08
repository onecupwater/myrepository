<template>
    <div>
        <el-table :data="tableData" border stripe header-cell-class-name="headerCellClass">

            <el-table-column align="center" prop="id" label="题目ID" width="45"></el-table-column>
            <el-table-column align="center" prop="name" label="题目" ></el-table-column>
            <el-table-column align="center" prop="a" label="选项A" ></el-table-column>
            <el-table-column align="center" prop="b" label="选项B" ></el-table-column>
            <el-table-column align="center" prop="c" label="选项C" ></el-table-column>
            <el-table-column align="center" prop="d" label="选项D"></el-table-column>
            <el-table-column align="center" prop="answer" label="标准答案" ></el-table-column>
            <el-table-column align="center" prop="analysis" label="解析"></el-table-column>
            <el-table-column align="center" prop="score" label="标准分数"></el-table-column>
            <el-table-column align="center" prop="studentAnswer" label="学生答案"></el-table-column>
            <el-table-column align="center" label="学生得分">
                <template slot-scope="scope">
                    <el-input type="text" v-model="scope.row.studentScore"></el-input>
                </template>
            </el-table-column>
        </el-table>
        <div style="text-align: right; padding-top: 20px; padding-bottom: 50px">
            <el-button type="primary" @click="submitScore">评分完成</el-button>
        </div>
    </div>
</template>

<script>
    export default {
        name: "StudentPaperDetail",
        data(){
            return{
                studentPaperId: this.$route.query.studentPaperId,
                tableData: [],
                form: {},
            }
        },
        created() {
            this.load();
        },
        methods:{
            load(){
                this.request.get("/studentPaper/" + this.studentPaperId).then(res=>{
                    if(res.code===200){
                        //不要理解成一张试卷是一个对象，理解成一道题是一个对象，
                        // 这样，tableData能理所应当的是集合了
                        this.tableData = JSON.parse(res.data.paperContent);
                        //自动评分
                        this.tableData.forEach(item=>{
                            if(item.studentAnswer === item.answer){
                                item.studentScore = item.score;
                            }
                        });
                    }
                });
            },
            submitScore(){
                let total = 0;
                this.tableData.forEach(item=>{
                    //得分为空的行记录，设置分数等于0
                    if(!item.studentScore){
                        item.studentScore = 0;
                    }
                    total = parseInt(total) + parseInt(item.studentScore);
                });
                //往student_paper表为score赋值
                this.form.id = this.studentPaperId;
                this.form.score = total;
                this.request.post("/studentPaper/save", this.form).then(res=>{
                    if(res.code===200){
                        this.$message.success("评分成功");
                        //跳转阅卷列表页面
                        this.$router.push("/studentPaper");
                    }
                });
            },
        }
    }
</script>

<style scoped>

</style>