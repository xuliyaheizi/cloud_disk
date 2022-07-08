<template>
    <div id="login">
        <div class="form">
            <el-form :label-position="labelPosition" label-width="80px" :model="loginAlign" size="small"
                     ref="form" :rules="rules">
                <el-form-item label="用户名">
                    <el-input v-model="loginAlign.uname" type="text"></el-input>
                </el-form-item>
                <el-form-item label="密码">
                    <el-input v-model="loginAlign.pwd" type="password"></el-input>
                </el-form-item>
                <el-form-item label="验证码">
                    <el-col :span="12">
                        <el-form-item>
                            <el-input v-model="loginAlign.code"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="11" style="float: right">
                        <el-form-item>
                            <el-image :src="yzm" @click="resFlush"></el-image>
                        </el-form-item>
                    </el-col>
                </el-form-item>
                <el-form-item size="large">
                    <el-button type="primary" @click.native.prevent="doLogin">登录</el-button>
                    <el-button>注册</el-button>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script>
import {login} from "@/api/login";

export default {
    name: "login",
    data() {
        return {
            yzm: "/api/verifyCodeServlet",
            loginAlign: {
                uname: '',
                pwd: '',
                code: '',
            },
            labelPosition: 'right',
            loading: false,
            rules: {
                username: [
                    {required: true, message: "请输入用户名", trigger: "blur"},
                    {max: 10, message: "不能大于10个字符", trigger: "blur"},
                ],
                password: [
                    {required: true, message: "请输入密码", trigger: "blur"},
                    {max: 10, message: "不能大于10个字符", trigger: "blur"},
                ],
            },
        }
    },
    watch: {
        $route: {
            handler: function (route) {
                this.redirect = route.query && route.query.redirect;
            },
            immediate: true,
        },
    },
    methods: {
        resFlush() {
            this.yzm = "/api/verifyCodeServlet?" + new Date();
        },
        doLogin(e) {
            // 阻止页面刷新
            e.preventDefault();
            let formData = new FormData;
            for (let loginAlignKey in this.loginAlign) {
                formData.append(loginAlignKey, this.loginAlign[loginAlignKey]);
            }
            login(formData).then(resp => {
                if (resp.code == 200) {
                    this.$router.push({
                        path: "/indexPage",
                        name: "cloudDisk",
                    })
                } else {
                    this.$message({
                        type: 'error',
                        message: resp.msg,
                    })
                }
            })
        },
    }
}
</script>

<style scoped>
#login {
    margin: 0px auto;
    padding: 0;
}

.form {
    position: absolute;
    width: 300px;
    top: 20%;
    left: 38%;
}

.el-form-item--mini.el-form-item, .el-form-item--small.el-form-item {
    margin-bottom: 12px !important;
}
</style>