<template>
  <v-container class="fill-height" fluid>
    <v-row align="center" justify="center">
      <v-col cols="12" lg="4" md="6" sm="8">
        <v-card class="elevation-18">
          <v-toolbar dark color="blue" flat>
            <v-toolbar-title>Login Form</v-toolbar-title>
          </v-toolbar>
          <v-alert color="error" :value="error" type="error">
            Username or password are incorrent
          </v-alert>
          <v-card-text>
            <v-form>
              <v-text-field
                v-model="username"
                label="Username"
                prepend-icon="mdi-account"
                type="text"
              ></v-text-field>
              <v-text-field
                v-model="password"
                label="Password"
                prepend-icon="mdi-lock"
                :append-icon="showPass ? 'mdi-eye' : 'mdi-eye-off'"
                :type="showPass ? 'text' : 'password'"
                @click:append="showPass = !showPass"
              ></v-text-field>
              <v-card-actions>
                <v-btn large rounded color="primary" dark to="/signup">
                  Sign Up Consumers
                </v-btn>
                <v-spacer></v-spacer>
                <v-btn large rounded color="green" dark @click.prevent="login">
                  Login
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
import md5 from "md5";
import Vue from "vue";
export default {
  name: "login",

  methods: {
    login() {
      this.error = false;
      this.$store.commit("setCharging", true);
      this.$store
        .dispatch("LOGIN", {
          username: this.username,
          password: md5(this.password)
        })
        .then(data => {
          this.$store.commit("setCharging", false);
          switch (data) {
            case "CONSUMER":
              this.$router.replace("/consumer_home");
              break;
            case "UPLOADER":
              this.$router.replace("/uploader_home");
              break;
            case "ADMIN":
              this.$router.replace("/admin_home");
              break;
            default:
          }
          Vue.notify({
            group: "foo",
            title: "Logged",
            text: "Your login has been successfully completed ",
            type: "success"
          });
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
  },
  created() {
    this.$store.commit("removeAllCons");
    this.$store.commit("removeAllAdmin");
    if (
      localStorage.getItem("token") !== "" &&
      localStorage.getItem("token") !== null
    ) {
      this.$store.commit("setCharging", true);
      this.$store
        .dispatch("SND_TOKEN")
        .then(role => {
          this.$store.commit("setCharging", false);
          switch (role) {
            case "CONSUMER":
              this.$router.replace("/consumer_home");
              break;
            case "UPLOADER":
              this.$router.replace("/uploader_home");
              break;
            case "ADMIN":
              this.$router.replace("/admin_home");
              break;
            default:
          }
        })
        .catch(() => this.$store.commit("setCharging", false));
    }
  },
  data: () => ({
    username: "",
    password: "",
    error: false,
    showPass: false
  })
};
</script>
