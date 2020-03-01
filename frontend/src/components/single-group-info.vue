<template>
  <div @click="$emit('chat')">
    <el-row>

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
