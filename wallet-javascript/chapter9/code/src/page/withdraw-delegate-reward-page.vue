<template>
    <div class="withdraw-delegate-reward-page vertical-only-layout">
        <header-bar />
        <div class="go-back" @click="onGoBack">< Back</div>
        <page-title>领取委托奖励</page-title>
        <el-form ref="sendForm" :model="withdrawInfo">
            <el-form-item label="领取奖励">
                <el-input class="send-input" disabled v-model="withdrawInfo.withdrawNum" />
            </el-form-item>

            <el-form-item label="所需手续费">
                <el-input disabled class="send-input" v-model="withdrawInfo.gasfee" />
            </el-form-item>

            <el-form-item>
                <div class="horzontal-layout">
                    <span class="flex-1"></span>
                    <el-button class="create-btn" @click="onGoBack">取消</el-button>

                    <el-button class="create-btn" type="primary" @click="onWithdraw"
                        >领取</el-button
                    >
                </div>
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
        pageTitle
    },
    data() {
        return {
            withdrawInfo: {
                withdrawNum: this.$route.query.withdrawNum,
                gasfee: 0
            }
        };
    },

    async mounted() {
        this.withdrawInfo.gasfee = (await this.digging.TransactionManager.CalcGasUsed()) + " Lat";
    },
    methods: {
        async onWithdraw() {
            await this.digging.DelegateManager.WithdrawDelegateReward();

            this.$message.success("领取成功!");
            this.onGoBack();
        },

        onGoBack() {
            this.$router.go(-1);
        }
    }
};
</script>

<style lang="less" scoped>
.withdraw-delegate-reward-page {
    margin: 2% auto 0 auto;
    width: 300px;
    justify-content: flex-start;

    .send-input {
        width: 300px;
        display: block;
    }
    .go-back {
        cursor: pointer;
        font-weight: bold;
        margin: 0px 0 30px 0;
        font-size: 20px;
    }
    .create-btn {
        margin-left: 20px;
        // width: 170px;
        // height: 44px;
    }
}
</style>
