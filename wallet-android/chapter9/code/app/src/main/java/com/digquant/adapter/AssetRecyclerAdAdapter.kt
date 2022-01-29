package com.digquant.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.digquant.activity.AssetChainActivity
import com.digquant.databinding.ItemAssetListBinding
import com.digquant.entity.AssetItemData
import com.digquant.util.AmountUtil
import com.digquant.util.DXRouter

/**
 *
 */
class AssetItemViewHolder(itemView: View, val adapter: AssetRecyclerAdAdapter) :
    BaseViewHolder(itemView) {
    private val binding = ItemAssetListBinding.bind(itemView)
    override fun OnRender(position: Int) {
        val assetItemData = adapter.GetItemData(position)
        binding.assetsIcon.setImageResource(assetItemData.iconId)
        binding.assetsName.text = assetItemData.name


        binding.assetsAmount.text = AmountUtil.formatAmountText3(assetItemData.amount.toString())




        binding.root.setOnClickListener {

            DXRouter.JumpTo(itemView.context, AssetChainActivity::class.java)
        }
    }
}

class AssetRecyclerAdAdapter : RecyclerView.Adapter<BaseViewHolder>() {

    private var itemList = ArrayList<AssetItemData>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        val binding = ItemAssetListBinding.inflate(inflater, parent, false)

        return AssetItemViewHolder(binding.root, this)
    }


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.OnRender(position)
    }

    override fun getItemCount(): Int {

        return itemList.size
    }


    fun GetItemData(position: Int): AssetItemData {
        return this.itemList[position]
    }

    /**
     * 更新数据
     */

    fun UpdateData(itemList: List<AssetItemData>) {
        this.itemList.clear()
        this.itemList.addAll(itemList)
        notifyDataSetChanged()
    }

}