<template>
    <div class="aside">
        <el-container>
            <el-aside style="width: auto !important;">
                <el-col>
                    <el-menu
                        default-active="/indexPage"
                        class="el-menu-vertical-demo"
                        :collapse="isCollapse"
                        unique-opened
                        router>
                        <el-menu-item index="/indexPage">
                            <i class="el-icon-s-promotion"></i>
                            <span slot="title">首页</span>
                        </el-menu-item>
                        <el-submenu index="2">
                            <template slot="title">
                                <i class="el-icon-location"></i>
                                <span>中控管理</span>
                            </template>
                            <el-menu-item index="/indexPage/centerPage">
                                <i class="el-icon-chicken"></i>
                                <span slot="title">gangLia管理
                            </span>
                            </el-menu-item>
                            <el-menu-item index="/indexPage/azkabanPage">
                                <i class="el-icon-food"></i>
                                <span slot="title">azkaban管理</span>
                            </el-menu-item>
                            <el-menu-item index="/indexPage/nmPage">
                                <i class="el-icon-fork-spoon"></i>
                                <span slot="title">nmNode管理</span>
                            </el-menu-item>
                            <el-menu-item index="/indexPage/rmNodePage">
                                <i class="el-icon-dessert"></i>
                                <span slot="title">rmNode管理</span>
                            </el-menu-item>
                        </el-submenu>
                        <el-menu-item index="/indexPage/cloudreve">
                            <i class="el-icon-s-promotion"></i>
                            <span slot="title">cloudreve</span>
                        </el-menu-item>
                    </el-menu>
                </el-col>
            </el-aside>
            <el-main>
                <el-button :icon="butIcon" @click="openAside" size="small"></el-button>
                <router-view/>
            </el-main>
        </el-container>
    </div>
</template>

<script>
export default {
    name: "indexPage",
    data() {
        return {
            isCollapse: false,
            butIcon: "el-icon-s-fold",
        }
    },
    methods: {
        openAside() {
            if (this.isCollapse == true) {
                this.isCollapse = false;
                this.butIcon = "el-icon-s-fold";
            } else {
                this.isCollapse = true;
                this.butIcon = "el-icon-s-unfold";
            }
        },
    }
}
</script>

<style scoped>
.el-menu-vertical-demo:not(.el-menu--collapse) {
    width: 200px;
    min-height: 550px;
}
</style>