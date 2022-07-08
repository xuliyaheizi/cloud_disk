<template>
    <div id="readResult">
        <p v-html="result"></p>
    </div>
</template>
<script>
import {read} from "@/api/cloudDisk";

export default {
    name: "readResult",
    data() {
        return {
            fileName: "",
            path: "",
            result: "",
        }
    },
    methods: {
        initRead() {
            this.fileName = window.sessionStorage.getItem("fileName");
            this.path = window.sessionStorage.getItem("path");
            window.document.title = this.fileName;
            let params = {
                path: this.path,
            }
            read(params).then(resp => {
                if (resp.code == 200) {
                    this.result = resp.data;
                } else {
                    this.$message({
                        type: "error",
                        message: resp.msg,
                    })
                }
            })
        }
    },
    mounted() {
        this.initRead();
    }
}
</script>

<style scoped>
#readResult {
    font-size: 13px;
    font-weight: normal;
}

#readResult p {
    line-height: 1.2rem;
}
</style>