<template>
  <div class="create-password-page vertical-only-layout">
    <header-bar />
    <div class="go-back" @click="onGoBack">< Back</div>

    <div class="create-password-title">创建密码</div>
    <el-form ref="passwordForm" :model="passwordInfo">
      <el-form-item
        prop="newPassword"
        label="新密码(至少8个字符)"
        :rules="[
          {
            required: true,
            message: '请输入新密码(至少8个字符)',
            validator: validator.ValidatePassword,
          },
        ]"
      >
        <el-input
          class="pwd-input"
          type="password"
          v-model="passwordInfo.newPassword"
          placeholder="请输入新密码(至少8个字符)"
          :minlength="8"
        />
      </el-form-item>
      <el-form-item
        prop="confirmPassword"
        label="确认密码"
        :rules="[
          {
            required: true,
            message: '请再次输入密码',
            validator: validator.ValidatePassword,
          },
        ]"
      >
        <el-input
          class="pwd-input"
          type="password"
          v-model="passwordInfo.confirmPassword"
          placeholder="请再次输入密码"
          :minlength="8"
        />
      </el-form-item>
      <el-form-item>
        <el-button class="create-btn" type="primary" @click="onCreate"
          >创建</el-button
        >
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import headerBar from "@/component/header-bar.vue";
export default {
  components: {
    headerBar,
  },
  data() {
    return {
      passwordInfo: {
        newPassword: "",
        confirmPassword: "",
      },
    };
  },
  methods: {
    onGoBack() {
      this.$router.push("/select-action");
    },
    onCreate() {
      this.$refs.passwordForm.validate((vaild) => {
        if (!vaild) {
          return;
        }
        if (
          this.passwordInfo.newPassword !== this.passwordInfo.confirmPassword
        ) {
          this.$message.error("两次密码不一致");
          return;
        }
      });
    },
  },
};
</script>

<style lang="less" scoped>
.create-password-page {
  margin: 2% auto 0 auto;
  width: 820px;
  justify-content: flex-start;
  .create-password-title {
    font-size: 2.5rem;
    margin-bottom: 24px;
    color: black;
  }
  .pwd-input {
    width: 350px;
    display: block;
  }
  .go-back {
    cursor: pointer;
    font-weight: bold;
    margin: 30px 0 30px 0;
    font-size: 20px;
  }
  .create-btn {
    width: 170px;
    height: 44px;
  }
}
</style>