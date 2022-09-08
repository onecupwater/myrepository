<template>
    <el-container style="height: 100vh;">
      <el-aside :width="asideWidth + 'px'" style="background-color: rgb(238, 241, 246); box-shadow: 2px 0 6px rgb(0 21 41/35%)">
        <el-menu :default-openeds="['1', '3']" style="height: 100% ; overflow-x: hidden"
                 background-color="rgb(48, 65, 86)"
                 text-color="#fff"
                 active-text-color="#ffd04e"
                 :collapse-transition="false"
                 :collapse="isCollapse">
          <div style="height: 60px; line-height: 60px; text-align: center">
            <img src="../assets/logo.png" style="height: 21px; margin-right: 1.5px; position: relative; top: 5px">
            <b style="color: white" v-show="logoTextShow">后台管理系统</b>
          </div>
          <el-submenu index="1">
            <template slot="title"><i class="el-icon-message"></i>
              <span slot="title">导航一</span>
            </template>
            <el-menu-item-group>
              <template slot="title">分组一</template>
              <el-menu-item index="1-1">选项1</el-menu-item>
              <el-menu-item index="1-2">选项2</el-menu-item>
            </el-menu-item-group>
            <el-menu-item-group title="分组2">
              <el-menu-item index="1-3">选项3</el-menu-item>
            </el-menu-item-group>
            <el-submenu index="1-4">
              <template slot="title">选项4</template>
              <el-menu-item index="1-4-1">选项4-1</el-menu-item>
            </el-submenu>
          </el-submenu>
          <el-submenu index="2">
            <template slot="title"><i class="el-icon-menu"></i>
              <span slot="title">导航二</span></template>
            <el-menu-item-group>
              <template slot="title">分组一</template>
              <el-menu-item index="2-1">选项1</el-menu-item>
              <el-menu-item index="2-2">选项2</el-menu-item>
            </el-menu-item-group>
            <el-menu-item-group title="分组2">
              <el-menu-item index="2-3">选项3</el-menu-item>
            </el-menu-item-group>
            <el-submenu index="2-4">
              <template slot="title">选项4</template>
              <el-menu-item index="2-4-1">选项4-1</el-menu-item>
            </el-submenu>
          </el-submenu>
          <el-submenu index="3">
            <template slot="title"><i class="el-icon-setting"></i>
              <span slot="title">导航三</span></template>
            <el-menu-item-group>
              <template slot="title">分组一</template>
              <el-menu-item index="3-1">选项1</el-menu-item>
              <el-menu-item index="3-2">选项2</el-menu-item>
            </el-menu-item-group>
            <el-menu-item-group title="分组2">
              <el-menu-item index="3-3">选项3</el-menu-item>
            </el-menu-item-group>
            <el-submenu index="3-4">
              <template slot="title">选项4</template>
              <el-menu-item index="3-4-1">选项4-1</el-menu-item>
            </el-submenu>
          </el-submenu>
        </el-menu>
      </el-aside>

      <el-container>
        <el-header style="font-size: 12px; border-bottom: 1px solid #ccc; line-height: 60px; display: flex">
          <div style="flex: 1;font-size: 25px">
            <span :class="collapseBtnClass" style="cursor: pointer" @click="collapse"></span>
          </div>
          <el-dropdown style="width: 78px; cursor: pointer">
            <span>王小虎</span><i class = "el-icon-arrow-down" style="margin-left: 5px"></i>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item>个人信息</el-dropdown-item>
              <el-dropdown-item>退出</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </el-header>

        <el-main style="padding: 7px 20px">
          <div style="margin: 15px 0px">
              <el-breadcrumb separator="/">
                <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
                <el-breadcrumb-item>用户管理</el-breadcrumb-item>
              </el-breadcrumb>
          </div>
          <div style="margin: 15px 0px">
            <el-button style="margin-right: 9px" type="primary" icon="el-icon-circle-plus-outline" @click="handleDialog">新增</el-button>
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
            <el-button type="primary" icon="el-icon-download" style="margin-left: 9px">导入</el-button>
            <el-button type="primary" icon="el-icon-upload2">导出</el-button>
            <el-input style="width: 200px; margin-left: 460px; margin-top: 6px; margin-right: 10px"
                      placeholder="请输入姓名" prefix-icon="el-icon-search" v-model="name"></el-input>
            <el-button type="primary" @click="load">搜索</el-button>
            <el-button type="warning" @click="reset">重置</el-button>
          </div>
          <el-table :data="tableData" border stripe header-cell-class-name="headerCellClass"
                    @selection-change="handleSelectionChange">
            <el-table-column align="center" type="selection" width="45"></el-table-column>
            <el-table-column align="center" prop="id" label="ID" width="60"></el-table-column>
            <el-table-column align="center" prop="name" label="姓名" width="80"></el-table-column>
            <el-table-column align="center" prop="email" label="邮箱"></el-table-column>
            <el-table-column align="center" prop="address" label="地址"></el-table-column>
            <el-table-column align="center" prop="createDate" label="创建日期"></el-table-column>
            <el-table-column align="center" prop="updateDate" label="修改日期"></el-table-column>
            <el-table-column label="操作" align="center">
              <template slot-scope="scope">
                <el-button type="success" style="margin-right: 8px" plain icon="el-icon-edit" @click="handleEdit(scope.row)">修改</el-button>
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
          <el-dialog title="用户信息" :visible.sync="dialogFormVisible" width="30%">
            <el-form :model="form" label-width="50px">
              <el-form-item label="姓名">
                <el-input v-model="form.name" autocomplete="off" ></el-input>
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input v-model="form.email" autocomplete="off" ></el-input>
              </el-form-item>
              <el-form-item label="地址">
                <el-input v-model="form.address" autocomplete="off" ></el-input>
              </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
              <el-button @click="dialogFormVisible = false">取 消</el-button>
              <el-button type="primary" @click="handleAdd">确 定</el-button>
            </div>
          </el-dialog>
        </el-main>
      </el-container>
    </el-container>
