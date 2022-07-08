const { defineConfig } = require("@vue/cli-service");
module.exports = defineConfig({
  // publicPath: "./",
  outputDir: "dist",
  lintOnSave: false,
  transpileDependencies: true,
  devServer: {
    host: "localhost",
    port: 80,
    proxy: {
      "/api": {
        target: "http://localhost:9001",
        changeOrigin: true, // target是域名的话，需要这个参数
        security: false, // 设置支持https协议的代理
        pathRewrite: {
          "^/api": "",
        },
      },
    },
  },
});
