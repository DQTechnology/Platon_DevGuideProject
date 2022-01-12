package com.digquant.page

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText
import com.digquant.R
import com.digquant.adapter.BaseViewHolder
import com.digquant.databinding.PageImportMnemonicPhraseBinding
import com.digquant.service.WalletManager
import com.digquant.util.CheckStrength
import com.digquant.util.ResourceUtil
import com.digquant.util.ToastUtil

class ImportMnemonicPage(itemView: View) : BaseViewHolder(itemView) {


    private val binding: PageImportMnemonicPhraseBinding =
        PageImportMnemonicPhraseBinding.bind(itemView)

    private var mShowPassword = false
    private var mShowRepeatPassword = false

    private var isEnableName = true
    private var isEnablePassword = true
    private var isMnemonicPhrase = true

    init {
        initDatas()
        initEvent()
    }

    private fun initDatas() {
        enableImport(false)
    }

    /**
     * 初始化事件
     */
    private fun initEvent() {
        binding.ivPasswordEyes.setOnClickListener {
            showPassword()
        }

        binding.ivRepeatPasswordEyes.setOnClickListener {
            showRepeatPassword()
        }
        /**
         * 导入
         */
        binding.sbtnImport.setOnClickListener {
            importMnemonic()
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
                val repeatPassword = binding.etRepeatPassword.text.toString().trim()

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
                    password != repeatPassword -> {
                        showPasswordError(
                            ResourceUtil.GetString(R.string.passwordTips),
                            true
                        )
                    }
                    else -> {
                        showPasswordError("", false)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                val password: String = binding.etPassword.text.toString().trim()
                checkPwdStreng(password)
            }
        })
        /**
         * 监听重复密码输入事件
         */
        binding.etRepeatPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val password = binding.etPassword.text.toString().trim()
                val repeatPassword = binding.etRepeatPassword.text.toString().trim()

