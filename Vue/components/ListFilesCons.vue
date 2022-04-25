<template>
  <v-row justify="center">
    <v-dialog v-model="dialogList" persistent max-width="800px">
      <v-card class="mx-auto">
        <v-toolbar color="indigo" dark>
          <v-avatar>
            <img :src="getCurrentUploader.imagePath" alt="avatar" />
          </v-avatar>
          <v-toolbar-title class="ml-5">{{
            getCurrentUploader.username
          }}</v-toolbar-title>
        </v-toolbar>
        <v-row justify="center" style="width:100%">
          <v-col cols="12" lg="7" md="7" sm="7">
            <v-list class="overflow-y-auto">
              <v-card-title class="ml-4">File list:</v-card-title>
              <v-list-item
                v-for="file in getFilesConsumerFiltered"
                :key="file.name"
                @click="openFile(file.url)"
                v-bind:class="{
                  notVisualized: checkVisualized(file.viewedDate)
                }"
              >
                <v-list-item-avatar>
                  <v-icon>{{ getExt(file.url) }}</v-icon>
                </v-list-item-avatar>

                <v-list-item-content>
                  <v-list-item-title
                    v-text="'Name: ' + file.name"
                  ></v-list-item-title>
                  <v-list-item-title
                    v-text="'Tags: ' + file.hashTags"
                  ></v-list-item-title>
                  <v-list-item-title
                    v-text="'Ip: ' + file.ipView"
                  ></v-list-item-title>
                  <v-list-item-title
                    v-text="'Date: ' + file.viewedDate"
                  ></v-list-item-title>
                </v-list-item-content>

                <v-list-item-avatar v-if="checkVisualized(file.viewedDate)">
                  <strong>New</strong>
                </v-list-item-avatar>
              </v-list-item>
            </v-list>
          </v-col>

          <v-col cols="12" lg="4" md="4" sm="4">
            <v-list class="overflow-y-auto">
              <v-card-title>Filter by Tags:</v-card-title>

              <v-list-item v-for="tag in getListTags" :key="tag">
                <v-checkbox
                  :label="tag"
                  input-value="true"
                  @change="updList($event, tag)"
                ></v-checkbox>
              </v-list-item>
            </v-list>
          </v-col>
        </v-row>

        <v-card-actions>
          <v-btn color="blue darken-1" text @click.native="close">Chiudi</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script>
import { mapGetters } from "vuex";
export default {
  name: "listfilesupl",

  data() {
    return {};
  },

  props: {
    dialogList: { default: false }
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

    checkVisualized(date) {
      return date === "Not visualized";
    },
    close() {
      this.$emit("update:dialogList", false);
    },
    openFile(url) {
      this.$store
        .dispatch("UPD_FILE_VIEW", url)
        .then(() => window.open(url))
        .catch(err => console.log(err));
    },
    updList(bool, tag) {
      this.$store.commit("updListFilter", { bool, tag });
    }
  },
  computed: {
    ...mapGetters([
      "getCurrentUploader",
      "getFilesConsumerFiltered",
      "getListTags"
    ])
  }
};
</script>

<style scoped>
.v-list {
  height: 60vh;
}
.notVisualized {
  background-color: #ffffe0;
}
</style>
