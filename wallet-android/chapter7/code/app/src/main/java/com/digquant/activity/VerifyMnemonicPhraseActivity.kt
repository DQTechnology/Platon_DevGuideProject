package com.digquant.activity

import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import com.digquant.R
import com.digquant.databinding.ActivityVerifyMnemonicPhraseBinding
import com.digquant.service.WalletManager
import com.digquant.util.*
import com.google.android.flexbox.FlexboxLayout
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bitcoinj.crypto.*


class VerifyMnemonicPhraseActivity : BaseActivity() {

    private lateinit var binding: ActivityVerifyMnemonicPhraseBinding

    /**
     * 存储单词信息
     */
    data class WordInfo(
        var checked: Boolean,
        var mnemonic: String, // 助记词
        val waitSelectIndex: Int, // 在待选单词列表的索引
        var showWordIndex: Int // 选中单词的索引
    )

    /**
     * 待选单词的索引映射
     */
    private var waitSelectWordInfoMap = HashMap<Int, WordInfo>()

    /**
     * 选中单词索引映射
     */
    private var selectWordInfoMap = HashMap<Int, WordInfo>()

    /**
     * 显示选中textview的单词列表
     */
    private val showTVMnemonicList = ArrayList<TextView>()

    /**
     * 显示选中textview的单词列表
     */
    private val waitSelectTVMnemonicList = ArrayList<TextView>()

    /**
     * 正确顺序的助记词
     */
    private lateinit var originMnemonicWordList: List<String>

    private var curEmptyIndex = 0


    override fun inflateUI(inflater: LayoutInflater): View {
        binding = ActivityVerifyMnemonicPhraseBinding.inflate(inflater, null, false)
        return binding.root
    }

    /**
     *
     */
    override fun initUI() {
        /**
         * 获取创建钱包的信息
         */
        val createWalletInfo = WalletManager.GetCreateWalletSession()

        /**
         * 获取助记词,如果助记词没有则1s关闭页面
         */
        val mnemonicWords = createWalletInfo?.mnemonicWords

        if (mnemonicWords == null) {
            ToastUtil.showLongToast(this, "助记词为空")
            this.finish()
            lifecycleScope.launch {
                /**
                 * 1s后关闭页面
                 */
                delay(1000)
                this@VerifyMnemonicPhraseActivity.finish()
            }
            return
        }
        /**
         * 保存正确顺序的助记词
         */
        originMnemonicWordList = mnemonicWords
        /**
         * 打乱助记词的顺序
         */
        val shuffledMnemonicWords = mnemonicWords.shuffled()
        /**
         * 显示待选助记词
         */
        showMnemonicWords(shuffledMnemonicWords)
        /**
         * 把显示选中助记词的ui对象存储成列表, 这样可以通过索引直接获取对应的UI对象
         */
        showTVMnemonicList.add(binding.tvMnemonic1)
        showTVMnemonicList.add(binding.tvMnemonic2)
        showTVMnemonicList.add(binding.tvMnemonic3)
        showTVMnemonicList.add(binding.tvMnemonic4)
        showTVMnemonicList.add(binding.tvMnemonic5)
        showTVMnemonicList.add(binding.tvMnemonic6)
        showTVMnemonicList.add(binding.tvMnemonic7)
        showTVMnemonicList.add(binding.tvMnemonic8)
        showTVMnemonicList.add(binding.tvMnemonic9)
        showTVMnemonicList.add(binding.tvMnemonic10)
        showTVMnemonicList.add(binding.tvMnemonic11)
        showTVMnemonicList.add(binding.tvMnemonic12)
    }

    /**
     * 打算的助记词显示的待选单词列表
     */
    private fun showMnemonicWords(shuffledMnemonicWords: List<String>) {
        binding.flAll.removeAllViews()
        shuffledMnemonicWords.forEachIndexed { index, s ->
            /**
             * 构建助记词信息对象
             * 因为单词没有被选中,因此序选中索引都设置为-1
             */
            val dataEntity = WordInfo(false, s, index, -1)
            /**
             * 构建好的单词信息全部存到待选表
             */
            waitSelectWordInfoMap[index] = dataEntity

            binding.flAll.addView(createAllItemView(dataEntity))
        }
    }

    /**
     * 创建待续单词的UI元素
     */
    private fun createAllItemView(
        dataEntity: WordInfo
    ): TextView? {
        val textView = TextView(this)
        textView.text = dataEntity.mnemonic
        textView.gravity = Gravity.CENTER
        textView.isAllCaps = false

        waitSelectTVMnemonicList.add(textView)

        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)

        textView.setBackgroundResource(R.drawable.bg_shape_verify_mnemonic_n)
        textView.setTextColor(ResourceUtil.GetColor(R.color.color_316def))

