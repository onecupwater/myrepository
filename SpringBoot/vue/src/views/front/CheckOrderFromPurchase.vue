<template>
    <div style="padding-top: 30px; padding-bottom: 100px; padding-left: 207px; padding-right: 210px">
        <div style="font-size: 16px; color: #666666; padding-bottom: 20px">
            核对订单信息
        </div>
        <el-card>
            <el-table :data="tableData" border stripe header-cell-class-name="headerCellClass">
                <el-table-column align="center" prop="code" label="商品编号" width="120"></el-table-column>
                <el-table-column align="center" label="商品图片">
                    <template slot-scope="scope">
                        <el-image :src="scope.row.picture" :preview-src-list="[scope.row.picture]"></el-image>
                    </template>
                </el-table-column>
                <el-table-column align="center" prop="name" label="商品名称" width="250" ></el-table-column>
                <el-table-column align="center" prop="price" label="商品单价（元）" width="110"></el-table-column>
                <el-table-column align="center" label="购买数量" prop="num" width="180"></el-table-column>
                <el-table-column align="center" prop="subtotal" label="小计（元）"></el-table-column>
            </el-table>
        </el-card>

        <!--提交订单-->
        <div style="padding-top: 20px">
            <el-card style="font-size: 14px">
                <div style="display: flex; line-height: 52px">
                    <div style="width: 200px; color: #666666; cursor: pointer; font-weight: bolder"
                         @click="returnGoodsDetail">
                        返回商品详情
                    </div>
                    <div style="width: 660px; text-align: right; color: #999999; padding-right: 20px">
                        <span>总价：<span style="color: #e2231a; font-size: 16px">￥ {{totalPrice}}</span></span>
                    </div>
                    <div style="background-color: #e54346; color: #ffffff;
                                font-size: 18px; font-weight: bolder;
                                width: 100px; height: 52px;
                                text-align: center; cursor: pointer"
                         @click="submitOrder">
                        提交订单
                    </div>
                </div>
            </el-card>
        </div>
    </div>
</template>

<script>

    import { BigNumber } from 'bignumber.js'

    export default {
        name: "CheckOrderFromPurchase",
        data(){
            return{
                num: this.$route.query.num,
                itemId: this.$route.query.itemId,
                tableData: [],
                totalPrice: 0,
                form: {},  //用来存储传给后台数据的对象
            }
        },
        created() {
            this.load();
        },
        methods:{
            load(){
                this.request.get("/goods/" + this.itemId).then(res=>{
                    if(res.code===200){
                        res.data.num = this.num;
                        res.data.subtotal = BigNumber(res.data.price).multipliedBy(this.num).toString();
                        this.tableData.push(res.data);
                        this.form = res.data;
                        this.totalPrice = res.data.subtotal;
                    }
                });
            },
            returnGoodsDetail(){
                this.$router.push({path:"/front/goodsDetail",query:{"itemId": this.itemId}});
            },
            //提交订单
            submitOrder(){
                console.log(this.form)
                this.request.post("/orders/saveByPurchase", this.form).then(res=>{
                    if(res.code===200){
                        this.$message.success(res.msg);
                        this.$router.push({path:"/front/ordersDetail",query:{"ordersId": res.data}});
                    }else {
                        this.$message.error(res.msg);
                    }
                });
            },
        }
    }
</script>

<style scoped>

</style>