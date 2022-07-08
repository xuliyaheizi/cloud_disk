<template>
    <div id="cloudDisk">
        <el-button type="text" size="medium" style="margin-right: 10px" @click="goUp()">返回上一级
        </el-button>
        <span>当前路径:</span>
        <!--输入框-->
        <el-input
            size="medium"
            placeholder="当前路径"
            v-model="currentPath"
            style="width: 30%;margin: 0px 10px 0px"
            @keyup.native.enter="searchFiles(currentPath)">
        </el-input>
        <!--搜索按钮-->
        <el-button type="primary" size="medium" icon="el-icon-search" round @click="searchFiles(currentPath)">搜索
        </el-button>
        <!--新建目录-->
        <el-button type="primary" size="medium" @click="mkdirNew">新建目录</el-button>
        <el-upload
            class="upload-demo"
            action="/api/hdfs/upload"
            ref="upload"
            :data="{curPath:currentPath}"
            :show-file-list=true
            :on-success="uploadSuccess"
            :file-list="fileList"
            :on-change="handleChange"
            style="width: 10%;display: inline-block;margin: 0px 10px 0px">
            <el-button size="medium" type="primary">上传<i class="el-icon-upload el-icon--right"></i></el-button>
        </el-upload>
        <el-table
            v-loading="isLoading"
            :data="hdfsData"
            style="width: 100%">
            <el-table-column
                prop="fileName"
                label="文件名"
                width="120">
                <template slot-scope="scope">
                    <el-button type="text" size="small" @click="showFiles(scope.row)">
                        {{ scope.row.fileName }}
                    </el-button>
                </template>
            </el-table-column>
            <el-table-column
                prop="isFile"
                label="类型">
            </el-table-column>
            <el-table-column
                prop="size"
                label="大小">
            </el-table-column>
            <el-table-column
                prop="duplicates"
                label="副本数">
            </el-table-column>
            <el-table-column
                prop="modifyTime"
                label="最后修改时间"
                width="200">
            </el-table-column>
            <el-table-column
                prop="rights"
                label="权限">
            </el-table-column>
            <el-table-column
                prop="owner"
                label="创建者">
            </el-table-column>
            <el-table-column
                prop="group"
                label="组">
            </el-table-column>
            <el-table-column
                label="操作"
                width="250"
                fixed="right">
                <template slot-scope="scope">
                    <el-button type="success" size="mini" plain @click="downLoad(scope.row)">下载</el-button>
                    <el-button type="info" size="mini" @click="renameFile(scope.row)">重命名</el-button>
                    <el-button @click="deleteFile(scope.row)" type="danger" size="mini">删除</el-button>
                    <!--<el-button type="text" size="small">移动</el-button>-->
                </template>
            </el-table-column>
        </el-table>
    </div>
</template>

<script>

import {deleteFile, mkdir, rename, showList} from "@/api/cloudDisk";

