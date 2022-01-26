package com.digquant.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import com.digquant.R
import com.digquant.api.PlatonApi
import com.digquant.databinding.DialogClaimRewardsBinding
import com.digquant.service.WalletManager
import com.digquant.util.AmountUtil
import com.digquant.util.ToastUtil
import com.digquant.util.ViewUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.math.BigInteger


class ClaimRewardsDialog(context: Context, val walletName: String, val reward: String) :
    Dialog(context, R.style.common_dialog) {

    private lateinit var binding: DialogClaimRewardsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogClaimRewardsBinding.inflate(LayoutInflater.from(context), null, false)
        setContentView(binding.root)
        val window = window

        if (window != null) {
            window.setWindowAnimations(R.style.dlg_enter_style)

            window.setGravity(Gravity.BOTTOM)
            window.decorView.setPadding(0, 0, 0, 0)
            val attributes = window.attributes
            attributes.width = ViewUtil.GetScreenWidth()
            attributes.height = WindowManager.LayoutParams.WRAP_CONTENT
            window.attributes = attributes
        }

        binding.claimRewardsAmount.text = reward

        binding.claimWallet.text = walletName

        initEvent()


        GlobalScope.launch(Dispatchers.Main) {


            val walletAddress = WalletManager.GetWalletAddress(walletName)

            val balance = PlatonApi.GetBalance(walletAddress)

            binding.balanceAmount.text = "可用余额: ${balance} LAT"


            binding.feeAmount.text = AmountUtil.convertVonToLat(
                PlatonApi.GetGasPrice().multiply(BigInteger("38640")).toString()
            ) + " LAT"
        }
    }


    private fun initEvent() {
        binding.close.setOnClickListener {
            dismiss()
        }

        binding.sbtnConfirm.setOnClickListener {
            val password = binding.password.text.toString()

            val privateKey =
                WalletManager.GetWalletPrivateKey(walletName, password)

            GlobalScope.launch(Dispatchers.Main) {
                val receiptInfo = PlatonApi.WithdrawDelegateReward(privateKey)

                ToastUtil.showLongToast(context, "领取成功!")

                dismiss()
            }
        }
    }


}