<template>
  <v-container class="fill-height" fluid>
    <v-row align="center" justify="center">
      <v-col cols="12" lg="4" md="6" sm="8">
        <v-card class="elevation-18">
          <v-toolbar dark color="blue">
            <v-toolbar-title>Signup Form</v-toolbar-title>
          </v-toolbar>
          <v-alert color="error" :value="error" type="error">
            Username already used
          </v-alert>
          <v-card-text>
            <v-form>
              <v-text-field
                name="user"
                label="Username"
                prepend-icon="mdi-account"
                type="text"
                v-model="username"
                required
                :error-messages="usernameErrors"
                @input="$v.username.$touch()"
                @blur="$v.username.$touch()"
              ></v-text-field>
              <v-text-field
                name="name"
                label="Name/Surname"
                v-model="name"
                prepend-icon="mdi-account-box"
                type="text"
                required
                :error-messages="nameErrors"
                @input="$v.name.$touch()"
                @blur="$v.name.$touch()"
              ></v-text-field>
              <v-text-field
                name="email"
                label="Email"
                prepend-icon="mdi-email"
                v-model="email"
                type="text"
                required
                :error-messages="emailErrors"
                @input="$v.email.$touch()"
                @blur="$v.email.$touch()"
              ></v-text-field>
              <v-text-field
                name="password"
                label="Password"
                prepend-icon="mdi-lock"
                v-model="password"
                :append-icon="showPass ? 'mdi-eye' : 'mdi-eye-off'"
                :type="showPass ? 'text' : 'password'"
                @click:append="showPass = !showPass"
                required
                :error-messages="passErrors"
                @input="$v.password.$touch()"
                @blur="$v.password.$touch()"
              ></v-text-field>
              <v-text-field
                name="confirm_password"
                label="Confirm Password"
                prepend-icon="mdi-lock"
                v-model="confirm_password"
                :error-messages="confirmPassErrors"
                type="password"
                @input="$v.confirm_password.$touch()"
                @blur="$v.confirm_password.$touch()"
              ></v-text-field>
              <v-card-actions>
                <v-btn large rounded class="ml-4" color="primary" dark to="/">
                  Login
                </v-btn>
                <v-spacer></v-spacer>
                <v-btn
                  large
                  rounded
                  color="success"
                  class="mr-4"
                  dark
                  @click="registration"
                  >Register
                </v-btn>
              </v-card-actions>
            </v-form>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import Vue from "vue";
import md5 from "md5";
import { checksForm } from "../checkForm";

export default {
  name: "signup",
  mixins: [checksForm],

  data: () => ({
    error: false,
    msgerror: "Error",
    username: "",
    name: "",
    email: "",
    role: "Consumer",
    password: "",
    confirm_password: "",
    showPass: false
  }),

  methods: {
    registration() {
      this.error = false;
      this.$v.$touch();
      if (!this.$v.$invalid) {
        this.$store.commit("setCharging", true);
        this.$store
          .dispatch("REGISTER", {
            username: this.username,
            name: this.name,
            email: this.email,
            role: this.role,
            password: md5(this.password)
          })
          .then(() => {
            this.$store.commit("setCharging", false);
            Vue.notify({
              group: "foo",
              title: "Registration completed",
              text: "Your registration has been successfully completed ",
              type: "success"
            });
            this.$router.replace("/consumer_home");
          })
          .catch(() => {
            this.error = true;
            this.$store.commit("setCharging", false);
            Vue.notify({
              group: "foo",
              title: `Error`,
              text: `Wrong credential`,
              type: "error"
            });
          });
      }
    }
  },
  created() {
    this.$store.commit("removeAllCons");
    this.$store.commit("removeAllAdmin");
  }
};
</script>
