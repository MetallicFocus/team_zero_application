<template>
  <div class="chat" @click="$emit('click-id')">
    <el-row>
      <el-col :span="6">
        <el-avatar class="avatar" :src="avatar"></el-avatar>
      </el-col>
      <el-col :span="18">
        <el-row style="margin-top: 3px;">
          <el-col :span="18">
            <label class="name">{{name}}</label>
          </el-col>
          <el-col :span="6">
            <label class="last-chat-time">{{time}}</label>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <label class="last-chat-content">{{content}}</label>
          </el-col>
        </el-row>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  props: ["id", "avatar", "name", "time", "content"],
  data: function() {
    return {};
  }
};
</script>

<style scoped src="../css/main.css">
</style>
