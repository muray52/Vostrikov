package com.example.Tinkoff_Kinopoisk

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter

//token for kinopoisk API connection
private const val KEY_API = "0ce8c551-3d09-4f92-98ad-622fcab2f353"
//URL for GET requests
private const val BASE_URL = "https://kinopoiskapiunofficial.tech"

class KinopoiskAPI {
    private val context: Context

    constructor(_context : Context){
        context = _context
    }

    //create Retrofit
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    //create Service
    private val service = retrofit.create(KinopoiskApiService::class.java)

    //get TOP 100 movies from Kinopoisk list
    // ----later add requesting pages
    fun getTOP(): FilmsPages?{
        val top100Request = service.getTop100Films()

        top100Request?.enqueue(object : Callback<FilmsPages>{
            override fun onResponse(
                call: Call<FilmsPages?>?,
                response: Response<FilmsPages?>
            ) {
                if ( response.isSuccessful ) {
                    val body: FilmsPages? = response.body()
                    if (body != null) writeToFile(body)
                    println("Films body = " + body.toString())
                } else {
                    Log.i("Internet", "Connection error")
                }
            }

            override fun onFailure(call: Call<FilmsPages?>, t: Throwable?) {
                call.cancel()
            }
        })

        return readFromFile()
    }

    fun getArrayListFromJSONArray(jsonArray: JSONArray?): ArrayList<JSONObject> {
        val arrayList = ArrayList<JSONObject>()
        try {
            if (jsonArray != null) {
                for (i in jsonArray.length()-1 downTo 0 ) {
                    arrayList.add(jsonArray.getJSONObject(i))
                }
            }
        } catch (js: JSONException) {
            js.printStackTrace()
        }
        return arrayList
    }

    // write json data to internal file
    fun writeToFile(body: FilmsPages?){
        val filename = "TOPFilms.json"
        val gson = Gson()
        try {
            val outputStreamWriter =
                OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE))
            outputStreamWriter.write(gson.toJson(body))
            outputStreamWriter.close()
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: " + e.toString())
        }
    }

    //read json from internal file
    fun readFromFile(filename : String = "TOPFilms.json") : FilmsPages? {
        val gson = GsonBuilder().create()
        var body : FilmsPages? = null
        try {
            val inputStreamReader = InputStreamReader(context.openFileInput(filename))
            val data = inputStreamReader.readText()
            inputStreamReader.close()
            body = gson.fromJson(data,FilmsPages::class.java)
        } catch (e: IOException) {
            Log.e("Exception", "File read failed: " + e.toString())
        }
        return body
    }

    //read json from local assets file
    fun readFileFromAssets(filename : String = "TOPFilms.json") : FilmsPages? {
        val gson = GsonBuilder().create()
        var body : FilmsPages? = null
        try {
            val inputStreamReader = InputStreamReader(context.assets.open(filename))
            val data = inputStreamReader.readText()
            inputStreamReader.close()
            body = gson.fromJson(data,FilmsPages::class.java)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return body
    }

    fun getFilmInfo(filmId: Int){
        //------rewrite like getTOP, add activity connection, etc
        Thread {
            val filmRequest = service.getFilmInfo(filmId)
            val resultExecution = filmRequest.execute().body()
        }.start()
    }
}

interface KinopoiskApiService {
    @Headers(
        "X-API-KEY: $KEY_API",
        "Content-Type: application/json"
    )
    @GET("/api/v2.2/films/top?type=TOP_100_POPULAR_FILMS")
    fun getTop100Films(): Call<FilmsPages>

    @Headers(
        "X-API-KEY: $KEY_API",
        "Content-Type: application/json")
    @GET("/api/v2.2/films/{FilmId}")
    fun getFilmInfo(@Path("FilmId") filmId: Int): Call<FilmDefinition>
}

data class FilmsPages (

    @SerializedName("pagesCount" ) var pagesCount : Int?             = null,
    @SerializedName("films"      ) var films      : ArrayList<Films> = arrayListOf()

)

data class Films (

    @SerializedName("filmId"           ) var filmId           : Int?                 = null,
    @SerializedName("nameRu"           ) var nameRu           : String?              = null,
    @SerializedName("nameEn"           ) var nameEn           : String?              = null,
    @SerializedName("year"             ) var year             : String?              = null,
    @SerializedName("filmLength"       ) var filmLength       : String?              = null,
    @SerializedName("countries"        ) var countries        : ArrayList<Countries> = arrayListOf(),
    @SerializedName("genres"           ) var genres           : ArrayList<Genres>    = arrayListOf(),
    @SerializedName("rating"           ) var rating           : String?              = null,
    @SerializedName("ratingVoteCount"  ) var ratingVoteCount  : Int?                 = null,
    @SerializedName("posterUrl"        ) var posterUrl        : String?              = null,
    @SerializedName("posterUrlPreview" ) var posterUrlPreview : String?              = null,
    @SerializedName("ratingChange"     ) var ratingChange     : String?              = null

)

