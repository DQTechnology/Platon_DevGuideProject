package com.digquant.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.digquant.R
import com.digquant.databinding.ItemAssetListBinding
import com.digquant.databinding.ItemTransactionListBinding
import com.digquant.databinding.ItemTransactionListFooterBinding
import com.digquant.entity.Transaction
import com.digquant.entity.TransactionStatus
import com.digquant.entity.TransactionType
import com.digquant.service.WalletManager
import com.digquant.util.*
import com.platon.aton.widge.ShadowDrawable
import org.web3j.utils.Convert
import java.math.BigDecimal

class TransactionListAdapter(val type: Int = ME_PAGE) : RecyclerView.Adapter<BaseViewHolder>() {

    companion object {
        var MAIN_PAGE = 0

        /**
         * 我的页面
         */
        var ME_PAGE = 1
        val MAX_ITEM_COUNT = 20

        var COMMON_ITEM_VIEW = 0

        var FOOTER_ITEM_VIEW = 1

        class TransactionFooterViewHolder(itemView: View) : BaseViewHolder(itemView) {


        }

        class TransactionViewHolder(itemView: View, val adapter: TransactionListAdapter) :
            BaseViewHolder(itemView) {
            private val binding = ItemTransactionListBinding.bind(itemView)

            override fun OnRender(position: Int) {

                val transaction = adapter.GetData(position)

                val transactionStatus =
                    TransactionStatus.getTransactionStatusByIndex(transaction.txReceiptStatus)
                // 是否是发送者
                val isSender: Boolean = transaction.from == WalletManager.GetCurWalletAddress()

                // 获取交易类型
                val transactionType =
                    TransactionType.getTxTypeByValue(NumberParserUtils.parseInt(transaction.txType))

                // 判断当前是不是发送
                val isSend =
                    isSender && transactionType !== TransactionType.UNDELEGATE
                            && transactionType !== TransactionType.EXIT_VALIDATOR
                            && transactionType !== TransactionType.CLAIM_REWARDS
                // 判断交易额是否为0
                val isValueZero: Boolean = !BigDecimalUtil.isBiggerThanZero(transaction.value)

                // 如果交易额为0,交易失败或者交易超时,则交易额显示为灰色
                val isTransactionGray =
                   isValueZero || transactionStatus === TransactionStatus.FAILED || transactionStatus === TransactionStatus.TIMEOUT


                if (isTransactionGray) {
                    binding.transactionAmount.text = AmountUtil.formatAmountText(transaction.value)
                    binding.transactionAmount.setTextColor(ResourceUtil.GetColor(R.color.color_b6bbd0))
                } else if (isSend) {
                    //发送LAT,数量显示为红色
                    binding.transactionAmount.text =
                        "-${AmountUtil.formatAmountText(transaction.value)}"
                    binding.transactionAmount.setTextColor(ResourceUtil.GetColor(R.color.color_ff3b3b))
                } else {
                    // 接收LAT数量显示为绿色
                    binding.transactionAmount.text =
                        "+${AmountUtil.formatAmountText(transaction.value)}"

                    binding.transactionAmount.setTextColor(ResourceUtil.GetColor(R.color.color_19a20e))
                }
                if (isTransactionGray) {
                    binding.transactionTime.setTextColor(ResourceUtil.GetColor(R.color.color_61646e_50))
                } else {
                    binding.transactionTime.setTextColor(ResourceUtil.GetColor(R.color.color_61646e))
                }

                binding.transactionStatus.setTextColor(ResourceUtil.GetColor(R.color.color_000000))
                binding.transactionStatus.text = getTxTDesc(transaction, isSend)
                binding.transactionTime.text = DateUtil.format(
                    transaction.timestamp,
                    DateUtil.DATETIME_FORMAT_PATTERN_WITH_SECOND
                )

                binding.transactionStatus.visibility =
                    if (transactionStatus === TransactionStatus.PENDING) View.GONE else View.VISIBLE
                // 如果是转账的,则显示箭头
                if (transactionType == TransactionType.TRANSFER) {
                    binding.transactionStatusIV.setImageResource(if (isSender) R.mipmap.icon_send_transation else R.mipmap.icon_receive_transaction)
                } else {
                    binding.transactionStatusIV.setImageResource(if (isSend) R.mipmap.icon_delegate else R.mipmap.icon_undelegate)
                }
            }


            private fun getTxTDesc(
                transaction: Transaction,
                isSender: Boolean
            ): String {
                val transactionType: TransactionType =
                    TransactionType.getTxTypeByValue(NumberParserUtils.parseInt(transaction.txType))

                return if (transactionType === TransactionType.TRANSFER) {
                    if (isSender) {
                        ResourceUtil.GetString(R.string.sent)
                    } else {
                        ResourceUtil.GetString(R.string.received)
                    }
                } else {
                    ResourceUtil.GetString(transactionType.txTypeDescRes)
                }
            }
        }

    }

    private var transactionList = ArrayList<Transaction>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        if (viewType == COMMON_ITEM_VIEW) {

            val binding = ItemTransactionListBinding.inflate(inflater, parent, false)
            return TransactionViewHolder(binding.root, this)

        } else {
            val binding = ItemTransactionListFooterBinding.inflate(inflater, parent, false)
            return TransactionFooterViewHolder(binding.root)
        }
    }

    fun setTransactionList(transactionList: List<Transaction>) {
        this.transactionList.clear()
        this.transactionList.addAll(transactionList)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.OnRender(position)
    }


    fun GetData(position: Int): Transaction {
        return transactionList[position]
    }

    override fun getItemViewType(position: Int): Int {
        val size = transactionList.size

        if (type == MAIN_PAGE) {
            if (size > MAX_ITEM_COUNT) {
                if (position == size - 1) {
                    return FOOTER_ITEM_VIEW
                }
            }
        }

        return COMMON_ITEM_VIEW
    }

    override fun getItemCount(): Int {
        /**
         * 如果是在发送页面获取,并且数量大于20则出现没查看更多记录
         */
        val size = transactionList.size

        return if (type == ME_PAGE) size else if (size >= MAX_ITEM_COUNT) size + 1 else size
    }
}