import Vue from "vue";
import VueRouter from "vue-router";
import Login from "../components/Auth/Login";
import Signup from "../components/Auth/Signup";
import Uploader from "../views/Uploader";
import Consumer from "../views/Consumer";
import Admin from "../views/Admin";
import Logout from "../components/Auth/Logout";
import axios from "axios";

Vue.use(VueRouter);

axios.interceptors.request.use(config => {
  config.baseURL = process.env.VUE_APP_APIROOT;
  config.headers["Authorization"] = `Bearer ${localStorage.getItem("token")}`;
  return config;
});

axios.interceptors.response.use(response => {
  if (response.data === "CHANGE TOKEN") {
    router.push({ name: "logout" }).catch(() => {});
    return Promise.reject(false);
  } else if (response.data === "NO TOKEN") {
    router.push({ name: "login" }).catch(() => {});
    return Promise.reject(false);
  }
  return response;
});

const routes = [
  {
    path: "/",
    name: "login",
    component: Login
  },
  {
    path: "/signup",
    name: "signup",
    component: Signup
  },
  {
    path: "/uploader_home",
    name: "uploader",
    component: Uploader,
    meta: {
      requiresAuthUpl: true
    }
  },
  {
    path: "/consumer_home",
    name: "consumer",
    component: Consumer,
    meta: {
      requiresAuthCons: true
    }
  },
  {
    path: "/admin_home",
    name: "admin",
    component: Admin,
    meta: {
      requiresAuthAdm: true
    }
  },
  {
    path: "/logout",
    name: "logout",
    component: Logout
  }
];

const router = new VueRouter({
  routes
});

router.beforeEach((to, from, next) => {
  let token = localStorage.getItem("token");
  let role = localStorage.getItem("role");

  if (to.matched.some(route => route.meta.requiresAuthAdm)) {
    if (token === null || role !== "ADMIN") return next({ path: "/" });
  } else if (to.matched.some(route => route.meta.requiresAuthCons)) {
    if (token === null || role !== "CONSUMER") return next({ path: "/" });
  } else if (to.matched.some(route => route.meta.requiresAuthUpl)) {
    if (token === null || role !== "UPLOADER") return next({ path: "/" });
  }

  next();
});

export default router;