data class FilmDefinition (

    @SerializedName("kinopoiskId"                ) var kinopoiskId                : Int?                 = null,
    @SerializedName("imdbId"                     ) var imdbId                     : String?              = null,
    @SerializedName("nameRu"                     ) var nameRu                     : String?              = null,
    @SerializedName("nameEn"                     ) var nameEn                     : String?              = null,
    @SerializedName("nameOriginal"               ) var nameOriginal               : String?              = null,
    @SerializedName("posterUrl"                  ) var posterUrl                  : String?              = null,
    @SerializedName("posterUrlPreview"           ) var posterUrlPreview           : String?              = null,
    @SerializedName("coverUrl"                   ) var coverUrl                   : String?              = null,
    @SerializedName("logoUrl"                    ) var logoUrl                    : String?              = null,
    @SerializedName("reviewsCount"               ) var reviewsCount               : Int?                 = null,
    @SerializedName("ratingGoodReview"           ) var ratingGoodReview           : Double?              = null,
    @SerializedName("ratingGoodReviewVoteCount"  ) var ratingGoodReviewVoteCount  : Int?                 = null,
    @SerializedName("ratingKinopoisk"            ) var ratingKinopoisk            : Double?              = null,
    @SerializedName("ratingKinopoiskVoteCount"   ) var ratingKinopoiskVoteCount   : Int?                 = null,
    @SerializedName("ratingImdb"                 ) var ratingImdb                 : Double?              = null,
    @SerializedName("ratingImdbVoteCount"        ) var ratingImdbVoteCount        : Int?                 = null,
    @SerializedName("ratingFilmCritics"          ) var ratingFilmCritics          : Double?              = null,
    @SerializedName("ratingFilmCriticsVoteCount" ) var ratingFilmCriticsVoteCount : Int?                 = null,
    @SerializedName("ratingAwait"                ) var ratingAwait                : Double?              = null,
    @SerializedName("ratingAwaitCount"           ) var ratingAwaitCount           : Int?                 = null,
    @SerializedName("ratingRfCritics"            ) var ratingRfCritics            : Double?              = null,
    @SerializedName("ratingRfCriticsVoteCount"   ) var ratingRfCriticsVoteCount   : Int?                 = null,
    @SerializedName("webUrl"                     ) var webUrl                     : String?              = null,
    @SerializedName("year"                       ) var year                       : Int?                 = null,
    @SerializedName("filmLength"                 ) var filmLength                 : Int?                 = null,
    @SerializedName("slogan"                     ) var slogan                     : String?              = null,
    @SerializedName("description"                ) var description                : String?              = null,
    @SerializedName("shortDescription"           ) var shortDescription           : String?              = null,
    @SerializedName("editorAnnotation"           ) var editorAnnotation           : String?              = null,
    @SerializedName("isTicketsAvailable"         ) var isTicketsAvailable         : Boolean?             = null,
    @SerializedName("productionStatus"           ) var productionStatus           : String?              = null,
    @SerializedName("type"                       ) var type                       : String?              = null,
    @SerializedName("ratingMpaa"                 ) var ratingMpaa                 : String?              = null,
    @SerializedName("ratingAgeLimits"            ) var ratingAgeLimits            : String?              = null,
    @SerializedName("hasImax"                    ) var hasImax                    : Boolean?             = null,
    @SerializedName("has3D"                      ) var has3D                      : Boolean?             = null,
    @SerializedName("lastSync"                   ) var lastSync                   : String?              = null,
    @SerializedName("countries"                  ) var countries                  : ArrayList<Countries> = arrayListOf(),
    @SerializedName("genres"                     ) var genres                     : ArrayList<Genres>    = arrayListOf(),
    @SerializedName("startYear"                  ) var startYear                  : Int?                 = null,
    @SerializedName("endYear"                    ) var endYear                    : Int?                 = null,
    @SerializedName("serial"                     ) var serial                     : Boolean?             = null,
    @SerializedName("shortFilm"                  ) var shortFilm                  : Boolean?             = null,
    @SerializedName("completed"                  ) var completed                  : Boolean?             = null

)

data class Countries (

    @SerializedName("country" ) var country : String? = null

)
data class Genres (

    @SerializedName("genre" ) var genre : String? = null

)