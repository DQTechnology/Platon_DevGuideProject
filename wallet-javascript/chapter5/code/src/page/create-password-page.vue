<template>
  <div v-if="bShowPage" class="create-password-page vertical-only-layout">
    <header-bar />
    <div class="go-back" @click="onGoBack">< Back</div>
    <page-title>创建密码</page-title>
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
import pageTitle from "@/component/page-title.vue";
export default {
  components: {
    headerBar,
    pageTitle,
  },
  data() {
    return {
      bShowPage: false,
      passwordInfo: {
        newPassword: "",
        confirmPassword: "",
      },
    };
  },
  created() {
    // 需要判断是否已经创建密码,方式跳转错误
    this.judgeExistPassword();
  },
  methods: {
    /**
     * 判断密码是否存在, 如果存在跳转到解锁,页面
     */
    async judgeExistPassword() {
      let IsPwdExist = await this.digging.PasswordManager.IsPasswordExist();
      if (IsPwdExist) {
        // 密码存在说明已经创建密码,然后判断是否已经解锁,未解锁,跳转到界面页面
        let isUnlock = await this.digging.PasswordManager.IsUnlock();
        if (!isUnlock) {
          this.$router.push("/unlock");
        } else {

          // todo 跳转到主界面
        }
      } else {
        this.bShowPage = true;
      }
    },
    /**
     * 点击创建按钮后,指定的函数
     */
    onCreate() {
      this.$refs.passwordForm.validate((vaild) => {
        if (!vaild) {
          return;
        }
        // 去掉密码的两边空格
        let newPassword = this.passwordInfo.newPassword.trim();
        let confirmPassword = this.passwordInfo.confirmPassword.trim();

        if (newPassword !== confirmPassword) {
          this.$message.error("两次密码不一致");
          return;
        }
        // 执行创建创建密码的动作
        this.doCreatePassword(newPassword);
      });
    },
    /**
     * 执行创建创建密码的动作
     */
    async doCreatePassword(newPassword) {
      await this.digging.PasswordManager.CreatePassword(newPassword);
      this.$message.success("创建密码成功!");
      //跳转到创建助记词的页面
      this.$router.push("/seed-phrase");
    },
    onGoBack() {
      this.$router.push("/select-action");
    },
  },
};
</script>

<style lang="less" scoped>
.create-password-page {
  margin: 2% auto 0 auto;
  width: 820px;
  justify-content: flex-start;

  .pwd-input {
    width: 350px;
    display: block;
  }
  .go-back {
    cursor: pointer;
    font-weight: bold;
    margin: 0px 0 30px 0;
    font-size: 20px;
  }
  .create-btn {
    width: 170px;
    height: 44px;
  }
}
</style>