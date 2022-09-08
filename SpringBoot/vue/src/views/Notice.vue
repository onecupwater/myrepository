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
                    title="您确定批量删除公告吗？"
                    @confirm="deleteByBatchIds()"
            >
                <el-button type="danger" icon="el-icon-delete" slot="reference">批量删除</el-button>
            </el-popconfirm>

            <el-input style="width: 200px; margin-left: 530px; margin-top: 6px; margin-right: 10px"
                      placeholder="请输入名称" prefix-icon="el-icon-search" v-model="name"></el-input>
            <el-button type="primary" @click="load">搜索</el-button>
            <el-button type="warning" @click="reset">重置</el-button>
        </div>
        <el-table :data="tableData" border stripe header-cell-class-name="headerCellClass"
                  @selection-change="handleSelectionChange">

            <el-table-column align="center" type="selection" width="45"></el-table-column>
            <el-table-column align="center" prop="id" label="ID" width="45"></el-table-column>
            <el-table-column align="center" prop="name" label="标题" ></el-table-column>
            <el-table-column align="center" label="内容" >
                <template slot-scope="scope">
                    <el-button type="primary"
                               @click="viewContent(scope.row.content)">查看内容</el-button>
                </template>
            </el-table-column>
            <el-table-column align="center" prop="nickname" label="发布人" ></el-table-column>
            <el-table-column align="center" prop="time" label="发布时间" width="90px" ></el-table-column>

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

        <!--新增和编辑的对话框-->
        <el-dialog title="公告信息" :visible.sync="dialogFormVisible" width="70%">
            <el-form :model="form" label-width="70px">
                <el-form-item label="标题">
                    <el-input v-model="form.name" autocomplete="off" ></el-input>
                </el-form-item>
                <el-form-item label="内容">
                    <!--wangEditor富文本编辑器-->
                    <div style="border: 1px solid #ccc;">
                        <Toolbar
                                style="border-bottom: 1px solid #ccc"
                                :editor="editor"
                                :defaultConfig="toolbarConfig"
                                :mode="mode"
                        />
                        <Editor
                                style="height: 300px; overflow-y: hidden;"
                                v-model="form.content"
                                :defaultConfig="editorConfig"
                                :mode="mode"
                                @onCreated="onCreated"
                        />
                    </div>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="handleAdd">确 定</el-button>
            </div>
        </el-dialog>

        <!--显示内容的对话框-->
        <el-dialog title="内容" :visible.sync="dialogContentVisible" width="70%">
            <Editor
                    style="height: 500px; overflow-y: hidden;"
                    v-model="content"
            />
        </el-dialog>

    </div>
</template>

<script>

    import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
    import axios from "axios";

    export default {
        name: "Notice",
        components: {
            Editor,
            Toolbar
        },
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
                dialogContentVisible: false,
                /*富文本编辑器属性*/
                editor: null,
                // html: '', html是官方的属性，再这里我把这个属性放进form里，从Editor的v-model可以看出来
                toolbarConfig: {
                    //可以在着里面去掉一些功能按钮，如全屏按钮
                },
                editorConfig: {
                    placeholder: '请输入内容...',
                    MENU_CONF: {
                        //上传图片按钮所需的属性
                        uploadImage:{
                            //如果后台的显示图片的接口返回值能满足wangeditor要求的格式，
                            // 直接在server属性写上接口地址，而不需要customUpload属性
                            //server: "",
                            maxNumberOfFiles: 1,
                            customUpload: this.pictureUpload,
                        },
                        //上传视频按钮所需的属性,记得调视频的大小1440，800
                        uploadVideo:{
                            maxNumberOfFiles: 1,
                            customUpload: this.videoUpload,
                        },
                    }
                },
                mode: 'default', // or 'simple'
            }
        },
        created() {
            this.load();
        },
        methods: {
            load(){
                //使用axios来获取后端数据，首先在vue前端项目的目录下安装axios,添加utils/request.js文件，内容可以参考vue项目
                this.request.get("notice/page",{
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
                this.request.post("/notice/removeBatchByIds",ids).then(res=>{
                    if(res.code===200){
                        this.$message.success("批量删除成功");
                        this.load();
                    }else {
                        this.$message.error("批量删除失败");
                    }
                })
            },
            deleteById(id){
                this.request.delete("notice/delete/" + id).then(res=>{
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
                this.request.post("notice/save",this.form).then(res=>{
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
            //显示内容的方法
            viewContent(content){
                this.content = content;
                this.dialogContentVisible = true;
            },
            //富文本编辑器上传图片的方法
            pictureUpload(file,insertFn){
                //对图片大小进行限制
                if(file.size > 5 * 1024 * 1024){
                    return this.$message.warning('上传的图片或视频大小不超过5M');
                }
                //axios请求
                let formData = new FormData();
                formData.append('file',file);
                axios({
                    //url是后台上传文件的接口，必须提前写好
                    url: "http://localhost:9090/file/upload",
                    method: 'post',
                    data: formData,
                    headers: {'Content-Type': 'multipart/form-data'},
                }).then(res=>{
                    this.$message.success("图片上传成功");
                    //自定义插入图片
                    //如果不想用这种方式插入图片，那就要改后端显示或者下载图片的接口的返回值了，
                    // 官方规定res的格式必须是如下：
                    /*{
                        "errno": 0, // 注意：值是数字，不能是字符串
                        "data": {
                            "url": "xxx", // 图片 src ，必须
                            "alt": "yyy", // 图片描述文字，非必须
                            "href": "zzz" // 图片的链接，非必须
                        }
                    }*/
                    insertFn(res.data);
                });
            },
            //富文本编辑器上传视频的方法
            videoUpload(file,insertFn){
                let formData = new FormData();
                formData.append('file',file);
                axios({
                    //url是后台上传文件的接口，必须提前写好
                    url: "http://localhost:9090/video/upload",
                    method: 'post',
                    data: formData,
                    headers: {'Content-Type': 'multipart/form-data'},
                }).then(res=>{
                    this.$message.success("视频上传成功");
                    //后台接口无法满足官方规定格式，只能按文档说明，自定义插入视频
                    insertFn(res.data.data);
                });
            },
            onCreated(editor) {
                this.editor = Object.seal(editor) // 一定要用 Object.seal() ，否则会报错
            },
        },
        beforeDestroy() {
            const editor = this.editor
            if (editor == null) return
            editor.destroy() // 组件销毁时，及时销毁编辑器
        }
    }
</script>

<style src="@wangeditor/editor/dist/css/style.css">
    .headerCellClass{
        background-color: lightgray !important;
    }
</style>