<template>
    <div style="padding-top: 30px; padding-left: 207px; padding-right: 210px">
        <div>

            <!--标题区-->
            <el-card>
                <h3>{{article.name}}</h3>
                <div style="text-align: right; color: #ccc">
                    <div style="padding-bottom: 5px">{{article.publisher}}</div>
                    <div style="padding-left: 10px">
                        <i class="el-icon-time"></i>
                        {{article.publishTime}}
                    </div>
                </div>
            </el-card>

            <!--正文区-->
            <div style="padding-top: 30px; margin-bottom: 30px">
                <mavon-editor
                        class="md"
                        :value="article.content"
                        :subfield="false"
                        :defaultOpen="'preview'"
                        :toolbarsFlag="false"
                        :editable="false"
                        :scrollStyle="true"
                        :ishljs="true"
                />
            </div>

            <!--评论区-->

            <el-card style="margin: 30px 0">
                <div style="margin: 20px 30px">
                    <div style="padding: 10px 0; color: #cccccc">
                        看帖是喜欢，评论才是真爱：
                    </div>
                    <div style="padding: 10px 0">
                        <el-input type="textarea" v-model="content" size="big"></el-input>
                    </div>
                    <div style="text-align: right">
                        <el-button type="primary" size="big" @click="commentAdd">评论</el-button>
                    </div>
                </div>
            </el-card>

            <!--评论区-->
            <el-card>
                <div style="margin: 10px 0">
                    <div style="border-bottom: 1px solid orangered; padding: 10px 0;
                                font-size: 20px; color: #8c939d">
                        全部评论
                    </div>

                    <!--评论列表-->
                    <div style="padding: 5px 0">

                        <el-card v-for="item in comments" :key="item.id">
                            <!--一级评论展示-->
                            <div style="display: flex">
                                <!--头像部分-->
                                <div style="width: 70px; text-align: center">
                                    <el-image :src="item.avatar"
                                              style="width: 50px; border-radius: 50px; "></el-image>
                                </div>
                                <!--评论信息部分-->
                                <div style="width: 852px">
                                    <div style="display: flex">
                                        <div style="width: 800px">
                                            {{item.nickname}}
                                            <span v-if="item.replyName">
                                                <span style="color: #cccccc">&nbsp;&nbsp;回复&nbsp;&nbsp;</span>
                                                <span>{{item.replyName}}</span>
                                            </span>
                                            <p style="color: #cccccc">
                                                {{item.floorNum+1}}楼
                                            </p>
                                        </div>
                                        <div v-if="item.userId===user.id">
                                            <el-popconfirm
                                                    confirm-button-text='确定'
                                                    cancel-button-text='取消'
                                                    icon="el-icon-info"
                                                    icon-color="red"
                                                    title="您确定删除评论吗？"
                                                    @confirm="delCommentById(item.id)"
                                            >
                                                <el-button type="text" size="big" style="color: #cccccc" slot="reference">删除</el-button>
                                            </el-popconfirm>
                                        </div>
                                    </div>

                                    <div style="padding: 20px 0; width: 800px">
                                        {{item.content}}
                                    </div>

                                    <div style="display: flex">
                                        <div style="line-height: 40px; width: 800px; color: #cccccc">
                                            {{item.commentTime}}
                                        </div>
                                        <div>
                                            <el-button type="text" size="big" style="color: #cccccc"
                                                       @click="replyAdd(item.id)">回复</el-button>
                                        </div>

                                    </div>
                                </div>
                            </div>
                            <!--子级展示-->
                            <div v-if="item.children.length" style="padding-left:70px; padding-right: 20px;">
                                <!--只循环两条children数据-->
                                <div v-for="subItem in item.children.slice(0 , 2)" :key="subItem.id" style=" background-color: #f7f8fc">
                                    <div style="display: flex; margin-left: 15px; padding-top: 15px">
                                        <!--头像部分-->
                                        <div style="width: 32px; text-align: center">
                                            <el-image :src="subItem.avatar"
                                                      style="width: 24px; border-radius: 24px; "></el-image>
                                        </div>
                                        <!--评论信息部分-->
                                        <div>
                                            <div style="display: flex">
                                                <div style="width: 720px">
                                                    {{subItem.nickname}}
                                                    <span v-if="subItem.replyName">
                                                        <span style="color: #cccccc">&nbsp;&nbsp;回复&nbsp;&nbsp;</span>
                                                        <span>{{subItem.replyName}}</span>
                                                    </span>
                                                </div>
                                                <div v-if="subItem.userId===user.id">
                                                    <el-popconfirm
                                                            confirm-button-text='确定'
                                                            cancel-button-text='取消'
                                                            icon="el-icon-info"
                                                            icon-color="red"
                                                            title="您确定删除评论吗？"
                                                            @confirm="delCommentById(subItem.id)"
                                                    >
                                                        <el-button type="text" size="big" style="color: #cccccc" slot="reference">删除</el-button>
                                                    </el-popconfirm>
                                                </div>
                                            </div>

                                            <div style="padding: 20px 0; width: 720px">
                                                {{subItem.content}}
                                            </div>

                                            <div style="display: flex">
                                                <div style="line-height: 40px; width: 720px; color: #cccccc">
                                                    {{subItem.commentTime}}
                                                </div>
                                                <div>
                                                    <el-button type="text" size="big" style="color: #cccccc"
                                                               @click="replyAdd(subItem.id)">回复</el-button>
                                                </div>

                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <!--如果children数据大于2，则显示下面的div-->
                                <div v-if="item.children.length > 2"
                                     style="height: 50px; text-align: center; line-height: 50px; color: #999; background-color: #f7f8fc; cursor: pointer"
                                     @click="openCommentDetail(item.id)">
                                    查看更多评论
                                </div>
                            </div>

                        </el-card>

                    </div>
                </div>
            </el-card>

            <el-dialog title="回复" :visible.sync="dialogReplyVisible" width="60%">
                <el-input type="textarea" v-model="commentForm.replyContent"></el-input>
                <div slot="footer" class="dialog-footer">
                    <el-button @click="dialogReplyVisible = false">取 消</el-button>
                    <el-button type="primary" @click="handleAddComment">确 定</el-button>
                </div>
            </el-dialog>

            <el-card style="margin: 30px 0; text-align: center; color: #cccccc">
                没有更多了
            </el-card>

            <!--展示显示更多评论的dialog-->
            <el-dialog title="查看评论" :visible.sync="dialogCommentDetailVisible" width="60%">
                <!--一级评论展示-->
                <div style="display: flex;">
                    <!--头像部分-->
                    <div style="width: 70px; text-align: center">
                        <el-image :src="mainComment.avatar"
                                  style="width: 45px; border-radius: 45px; "></el-image>
                    </div>
                    <!--评论信息部分-->
                    <div style="width: 752px">
                        <div style="display: flex">
                            <div style="width: 700px">
                                {{mainComment.nickname}}
                                <span v-if="mainComment.replyName">
                                                <span style="color: #cccccc">&nbsp;&nbsp;回复&nbsp;&nbsp;</span>
                                                <span>{{mainComment.replyName}}</span>
                                            </span>
                                <p style="color: #cccccc">
                                    {{mainComment.floorNum+1}}楼
                                </p>
                            </div>
                            <div v-if="mainComment.userId===user.id">
                                <el-popconfirm
                                        confirm-button-text='确定'
                                        cancel-button-text='取消'
                                        icon="el-icon-info"
                                        icon-color="red"
                                        title="您确定删除评论吗？"
                                        @confirm="delCommentById(mainComment.id)"
                                >
                                    <el-button type="text" size="big" style="color: #cccccc" slot="reference">删除</el-button>
                                </el-popconfirm>
                            </div>
                        </div>

                        <div style="padding: 20px 0">
                            {{mainComment.content}}
                        </div>

                        <div style="display: flex">
                            <div style="line-height: 40px; width: 700px; color: #cccccc">
                                {{mainComment.commentTime}}
                            </div>
                            <div>
                                <el-button type="text" size="big" style="color: #cccccc"
                                           @click="replyAdd(mainComment.id)">回复</el-button>
                            </div>
                        </div>
                    </div>
                </div>
                <!--一级评论与子级评论之间的样式-->
                <div style="height: 10px; background-color: #d9d9d9"></div>
                <div style="padding: 20px 0px; font-size: 16px">全部评论</div>
                <!--子级评论展示-->
                <div v-for="subItem in mainCommentDetails" :key="subItem.id">
                    <div style="display: flex; padding-top: 15px">
                        <!--头像部分-->
                        <div style="width: 70px; text-align: center">
                            <el-image :src="subItem.avatar"
                                      style="width: 45px; border-radius: 45px; "></el-image>
                        </div>
                        <!--评论信息部分-->
                        <div>
                            <div style="display: flex">
                                <div style="width: 700px">
                                    {{subItem.nickname}}
                                    <span v-if="subItem.replyName">
                                                        <span style="color: #cccccc">&nbsp;&nbsp;回复&nbsp;&nbsp;</span>
                                                        <span>{{subItem.replyName}}</span>
                                                    </span>
                                </div>
                                <div v-if="subItem.userId===user.id">
                                    <el-popconfirm
                                            confirm-button-text='确定'
                                            cancel-button-text='取消'
                                            icon="el-icon-info"
                                            icon-color="red"
                                            title="您确定删除评论吗？"
                                            @confirm="delCommentById(subItem.id)"
                                    >
                                        <el-button type="text" size="big" style="color: #cccccc" slot="reference">删除</el-button>
                                    </el-popconfirm>
                                </div>
                            </div>

                            <div style="padding: 20px 0; width: 700px">
                                {{subItem.content}}
                            </div>

                            <div style="display: flex">
                                <div style="line-height: 40px; width: 700px; color: #cccccc">
                                    {{subItem.commentTime}}
                                </div>
                                <div>
                                    <el-button type="text" size="big" style="color: #cccccc"
                                               @click="replyAdd(subItem.id)">回复</el-button>
                                </div>

                            </div>
                        </div>
                    </div>
                </div>
            </el-dialog>

        </div>
    </div>
