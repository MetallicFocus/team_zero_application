<template>
  <div id="signup" :style="divStyle">
      <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="150px" class="demo-ruleForm">
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
              <el-button type="primary" @click="submitForm('ruleForm')">Sign Up</el-button>
              <el-button @click="resetForm('ruleForm')">Reset</el-button>
              <el-button @click="onSignIn">Sign In</el-button>
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
            if (value === '') {
                callback();
            } else if (!reg.exec(value)) {
                callback(new Error('Please input a valid email address.'));
            }
            callback();
        };
        var validateUsername = (rule, value, callback) => {
            var reg1 = /[^a-zA-Z0-9_\-.]/; //Username can only contain number, character and ., -, _
            var reg2 = /^[0-9]/; //Username cannot start with numbers.
            var reg3 = /.{3,30}/; //The number of characters must be in between 3 and 30.
            if (value === '') {
                callback();
            } else if (reg2.exec(value)) {
                callback(new Error('Username cannot start with numbers.'));
            } else if (reg1.exec(value)) {
                callback(new Error('Username can only contain numbers, characters and ., -, _'));
            } else if (!reg3.exec(value)) {
                callback(new Error('The number of characters must be in between 3 and 30.'));
            }
            callback();
        };
        var validatePwd = (rule, value, callback) => {
            var reg1 = /(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#!?$%^&+])/;
            // Password contains at least 1 uppercase character
            // Password contains at least 1 lowercase character
            // Password contains at least 1 digit
            // Password contains at least 1 special character
            var reg2 = /\s/; // Password must not contain white spaces
            var reg3 = /.{6,30}/; // Password is between 6 and 30 characters
            if (value === '') {
                callback();
            } else if (!reg1.exec(value)) {
                callback(new Error('Password must contain uppercase and lowercase character, number, special character (only character in [@#!?$%^&+])'));
            } else if (reg2.exec(value)) {
                callback(new Error('Password cannot contain white space'));
            } else if (!reg3.exec(value)) {
                callback(new Error('The number of characters must be in between 6 and 30.'));
            }
            callback();
        };
        return {
            websocket: null,
            request: "",
            response: "",
            parsed_response: "",
            divStyle: "position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); width: 500px; height: 300px;",
            ruleForm: {
                email: '',
                name: '',
                pwd: '',
                picture: '',
                publicKey: '',
                privateKey: ''
            },
            rules: {
                email: [
                    { validator: validateEmail, trigger: 'blur' },
                    { required: true, message: 'Please input email address.', trigger: 'blur'}
                ],
                name: [
                    { validator: validateUsername, trigger: 'blur' },
                    { required: true, message: 'Please input user name.', trigger: 'blur'}
                ],
                pwd: [
                    { validator: validatePwd, trigger: 'blur' },
                    { required: true, message: 'Please input password.', trigger: 'blur'}
                ],
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
      console.log(newR);
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
    generateKeys: function() {
        // (1024 bits = 128 bytes) same as const dh = crypto.getDiffieHellman('modp2');
        const prime_number = "B10B8F96A080E01DDE92DE5EAE5D54EC52C99FBCFB06A3C6" +
            "9A6A9DCA52D23B616073E28675A23D189838EF1E2EE652C0" +
            "13ECB4AEA906112324975C3CD49B83BFACCBDD7D90C4BD70" +
            "98488E9C219A73724EFFD6FAE5644738FAA31A4FF55BCCC0" +
            "A151AF5F0DC8B4BD45BF37DF365C1A65E68CFDA76D4DA708" +
            "DF1FB2BC2E4A4371";

        const generator = "A4D1CBD5C3FD34126765A442EFB99905F8104DD258AC507F" +
            "D6406CFF14266D31266FEA1E5C41564B777E690F5504F213" +
            "160217B4B01B886A5E91547F9E2749F4D7FBD7D3B9A92EE1" +
            "909D0D2263F80A76A6A24C087A091F531DBF0A0169B6A28A" +
            "D662A4D18E73AFA32D779D5918D08BC8858F4DCEF97C2A24" +
            "855E6EEB22B3B2E5";

        var dh = crypto.createDiffieHellman(prime_number, 'hex', generator, 'hex'); //1024 bits
        dh.generateKeys();
        var privateKey = "776a030aa40ad618deeebd7c0711b2d73f2823b88740dabbfa5d7fb414cc9b93bde3ed117ef0e3a8bd3ae10c111be64dd8ccfcc6f524c3f23ff32d838cedc20255ef0bbdbf8b8de01c7030560301413518b1e593a913b4c758ea67e14cd60e85f54d64700d01e4b43c1fb1cef8bca86ec0c42de18cadf1ffe7f0e463b876f4ac";
        var publicKey = "78aaa8ca01fe27ccadeee693fda07350ddbf09c9f893b8141610cc0971d7597b2a83163c7c0e8f7aa059de8f54b8c4afcd8153eab3d5f93fb908faa03c412b15e98394454ed1ca75211d014f30067f2f3e8fe79d80d63a31355302a97d949e2e9c89d123ff116da65cf405cf2d487be92981e91f5b9ed45f980cef1fe8e04e5d";
        dh.setPrivateKey(privateKey, "hex");
        dh.setPublicKey(publicKey, "hex");
        return dh;
    },
    fromHex2Array: function(hexString) {
        return new Uint8Array(hexString.match(/.{1,2}/g).map(byte => parseInt(byte, 16)));
    },
    submitForm: function(formName) {
        // this.dhExample();
        this.$refs[formName].validate((valid) => {
            if (valid && (this.websocket.readyState === 1)) {
              this.dh = this.generateKeys();
              const public_key = Buffer.from(this.dh.getPublicKey(),'binary').toString('base64');
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
                alert('Please follow rules!');
            }
        });
    },
    onSignIn() {
      this.$router.push("/Signin");
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
