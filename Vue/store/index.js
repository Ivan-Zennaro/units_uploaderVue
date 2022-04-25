import Vue from "vue";
import Vuex from "vuex";

import cons_module from "./modules/cons_module";
import auth_module from "./modules/auth_module";
import upl_adm_module from "./modules/upl_adm_module";

Vue.use(Vuex);

export default new Vuex.Store({
  modules: {
    cons_module,
    auth_module,
    upl_adm_module
  }
});
