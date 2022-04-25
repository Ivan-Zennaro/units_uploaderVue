// utilizzo la libreria di validazione vuelidate per
// creare un modello univoco per la validazioni in:
// signup, addActor, modifyActor

import { validationMixin } from "vuelidate";
import { required, email } from "vuelidate/lib/validators";

export const checkUserUp = function(value, vm) {
  if (vm.role.toLowerCase() === "uploader") {
    return value.length === 4;
  } else return true;
};

export const checkUserAdmin = function(value, vm) {
  if (vm.role.toLowerCase() === "admin") {
    return /^(([^<>()[\].,;:\s@"]+(\.[^<>()[\].,;:\s@"]+)*)|(".+"))@(([^<>()[\].,;:\s@"]+\.)+[^<>()[\].,;:\s@"]{2,})$/i.test(
      value
    );
  } else return true;
};

export const checkUserCons = function(value, vm) {
  if (vm.role.toLowerCase() === "consumer") {
    return /^[A-Z]{6}\d{2}[A-Z]\d{2}[A-Z]\d{3}[A-Z]$/i.test(value);
  } else return true;
};

export const checkPassRequired = function(value, vm) {
  if (!(typeof vm.password === "undefined")) {
    return value != "";
  } else return true;
};

export const checkPassLeng = function(value, vm) {
  if (!(typeof vm.password === "undefined")) {
    return value.length >= 6;
  } else return true;
};

export const checkConfirmPass = function(value, vm) {
  if (!(typeof vm.confirm_password === "undefined")) {
    return vm.password === value;
  } else return true;
};

export const checksForm = {
  mixins: [validationMixin],

  validations: {
    username: {
      required,
      checkUserUp,
      checkUserAdmin,
      checkUserCons
    },
    name: { required },
    email: { required, email },
    password: { checkPassRequired, checkPassLeng },
    confirm_password: { checkConfirmPass }
  },
  computed: {
    usernameErrors() {
      const errors = [];
      if (!this.$v.username.$dirty) return errors;
      !this.$v.username.required && errors.push("Username is required.");
      !this.$v.username.checkUserAdmin &&
        errors.push("Username must be a valid email");
      !this.$v.username.checkUserUp &&
        errors.push("The length of the username must be 4");
      !this.$v.username.checkUserCons &&
        errors.push("Username must be your fiscal code");
      return errors;
    },
    nameErrors() {
      const errors = [];
      if (!this.$v.name.$dirty) return errors;
      !this.$v.name.required && errors.push("Name is required.");
      return errors;
    },
    emailErrors() {
      const errors = [];
      if (!this.$v.email.$dirty) return errors;
      !this.$v.email.email && errors.push("Must be valid e-mail");
      !this.$v.email.required && errors.push("E-mail is required");
      return errors;
    },
    passErrors() {
      const errors = [];
      if (!this.$v.password.$dirty) return errors;
      !this.$v.password.checkPassLeng &&
        errors.push("Password must be at least 6 characters long");
      !this.$v.password.checkPassRequired &&
        errors.push("Password is required");
      return errors;
    },
    confirmPassErrors() {
      const errors = [];
      if (!this.$v.confirm_password.$dirty) return errors;
      !this.$v.confirm_password.checkConfirmPass &&
        errors.push("Passwords doesnt match");
      return errors;
    }
  }
};
