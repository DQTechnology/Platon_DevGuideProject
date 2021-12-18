<template>
    <div class="tx-record">
        <el-table :data="datas" style="width: 100%" height="400">
            <el-table-column prop="txHash" label="交易哈希值" width="250">
                <template slot-scope="scope">
                    <span class="ellipsis">{{ scope.row.txHash }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="blockNumber" label="区块"> </el-table-column>

            <el-table-column label="确认时间" width="240">
                <template slot-scope="scope">
                    <span>{{ timestampToStr(scope.row.timestamp) }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="address" label="交易类型">
                <template slot-scope="scope">
                    <span>{{ getTXType(scope.row) }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="value" label="价值">
                <template slot-scope="scope">
                    <span>{{ scope.row.value }} LAT</span>
                </template>
            </el-table-column>
            <el-table-column prop="actualTxCost" label="交易费用(LAT)"> </el-table-column>
        </el-table>
        <div class="horzontal-layout flex-center tx-record-footer">
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
import TimeUtil from "@/util/TimeUtil.js";
export default {
    props: {
        address: {
            type: String,
            required: true
        }
    },
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
    watch: {
        address() {
            this.ReloadData();
        }
    },
    methods: {
        timestampToStr(timestamp) {
            return TimeUtil.TimestampToString(timestamp);
        },

        getTXType(row) {
            //
            if (row.txType === "0") {
                if (row.from === this.address) {
                    return "发送";
                } else {
                    return "接收";
                }
            } else if (row.txType === "1") {
                return "创建合约";
            } else if (row.txType === "2") {
                return "执行合约";
            } else if (row.txType === "5000") {
                return "领取奖励";
            } else if (row.txType === "1004") {
                return "委托";
            } else if (row.txType === "1005") {
                return "赎回委托";
            }
            return "未知";
        },
        ReloadData() {
            this.curPageIndex = 1;
            this.loadData();
        },
        async loadData() {
            if (!this.address) {
                return;
            }
            let res = await this.api.txRecord
                .GetTransactionListByAddress({
                    address: this.address,
                    pageNo: this.curPageIndex,
                    pageSize: 20,
                    txType: ""
                })
                .catch(e => {
                    this.$message.error(e);
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
.tx-record-footer {
    height: 50px;
}
</style>
