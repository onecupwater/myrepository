<template>
    <div style="padding-top: 30px; padding-left: 207px; padding-right: 210px">
        <div style="padding-top: 30px">
            <video-player
                    class="video-player vjs-custom-skin"
                    ref="videoPlayer"
                    :playsinline="true"
                    :options="playerOptions">
            </video-player>
        </div>
    </div>
</template>

<script>
    import {videoPlayer} from "vue-video-player";
    import 'video.js/dist/video-js.css';

    export default {
        name: "VideoDetail",
        components:{
            videoPlayer,
        },
        data(){
            return{
                itemId : this.$route.query.itemId,  //itemId，需要再进数据库查数据，以免刷新时页面空白
                // 视频播放
                playerOptions: {
                    playbackRates: [0.5, 1.0, 1.5, 2.0], //可选择的播放速度
                    autoplay: false, //如果true,浏览器准备好时开始回放。
                    muted: false, // 默认情况下将会消除任何音频。
                    loop: false, // 视频一结束就重新开始。
                    preload: "auto", // 建议浏览器在<video>加载元素后是否应该开始下载视频数据。auto浏览器选择最佳行为,立即开始加载视频（如果浏览器支持）
                    language: "zh-CN",
                    aspectRatio: "16:9", // 将播放器置于流畅模式，并在计算播放器的动态大小时使用该值。值应该代表一个比例 - 用冒号分隔的两个数字（例如"16:9"或"4:3"）
                    fluid: true, // 当true时，Video.js player将拥有流体大小。换句话说，它将按比例缩放以适应其容器。
                    sources: [
                        {
                            type: "video/mp4",
                            //最重要的属性，播放地址,在created方法中，往这个属性里赋视频的url
                            //这里先给视频一个能播放的初始视频，等created重新渲染src，如果一开始把src设置为空，
                            //每次跳转视频详情页面时浏览器老是先报错后，再执行created，所以才出此下策
                            src: "http://localhost:9090/video/entity/f59167b35eb44bdaa93c752deea5e707.mp4",
                        },
                    ],
                    poster: "", //你的封面地址
                    notSupportedMessage: "此视频暂无法播放，请稍后再试", //允许覆盖Video.js无法播放媒体源时显示的默认信息。
                    controlBar: {
                        timeDivider: true, //当前时间和持续时间的分隔符
                        durationDisplay: true, //显示持续时间
                        remainingTimeDisplay: false, //是否显示剩余时间功能
                        fullscreenToggle: true, //全屏按钮
                    },
                },
            }
        },
        created() {
            this.request.get("video/getById/" + this.itemId).then(res=>{
                if(res.code===200){
                    this.playerOptions.sources[0].src = res.data.url;
                    this.playerOptions.poster = res.data.cover;
                }else {
                    this.$message.error("获取视频失败");
                }
            });
        },
    }
</script>

<style scoped>

</style>