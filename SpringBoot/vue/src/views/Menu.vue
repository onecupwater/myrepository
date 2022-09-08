<template>
    <div>
        <div style="margin: 15px 0px">
            <el-button style="margin-right: 9px" type="primary" icon="el-icon-circle-plus-outline"
                       @click="handleDialogByAddMenu">新增一级菜单
            </el-button>
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
        </div>
        <el-table :data="tableData" border stripe header-cell-class-name="headerCellClass"
                  default-expand-all row-key="id"
                  @selection-change="handleSelectionChange">

            <el-table-column align="center" type="selection" width="55"></el-table-column>
            <!--<el-table-column align="center" prop="id" label="ID" width="120"></el-table-column>-->
            <el-table-column align="center" prop="name" label="菜单名称"></el-table-column>
            <el-table-column align="center" label="图标" class-name="fontSize18" label-class-name="fontSize12">
                <template slot-scope="scope" >
                    <i :class="scope.row.icon"/>
                </template>
            </el-table-column>
            <el-table-column align="center" prop="path" label="路径"></el-table-column>
            <el-table-column align="center" prop="pagePath" label="页面路径"></el-table-column>
            <el-table-column align="center" prop="description" label="描述"></el-table-column>
            <el-table-column align="center" prop="sort" label="排序"></el-table-column>
            <el-table-column label="操作" align="center" width="320">
                <template slot-scope="scope">
                    <el-button type="primary" plain icon="el-icon-plus"
                               v-if="!scope.row.path && !scope.row.pid"
                               @click="handleDialogByAddChildrenMenu(scope.row.id)">新建子菜单
                    </el-button>
                    <el-button type="success" style="margin-right: 8px" plain icon="el-icon-edit"
                               @click="handleEdit(scope.row)">修改
                    </el-button>
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
        <el-dialog title="菜单信息" :visible.sync="dialogFormVisible" width="30%">
            <el-form :model="form" label-width="70px">
                <el-form-item label="名称">
                    <el-input v-model="form.name" autocomplete="off"></el-input>
                </el-form-item>

                <el-form-item label="图标">
                    <el-select clearable v-model="form.icon" placeholder="请选择" style="width: 100%">
                        <el-option v-for="item in icons"
                                   :key="item.name" :label="item.description" :value="item.value">
                            <span style="float: left;">
                                <i :class="item.value"></i>
                            </span>
                            <span style="padding-left: 6px;">{{ item.description }}</span>
                        </el-option>
                    </el-select>
                </el-form-item>

                <el-form-item label="路径">
                    <el-input :placeholder="placeholderDescription" v-model="form.path"
                              autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="页面路径">
                    <el-input v-model="form.pagePath" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="描述">
                    <el-input v-model="form.description" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="排序">
                    <el-input v-model="form.sort" autocomplete="off"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="handleAdd">确 定</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "Menu",
        data() {
            return {
                tableData: [],
                name: "",
                form: [],
                dialogFormVisible: false,
                multipleSelection: [],
                icons: [],
                placeholderDescription : "",
            }
        },
        created() {
            this.load();
        },
        methods: {
            load() {
                //使用axios来获取后端数据，首先在vue前端项目的目录下安装axios,添加utils/request.js文件，内容可以参考vue项目
                this.request.get("menu/tree", {
                    params: {
                        name: this.name
                    }
                }).then(res => {
                    this.tableData = res.data;
                });

                this.request.get("menu/icons").then(res => {
                    this.icons = res.data;
                });
            },
            handleSelectionChange(val) {
                this.multipleSelection = val;
            },
            deleteByBatchIds() {
                let ids = this.multipleSelection.map(value => value.id);
                this.request.post("/menu/removeBatchByIds", ids).then(res => {
                    if (res.code === 200) {
                        this.$message.success("批量删除成功");
                        this.load();
                    } else {
                        this.$message.error("批量删除失败");
                    }
                })
            },
            deleteById(id) {
                this.request.delete("menu/delete/" + id).then(res => {
                    if (res.code === 200) {
                        this.$message.success("删除成功");
                        this.load();
                    } else {
                        this.$message.error("删除失败")
                    }
                })
            },
            handleAdd() {
                //这个方法对应的是：‘菜单信息’对话框里点击‘确认’时触发的方法
                this.request.post("menu/save", this.form).then(res => {
                    if (res.code === 200) {
                        this.$message.success("保存成功");
                        this.dialogFormVisible = false;
                        this.load();
                    } else {
                        this.$message.error("保存失败");
                    }
                })
            },
            handleEdit(row) {
                //这个方法对应的是：点击菜单管理的‘修改’按钮时触发的方法
                this.dialogFormVisible = true;
                this.form = row;
            },
            handleDialogByAddChildrenMenu(id) {
                //这个方法对应的是：点击菜单管理的‘新建子菜单’按钮时触发的方法
                this.dialogFormVisible = true;
                this.placeholderDescription = "";
                this.form = {};
                if (id) {
                    this.form.pid = id;
                }
            },
            handleDialogByAddMenu() {
                //这个方法对应的是：点击菜单管理的‘新增一级菜单’按钮时触发的方法
                this.dialogFormVisible = true;
                this.placeholderDescription = "不设置路径则可以添加子菜单";
                this.form = {};
            },
        }
    }
</script>

<style>
    .headerCellClass {
        background-color: lightgray !important;
    }
    .fontSize18 {
        font-size: 18px;
    }
    .fontSize12 {
        font-size: 12px;
    }
</style>