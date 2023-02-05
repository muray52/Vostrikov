package com.example.Tinkoff_Kinopoisk

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import org.json.JSONException
import org.json.JSONObject

class FilmsAdapter(
    var contextNew: Context,
    var listLayout: Int,
    field: Int,
    var list: ArrayList<JSONObject>
) : ArrayAdapter<JSONObject>(
    contextNew, listLayout, field, list
) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = inflater.inflate(listLayout, parent, false)
        val nameRu = itemView.findViewById<TextView>(R.id.nameRu)
        val year = itemView.findViewById<TextView>(R.id.year)

        try {
            nameRu.text = list[position].getString("nameRu")
            year.text = list[position]?.getString("year")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return itemView
    }
}