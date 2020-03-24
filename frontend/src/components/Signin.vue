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
              <el-col :span="4">
                <el-button style="width: 100px; height: 50px;" v-on:click="onSignIn()">Sign In</el-button>
              </el-col>
              <el-col offset="4" :span="4">
                <router-link
                  to="/Signup"
                  tag="el-button"
                  style="width: 100px; height: 50px; margin-left: 50px;"
                >Sign Up</router-link>
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
                    <el-dropdown-item
                      icon="el-icon-circle-plus-outline"
                      command="createGroup"
                    >Create Group</el-dropdown-item>
                    <el-dropdown-item
                      icon="el-icon-chat-dot-round"
                      command="searchGroups"
                    >Search Groups</el-dropdown-item>
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
            <group-chat
              v-on:click-id="showGroupChat(group.id)"
              v-for="group in grouplist"
              v-bind:key="group.id"
              :name="group.name"
              :time="group.messages[group.messages.length-1].time"
              :content="group.messages[group.messages.length-1].content"
            ></group-chat>
          </el-main>
        </el-aside>
        <single-chat-panel
          v-for="chat in chatlist"
          v-on:send-message="text2($event, args)"
          v-bind:key="chat.id"
          :chat="chat"
          v-show="chat.show"
        ></single-chat-panel>
        <group-chat-panel
          v-for="group in grouplist"
          v-on:send-message="sendGroupMessage($event, args)"
          v-on:add-members="addMembers()"
          v-on:view-members="viewMembers()"
          v-on:exit-group="exitGroup()"
          v-bind:key="group.id"
          :group="group"
          v-show="group.show"
        ></group-chat-panel>
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
              style="margin-top: 50px;"
            ></el-input>
          </el-col>
          <el-col :span="2">
            <el-button
              icon="el-icon-search"
              type="primary"
              @click="searchUsers()"
              style="margin-top: 50px;"
            ></el-button>
          </el-col>
        </el-row>
        <div class="searchResults">
          <label>Search results are as follows:</label>
          <single-user-info
            @chat="chatwith(userdata.username, userdata.publicKey)"
            :key="userdata.username"
            class="single-user-info"
            v-for="userdata in searchUserForm.usersdata"
            :username="userdata.username"
            :email="userdata.email"
            :isloggedin="userdata.IsLoggedIn"
          ></single-user-info>
        </div>
        <el-row>
          <el-col offset="10" :span="4">
            <el-button style="margin-top: 10px;" @click="closeSearchUserPanel">Cancel</el-button>
          </el-col>
        </el-row>
      </div>
    </div>
    <div v-show="createGroupForm.display">
      <div class="mask"></div>
      <div class="searchPanel">
        <el-row>
          <el-col :span="10" offset="6">
            <el-input
              v-model="searchUserForm.searchField"
              placeholder="Type user name"
              style="margin-top: 10px;"
            ></el-input>
          </el-col>
          <el-col :span="2">
            <el-button
              icon="el-icon-search"
              type="primary"
              @click="searchUsers()"
              style="margin-top: 10px;"
            ></el-button>
          </el-col>
        </el-row>
        <div class="searchResults">
          <span>Selected users: {{ selectedGroupUserNames }}</span>
          <div v-for="userdata in searchUserForm.usersdata" :key="userdata.username">
            <input
              type="checkbox"
              :id="userdata.username"
              :value="userdata.username"
              v-model="selectedGroupUserNames"
            />
            <label for="userdata.username">{{userdata.username}}</label>
          </div>
        </div>
        <el-row>
          <el-col :span="10" offset="6">
            <el-input
              v-model="createGroupForm.groupName"
              placeholder="Please input group name"
              style="margin-top: 10px;"
            ></el-input>
          </el-col>
        </el-row>
        <el-row>
          <el-col offset="5" :span="2">
            <el-button
              style="margin-top: 10px;"
              @click="createGroupWithMembers(self.name, selectedGroupUserNames)"
            >Create Group</el-button>
          </el-col>
          <el-col offset="10" :span="4">
            <el-button style="margin-top: 10px;" @click="closeCreateGroupPanel">Cancel</el-button>
          </el-col>
        </el-row>
      </div>
    </div>
    <div v-show="addMembersForm.display">
      <div class="mask"></div>
      <div class="searchPanel">
        <el-row>
          <el-col :span="10" offset="6">
            <el-input
              v-model="searchUserForm.searchField"
              placeholder="Type user name"
              style="margin-top: 10px;"
            ></el-input>
          </el-col>
          <el-col :span="2">
            <el-button
              icon="el-icon-search"
              type="primary"
              @click="searchUsers()"
              style="margin-top: 10px;"
            ></el-button>
          </el-col>
        </el-row>
        <div class="searchResults">
          <span>Selected users: {{ selectedMembers }}</span>
          <div v-for="userdata in searchUserForm.usersdata" :key="userdata.username">
            <input
              type="checkbox"
              :id="userdata.username"
              :value="userdata.username"
              v-model="selectedMembers"
            />
            <label for="userdata.username">{{userdata.username}}</label>
          </div>
        </div>
        <el-row>
          <el-col offset="5" :span="2">
            <el-button
              style="margin-top: 10px;"
              @click="addMembersToExistingGroup(selectedMembersGroupName,selectedMembers)"
            >Add Members</el-button>
          </el-col>
          <el-col offset="10" :span="4">
            <el-button style="margin-top: 10px;" @click="closeaddMembersPanel">Cancel</el-button>
          </el-col>
        </el-row>
      </div>
    </div>
    <div v-show="viewMembersForm.display">
      <div class="mask"></div>
      <div class="searchPanel">
        <div class="searchResults">
          <el-row>
            <el-col v-for="member in selectedMembers" :key="member">
              <label>{{member}}</label>
            </el-col>
          </el-row>
        </div>
        <el-row>
          <el-col offset="10" :span="4">
            <el-button style="margin-top: 10px;" @click="closeviewMembersPanel">Cancel</el-button>
          </el-col>
        </el-row>
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
              style="margin-top: 50px;"
            ></el-input>
          </el-col>
          <el-col :span="2">
            <el-button
              icon="el-icon-search"
              type="primary"
              @click="searchGroups()"
              style="margin-top: 50px;"
            ></el-button>
          </el-col>
        </el-row>
        <div class="searchResults">
          <label>Search results are as follows:</label>
          <single-group-info
            class="single-user-info"
            :key="groupdata.groupName"
            v-for="groupdata in searchGroupForm.groupsdata"
            :groupname="groupdata.groupName"
          ></single-group-info>
        </div>
        <el-row>
          <el-col offset="10" :span="4">
            <el-button style="margin-top: 10px;" @click="closeSearchGroupPanel">Cancel</el-button>
          </el-col>
        </el-row>
        <!--Todo: click fails to work-->
      </div>
    </div>
  </div>
