<template>
    <div style="padding-top: 30px; padding-bottom: 100px; padding-left: 207px; padding-right: 210px">
        <el-card>
            <div style="border-bottom: 1px solid #8c939d;
                        padding: 10px 0;
                        color: #8c939d">订单号： {{orders.no}}
            </div>
            <div style="padding: 10px 0"/>
            <el-table
                      :data="ordersGoods" border stripe header-cell-class-name="headerCellClass">
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

            <div style="padding-top: 20px; padding-right: 20px; text-align: right">
                <span style="color: #666666; font-weight: bolder">合计：</span>
                <span style="color: #e2231a; font-size: 16px">￥ {{orders.amount}}</span>
            </div>

            <div style="padding-left: 860px; padding-top: 20px" v-if="orders.state==='待支付'">
                <div style="background-color: #e54346; color: #ffffff;
                        font-size: 18px; font-weight: bolder;
                        width: 100px; height: 52px;
                        text-align: center; line-height: 52px;
                        cursor: pointer"
                     @click="">
                    去支付
                </div>
            </div>
        </el-card>
    </div>
</template>

<script>

    export default {
        name: "OrdersDetail",
        data() {
            return{
                ordersId: this.$route.query.ordersId,
                orders: {},
                ordersGoods: [],
            }
        },
        created() {
            this.load();
        },
        methods: {
            load(){
                this.request.get("orders/findOrdersAndGoodsByOrderId/" + this.ordersId).then(res=>{
                    if(res.code===200){
                        this.orders = res.data.orders;
                        this.ordersGoods = res.data.ordersGoods;
                    }
                });
            },
        }
    }
</script>

<style scoped>

</style>