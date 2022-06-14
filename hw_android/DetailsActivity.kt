package com.example.hw_android

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.coroutines.*
import kotlin.math.roundToInt

class DetailsActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var service: WeatherService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val id = intent.extras?.getInt(KEY_ID) ?: 0

        service = ApiFactory.weatherService
        responseById(id)
    }

    private fun responseById(id: Int) {
        launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    service.weatherByID(id)
                }
                supportActionBar?.setTitle(response.name)
                tv_temp.text = response.main.temp.roundToInt().toString().plus("\u2103")
                tv_humidity.text = response.main.humidity.toString().plus("\u0025")
                tv_main.text = response.weather[0].description
                tv_wind.text = response.wind.deg.toString()
                response.wind.deg.let {
                    val windDeg = when {//поменять на другие деления по 45
                        (it > 22.5) and (it <= 67.5) -> {
                            "NE"
                        }
                        (it > 67.5) and (it <= 112.5) -> {
                            "E"
                        }
                        (it > 112.5) and (it <= 157.5) -> {
                            "SE"
                        }
                        (it > 157.5) and (it <= 202.5) -> {
                            "S"
                        }
                        (it > 202.5) and (it <= 247.5) -> {
                            "SW"
                        }
                        (it > 247.5) and (it <= 292.5) -> {
                            "W"
                        }
                        (it > 292.5) and (it <= 337.5) -> {
                            "NW"
                        }
                        else -> {
                            "N"
                        }
                    }
                    tv_wind.text = windDeg
                }
                tv_clouds.text = response.clouds.all.toString().plus("\u0025")
                Glide.with(this@DetailsActivity)
                    .load(getIconUrl(response.weather.get(0).icon))
                    .into(iv_weather)
            } catch (ex: Exception) {
                throw IllegalArgumentException(ex)
            }
        }
    }

    private fun getIconUrl(id: String?) = "http://openweathermap.org/img/wn/$id@2x.png"

    companion object {
        private const val KEY_ID = "id"

        fun createIntent(activity: Activity, id: Int) =
            Intent(activity, DetailsActivity::class.java).putExtra(KEY_ID, id)
    }
}