                // 判断名字的长度
                when {
                    TextUtils.isEmpty(repeatPassword) -> {
                        showPasswordError(
                            ResourceUtil.GetString(R.string.validRepeatPasswordEmptyTips),
                            true
                        )
                    }
                    password != repeatPassword -> {
                        showPasswordError(
                            ResourceUtil.GetString(R.string.passwordTips),
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
        /**
         *
         */
        addWordWatcher()
    }

    /**
     * 导入助记词
     */
    private fun importMnemonic() {

        val name = binding.etName.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val repeatPassword = binding.etRepeatPassword.text.toString().trim()
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
         * 检测重复密码
         */
        if (TextUtils.isEmpty(repeatPassword)) {
            showPasswordError(ResourceUtil.GetString(R.string.validRepeatPasswordEmptyTips), true)
            return
        }

        if (password != repeatPassword) {
            showPasswordError(ResourceUtil.GetString(R.string.passwordTips), true)
        }

        val mnemonicWords = ArrayList<String>(12)
        /**
         * 添加助记词到list
         */
        with(mnemonicWords) {
            val mnemonic1: String = binding.etMnemonic1.text.toString().trim()
            val mnemonic2: String = binding.etMnemonic2.text.toString().trim()
            val mnemonic3: String = binding.etMnemonic3.text.toString().trim()
            val mnemonic4: String = binding.etMnemonic4.text.toString().trim()
            val mnemonic5: String = binding.etMnemonic5.text.toString().trim()
            val mnemonic6: String = binding.etMnemonic6.text.toString().trim()
            val mnemonic7: String = binding.etMnemonic7.text.toString().trim()
            val mnemonic8: String = binding.etMnemonic8.text.toString().trim()
            val mnemonic9: String = binding.etMnemonic9.text.toString().trim()
            val mnemonic10: String = binding.etMnemonic10.text.toString().trim()
            val mnemonic11: String = binding.etMnemonic11.text.toString().trim()
            val mnemonic12: String = binding.etMnemonic12.text.toString().trim()

            add(mnemonic1)
            add(mnemonic2)
            add(mnemonic3)
            add(mnemonic4)
            add(mnemonic5)
            add(mnemonic6)
            add(mnemonic7)
            add(mnemonic8)
            add(mnemonic9)
            add(mnemonic10)
            add(mnemonic11)
            add(mnemonic12)
        }

        /**
         * 去钱包管理类创建Session
         */
        val isSucceed = WalletManager.ImportMnemonicWords(name, password, mnemonicWords)

        if (!isSucceed) {
            ToastUtil.showLongToast(itemView.context, "导入助记词失败")
        } else {
            ToastUtil.showLongToast(itemView.context, "导入成功!")
        }
    }

    /**
     * 添加每个助记词输入框的监听事件
     */
    private fun addWordWatcher() {
        setWordWatcher(binding.etMnemonic1, binding.etMnemonic2)
        setWordWatcher(binding.etMnemonic2, binding.etMnemonic3)
        setWordWatcher(binding.etMnemonic3, binding.etMnemonic4)
        setWordWatcher(binding.etMnemonic4, binding.etMnemonic5)
        setWordWatcher(binding.etMnemonic5, binding.etMnemonic6)
        setWordWatcher(binding.etMnemonic6, binding.etMnemonic7)
        setWordWatcher(binding.etMnemonic7, binding.etMnemonic8)
        setWordWatcher(binding.etMnemonic8, binding.etMnemonic9)
        setWordWatcher(binding.etMnemonic9, binding.etMnemonic10)
        setWordWatcher(binding.etMnemonic10, binding.etMnemonic11)
        setWordWatcher(binding.etMnemonic11, binding.etMnemonic12)
        setWordWatcher(binding.etMnemonic12, null)
    }

    private fun setWordWatcher(src: EditText, dst: EditText?) {

        src.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // 获取所有助记词
                val mnemonic1: String = binding.etMnemonic1.text.toString().trim()
                val mnemonic2: String = binding.etMnemonic2.text.toString().trim()
                val mnemonic3: String = binding.etMnemonic3.text.toString().trim()
                val mnemonic4: String = binding.etMnemonic4.text.toString().trim()
                val mnemonic5: String = binding.etMnemonic5.text.toString().trim()
                val mnemonic6: String = binding.etMnemonic6.text.toString().trim()
                val mnemonic7: String = binding.etMnemonic7.text.toString().trim()
                val mnemonic8: String = binding.etMnemonic8.text.toString().trim()
                val mnemonic9: String = binding.etMnemonic9.text.toString().trim()
                val mnemonic10: String = binding.etMnemonic10.text.toString().trim()
                val mnemonic11: String = binding.etMnemonic11.text.toString().trim()
                val mnemonic12: String = binding.etMnemonic12.text.toString().trim()

                // 判断所有的助记词是否为空
                if (TextUtils.isEmpty(mnemonic1) || TextUtils.isEmpty(mnemonic2) || TextUtils.isEmpty(
                        mnemonic3
                    ) || TextUtils.isEmpty(mnemonic4)
                    || TextUtils.isEmpty(mnemonic5) || TextUtils.isEmpty(mnemonic6) || TextUtils.isEmpty(
                        mnemonic7
                    ) || TextUtils.isEmpty(mnemonic8)
                    || TextUtils.isEmpty(mnemonic9) || TextUtils.isEmpty(mnemonic10) || TextUtils.isEmpty(
                        mnemonic11
                    ) || TextUtils.isEmpty(mnemonic12)
                ) {
                    showMnemonicPhraseError(
                        ResourceUtil.GetString(R.string.validMnenonicEmptyTips),
                        true
                    )
                    return
                }
                if (TextUtils.isEmpty(mnemonic1) && TextUtils.isEmpty(mnemonic2) && TextUtils.isEmpty(
                        mnemonic3
                    ) && TextUtils.isEmpty(mnemonic4)
                    && TextUtils.isEmpty(mnemonic5) && TextUtils.isEmpty(mnemonic6) && TextUtils.isEmpty(
                        mnemonic7
                    ) && TextUtils.isEmpty(mnemonic8)
                    && TextUtils.isEmpty(mnemonic9) && TextUtils.isEmpty(mnemonic10) && TextUtils.isEmpty(
                        mnemonic11
                    ) && TextUtils.isEmpty(mnemonic12)
                ) {
                    showMnemonicPhraseError(
                        ResourceUtil.GetString(R.string.validMnenonicEmptyTips),
                        true
                    )
                    return
                }
                if (!TextUtils.isEmpty(mnemonic1) && !TextUtils.isEmpty(mnemonic2) && !TextUtils.isEmpty(
                        mnemonic3
                    ) && !TextUtils.isEmpty(mnemonic4)
                    && !TextUtils.isEmpty(mnemonic5) && !TextUtils.isEmpty(mnemonic6) && !TextUtils.isEmpty(
                        mnemonic7
                    ) && !TextUtils.isEmpty(mnemonic8)
                    && !TextUtils.isEmpty(mnemonic9) && !TextUtils.isEmpty(mnemonic10) && !TextUtils.isEmpty(
                        mnemonic11
                    ) && !TextUtils.isEmpty(mnemonic12)
                ) {
                    showMnemonicPhraseError(
                        ResourceUtil.GetString(R.string.validMnenonicEmptyTips),
                        false
                    )
                    return
                }
            }

            override fun afterTextChanged(s: Editable) {
                val text = s.toString()
                if (text.contains(" ")) {
                    src.setText(text.replace(" ", ""))
                    if (dst != null) {
                        dst.requestFocus()
                        dst.setSelection(dst.text.length)
                    }
                }
            }
        })
    }


    fun showNameError(text: String?, isVisible: Boolean) {
        binding.tvNameError.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.tvNameError.text = text
        this.isEnableName = isVisible
        enableImport(!isEnableName && !isEnablePassword && !isMnemonicPhrase )
    }


    fun showMnemonicPhraseError(text: String?, isVisible: Boolean) {
        binding.tvMnemonicPhraseError.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.tvMnemonicPhraseError.text = text
        isMnemonicPhrase = isVisible
        enableImport(!isEnableName && !isEnablePassword && !isMnemonicPhrase)
    }

    private fun checkPwdStreng(password: String) {

        binding.layoutPasswordStrength.setVisibility(if (TextUtils.isEmpty(password)) View.GONE else View.VISIBLE)
        if (TextUtils.isEmpty(password)) {

            binding.tvStrength.setText(R.string.strength)
            binding.vLine1.setBackgroundColor(ResourceUtil.GetColor(R.color.color_00000000))
            binding.vLine2.setBackgroundColor(ResourceUtil.GetColor(R.color.color_00000000))
            binding.vLine3.setBackgroundColor(ResourceUtil.GetColor(R.color.color_00000000))
            binding.vLine4.setBackgroundColor(ResourceUtil.GetColor(R.color.color_00000000))
            return
        }
        when (CheckStrength.getPasswordLevelNew(password)) {
            CheckStrength.LEVEL.EASY -> {
                binding.tvStrength.setTextColor(ResourceUtil.GetColor(R.color.color_f5302c))
                binding.tvStrength.setText(R.string.weak)
                binding.vLine1.setBackgroundColor(
                    ResourceUtil.GetColor(
                        R.color.color_f5302c
                    )
                )
                binding.vLine2.setBackgroundColor(
                    ResourceUtil.GetColor(
                        R.color.color_00000000
                    )
                )
                binding.vLine3.setBackgroundColor(
                    ResourceUtil.GetColor(
                        R.color.color_00000000
                    )
                )
                binding.vLine4.setBackgroundColor(
                    ResourceUtil.GetColor(
                        R.color.color_00000000
                    )
                )
            }
            CheckStrength.LEVEL.MIDIUM -> {
                binding.tvStrength.setTextColor(ResourceUtil.GetColor(R.color.color_ff9000))
                binding.tvStrength.setText(R.string.so_so)
                binding.vLine1.setBackgroundColor(
                    ResourceUtil.GetColor(
                        R.color.color_ff9000
                    )
                )
                binding.vLine2.setBackgroundColor(
                    ResourceUtil.GetColor(
                        R.color.color_ff9000
                    )
                )
                binding.vLine3.setBackgroundColor(
                    ResourceUtil.GetColor(
                        R.color.color_00000000
                    )
                )
                binding.vLine4.setBackgroundColor(
                    ResourceUtil.GetColor(
                        R.color.color_00000000
                    )
                )
            }
            CheckStrength.LEVEL.STRONG -> {
                binding.tvStrength.setTextColor(ResourceUtil.GetColor(R.color.color_58b8ff))
                binding.tvStrength.setText(R.string.good)
                binding.vLine1.setBackgroundColor(
                    ResourceUtil.GetColor(
                        R.color.color_58b8ff
                    )
                )
                binding.vLine2.setBackgroundColor(
                    ResourceUtil.GetColor(
                        R.color.color_58b8ff
                    )
                )
                binding.vLine3.setBackgroundColor(
                    ResourceUtil.GetColor(
                        R.color.color_58b8ff
                    )
                )
                binding.vLine4.setBackgroundColor(
                    ResourceUtil.GetColor(
                        R.color.color_00000000
                    )
                )
            }
            CheckStrength.LEVEL.VERY_STRONG, CheckStrength.LEVEL.EXTREMELY_STRONG -> {
                binding.tvStrength.setTextColor(ResourceUtil.GetColor(R.color.color_19a20e))
                binding.tvStrength.setText(R.string.strong)
                binding.vLine1.setBackgroundColor(
                    ResourceUtil.GetColor(
                        R.color.color_19a20e
                    )
                )
                binding.vLine2.setBackgroundColor(
                    ResourceUtil.GetColor(
                        R.color.color_19a20e
                    )
                )
                binding.vLine3.setBackgroundColor(
                    ResourceUtil.GetColor(
                        R.color.color_19a20e
                    )
                )
                binding.vLine4.setBackgroundColor(
                    ResourceUtil.GetColor(
                        R.color.color_19a20e
                    )
                )
            }
            else -> {
            }
        }
    }

    /**
     * 显示密码
     */
    private fun showPassword() {
        if (mShowPassword) {
            // 显示密码
            binding.etPassword.transformationMethod =
                HideReturnsTransformationMethod.getInstance()
            binding.etPassword.setSelection(binding.etPassword.text.toString().length)
            binding.ivPasswordEyes.setImageResource(R.mipmap.icon_open_eyes)
        } else {
            // 隐藏密码
            binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.etPassword.setSelection(binding.etPassword.text.toString().length)
            binding.ivPasswordEyes.setImageResource(R.mipmap.icon_close_eyes)
        }
        mShowPassword = !mShowPassword
    }

    /**
     * 显示重复密码
     */
    private fun showRepeatPassword() {
        if (mShowRepeatPassword) {
            // 显示密码
            binding.etRepeatPassword.transformationMethod =
                HideReturnsTransformationMethod.getInstance()
            binding.etRepeatPassword.setSelection(binding.etRepeatPassword.text.toString().length)
            binding.ivRepeatPasswordEyes.setImageResource(R.mipmap.icon_open_eyes)
        } else {
            // 隐藏密码
            binding.etRepeatPassword.transformationMethod =
                PasswordTransformationMethod.getInstance()
            binding.etRepeatPassword.setSelection(binding.etRepeatPassword.text.toString().length)
            binding.ivRepeatPasswordEyes.setImageResource(R.mipmap.icon_close_eyes)
        }
        mShowRepeatPassword = !mShowRepeatPassword
    }

    fun showPasswordError(text: String?, isVisible: Boolean) {
        binding.tvPasswordError.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.tvPasswordError.text = text
        binding.tvPasswordDesc.visibility = if (isVisible) View.GONE else View.VISIBLE
        isEnablePassword = isVisible
        enableImport(!isEnableName && !isEnablePassword && !isMnemonicPhrase)
    }


    private fun enableImport(enabled: Boolean) {
        binding.sbtnImport.isEnabled = enabled
    }

}