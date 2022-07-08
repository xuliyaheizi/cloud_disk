<template>
    <div id="cloudreve">
        <iframe
            :src="cloudreveUrl"
        ></iframe>
    </div>
</template>

<script>
export default {
    name: "cloudreve",
    data() {
        return {
            cloudreveUrl: "http://zhulinz.top:5212/home?path=%2F",
        }
    },
}
</script>

<style scoped>
#cloudreve {
    margin: 10px 10px;
    float: left;
    width: 100%;
    overflow: hidden;
}

#cloudreve iframe {
    position: absolute;
    transform: scale(.5, .5) translate(-50%, -50%);
    width: 165%;
    height: 160%;
}
</style>