export default {
    name: "cloudDisk",
    data() {
        return {
            hdfsData: [],
            currentPath: "/",
            isLoading: false,
            parentPath: "/",
            fileList: [],
        }
    },
    methods: {
        //下载
        downLoad(row) {
            let elemIf = document.createElement('iframe');
            console.log(row.isFile);
            if (row.isFile == '文件') {
                //下载文件
                elemIf.src = '/api/hdfs/file/downLoadFile?fileName=' + row.fileName + "&path=" + row.relativePath;
            } else {
                //下载文件夹
                elemIf.src = '/api/hdfs/file/downLoadDirectoryFile?fileName=' + row.fileName + "&path=" + row.relativePath;
            }
            elemIf.style.display = 'none';
            document.body.appendChild(elemIf);
        },
        //文件上传超出限制
        handleChange(file, fileList) {
            this.fileList = fileList.slice(-1);
        },
        //文件上传成功回调
        uploadSuccess(response, file, fileList) {
            if (response.code == 200) {
                this.showList(this.currentPath);
            } else {
                this.$refs.upload.clearFiles();
                this.$message({
                    type: 'error',
                    message: response.msg,
                })
            }
        },
        //重命名
        renameFile(row) {
            this.$prompt('请修改目录名称', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                inputValue: row.relativePath,
            }).then(({value}) => {
                let params = {
                    srcPath: row.relativePath,
                    newPath: value,
                }
                rename(params).then(resp => {
                    if (resp.code == 200) {
                        this.$message({
                            type: 'success',
                            message: '修改成功'
                        });
                        this.hdfsData[row.index].fileName = resp.data.fileName;
                    }
                })
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '取消修改'
                });
            });
        },
        //新建目录
        mkdirNew() {
            this.$prompt('请输入目录名称', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                inputPattern: /[^/]+$/,
                inputErrorMessage: '格式错误',
            }).then(({value}) => {
                let path;
                if (this.currentPath == "/" || this.currentPath == "") {
                    path = this.currentPath + value;
                } else {
                    path = this.currentPath + "/" + value;
                }
                mkdir(path).then(resp => {
                    if (resp.code == 200) {
                        this.$message({
                            type: 'success',
                            message: resp.msg,
                        });
                        this.showList(this.currentPath);
                    }
                })
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '取消输入'
                });
            });
        },
        //搜索
        searchFiles(path) {
            this.parentPath = this.currentPath.substring(0, this.currentPath.lastIndexOf("/"));
            this.currentPath = path;
            this.showList(path);
        },
        goUp() {
            //返回上一级
            if (this.currentPath == "/" || this.parentPath == " ") {
                this.showList("/");
                return;
            }
            //记录当前页面的父目录
            let temp = this.parentPath;
            //将当前路径变为父目录路径
            this.currentPath = this.parentPath;
            //将父目录路径变为父目录的父目录路径
            this.parentPath = this.parentPath.substring(0, this.parentPath.lastIndexOf("/"));
            this.showList(temp);
        },
        //删除
        deleteFile(row) {
            this.$confirm('此操作将永久删除该文件, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                deleteFile(row.relativePath).then(resp => {
                    if (resp.code == 200) {
                        this.$message({
                            type: 'success',
                            message: resp.msg,
                        });
                        this.showList(this.currentPath);
                    }
                })
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '已取消删除'
                });
            });
        },
        showFiles(row) {
            if (row.isFile == '目录') {
                this.parentPath = this.currentPath;
                this.currentPath = row.relativePath;
                this.showList(row.relativePath);
            } else {
                this.$confirm('是否读取该文件?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    window.sessionStorage.setItem("path", row.relativePath);
                    window.sessionStorage.setItem("fileName", row.fileName);
                    let readUrl = this.$router.resolve({
                        name: "readResult"
                    })
                    window.open(readUrl.href)
                }).catch(() => {
                    this.$message({
                        type: 'info',
                        message: '已取消下载'
                    });
                });
            }
        },
        showList(currentPath) {

            //清除表格
            this.hdfsData.length = 0;
            this.isLoading = true;
            showList(currentPath).then(resp => {
                setTimeout(() => {
                    if (resp.data == null) {
                        this.hdfsData = [];
                    } else {
                        for (let i = 0; i < resp.data.length; i++) {
                            this.hdfsData.push({
                                fileName: resp.data[i].fileName,
                                isFile: resp.data[i].isFile == 'true' ? "文件" : "目录",
                                size: resp.data[i].size,
                                duplicates: resp.data[i].duplicates,
                                modifyTime: resp.data[i].modifyTime,
                                rights: resp.data[i].rights,
                                owner: resp.data[i].owner,
                                group: resp.data[i].group,
                                relativePath: resp.data[i].relativePath,
                                parentPath: resp.data[i].parentPath,
                                index: i,
                            })
                        }
                    }
                    this.isLoading = false;
                }, 500)
            });
        },
    },
    mounted() {
        this.showList(this.currentPath);
    }
}
</script>

<style scoped>
@import "../css/cloudDisk.css";
</style>