<template>
    <div style="padding-top: 30px; padding-left: 207px; padding-right: 210px">
        <div>
            <el-row :gutter="10">
                <el-col :span="6" v-for="item in comics">
                    <img :src="item.url" width="100%" style="padding-bottom: 10px">
                    <span>
                        {{item.name}}
                    </span>
                </el-col>
            </el-row>
        </div>
    </div>
</template>

<script>
    export default {
        name: "Comic",
        data(){
            return {
                comics : []
            }
        },
        created() {
            this.request.get("course/getAllFiles").then(res=>{
                this.comics = res.data
                    .filter(v=>v.type==='png' || v.type==='jpg' || v.type==='webp');
            });
        }
    }
</script>

<style scoped>

</style>