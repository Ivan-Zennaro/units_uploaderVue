<template>
  <v-row justify="center">
    <v-dialog v-model="dialogAddFile" persistent max-width="600px">
      <v-card>
        <v-card-title>
          <span class="headline">Add File</span>
        </v-card-title>

        <v-card-text>
          <v-container>
            <v-alert color="error" :value="error" type="error">
              Error
            </v-alert>
            <v-row>
              <v-form
                ref="form"
                style="width:100%"
                v-model="valid"
                lazy-validation
              >
                <v-col cols="12">
                  <v-file-input
                    accept=".pdf,.doc,.docx,.png,.jpg,.mp3"
                    label="File input"
                    v-model="file"
                    :rules="[
                      v => !!v || 'File is required',
                      v =>
                        !v ||
                        v.size < 5000000 ||
                        'File size should be less than 5 MB!'
                    ]"
                    required
                  ></v-file-input>
                </v-col>

                <v-col cols="12">
                  <v-text-field
                    prepend-icon="mdi-file-document-edit-outline"
                    label="Name"
                    v-model="name"
                    :rules="[v => !!v || 'Name is required']"
                    required
                  ></v-text-field>
                </v-col>

                <v-col cols="12">
                  <v-combobox
                    v-model="chips"
                    chips
                    clearable
                    label="Insert Hashtags"
                    multiple
                    required
                    :rules="[v => v.length > 0 || 'At laest a tag is required']"
                    prepend-icon="mdi-pound-box-outline"
                    solo
                  >
                    <template v-slot:selection="{ item, select, selected }">
                      <v-chip
                        :input-value="selected"
                        close
                        @click:close="remove(item)"
                      >
                        <strong>{{ item }}</strong>
                      </v-chip>
                    </template>
                  </v-combobox>
                </v-col>
              </v-form>
            </v-row>
          </v-container>
        </v-card-text>
        <v-card-actions>
          <v-btn color="blue darken-1" text @click="reset">
            Reset Form
          </v-btn>
          <v-spacer></v-spacer>
          <v-btn color="blue darken-1" text @click="close">Close</v-btn>
          <v-btn
            color="blue darken-1"
            text
            :disabled="!valid"
            @click.prevent="addFile"
          >
            Add
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script>
import Vue from "vue";
import { mapGetters } from "vuex";

export default {
  name: "addFile",

  data: () => ({
    error: false,
    name: "",
    chips: [],
    valid: true,
    file: ""
  }),

  props: {
    dialogAddFile: { default: false }
  },
  computed: {
    ...mapGetters(["singleActor"])
  },
  methods: {
    remove(item) {
      this.chips.splice(this.chips.indexOf(item), 1);
      this.chips = [...this.chips];
    },
    reset() {
      this.$refs.form.reset();
      this.error = false;
    },
    close() {
      this.error = false;
      this.$emit("update:dialogAddFile", false);
      this.$refs.form.reset();
    },
    othersCheck() {
      if (this.name.includes("_")) {
        this.error = true;
        Vue.notify({
          group: "foo",
          title: "Name Error",
          text: "File name is not valid ",
          type: "error"
        });
        return false;
      }
      if (this.chips.filter(txt => txt.toString().includes(",")).length > 0) {
        this.error = true;
        Vue.notify({
          group: "foo",
          title: "Hashtags Error",
          text: "Hashtags cannot contains any comma ",
          type: "error"
        });
        return false;
      }
      return true;
    },
    addFile() {
      this.error = false;
      if (this.$refs.form.validate()) {
        if (!this.othersCheck()) return;
        this.$emit("update:dialogAddFile", false); // nascondo il dialog Add file
        this.$store.commit("setCharging", true); // setto il caricamento

        //riempio il formData da inviare
        let formData = new FormData();
        formData.append("file", this.file);
        formData.append("filename", this.name);
        formData.append("tags", this.chips.toString());
        formData.append("consumer", this.singleActor.username);
        formData.append("emailcons", this.singleActor.email);
        formData.append("namecons", this.singleActor.name);

        this.$store
          .dispatch("ADD_FILE", formData)
          .then(() => {
            Vue.notify({
              group: "foo",
              title: "PDF Added",
              text: "A Pdf has been successfully added ",
              type: "success"
            });
            this.$store.commit("setCharging", false); //se ok -> stop caricamento
            this.$refs.form.reset();
          })
          .catch(() => {
            this.error = true; //se non ok -> display error
            this.$emit("update:dialogAddFile", true); //se non ok -> mostra dialog nascosto
            this.$store.commit("setCharging", false); //se non ok -> stop caricamento
            Vue.notify({
              group: "foo",
              title: "Name error",
              text: "File name already used ",
              type: "error"
            });
          });
      }
    }
  }
};
</script>

<style></style>
