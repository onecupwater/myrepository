<template>
    <div style="padding-top: 30px; padding-bottom: 100px; padding-left: 207px; padding-right: 210px">
        <el-table :data="tableData" border stripe header-cell-class-name="headerCellClass"
                  @selection-change="handleSelectionChange">

            <el-table-column align="center" type="selection" width="45"></el-table-column>
            <el-table-column align="center" prop="code" label="商品编号" width="120"></el-table-column>
            <el-table-column align="center" label="商品图片">
                <template slot-scope="scope">
                    <el-image :src="scope.row.picture" :preview-src-list="[scope.row.picture]"></el-image>
                    <!--<img :src="scope.row.picture" style="width: 70px">-->
                </template>
            </el-table-column>
            <el-table-column align="center" prop="goodsName" label="商品名称" width="250" ></el-table-column>
            <el-table-column align="center" prop="price" label="商品单价（元）" width="110"></el-table-column>
            <el-table-column align="center" label="购买数量" width="180">
                <template slot-scope="scope">
                    <el-input-number :min="1" :max="100"
                                     v-model="scope.row.num"
                                     :precision="0"
                                     @change="updateSubTotal(scope.row)"/>
                </template>
            </el-table-column>
            <el-table-column align="center" prop="subtotal" label="小计（元）"></el-table-column>

            <el-table-column label="操作" align="center">
                <template slot-scope="scope">
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
                    layout="total, prev, pager, next"
                    :total="total">
            </el-pagination>
        </div>
        <!--合计-->
        <div style="padding-top: 20px">
            <el-card style="font-size: 14px">
                <div style="display: flex; line-height: 52px">
                    <div style="width: 860px; text-align: right; color: #999999; padding-right: 20px">
                        <span style="padding-right: 20px">已选择 <span style="color: #e2231a">{{totalGoodsNum}}</span> 件商品</span>
                        <span>总价：<span style="color: #e2231a; font-size: 16px">￥ {{totalPrice}}</span></span>
                    </div>
                    <div style="background-color: #e54346; color: #ffffff;
                                font-size: 18px; font-weight: bolder;
                                width: 100px; height: 52px;
                                text-align: center; cursor: pointer"
                         @click="goToSettle">
                        去结算
                    </div>
                </div>
            </el-card>
        </div>

    </div>
</template>

<script>

    import { BigNumber } from 'bignumber.js'

    export default {
        name: "Cart",
        data(){
            return{
                tableData: [],
                total: 0,
                pageNum: 1,
                pageSize: 10,
                name: "",
                multipleSelection: [],
                totalGoodsNum: 0,
                totalPrice: 0,
            }
        },created() {
            this.load();
        },
        methods:{
            load(){
                //使用axios来获取后端数据，首先在vue前端项目的目录下安装axios,添加utils/request.js文件，内容可以参考vue项目
                this.request.get("cart/page",{
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
            deleteById(id){
                this.request.delete("cart/delete/" + id).then(res=>{
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
            //左侧钩子被选中时会触发的方法
            handleSelectionChange(val){
                //val是最左侧打钩的集合，集合里包含被选中的每一行里的所有数据
                this.multipleSelection = val;
                //在这里对被选中的商品进行价格计算
                //console.log(val)
                this.totalPrice = 0;
                this.totalGoodsNum = 0;
                if(val && val.length){
                    val.forEach((currentItem)=>{
                        currentItem.subtotal = BigNumber(currentItem.price)
                            .multipliedBy(currentItem.num).toString();;
                        this.totalPrice = BigNumber(this.totalPrice).plus(currentItem.subtotal).toString();
                        this.totalGoodsNum = this.totalGoodsNum + currentItem.num;
                    });
                }
            },
            //el-input-number的数量发生变化时触发的方法
            updateSubTotal(currentItem){

                //不输入数字时，默认数量是1
                if(!currentItem.num){
                    currentItem.num = 1;
                    this.$message.warning("商品数量为空时，默认数量是：1");
                }

                //重新计算小计
                currentItem.subtotal = BigNumber(currentItem.price)
                    .multipliedBy(currentItem.num).toString();
                //把数据库的旧数据改成新数据
                this.request.post("/cart/updateById", currentItem).then(res=>{
                    if(res.code!==200){
                        this.$message.error("无法修改数量");
                    }
                });
                //让总价也随着数量的变化而变化
                this.totalPrice = 0;
                this.totalGoodsNum = 0;
                //multipleSelection存储着被勾选的商品
                if(this.multipleSelection.length){
                    this.multipleSelection.forEach((item)=>{
                        item.subtotal = BigNumber(item.price)
                            .multipliedBy(item.num).toString();
                        this.totalPrice = BigNumber(this.totalPrice).plus(item.subtotal).toString();
                        this.totalGoodsNum = this.totalGoodsNum + item.num;
                    });
                }
            },
            goToSettle(){
                if(!this.multipleSelection.length){
                    this.$message.error("请选择需要购买的商品");
                    return;
                }

                //跳转检查订单页面
                let idList = this.multipleSelection.map(item=>item.id);
                this.$router.push({path:"/front/checkOrderFromCart",query:{"idList" : idList}});
            },
        }
    }
</script>

<style scoped>

</style>