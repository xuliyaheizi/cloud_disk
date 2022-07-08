<template>
    <div id="nmPage">
        <iframe
            :src="nmUrl"
        ></iframe>
    </div>
</template>

<script>
export default {
    name: "nmPage",
    data() {
        return {
            nmUrl: "",
        }
    },
    mounted() {
        this.nmUrl = '/static/namenode.html';
    }
}
</script>

<style scoped>
#nmPage {
    margin: 10px 10px;
    float: left;
    width: 100%;
    overflow: hidden;
}

#nmPage iframe {
    position: absolute;
    transform: scale(.5, .5) translate(-50%, -50%);
    width: 165%;
    height: 160%;
}
</style>