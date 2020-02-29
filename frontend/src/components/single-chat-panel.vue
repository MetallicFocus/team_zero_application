<template>
  <div @click="$emit('chat')">
    <el-container id="right-panel">
      <el-header id="object-info">
        <el-avatar class="avatar" :src="chat.avatar" style="margin-top: 5px;"></el-avatar>
        <label style="font-size: 30px">{{chat.name}}</label>
      </el-header>
      <el-main id="chatting-panel">
        <div v-for="message in chat.messages">
          <single-message-from-object
            v-if="message.objectflag"
            :avatar="message.avatar"
            :time="message.time"
            :content="message.content"
          ></single-message-from-object>
          <single-message-from-self
            v-else
            :avatar="message.avatar"
            :time="message.time"
            :content="message.content"
          ></single-message-from-self>
        </div>
      </el-main>
      <el-footer id="text-panel">
        <el-row>
          <el-col :span="22">
            <el-input v-model="textfield" style="margin-left: 5px;"></el-input>
          </el-col>
          <el-col :span="1">
            <el-button type="primary" icon="el-icon-check" @click="send" style="margin-left: 15px;"></el-button>
          </el-col>
        </el-row>
      </el-footer>
    </el-container>
  </div>
</template>

<script>
import singleMessageFromObject from "./single-message-from-object.vue";
import singleMessageFromSelf from "./single-message-from-self.vue";

export default {
  props: ["chat"],
  data: function() {
    return {
      textfield: ""
    };
  },
  components: {
    singleMessageFromObject,
    singleMessageFromSelf
  },
  methods: {
    send: function() {
      let temp = this.textfield;
      this.textfield = "";
      this.$emit("send-message", [this.chat.name, temp]);
    }
  }
};
</script>

<style scoped src="../css/main.css">
</style>
