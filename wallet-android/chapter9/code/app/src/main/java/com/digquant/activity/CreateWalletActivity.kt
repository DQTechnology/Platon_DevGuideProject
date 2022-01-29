package com.digquant.activity

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import com.digquant.R
import com.digquant.databinding.ActivityCreateWalletBinding
import com.digquant.service.WalletManager
import com.digquant.util.*

class CreateWalletActivity : BaseActivity() {
    private lateinit var binding: ActivityCreateWalletBinding

    /**
     * 钱包名是否符合规范
     */
    private var isEnableName = true

    /**
     * 密码是是否符合要求
     */
    private var isEnablePassword = true

    /**
     * 是否显示面
     */
    private var mShowPassword = false

    /**
     * 是否显示重复密码
     */
    private var mShowRepeatPassword = false

    /**
     * 构建UI
     */
    override fun inflateUI(inflater: LayoutInflater): View {
        binding = ActivityCreateWalletBinding.inflate(inflater, null, false)
        return binding.root
    }

    /**
     * 初始化UI
     */
    override fun initUI() {
        enableCreate(false)
        showPassword()
        showRepeatPassword()
    }

    /**
     * 初始化事件
     */
    override fun initEvent() {


        /**
         * 返回Activity
         */
        binding.backBtn.setOnClickListener {
            this.finish();
        }

        /**
         *  创建钱包
         */
        binding.sbtnCreate.setOnClickListener {
            createWallet()
        }
        /**
         * 显示密码
         */
        binding.ivPasswordEyes.setOnClickListener {
            showPassword()
        }
        /**
         * 显示重复密码
         */
        binding.ivRepeatPasswordEyes.setOnClickListener {
            showRepeatPassword()
        }

        /**
         * 检测钱包名称
         */
        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                /**
                 * 判断钱包名是否符合规则，字符不能超过20个
                 */
                val name = binding.etName.text.toString().trim()
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
         * 检测密码规范
         */
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = binding.etPassword.text.toString().trim()
                val repeatPassword = binding.etRepeatPassword.text.toString().trim()
                when {
                    /**
                     * 判断密码是否为空
                     */
                    TextUtils.isEmpty(password) -> {
                        showPasswordError(
                            ResourceUtil.GetString(R.string.validPasswordEmptyTips),
                            true
                        )
                    }
                    /**
                     * 判断密码强度
                     */
                    password.length < 6 -> {
                        showPasswordError(
                            ResourceUtil.GetString(R.string.validPasswordEmptyTips),
                            true
                        )
                    }
                    /**
                     * 判断重复密码是否相等
                     */
                    repeatPassword != password -> {
                        showPasswordError(ResourceUtil.GetString(R.string.passwordTips), true)
                    }
                    else -> {
                        if (password == repeatPassword) {
                            showPasswordError("", false)
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        /**
         * 监听重复密码
         */
        binding.etRepeatPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = binding.etPassword.text.toString().trim()
                val repeatPassword = binding.etRepeatPassword.text.toString().trim()

                when {
                    /**
                     * 判断密码是否为空
                     */
                    TextUtils.isEmpty(repeatPassword) -> {
                        showPasswordError(
                            ResourceUtil.GetString(R.string.validRepeatPasswordEmptyTips),
                            true
                        )
                    }

                    /**
                     * 判断重复密码是否相等
                     */
                    repeatPassword != password -> {
                        showPasswordError(ResourceUtil.GetString(R.string.passwordTips), true)
                    }
                    else -> {
                        if (password == repeatPassword) {
                            showPasswordError("", false)
                        }
                    }
                }

            }

            override fun afterTextChanged(s: Editable?) {
                val password: String = binding.etPassword.getText().toString().trim { it <= ' ' }
                checkPwdStrength(password)
            }
        })
    }

    /**
     * 创建钱包
     */
    private fun createWallet() {
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
            return
        }

        // 判断钱包后是否已经存在

        if (WalletManager.IsWalletExist(name)) {
            ToastUtil.showLongToast(this, ResourceUtil.GetString(R.string.validWalletNameDuplicateTips))
            return
        }

        /**
         * 去钱包管理类创建Session
         */
        val isSucceed = WalletManager.BuildCreateWalletSession(name, password)

        if (!isSucceed) {
            ToastUtil.showLongToast(this, "创建钱包助记词失败")
        }

