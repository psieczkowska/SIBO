package edu.ib.sibo.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import edu.ib.sibo.R
import edu.ib.sibo.models.Meal
import edu.ib.sibo.models.Product
import kotlinx.android.synthetic.main.item_meal.view.*
import kotlinx.android.synthetic.main.item_product.view.*
import kotlinx.android.synthetic.main.item_product.view.tv_p_amount
import kotlinx.android.synthetic.main.item_product.view.tv_p_name
import java.util.*
import kotlin.collections.ArrayList

open class MealItemsAdapter(private val context: Context, private var list: ArrayList<Meal>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var listCopy: ArrayList<Meal> = ArrayList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_meal, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            holder.itemView.tv_m_name.text = "Nazwa: ${model.name}"
            holder.itemView.tv_m_time.text = model.time
            holder.itemView.m_color.setBackgroundColor(model.color.toInt())
            holder.itemView.tv_m_amount.text = "Ilość: ${model.amount} g."


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