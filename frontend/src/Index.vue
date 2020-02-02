<template>
  <div id="app">
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
            <el-button style="width: 100px; height: 50px;" v-on:click="onSignIn()">Sign In</el-button>
            <el-button
              style="width: 100px; height: 50px; margin-left: 50px;"
              v-on:click="onSignUp()"
            >Sign Up</el-button>
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
                <i id="add" class="el-icon-circle-plus-outline" @click="showSearchPanel"></i>
              </el-col>
            </el-row>
          </el-header>
          <el-header id="search-bar">
            <el-input v-model="search" placeholder="Search for history" style="width: 195px;"></el-input>
            <el-button icon="el-icon-search" type="primary" @click="searchHistory(searchField)"></el-button>
          </el-header>
          <el-main id="chat-list-panel">
            <single-chat
              v-on:click-id="showchat(chat.id)"
              v-for="chat in chatlist"
              v-bind:key="chat.id"
              :id="chat.id"
              :avatar="chat.avatar"
              :name="chat.name"
              :time="chat.time"
              :content="chat.content"
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
      <div id="mask"></div>
      <div id="searchUserPanel">
        <el-input
          v-model="searchUserForm.searchField"
          placeholder="Please input user name"
          style="margin-left: 160px; margin-top: 50px; width: 400px;"
        ></el-input>
        <el-button icon="el-icon-search" type="primary" @click="searchUsers()"></el-button>
        <div id="searchResults">
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
        <el-button style="margin-left: 350px; margin-top: 10px;" @click="closeSearchPanel">Cancel</el-button>
      </div>
    </div>
  </div>
</template>
<script>
//import HelloWorld from "./components/HelloWorld.vue";
//TODO: Encryption
const crypto = require("crypto");

export default {
  name: "app",
  data: function() {
    return {
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
      websocket: null,
      request: "",
      response: "",
      parsed_response: "",
      onmessage_flag: false,
      self: {
        avatar: "/img/avatar.jpg",
        name: "User1"
      },
      search: "",
      chat_num: 1,
      chatlist: [
        {
          id: "1",
          avatar: "/img/avatar.jpg",
          name: "Template",
          time: "00:00",
          content: "Me: test!",
          show: 1,
          messages: [
            {
              avatar: "/img/avatar.jpg",
              time: "00:00",
              content: "11111111111!",
              objectflag: 1
            },
            {
              avatar: "/img/avatar.jpg",
              time: "00:00",
              content: "22222222222!",
              objectflag: 0
            }
          ]
        }
      ]
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
      // console.log("Watched newR: " + newR);
      this.parsed_response = JSON.parse(newR);
      // console.log(this.parsed_response);
      switch (this.parsed_response.type) {
        case "TEXT":
          if (this.parsed_response.recipient !== this.self.name) return; //error forwarded message
          var sender = this.parsed_response.sender;
          var message = this.parsed_response.message;
          this.chatwith(sender);
          //Todo: retrieve time instead of creating
          var time = new Date();
          this.updateChat(sender, this.self.name, message, time);
          break;
        case "REPLY":
          switch (this.parsed_response.REPLY) {
            case "LOGIN: SUCCESS":
              // console.log("LOGIN: SUCCESS");
              this.signinState = false;
              this.initPost(this.self.name);
              break;
            case "GETALLCONTACTS: SUCCESS":
              var contacts = this.parsed_response.contacts;
              for (let i in contacts) {
                if (contacts[i].username !== this.self.name)
                  this.chatwith(contacts[i].username);
              }
              break;
            case "SEARCHCONTACTS: SUCCESS":
              if (this.parsed_response.contacts.length > 1)
                this.searchUserForm.usersdata = this.parsed_response.contacts;
              else
                this.searchUserForm.usersdata = [this.parsed_response.contacts];
              break;
          }
          break;
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
          this.chatlist[chat_key].show = 1;
        }
      }
    },
    text2: function(args) {
      let recipient = args[0];
      let message = args[1];
      let time = new Date(); //Todo: formalize date
      this.request =
        '{"type": "TEXT"' +
        ', "sender":"' +
        this.self.name +
        '", "recipient":"' +
        recipient +
        '", "message":"' +
        message +
        '"}';
      this.send();
      //Todo: determine text successful
      // if (this.parsed_response.REPLY === 'TEXT: SUCCESS'){
      //     this.updateChat(recipient, message);
      // } else {
      //     console.log(this.parsed_response.message);
      // }
      this.updateChat(this.self.name, recipient, message, time);
    },
    updateChat: function(sender, recipient, message, time) {
      let hour = time.getHours();
      let paddingHour = hour > 9 ? "" : "0";
      let minute = time.getMinutes();
      let paddingMinute = minute > 9 ? "" : "0";
      time = paddingHour + hour + ":" + paddingMinute + minute;
      if (sender === this.self.name) {
        //message out
        for (var chat_key in this.chatlist) {
          if (this.chatlist[chat_key].name === recipient) {
            var message_key = this.chatlist[chat_key].messages.length;
            Vue.set(this.chatlist[chat_key].messages, message_key, {
              avatar: "/img/avatar.jpg",
              time: time,
              content: message,
              objectflag: 0
            });
          }
        }
      } else {
        for (chat_key in this.chatlist) {
          //message in
          if (this.chatlist[chat_key].name === sender) {
            message_key = this.chatlist[chat_key].messages.length;
            Vue.set(this.chatlist[chat_key].messages, message_key, {
              avatar: "/img/avatar.jpg",
              time: time,
              content: message,
              objectflag: 1
            });
          }
        }
      }

      //Todo: animation: slide down to the new message
    },
    searchHistory: function(searchField) {},
    showSearchPanel: function() {
      this.searchUserForm.display = true;
    },
    closeSearchPanel: function() {
      this.searchUserForm.display = false;
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
      Vue.set(this.chatlist, this.chat_num, {
        id: ++this.chat_num,
        avatar: "/img/avatar.jpg",
        name: username,
        time: "",
        content: "",
        show: 0,
        messages: []
      });
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
    initPost: function() {
      this.request = "{\n" + 'type: "GETALLCONTACTS",\n' + "}";
      this.send();
    },
    onSignIn: function() {
      this.self = { avatar: "/img/avatar.jpg", name: this.ruleForm.name };
      if (true) {
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
    onSignUp: function() {
      window.location.href = "/sign_up";
    }
  }
};
</script>

<style>
#main-panel {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
}

#self-info {
}

