package edu.ib.sibo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.ib.sibo.R
import edu.ib.sibo.models.Product
import edu.ib.sibo.models.Rate
import kotlinx.android.synthetic.main.item_product.view.*
import kotlinx.android.synthetic.main.item_rate.view.*
import java.util.*
import kotlin.collections.ArrayList

class RateItemsAdapter (private val context: Context, private var list: ArrayList<Rate>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_rate, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            holder.itemView.tv_comment_specialist.text = model.comment
            holder.itemView.tv_rate_specialist.text = model.rate.toString()

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}