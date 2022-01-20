package com.digquant.entity



import com.digquant.R
import java.util.HashMap

enum class TransactionStatus {
    FAILED {

        override val transactionStatusDescRes: Int
            get() = R.string.failed
        override val transactionStatusDescColorRes: Int
            get() = R.color.color_f5302c
    },
    SUCCESSED {


        override val transactionStatusDescRes: Int
            get() = R.string.success
        override val transactionStatusDescColorRes: Int
            get() = R.color.color_19a201
    },
    PENDING {


        override val transactionStatusDescRes: Int
            get() = R.string.pending
        override val transactionStatusDescColorRes: Int
            get() = R.color.color_105cfe
    },
    TIMEOUT {

        override val transactionStatusDescRes: Int
            get() = R.string.timeout
        override val transactionStatusDescColorRes: Int
            get() = R.color.color_f5302c
    };

    companion object {
        private val map: MutableMap<Int, TransactionStatus> = HashMap()

        fun getTransactionStatusByIndex(index: Int): TransactionStatus? {
            return map[index]
        }

        init {
            for (status in TransactionStatus.values()) {
                map[status.ordinal] = status
            }
        }
    }



    abstract val transactionStatusDescRes: Int
    abstract val transactionStatusDescColorRes: Int
}
