<template>
    <div style="padding-top: 30px; padding-left: 207px; padding-right: 210px">
        <el-collapse v-model="activeNames"
                     v-if="notices.length != 0"
                     v-for="item in notices" :key="item.id">
            <el-collapse-item :name="item.id" style="font-size: 18px">
                <template slot="title">
                    <div style="font-size: 16px; width: 550px">{{item.name}}</div>
                    <!--<i class="header-icon el-icon-info"></i>-->
                    <div style="width: 200px; color: #8c939d">发布人：{{item.nickname}}</div>
                    <div style="color: #8c939d">发布时间：{{item.time}}</div>
                </template>
                <div>
                    <Editor
                            style="height: 500px; overflow-y: hidden;"
                            v-model="item.content"
                            :defaultConfig="editorConfig"
                            :mode="mode"
                            @onCreated="onCreated"
                    />
                </div>
            </el-collapse-item>
        </el-collapse>

        <el-collapse v-model="activeNames" v-if="notices.length == 0">
            <el-collapse-item  name="1">
                <div style="text-align: center; color: #8c939d; font-size: 18px">暂 无 公 告</div>
            </el-collapse-item>
        </el-collapse>
    </div>
</template>

<script>
    import {Editor} from '@wangeditor/editor-for-vue'
    export default {
        name: "Notice",
        components: {
            Editor
        },
        data(){
            return{
                activeNames: ['1'],
                notices: [],
                editor: null,
                editorConfig: {},
                mode: 'default', // or 'simple'
            }
        },
        created() {
            this.request.get("/notice").then(res=>{
                if(res.code===200){
                    this.notices = res.data;
                }
            });
        },
        methods:{
            onCreated(editor) {
                this.editor = Object.seal(editor) // 一定要用 Object.seal() ，否则会报错
                editor.disable();  //设置编辑器只读
            },
        }
    }
</script>

<style src="@wangeditor/editor/dist/css/style.css">

</style>