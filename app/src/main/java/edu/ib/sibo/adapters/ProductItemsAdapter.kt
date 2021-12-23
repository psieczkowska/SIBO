package edu.ib.sibo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import edu.ib.sibo.R
import edu.ib.sibo.models.Product
import kotlinx.android.synthetic.main.item_product.view.*
import java.util.*
import kotlin.collections.ArrayList

open class ProductItemsAdapter(private val context: Context, private var list: ArrayList<Product>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var listCopy: ArrayList<Product> = ArrayList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_product, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            holder.itemView.tv_p_name.text = "Nazwa: ${model.name}"
            holder.itemView.tv_p_category.text = "Kategoria: ${model.category}"
            holder.itemView.tv_p_fermentation.text = "Typ: ${model.fermentation}"
            holder.itemView.tv_p_amount.text = "Bezpieczna ilość: ${model.amount}"

            if (model.fermentation == "zakazane")
                holder.itemView.setBackgroundResource(R.drawable.red_product)
            if (model.fermentation == "mało fermentujące")
                holder.itemView.setBackgroundResource(R.drawable.green_product)
            if (model.fermentation == "średnio fermentujące")
                holder.itemView.setBackgroundResource(R.drawable.orange_product)
            if (model.fermentation == "mocno fermentujące")
                holder.itemView.setBackgroundResource(R.drawable.yellow_product)

            holder.itemView.setOnClickListener {
                //TODO on click - product

            }
        }
    }

    open fun filter(text: String) {

        val textLow: String

        list.clear()
        if (text.isEmpty()) {
            list.addAll(listCopy)
        } else {
            textLow = text.toLowerCase(Locale.ROOT)
            for (item in listCopy) {
                if (item.name.toLowerCase(Locale.ROOT).contains(textLow)
                ) {
                    list.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}