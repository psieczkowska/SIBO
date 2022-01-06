package edu.ib.sibo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.ib.sibo.R
import edu.ib.sibo.models.Specialist
import kotlinx.android.synthetic.main.item_specialist.view.*
import java.util.*
import kotlin.collections.ArrayList

class SpecialistItemsAdapter(
    private val context: Context,
    private var list: ArrayList<Specialist>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var listCopy: ArrayList<Specialist> = ArrayList(list)
    var value: Int = 0
    var valueD: Double = 0.0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_specialist, parent, false)
        )
    }

    private var onClickListener: OnClickListener? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            holder.itemView.tv_specialist_name.text = model.name
            holder.itemView.tv_specialist_surname.text = model.surname
            holder.itemView.tv_specialist_city.text = model.address

            for (i in model.rating) {
                value += i.rate
            }

            valueD = (value.toDouble() / model.rating.size)
            value = 0
            holder.itemView.tv_specialist_rating.text = "$valueD/5"
            holder.itemView.tv_specialist_type.text = model.type
            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
                }
            }
            valueD = 0.0
        }
    }

    fun filter(text: String) {

        val textLow: String

        list.clear()
        if (text.isEmpty()) {
            list.addAll(listCopy)
        } else {
            textLow = text.toLowerCase(Locale.ROOT)
            for (item in listCopy) {
                if (item.name.toLowerCase(Locale.ROOT).contains(textLow) || item.address.toLowerCase(
                        Locale.ROOT
                    ).contains(textLow) || item.surname.toLowerCase(Locale.ROOT).contains(textLow)
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

    interface OnClickListener {
        fun onClick(position: Int, model: Specialist)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}