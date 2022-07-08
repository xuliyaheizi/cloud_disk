import Vue from "vue";
import VueRouter from "vue-router";

Vue.use(VueRouter);

const routes = [
  {
    path: "/",
    name: "login",
    component: () => import("@/views/login"),
    meta: {
      title: "登录",
    },
  },
  {
    path: "/indexPage",
    name: "indexPage",
    component: () => import("@/views/indexPage"),
    children: [
      {
        path: "",
        name: "cloudDisk",
        component: () => import("@/views/cloudDisk"),
        meta: {
          title: "在线云盘",
        },
      },
      {
        path: "centerPage",
        name: "centerPage",
        component: () => import("@/views/centerPage"),
        meta: {
          title: "中控台",
        },
      },
      {
        path: "azkabanPage",
        name: "azkabanPage",
        component: () => import("@/views/azkabanPage"),
        meta: {
          title: "azkaban管理台",
        },
      },
      {
        path: "nmPage",
        name: "nmPage",
        component: () => import("@/views/nmPage"),
        meta: {
          title: "nmNode管理台",
        },
      },
      {
        path: "rmNodePage",
        name: "rmNodePage",
        component: () => import("@/views/rmNodePage"),
        meta: {
          title: "rmNode管理台",
        },
      },
      {
        path: "cloudreve",
        name: "cloudreve",
        component: () => import("@/views/cloudreve"),
      },
    ],
  },
  {
    path: "/readResult",
    name: "readResult",
    component: () => import("@/views/readResult"),
  },
];
const router = new VueRouter({
  // mode: "history",
  base: process.env.BASE_URL,
  routes,
});

export default router;
