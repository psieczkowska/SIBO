package edu.ib.sibo.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.ib.sibo.R
import edu.ib.sibo.models.Meal
import kotlinx.android.synthetic.main.item_meal.view.*
import kotlin.collections.ArrayList

open class MealItemsAdapter(private val context: Context, private var list: ArrayList<Meal>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_meal, parent, false)
        )
    }

    private var onClickListener: OnClickListener? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            holder.itemView.tv_m_name.text = "Nazwa: ${model.name}"
            holder.itemView.tv_m_time.text = model.time
            holder.itemView.m_color.setBackgroundColor(model.color.toInt())
            holder.itemView.tv_m_amount.text = "Ilość: ${model.amount} g"


            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }

    interface OnClickListener {
        fun onClick(position: Int, model: Meal)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}