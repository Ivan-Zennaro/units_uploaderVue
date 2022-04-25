<template>
  <v-card>
    <v-toolbar color="indigo" dark>
      <v-toolbar-title>Stats of {{ getCurrentStat }}</v-toolbar-title>
    </v-toolbar>
    <v-container class="spacing-playground pa-6">
      <v-card-text>Number of files loaded: {{ getstat1 }}</v-card-text>
      <v-card-text>Number of different consumers: {{ getstat2 }}</v-card-text>

      <v-alert color="error" :value="error" type="error">{{
        error_text
      }}</v-alert>

      <v-card-actions>
        <v-menu
          v-model="menu"
          :close-on-content-click="false"
          :nudge-right="40"
          transition="scale-transition"
          offset-y
          min-width="290px"
          style="width:100%"
        >
          <template v-slot:activator="{ on }" style="width:100%">
            <v-text-field
              v-model="date1"
              label="Start Date"
              readonly
              v-on="on"
              style="width:100%"
            ></v-text-field>
          </template>
          <v-date-picker v-model="date1" @input="menu = false"></v-date-picker>
        </v-menu>
      </v-card-actions>

      <v-card-actions>
        <v-menu
          v-model="menu2"
          :close-on-content-click="false"
          :nudge-right="40"
          transition="scale-transition"
          offset-y
          min-width="290px"
        >
          <template v-slot:activator="{ on }">
            <v-text-field
              v-model="date2"
              label="End Date"
              readonly
              v-on="on"
            ></v-text-field>
          </template>
          <v-date-picker v-model="date2" @input="menu2 = false"></v-date-picker>
        </v-menu>
      </v-card-actions>

      <v-card-actions>
        <v-btn color="blue darken-1" text @click="lmStats">
          Get Last Month Stats
        </v-btn>
        <v-spacer></v-spacer>
        <v-btn color="blue darken-1" text @click="newStats">
          Get Stats
        </v-btn>
      </v-card-actions>
    </v-container>
  </v-card>
</template>

<script>
import { mapGetters } from "vuex";
import Vue from "vue";

export default {
  name: "statsAdmin",
  data() {
    return {
      date1: null,
      date2: null,
      menu: false,
      menu2: false,
      error: false,
      error_text: "Errore"
    };
  },

  computed: {
    ...mapGetters(["getstat1", "getstat2", "getCurrentStat"])
  },
  methods: {
    lmStats() {
      this.error = false;
      if (this.getCurrentStat === "") {
        this.error_text = "No uploader selected";
        Vue.notify({
          group: "foo",
          title: `Error`,
          text: `You have to select an uploader`,
          type: "error"
        });
        return (this.error = true);
      }
      this.date1 = null;
      this.date2 = null;
      this.$store
        .dispatch("GET_STATS_ACTOR", this.getCurrentStat)
        .then(() =>
          Vue.notify({
            group: "foo",
            title: `Stats update`,
            text: `Stats of  ${this.getCurrentStat} has been successfully updated`,
            type: "success"
          })
        )
        .catch(err => console.log(err));
    },
    newStats() {
      if (
        this.date1 === null ||
        this.date2 === null ||
        this.getCurrentStat === ""
      ) {
        this.error_text = "Select a valid period and an uploader";
        Vue.notify({
          group: "foo",
          title: `Error`,
          text: `You have to select an uploader and a valid period`,
          type: "error"
        });
        return (this.error = true);
      }
      this.error = false;
      this.$store
        .dispatch("GET_STATS_UPDATE", {
          username: this.getCurrentStat,
          from: this.date1,
          until: this.date2
        })
        .then(() =>
          Vue.notify({
            group: "foo",
            title: `Stats update`,
            text: `Stats of  ${this.getCurrentStat} has been successfully updated`,
            type: "success"
          })
        )
        .catch(err => console.log(err));
    }
  },
  created() {
    this.$store.dispatch("GET_MY_INFO").catch(() => {});
  }
};
</script>
