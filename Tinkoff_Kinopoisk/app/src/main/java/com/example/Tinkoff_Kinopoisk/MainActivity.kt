package com.example.Tinkoff_Kinopoisk

import android.content.Context
import android.os.Bundle
import android.widget.ListAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private var listView: ListView? = null
    private var jsonArray: JSONArray? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        val context: Context = applicationContext
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listView = findViewById<ListView>(R.id.listView)

        val kp = KinopoiskAPI(context)
        var filmsTop = kp.getTOP()

        if ( filmsTop == null ){
            //load from local file, if something went wrong with GET request
            filmsTop = kp.readFileFromAssets("TOPFilms.json")
        }
        if ( filmsTop != null ){
            val gson = Gson()
            val jsonObject = JSONObject(gson.toJson(filmsTop))
            jsonArray = jsonObject.getJSONArray("films")
            val listItems = kp.getArrayListFromJSONArray(jsonArray)
            //println("listItems = " + listItems)

            //update ListView data on display
            val adapter: ListAdapter =
                FilmsAdapter(this, R.layout.row_films, R.id.nameRu, listItems)
            listView?.setAdapter(adapter)
        }
    }
}