<template>
  <v-row justify="center">
    <v-dialog v-model="dialogAdd" persistent max-width="600px">
      <v-card>
        <v-form ref="form">
          <v-card-title>
            <span class="headline">Add {{ this.role }}</span>
          </v-card-title>

          <v-card-text>
            <v-container>
              <v-alert color="error" :value="error" type="error">
                Username already used
              </v-alert>
              <v-row>
                <v-col cols="12" sm="6" md="6">
                  <v-text-field
                    prepend-icon="mdi-account"
                    label="Username"
                    v-model="username"
                    required
                    :error-messages="usernameErrors"
                    @input="$v.username.$touch()"
                    @blur="$v.username.$touch()"
                  ></v-text-field>
                </v-col>
                <v-spacer></v-spacer>
                <v-col cols="12" sm="6" md="6">
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
                <v-col cols="12">
                  <v-text-field
                    label="Password"
                    prepend-icon="mdi-lock"
                    v-model="password"
                    :append-icon="showPass ? 'mdi-eye' : 'mdi-eye-off'"
                    :type="showPass ? 'text' : 'password'"
                    @click:append="showPass = !showPass"
                    :error-messages="passErrors"
                    @input="$v.password.$touch()"
                    @blur="$v.password.$touch()"
                    required
                  ></v-text-field>
                </v-col>
                <v-col cols="12">
                  <v-text-field
                    label="Confirm Password"
                    prepend-icon="mdi-lock"
                    v-model="confirm_password"
                    type="password"
                    :error-messages="confirmPassErrors"
                    @input="$v.confirm_password.$touch()"
                    @blur="$v.confirm_password.$touch()"
                    required
                  ></v-text-field>
                </v-col>
              </v-row>
            </v-container>
          </v-card-text>
          <v-card-actions>
            <v-btn color="blue darken-1" text @click="reset">Reset Form</v-btn>
            <v-spacer></v-spacer>
            <v-btn color="blue darken-1" text @click="close">
              Close
            </v-btn>
            <v-btn color="blue darken-1" text @click.prevent="addActor">
              Add
            </v-btn>
          </v-card-actions>
        </v-form>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script>
import Vue from "vue";
import md5 from "md5";
import { checksForm } from "./checkForm";

export default {
  name: "addActor",
  mixins: [checksForm],

  data: () => ({
    error: false,
    username: "",
    name: "",
    email: "",
    password: "",
    confirm_password: "",
    showPass: false
  }),

  props: {
    dialogAdd: { default: false },
    role: { type: String }
  },
  methods: {
    reset() {
      this.$v.$reset();
      (this.username = ""), (this.name = "");
      (this.email = ""), (this.password = ""), (this.confirm_password = "");
    },
    close() {
      this.error = false;
      this.$emit("update:dialogAdd", false);
    },
    addActor() {
      this.error = false;
      this.$v.$touch();
      if (!this.$v.$invalid) {
        this.close(); //chiudo la schermata
        this.$store.commit("setCharging", true); // avvio il caricamento
        this.$store
          .dispatch("CREATE_NEW", {
            username: this.username,
            name: this.name,
            email: this.email,
            role: this.role.toUpperCase(),
            password: md5(this.password)
          })
          .then(() => {
            Vue.notify({
              group: "foo",
              title: `${this.role} created`,
              text: `${this.role} has been successfully created `,
              type: "success"
            });
            this.reset();
            this.$store.commit("setCharging", false);
          })
          .catch(() => {
            this.error = true; //se non ok -> display error
            this.$emit("update:dialogAdd", true); //se non ok -> mostra dialog nascosto
            this.$store.commit("setCharging", false); ///e non ok -> stop caricamento
            Vue.notify({
              group: "foo",
              title: "Error",
              text: "Username is not valid",
              type: "error"
            });
          });
      }
    }
  }
};
</script>

<style></style>
