import axios from "axios";

const state = {};
const getters = {};

const actions = {
  REGISTER: ({ commit }, payload) => {
    return new Promise((resolve, reject) => {
      axios
        .post("auth/registration", payload)
        .then(({ data, status }) => {
          if (status === 200) {
            commit("registration", data);
            resolve(true);
          } else reject();
        })
        .catch(error => reject(error));
    });
  },

  LOGIN: ({ commit }, { username, password }) => {
    return new Promise((resolve, reject) => {
      axios
        .post("auth/check_credential", { username, password })
        .then(({ data, status }) => {
          if (status === 200) {
            commit("registration", data);
            resolve(data.role);
          } else reject();
        })
        .catch(error => reject(error));
    });
  },

  // mando il token se è valido mi ritorna il ruolo
  // dell' attore associato al token
  SND_TOKEN: ({ commit }) => {
    return new Promise((resolve, reject) => {
      axios
        .get("auth/access_token")
        .then(({ data, status }) => {
          if (status === 200) {
            resolve(data);
          } else reject();
        })
        .catch(() => commit("notLogged"));
    });
  }
};

const mutations = {
  registration: (state, { token, role }) => {
    console.log(token);
    localStorage.setItem("token", token);
    localStorage.setItem("role", role);
  },
  notLogged: () => localStorage.clear()
};

export default {
  state,
  getters,
  actions,
  mutations
};