        val paddingLeftAndRight: Int = DensityUtil.DP2PX(this, 12f)
        val paddingTopAndBottom = 0
        ViewCompat.setPaddingRelative(
            textView,
            paddingLeftAndRight,
            paddingTopAndBottom,
            paddingLeftAndRight,
            paddingTopAndBottom
        )
        val layoutParams = FlexboxLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            DensityUtil.DP2PX(this, 38F)
        )
        val marginRight: Int = DensityUtil.DP2PX(this, 10F)
        val marginTop: Int = DensityUtil.DP2PX(this, 12F)
        layoutParams.setMargins(0, marginTop, marginRight, 0)
        textView.layoutParams = layoutParams
        return textView
    }

    /**
     * 处理单词的点击事件
     */
    private fun selectWord(index: Int, isWaitSelect: Boolean) {
        /**
         * 通过isWaitSelect判断,index获取单词信息对象是从待选对象获取,还是已选对象获取
         */
        val wordInfo: WordInfo? = if (isWaitSelect) {
            waitSelectWordInfoMap[index]
        } else {
            selectWordInfoMap[index]
        }

        if (wordInfo == null) {
            return
        }

        if (isWaitSelect) {
            /**
             * 如果点击的是待选单词列表,那每次都取反
             */
            wordInfo.checked = !wordInfo.checked
        } else {
            if (wordInfo.showWordIndex == -1) {
                return
            }
            /**
             * 如果点击的是已选单词列表,则每次则认为是取消选中
             */
            wordInfo.checked = false
        }
        /**
         * 获取待选单词的UI
         */
        val waitSelectMnemonicTV = waitSelectTVMnemonicList[wordInfo.waitSelectIndex]

        if (!wordInfo.checked) {
            // 取消选中词
            waitSelectMnemonicTV.setBackgroundResource(R.drawable.bg_shape_verify_mnemonic_n)
            waitSelectMnemonicTV.setTextColor(ResourceUtil.GetColor(R.color.color_316def))
            selectWordInfoMap.remove(wordInfo.showWordIndex)
            val showMnemonicTV = showTVMnemonicList[wordInfo.showWordIndex]
            showMnemonicTV.text = ""

            /**
             * 如果取消的位置索引比当前的空位置的索引小,则更新
             */
            if (wordInfo.showWordIndex < curEmptyIndex) {
                curEmptyIndex = wordInfo.showWordIndex
            }

            wordInfo.showWordIndex = -1
            binding.sbtnSubmit.isEnabled = false
        } else {
            // 选中单词
            waitSelectMnemonicTV.setBackgroundResource(R.drawable.bg_shape_verify_mnemonic_h)
            waitSelectMnemonicTV.setTextColor(ResourceUtil.GetColor(R.color.color_b6bbd0))


            wordInfo.showWordIndex = curEmptyIndex

            selectWordInfoMap[wordInfo.showWordIndex] = wordInfo

            /**
             * 寻找下一个为空的位置
             */
            findNextEmptyIndex()

            val showMnemonicTV = showTVMnemonicList[wordInfo.showWordIndex]

            showMnemonicTV.text = wordInfo.mnemonic

            // 选完单词后,创建按钮设置为可用
            if (selectWordInfoMap.size == originMnemonicWordList.size) {
                binding.sbtnSubmit.isEnabled = true
            }
        }
    }

    /**
     * 寻找下一个空的位置
     */
    private fun findNextEmptyIndex() {
        do {
            /**
             * 如果为空则说明当前位置并没有选中单词
             */
            if (selectWordInfoMap[++curEmptyIndex] == null) {
                return
            }
        } while (curEmptyIndex < waitSelectWordInfoMap.size)
    }


    /**
     * 生成密码
     */
    private fun genPrivateKey() {
        originMnemonicWordList.forEachIndexed { index, s ->

            val dataEntity = selectWordInfoMap[index];
            if (dataEntity == null) {
                ToastUtil.showLongToast(this, "助记词顺序不正确, 请重新选择")
                return@forEachIndexed
            }

            if (s != dataEntity.mnemonic) {
                ToastUtil.showLongToast(this, "助记词顺序不正确, 请重新选择")
                return@forEachIndexed
            }
            if (!WalletManager.GenerateWallet()) {

                ToastUtil.showLongToast(this, "生成钱包失败")
                return@forEachIndexed
            }
            ToastUtil.showLongToast(this, "生成钱包成功!")

        }
    }

    /**
     *
     */
    override fun initEvent() {
        /**
         *初始化显示选择的助记词事件
         */
        showTVMnemonicList.forEachIndexed { index, textView ->

            textView.setOnClickListener {
                selectWord(index, false)
            }
        }
        /**
         * 初始化等待选择的助记词事件
         */
        waitSelectTVMnemonicList.forEachIndexed { index, textView ->
            textView.setOnClickListener {
                selectWord(index, true)
            }
        }
        /**
         * 生成秘钥
         */
        binding.sbtnSubmit.setOnClickListener {
            genPrivateKey()
        }
        /**
         * 清空所有选中的单词
         */
        binding.btnEmpty.setOnClickListener {

            /**
             * 选中的单词都设置为空
             */
            showTVMnemonicList.forEachIndexed { index, textView ->

                textView.text = "";

                val dataEntity: WordInfo? = selectWordInfoMap[index]
                if (dataEntity != null) {
                    dataEntity.checked = false
                }
            }
            /**
             * 重新显示设置的单词
             */
            waitSelectTVMnemonicList.forEachIndexed { index, textView ->
                textView.setBackgroundResource(R.drawable.bg_shape_verify_mnemonic_n)
                textView.setTextColor(ResourceUtil.GetColor(R.color.color_316def))
            }
            curEmptyIndex = 0
            selectWordInfoMap.clear()
            binding.sbtnSubmit.isEnabled = false
        }
    }


}