<template>
    <div class="pending-tx-record">
        <el-table :data="datas" style="width: 100%" height="400">
            <el-table-column prop="txHash" label="交易哈希值" width="250">
                <template slot-scope="scope">
                    <span class="ellipsis" :title="scope.row.txHash">{{ scope.row.txHash }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="blockNumber" label="区块"> </el-table-column>

            <el-table-column prop="to" label="接收地址" width="240">
                <template slot-scope="scope">
                    <span class="ellipsis" :title="scope.row.to">{{ scope.row.to }}</span>
                </template>
            </el-table-column>

            <el-table-column label="发送时间" width="240">
                <template slot-scope="scope">
                    <span>{{ timestampToStr(scope.row.timestamp) }}</span>
                </template>
            </el-table-column>

            <el-table-column prop="status" label="状态"> </el-table-column>

            <el-table-column prop="value" label="价值"> </el-table-column>
        </el-table>
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
            bStop: false,
            isPooling: false
        };
    },
    destroyed() {
        this.bStop = true;
    },
    mounted() {
        this.loadData();
    },
    watch: {
        address() {
            this.loadData();
        }
    },
    methods: {
        timestampToStr(timestamp) {
            return TimeUtil.TimestampToString(timestamp);
        },

        loadData() {
            //
            this.datas = this.digging.TransactionManager.GetPendingRecords(this.address);
      
            if (this.isPooling) {
                return;
            }
            this.isPooling = true;

            this.loopPullData();
        },

        loopPullData() {
            //  做退出标记,否则及时页面销毁了,也会一直执行
            if (this.bStop) {
                return;
            }
            setTimeout(() => {
                this.loadData();
            }, 10000);
        }
    }
};
</script>
<style lang="less" scoped>
.pending-tx-record-footer {
    height: 50px;
}
</style>
