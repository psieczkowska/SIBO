package edu.ib.sibo.adapters

import android.content.Context
import android.graphics.Color.red
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.google.common.io.Resources.getResource
import edu.ib.sibo.R
import edu.ib.sibo.models.Product
import kotlinx.android.synthetic.main.item_product.view.*
import java.util.ArrayList

open class ProductItemsAdapter(private val context: Context, private var list: ArrayList<Product>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
                holder.itemView.setBackgroundColor(getColor(context, R.color.color_zakazany))
            if (model.fermentation == "mało fermentujące")
                holder.itemView.setBackgroundColor(getColor(context, R.color.color_malo))
            if (model.fermentation == "średnio fermentujące")
                holder.itemView.setBackgroundColor(getColor(context, R.color.color_srednio))
            if (model.fermentation == "mocno fermentujące")
                holder.itemView.setBackgroundColor(getColor(context, R.color.color_srednio))

            holder.itemView.setOnClickListener {
                //TODO on click - product

            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}