import axios from "axios";

//stati di default
const getDefaultState = () => {
  return {
    // actorProxy dell'attore corrente loggato
    me: {}, // tutte le view

    //uploader scelto per visualizzare i file
    currentUploader: "", //consumer view

    //uploader che hanno caricato almeno un file per consumer corrente
    listUploaders: [], //consumer view

    //file caricati da currentUploader per consumer corrente
    filesConsumer: [], //consumer view

    //lista filtrata di filesConsumer in base ai tags
    filesConsumerFiltered: [], //consumer view

    // lista hashtags di tutti i filesConsumer, rimane immutata
    // serve per mostrarla a schermo al consumer
    listTags: [], //consumer view

    //lista hashtags selezionati
    listTagsFiltered: [], //consumer view

    //icona di caricamento
    charging: false //tutte le view
  };
};

const state = getDefaultState();

const getters = {
  getMe: state => state.me,
  getCurrentUploader: state => state.currentUploader,
  getFilesConsumer: state => state.filesConsumer,
  getFilesConsumerFiltered: state => state.filesConsumerFiltered,
  getListUploader: state => state.listUploaders,
  getListTags: state => state.listTags,
  getListTagsFiltered: state => state.listTagsFiltered,
  getCharging: state => state.charging
};

const actions = {
  // popola listUploaders basandosi sul jwt
  GET_MY_UPLS: ({ commit }) => {
    return new Promise((resolve, reject) => {
      axios
        .get("/cons/getmyupls/")
        .then(({ data, status }) => {
          if (status === 200) {
            commit("setListUploaders", data);
            resolve(true);
          }
        })
        .catch(error => reject(error));
    });
  },

  // popola filesConsumer basandosi su username uploader e jwt
  GET_FILES_CONS: ({ commit }, username) => {
    return new Promise((resolve, reject) => {
      axios
        .get(`/cons/getfilecons/${username}`)
        .then(({ data, status }) => {
          if (status === 200) {
            commit("setFileCons", data);
            resolve(true);
          }
        })
        .catch(error => reject(error));
    });
  },

  // invia url del file letto, il server aggiorna il file
  // (ip e data di visione) e lo restituisce
  UPD_FILE_VIEW: ({ commit }, url) => {
    return new Promise((resolve, reject) => {
      axios
        .post("/cons/readfilecons/", { url })
        .then(({ data, status }) => {
          if (status === 200) {
            commit("updFileView", data);
            resolve(true);
          }
        })
        .catch(error => reject(error));
    });
  },

  // setta me
  async GET_MY_INFO({ commit }) {
    const response = await axios.get("/cons/getinfo");
    commit("setMyData", response.data);
  },

  UPLIMG: ({ commit }, payload) => {
    return new Promise((resolve, reject) => {
      axios
        .post("/cons/uplimg/", payload)
        .then(({ data, status }) => {
          if (status === 200) {
            commit("setNewImg", data);
            resolve(true);
          } else reject();
        })
        .catch(error => {
          reject(error);
        });
    });
  },

  UPD_PASS: ({ commit }, { prev_pass, new_pass }) => {
    return new Promise((resolve, reject) => {
      axios
        .post(`/cons/updpass?prev_pass=${prev_pass}&new_pass=${new_pass}`)
        .then(({ status }) => {
          if (status === 200) {
            resolve(true);
            commit();
          } else reject();
        })
        .catch(err => reject(err));
    });
  }
};

const mutations = {
  setCharging: (state, bool) => (state.charging = bool),
  setListUploaders: (state, upls) => (state.listUploaders = upls),
  setMyData: (state, me) => (state.me = me),
  setName: (state, name) => (state.me.name = name),
  setEmail: (state, email) => (state.me.email = email),
  setCurrentUpl: (state, data) => (state.currentUploader = data),
  removeAllCons: state => Object.assign(state, getDefaultState()),
  setNewImg: (state, data) => (state.me.imagePath = data.imagePath),
  setFileCons: (state, files) => {
    //inizialmente vettore filtrato = al vettore completo
    state.filesConsumer = state.filesConsumerFiltered = files;
    state.listTags = [];
    state.listTagsFiltered = [];
    // riempio i vettori dei tags
    for (let i = 0; i < files.length; i++) {
      //per ogni file: ottengo vettore di hashtags
      let res = files[i].hashTags.split(",");
      for (let j = 0; j < res.length; j++) {
        // per ogni hashtag
        if (!state.listTags.includes(res[j]) && res[j] != "") {
          // se non è già presente: lo aggiungo
          state.listTags.unshift(res[j]);
          state.listTagsFiltered.unshift(res[j]);
        }
      }
    }
    //riordino per data i file consumer
    state.filesConsumerFiltered.sort(
      (a, b) => -a.viewedDate.localeCompare(b.viewedDate)
    );
  },
  // modifico ip e data di visulizzazione del file visualizzato
  updFileView: (state, file) => {
    state.filesConsumer.find(f => f.name === file.name).ipView = file.ipView;
    state.filesConsumer.find(f => f.name === file.name).viewedDate =
      file.viewedDate;
    state.filesConsumerFiltered.find(f => f.name === file.name).ipView =
      file.ipView;
    state.filesConsumerFiltered.find(f => f.name === file.name).viewedDate =
      file.viewedDate;
  },
  // chiamato dal component ogni volta che triggero evento change di checkbox hashtags
  updListFilter: (state, { bool, tag }) => {
    //aggiungo (bool = true) o rimuovo (false) dalla lista filtrata il tag
    if (bool === true) state.listTagsFiltered.unshift(tag);
    else
      state.listTagsFiltered = state.listTagsFiltered.filter(
        ctag => ctag !== tag
      );

    //aggiorno la lista di file filtrati
    state.filesConsumerFiltered = []; //la assegno ad un vettore vuoto
    for (let i = 0; i < state.filesConsumer.length; i++) {
      // per ogni file: salvo i tags e lo contrassegno da tenere
      let tagsCurrentFile = state.filesConsumer[i].hashTags.split(",");
      let toKeep = true;
      for (let j = 0; j < tagsCurrentFile.length && toKeep; j++) {
        // per ogni tags del file se questo non è presente nella lista
        // tags checked allora non è da tenere
        if (!state.listTagsFiltered.includes(tagsCurrentFile[j]))
          toKeep = false;
      }
      if (toKeep) state.filesConsumerFiltered.unshift(state.filesConsumer[i]); //elimino
    }
    //riordino in base alla data
    state.filesConsumerFiltered.sort(
      (a, b) => -a.viewedDate.localeCompare(b.viewedDate)
    );
  }
};

export default {
  state,
  getters,
  actions,
  mutations
};
