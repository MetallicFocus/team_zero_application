<template>
  <div @click="$emit('chat')">
    <el-row>
      <el-col :span="8">
        <label>{{groupname}}</label>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  props: ["groupname"],
  data: function() {
    return {};
  }
};
</script>
