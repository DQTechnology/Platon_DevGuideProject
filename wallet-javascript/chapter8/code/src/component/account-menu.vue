<template>
    <div class="account-menu vertical-layout">
        <div class="account-list">
            <div
                @click="onSwitchAccount(item.account)"
                class="horzontal-layout account-item flex-horzontal-center"
                v-for="(item, index) in accountList"
                :key="index"
            >
                <img src="@/assets/little_dog.jpg" />
                <div class="vertical-layout">
                    <div class="account-name">{{ item.account }}</div>
                    <div class="account-lat">
                        <span>{{ item.lat }}</span> LAT
                    </div>
                </div>
            </div>
        </div>

        <div class="account-item-2" @click="onJumpToImportAccount">
            <i class="el-icon-download"></i>
            <span class="item-name">导入账户</span>
        </div>
    </div>
</template>
<script>
export default {
    data() {
        return {
            accountList: [],
            curAccount: ""
        };
    },
    mounted() {
        this.loadAccountList();
    },
    methods: {
        onSwitchAccount(account) {
            this.$emit("on-switch-account", account);
        },

        ReloadData() {
            this.accountList.forEach(async ele => {
                ele.lat = 0;
                // 获取钱包余额
                ele.lat = await this.digging.TransactionManager.GetBalanceOf(ele.address);
                this.$forceUpdate();
            });
        },
        async loadAccountList() {
            let res = await this.digging.PrivateKeyManager.GetAccountList();

            this.accountList = res.accountList;
            this.ReloadData();

            this.curAccount = res.curAccount;
        },
        onJumpToImportAccount() {
            this.$router.push("/import-account");
        }
    }
};
</script>

<style lang="less" scoped>
.account-menu {
    .account-list {
        overflow-y: auto;
        position: relative;
        max-height: 256px;
        padding-bottom: 10px;
        margin-bottom: 10px;
        border-bottom: 1px solid #ececec;
        .account-item {
            cursor: pointer;
            padding: 10px;
            img {
                border-radius: 50%;
                overflow: hidden;
                width: 32px;
                height: 32px;
                margin-right: 10px;
            }
            &:hover {
                background: #ececec;
            }

            .account-name {
                color: black;
                font-size: 18px;
            }
            .account-lat {
                font-size: 14px;
            }
        }
    }
    .account-item-2 {
        cursor: pointer;

        padding: 10px;
        .el-icon-download {
            font-size: 20px !important;
        }
        .item-name {
            margin-left: 10px;
            font-size: 16px !important;
        }
        &:hover {
            background: #ececec;
        }
    }
}
</style>
