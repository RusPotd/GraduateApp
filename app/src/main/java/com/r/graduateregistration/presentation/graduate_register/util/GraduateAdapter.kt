package com.r.graduateregistration.presentation.graduate_register.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.r.graduateregistration.R
import com.r.graduateregistration.domain.models.GraduateData


class GraduateAdapter(
    private val mList: List<GraduateData>,
    private val onClickListener: (GraduateData) -> Unit
) : RecyclerView.Adapter<GraduateAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_graduate_list, parent, false)

        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val graduateData = mList[position]

        holder.textView.text = graduateData.name
        holder.txtUniversity.text = graduateData.district

        holder.layout.setOnClickListener {
            onClickListener.invoke(mList[position])
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val textView: TextView = itemView.findViewById(R.id.text_username)
        val txtUniversity: TextView = itemView.findViewById(R.id.text_university)
        val layout: LinearLayout = itemView.findViewById(R.id.layout_grd)
    }
}