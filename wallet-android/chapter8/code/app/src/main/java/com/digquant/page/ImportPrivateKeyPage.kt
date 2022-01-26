package com.digquant.page

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import com.digquant.R
import com.digquant.activity.MainActivity
import com.digquant.adapter.BaseViewHolder

import com.digquant.databinding.PageImportPrivateKeyBinding
import com.digquant.entity.ImportWalletSuccessEvent
import com.digquant.service.WalletManager
import com.digquant.util.*
import org.greenrobot.eventbus.EventBus

class ImportPrivateKeyPage(itemView: View) : BaseViewHolder(itemView) {

    private val binding: PageImportPrivateKeyBinding =
        PageImportPrivateKeyBinding.bind(itemView)

    private var mShowPassword = false
    private var mShowRepeatPassword = false

    private var isEnableName = true
    private var isEnablePassword = true
    private var isEnablePrivateKey = true

    init {
        initDatas()
        initEvent()
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
            importPrivateKey()
        }
        /**
         * 粘贴秘钥的
         */
        binding.btnPaste.setOnClickListener {

            binding.etPrivateKey.setText(CommonUtil.getTextFromClipboard(itemView.context))

            binding.etPrivateKey.setSelection(binding.etPrivateKey.text.toString().length)
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
                    WalletManager.IsWalletExist(name) -> {
                        showNameError(
                            ResourceUtil.GetString(R.string.validWalletNameDuplicateTips),
                            true
                        )
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

        binding.etPrivateKey.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val privateKey: String = binding.etPrivateKey.text.toString().trim()
                if (TextUtils.isEmpty(privateKey)) {
                    showPrivateKeyError(
                        ResourceUtil.GetString(R.string.validPrivateKeyEmptyTips),
                        true
                    )
                } else {
                    showPrivateKeyError("", false)
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

    }

    private fun importPrivateKey() {

        val name = binding.etName.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val repeatPassword = binding.etRepeatPassword.text.toString().trim()

        val privateKey = binding.etPrivateKey.text.toString().trim()
        /**
         * 检测名字长度
         */
        if (name.length > 20) {
            showNameError(ResourceUtil.GetString(R.string.validWalletNameTips), true)
            return
        }

        if (WalletManager.IsWalletExist(name)) {
            showNameError(ResourceUtil.GetString(R.string.validWalletNameDuplicateTips), true)
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


        /**
         * 判断秘钥是否为空
         */
        if (TextUtils.isEmpty(privateKey)) {
            showPrivateKeyError(ResourceUtil.GetString(R.string.validPrivateKeyEmptyTips), true)
            return
        }

        val isSucceed = WalletManager.ImportPrivateKey(name, password, privateKey)

        if (!isSucceed) {
            ToastUtil.showLongToast(itemView.context, "导入秘钥失败")
        } else {
            ToastUtil.showLongToast(itemView.context, "导入成功!")
            EventBus.getDefault().post(ImportWalletSuccessEvent())
        }

    }

    fun showNameError(text: String?, isVisible: Boolean) {

        binding.tvNameError.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.tvNameError.text = text
        this.isEnableName = isVisible
        enableImport(!isEnableName && !isEnablePassword && !isEnablePrivateKey)
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

    fun showPrivateKeyError(text: String?, isVisible: Boolean) {
        binding.tvPrivateKeyError.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.tvPrivateKeyError.text = text
        isEnablePrivateKey = isVisible
        enableImport(!isEnableName && !isEnablePassword && isEnablePrivateKey)
    }

    fun showPasswordError(text: String?, isVisible: Boolean) {
        binding.tvPasswordError.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.tvPasswordError.text = text
        binding.tvPasswordDesc.visibility = if (isVisible) View.GONE else View.VISIBLE
        isEnablePassword = isVisible
        enableImport(!isEnableName && !isEnablePassword && !isEnablePrivateKey)
    }

    private fun initDatas() {
        enableImport(false)
    }

    private fun enableImport(enabled: Boolean) {
        binding.sbtnImport.isEnabled = enabled
    }
}