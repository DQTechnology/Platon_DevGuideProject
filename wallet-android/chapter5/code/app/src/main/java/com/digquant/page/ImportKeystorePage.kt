package com.digquant.page

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import com.digquant.R
import com.digquant.adapter.BaseViewHolder
import com.digquant.databinding.PageImportKeystoreBinding
import com.digquant.service.WalletManager
import com.digquant.util.CommonUtil
import com.digquant.util.ResourceUtil
import com.digquant.util.ToastUtil

class ImportKeystorePage(itemView: View) : BaseViewHolder(itemView) {

    private val binding: PageImportKeystoreBinding = PageImportKeystoreBinding.bind(itemView)

    private var isEnableName = true
    private var isEnablePassword = true
    private var isEnableKeystore = true

    init {
        initDatas()
        initEvent()
    }

    private fun initDatas() {
        enableImport(false)
    }

    private fun initEvent() {


        binding.btnPaste.setOnClickListener {

            binding.etKeystore.setText(CommonUtil.getTextFromClipboard(itemView.context))

            binding.etKeystore.setSelection(binding.etKeystore.text.toString().length)
        }

        /**
         * 监听钱包名称
         */
        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val name = binding.etName.text.toString().trim()
                // 判断名字的长度
                when {
                    TextUtils.isEmpty(name) -> {
                        showNameError(
                            ResourceUtil.GetString(R.string.validWalletNameEmptyTips),
                            true
                        )
                    }
                    name.length > 20 -> {
                        showNameError(ResourceUtil.GetString(R.string.validWalletNameTips), true)
                    }
                    else -> {
                        showNameError("", false)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        /**
         * 监听密码输入事件
         */
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val password = binding.etPassword.text.toString().trim()

                // 判断名字的长度
                when {
                    TextUtils.isEmpty(password) -> {
                        showPasswordError(
                            ResourceUtil.GetString(R.string.validPasswordEmptyTips),
                            true
                        )
                    }
                    password.length < 6 -> {
                        showPasswordError(
                            ResourceUtil.GetString(R.string.validPasswordTips),
                            true
                        )
                    }
                    else -> {
                        showPasswordError("", false)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etKeystore.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val keystore: String = binding.etKeystore.text.toString().trim()
                if (TextUtils.isEmpty(keystore)) {
                    showKeystoreError(ResourceUtil.GetString(R.string.validKeystoreEmptyTips), true)
                } else {
                    showKeystoreError("", false)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        binding.sbtnImport.setOnClickListener {
            importKeyStore()
        }
    }

    private fun importKeyStore() {

        val name = binding.etName.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val keystore = binding.etKeystore.text.toString().trim()
        /**
         * 检测名字长度
         */
        if (name.length > 20) {
            showNameError(ResourceUtil.GetString(R.string.validWalletNameTips), true)
            return
        }
        /**
         * 检测密码
         */
        if (TextUtils.isEmpty(password)) {
            showPasswordError(ResourceUtil.GetString(R.string.validPasswordEmptyTips), true)
            return
        }
        /**
         * 检测钱包文件是否为空
         */
        if (TextUtils.isEmpty(keystore)) {
            showKeystoreError(ResourceUtil.GetString(R.string.validKeystoreEmptyTips), true)
            return
        }

        val status = WalletManager.ImportKeyStore(name, password, keystore)

        if (status.errCode != 0) {
            ToastUtil.showLongToast(itemView.context, status.errMsg)
        } else {
            ToastUtil.showLongToast(itemView.context, "导入成功!")
        }
    }

    fun showKeystoreError(text: String?, isVisible: Boolean) {

        binding.tvKeystoreError.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.tvKeystoreError.text = text
        this.isEnableKeystore = isVisible
        enableImport(!isEnableName && !isEnablePassword && !isEnableKeystore)
    }

    fun showNameError(text: String?, isVisible: Boolean) {
        binding.tvNameError.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.tvNameError.text = text
        this.isEnableName = isVisible
        enableImport(!isEnableName && !isEnablePassword && !isEnableKeystore)
    }

    fun showPasswordError(text: String?, isVisible: Boolean) {
        binding.tvPasswordError.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.tvPasswordError.text = text
        isEnablePassword = isVisible
        enableImport(!isEnableName && !isEnablePassword && !isEnableKeystore)
    }

    private fun enableImport(enabled: Boolean) {
        binding.sbtnImport.isEnabled = enabled
    }


}