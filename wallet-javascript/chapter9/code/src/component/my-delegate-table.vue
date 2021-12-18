<template>
    <div class="my-delegate-table">
        <div class="horzontal-layout header-bar flex-horzontal-center ">
            <span class="flex-1"></span>
            <span>总委托</span>
            <span style="margin:0 16px;">{{ totalDelegate }}LAT</span>
            <span>可领取</span>
            <span style="margin:0 16px;">{{ totalReward }}LAT</span>
            <el-button
                @click="onJumpToWithdrawRewardPage"
                type="primary"
                size="mini"
                round
                style="margin:0 16px 0 0;"
                >领取</el-button
            >
        </div>
        <el-table :data="datas" style="width: 100%" height="400">
            <el-table-column prop="nodeName" label="节点名" width="120"> </el-table-column>

            <el-table-column prop="nodeId" label="节点ID" width="500">
                <template slot-scope="scope">
                    <span class="ellipsis">{{ scope.row.nodeId }}</span>
                </template>
            </el-table-column>

            <el-table-column prop="deletegateLAT" label="委托LAT"> </el-table-column>

            <el-table-column prop="reward" label="待领取奖励" />

            <el-table-column label="操作" header-align="center" align="center" fixed="right">
                <template slot-scope="scope">
                    <el-button size="mini" type="text" @click="onUnDelegate(scope.row)"
                        >赎回委托</el-button
                    >
                </template>
            </el-table-column>
        </el-table>
    </div>
</template>

<script>
export default {
    data() {
        return {
            datas: [],
            totalDelegate: 0,
            totalReward: 0
        };
    },
    mounted() {
        this.loadData();
    },

    methods: {
        ReloadData() {
            this.loadData();
        },

        onJumpToWithdrawRewardPage() {
            this.$router.push({
                path: "withdraw-delegate-reward",
                query: {
                    withdrawNum: this.totalReward
                }
            });
        },

        async onUnDelegate(row) {
            this.$router.push({
                path: "undelegate-lat",
                query: {
                    deletegateLAT: row.deletegateLAT,
                    nodeName: row.nodeName,
                    nodeId: row.nodeId,
                    stakingBlockNum: row.stakingBlockNum
                }
            });
        },

        async loadData() {
            let retInfo = await this.digging.DelegateManager.GetDelegateInfoList();

            this.datas = retInfo.delegationList;
            this.totalDelegate = retInfo.totalDelegate;
            this.totalReward = retInfo.totalReward;
        }
    }
};
</script>
<style lang="less" scoped>
.my-delegate-table {
    .header-bar {
        padding: 4px 0;
        border-bottom: 1px solid #ececec;
    }
    .my-delegate-table-footer {
        height: 50px;
    }
}
</style>
