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
            <el-input style="width: 200px; margin-left: 564px; margin-top: 6px; margin-right: 10px"
                      placeholder="请输入姓名" prefix-icon="el-icon-search" v-model="name"></el-input>
            <el-button type="primary" @click="load">搜索</el-button>
            <el-button type="warning" @click="reset">重置</el-button>
        </div>
        <el-table :data="tableData" border stripe header-cell-class-name="headerCellClass"
                  @selection-change="handleSelectionChange">
            <el-table-column align="center" type="selection" width="55"></el-table-column>
            <el-table-column align="center" prop="id" label="ID" width="55"></el-table-column>
            <el-table-column align="center" prop="name" width="200" label="角色名称"></el-table-column>
            <el-table-column align="center" prop="flag" width="200" label="唯一标识"></el-table-column>
            <el-table-column align="center" prop="description" label="描述"></el-table-column>
            <el-table-column label="操作" align="center">
                <template slot-scope="scope">

                    <el-button type="info" @click="handleMenu(scope.row)" plain icon="el-icon-menu">分配菜单</el-button>

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
        <el-dialog title="角色信息" :visible.sync="dialogFormVisible" width="30%">
            <el-form :model="form" label-width="70px">
                <el-form-item label="名称">
                    <el-input v-model="form.name" autocomplete="off" ></el-input>
                </el-form-item>
                <el-form-item label="唯一标识">
                    <el-input v-model="form.flag" autocomplete="off" ></el-input>
                </el-form-item>
                <el-form-item label="描述">
                    <el-input v-model="form.description" autocomplete="off" ></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="handleAdd">确 定</el-button>
            </div>
        </el-dialog>

        <el-dialog title="菜单分配" :visible.sync="dialogMenuVisible" width="30%">
            <el-tree
                    :data="menuData"
                    show-checkbox
                    node-key="id"
                    :default-expanded-keys="expandedKeys"
                    :default-checked-keys="checkedKeys"
                    :props="defaultProps"
                    ref="tree">
                <span class="custom-tree-node" slot-scope="{ node, data }">
                    <span><i :class="data.icon"></i></span>
                    <span> {{ data.name }}</span>
                </span>
            </el-tree>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogMenuVisible = false">取 消</el-button>
                <el-button type="primary" @click="roleMenu">确 定</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "Role",
        data(){
            return {
                tableData: [],
                icons: [],
                total: 0,
                pageNum: 1,
                pageSize: 10,
                name: "",
                form: [],
                dialogFormVisible: false,
                dialogMenuVisible: false,
                multipleSelection: [],
                menuData: [],
                defaultProps: {
                    children: 'children',
                    label: 'name'
                },
                expandedKeys: [],
                checkedKeys: [],
                currentRole: [],
            }
        },
        created() {
            this.load();
        },
        methods: {
            load(){
                //使用axios来获取后端数据，首先在vue前端项目的目录下安装axios,添加utils/request.js文件，内容可以参考vue项目
                this.request.get("role/page",{
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
                this.request.post("/role/removeBatchByIds",ids).then(res=>{
                    if(res.code===200){
                        this.$message.success("批量删除成功");
                        this.load();
                    }else {
                        this.$message.error("批量删除失败");
                    }
                })
            },
            deleteById(id){
                this.request.delete("role/delete/" + id).then(res=>{
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
                this.request.post("role/save",this.form).then(res=>{
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
            handleMenu(role){
                //点击‘菜单分配’按钮时触发，将当前行role赋值给this.currentRole，
                // 方便‘菜单分配’对话框点击‘确定’按钮时能获得当前行currentRole
                this.currentRole = role;
                //获得改造后菜单表的数据
                this.request.get("menu/tree").then(res=>{
                    this.menuData = res.data;
                    //res.data.map(v=>v.id)的结果只是一级菜单的id，不包含二级菜单的id
                    this.expandedKeys = res.data.map(v=>v.id);
                });
                //根据roleId回显保存在角色菜单表的所有menuIds，让this.checkedKeys默认选中这些查出来的menuIds
                //但是由于父子互相关联的关系，只要父级被选中，所有子级都被选中，
                // 即使有某一个子级的id不在checkedKeys集合里，父级的选中还是强迫该子级的勾选框也被选中
                this.request.get("role/selectMenuIdsByRoleId/"+role.id).then(res=>{
                    this.checkedKeys = res.data;
                    // checkedKeys的优先级还是低了一点，所以需要下面的方法
                    //获得原始数据库的菜单表的所有id
                    this.request.get("menu/findAllIds").then(res=>{
                        //可以理解为菜单表的所有id就是tree控件的所有勾选框，不管是父级还是子级
                        //因为checkedKeys是存储的是从数据库根据当前roleId获得所有准确的menuId，不信可以打印试试
                        //用所有id，也就是所有勾选框遍历，逐个逐个勾选框与checkedKeys做比较
                        res.data.forEach(id=>{
                            //只要当前的勾选框的id在checkedKeys集合里找不到
                            if(!this.checkedKeys.includes(id)){
                                //那么就可以给当前这个勾选框设置没选中
                                this.$refs.tree.setChecked(id,false);
                            }
                        });
                    });
                });
                //打开‘分配菜单’对话框
                this.dialogMenuVisible = true;
            },
            roleMenu(){
                //点击‘菜单分配’对话框的‘确定’按钮时触发
                let menuIds = this.$refs.tree.getCheckedKeys();
                this.request.post("role/saveMenuIdsByRoleId/"+this.currentRole.id,menuIds).then(res=>{
                    if(res.code===200){
                        this.dialogMenuVisible = false;
                        //当对‘管理员’这个角色进行菜单分配，点击确认‘确定’时，让浏览器重新登录
                        if(this.currentRole.flag === "ROLE_ADMIN"){
                            this.$store.commit("logout");
                            this.$message.success("角色菜单分配成功，请重新登录，查看效果");

                        }else {
                            //不是对‘管理员’这个角色进行菜单分配，就执行下面的代码：
                            this.$message.success(res.msg);
                        }
                    }else {
                        this.$message.error(res.msg);
                    }
                });
            }
        }
    }
</script>

<style>
    .headerCellClass{
        background-color: lightgray !important;
    }
</style>