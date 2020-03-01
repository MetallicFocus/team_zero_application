<!--suppress ALL -->
<template>
  <div id="Index">
    <div id="login" v-show="signinState">
      <div :style="signinStyle">
        <el-form
          label-width="100px"
          style="margin-top: 80px; margin-left: 20px; margin-right: 30px; margin-bottom: 50px;"
        >
          <el-form-item label="User Name: ">
            <el-input name="name" v-model="ruleForm.name"></el-input>
          </el-form-item>
          <el-form-item label="Password: ">
            <el-input show-password name="pwd" v-model="ruleForm.pwd"></el-input>
          </el-form-item>
          <el-form-item style="margin-left: 10px;">
            <el-row>
              <el-col :span="4"><el-button style="width: 100px; height: 50px;" v-on:click="onSignIn()">Sign In</el-button></el-col>
              <el-col offset="4" :span="4">
                <router-link
                  to="/Signup"
                  tag="el-button"
                  style="width: 100px; height: 50px; margin-left: 50px;"
              >Signup</router-link>
              </el-col>
            </el-row>
          </el-form-item>
        </el-form>
      </div>
    </div>
    <div id="main-panel" v-show="!signinState">
      <el-container>
        <el-aside id="left-panel">
          <el-header id="self-info">
            <el-row>
              <el-col :span="8">
                <el-avatar class="avatar" :src="self.avatar" style="margin-top: 5px;"></el-avatar>
              </el-col>
              <el-col :span="12">
                <label style="font-size: 30px; text-align: left;">{{self.name}}</label>
              </el-col>
              <el-col :span="2">
                <el-dropdown trigger="click" @command="handleCommand">
                  <i class="more el-icon-more el-dropdown-link" style="color: #D5F3EF;"></i>
                    <el-dropdown-menu slot="dropdown">
                      <el-dropdown-item icon="el-icon-s-custom" command="profile">Profile</el-dropdown-item>
                      <el-dropdown-item icon="el-icon-user" command="searchUsers">Search Users</el-dropdown-item>
                      <el-dropdown-item icon="el-icon-chat-dot-round" command="searchGroups">Search Groups</el-dropdown-item>
                      <el-dropdown-item icon="el-icon-s-tools" command="setting">Setting</el-dropdown-item>
                      <el-dropdown-item icon="el-icon-close" command="logout">Log out</el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </el-col>
            </el-row>
          </el-header>
          <el-header id="search-bar">
            <el-input v-model="search" placeholder="Search for history" style="width: 195px;"></el-input>
            <el-button icon="el-icon-search" type="primary" @click="searchHistory()"></el-button>
          </el-header>
          <el-main id="chat-list-panel">
            <single-chat
              v-on:click-id="showchat(chat.id)"
              v-for="chat in chatlist"
              v-bind:key="chat.id"
              :id="chat.id"
              :avatar="chat.avatar"
              :name="chat.name"
              :time="chat.messages[chat.messages.length-1].time"
              :content="chat.messages[chat.messages.length-1].content"
              :lastSender="chat.messages[chat.messages.length-1].objectflag"
              :newMessageNum="chat.new_message_num"
              :hidden="chat.badge_hidden"
            ></single-chat>
          </el-main>
        </el-aside>
        <single-chat-panel
          v-for="chat in chatlist"
          v-on:send-message="text2($event, args)"
          v-bind:key="chat.id"
          :chat="chat"
          v-show="chat.show"
        ></single-chat-panel>
      </el-container>
    </div>
    <div v-show="searchUserForm.display">
      <div class="mask"></div>
      <div class="searchPanel">
        <el-row>
          <el-col :span="10" offset="6">
            <el-input
              v-model="searchUserForm.searchField"
              placeholder="Please input user name"
              style="margin-top: 50px;">
            </el-input>
          </el-col>
          <el-col :span="2"><el-button icon="el-icon-search" type="primary" @click="searchUsers()" style="margin-top: 50px;"></el-button></el-col>
        </el-row>
        <div class="searchResults">
          <label>Search results are as follows:</label>
          <single-user-info
            @chat="chatwith(userdata.username)"
            :key="userdata.username"
            class="single-user-info"
            v-for="userdata in searchUserForm.usersdata"
            :username="userdata.username"
            :email="userdata.email"
            :isloggedin="userdata.IsLoggedIn"
          ></single-user-info>
        </div>
        <el-row><el-col offset="10" :span="4"><el-button style="margin-top: 10px;" @click="closeSearchUserPanel">Cancel</el-button></el-col></el-row>
      </div>
    </div>
    <div v-show="searchGroupForm.display">
      <div class="mask"></div>
      <div class="searchPanel">
        <el-row>
          <el-col :span="10" offset="6">
            <el-input
              v-model="searchGroupForm.searchField"
              placeholder="Please input group name"
              style="margin-top: 50px;">
            </el-input>
          </el-col>
          <el-col :span="2"><el-button icon="el-icon-search" type="primary" @click="searchGroups()" style="margin-top: 50px;"></el-button></el-col>
        </el-row>
        <div class="searchResults">
          <label>Search results are as follows:</label>
          <single-group-info
            class="single-user-info"
            v-for="groupdata in searchGroupForm.groupsdata"
          ></single-group-info>
        </div>
        <el-row><el-col offset="10" :span="4"><el-button style="margin-top: 10px;" @click="closeSearchGroupPanel">Cancel</el-button></el-col></el-row> <!--Todo: click fails to work-->
      </div>
    </div>
  </div>
