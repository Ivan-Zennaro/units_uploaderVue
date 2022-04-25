<template>
  <v-card class="mx-auto">
    <v-toolbar color="indigo" dark>
      <v-toolbar-title>List Files of {{ getCurrentConsumer }}</v-toolbar-title>
    </v-toolbar>
    <AddFile :dialogAddFile.sync="dialogAddFile" />
    <Confirm ref="confirm"></Confirm>
    <v-list class="overflow-y-auto">
      <v-list-item @click="openDialog()">
        <v-list-item-content>
          Add new File for {{ getCurrentConsumer }}
        </v-list-item-content>

        <v-list-item-icon>
          <v-icon>mdi-plus</v-icon>
        </v-list-item-icon>
      </v-list-item>

      <v-divider></v-divider>

      <v-list-item v-for="file in getFilesUploader" :key="file.name">
        <v-list-item-avatar>
          <v-icon>{{ getExt(file.url) }}</v-icon>
        </v-list-item-avatar>

        <v-list-item-content @click="openFile(file.url)">
          <v-list-item-title v-text="'Name: ' + file.name"></v-list-item-title>
          <v-list-item-title
            v-text="'Tags: ' + file.hashTags"
          ></v-list-item-title>
          <v-list-item-title v-text="'Ip: ' + file.ipView"></v-list-item-title>
          <v-list-item-title
            v-text="'Date: ' + file.viewedDate"
          ></v-list-item-title>
        </v-list-item-content>

        <v-list-item-avatar @click="deleteFile(file.name)">
          <v-icon>mdi-delete</v-icon>
        </v-list-item-avatar>
      </v-list-item>
    </v-list>
  </v-card>
</template>

<script>
import AddFile from "./AddFile";
import { mapGetters } from "vuex";
import Vue from "vue";
import Confirm from "./Confirm";

export default {
  name: "listfilesupl",

  data() {
    return {
      dialogAddFile: false
    };
  },
  components: {
    AddFile,
    Confirm
  },
  methods: {
    getExt(file) {
      let ext = file
        .substring(file.lastIndexOf(".") + 1, file.length)
        .toLowerCase();
      switch (ext) {
        case "jpg":
          return "mdi-file-image";
        case "png":
          return "mdi-file-image";
        case "mp3":
          return "mdi-music-note";
        default:
          return "mdi-file";
      }
    },
    openDialog() {
      if (this.getCurrentConsumer === "")
        Vue.notify({
          group: "foo",
          title: "No consumer selected",
          text: "Plese select a cosumer",
          type: "error"
        });
      else {
        this.dialogAddFile = true;
        this.$store.dispatch("GET_ACTOR", this.getCurrentConsumer);
      }
    },
    deleteFile(name) {
      this.$refs.confirm
        .open("Delete", "Are you sure?", { color: "indigo" })
        .then(confirm => {
          if (confirm) {
            this.$store.commit("setCharging", true);
            this.$store
              .dispatch("DEL_FILE", {
                name: name,
                consumer: this.getCurrentConsumer,
                uploader: this.getFilesUploader[0].uploader
              })
              .then(() => {
                Vue.notify({
                  group: "foo",
                  title: "Deleted ",
                  text: "File correctly deleted",
                  type: "success"
                });
                this.$store.commit("setCharging", false);
              })
              .catch(err => {
                console.log(err);
                this.$store.commit("setCharging", false);
              });
          }
        });
    },
    openFile(url) {
      window.open(url);
    }
  },
  computed: {
    ...mapGetters(["getCurrentConsumer", "getFilesUploader"])
  }
};
</script>

<style>
.v-list {
  height: 75vh;
}
.v-list-item:hover {
  background-color: #f2f2f2;
  cursor: pointer;
}
</style>
