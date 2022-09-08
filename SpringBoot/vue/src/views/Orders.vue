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

            <el-input style="width: 200px; margin-left: 400px; margin-top: 6px; margin-right: 10px"
                      placeholder="请输入名称" prefix-icon="el-icon-search" v-model="no"></el-input>
            <el-button type="primary" @click="load">搜索</el-button>
            <el-button type="warning" @click="reset">重置</el-button>
        </div>
        <el-table :data="tableData" border stripe header-cell-class-name="headerCellClass"
                  @selection-change="handleSelectionChange">

            <el-table-column align="center" type="selection" width="45"></el-table-column>
            <el-table-column align="center" prop="id" label="ID" width="45"></el-table-column>
            <el-table-column align="center" prop="name" label="订单名称" ></el-table-column>
            <el-table-column align="center" prop="no" label="订单号" width="140px" ></el-table-column>
            <el-table-column align="center" prop="username" label="用户名" ></el-table-column>
            <el-table-column align="center" prop="nickname" label="用户昵称" ></el-table-column>
            <el-table-column align="center" label="订单详情" >
                <template slot-scope="scope">
                    <el-button type="primary"
                               @click="viewOrderDetail(scope.row)">查看订单</el-button>
                </template>
            </el-table-column>
            <el-table-column align="center" prop="amount" label="支付金额" ></el-table-column>
            <el-table-column align="center" prop="state" label="支付状态"></el-table-column>
            <el-table-column align="center" prop="orderTime" label="下单时间" width="90px" ></el-table-column>
            <el-table-column align="center" prop="payTime" label="支付时间" width="90px" ></el-table-column>

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
        <el-dialog title="订单信息" :visible.sync="dialogFormVisible" width="30%">
            <el-form :model="form" label-width="70px">
                <el-form-item label="订单号">
                    <el-input v-model="form.no" autocomplete="off" disabled ></el-input>
                </el-form-item>
                <el-form-item label="用户名">
                    <el-input v-model="form.username" autocomplete="off" disabled></el-input>
                </el-form-item>
                <el-form-item label="用户昵称">
                    <el-input v-model="form.nickname" autocomplete="off" disabled></el-input>
                </el-form-item>
                <el-form-item label="订单名称">
                    <el-input v-model="form.name" autocomplete="off" disabled></el-input>
                </el-form-item>
                <el-form-item label="支付金额">
                    <el-input v-model="form.amount" autocomplete="off" disabled></el-input>
                </el-form-item>
                <el-form-item label="支付状态">
                    <el-input v-model="form.state" autocomplete="off" ></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="handleAdd">确 定</el-button>
            </div>
        </el-dialog>

        <el-dialog title="购买商品信息" :visible.sync="dialogOrdersGoodsVisible" width="90%">
            <el-table :data="ordersGoods" border stripe header-cell-class-name="headerCellClass">
                <el-table-column align="center" label="图片">
                    <template slot-scope="scope">
                        <el-image :src="scope.row.picture"
                                  :preview-src-list="[scope.row.picture]"
                                  style="width: 100px" />
                    </template>
                </el-table-column>
                <el-table-column align="center" prop="goodsName" label="商品名称" ></el-table-column>
                <el-table-column align="center" prop="code" label="商品编号" ></el-table-column>
                <el-table-column align="center" prop="price" label="单价" ></el-table-column>
                <el-table-column align="center" prop="store" label="库存" ></el-table-column>
                <el-table-column align="center" prop="num" label="购买数量"></el-table-column>
                <el-table-column align="center" prop="subtotal" label="小计" ></el-table-column>
            </el-table>
            <div style="padding-top: 20px; padding-right: 50px; text-align: right">
                <span style="color: #666666; font-weight: bolder">合计：</span>
                <span style="color: #e2231a; font-size: 16px">￥ {{totalPrice}}</span>
            </div>
        </el-dialog>

    </div>
</template>

<script>
    export default {
        name: "Orders",
        data(){
            return {
                tableData: [],
                total: 0,
                pageNum: 1,
                pageSize: 10,
                no: "",     //订单号
                form: [],
                dialogFormVisible: false,
                multipleSelection: [],
                ordersGoods: [],
                dialogOrdersGoodsVisible: false,
                totalPrice: 0,
            }
        },
        created() {
            this.load();
        },
        methods: {
            load(){
                //使用axios来获取后端数据，首先在vue前端项目的目录下安装axios,添加utils/request.js文件，内容可以参考vue项目
                this.request.get("orders/page",{
                    params: {
                        pageNum: this.pageNum,
                        pageSize: this.pageSize,
                        no: this.no
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
                this.request.post("/orders/removeBatchByIds",ids).then(res=>{
                    if(res.code===200){
                        this.$message.success("批量删除成功");
                        this.load();
                    }else {
                        this.$message.error("批量删除失败");
                    }
                })
            },
            deleteById(id){
                this.request.delete("orders/delete/" + id).then(res=>{
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
                this.request.post("orders/save",this.form).then(res=>{
                    if(res.code===200){
                        this.$message.success("保存成功");
                        this.dialogFormVisible = false;
                        this.load();
                    }else {
                        this.$message.error("保存失败");
                    }
                })
            },
            reset(){
                this.no = '';
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
            viewOrderDetail(item){
                this.request.get("/orders/findGoodsByOrderId/" + item.id).then(res=>{
                    if(res.code===200){
                        this.ordersGoods = res.data;
                        this.totalPrice = item.amount;
                        this.dialogOrdersGoodsVisible = true;
                    }
                });
            },
        }
    }
</script>

<style>
    .headerCellClass{
        background-color: lightgray !important;
    }
</style>