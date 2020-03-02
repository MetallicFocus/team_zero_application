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
const CryptoJS = require("crypto-js");
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


        // 256 bits = 32 bytes
        // const prime_number = "f2f0cd410c64e9d30d4a00deb81ad28450ef3c909ebf69975d87d54056043883";

        const dh = crypto.createDiffieHellman(prime_number, 'hex', generator, 'hex'); //1024 bits
        dh.generateKeys();
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
    },
    dhExample() {
        // test example
        let alice_private_key = "776a030aa40ad618deeebd7c0711b2d73f2823b88740dabbfa5d7fb414cc9b93bde3ed117ef0e3a8bd3ae10c111be64dd8ccfcc6f524c3f23ff32d838cedc20255ef0bbdbf8b8de01c7030560301413518b1e593a913b4c758ea67e14cd60e85f54d64700d01e4b43c1fb1cef8bca86ec0c42de18cadf1ffe7f0e463b876f4ac";
        let alice_public_key = "78aaa8ca01fe27ccadeee693fda07350ddbf09c9f893b8141610cc0971d7597b2a83163c7c0e8f7aa059de8f54b8c4afcd8153eab3d5f93fb908faa03c412b15e98394454ed1ca75211d014f30067f2f3e8fe79d80d63a31355302a97d949e2e9c89d123ff116da65cf405cf2d487be92981e91f5b9ed45f980cef1fe8e04e5d";
        let alice = this.generateKeys();
        alice.setPublicKey(this.fromHex2Array(alice_public_key));
        alice.setPrivateKey(this.fromHex2Array(alice_private_key));

        console.log(Buffer.from(alice_private_key, 'hex').toString('base64'));

        let bob_private_key =  "3d3ed89888faad1b9cff073a5119fc6e81f1586246237203f85e7cf2671340cbe84fba53e05e0e8cbc8db6b1c2a58e3d97b58e1c43ab0523b4d5d5433bb3e317dfbc6d447c05e4153cca6227f6c68f167462894d0ac8d336ffe39561ebc0d6ae19f04c2c18cdd45b3b79e9fe2de5c7d2bbec44b0150c92ec2efb7de065f62f1a";
        let bob_public_key = "4a57ecb42f37f223d640c183c92398947ea3636cd5a8918448b7f71893fcaf9bbee439c0d6c37e293a6f89333315cfc74c6c0c688fb2d2777a6e739aa37c71c561213c647d01f29cfe1e2d09ef0675fab4628c15a94c1c50940be35c59a6caaeae2bd052b13f6bbb0233fbafc20bd9869ce51a5e9cd3cde3283b162ce05867d0";
        let bob = this.generateKeys();
        bob.setPublicKey(this.fromHex2Array(bob_public_key));
        bob.setPrivateKey(this.fromHex2Array(bob_private_key));

        // bob_private_key = Buffer.from(bob_private_key, 'hex').toString('base4');
        // bob_public_key = Buffer.from(public_key, 'hex').toString('base64');

        console.log(Buffer.from(bob_public_key, 'hex').toString('base64'));

        const alice_public_unit8arr = this.fromHex2Array(alice_public_key);
        const bob_public_unit8arr = this.fromHex2Array(bob_public_key);
        const aliceSecret = alice.computeSecret(bob_public_unit8arr).toString('hex');
        const bobSecret = bob.computeSecret(alice_public_unit8arr).toString('hex');

        const sharedSecret = "aa42bd363092256fe032e754887c647c92e922a359ecda132b7b8c1a2bc8422aa3133f88234bc538c84d40f3a9181e93a7cf1c127824d5d109243c5db4f7ffa686bd0196bc1d06ebd2dfad6272589fa3736da660446c672835bb02d6911015f97c1f0d9da74596152937bb99bb068d677de4f41a8a2fb343840a59d09a241502";
        console.log(aliceSecret);
        console.log(bobSecret);
        console.log(aliceSecret === bobSecret);

        const plaintext = "hello world!";
        const ciphertext_by_alice =  CryptoJS.AES.encrypt(plaintext, aliceSecret, {
            mode: CryptoJS.mode.CBC,
            padding: CryptoJS.pad.Pkcs7
        }).toString();
        const decipheredtext_by_bob = CryptoJS.AES.decrypt("U2FsdGVkX19KM+mtatFEeduIbZEvJogXSaCJBaB5GFI=", bobSecret, {
            mode: CryptoJS.mode.CBC,
            padding: CryptoJS.pad.Pkcs7
        }).toString(CryptoJS.enc.Utf8);

        console.log("plaintext: "+plaintext);
        console.log("cipher text: "+ciphertext_by_alice);
        console.log("deciphered text: "+decipheredtext_by_bob);

    }
  }
};
</script>

<style scoped src="../css/main.css">
</style>
