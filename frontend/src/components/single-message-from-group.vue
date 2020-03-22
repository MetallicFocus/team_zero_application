<template>
  <div class="message-from-object">
    <el-row style="margin-top: 20px;">
      <el-col :span="1" style="margin-right: 10px; margin-top: 2px">
        <el-avatar :src="avatar"></el-avatar>
      </el-col>
      <el-col :span="10">
        <el-card shadow="always">
          <el-row>
            <el-col :span="24" style="text-align: left; word-break: break-word;color: red;">
              <label style="float: left;">{{sender}}</label>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24" style="text-align: left; word-break: break-word">
              <label style="float: left;">{{content}}</label>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <label style="font-size: 12px; float: right; margin-top: 10px;">{{time}}</label>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  props: ["avatar", "time", "content", "sender"],
  data: function() {
    return {};
  }
};
</script>

<style scoped src="../css/main.css">
</style>
