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
                    title="您确定批量删除文章吗？"
                    @confirm="deleteByBatchIds()"
            >
                <el-button type="danger" icon="el-icon-delete" slot="reference">批量删除</el-button>
            </el-popconfirm>


            <el-input style="width: 200px; margin-left: 564px; margin-top: 6px; margin-right: 10px"
                      placeholder="请输入标题" prefix-icon="el-icon-search" v-model="name"></el-input>
            <el-button type="primary" @click="load">搜索</el-button>
            <el-button type="warning" @click="reset">重置</el-button>
        </div>
        <el-table :data="tableData" border stripe header-cell-class-name="headerCellClass"
                  @selection-change="handleSelectionChange">

            <el-table-column align="center" type="selection" width="45"></el-table-column>
            <el-table-column align="center" prop="id" label="ID" width="45"></el-table-column>
            <el-table-column align="center" prop="name" label="标题" ></el-table-column>
            <el-table-column align="center" prop="content" label="内容" >
                <template slot-scope="scope">
                    <el-button type="primary" @click="viewContent(scope.row.content)">查看内容</el-button>
                </template>
            </el-table-column>
            <el-table-column align="center" prop="publisher" label="发布人" ></el-table-column>
            <el-table-column align="center" prop="publishTime" label="发布时间" width="90px" ></el-table-column>

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

        <el-dialog title="文章信息" :visible.sync="dialogFormVisible" width="80%">
            <el-form :model="form" label-width="70px">
                <el-form-item label="标题">
                    <el-input v-model="form.name" autocomplete="off" ></el-input>
                </el-form-item>
                <el-form-item label="内容" >
                    <mavon-editor ref="md"
                                  v-model="form.content" :ishljs="true" @imgAdd="imgAdd"/>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="handleAdd">确 定</el-button>
            </div>
        </el-dialog>

        <el-dialog title="文章内容" :visible.sync="dialogArticleVisible" width="60%">
            <mavon-editor
                    class="md"
                    :value="content"
                    :subfield="false"
                    :defaultOpen="'preview'"
                    :toolbarsFlag="false"
                    :editable="false"
                    :scrollStyle="true"
                    :ishljs="true"
            />
        </el-dialog>

    </div>
</template>

<script>
    import axios from "axios";

    export default {
        name: "Article",
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
                content: "",
                dialogArticleVisible : false,

            }
        },
        created() {
            this.load();
        },
        methods: {
            load(){
                //使用axios来获取后端数据，首先在vue前端项目的目录下安装axios,添加utils/request.js文件，内容可以参考vue项目
                this.request.get("article/page",{
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
            viewContent(content){
                this.content = content;
                this.dialogArticleVisible = true;
            },
            imgAdd(pos,$file){
                let $vm = this.$refs.md;
                //第一步，将图片上传到服务器
                const formData = new FormData();
                formData.append('file',$file);
                axios({
                    //url是后台上传文件的接口，必须提前写好
                    url: "http://localhost:9090/file/upload",
                    method: 'post',
                    data: formData,
                    headers: {'Content-Type': 'multipart/form-data'},
                }).then((res)=>{
                    //第二步，将返回的url替换到文本原位置
                    $vm.$img2Url(pos,res.data);
                });
            },
            handleAdd(){
                this.request.post("article/save",this.form).then(res=>{
                    if(res.code===200){
                        this.$message.success("保存成功");
                        this.dialogFormVisible = false;
                        this.load();
                    }else {
                        this.$message.error("保存失败");
                    }
                })
            },
            handleEdit(row){
                this.dialogFormVisible = true;
                this.form = row;
            },
            handleDialog(){
                this.dialogFormVisible = true;
                this.form = {};
            },
            handleSelectionChange(val){
                this.multipleSelection = val;
            },
            deleteByBatchIds(){
                let ids = this.multipleSelection.map(value => value.id);
                this.request.post("/article/removeBatchByIds",ids).then(res=>{
                    if(res.code===200){
                        this.$message.success("批量删除成功");
                        this.load();
                    }else {
                        this.$message.error("批量删除失败");
                    }
                })
            },
            deleteById(id){
                this.request.delete("article/delete/" + id).then(res=>{
                    if(res.code===200){
                        this.$message.success("删除成功");
                        this.load();
                    }else {
                        this.$message.error("删除失败")
                    }
                })
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
        }
    }
</script>

<style>
    .headerCellClass{
        background-color: lightgray !important;
    }
</style>