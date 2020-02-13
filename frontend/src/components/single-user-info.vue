<template>
  <div @click="$emit('chat')">
    <el-row>
      <el-col :span="8">
        <label>{{username}}</label>
      </el-col>
      <el-col :span="8">
        <label>{{email}}</label>
      </el-col>
      <el-col :span="8" v-if="isloggedin">
        <label>on line</label>
      </el-col>
      <el-col :span="8" v-else>
        <label>off line</label>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  props: ["username", "email", "isloggedin"],
  data: function() {
    return {};
  }
};
</script>

<style>
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