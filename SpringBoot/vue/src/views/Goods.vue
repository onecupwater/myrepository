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
            <el-table-column align="center" prop="name" label="商品名称" ></el-table-column>
            <el-table-column align="center" prop="code" label="商品编号" ></el-table-column>
            <el-table-column align="center" prop="price" label="价格" ></el-table-column>
            <el-table-column align="center" prop="store" label="库存" ></el-table-column>
            <el-table-column align="center" prop="grossWeight" label="毛重"></el-table-column>
            <el-table-column align="center" prop="place" label="产地" ></el-table-column>
            <el-table-column align="center" label="图片">
                <template slot-scope="scope">
                    <el-button type="primary"
                               @click="viewGoodsPicture(scope.row.picture)">查看商品图片</el-button>
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
        <el-dialog title="商品信息" :visible.sync="dialogFormVisible" width="30%">
            <el-form :model="form" label-width="70px">
                <el-form-item label="商品名称">
                    <el-input v-model="form.name" autocomplete="off" ></el-input>
                </el-form-item>
                <el-form-item label="商品编号">
                    <el-input v-model="form.code" autocomplete="off" ></el-input>
                </el-form-item>
                <el-form-item label="价格">
                    <el-input v-model="form.price" autocomplete="off" ></el-input>
                </el-form-item>
                <el-form-item label="库存">
                    <el-input v-model="form.store" autocomplete="off" ></el-input>
                </el-form-item>
                <el-form-item label="毛重">
                    <el-input v-model="form.grossWeight" autocomplete="off" ></el-input>
                </el-form-item>
                <el-form-item label="产地">
                    <el-input v-model="form.place" autocomplete="off" ></el-input>
                </el-form-item>
                <el-form-item label="图片">
                    <el-upload
                            class="avatar-uploader"
                            action="http://localhost:9090/file/upload"
                            :show-file-list="false"
                            :on-success="handleAvatarSuccess"
                    >
                        <img v-if="form.picture" :src="form.picture" class="avatar">
                        <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                    </el-upload>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="handleAdd">确 定</el-button>
            </div>
        </el-dialog>

        <el-dialog title="商品图片" :visible.sync="dialogGoodsPictureVisible" width="50%"
                   style="text-align: center">
            <el-image :src="goodsPicture"></el-image>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "Goods",
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
                dialogGoodsPictureVisible: false,
                goodsPicture: "",
            }
        },
        created() {
            this.load();
        },
        methods: {
            load(){
                //使用axios来获取后端数据，首先在vue前端项目的目录下安装axios,添加utils/request.js文件，内容可以参考vue项目
                this.request.get("goods/page",{
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
            handleAfterUpload(response){
                if(response.code!==200){
                    this.$message.error(response.msg);
                }else {
                    this.$message.success("视频上传成功");
                    this.load();
                }
            },
            handleSelectionChange(val){
                this.multipleSelection = val;
            },
            deleteByBatchIds(){
                let ids = this.multipleSelection.map(value => value.id);
                this.request.post("/goods/removeBatchByIds",ids).then(res=>{
                    if(res.code===200){
                        this.$message.success("批量删除成功");
                        this.load();
                    }else {
                        this.$message.error("批量删除失败");
                    }
                })
            },
            deleteById(id){
                this.request.delete("goods/delete/" + id).then(res=>{
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
                this.request.post("goods/save",this.form).then(res=>{
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
            handleAvatarSuccess(res){
                this.form.picture = res;
            },
            viewGoodsPicture(picture){
                this.goodsPicture = picture;
                this.dialogGoodsPictureVisible = true;
            },
        }
    }
</script>

<style>
    .headerCellClass{
        background-color: lightgray !important;
    }
    .avatar-uploader .el-upload {
        border: 1px dashed #d9d9d9;
        border-radius: 6px;
        cursor: pointer;
        position: relative;
        overflow: hidden;
        margin-left: 20px;
    }
    .avatar-uploader .el-upload:hover {
        border-color: #409EFF;
    }
    .avatar-uploader-icon {
        font-size: 28px;
        color: #8c939d;
        width: 120px;
        height: 120px;
        line-height: 120px;
        text-align: center;
    }
    .avatar {
        width: 120px;
        height: 120px;
        display: block;
    }
</style>