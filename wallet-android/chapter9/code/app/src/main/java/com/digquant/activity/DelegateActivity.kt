package com.digquant.activity

import android.provider.ContactsContract
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.digquant.R
import com.digquant.api.PlatonApi
import com.digquant.databinding.ActivityDelegateBinding
import com.digquant.service.WalletManager
import com.digquant.util.*
import com.platon.aton.widge.ShadowDrawable
import kotlinx.coroutines.launch
import org.web3j.platon.contracts.DelegateContract
import java.math.BigInteger

class DelegateActivity : BaseActivity() {

    /**
     * 使用binding
     */
    lateinit var binding: ActivityDelegateBinding

    lateinit var nodeId: String


    lateinit var walletName: String

    override fun inflateUI(inflater: LayoutInflater): View {
        ViewUtil.SetStatusBarColorToBlack(window)
        binding = ActivityDelegateBinding.inflate(inflater, null, false);
        return binding.root
    }

    override fun initUI() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        ShadowDrawable.setShadowDrawable(
            binding.amountChoose,
            ResourceUtil.GetColor(R.color.color_ffffff),
            DensityUtil.DP2PX(this, 4.0f),
            ResourceUtil.GetColor(R.color.color_cc9ca7c2),
            DensityUtil.DP2PX(this, 5.0f),
            0,
            DensityUtil.DP2PX(this, 2.0f)
        )

        /**
         * 1, 节点id和节点名必须传
         */
        val nodeId = this.intent.getStringExtra("nodeId")
        if (TextUtils.isEmpty(nodeId)) {
            ToastUtil.showLongToast(this, "请选择节点!")
            this.finish()
            return
        }
        this.nodeId = nodeId!!
        val nodeName = this.intent.getStringExtra("nodeName")
        if (TextUtils.isEmpty(nodeName)) {
            ToastUtil.showLongToast(this, "请选择节点!")
            this.finish()
            return
        }

        val url = this.intent.getStringExtra("url")

        binding.delegateNodeName.text = nodeName
        binding.delegateNodeId.text = AddressFormatUtil.formatAddress(nodeId)

        Glide.with(this).load(url)
            .error(R.mipmap.icon_validators_default)
            .placeholder(R.mipmap.icon_validators_default).into(binding.delegateNodeIcon)
        /**
         *
         */
        Glide.with(this).load(R.mipmap.avatar_14)
            .error(R.mipmap.icon_validators_default)
            .placeholder(R.mipmap.icon_validators_default).into(binding.walletIcon)


        val walletName = this.intent.getStringExtra("walletName")
        if (walletName == null) {
            this.walletName = WalletManager.GetCurrentWalletName()
        } else {
            this.walletName = walletName!!
        }




        binding.delegateWalletName.text = this.walletName

        val walletAddress = WalletManager.GetWalletAddress(this.walletName)
        binding.delegateWalletAddress.text =
            AddressFormatUtil.formatAddress(WalletManager.GetWalletAddress(this.walletName))

        lifecycleScope.launch {
            val balance = PlatonApi.GetBalance(walletAddress)

            binding.balance.text = balance.toString()

            binding.delegateFee.text = AmountUtil.convertVonToLat(
                PlatonApi.GetGasPrice().multiply(BigInteger("49920")).toString()
            )
        }
    }

    override fun initEvent() {
        binding.sbtnDelegate.setOnClickListener {

            val password = binding.password.text.toString()

            val lat = binding.delegateAmount.text.toString()

            val privateKey =
                WalletManager.GetWalletPrivateKey(walletName, password)

            lifecycleScope.launch {
                val receiptInfo = PlatonApi.Delegate(privateKey, nodeId, lat)
                ToastUtil.showLongToast(this@DelegateActivity, "委托成功!")
            }

        }

    }


}