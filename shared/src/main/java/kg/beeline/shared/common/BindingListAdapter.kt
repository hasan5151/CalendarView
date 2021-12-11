package kg.beeline.shared.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding

/** Created by Jahongir on 26/01/2021.*/
abstract class BindingListAdapter<T, VB : ViewBinding, VH : ViewHolder>(diffCallBack: ItemCallback<T>) : ListAdapter<T, VH>(diffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = createBinding(inflater, parent)
        return createViewHolder(binding)
    }

    abstract fun createBinding(inflater: LayoutInflater, parent: ViewGroup): VB
    abstract fun createViewHolder(itemBinding: ViewBinding): VH
}