<template>
  <v-card>
    <v-toolbar color="indigo" dark>
      <v-avatar>
        <img :src="getMe.imagePath" alt="avatar" />
      </v-avatar>

      <v-toolbar-title class="ml-3">{{ getMe.username }}</v-toolbar-title>
    </v-toolbar>

    <v-container class="spacing-playground pa-10" fluid>
      <v-form class="mt-5" ref="form" lazy-validation>
        <v-file-input
          accept=".jpg"
          label="Change profile image"
          v-model="file"
          :rules="[
            v =>
              !v || v.size < 2000000 || 'Image size should be less than 2 MB!'
          ]"
        ></v-file-input>
        <v-text-field
          label="Name/Surname"
          v-model="name"
          prepend-icon="mdi-account-box"
          required
          :rules="[v => !!v || 'Name/Surname is required']"
        ></v-text-field>

        <v-text-field
          label="Email"
          prepend-icon="mdi-email"
          v-model="email"
          required
          :rules="[
            v => !!v || 'E-mail is required',
            v => /.+@.+\..+/.test(v) || 'E-mail must be valid'
          ]"
        ></v-text-field>

        <v-card-actions>
          <v-spacer></v-spacer>

          <v-btn color="blue darken-1" text @click.prevent="save">Save</v-btn>
        </v-card-actions>
      </v-form>
    </v-container>
  </v-card>
</template>

<script>
import Vue from "vue";
import { mapGetters, mapMutations } from "vuex";

export default {
  name: "InfoActor",
  data() {
    return {
      file: null
    };
  },
  computed: {
    ...mapGetters(["getMe"]),

    name: {
      get() {
        return this.getMe.name;
      },
      set(newName) {
        return this.setName(newName);
      }
    },
    email: {
      get() {
        return this.getMe.email;
      },
      set(newEmail) {
        return this.setEmail(newEmail);
      }
    }
  },
  methods: {
    ...mapMutations(["setName", "setEmail"]),
    imgUpload() {
      if (this.file === "" || this.file === undefined || this.file === null)
        return;
      let formData = new FormData();
      formData.append("file", this.file);

      //controllo formato immagine, se sbagliata non carica
      if (!this.file.name.includes(".jpg"))
        return Vue.notify({
          group: "foo",
          title: "Unsupported image format ",
          text: "Please select a correct jpg image",
          type: "error"
        });

      this.$store
        .dispatch("UPLIMG", formData)
        .then(() => {
          Vue.notify({
            group: "foo",
            title: "Updated",
            text: "Your image profile has been successfully updated",
            type: "success"
          });
        })
        .catch(err => console.log(err));
    },
    save() {
      if (this.$refs.form.validate()) {
        this.$store.commit("setCharging", true);
        this.imgUpload();
        this.$store
          .dispatch("UPD_ACTOR", {
            username: this.getMe.username,
            name: this.getMe.name,
            email: this.getMe.email,
            role: this.getMe.role.toUpperCase()
          })
          .then(() => {
            this.$store.commit("setCharging", false);
            Vue.notify({
              group: "foo",
              title: "Updated",
              text: "Your account has been successfully updated",
              type: "success"
            });
          })
          .catch(err => {
            console.log(err);
            this.$store.commit("setCharging", false);
          });
      }
    }
  },
  created() {
    this.$store.dispatch("GET_MY_INFO").catch(() => {});
  }
};
</script>
