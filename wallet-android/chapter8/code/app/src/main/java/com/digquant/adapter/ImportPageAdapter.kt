package com.digquant.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.digquant.databinding.PageImportKeystoreBinding
import com.digquant.databinding.PageImportMnemonicPhraseBinding
import com.digquant.databinding.PageImportPrivateKeyBinding
import com.digquant.page.ImportKeystorePage
import com.digquant.page.ImportMnemonicPage
import com.digquant.page.ImportPrivateKeyPage
import java.lang.RuntimeException

class ImportPageAdapter : RecyclerView.Adapter<BaseViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        // 对应的创建出三个页面对象
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            0 -> {
                val binding = PageImportMnemonicPhraseBinding.inflate(inflater, parent, false)
                return ImportMnemonicPage(binding.root)
            }
            1 -> {
                val binding = PageImportKeystoreBinding.inflate(inflater, parent, false)
                return ImportKeystorePage(binding.root)
            }
            2 -> {
                val binding = PageImportPrivateKeyBinding.inflate(inflater, parent, false)
                return ImportPrivateKeyPage(binding.root)

            }
            else -> {
                throw RuntimeException("无法识别页面类型")
            }
        }


    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        // 因为页面都是静态的,因此不需要动态渲染
    }

    /**
     * 返回position作为页面类型
     */
    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        // 三个页面
        return 3
    }
}