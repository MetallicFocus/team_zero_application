<template>
  <div @click="$emit('chat')">
    <el-container id="right-panel">
      <el-header id="object-info">
        <el-row>
            <el-col :span="2"><el-avatar class="avatar" :src="chat.avatar" style="margin-top: 5px;"></el-avatar></el-col>
            <el-col :span="20"><label style="font-size: 30px">{{chat.name}}</label></el-col>
            <el-col :span="2"><i class="el-icon-more more" style="color: #D5F3EF;"></i></el-col>
        </el-row>
      </el-header>
      <el-main id="chatting-panel">
        <div v-for="message in chat.messages">
          <div v-if="message.time !== ''">
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
        </div>
      </el-main>
      <el-footer id="text-panel">
        <el-row>
          <el-col :span="2"><el-button class="el-icon-star-off" style="border-color: #8c939d;"></el-button></el-col>
          <el-col :span="20">
            <el-input v-model="textfield" style="margin-left: 5px;"></el-input>
          </el-col>
          <el-col :span="2">
            <el-popover
              placement="right"
              width="400"
              trigger="click">
              <!--Todo: emoji picker-->
              <!--<div class="emoji-panel" style="width: 100px; height: 100px; bottom: 10px; left: 10px">-->
                <!--<span-->
                  <!--v-for="(emoji, emojiName) in emojis"-->
                  <!--:key="emojiName"-->
                  <!--:title="emojiName"-->
                  <!--style="margin:0 4px 0 0; font-size: 18px;"-->
                  <!--@click="insert(emoji)"-->
                <!--&gt;{{emoji}}</span>-->
              <!--</div>-->
              <el-button type="primary" slot="reference" icon="el-icon-check" @click="send" style="margin-left: 15px;"></el-button>
            </el-popover>
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
