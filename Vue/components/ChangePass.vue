<template>
  <v-row justify="center">
    <v-dialog v-model="dialogPass" persistent max-width="600px">
      <v-card>
        <v-card-title>
          <span class="headline">Change Password</span>
        </v-card-title>
        <v-card-text>
          <v-container>
            <v-form class="mt-5" ref="form" lazy-validation>
              <v-row>
                <v-col cols="12">
                  <v-alert color="error" :value="error" type="error">
                    Wrong Password
                  </v-alert>
                  <v-text-field
                    label="Previous Password"
                    v-model="prev_pass"
                    prepend-icon="mdi-lock-clock"
                    required
                    :append-icon="showPass ? 'mdi-eye' : 'mdi-eye-off'"
                    :type="showPass ? 'text' : 'password'"
                    @click:append="showPass = !showPass"
                    :rules="[v => !!v || 'Previous password is required']"
                  ></v-text-field>
                </v-col>
                <v-col cols="12">
                  <v-text-field
                    label="New Password"
                    v-model="new_pass"
                    prepend-icon="mdi-lock"
                    :append-icon="showPass2 ? 'mdi-eye' : 'mdi-eye-off'"
                    :type="showPass2 ? 'text' : 'password'"
                    @click:append="showPass2 = !showPass2"
                    required
                    :rules="[
                      v => !!v || 'New password is required',
                      v =>
                        v.length >= 6 ||
                        'Password must be at least 6 characters long'
                    ]"
                  ></v-text-field>
                </v-col>
                <v-col cols="12">
                  <v-text-field
                    label="Repeat New Password"
                    v-model="rep_pass"
                    prepend-icon="mdi-lock-check"
                    type="password"
                    :rules="[v => v === new_pass || 'Passwords doesnt match']"
                  ></v-text-field>
                </v-col>
              </v-row>
            </v-form>
          </v-container>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue darken-1" text @click.native="close">
            Close
          </v-btn>
          <v-btn color="blue darken-1" text @click.prevent="change">
            Change
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script>
import Vue from "vue";
import md5 from "md5";

export default {
  name: "changePass",

  data: () => ({
    prev_pass: "",
    new_pass: "",
    rep_pass: "",
    error: false,
    showPass2: false,
    showPass: false
  }),

  props: {
    dialogPass: { default: false }
  },

  methods: {
    close() {
      this.error = false;
      this.$refs.form.reset();
      this.$emit("update:dialogPass", false);
    },

    change() {
      this.error = false;
      if (this.$refs.form.validate()) {
        this.$emit("update:dialogPass", false); // nascondo il dialog Pass
        this.$store.commit("setCharging", true); // setto il caricamento
        this.$store
          .dispatch("UPD_PASS", {
            prev_pass: md5(this.prev_pass),
            new_pass: md5(this.new_pass)
          })
          .then(() => {
            Vue.notify({
              group: "foo",
              title: "Password modify",
              text: "Password has been successfully modify ",
              type: "success"
            });
            this.$store.commit("setCharging", false); //se ok -> stop caricamento
          })
          .catch(() => {
            this.error = true;
            this.$emit("update:dialogPass", true); //se non ok -> mostra dialog nascosto
            this.$store.commit("setCharging", false); //se non ok -> stop caricamento
            Vue.notify({
              group: "foo",
              title: "Error",
              text: "Previous password is not correct ",
              type: "error"
            });
          });
        this.$refs.form.reset();
      }
    }
  }
};
</script>
