<template>
  <v-container class="fill-height" fluid>
    <v-row align="center" justify="center">
      <v-card class="elevation-18">
        <v-card-text>Session Expired. Please login again</v-card-text>
        <v-card-actions class="justify-center">
          <v-btn to="/">Login</v-btn>
        </v-card-actions>
      </v-card>
    </v-row>
  </v-container>
</template>

<script>
export default {
  name: "logout",
  components: {},
  data() {
    return {};
  },
  methods: {},
  created() {
    this.$store.commit("removeAllCons");
    this.$store.commit("removeAllAdmin");
    localStorage.clear();
  }
};
</script>

<style></style>
