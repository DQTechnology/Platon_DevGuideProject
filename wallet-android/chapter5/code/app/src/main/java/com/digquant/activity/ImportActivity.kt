package com.digquant.activity


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.digquant.R
import com.digquant.adapter.ImportPageAdapter
import com.digquant.databinding.ActivityImportWalletBinding
import com.digquant.databinding.LayoutAppTabItem1Binding
import com.digquant.util.DensityUtil
import com.digquant.util.ResourceUtil

class ImportActivity : BaseActivity() {
    private lateinit var binding: ActivityImportWalletBinding

    /**
     * 创建页面对象
     */
    override fun inflateUI(inflater: LayoutInflater): View {
        binding = ActivityImportWalletBinding.inflate(inflater, null, false)
        return binding.root
    }

    /**
     *
     */
    override fun initUI() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        // 初始化tabBar
        val indicatorThickness = DensityUtil.DP2PX(this, 2.0f)
        binding.stbBar.setIndicatorThickness(indicatorThickness)
        binding.stbBar.setIndicatorCornerRadius((indicatorThickness / 2.0f))
        // 设置tabBar的TabView创建函数
        binding.stbBar.setCustomTabView { container, title -> getTableView(container, title) }
        /**
         * 这里支持三种导入方式: 助记词, 钱包文件, 私钥
         */
        val titleList = ArrayList<String>(3)
        with(titleList) {
            add(ResourceUtil.GetString(R.string.mnemonicPhrase))
            add(ResourceUtil.GetString(R.string.keystore))
            add(ResourceUtil.GetString(R.string.privateKey))
        }
        // 创建页面的Adapter
        binding.vpContent.adapter = ImportPageAdapter()
        binding.stbBar.setViewPager(binding.vpContent, titleList)
    }

    override fun initEvent() {}
    /**
     * 创建tabView
     */
    private fun getTableView(container: ViewGroup, title: String): View? {
        val inflater = LayoutInflater.from(this)
        val tabBinding = LayoutAppTabItem1Binding.inflate(inflater, container, false)
        tabBinding.ivIcon.visibility = View.GONE
        tabBinding.tvTitle.text = title
        tabBinding.tvTitle.setTextColor(
            ContextCompat.getColorStateList(
                this,
                R.color.color_app_tab_text2
            )
        )
        return tabBinding.root
    }
}