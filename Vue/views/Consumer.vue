<template>
  <v-container fluid>
    <v-app-bar app color="indigo" dark>
      <v-toolbar-title>Consumer Page</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-tooltip bottom>
        <template v-slot:activator="{ on }">
          <v-icon class="mr-6" @click="dialogPass = true" v-on="on">
            mdi-lock-reset
          </v-icon>
        </template>
        <span>Change Password</span>
      </v-tooltip>

      <v-tooltip bottom>
        <template v-slot:activator="{ on }">
          <v-icon class="mr-6" @click="logout" v-on="on">mdi-logout</v-icon>
        </template>
        <span>Logout</span>
      </v-tooltip>
    </v-app-bar>

    <v-main>
      <ChangePass :dialogPass.sync="dialogPass" />
      <v-row justify="center">
        <v-col cols="12" lg="4" md="5" sm="12">
          <ListUplCons />
        </v-col>
        <v-col cols="12" lg="4" md="5" sm="12">
          <InfoActor />
        </v-col>
      </v-row>
    </v-main>
  </v-container>
</template>

<script>
import ListUplCons from "../components/ListUplCons";
import InfoActor from "../components/InfoActor";
import ChangePass from "../components/ChangePass";

export default {
  name: "uploader",
  components: {
    ListUplCons,
    InfoActor,
    ChangePass
  },
  data() {
    return {
      dialogPass: false
    };
  },
  methods: {
    logout() {
      localStorage.clear();
      this.$router.replace("/");
    }
  }
};
</script>

<style></style>