</template>

<script>


export default {
  data(){
    return{
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 5,
      name: "",
      form: [],
      dialogFormVisible: false,
      multipleSelection: [],
      collapseBtnClass: 'el-icon-s-fold',
      isCollapse: false,
      asideWidth: 200,
      logoTextShow: true
    }
  },
  created() {
    this.load();
  },
  methods: {
    load(){

      //使用fetch来获取后端数据
      /*fetch("http://localhost:9090/user1/selectPage?pageNum="+this.pageNum+"&pageSize="+this.pageSize+"&name="+this.name)
              .then(res => res.json()).then(res => {
        this.tableData = res.data;
        this.total = res.total;
      })*/

      //使用axios来获取后端数据，首先在vue前端项目的目录下安装axios,添加utils/request.js文件，内容可以参考vue项目
      this.request.get("user/selectPage",{
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name
        }
      }).then(res=>{
        this.tableData = res.records;
        this.total = res.total;
      })
    },
    handleSelectionChange(val){
      this.multipleSelection = val;
    },
    deleteByBatchIds(){
      let ids = this.multipleSelection.map(value => value.id);
      this.request.post("/user/batchDelete",ids).then(res=>{
        if(res > 0){
          this.$message.success("批量删除成功");
          this.load();
        }else {
          this.$message.error("批量删除失败");
        }
      })
    },
    deleteById(id){
      this.request.delete("user/delete/" + id).then(res=>{
        if(res==1){
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
      this.request.post("user/save",this.form).then(res=>{
        if(res==1){
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
    collapse(){
        this.isCollapse = !this.isCollapse;
        if(this.isCollapse){
          this.collapseBtnClass = 'el-icon-s-unfold';
          this.asideWidth = 64;
          this.logoTextShow = false;
        }else {
          this.collapseBtnClass = 'el-icon-s-fold';
          this.asideWidth = 200;
          this.logoTextShow = true;
        }

    }
  }
}
</script>

<style>
  .headerCellClass{
    background-color: lightgray !important;
  }
</style>