        DXRouter.JumpTo(this, BackupMnemonicPhraseActivity::class.java)
    }

    /**
     * 检测密码强度
     */
    private fun checkPwdStrength(password: String) {

        binding.passwordStrength.visibility =
            if (TextUtils.isEmpty(password)) View.GONE else View.VISIBLE

        if (TextUtils.isEmpty(password)) {
            binding.tvStrength.text = ResourceUtil.GetString(R.string.strength)

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
                    ResourceUtil.GetColor(R.color.color_f5302c)
                )
                binding.vLine2.setBackgroundColor(
                    ResourceUtil.GetColor(R.color.color_00000000)
                )
                binding.vLine3.setBackgroundColor(
                    ResourceUtil.GetColor(R.color.color_00000000)
                )
                binding.vLine4.setBackgroundColor(
                    ResourceUtil.GetColor(R.color.color_00000000)
                )
            }
            CheckStrength.LEVEL.MIDIUM -> {
                binding.tvStrength.setTextColor(
                    ResourceUtil.GetColor(R.color.color_ff9000)
                )
                binding.tvStrength.setText(R.string.so_so)
                binding.vLine1.setBackgroundColor(
                    ResourceUtil.GetColor(R.color.color_ff9000)
                )
                binding.vLine2.setBackgroundColor(
                    ResourceUtil.GetColor(R.color.color_ff9000)
                )
                binding.vLine3.setBackgroundColor(
                    ResourceUtil.GetColor(R.color.color_00000000)
                )
                binding.vLine4.setBackgroundColor(
                    ResourceUtil.GetColor(R.color.color_00000000)
                )
            }
            CheckStrength.LEVEL.STRONG -> {
                binding.tvStrength.setTextColor(
                    ResourceUtil.GetColor(R.color.color_58b8ff)
                )
                binding.tvStrength.setText(R.string.good)
                binding.vLine1.setBackgroundColor(
                    ResourceUtil.GetColor(R.color.color_58b8ff)
                )
                binding.vLine2.setBackgroundColor(
                    ResourceUtil.GetColor(R.color.color_58b8ff)
                )
                binding.vLine3.setBackgroundColor(
                    ResourceUtil.GetColor(R.color.color_58b8ff)
                )
                binding.vLine4.setBackgroundColor(
                    ResourceUtil.GetColor(R.color.color_00000000)
                )
            }
            CheckStrength.LEVEL.VERY_STRONG, CheckStrength.LEVEL.EXTREMELY_STRONG -> {
                binding.tvStrength.setTextColor(
                    ResourceUtil.GetColor(R.color.color_19a20e)
                )
                binding.tvStrength.setText(R.string.strong)
                binding.vLine1.setBackgroundColor(
                    ResourceUtil.GetColor(R.color.color_19a20e)
                )
                binding.vLine2.setBackgroundColor(
                    ResourceUtil.GetColor(R.color.color_19a20e)
                )
                binding.vLine3.setBackgroundColor(
                    ResourceUtil.GetColor(R.color.color_19a20e)
                )
                binding.vLine4.setBackgroundColor(
                    ResourceUtil.GetColor(R.color.color_19a20e)
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
            binding.etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()

            binding.etPassword.setSelection(binding.etPassword.text.toString().length)
            binding.ivPasswordEyes.setImageResource(R.mipmap.icon_open_eyes)

        } else {
            // 隐藏密码
            binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.etPassword.setSelection(binding.etPassword.text.toString().length)
            binding.ivPasswordEyes.setImageResource(R.mipmap.icon_close_eyes)

        }
        mShowPassword = !mShowPassword;
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
            !mShowRepeatPassword
        } else {
            // 隐藏密码
            binding.etRepeatPassword.transformationMethod =
                PasswordTransformationMethod.getInstance()
            binding.etRepeatPassword.setSelection(binding.etRepeatPassword.text.toString().length)
            binding.ivRepeatPasswordEyes.setImageResource(R.mipmap.icon_close_eyes)
            !mShowRepeatPassword
        }
        mShowRepeatPassword = !mShowRepeatPassword;
    }

    /**
     * 显示名字的错误信息
     */
    private fun showNameError(text: String?, isVisible: Boolean) {
        binding.tvNameError.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.tvNameError.text = text
        this.isEnableName = isVisible
        enableCreate(!isEnablePassword && !isEnableName)
    }

    /**
     * 显示面错误的信息
     */
    fun showPasswordError(text: String, isVisible: Boolean) {
        binding.tvPasswordError.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.tvPasswordError.text = text
        binding.tvPasswordDesc.visibility = if (isVisible) View.GONE else View.VISIBLE
        isEnablePassword = isVisible
        enableCreate(!isEnablePassword && !isEnableName)
    }

    /**
     *
     */
    private fun enableCreate(enabled: Boolean) {
        binding.sbtnCreate.isEnabled = enabled
    }
}