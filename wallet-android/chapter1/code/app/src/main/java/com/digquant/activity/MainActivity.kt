package com.digquant.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import com.digquant.api.PlatonApi
import com.digquant.databinding.ActivityMainBinding
import com.digquant.util.ThreadPoolUtil
import org.web3j.utils.Convert
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
        val inflater = LayoutInflater.from(this)
        binding = ActivityMainBinding.inflate(inflater, null, false);
        setContentView(binding.root)

        handler = Handler();

        initEvent();

    }

    /**
     * 初始化时间
     */
    private fun initEvent() {

        binding.btn.setOnClickListener {
            val walletAddress = binding.walletAddress.text.toString()
            /**
             * 因此platonApi调用Http的方式都是同步的
             * 而在UI线程不能发起Http调用因此需要在子线程调用
             * 把调用任务放在线程池
             */
            ThreadPoolUtil.AddTask {
                /**
                 * 获取platonApi对象
                 */
                val platonApi = PlatonApi.platonApi
                val platonBalance = platonApi.GetBalance(walletAddress)

                /**
                 * 获取的balance单位为Von,要手动转为LAT
                 */
                val lat =
                    Convert.fromVon(BigDecimal(platonBalance.balance), Convert.Unit.LAT).toDouble();
                // 通过handler讲balance传回UI线程
                handler.post {
                    binding.balance.text = lat.toString()
                }
            }
        }
    }
}