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
      var reg = /^[A-Za-z0-9_\-\.+]+\@[A-Za-z0-9_\-\.]+\.([A-Za-z]{2,6})$/;
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
      },
      dh: null
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
        this.storePriKey();
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
    generateKeys: function(private_key) {
        // 1024 bits = 128 bytes
        const prime_number = "ffffffffffffffffc90fdaa22168c234c4c6628b80dc1cd129024e088a67cc74020bbea63b139b22514a08798e3404ddef9519b3cd3a431b302b0a6df25f14374fe1356d6d51c245e485b576625e7ec6f44c42e9a637ed6b0bff5cb6f406b7edee386bfb5a899fa5ae9f24117c4b1fe649286651ece65381ffffffffffffffff";
        // 256 bits = 32 bytes
        // const prime_number = "f2f0cd410c64e9d30d4a00deb81ad28450ef3c909ebf69975d87d54056043883";
        const dh = crypto.createDiffieHellman(prime_number, 'hex'); //1024 bits
        //const dh = crypto.getDiffieHellman('modp2'); //1024 bits
        if (private_key === undefined) dh.generateKeys();
        else {
            dh.setPrivateKey(private_key);
        }
        // let key_exchange_arguments = {
        //     dh: dh,
        //     prime_number: null, //which is q in our notation
        //     primitive_root: null, //which is t in our notation
        //     x_of_a: null, //the only secret parameter for each of its participant
        //     y_of_a: null //the public key which each of participant exchange with other part
        // };
        // key_exchange_arguments.prime_number = dh.getPrime().toString("hex");
        // key_exchange_arguments.primitive_root = dh.getGenerator().toString("hex");
        // key_exchange_arguments.x_of_a = dh.getPrivateKey().toString("hex");
        // key_exchange_arguments.y_of_a = dh.getPublicKey().toString("hex");
        // return key_exchange_arguments;
        return dh;
    },
    fromHex2Array: function(hexString) {
        return new Uint8Array(hexString.match(/.{1,2}/g).map(byte => parseInt(byte, 16)));
    },
    submitForm: function() {
        //test example
        // const alice_private_key = "4a7d656b2ee3bcc668e0880f91a078ef5889141ada0a4706371e7cb085ac292dd6cb79c9763df132f6235b413b0aabdcd21e7f969a154311472a89e929230b9fbf4ae9ff3a04f10af6cc0880a1411a6346116eaa4e966e138eb80a611c8e96051c45abda6ef1333af26bb1117a331ace7abd7061430a7a136801a627c5686d0c";
        // const alice_public_key = "32307adb6647d7fa1c406bc5bab4c9fa2d5b2769d640a768028cf39146109ea8fcbbdd5d713e22b23e7fabe9d3ad3c3e396f4b3ad5dedb9a4c64a7e880275661f42bb8a1594e8341575e9f8b347276569a5c12b561f3f70ed13f2435bdd89dc6bc98b51038a45e5de2b260c72a989dbb5df985b53e2bf4bb6f3493decc3a4fa4";
        // const alice = this.generateKeys(this.fromHex2Array(alice_private_key));
        //
        // const bob_private_key = "e26d39766b2001f0f7fbf3157cb6020d8a2e271445ca0ca0d14473e76d7ab88ec85eef6b4d1d69ae88198ab9550d246075b4799c5522a694b4e7e8211c7e41091b7af9a6aa5d1e2b5b4281f3528dbefbb81f1216dd2cf6f51028fdfc053ad7f81877bcc2bdee029d9a0c215c133ca5fb4b4c274d27f43db52ba31f1a11e077de";
        // const bob_public_key = "14a85284dfaabffb07cb09931ef47354c70f8d05096979695ccab0d58fd803ff6282f6a976f1b542bb4a00b9b29c57e263e8a3e804a7f05e782cdaf02bdfabea0082d84fd1185a09bda33a50a64c3d1c7aba30b11cc832fd8542fd65835b45c6f2b3f136a5dbaa947d9c9518d03ab7da775e4d4d24f575bfdce748fa591e939f";
        // const bob = this.generateKeys(this.fromHex2Array(bob_private_key));
        //
        // const alice_public_unit8arr = this.fromHex2Array(alice_public_key);
        // const bob_public_unit8arr = this.fromHex2Array(bob_public_key);
        // const aliceSecret = alice.computeSecret(bob_public_unit8arr).toString('hex');
        // const bobSecret = bob.computeSecret(alice_public_unit8arr).toString('hex');
        //
        // /* aliceSecret and bobSecret should be the same */
        // console.log(aliceSecret);
        // console.log(bobSecret);
        // console.log(aliceSecret === bobSecret);

      //Todo: determine if connection is successful
      this.$refs[formName].validate((valid) => {
        if (valid && (this.websocket.readyState === 1)) {
          this.dh = this.generateKeys();
          const public_key = Buffer.from(this.dh.getPublicKey(),'binary').toString('base64');
          //Todo: (RSA) generate pub-pri key pairs and store somewhere
          this.request = "{\n" +
            "type: \"REGISTER\",\n" +
            "username:\"" + this.ruleForm.name + "\",\n" +
            "password:\"" + crypto
                  .createHash("md5")
                  .update(this.ruleForm.pwd)
                  .digest("hex")  + "\",\n" +
            "email:\"" + this.ruleForm.email + "\",\n" +
            "picture:\"" + this.ruleForm.picture + "\",\n" +
            "publicKey:\"" + public_key + "\"\n" +
            "}";
          this.send();
        } else {
            console.log('Please follow rules!');
        }
      });
    },
    onSignIn() {
      this.$router.push("/index");
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
    },
    storePriKey() {
        let item_name = this.ruleForm.name+"_private_key";
        if(!localStorage.getItem(item_name)) {
            localStorage.setItem(item_name, this.dh.getPrivateKey("hex"));
            console.log("SetItem succeeds!");
        } else {
            // to delete:
            console.log(item_name+": " + localStorage.getItem(item_name));
        }
    }
  }
};
</script>

<style scoped src="../css/main.css">
</style>
