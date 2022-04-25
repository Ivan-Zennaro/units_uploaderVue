<template>
  <v-row justify="center">
    <v-dialog v-model="dialogModify" persistent max-width="600px">
      <v-card>
        <v-card-title>
          <span class="headline"
            >Modify {{ this.role }} - {{ this.username }}</span
          >
        </v-card-title>
        <v-card-text>
          <v-container>
            <v-row>
              <v-col cols="12">
                <v-text-field
                  label="Name/Surname"
                  v-model="name"
                  prepend-icon="mdi-account-box"
                  required
                  :error-messages="nameErrors"
                  @input="$v.name.$touch()"
                  @blur="$v.name.$touch()"
                ></v-text-field>
              </v-col>
              <v-col cols="12">
                <v-text-field
                  label="Email"
                  prepend-icon="mdi-email"
                  v-model="email"
                  required
                  :error-messages="emailErrors"
                  @input="$v.email.$touch()"
                  @blur="$v.email.$touch()"
                ></v-text-field>
              </v-col>
            </v-row>
          </v-container>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue darken-1" text @click="close">
            Close
          </v-btn>
          <v-btn color="blue darken-1" text @click.prevent="modifyActor">
            Modify
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script>
import Vue from "vue";
import { checksForm } from "./checkForm";
import { mapGetters } from "vuex";

export default {
  name: "modifyActor",
  mixins: [checksForm],

  data: () => ({
    username: "",
    name: "",
    email: ""
  }),

  props: {
    dialogModify: { default: false },
    role: { type: String }
  },

  computed: {
    ...mapGetters(["singleActor"])
  },
  methods: {
    close() {
      this.error = false;
      this.$emit("update:dialogModify", false);
    },

    updateForm() {
      this.username = this.singleActor.username;
      this.name = this.singleActor.name;
      this.email = this.singleActor.email;
    },

    //modifica qua copiando a destra
    modifyActor() {
      this.error = false;
      this.$v.$touch();
      if (!this.$v.$invalid) {
        this.$emit("update:dialogModify", false); // nascondo il dialog Add file
        this.$store.commit("setCharging", true); // setto il caricamento
        this.$store
          .dispatch("UPD_ACTOR", {
            username: this.username,
            name: this.name,
            email: this.email,
            role: this.role.toUpperCase()
          })
          .then(() => {
            Vue.notify({
              group: "foo",
              title: `${this.role} modify`,
              text: `${this.role}  has been successfully modify`,
              type: "success"
            });
            this.$store.commit("setCharging", false); //se ok -> stop caricamento
          })
          .catch(err => {
            console.log(err);
            Vue.notify({
              group: "foo",
              title: "Error",
              text: "Error while modifying actor",
              type: "error"
            });
            this.$store.commit("setCharging", false); //se ok -> stop caricamento
          });
      }
    }
  }
};
</script>

<style></style>
