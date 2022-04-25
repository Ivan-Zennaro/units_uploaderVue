<template>
  <v-card class="mx-auto">
    <v-toolbar color="indigo" dark>
      <v-toolbar-title>List {{ this.role }}</v-toolbar-title>
    </v-toolbar>
    <AddActor :dialogAdd.sync="dialogAdd" :role="this.role" />
    <Confirm ref="confirm"></Confirm>
    <ModifyActor
      :dialogModify.sync="dialogModify"
      :role="this.role"
      ref="modActor"
    />
    <v-list class="overflow-y-auto">
      <v-list-item @click="dialogAdd = true">
        <v-list-item-content>Add new {{ this.role }}</v-list-item-content>
        <v-list-item-icon>
          <v-icon>mdi-plus</v-icon>
        </v-list-item-icon>
      </v-list-item>

      <v-divider></v-divider>

      <v-list-item
        v-for="actor in getCurrentList"
        :key="actor.username"
        
        v-bind:class="{ thisMe: actor.username === getMe.username }"
      >
        <v-list-item-avatar>
          <v-img :src="actor.imagePath" alt="avatar"></v-img>
        </v-list-item-avatar>

        <v-list-item-content @click="doAction(actor.username)">
          <v-list-item-title v-text="actor.name"></v-list-item-title>
          <v-list-item-subtitle v-text="actor.email"></v-list-item-subtitle>
        </v-list-item-content>

        <v-list-item-icon @click="modifyActor(actor.username)">
          <v-icon>mdi-pen</v-icon>
        </v-list-item-icon>

        <v-list-item-icon
          @click.prevent="deleteActor(actor.username)"
          v-if="actor.username !== getMe.username"
        >
          <v-icon>mdi-delete</v-icon>
        </v-list-item-icon>
      </v-list-item>
    </v-list>
  </v-card>
</template>

<script>
import AddActor from "./AddActor";
import ModifyActor from "./ModifyActor";
import { mapGetters } from "vuex";
import Confirm from "./Confirm";
import Vue from "vue";

export default {
  name: "listConsumers",
  data() {
    return {
      dialogAdd: false,
      dialogModify: false
    };
  },
  components: {
    AddActor,
    ModifyActor,
    Confirm
  },
  methods: {
    deleteActor(username) {
      this.$refs.confirm
        .open("Delete", "Are you sure?", { color: "indigo" })
        .then(confirm => {
          if (confirm) {
            // evita che si possa caricare un file per un consumer eliminato
            this.$store.commit("setCurrentCons", "");
            this.$store.commit("setFilesUpl", []);
            // evito che si possano vedere le stats di un uploader eliminati
            this.$store.commit("setCurrentStats", {
              username: "",
              stat1: "",
              stat2: ""
            });
            // richiedo l'eliminazione dell'attore
            this.$store.commit("setCharging", true);
            this.$store
              .dispatch("DEL_ACTOR", username)
              .then(() => {
                Vue.notify({
                  group: "foo",
                  title: "Deleted",
                  text: `${this.role} succeddful deleted`,
                  type: "success"
                });
                this.$store.commit("setCharging", false);
              })
              .catch(() => this.$store.commit("setCharging", false));
          }
        });
    },
    modifyActor(username) {
      this.$store
        .dispatch("GET_ACTOR", username)
        .then(() => {
          this.$refs.modActor.updateForm();
          this.dialogModify = true;
        })
        .catch(err => console.log(err));
    },
    doAction(username) {
      // azione da fare se ci troviamo in lista uploader (in admin)
      if (this.role.toLowerCase() === "uploader") {
        this.$store
          .dispatch("GET_STATS_ACTOR", username)
          .then(() =>
            Vue.notify({
              group: "foo",
              title: `Stats update`,
              text: `Stats of  ${username} has been successfully updated`,
              type: "success"
            })
          )
          .catch(err => console.log(err));
      }
      // azione da fare se ci troviamo in lista consumer (in uploader)
      if (this.role.toLowerCase() === "consumer") {
        this.$store
          .dispatch("GET_FILES_UPL", username)
          .catch(err => console.log(err));
      }
    }
  },
  props: {
    role: { type: String }
  },
  computed: {
    ...mapGetters(["getMe"]),
    getCurrentList() {
      switch (this.role.toLowerCase()) {
        case "uploader":
          return this.$store.getters.allUploader;
        case "admin":
          return this.$store.getters.allAdmin;
        case "consumer": {
          return this.$store.getters.allConsumer;
        }
      }
      return "";
    }
  },
  created() {
    if (this.role.toLowerCase() === "uploader") {
      this.$store.dispatch("GET_UPLOADER_LIST").catch(() => {});
    } else if (this.role.toLowerCase() === "admin") {
      this.$store.dispatch("GET_ADMIN_LIST").catch(() => {});
    } else if (this.role.toLowerCase() === "consumer") {
      this.$store.dispatch("GET_CONSUMER_LIST").catch(() => {});
    }
  }
};
</script>

<style scoped>
.v-list {
  height: 75vh;
}
.thisMe {
  background-color: #90ee90;
}
.v-list-item:hover {
  background-color: #f2f2f2;
  cursor: pointer;
}
</style>