</template>
<script>
import singleUserInfo from "./single-user-info.vue";
import singleChat from "./single-chat.vue";
import singleChatPanel from "./single-chat-panel.vue";
import Vue from "vue";
import SingleGroupInfo from "./single-group-info";
//TODO: Encryption
const CryptoJS = require("crypto-js");
const crypto = require("crypto");

export default {
  name: "Index",
  data: function() {
    return {
      crypto: {
        key: null,
        iv: null
      },
      signinState: true,
      signinStyle:
        "position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); border-color: #29B19F; border-style: solid; width: 500px; height: 300px;",
      ruleForm: {
        name: "",
        pwd: ""
      },
      args: [],
      searchUserForm: {
        display: false,
        searchField: "",
        usersdata: []
      },
      searchGroupForm: {
        display: false,
        searchField: "",
        groupsdata: []
      },
      websocket: null,
      request: "",
      response: "",
      parsed_response: "",
      onmessage_flag: false,
      self: {
        avatar: "/img/avatar.jpg",
        name: "User1",
        private_key: "",
      },
      search: "",
      chat_num: 1,
      chatlist: [
        {
          id: "1",
          avatar: "/img/avatar.jpg",
          name: "Template",
          show: 1,
          new_message_num: 0,
          badge_hidden: true,
          has_got_history: false,
          public_key: "",
          messages: [
            {
              avatar: "/img/avatar.jpg",
              time: "2020-02-29 00:00",
              content: "11111111111ssssssssssssssssssssssssssssssssssssssssssssswfergthyujikujynhbgfdcsxdcerftgyjuikujhtrgefwdfwretryutecwvetyujkilouyjhngssssssssssssssssssss!",
              objectflag: 1
            },
            {
              avatar: "/img/avatar.jpg",
              time: "2020-02-29 00:01",
              content: "2222fffffffffffffffff!",
              objectflag: 0
            }
          ]
        }
      ]
    };
  },
  components: {
      SingleGroupInfo,
    singleUserInfo,
    singleChat,
    singleChatPanel
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
      try {
        console.log("Watched newR: " + newR);
        this.parsed_response = JSON.parse(newR);
        switch (this.parsed_response.type) {
          case "TEXT":
            if (this.parsed_response.recipient !== this.self.name) return; //error forwarded message
            var sender = this.parsed_response.sender;
            var message = this.decrypt(this.parsed_response.message);
            this.getNewText(sender, message);
            break;
          case "REPLY":
            switch (this.parsed_response.REPLY) {
              case "LOGIN: SUCCESS":
                this.signinState = false;
                this.initPost();
                break;
              case "GETALLCONTACTS: SUCCESS":
                var contacts = this.parsed_response.contacts;
                for (let i in contacts) {
                  if (contacts[i].username !== this.self.name) {
                    this.chatwith(contacts[i].username);
                  }
                }
                break;
              case "SEARCHCONTACTS: SUCCESS":
                if (this.parsed_response.contacts.length > 1)
                  this.searchUserForm.usersdata = this.parsed_response.contacts;
                else
                  this.searchUserForm.usersdata = [
                    this.parsed_response.contacts
                  ];
                break;
              case "GETCHATHISTORY: SUCCESS":
                var messages = this.parsed_response.messages;
                if (messages === undefined) break;
                else if (messages.length === undefined) {
                  messages = [messages];
                }
                for (let i in messages) {
                  var sender = messages[i].sender;
                  var recipient = messages[i].recipient;
                  var message = messages[i].message;
                  var time = messages[i].timestamp;
                  this.updateChat(
                    sender,
                    recipient,
                    this.decrypt(message),
                    new Date(time),
                    false
                  );
                }
                break;
            }
            break;
        }
      } catch (e) {
        console.log(e.stack);
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
      this.onmessage_flag = true;
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
    showchat: function(id) {
      for (var chat_key of Object.keys(this.chatlist)) {
        if (this.chatlist[chat_key].id !== id) {
          this.chatlist[chat_key].show = 0;
        } else {
          this.getChatHistory(this.chatlist[chat_key].name, chat_key);
          this.chatlist[chat_key].has_got_history = true;
          this.chatlist[chat_key].show = 1;
          this.chatlist[chat_key].badge_hidden = true;
        }
      }
    },
    text2: function(args) {
      let recipient = args[0];
      let message = args[1];
      let time = new Date();

      // Todo: bug to fix: a blank message popup created in text field
      if (message === '') {
          alert('Please type sth. before sending!');
          return;
      }

      this.request =
        '{"type": "TEXT"' +
        ', "sender":"' +
        this.self.name +
        '", "recipient":"' +
        recipient +
        '", "message":"' +
        this.encrypt(message) +
        '"}';
      console.log(this.request);
      this.send();

      //Todo: determine text successful
      // if (this.parsed_response.REPLY === 'TEXT: SUCCESS'){
      //     this.updateChat(recipient, message);
      // } else {
      //     console.log(this.parsed_response.message);
      // }
      this.updateChat(this.self.name, recipient, message, time, false);
    },
    getNewText: function(sender, message) {
        var chat_key = this.isContactExist(sender);
        if (chat_key === false) {
            chat_key = this.createNewChat(sender);
            this.getChatHistory(sender, chat_key);
        }

        var time = new Date();
        this.updateChat(
            sender,
            this.self.name,
            message,
            time,
            true
        );
        this.popUpChat(chat_key);
        // Todo: onclick: turn to concerned chat panel
        const h = this.$createElement;

        this.$notify({
            title: 'New message!',
            message: h('i', { style: 'color: teal'}, sender + ': ' + message)
        });
    },
    isContactExist: function(username) {
      var exist = false;
        for (var chat_key in this.chatlist) {
          if (this.chatlist[chat_key].name === username) {
            //check if chat is already existed
                //Todo: to correct: chat panel dispears for a while
            exist = chat_key;
          }
      }
      return exist;
    },
    updateChat: function(sender, recipient, message, time, is_new_message) {
      let hour = time.getHours();
      let paddingHour = hour > 9 ? "" : "0";
      let minute = time.getMinutes();
      let paddingMinute = minute > 9 ? "" : "0";
      let year = time.getYear() + 1900;
      let month = time.getMonth() + 1;
      let day = time.getDate();
      time =
        year +
        "-" +
        month +
        "-" +
        day +
        " " +
        paddingHour +
        hour +
        ":" +
        paddingMinute +
        minute;
        var message_info = {
            avatar: "/img/avatar.jpg",
            time: time,
            content: message,
            objectflag: 0
        };
        var message_key = '';

      if (sender === this.self.name) {
        //message out
        for (var chat_key in this.chatlist) {
          if (this.chatlist[chat_key].name === recipient) {
            if (!this.isChatRedundant(chat_key, message_info)) {
                message_key = this.chatlist[chat_key].messages.length;
                Vue.set(this.chatlist[chat_key].messages, message_key,message_info);
            }
            this.chatlist[chat_key].messages.sort(this.sortCompareFunction);
          }
        }
      } else {
        for (var chat_key in this.chatlist) {
          //message in
          if (this.chatlist[chat_key].name === sender) {
              if (is_new_message) {
                  if(this.chatlist[chat_key].badge_hidden) this.chatlist[chat_key].new_message_num = 0;
                  this.chatlist[chat_key].new_message_num++;
                  this.chatlist[chat_key].badge_hidden = false;
              }
              if (!this.isChatRedundant(chat_key, message_info)) {
                  message_key = this.chatlist[chat_key].messages.length;
                  message_info.objectflag = 1;
                  Vue.set(this.chatlist[chat_key].messages, message_key, message_info);
              }
              this.chatlist[chat_key].messages.sort(this.sortCompareFunction);
          }
        }
      }

      //Todo: animation: slide down to the new message
    },
    searchHistory: function() {
        var search = this.search;
    }, //Todo: implement history search
    showSearchUserPanel: function() {
      this.searchUserForm.display = true;
    },
    showSearchGroupPanel: function() {
      this.searchGroupForm.display = true;
    },
    closeSearchUserPanel: function() {
      this.searchUserForm.display = false;
    },
    closeSearchGroupPanel: function() {
      this.searchGroupForm.display = false;
    },
    closeSearchPanel: function() {
      this.searchUserForm.display = false;
    },
    createNewChat: function(username) {
        var chat_key = this.chat_num;
        Vue.set(this.chatlist, this.chat_num, {
            id: ++this.chat_num,
            avatar: "",
            name: username,
            show: 0,
            new_message_num: 0,
            badge_hidden: true,
            has_got_history: false,
            messages: [
                {
                    avatar: "",
                    time: "",
                    content: "",
                    objectflag: 0
                }]
        });
        return chat_key;
    },
    chatwith: function(username) {
      if (username === this.self.name) return; //Todo: chat with oneself
      for (var chat_key in this.chatlist) {
        if (this.chatlist[chat_key].name === username) {
          //check if chat is already existed
          //Todo: to correct: chat panel dispears for a while
          this.closeSearchPanel();
          this.showchat(++chat_key);
          return;
        }
      }
      this.createNewChat(username);
      this.closeSearchPanel();
      this.showchat(this.chat_num);
    },
    searchUsers: function() {
      this.request =
        "{\n" +
        'type: "SEARCHCONTACTS",\n' +
        'search:"' +
        this.searchUserForm.searchField +
        '",\n' +
        "}";
      this.send();

      //Todo: fail to search
    },
    searchGroups: function() {
        // Todo: search groups
    },
    initPost: function() {
      // this.request = "{\n" + 'type: "GETALLCONTACTS",\n' + "}";
      // this.send();
      this.initCrypto();
    },
    isDeviceAuthorised() {
        const item_name = this.self.name+"_private_key";
        const private_key = localStorage.getItem(item_name);
        if(!private_key) {
            alert("Please log in with an authorised device!");
            return false;
        } else {
            this.self.private_key = private_key;
            return true;
        }
    },
    onSignIn: function() {
      this.self.name = this.ruleForm.name;
      if (this.isDeviceAuthorised()) {
        //Todo: determine if connection is successful
        this.request =
          "{\n" +
          'type: "LOGIN",\n' +
          'username:"' +
          this.ruleForm.name +
          '",\n' +
          'password:"' +
          crypto
            .createHash("md5")
            .update(this.ruleForm.pwd)
            .digest("hex") +
          '"\n' +
          "}";
        this.send();
      } else {
        alert("Error: " + this.response);
      }
    },
    initCrypto: function() {
      let key_hex = "2646294A404E635266556A576E5A7234";
      this.crypto.key = CryptoJS.enc.Utf8.parse(this.hex2a(key_hex));
      let iv_str = "0123456789abcdef";
      this.crypto.iv = CryptoJS.enc.Utf8.parse(iv_str);
    },
    encrypt: function(message) {
      return CryptoJS.AES.encrypt(message, this.crypto.key, {
        iv: this.crypto.iv,
        mode: CryptoJS.mode.CBC,
        padding: CryptoJS.pad.Pkcs7
      }).toString();
    },
    decrypt: function(ciphertext) {
      return CryptoJS.AES.decrypt(ciphertext, this.crypto.key, {
        iv: this.crypto.iv,
        mode: CryptoJS.mode.CBC,
        padding: CryptoJS.pad.Pkcs7
      }).toString(CryptoJS.enc.Utf8);
    },
    hex2a: function(hex) {
      var str = "";
      for (var i = 0; i < hex.length && hex.substr(i, 2) !== "00"; i += 2)
        str += String.fromCharCode(parseInt(hex.substr(i, 2), 16));
      return str;
    },
    hasGotHistory(chat_key) {
      return this.chatlist[chat_key].has_got_history;
    },
    getChatHistory: function(contact_name, chat_key) {
      if (this.hasGotHistory(chat_key)) return;
      this.request =
        "{\n" +
        'type: "GETCHATHISTORY",\n' +
        'myUsername: "' +
        this.self.name +
        '",\n' +
        'theirUsername: "' +
        contact_name +
        '",\n' +
        'historyDays: "' +
        1 +
        '"\n' +
        "}";
      this.send();
    },
    isChatRedundant: function (chat_key, message) {
      var redundant = false;
      var chat = this.chatlist[chat_key];
      for (var message_key in chat.messages) {
        if (chat.messages[message_key].content === message.content && chat.messages[message_key].time === message.time) redundant = true;
      }
      return redundant;
    },
    popUpChat: function (chat_key) {
        var chat = this.chatlist[chat_key];
        this.chatlist.splice(chat_key, chat_key+1);
        this.chatlist.unshift(chat);
    },
    sortCompareFunction: function (message_a, message_b) {
        let time_a = message_a.time;
        let time_b = message_b.time;
        return time_a.localeCompare(time_b);
    },
    handleCommand: function(command) {
        switch (command) {
            // Todo: personal profile (not in the project scope)
            case "profile":
                break;
            case "searchUsers":
                this.showSearchUserPanel();
                break;
            case "searchGroups":
                this.showSearchGroupPanel();
                break;
            // Todo: setting (not in the project scope)
            case "setting":
                break;
            case "logout":
                this.websocket.close();
                location.reload();
        }
    }
  }
};
</script>

<style scoped src="../css/main.css">
</style>
