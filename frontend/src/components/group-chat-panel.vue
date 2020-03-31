<template>
  <div @click="$emit('chat')">
    <el-container id="right-panel">
      <el-header id="object-info">
        <el-row>
          <el-col :span="2">
            <el-avatar class="avatar" :src="group.avatar" style="margin-top: 5px;"></el-avatar>
          </el-col>
          <el-col :span="20">
            <label style="font-size: 30px">{{group.name}}</label>
          </el-col>
          <el-col :span="2">
            <el-dropdown trigger="click" @command="handleCommand">
              <i class="more el-icon-more el-dropdown-link" style="color: #D5F3EF;"></i>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item
                  icon="el-icon-circle-plus-outline"
                  command="addMember"
                  v-if="!group.userLeft"
                >Add Member</el-dropdown-item>
                <el-dropdown-item
                  icon="el-icon-s-custom"
                  command="members"
                  v-if="!group.userLeft"
                >Members</el-dropdown-item>
                <el-dropdown-item
                  icon="el-icon-s-custom"
                  command="exitGroup"
                  v-if="!group.userLeft"
                >Exit Group</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </el-col>
        </el-row>
      </el-header>
      <el-main id="chatting-panel">
        <div v-for="message in group.messages">
          <div v-if="message.time !== ''">
            <single-message-from-group
              v-if="message.objectflag"
              :avatar="message.avatar"
              :time="message.time"
              :content="message.content"
              :sender="message.sender"
            ></single-message-from-group>
            <single-message-from-self
              v-else
              :avatar="message.avatar"
              :time="message.time"
              :content="message.content"
            ></single-message-from-self>
          </div>
        </div>
      </el-main>
      <el-footer id="text-panel">
        <el-row v-if="group.userLeft">
          <el-col :span="24">
            <h1>You exited the group, you can't send or receive messages</h1>
          </el-col>
        </el-row>
        <el-row v-else>
          <el-col :span="2">
            <el-button class="el-icon-star-off" style="border-color: #8c939d;"></el-button>
          </el-col>
          <el-col :span="20">
            <el-input v-model="textfield" style="margin-left: 5px;"></el-input>
          </el-col>
          <el-col :span="2">
            <el-popover placement="right" width="400" trigger="click">
              <el-button
                type="primary"
                slot="reference"
                icon="el-icon-check"
                @click="send"
                style="margin-left: 15px;"
              ></el-button>
            </el-popover>
          </el-col>
        </el-row>
      </el-footer>
    </el-container>
  </div>
</template>

<script>
import singleMessageFromGroup from "./single-message-from-group.vue";
import singleMessageFromSelf from "./single-message-from-self.vue";

export default {
  props: ["group"],
  data: function() {
    return {
      textfield: ""
    };
  },
  components: {
    singleMessageFromGroup,
    singleMessageFromSelf
  },
  methods: {
    send() {
      let temp = this.textfield;
      this.textfield = "";
      this.$emit("send-message", [this.group.name, temp]);
    },
    handleCommand(command) {
      switch (command) {
        case "addMember":
          this.$emit("add-members");
          break;
        case "members":
          this.$emit("view-members");
          break;
        case "exitGroup":
          this.$emit("exit-group");
          this.group.userLeft = true;
          break;
      }
    }
  }
};
</script>

<style scoped src="../css/main.css">
</style>
