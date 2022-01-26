package com.digquant.activity

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.digquant.R
import com.digquant.api.PlatonApi
import com.digquant.databinding.ActivityWithdrawDelegateBinding
import com.digquant.service.WalletManager
import com.digquant.util.*
import com.platon.aton.widge.ShadowDrawable
import jnr.ffi.provider.jffi.NumberUtil
import kotlinx.coroutines.launch
import org.web3j.utils.Numeric
import java.math.BigInteger

class WithdrawDelegateActivity : BaseActivity() {


    lateinit var binding: ActivityWithdrawDelegateBinding

    lateinit var nodeId: String


    lateinit var walletName: String


    override fun inflateUI(inflater: LayoutInflater): View {
        ViewUtil.SetStatusBarColorToBlack(window)
        binding = ActivityWithdrawDelegateBinding.inflate(inflater, null, false);
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


        walletName = this.intent.getStringExtra("walletName")!!


        //
        binding.delegateWalletName.text = walletName
        binding.delegateWalletAddress.text =
            AddressFormatUtil.formatAddress(WalletManager.GetWalletAddress(walletName))

        // 显示钱包信息
        binding.delegateWalletName.text = walletName

        binding.delegateWalletAddress.text =
            AddressFormatUtil.formatAddress(WalletManager.GetWalletAddress(walletName))


        val url = this.intent.getStringExtra("url")

        binding.delegateNodeName.text = nodeName
        binding.delegateNodeId.text = AddressFormatUtil.formatAddress(nodeId)

        Glide.with(this).load(url)
            .error(R.mipmap.icon_validators_default)
            .placeholder(R.mipmap.icon_validators_default).into(binding.delegateNodeIcon)

        val delegateAmount = this.intent.getStringExtra("delegateAmount")
        binding.delegateAmount.text = delegateAmount

        lifecycleScope.launch {
            binding.delegateFee.text = AmountUtil.convertVonToLat(
                PlatonApi.GetGasPrice().multiply(BigInteger("49920")).toString()
            )
        }
    }

    override fun initEvent() {
        binding.sbtnWithdrawDelegate.setOnClickListener {
            val password = binding.password.text.toString()
            // 赎回的lat
            val lat = binding.withdrawAmount.text.toString()

            //
            val privateKey =
                WalletManager.GetWalletPrivateKey(walletName, password)


            lifecycleScope.launch {
                //获取委托所在的块高

                val stakingBlockNum = getDelegationIdInfo()

                val receiptInfo = PlatonApi.UnDelegate(privateKey, stakingBlockNum, nodeId, lat)

                ToastUtil.showLongToast(this@WithdrawDelegateActivity, "赎回委托成功!")

            }
        }
    }

    /**
     * 获取在指定节点的委托信息
     */
    private suspend fun getDelegationIdInfo(): BigInteger {
        val walletAddress = WalletManager.GetWalletAddress(walletName)

        val delegateNodeList = PlatonApi.GetRelatedListByDelAddr(walletAddress)
        delegateNodeList.forEach {

            // 从区块链中获取的nodeId是不带0x, 需要手动加上
            if (Numeric.prependHexPrefix(it.nodeId) == nodeId) {
                return it.stakingBlockNum!!
            }
        }
        return BigInteger.ZERO
    }

}