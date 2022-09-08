<template>
    <div style="padding-top: 30px; padding-bottom: 100px; padding-left: 207px; padding-right: 210px">
        <el-card>
            <div style="display: flex">
                <img :src="goods.picture"
                     style="width: 450px; height: 450px; font-size: 16px; color: #666666">
                <div style="padding-left: 40px; padding-top: 30px">
                    <div style="padding-bottom: 40px">{{goods.name}}</div>
                    <div style="padding-bottom: 40px">商品编号： {{goods.code}}</div>
                    <div style="padding-bottom: 40px">库存： {{goods.store}}</div>
                    <div style="padding-bottom: 40px">
                        价格：<span style="color: #e4393c">￥ <span style="font-size: 22px">{{goods.price}}</span></span>
                    </div>
                    <div style="padding-bottom: 40px">
                        <span>购买数量： </span>
                        <el-input-number :min="1" :max="100"
                                         v-model="num"
                                         @change="toInteger"/>
                    </div>
                    <div>
                        <el-button type="danger" size="big" @click="joinMyCart">加入购物车</el-button>
                        <el-button type="warning" size="big" @click="goCheckOrder">直接购买</el-button>
                    </div>
                </div>
            </div>
        </el-card>
    </div>
</template>

<script>
    export default {
        name: "GoodsDetail",
        data(){
            return{
                itemId: this.$route.query.itemId,
                goods: {},
                num: 1, //商品初始数量
                user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
                form: {}, //存储cart表需要的字段
            }
        },
        created() {
            this.load();
        },
        methods:{
            load(){
                this.request.get("/goods/" +this.itemId).then(res=>{
                    if(res.code===200){
                        this.goods = res.data;
                    }
                });
            },
            //校验输入数量框，只能是正整数
            toInteger(){
                let reg = /^[0-9]+$/
                if(!reg.test(this.num)){
                    this.$message.warning("只能输入正整数");
                    //有$nextTick才能保证这句代码‘this.num = parseInt(this.num)’是最后执行的
                    this.$nextTick(()=>{
                        //parseInt是把当前有小数点的num去掉小数点
                        this.num = parseInt(this.num);
                    });
                }
            },
            joinMyCart(){
                //如果没有登录，则提示用户登录
                if(!this.user.name){
                    this.$message.warning("请先登录");
                    return;
                }

                //设置form
                this.form.userId = this.user.id;
                this.form.goodsId = this.goods.id;
                this.form.num = this.num;
                //toFixed是将相乘的结果四舍五入，也就是小数点后第3位为被四舍五入
                this.form.subtotal = (this.num * this.goods.price).toFixed(2);

                //将form存进数据库
                this.request.post("cart/save",this.form).then(res=>{
                    if(res.code===200){
                        this.$message.success("加入购物车成功，请查看‘我的购物车’");
                    }else{
                        this.$message.error("加入购物车失败");
                    }
                });
            },
            goCheckOrder(){
                //如果没有登录，则提示用户登录
                if(!this.user.name){
                    this.$message.warning("请先登录");
                    return;
                }

                this.$router.push({
                    path:"/front/checkOrderFromPurchase",
                    query:{"num": this.num,"itemId": this.itemId}
                });
            },
        }
    }
</script>

<style scoped>

</style>