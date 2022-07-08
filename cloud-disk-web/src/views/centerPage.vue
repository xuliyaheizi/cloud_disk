<template>
    <!--中控台-->
    <div id="centerPate">
        <div id="gangLia">
            <iframe
                :src="gangliaUrl"
            ></iframe>
        </div>
    </div>
</template>

<script>


export default {
    name: "centerPage",
    data() {
        return {
            gangliaUrl: "http://172.168.0.10/ganglia",
        }
    },
}
</script>

<style scoped>
@import "../css/centerPage.css";
</style>