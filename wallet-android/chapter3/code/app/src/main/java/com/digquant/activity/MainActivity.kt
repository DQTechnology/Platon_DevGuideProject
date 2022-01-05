package com.digquant.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import androidx.lifecycle.lifecycleScope
import com.digquant.api.PlatonApi
import com.digquant.databinding.ActivityMainBinding
import com.digquant.entity.PlatonParams
import com.digquant.util.ViewUtil
import kotlinx.coroutines.launch
import org.web3j.utils.Convert
import org.web3j.utils.Numeric
import java.math.BigDecimal

class MainActivity : AppCompatActivity() {

    /**
     * 使用binding
     */
    lateinit var binding: ActivityMainBinding

    /**
     * 创建handler
     */
    lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 构建布局

        ViewUtil.SetWindow(window)

        ViewUtil.SetStatusBarColorToBlack(window)

        val inflater = LayoutInflater.from(this)
        binding = ActivityMainBinding.inflate(inflater, null, false);
        setContentView(binding.root)

        handler = Handler();

        initEvent();

    }

    /**
     * 初始化事件
     */
    private fun initEvent() {
        binding.btn.setOnClickListener {
            lifecycleScope.launch {
                /**
                 * 获取钱包余额
                 */
//                val lat = PlatonApi.GetBalance("lat1tgu6pts6nhmneu5zhqly3rc83r6y6ecfmde03e")

                /**
                 * 获取hrp
                 */
//                val hrp = PlatonApi.GetAddressHrp()

//                val gasPrice = PlatonApi.GetGasPrice()

//                val blockInfo = PlatonApi.GetBlockByNumber(6533257)

//                val blockTxNum = PlatonApi.GetBlockTransactionCountByNumber(6533257)

//                val txCount = PlatonApi.GetTransactionCount("lat1tgu6pts6nhmneu5zhqly3rc83r6y6ecfmde03e")


                val receipt = PlatonApi.SendLATTO(
                    "a4ac816da1ab40f805d026009247002f47c8c0a9af95b35ca9741c576466e1a8",
                    "lat1zrq89dhle45g78mm4j8aq3dq5m2shpu56ggc6e",
                    10
                )
                val asdasd = 10;


            }
        }

    }
}