</template>
<script>
import singleUserInfo from "./single-user-info.vue";
import singleChat from "./single-chat.vue";
import singleChatPanel from "./single-chat-panel.vue";
import groupChatPanel from "./group-chat-panel.vue";
import groupChat from "./group-chat.vue";
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
        dh_prime: null,
        dh_generator: null,
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
      selectedGroupUserNames: [],
      selectedMembers: [],
      selectedMembersGroupName: "",
      searchUserForm: {
        display: false,
        searchField: "",
        usersdata: []
      },
      createGroupForm: {
        display: false,
        groupName: ""
      },
      searchGroupForm: {
        display: false,
        searchField: "",
        groupsdata: []
      },
      addMembersForm: {
        display: false,
        groupName: ""
      },
      viewMembersForm: {
        display: false
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
        dh: null
      },
      search: "",
      chat_num: 1,
      groupChat_num: 0,
      grouplist: [],
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
          shared_key: "",
          messages: [
            {
              avatar: "/img/avatar.jpg",
              time: "2020-02-29 00:00",
              content:
                "11111111111ssssssssssssssssssssssssssssssssssssssssssssswfergthyujikujynhbgfdcsxdcerftgyjuikujhtrgefwdfwretryutecwvetyujkilouyjhngssssssssssssssssssss!",
              objectflag: 1, //0 from me, 1 to me
              status: 2 // new: 0; unread: 1; read: 2
            },
            {
              avatar: "/img/avatar.jpg",
              time: "2020-02-29 00:01",
              content: "2222fffffffffffffffff!",
              objectflag: 0,
              status: 2 // new: 0; unread: 1; read: 2
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
    singleChatPanel,
    groupChat,
    groupChatPanel
  },
  mounted() {
    if ("WebSocket" in window) {
      this.websocket = new WebSocket("ws://localhost:1234");
      this.initWebSocket();
      // test: to delete
      // this.ruleForm.name = "a2020";
      // this.ruleForm.pwd = "Aa1111!!";
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
            var message = this.parsed_response.message;
            this.getNewText(sender, message);
            break;
          case "GROUPTEXT":
            var sender = this.parsed_response.sender;
            var groupName = this.parsed_response.groupName;
            var message = this.parsed_response.message;
            this.getNewGroupText(sender, groupName, message);
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
                    this.chatwith(contacts[i].username, contacts[i].publicKey);
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
              case "SEARCHGROUPS: SUCCESS":
                if (this.parsed_response.groups.length > 1)
                  this.searchGroupForm.groupsdata = this.parsed_response.groups;
                else
                  this.searchGroupForm.groupsdata = [
                    this.parsed_response.groups
                  ];
                break;
              case "GETCHATHISTORY: SUCCESS":
                var messages = this.parsed_response.messages;
                var object_name = "";
                if (messages === undefined) break;
                else if (messages.length === undefined) {
                  messages = [messages];
                }
                for (let i in messages) {
                  var sender = messages[i].sender;
                  var recipient = messages[i].recipient;
                  if (i == 0) {
                    object_name = sender;
                    if (sender === this.self.name) object_name = recipient;
                  }
                  var message = messages[i].message;
                  var time = messages[i].timestamp;
                  this.updateChat(
                    sender,
                    recipient,
                    this.decrypt(message, this.getSharedSecret(object_name)),
                    new Date(time),
                    2
                  );
                }
                break;
              case "GETPUBLICKEY: SUCCESS":
                var username = this.parsed_response.username;
                var public_key = this.parsed_response.publicKey;
                this.updatePublicKey(username, public_key);
                this.checkNewSentMessage(username);
                break;
              case "GETALLUSERGROUPS: SUCCESS":
                var groups = this.parsed_response.groups;
                groups.forEach(group => {
                  this.creatNewGroupChat(group.groupName, group.members);
                });
                break;
              case "GETGROUPHISTORY: SUCCESS":
                var messages = this.parsed_response.messages;
                if (messages === undefined) break;
                else if (messages.length === undefined) {
                  messages = [messages];
                }
                for (let i in messages) {
                  var sender = messages[i].sender;
                  var groupName = messages[i].groupName;
                  var message = messages[i].message;
                  var time = messages[i].timestamp;
                  this.updateGroupChat(
                    sender,
                    groupName,
                    message,
                    new Date(time),
                    2
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
      for (var groupId of Object.keys(this.grouplist)) {
        this.grouplist[groupId].show = 0;
      }
      for (var chat_key of Object.keys(this.chatlist)) {
        if (this.chatlist[chat_key].id !== id) {
          this.chatlist[chat_key].show = 0;
        } else {
          this.getChatHistory(this.chatlist[chat_key].name, chat_key);
          this.chatlist[chat_key].has_got_history = true;
          this.chatlist[chat_key].show = 1;
          this.chatlist[chat_key].badge_hidden = true;
          this.readAllMessagesByChatkey(chat_key);
        }
      }
    },
    readAllMessagesByChatkey: function(chat_key) {
      for (var message_key in this.chatlist[chat_key].messages) {
        this.chatlist[chat_key].messages[message_key].status = 1; // read
      }
    },
    readAllMessagesByUsername: function(username) {
      var chat_key = this.isContactExist(username);
      for (var message_key in this.chatlist[chat_key].messages) {
        this.chatlist[chat_key].messages[message_key].status = 1; // read
      }
    },
    text2: function(args) {
      let recipient = args[0];
      let message = args[1];
      let time = new Date();
      let shared_key = this.getSharedSecret(recipient);

      // Todo: bug to fix: a blank message popup created in text field
      if (message === "") {
        alert("Please type sth. before sending!");
        return;
      }

      this.request =
        '{"type": "TEXT"' +
        ', "sender":"' +
        this.self.name +
        '", "recipient":"' +
        recipient +
        '", "message":"' +
        this.encrypt(message, shared_key) +
        '"}';
      this.send();

      //Todo: determine text successful
      // if (this.parsed_response.REPLY === 'TEXT: SUCCESS'){
      //     this.updateChat(recipient, message);
      // } else {
      //     console.log(this.parsed_response.message);
      // }
      this.updateChat(this.self.name, recipient, message, time, 2);
    },
    getSharedSecret(username) {
      for (var chat_key in this.chatlist) {
        if (this.chatlist[chat_key].name === username) {
          return this.chatlist[chat_key].shared_key;
        }
      }
    },
    getPublicKeyof(username) {
      this.request =
        "{\n" +
        'type: "GETPUBLICKEY",\n' +
        'username:"' +
        username +
        '",\n' +
        "}";
      this.send();
    },
    updatePublicKey(username, public_key) {
      var chat_key = this.isContactExist(username);
      var public_key_hex = Buffer.from(public_key, "base64").toString("hex");
      if (chat_key !== false) {
        this.chatlist[chat_key].public_key = public_key_hex;
        return;
      }
      chat_key = this.createNewChat(username, public_key_hex);
      this.getChatHistory(username, chat_key);
    },
    checkNewSentMessage: function(username) {
      var chat_key = this.isContactExist(username);
      for (var message_key in this.chatlist[chat_key].messages) {
        if (this.chatlist[chat_key].messages[message_key].status === 0) {
          this.getNewText(
            username,
            this.chatlist[chat_key].messages[message_key].content
          );
        }
      }
    },
    getNewText: function(sender, message) {
      var chat_key = this.isContactExist(sender);
      if (chat_key === false) {
        this.getPublicKeyof(sender);
        return;
      }

      var shared_key = this.getSharedSecret(sender);
      message = this.decrypt(message, shared_key);
      var time = new Date();
      this.updateChat(sender, this.self.name, message, time, 0);
      this.popUpChat(chat_key);
      // Todo: onclick: turn to concerned chat panel
      const h = this.$createElement;

      this.$notify({
        title: "New message!",
        message: h("i", { style: "color: teal" }, sender + ": " + message)
      });
    },
    getNewGroupText(sender, groupName, message) {
      var time = new Date();
      this.updateGroupChat(sender, groupName, message, time, 0);
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
    updateChat: function(sender, recipient, message, time, message_status) {
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
        objectflag: 0,
        status: message_status
      };
      var message_key = "";

      if (sender === this.self.name) {
        //message out
        for (var chat_key in this.chatlist) {
          if (this.chatlist[chat_key].name === recipient) {
            if (!this.isChatRedundant(chat_key, message_info)) {
              message_key = this.chatlist[chat_key].messages.length;
              Vue.set(
                this.chatlist[chat_key].messages,
                message_key,
                message_info
              );
            }
            this.chatlist[chat_key].messages.sort(this.sortCompareFunction);
          }
        }
      } else {
        for (var chat_key in this.chatlist) {
          //message in
          if (this.chatlist[chat_key].name === sender) {
            if (message_status !== 2) {
              if (this.chatlist[chat_key].badge_hidden)
                this.chatlist[chat_key].new_message_num = 0;
              this.chatlist[chat_key].new_message_num++;
              this.chatlist[chat_key].badge_hidden = false;
            }
            if (!this.isChatRedundant(chat_key, message_info)) {
              if (message_status === 0) message_info.status = 1; //suggest message loaded to panel
              message_key = this.chatlist[chat_key].messages.length;
              message_info.objectflag = 1;
              Vue.set(
                this.chatlist[chat_key].messages,
                message_key,
                message_info
              );
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
    showCreateGroupPanel: function() {
      this.createGroupForm.display = true;
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
    closeCreateGroupPanel: function() {
      this.createGroupForm.display = false;
    },
    closeSearchPanel: function() {
      this.searchUserForm.display = false;
    },
    showaddMembersPanel: function() {
      this.addMembersForm.display = true;
    },
    closeaddMembersPanel: function() {
      this.addMembersForm.display = false;
    },
    showviewMembersPanel: function() {
      this.viewMembersForm.display = true;
    },
    closeviewMembersPanel: function() {
      this.viewMembersForm.display = false;
    },
    fromHex2Array: function(hexString) {
      return new Uint8Array(
        hexString.match(/.{1,2}/g).map(byte => parseInt(byte, 16))
      );
    },
    createNewChat: function(username, public_key) {
      let chat_key = this.chat_num;
      let shared_key = this.self.dh
        .computeSecret(this.fromHex2Array(public_key))
        .toString("hex");
      console.log(this.self.name + " private_key: " + this.self.private_key);
      console.log(username + " public_key: " + public_key);
      console.log("shared_key: " + shared_key);
      Vue.set(this.chatlist, this.chat_num, {
        id: ++this.chat_num,
        avatar: "",
        name: username,
        show: 0,
        new_message_num: 0,
        badge_hidden: true,
        has_got_history: false,
        public_key: public_key,
        shared_key: shared_key,
        messages: [
          {
            avatar: "",
            time: "",
            content: "",
            objectflag: 0
          }
        ]
      });
      return chat_key;
    },
    chatwith: function(username, public_key_base64) {
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
      let public_key = Buffer.from(public_key_base64, "base64").toString("hex");
      this.createNewChat(username, public_key);
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
    },
    searchGroups: function() {
      this.request =
        "{\n" +
        'type: "SEARCHGROUPS",\n' +
        'search:"' +
        this.searchGroupForm.searchField +
        '",\n' +
        "}";
      this.send();
    },
    initPost: function() {
      // this.request = "{\n" + 'type: "GETALLCONTACTS",\n' + "}";
      // this.send();
      this.getAllgroups();
      this.initCrypto();
    },
    isDeviceAuthorised() {
      const item_name = this.self.name + "_private_key";
      const private_key = localStorage.getItem(item_name);
      if (!private_key) {
        alert(
          "Please check your username, or you may be logging in on an unauthorised device!"
        );
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
      }
    },
    initCrypto: function() {
      const key_hex = "2646294A404E635266556A576E5A7234";
      this.crypto.key = CryptoJS.enc.Utf8.parse(this.hex2a(key_hex));
      const iv_str = "0123456789abcdef";
      this.crypto.iv = CryptoJS.enc.Utf8.parse(iv_str);
      const prime_number =
        "B10B8F96A080E01DDE92DE5EAE5D54EC52C99FBCFB06A3C6" +
        "9A6A9DCA52D23B616073E28675A23D189838EF1E2EE652C0" +
        "13ECB4AEA906112324975C3CD49B83BFACCBDD7D90C4BD70" +
        "98488E9C219A73724EFFD6FAE5644738FAA31A4FF55BCCC0" +
        "A151AF5F0DC8B4BD45BF37DF365C1A65E68CFDA76D4DA708" +
        "DF1FB2BC2E4A4371";
      this.crypto.dh_prime = prime_number;
      const generator =
        "A4D1CBD5C3FD34126765A442EFB99905F8104DD258AC507F" +
        "D6406CFF14266D31266FEA1E5C41564B777E690F5504F213" +
        "160217B4B01B886A5E91547F9E2749F4D7FBD7D3B9A92EE1" +
        "909D0D2263F80A76A6A24C087A091F531DBF0A0169B6A28A" +
        "D662A4D18E73AFA32D779D5918D08BC8858F4DCEF97C2A24" +
        "855E6EEB22B3B2E5";
      this.crypto.dh_generator = generator;

      let dh = crypto.createDiffieHellman(
        prime_number,
        "hex",
        generator,
        "hex"
      );
      dh.setPrivateKey(this.fromHex2Array(this.self.private_key));
      this.self.dh = dh;
    },
    // encrypt: function(message) {
    //   return CryptoJS.AES.encrypt(message, this.crypto.key, {
    //     iv: this.crypto.iv,
    //     mode: CryptoJS.mode.CBC,
    //     padding: CryptoJS.pad.Pkcs7
    //   }).toString();
    // },
    decrypt: function(ciphertext, passphrase) {
      console.log("ciphertext: " + ciphertext);
      console.log("passphrase: " + passphrase);
      return CryptoJS.AES.decrypt(ciphertext, passphrase, {
        mode: CryptoJS.mode.CBC,
        padding: CryptoJS.pad.Pkcs7
      }).toString(CryptoJS.enc.Utf8);
    },
    encrypt: function(message, passphrase) {
      return CryptoJS.AES.encrypt(message, passphrase, {
        mode: CryptoJS.mode.CBC,
        padding: CryptoJS.pad.Pkcs7
      }).toString();
    },
    // decrypt: function(ciphertext) {
    //   return CryptoJS.AES.decrypt(ciphertext, this.crypto.key, {
    //           iv: this.crypto.iv,
    //           mode: CryptoJS.mode.CBC,
    //           padding: CryptoJS.pad.Pkcs7
    //       }).toString(CryptoJS.enc.Utf8);
    // },
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
    isChatRedundant: function(chat_key, message) {
      var redundant = false;
      var chat = this.chatlist[chat_key];
      for (var message_key in chat.messages) {
        if (
          chat.messages[message_key].content === message.content &&
          chat.messages[message_key].time === message.time
        )
          redundant = true;
      }
      return redundant;
    },
    popUpChat: function(chat_key) {
      var chat = this.chatlist[chat_key];
      this.chatlist.splice(chat_key, chat_key + 1);
      this.chatlist.unshift(chat);
    },
    sortCompareFunction: function(message_a, message_b) {
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
        case "createGroup":
          this.showCreateGroupPanel();
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
    },
    createGroup(username, groupName) {
      this.request =
        "{\n" +
        'type: "CREATEGROUP",\n' +
        'username: "' +
        username +
        '",\n' +
        'groupName: "' +
        groupName +
        '",\n' +
        "picture:null\n" +
        "}";
      this.send();
    },
    addMembersToGroup(groupName, members) {
      for (let i = 0; i < members.length; i++) {
        this.request =
          "{\n" +
          'type: "JOINGROUP",\n' +
          'username: "' +
          members[i] +
          '",\n' +
          'groupName: "' +
          groupName +
          '",\n' +
          "}";
        this.send();
      }
    },
    addMembersToExistingGroup(groupName, members) {
      this.addMembersToGroup(groupName, members);
      this.closeaddMembersPanel();
    },
    createGroupWithMembers(userName, members) {
      this.createGroup(userName, this.createGroupForm.groupName);
      this.addMembersToGroup(this.createGroupForm.groupName, members);
      this.closeCreateGroupPanel();
      this.creatNewGroupChat(this.createGroupForm.groupName, members);
    },
    getAllgroups() {
      this.request =
        "{\n" +
        'type: "GETALLUSERGROUPS",\n' +
        'myUsername: "' +
        this.self.name +
        '",\n' +
        "}";
      this.send();
    },
    creatNewGroupChat(groupName, members) {
      this.grouplist.push({
        id: this.groupChat_num++,
        avatar: "",
        name: groupName,
        members: members,
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
          }
        ]
      });
    },
    showGroupChat(id) {
      for (var chatId of Object.keys(this.chatlist)) {
        this.chatlist[chatId].show = 0;
      }
      for (var groupId of Object.keys(this.grouplist)) {
        if (id == groupId) {
          this.grouplist[id].show = 1;
        } else {
          this.grouplist[groupId].show = 0;
        }
      }
      this.selectedMembers = this.grouplist[id].members;
      this.selectedMembersGroupName = this.grouplist[id].name;
      this.getGroupHistory(this.grouplist[id].name);
    },
    sendGroupMessage(args) {
      let groupName = args[0];
      let message = args[1];
      let time = new Date();

      if (message === "") {
        alert("Please type sth. before sending!");
        return;
      }
      this.request =
        '{"type": "GROUPTEXT"' +
        ', "sender":"' +
        this.self.name +
        '", "groupName":"' +
        groupName +
        '", "message":"' +
        message +
        '"}';
      this.send();
      this.updateGroupChat(this.self.name, groupName, message, time, 2);
    },
    addMembers() {
      this.showaddMembersPanel();
    },
    viewMembers() {
      this.showviewMembersPanel();
    },
    getGroupHistory(groupName) {
      this.request =
        "{\n" +
        'type: "GETGROUPHISTORY",\n' +
        'myUsername: "' +
        this.self.name +
        '",\n' +
        'groupName: "' +
        groupName +
        '",\n' +
        'historyDays: "' +
        10 +
        '"\n' +
        "}";
      this.send();
    },
    isGroupChatRedundant(groupId, message) {
      var redundant = false;
      var chat = this.grouplist[groupId];
      for (var message_key in chat.messages) {
        if (
          chat.messages[message_key].content === message.content &&
          chat.messages[message_key].time === message.time
        )
          redundant = true;
      }
      return redundant;
    },
    updateGroupChat(sender, groupName, message, time, message_status) {
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
        objectflag: 0, //0 from me, 1 to me
        status: message_status,
        sender: sender
      };
      var message_key = "";
      if (sender === this.self.name) {
        //message out
        for (var groupId in this.grouplist) {
          if (this.grouplist[groupId].name === groupName) {
            if (!this.isGroupChatRedundant(groupId, message_info)) {
              message_key = this.grouplist[groupId].messages.length;
              Vue.set(
                this.grouplist[groupId].messages,
                message_key,
                message_info
              );
              this.grouplist[groupId].messages.sort(this.sortCompareFunction);
            }
          }
        }
      } else {
        for (var groupId in this.grouplist) {
          //message in
          if (this.grouplist[groupId].name === groupName) {
            if (message_status !== 2) {
              if (this.grouplist[groupId].badge_hidden)
                this.grouplist[groupId].new_message_num = 0;
              this.grouplist[groupId].new_message_num++;
              this.grouplist[groupId].badge_hidden = false;
            }
            if (!this.isGroupChatRedundant(groupId, message_info)) {
              if (message_status === 0) message_info.status = 1;
              message_key = this.grouplist[groupId].messages.length;
              message_info.objectflag = 1;
              Vue.set(
                this.grouplist[groupId].messages,
                message_key,
                message_info
              );
            }
            this.grouplist[groupId].messages.sort(this.sortCompareFunction);
          }
        }
      }
    }
  }
};
</script>

<style scoped src="../css/main.css">
</style>
