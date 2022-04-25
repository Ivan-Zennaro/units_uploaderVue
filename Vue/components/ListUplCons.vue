<template>
  <v-card class="mx-auto">
    <v-toolbar color="indigo" dark>
      <v-toolbar-title>List of Uploader</v-toolbar-title>
    </v-toolbar>
    <ListFilesCons :dialogList.sync="dialogList" />
    <v-list class="overflow-y-auto">
      <v-list-item
        v-for="actor in getListUploader"
        :key="actor.username"
        @click="doAction(actor.username, actor.imagePath)"
      >
        <v-list-item-avatar>
          <v-img :src="actor.imagePath" alt="avatar"></v-img>
        </v-list-item-avatar>

        <v-list-item-content>
          <v-list-item-title v-text="actor.name"></v-list-item-title>
          <v-list-item-subtitle v-text="actor.email"></v-list-item-subtitle>
        </v-list-item-content>
      </v-list-item>
    </v-list>
  </v-card>
</template>

<script>
import ListFilesCons from "../components/ListFilesCons";
import { mapGetters } from "vuex";
export default {
  name: "listConsumers",
  components: {
    ListFilesCons
  },
  data() {
    return {
      dialogList: false
    };
  },

  methods: {
    doAction(username, img) {
      this.dialogList = true;
      this.$store.commit("setCurrentUpl", {
        username: username,
        imagePath: img
      });
      this.$store.dispatch("GET_FILES_CONS", username).catch(() => {});
    }
  },

  computed: {
    ...mapGetters(["getListUploader", "getFilesConsumer"])
  },
  created() {
    this.$store
      .dispatch("GET_MY_UPLS")
      //appertura automatica schermata file nel caso di un solo uploader
      .then(() => {
        if (this.getListUploader.length === 1)
          this.doAction(
            this.getListUploader[0].username,
            this.getListUploader[0].imagePath
          );
      })
      .catch(err => console.log(err));
  }
};
</script>

<style scoped>
.v-list {
  height: 75vh;
}
</style>
