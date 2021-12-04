<template>
  <div class="welcome-page">
    <img src="@/assets/logo.png" />
    <div class="header">欢迎使用Digging PlatON专属钱包</div>
    <div class="description">我们很高兴与您见面</div>
    <el-button class="first-time-flow-btn" type="primary" @click="onJumpToNext"
      >开始使用</el-button
    >
  </div>
</template>

<script>
export default {
  methods: {
    onJumpToNext() {
      this.$router.push("/select-action");
    },
  },
};
</script>

<style lang="less" scoped>
.welcome-page {
  margin: 110px auto 0 auto;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: center;
  max-width: 442px;
  padding: 0 18px;
  color: black;
  .header {
    font-size: 28px;
    margin-bottom: 22px;
    margin-top: 50px;
    text-align: center;
  }
  .description {
    text-align: center;
    font-size: 16px;
  }
  .first-time-flow-btn {
    width: 184px;
    font-weight: 500;
    margin-top: 44px;
  }
}
</style>