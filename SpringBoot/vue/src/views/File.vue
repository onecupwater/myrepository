<template>
    <div>
        <div style="margin: 15px 0px">
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
                    action="http://localhost:9090/file/upload"
                    :show-file-list="false"
                    :on-success="handleImportSuccess"
                    :on-error="handleImportError"
            >
                <el-button type="primary" icon="el-icon-upload" style="margin-left: 9px">上传文件</el-button>
            </el-upload>

            <span style="margin-left: 140px">
                <el-input style="width: 200px; margin-left: 400px; margin-top: 6px; margin-right: 10px"
                          placeholder="请输入姓名" prefix-icon="el-icon-search" v-model="name"></el-input>
            <el-button type="primary" @click="load">搜索</el-button>
            <el-button type="warning" @click="reset">重置</el-button>
            </span>
        </div>
        <el-table :data="tableData" border stripe header-cell-class-name="headerCellClass"
                  @selection-change="handleSelectionChange">
            <el-table-column align="center" type="selection"></el-table-column>
            <el-table-column align="center" prop="id" label="ID" ></el-table-column>
            <el-table-column align="center" prop="name" label="图片名称"></el-table-column>
            <el-table-column align="center" prop="type" label="类型" ></el-table-column>
            <el-table-column align="center" prop="size" label="图片大小 (KB)" ></el-table-column>
            <el-table-column align="center" label="预览文件">
                <template slot-scope="scope">
                    <el-button type="primary" @click="previewFile(scope.row.url)">预览</el-button>
                </template>
            </el-table-column>

            <el-table-column align="center" prop="url" label="文件下载" >
                <template slot-scope="scope">
                    <el-popconfirm
                            confirm-button-text='确定'
                            cancel-button-text='取消'
                            title="您确定下载吗？"
                            @confirm="downloadFile(scope.row.url)">
                        <el-button type="primary"  slot="reference">下载</el-button>
                    </el-popconfirm>
                </template>
            </el-table-column>

            <el-table-column align="center" prop="enable" label="启用" >
                <template slot-scope="scope">
                    <el-switch
                            v-model="scope.row.enable"
                            active-color="#13ce66"
                            inactive-color="#ff4949"
                            @change="enableSwitch(scope.row)"
                    >
                    </el-switch>
                </template>
            </el-table-column>

            <el-table-column label="操作" align="center">
                <template slot-scope="scope">
                    <el-popconfirm
                            confirm-button-text='确定'
                            cancel-button-text='取消'
                            icon="el-icon-info"
                            icon-color="red"
                            title="您确定删除吗？"
                            @confirm="deleteById(scope.row)"
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
    </div>
</template>

<script>
    export default {
        name: "File",
        data(){
            return{
                tableData: [],
                total: 0,
                pageNum: 1,
                pageSize: 5,
                name: "",
                form: [],
                multipleSelection: [],
                value: ""
            }
        },
        created() {
            this.load();
        },
        methods:{
            load(){
                //使用axios来获取后端数据，首先在vue前端项目的目录下安装axios,添加utils/request.js文件，内容可以参考vue项目
                this.request.get("file/page",{
                    params: {
                        pageNum: this.pageNum,
                        pageSize: this.pageSize,
                        name: this.name
                    }
                }).then(res=>{
                    this.tableData = res.data.records;
                    this.total = res.data.total;
                })
            },
            deleteByBatchIds(){
                let filesList = this.multipleSelection;
                filesList.map(value => {
                    value.isDelete = true;
                });
                this.request.post("/file/removeBatchByIds",filesList).then(res=>{
                    if(res.code===200){
                        this.$message.success("批量删除成功");
                        this.load();
                    }else {
                        this.$message.error("批量删除失败");
                    }
                })
            },
            handleImportError(){
                this.$message.error("上传失败");
                this.load();
            },
            handleImportSuccess(){
                this.$message.success("上传成功");
                this.load();
            },
            reset(){
                this.name = '';
                this.load();
            },
            handleSelectionChange(val){
                this.multipleSelection = val;
            },
            deleteById(files){
                files.isDelete = true;
                this.request.post("file/delete/",files).then(res=>{
                    if(res.code===200){
                        this.$message.success("删除成功");
                        this.load();
                    }else {
                        this.$message.error("删除失败")
                    }
                })
            },
            handleSizeChange(pageSize){
                this.pageSize = pageSize;
                this.load();
            },
            handleCurrentChange(pageNum){
                this.pageNum = pageNum;
                this.load();
            },
            enableSwitch(files){
                //这个点击事件传过来的参数scope.row，也就是files，当你点击的时候，enable的值就已经被修改了
                //所以不用再这里对enable重新赋值了
                //这里也是调用后台的修改接口，而后台的delete石逻辑删除，也就是delete本质是修改接口，所以后台不用写新的接口
                this.request.post("/file/delete",files).then(res=>{
                    if(res.code===200){
                        this.$message.success("操作成功")
                    }else {
                        this.$message.error("操作失败，后台修改状态出错")
                    }
                });
            },
            downloadFile(url){
                window.open(url);
            },
            previewFile(url){
                             //https://file.keking.cn/index
                window.open('https://file.keking.cn/onlinePreview?url='
                    +encodeURIComponent(window.btoa(url)));
            }
        }
    }
</script>

<style scoped>

</style>