#search-bar {
  background-color: #eefaf9;
}

#chat-list-panel {
  height: 480px;
  overflow: auto;
}

#object-info {
}

#chatting-panel {
  background-color: #f5f7fa;
}

#text-panel {
  background-color: #fbfdff;
  margin-top: 20px;
}

#add {
  margin-top: 10px;
  text-align: center;
  font-size: 40px;
  color: #f0f0f0;
}

#add:hover {
  color: #ffffff;
  cursor: pointer;
}

.chat {
  height: 50px;
  background-color: #abe8e0;
  margin-top: 2px;
  padding-top: 10px;
  padding-bottom: 10px;
}

.chat:hover {
  background-color: #97e2d8;
}

.chat .avatar {
  width: 40px;
  height: 40px;
  border-radius: 20px;
  margin: 5px 12px;
}

.chat .name {
  font-weight: 600;
}

.chat .last-chat-time {
  text-align: right;
}

.chat .last-chat-content {
  line-height: 30px;
}

.avatar {
  width: 50px;
  height: 50px;
  border-radius: 25px;
}

.el-header {
  background-color: #2ec5b1;
  text-align: left;
  line-height: 60px;
}

.el-footer {
  background-color: #f4f4f5;
}

.el-aside {
  background-color: #abe8e0;
}

.el-main {
  background-color: #eefaf9;
}

.el-container {
  height: 600px;
  width: 1300px;
  margin-bottom: 40px;
}

#mask {
  position: absolute;
  top: 0px;
  left: 0px;
  width: 100%;
  height: 100%;
  background-color: #000000;
  opacity: 0.5;
}

#searchUserPanel {
  border-radius: 10px;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  width: 800px;
  height: 500px;
  background-color: #ffffff;
}

#searchResults {
  border-radius: 5px;
  overflow: auto;
  margin-top: 20px;
  margin-left: 50px;
  padding: 10px 10px;
  width: 700px;
  height: 300px;
  background-color: #f2f8fe;
}

.single-user-info {
  border-radius: 10px;
  margin-top: 5px;
  height: 60px;
  padding-top: 30px;
  text-align: center;
}

.single-user-info:hover {
  background: #d5f3ef;
  cursor: pointer;
}
</style>
