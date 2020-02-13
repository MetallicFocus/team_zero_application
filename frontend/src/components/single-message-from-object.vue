<template>
  <div class="message-from-object">
    <el-row style="margin-top: 20px;">
      <el-col :span="1" style="margin-right: 10px; margin-top: 2px;">
        <el-avatar :src="avatar"></el-avatar>
      </el-col>
      <el-col :span="10">
        <el-card shadow="always">
          <label>{{content}}</label>
          <label style="font-size: 12px; float: right; margin-top: 10px;">{{time}}</label>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  props: ["avatar", "time", "content"],
  data: function() {
    return {};
  }
};
</script>

