<template>
    <div style="padding-top: 30px; padding-bottom: 100px; padding-left: 207px; padding-right: 210px">

        <!--轮播图-->
        <div class="block">
            <el-carousel height="350px">
                <el-carousel-item v-for="item in pictures" :key="item">
                    <img :src="item" style="height: 350px; width: 1006px">
                </el-carousel-item>
            </el-carousel>
        </div>
        <!--商品展示-->
        <el-row :gutter="10" style="padding-top: 20px">
            <el-col :span="6" v-for="item in goods">
                <img :src="item.picture" width="100%"
                     style="padding-bottom: 20px; cursor: pointer"
                     @click="goGoodsDetail(item.id)">
                <div style="overflow: hidden;
                            text-overflow: ellipsis;
                            display: -webkit-box;
                            -webkit-box-orient: vertical;
                            -webkit-line-clamp: 2;
                            color: #666;
                            font-size: 14px;
                            cursor: pointer"
                     @click="goGoodsDetail(item.id)">{{item.name}}
                </div>
                <div style="text-align: center;
                            padding-top: 10px;
                            color: orangered">￥ {{item.price}}
                </div>
            </el-col>
        </el-row>
    </div>
</template>

<script>
    export default {
        name: "FrontHome",
        data(){
            return {
                pictures : [
                    "https://img20.360buyimg.com/babel/s1580x680_jfs/t1/83532/11/16765/264988/61385fdfE8ad957b6/e578bba9cea6996b.jpg",
                    "https://img13.360buyimg.com/babel/s1580x680_jfs/t1/205511/16/6502/84403/61407038E342a09a5/b6b5b4e44b221377.jpg"
                ],
                goods: [],
            }
        },
        created() {
            this.load();
        },
        methods:{
            load(){
                this.request.get("/goods").then(res=>{
                    if(res.code===200){
                        this.goods = res.data;
                    }
                });
            },
            goGoodsDetail(id){
                this.$router.push({path:"/front/goodsDetail",query:{"itemId" : id}});
            },
        }
    }
</script>

<style scoped>

</style>