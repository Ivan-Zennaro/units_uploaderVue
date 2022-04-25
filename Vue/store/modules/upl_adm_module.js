import axios from "axios";

const getDefaultState = () => {
  return {
    // Salva l'attore che si vuole modificare
    currentActor: "", //uploader e admin view

    //lista uploaders generale
    uploaders: [], // admin view

    //lista admins generale
    admins: [], // admin view

    stat1: "", // admin view
    stat2: "", // admin view
    //nome uploader di cui sto visualizzando le stats
    currentStat: "", // admin view

    //lista consumer generale
    consumers: [], //uploader view

    //Salva il consumer selezionato per cui vengono mostrati i documenti caricati
    currentConsumer: "", //uploader view
    filesUploader: [] //uploader view
  };
};

const state = getDefaultState();

const getters = {
  singleActor: state => state.currentActor,
  allConsumer: state => state.consumers,
  getCurrentConsumer: state => state.currentConsumer,
  getFilesUploader: state => state.filesUploader,
  allUploader: state => state.uploaders,
  allAdmin: state => state.admins,
  getstat1: state => state.stat1,
  getstat2: state => state.stat2,
  getCurrentStat: state => state.currentStat
};

const actions = {
  //uploader
  ADD_FILE: ({ commit }, payload) => {
    return new Promise((resolve, reject) => {
      axios
        .post("/upl/uploadfile", payload)
        .then(({ data, status }) => {
          if (status === 200) {
            commit("addFileUploader", data);
            resolve();
          }
        })
        .catch(err => reject(err));
    });
  },

  //uploader
  async GET_FILES_UPL({ commit }, consumer) {
    commit("setCurrentCons", consumer);
    const response = await axios.get("/upl/getfilesupl/" + consumer);
    commit("setFilesUpl", response.data);
  },

  //uploader
  DEL_FILE: ({ commit }, { name, consumer, uploader }) => {
    return new Promise((resolve, reject) => {
      axios
        .delete(`/upl/delfile/${uploader}_${consumer}_${name}`)
        .then(({ status }) => {
          if (status === 200) {
            commit("removeFile", name);
            resolve();
          }
        })
        .catch(err => reject(err));
    });
  },

  //uploader e admin
  CREATE_NEW: ({ commit }, actor) => {
    return new Promise((resolve, reject) => {
      axios
        .post("upl/newactor", actor)
        .then(({ data, status }) => {
          if (status === 200) {
            switch (data.role.toLowerCase()) {
              case "uploader":
                commit("addUploader", data);
                break;
              case "consumer":
                commit("addConsumer", data);
                break;
              case "admin":
                commit("addAdmin", data);
                break;
            }
            resolve(true);
          } else reject();
        })
        .catch(err => reject(err));
    });
  },
  //uploader e admin: serve per modificare l'actor
  GET_ACTOR: ({ commit }, username) => {
    return new Promise((resolve, reject) => {
      axios
        .get(`upl/getactor/${username}`)
        .then(({ data, status }) => {
          if (status === 200) {
            commit("setCurrentActor", data);
            resolve(true);
          } else reject();
        })
        .catch(err => reject(err));
    });
  },

  //uploader e admin view: serve per modificare l'actor
  UPD_ACTOR: ({ commit }, { username, name, email, role }) => {
    return new Promise((resolve, reject) => {
      axios
        .post("cons/updactor", { username, name, email, role })
        .then(({ status }) => {
          if (status === 200) {
            commit("updateCurrentActor", { username, name, email, role });
            resolve(true);
          } else reject();
        })
        .catch(err => reject(err));
    });
  },

  // uploader view
  async GET_CONSUMER_LIST({ commit }) {
    const response = await axios.get("/upl/listcons");
    commit("setConsumers", response.data);
  },

  //uploader e admin view
  async DEL_ACTOR({ commit }, username) {
    await axios.delete(`/upl/delactor/${username}`);
    commit("removeActor", username);
  },

  //admins: acquisisce stats tra date specificate
  GET_STATS_UPDATE: ({ commit }, { username, from, until }) => {
    return new Promise((resolve, reject) => {
      axios
        .get(`admin/getstats/${username}/${from}/${until}`)
        .then(({ data, status }) => {
          if (status === 200) {
            commit("setCurrentStats", data);
            resolve(true);
          } else reject();
        })
        .catch(err => reject(err));
    });
  },

  //admins: aquisisce stats mensili actor
  async GET_STATS_ACTOR({ commit }, uploader) {
    const response = await axios.get(`admin/getstats/${uploader}`);
    commit("setCurrentStats", response.data);
  },

  //admins
  async GET_UPLOADER_LIST({ commit }) {
    const response = await axios.get("/admin/listupl");
    commit("setUploaders", response.data);
  },

  //admins
  async GET_ADMIN_LIST({ commit }) {
    const response = await axios.get("/admin/listadmin");
    commit("setAdmins", response.data);
  }
};

const mutations = {
  addUploader: (state, uploader) => state.uploaders.unshift(uploader),
  addAdmin: (state, admin) => state.admins.unshift(admin),
  addConsumer: (state, consumer) => state.consumers.unshift(consumer),
  setFilesUpl: (state, files) => {
    state.filesUploader = files;
    state.filesUploader.sort(
      (a, b) => -a.viewedDate.localeCompare(b.viewedDate)
    );
  },
  setCurrentActor: (state, actor) => (state.currentActor = actor),
  setConsumers: (state, consumers) => (state.consumers = consumers),
  setCurrentCons: (state, username) => (state.currentConsumer = username),
  addFileUploader: (state, file) => state.filesUploader.unshift(file),
  removeActor: (state, username) => {
    state.uploaders = state.uploaders.filter(
      uploader => uploader.username !== username
    );
    state.admins = state.admins.filter(admin => admin.username !== username);
    state.consumers = state.consumers.filter(
      consumer => consumer.username !== username
    );
  },
  removeFile: (state, name) =>
    (state.filesUploader = state.filesUploader.filter(
      file => file.name !== name
    )),
  updateCurrentActor: (state, { username, name, email, role }) => {
    var act; // punta al attore da modificare
    if (role == "ADMIN")
      act = state.admins.find(actor => actor.username === username);
    else if (role == "UPLOADER")
      act = state.uploaders.find(actor => actor.username === username);
    else if (role == "CONSUMER")
      act = state.consumers.find(actor => actor.username === username);

    if (act !== undefined) {
      act.name = name;
      act.email = email;
    }
  },

  //admins
  setUploaders: (state, uploaders) => (state.uploaders = uploaders),
  setAdmins: (state, admins) => (state.admins = admins),

  setCurrentStats: (state, data) => {
    state.currentStat = data.username;
    state.stat1 = data.stat1;
    state.stat2 = data.stat2;
  },
  removeAllAdmin: state => Object.assign(state, getDefaultState())
};

export default {
  state,
  getters,
  actions,
  mutations
};