</template>

<script>
    export default {
        name: "ArticleDetail",
        data(){
            return{
                itemId : this.$route.query.itemId,
                user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
                article: {},
                comments: [],
                commentForm: {},
                content: "",
                dialogReplyVisible: false,
                mainComment: {},
                mainCommentDetails: [],
                dialogCommentDetailVisible: false,
            }
        },
        created() {
            this.load();
            this.commentList();
        },
        methods:{
            //刷新文章
            load(){
                this.request.get("/article/" + this.itemId).then(res=>{
                    if(res.code===200){
                        this.article = res.data;
                    }else {
                        this.$message.error(res.msg)
                    }
                });
            },
            //刷新评论
            commentList(){
                this.request.get("/comment/findCommentTreeListByArticleId/" + this.itemId).then(res=>{
                    if(res.code===200){
                        this.comments = res.data;
                    }else {
                        this.$message.error(res.msg)
                    }
                });
            },
            /**
             * 新增评论
             * 当点击评论按钮时，说明新增评论是一级评论，不需要pid，orginid，replyid，只需要userid，articleid
             */
            commentAdd(){
                //这步置空commentForm很重要，防止用户点击回复后，什么内容都没输，之后点击取消，
                // 接着又去点击评论，导致评论的位置变成之前点击回复的评论的子级
                this.commentForm = {};
                this.commentForm.content = this.content;
                if(!(this.user.id)){
                    this.$message.error("请登录后评论");
                    return;
                };
                this.commentForm.userId = this.user.id;
                this.commentForm.articleId = this.itemId;
                //新增评论
                this.request.post("/comment/save", this.commentForm).then(res=>{
                    if(res.code===200){
                        this.$message.success("评论成功，请查看全部评论");
                        this.commentList();
                        this.commentForm = {};
                        this.content = "";
                    }else {
                        this.$message.error("评论失败");
                    }
                });
            },
            /**
             * 新增回复
             * 当点击回复按钮时，说明新增评论是二级评论，需要pid，orginid，replyid，userid，articleid
             * 而且，回复时，commentForm的content属性不能存回复的数据，因为回复时会弹出对话框，
             * 回复的内容如果存进content属性，会导致外面的评论框也显示相同的内容，
             * 所以需要给commentForm加另一个replyContent属性来存回复的内容，
             * 当replyContent有值时，把replyContent赋值给content就行
             * 回复时，pid，orginid可以一样，是上一级评论的id，特殊情况，二级评论回复的是另一个二级评论，
             * 这时候pid，orginid就不能是一样的了，但这个需要后台去判断，因为点击回复时，
             * commentForm里会有上一级评论的id，被存储在pid或者orginid，前端传过来时，
             * 如果有pid，必然有orginid，而且是相同的，后台就可以根据pid去数据库找id=pid的数据，找到的只能是一条，
             * 因为数据库的id是唯一的，再获取找到的记录的orginid，将新的orginid重新赋值给commentForm的orginid属性，
             * 同时也将记录的userid赋值给commentForm的replyid
             * 用这样后台处理的方式来保证orginid的准确性，如果后台根据pid获取不到数据，说明这条被回复数据已经被删了，
             * 那就告诉用户说回复不了，当前数据已经被删
             */
            replyAdd(currentCommentId){
                if(!(this.user.id)){
                    this.$message.error("请登录后回复");
                    return;
                }
                this.dialogReplyVisible = true;
                this.commentForm.pid = currentCommentId;
                this.commentForm.originId = currentCommentId;
                this.commentForm.userId = this.user.id;
                this.commentForm.articleId = this.itemId;
            },
            //接上面的解释，把replyContent赋值给content需要在对话框点击确认时才执行，在点击回复时，
            // 无法将replyContent赋值给content，如果在上面的方法做这一步，那replyContent永远是null
            handleAddComment(){
                this.commentForm.content = this.commentForm.replyContent;
                this.request.post("/comment/save", this.commentForm).then(res=>{
                    if(res.code===200){
                        this.$message.success("回复成功，请查看全部评论");
                        this.commentList();
                        this.commentForm = {};
                        this.content = "";
                        this.dialogReplyVisible = false;
                    }else {
                        this.$message.error("回复失败");
                    }
                });
            },
            delCommentById(id){
                this.request.delete("/comment/delete/" + id).then(res=>{
                    if(res.code===200){
                        this.$message.success("删除评论成功");
                        this.commentList();
                        //这一步是为了防止用户在‘查看评论’的对话框里删掉自己的评论时，
                        //对话框没有关闭导致用户看到对话框里原来被删的记录还在而产生误解，所以索性把对话框关了
                        this.dialogCommentDetailVisible = false;
                    }else {
                        this.$message.error("删除评论失败");
                    }
                });
            },
            openCommentDetail(itemId){
                this.request.get("/comment/" +itemId).then(res=>{
                    this.mainComment = res.data.mainComment;
                    this.mainCommentDetails = res.data.mainCommentDetails;
                    //等数据都渲染好后，再弹出对话框
                    this.dialogCommentDetailVisible = true;
                });
            },
        }
    }
</script>

<style scoped>

</style>