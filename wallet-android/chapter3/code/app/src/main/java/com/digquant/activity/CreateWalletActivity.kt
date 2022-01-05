package com.digquant.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.digquant.R
import com.digquant.databinding.ActivityCreateWalletBinding
import com.digquant.service.WalletManager
import com.digquant.util.*

class CreateWalletActivity : AppCompatActivity() {

    private lateinit var bindding: ActivityCreateWalletBinding


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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ViewUtil.SetWindow(window)
        /**
         * 设置状态栏的颜色为黑色
         */
        ViewUtil.SetStatusBarColorToBlack(window)
        val inflater = LayoutInflater.from(this)
        bindding = ActivityCreateWalletBinding.inflate(inflater, null, false)

        setContentView(bindding.root)

        initUI()
        initEvent()
    }

    /**
     * 初始化UI
     */
    private fun initUI() {
        enableCreate(false)
        showPassword()
        showRepeatPassword()
    }

    /**
     * 初始化事件
     */
    private fun initEvent() {


        /**
         * 返回Activity
         */
        bindding.backBtn.setOnClickListener {
            this.finish();
        }

        /**
         *  创建钱包
         */
        bindding.sbtnCreate.setOnClickListener {
            createWallet()
        }
        /**
         * 显示密码
         */
        bindding.ivPasswordEyes.setOnClickListener {
            showPassword()
        }
        /**
         * 显示重复密码
         */
        bindding.ivRepeatPasswordEyes.setOnClickListener {
            showRepeatPassword()
        }

        /**
         * 检测钱包名称
         */
        bindding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                /**
                 * 判断钱包名是否符合规则，字符不能超过20个
                 */
                val name = bindding.etName.text.toString().trim()
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
         * 检测密码规范
         */
        bindding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = bindding.etPassword.text.toString().trim()
                val repeatPassword = bindding.etRepeatPassword.text.toString().trim()
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
        bindding.etRepeatPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = bindding.etPassword.text.toString().trim()
                val repeatPassword = bindding.etRepeatPassword.text.toString().trim()

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
                val password: String = bindding.etPassword.getText().toString().trim { it <= ' ' }
                checkPwdStrength(password)
            }
        })
    }

    /**
     * 创建钱包
     */
    private fun createWallet() {
        val name = bindding.etName.text.toString().trim()
        val password = bindding.etPassword.text.toString().trim()
        val repeatPassword = bindding.etRepeatPassword.text.toString().trim()

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
        /**
         * 去钱包管理类创建Session
         */
        val isSucceed = WalletManager.BuildCreateWalletSession(name, password)

        if(!isSucceed) {
            ToastUtil.showLongToast(this, "创建钱包助记词失败")
        }

        DXRouter.JumpTo(this, BackupMnemonicPhraseActivity::class.java)
    }

    /**
     * 检测密码强度
     */
    private fun checkPwdStrength(password: String) {

        bindding.passwordStrength.visibility =
            if (TextUtils.isEmpty(password)) View.GONE else View.VISIBLE

        if (TextUtils.isEmpty(password)) {
            bindding.tvStrength.text = ResourceUtil.GetString(R.string.strength)

            bindding.vLine1.setBackgroundColor(ResourceUtil.GetColor(R.color.color_00000000))
            bindding.vLine2.setBackgroundColor(ResourceUtil.GetColor(R.color.color_00000000))
            bindding.vLine3.setBackgroundColor(ResourceUtil.GetColor(R.color.color_00000000))
            bindding.vLine4.setBackgroundColor(ResourceUtil.GetColor(R.color.color_00000000))
            return
        }

        when (CheckStrength.getPasswordLevelNew(password)) {
            CheckStrength.LEVEL.EASY -> {
                bindding.tvStrength.setTextColor(ResourceUtil.GetColor(R.color.color_f5302c))
                bindding.tvStrength.setText(R.string.weak)
                bindding.vLine1.setBackgroundColor(
                    ResourceUtil.GetColor(R.color.color_f5302c)
                )
                bindding.vLine2.setBackgroundColor(
                    ResourceUtil.GetColor(R.color.color_00000000)
                )
                bindding.vLine3.setBackgroundColor(
                    ResourceUtil.GetColor(R.color.color_00000000)
                )
                bindding.vLine4.setBackgroundColor(
                    ResourceUtil.GetColor(R.color.color_00000000)
                )
            }
            CheckStrength.LEVEL.MIDIUM -> {
                bindding.tvStrength.setTextColor(
                    ResourceUtil.GetColor(R.color.color_ff9000)
                )
                bindding.tvStrength.setText(R.string.so_so)
                bindding.vLine1.setBackgroundColor(
                    ResourceUtil.GetColor(R.color.color_ff9000)
                )
                bindding.vLine2.setBackgroundColor(
                    ResourceUtil.GetColor(R.color.color_ff9000)
                )
                bindding.vLine3.setBackgroundColor(
                    ResourceUtil.GetColor(R.color.color_00000000)
                )
                bindding.vLine4.setBackgroundColor(
                    ResourceUtil.GetColor(R.color.color_00000000)
                )
            }
            CheckStrength.LEVEL.STRONG -> {
                bindding.tvStrength.setTextColor(
                    ResourceUtil.GetColor(R.color.color_58b8ff)
                )
                bindding.tvStrength.setText(R.string.good)
                bindding.vLine1.setBackgroundColor(
                    ResourceUtil.GetColor(R.color.color_58b8ff)
                )
                bindding.vLine2.setBackgroundColor(
                    ResourceUtil.GetColor(R.color.color_58b8ff)
                )
                bindding.vLine3.setBackgroundColor(
                    ResourceUtil.GetColor(R.color.color_58b8ff)
                )
                bindding.vLine4.setBackgroundColor(
                    ResourceUtil.GetColor(R.color.color_00000000)
                )
            }
            CheckStrength.LEVEL.VERY_STRONG, CheckStrength.LEVEL.EXTREMELY_STRONG -> {
                bindding.tvStrength.setTextColor(
                    ResourceUtil.GetColor(R.color.color_19a20e)
                )
                bindding.tvStrength.setText(R.string.strong)
                bindding.vLine1.setBackgroundColor(
                    ResourceUtil.GetColor(R.color.color_19a20e)
                )
                bindding.vLine2.setBackgroundColor(
                    ResourceUtil.GetColor(R.color.color_19a20e)
                )
                bindding.vLine3.setBackgroundColor(
                    ResourceUtil.GetColor(R.color.color_19a20e)
                )
                bindding.vLine4.setBackgroundColor(
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
            bindding.etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()

            bindding.etPassword.setSelection(bindding.etPassword.text.toString().length)
            bindding.ivPasswordEyes.setImageResource(R.mipmap.icon_open_eyes)

        } else {
            // 隐藏密码
            bindding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            bindding.etPassword.setSelection(bindding.etPassword.text.toString().length)
            bindding.ivPasswordEyes.setImageResource(R.mipmap.icon_close_eyes)

        }
        mShowPassword = !mShowPassword;
    }

    /**
     * 显示重复密码
     */
    private fun showRepeatPassword() {
        if (mShowRepeatPassword) {
            // 显示密码
            bindding.etRepeatPassword.transformationMethod =
                HideReturnsTransformationMethod.getInstance()
            bindding.etRepeatPassword.setSelection(bindding.etRepeatPassword.text.toString().length)

            bindding.ivRepeatPasswordEyes.setImageResource(R.mipmap.icon_open_eyes)
            !mShowRepeatPassword
        } else {
            // 隐藏密码
            bindding.etRepeatPassword.transformationMethod =
                PasswordTransformationMethod.getInstance()
            bindding.etRepeatPassword.setSelection(bindding.etRepeatPassword.text.toString().length)
            bindding.ivRepeatPasswordEyes.setImageResource(R.mipmap.icon_close_eyes)
            !mShowRepeatPassword
        }
        mShowRepeatPassword = !mShowRepeatPassword;
    }

    /**
     * 显示名字的错误信息
     */
    private fun showNameError(text: String?, isVisible: Boolean) {
        bindding.tvNameError.visibility = if (isVisible) View.VISIBLE else View.GONE
        bindding.tvNameError.text = text
        this.isEnableName = isVisible
        enableCreate(!isEnablePassword && !isEnableName)
    }

    /**
     * 显示面错误的信息
     */
    fun showPasswordError(text: String, isVisible: Boolean) {
        bindding.tvPasswordError.visibility = if (isVisible) View.VISIBLE else View.GONE
        bindding.tvPasswordError.text = text
        bindding.tvPasswordDesc.visibility = if (isVisible) View.GONE else View.VISIBLE
        isEnablePassword = isVisible
        enableCreate(!isEnablePassword && !isEnableName)
    }

    /**
     *
     */
    private fun enableCreate(enabled: Boolean) {
        bindding.sbtnCreate.isEnabled = enabled
    }
}