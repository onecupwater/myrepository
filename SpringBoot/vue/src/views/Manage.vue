<template>
    <el-container style="height: 100vh;">

      <el-aside :width="asideWidth + 'px'" style="background-color: rgb(238, 241, 246); box-shadow: 2px 0 6px rgb(0 21 41/35%)">
        <Aside :is-collapse="isCollapse" :logo-text-show="logoTextShow"/>
      </el-aside>

      <el-container>

        <el-header>
          <Header :collapse-btn-class="collapseBtnClass" :collapse="collapse"/>
        </el-header>

        <el-main style="padding: 7px 20px">
          <!--这里是主体展示部分-->
          <router-view/>
        </el-main>

      </el-container>

    </el-container>
</template>

<script>

import Aside from "../components/Aside";
import Header from "../components/Header";

export default {
  name: "Manage",
  data(){
    return{
      collapseBtnClass: 'el-icon-s-fold',
      isCollapse: false,
      asideWidth: 200,
      logoTextShow: true,
    }
  },
  components: {
    Aside,
    Header
  },
  methods: {
    collapse(){
      this.isCollapse = !this.isCollapse;
      if(this.isCollapse){
        this.collapseBtnClass = 'el-icon-s-unfold';
        this.asideWidth = 64;
        this.logoTextShow = false;
      }else {
        this.collapseBtnClass = 'el-icon-s-fold';
        this.asideWidth = 200;
        this.logoTextShow = true;
      }
    }
  }
}
</script>


