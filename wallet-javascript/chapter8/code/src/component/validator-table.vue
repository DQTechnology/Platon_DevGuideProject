<template>
    <div class="validator-table">
        <el-table :data="datas" style="width: 100%" height="400">
            <el-table-column prop="ranking" label="排名" width="100" />

            <el-table-column prop="nodeName" label="节点名" width="120"> </el-table-column>

            <el-table-column label="状态">
                <template slot-scope="scope">
                    <span>{{ getStatus(scope.row) }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="totalValue" label="总质押">
                <template slot-scope="scope">
                    <span>{{ scope.row.totalValue }}LAT</span>
                </template>
            </el-table-column>

            <el-table-column prop="delegateValue" label="委托人数">
                <template slot-scope="scope">
                    <span>{{ scope.row.delegateValue }}LAT</span>
                </template>
            </el-table-column>

            <el-table-column prop="delegatedRewardRatio" label="委托奖励比例" />

            <el-table-column prop="delegateQty" label="委托者" />
            <el-table-column prop="blockQty" label="已产生区块数" />

            <el-table-column prop="expectedIncome" label="预计年收化率" />

            <el-table-column prop="deleAnnualizedRate" label="预计委托年化率" />
            <el-table-column prop="genBlocksRate" label="出块率" />
            <el-table-column prop="version" label="版本号" />

            <el-table-column label="操作" header-align="center" align="center" fixed="right">
                <template slot-scope="scope">
                    <el-button size="mini" type="text" @click="onDelegate(scope.row)"
                        >委托</el-button
                    >
                </template>
            </el-table-column>
        </el-table>
        <div class="horzontal-layout flex-center validator-table-footer">
            <el-pagination
                class="table-pager"
                background
                layout="total,  prev, pager, next"
                :total="totals"
                :page-size="20"
                :current-page.sync="curPageIndex"
                @current-change="loadData"
            ></el-pagination>
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
            datas: [],
            curPageIndex: 1,
            totals: 0
        };
    },
    mounted() {
        this.loadData();
    },

    methods: {
        ReloadData() {
            this.curPageIndex = 1;
            this.loadData();
        },
        onDelegate(row) {
 
            this.$router.push({
                path: "/delegate-lat",
                query: {
                    nodeId: row.nodeId,
                    nodeName: row.nodeName
                }
            });
        },
        getStatus(row) {
            if (row.status === 1) {
                return "候选中";
            } else if (row.status === 2) {
                return "活跃中";
            } else if (row.status === 3) {
                return "出块中";
            } else if (row.status === 6) {
                return "共识中";
            }
        },

        async loadData() {
            let res = await this.api.validator
                .GetAliveStakingList({
                    pageNo: this.curPageIndex,
                    pageSize: 20,
                    queryStatus: "all"
                })
                .catch(e => {
                    console.log(e);
                });

            if (res.code !== 0) {
                this.$message.error(res.errMsg);
                return;
            }
            this.datas = res.data;
            this.totals = res.totalCount;
        }
    }
};
</script>
<style lang="less" scoped>
.validator-table-footer {
    height: 50px;
}
</style>
