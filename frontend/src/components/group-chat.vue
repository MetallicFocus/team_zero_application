<template>
  <div class="chat" @click="$emit('click-id')">
    <el-row>
      <el-col :span="6">
        <el-badge :value="this.newMessageNum" :hidden="this.hidden">
          <el-avatar class="avatar" :src="avatar"></el-avatar>
        </el-badge>
      </el-col>
      <el-col :span="18">
        <el-row>
          <el-col :span="16" style="margin-top: 5px;">
            <label class="name">{{name}}</label>
          </el-col>
          <el-col :span="8">
            <label class="last-chat-time">{{time}}</label>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="22" style="overflow: hidden">
            <label class="last-chat-content" style="float: left">{{content}}</label>
          </el-col>
        </el-row>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  props: ["id", "avatar", "name", "time", "content", "newMessageNum", "hidden"],
  data: function() {
    return {};
  },
  methods: {}
};
</script>

<style scoped src="../css/main.css">
</style>
