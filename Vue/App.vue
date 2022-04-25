<template>
  <v-app>
    <notifications group="foo" position="center" />
    <v-overlay :value="charging">
      <v-progress-circular
        v-if="charging"
        indeterminate
        color="primary"
      ></v-progress-circular>
    </v-overlay>
    <router-view></router-view>
  </v-app>
</template>

<script>
export default {
  name: "App",
  components: {},
  computed: {
    charging() {
      return this.$store.getters.getCharging;
    }
  },
  data: () => ({}),
  methods: {}
};
</script>
