package com.digquant.activity

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.digquant.api.PlatonApi
import com.digquant.databinding.ActivitySendTransactionBinding
import com.digquant.service.WalletManager
import com.digquant.util.AmountUtil
import com.digquant.util.ToastUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigInteger

class SendTransactionActivity : BaseActivity() {

    private lateinit var binding: ActivitySendTransactionBinding

    private var balance: Double = 0.0

    override fun inflateUI(inflater: LayoutInflater): View {
        binding = ActivitySendTransactionBinding.inflate(inflater, null, false)

        return binding.root
    }

    override fun initUI() {
        loadData()
    }

    /**
     * 加载数据
     */
    private fun loadData() {
        lifecycleScope.launch {
            getBalance()
            getGasFee()
        }
    }

    /**
     * 获取当前钱包余额
     */
    private suspend fun getBalance() {
        val walletAddress = WalletManager.GetCurWalletAddress()
        val amount = PlatonApi.GetBalance(walletAddress)
        this.balance = amount
        binding.walletBalance.text = "当前余额: ${amount.toString()} LAT"
    }

    /**
     * 获取手续费
     */
    private suspend fun getGasFee() {
        val gasfee = PlatonApi.GetGasPrice().multiply(BigInteger("21000"))
        binding.feeAmount.text = AmountUtil.formatAmountText(gasfee.toString())
    }

    override fun initEvent() {
        binding.sendBtn.setOnClickListener {
            // 1, 通过密码获取钱包秘钥
            val password = binding.password.text.toString()
            if (TextUtils.isEmpty(password)) {
                ToastUtil.showLongToast(this, "密码不能为空")
                return@setOnClickListener
            }

            val privateKey =
                WalletManager.GetWalletPrivateKey(WalletManager.GetCurrentWalletName(), password)
            if (TextUtils.isEmpty(privateKey)) {
                ToastUtil.showLongToast(this, "密码错误")
                return@setOnClickListener
            }

            val toAddress = binding.walletAddress.text.toString()
            if (TextUtils.isEmpty(toAddress)) {
                ToastUtil.showLongToast(this, "接收地址不能为空")
                return@setOnClickListener
            }
            val lat = binding.walletAmount.text.toString()
            lifecycleScope.launch {
                // 发送lat
                val receiptInfo = PlatonApi.SendLATTO(privateKey, toAddress, lat.toLong())

                ToastUtil.showLongToast(this@SendTransactionActivity, "发送成功")

                delay(1000)
                this@SendTransactionActivity.finish()
            }
        }
    }
}