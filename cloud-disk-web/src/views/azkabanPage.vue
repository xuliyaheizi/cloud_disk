<template>
    <div id="azkabanPage">
        <iframe
            :src="azkabanUrl"
        ></iframe>
    </div>
</template>

<script>
export default {
    name: "azkabanPage",
    data() {
        return {
            azkabanUrl: "https://172.168.0.10:8443/",
        }
    },
}
</script>

<style scoped>

#azkabanPage {
    margin: 10px 10px;
    float: left;
    width: 100%;
    overflow: hidden;
}

#azkabanPage iframe {
    position: absolute;
    transform: scale(.5, .5) translate(-50%, -50%);
    width: 165%;
    height: 160%;
}

</style>