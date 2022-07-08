<template>
    <div id="rmNodePage">
        <iframe
            :src="rmUrl"
        ></iframe>
    </div>
</template>

<script>
export default {
    name: "rmNodePage",
    data() {
        return {
            rmUrl: "",
        }
    },
    mounted() {
        this.rmUrl = '/static/resourcenode.html';
    }
}
</script>

<style scoped>
#rmNodePage {
    margin: 10px 10px;
    float: left;
    width: 100%;
    overflow: hidden;
}

#rmNodePage iframe {
    position: absolute;
    transform: scale(.5, .5) translate(-50%, -50%);
    width: 165%;
    height: 160%;
}
</style>