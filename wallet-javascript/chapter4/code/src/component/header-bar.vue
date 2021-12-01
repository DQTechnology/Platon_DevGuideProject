<template>
  <div class="logo-container horzontal-layout">
    <img class="logo" src="@/assets/logo_48.png" />
    <span class="title">Digging</span>
  </div>
</template>

<style lang="less" scoped>
.logo-container {
  height: 70px;
  padding:5px 0 0 5px;
  .logo {
    width: 32px;
    height: 32px;
  }
  .title {
    font-size: 16px;
    font-weight: bold;
    display: inline-block;
    margin-left: 10px;
    line-height: 32px;
  }
}
</style>