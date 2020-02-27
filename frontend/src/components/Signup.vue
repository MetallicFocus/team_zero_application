<template>
  <div id="signup" :style="divStyle">
    <el-form
      :model="ruleForm"
      :rules="rules"
      ref="ruleForm"
      label-width="150px"
      class="demo-ruleForm"
    >
      <el-form-item label="Email Address" prop="email">
        <el-input v-model="ruleForm.email"></el-input>
      </el-form-item>
      <el-form-item label="User Name" prop="name">
        <el-input v-model="ruleForm.name"></el-input>
      </el-form-item>
      <el-form-item label="Password" prop="pwd">
        <el-input v-model="ruleForm.pwd" show-password></el-input>
      </el-form-item>
      <el-form-item style="margin-top: 50px;">
        <el-button type="primary" @click="submitForm()">Sign Up</el-button>
        <el-button @click="resetForm('ruleForm')">Reset</el-button>
        <router-link to="/Signin" tag="el-button">Signin</router-link>
      </el-form-item>
    </el-form>
    <div>{{request}}</div>
    <div>{{response}}</div>
    <div>{{parsed_response}}</div>
  </div>
</template>

<script>
//import HelloWorld from "./components/HelloWorld.vue";

//TODO: Encryption

const crypto = require("crypto");

export default {
  name: "Signup",
  data: function() {
    var validateEmail = (rule, value, callback) => {
      var reg = "/^[A-Za-z0-9_-.+]+@[A-Za-z0-9_-.]+.([A-Za-z]{2,6})$/";
      //E-mail contains only one '@' character
      //E-mail contains at least a dot ('.')
      //E-mail contains between 2 and 6 characters after a dot ('.')
      if (value === "") {
        callback();
      } else if (!reg.exec(value)) {
        callback(new Error("Please input a valid email address."));
      }
    };
    var validateUsername = (rule, value, callback) => {
      var reg1 = /[^a-zA-Z0-9_\-.]/; //Username can only contain number, character and ., -, _
      var reg2 = /^[0-9]/; //Username cannot start with numbers.
      var reg3 = /.{3,30}/; //The number of characters must be in between 3 and 30.
      if (value === "") {
        callback();
      } else if (reg2.exec(value)) {
        callback(new Error("Username cannot start with numbers."));
      } else if (reg1.exec(value)) {
        callback(
          new Error("Username can only contain numbers, characters and ., -, _")
        );
      } else if (!reg3.exec(value)) {
        callback(
          new Error("The number of characters must be in between 3 and 30.")
        );
      }
    };
    var validatePwd = (rule, value, callback) => {
      var reg1 = /(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#!?$%^&+])/;
      // Password contains at least 1 uppercase character
      // Password contains at least 1 lowercase character
      // Password contains at least 1 digit
      // Password contains at least 1 special character
      var reg2 = /\s/; // Password must not contain white spaces
      var reg3 = /.{6,30}/; // Password is between 6 and 30 characters
      if (value === "") {
        callback();
      } else if (!reg1.exec(value)) {
        callback(
          new Error(
            "Password must contain uppercase and lowercase character, number, special character (only character in [@#!?$%^&+])"
          )
        );
      } else if (reg2.exec(value)) {
        callback(new Error("Password cannot contain white space"));
      } else if (!reg3.exec(value)) {
        callback(
          new Error("The number of characters must be in between 6 and 30.")
        );
      }
    };
    return {
      websocket: null,
      request: "",
      response: "",
      parsed_response: "",
      divStyle:
        "position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); width: 500px; height: 300px;",
      ruleForm: {
        email: "",
        name: "",
        pwd: "",
        picture: ""
      },
      rules: {
        email: [
          { validator: validateEmail, trigger: "blur" },
          {
            required: true,
            message: "Please input email address.",
            trigger: "blur"
          }
        ],
        name: [
          { validator: validateUsername, trigger: "blur" },
          {
            required: true,
            message: "Please input user name.",
            trigger: "blur"
          }
        ],
        pwd: [
          { validator: validatePwd, trigger: "blur" },
          { required: true, message: "Please input password.", trigger: "blur" }
        ]
      }
    };
  },
  mounted() {
    if ("WebSocket" in window) {
      this.websocket = new WebSocket("ws://localhost:1234");
      this.initWebSocket();
    } else {
      alert("Websocket is not supported by this browser!");
      return null;
    }
  },
  beforeDestroy() {
    this.websocket.close();
  },
  watch: {
    response(newR, oldR) {
      this.parsed_response = JSON.parse(newR);
      if (this.parsed_response.REPLY === "REGISTER: SUCCESS") {
        alert("Register successfully!");
        this.onSignIn();
      } else {
        alert("Register failed!");
      }
    }
  },
  methods: {
    initWebSocket() {
      //connection occurs error
      this.websocket.onerror = this.setErrorMessage;
      //connection succeeds
      this.websocket.onopen = this.setOnopenMessage;
      //get response from Server
      this.websocket.onmessage = this.setOnmessageMessage;
      //connection closes
      this.websocket.onclose = this.setOncloseMessage;
      //connection closes automatically after leaving websites
      window.onbeforeunload = this.onBeforeUnload;
    },
    setErrorMessage() {
      this.response =
        "WebSocket occurs error." + "   Status：" + this.websocket.readyState;
    },
    setOnopenMessage() {
      this.response =
        "WebSocket succeeds." + "   status：" + this.websocket.readyState;
    },
    setOnmessageMessage(event) {
      this.response = event.data;
    },
    setOncloseMessage() {
      this.response =
        "WebSocket closes." + "   status：" + this.websocket.readyState;
    },
    onBeforeUnload() {
      this.websocket.close();
    },
    //send message (JSON) to Server
    send() {
      this.websocket.send(this.request);
    },

    submitForm: function() {
      if (true) {
        const dh = crypto.createDiffieHellman(256);
        dh.generateKeys();
        let key_exchange_arguments = {
          prime_number: null, //which is q in our notation
          primitive_root: null, //which is t in our notation
          x_of_a: null, //the only secret parameter for each of its participant
          y_of_a: null //the public key which each of participant exchange with other part
        };
        key_exchange_arguments.prime_number = dh.getPrime().toString("base64");
        key_exchange_arguments.primitive_root = dh
          .getGenerator()
          .toString("base64");
        key_exchange_arguments.x_of_a = dh.getPrivateKey().toString("base64");
        key_exchange_arguments.y_of_a = dh.getPublicKey().toString("base64");
        console.log(key_exchange_arguments);
        //Todo: determine if connection is successful
        this.request =
          "{\n" +
          'type: "REGISTER",\n' +
          'username:"' +
          this.ruleForm.name +
          '",\n' +
          'password:"' +
          crypto
            .createHash("md5")
            .update(this.ruleForm.pwd)
            .digest("hex") +
          '",\n' +
          'email:"' +
          this.ruleForm.email +
          '",\n' +
          'picture:"' +
          this.ruleForm.picture +
          '",\n' +
          'publicKey:"' +
          key_exchange_arguments.y_of_a +
          '"\n' +
          "}";
        this.send();
      } else {
        alert("Error: " + this.response);
      }
    },
    onSignIn() {
      this.$router.push("/index");
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
    }
  }
};
</script>

<style scoped src="../css/main.css">
</style>
