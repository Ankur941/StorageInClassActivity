package com.example.networkapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException

// TODO (1: Fix any bugs)
// TODO (2: Add function saveComic(...) to save comic info when downloaded
// TODO (3: Automatically load previously saved comic when app starts)

class MainActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    lateinit var titleTextView: TextView
    lateinit var descriptionTextView: TextView
    lateinit var numberEditText: EditText
    lateinit var showButton: Button
    lateinit var comicImageView: ImageView
    private lateinit var file: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //intialize the file
        file = File(filesDir, "comic.json")

        requestQueue = Volley.newRequestQueue(this)

        titleTextView = findViewById<TextView>(R.id.comicTitleTextView)
        descriptionTextView = findViewById<TextView>(R.id.comicDescriptionTextView)
        numberEditText = findViewById<EditText>(R.id.comicNumberEditText)
        showButton = findViewById<Button>(R.id.showComicButton)
        comicImageView = findViewById<ImageView>(R.id.comicImageView)

        showButton.setOnClickListener {
            val comicId = numberEditText.text.toString()
            if (comicId.isNotEmpty()) {
                downloadComic(comicId)
            } else {
                Toast.makeText(this, "Please enter a comic number.", Toast.LENGTH_SHORT).show()
            }
        }

        loadSavedComic()

    }

    private fun loadSavedComic() {
        if (file.exists() && file.canRead()) {
            try {
                val savedJsonString = file.readText()
                if (savedJsonString.isNotEmpty()) {
                    val comicObject = JSONObject(savedJsonString)
                    showComic(comicObject)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Error reading saved comic.", Toast.LENGTH_SHORT).show()
            } catch (e: JSONException) {
                e.printStackTrace()
                Toast.makeText(this, "Error parsing saved comic.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // Fetches comic from web as JSONObject
    private fun downloadComic (comicId: String) {
        val url = "https://xkcd.com/$comicId/info.0.json"
        val jsonObjectRequest = JsonObjectRequest(url,
            { response ->

                showComic(response)
                saveComic(response)
            },
            { error ->

                error.printStackTrace()
                Toast.makeText(this, "Download failed. Check the number or your connection.", Toast.LENGTH_LONG).show()
            }
        )
        requestQueue.add(jsonObjectRequest)
    }

    // Display a comic for a given comic JSON object
    private fun showComic (comicObject: JSONObject) {
        try {
            titleTextView.text = comicObject.getString("title")
            descriptionTextView.text = comicObject.getString("alt")
            Picasso.get().load(comicObject.getString("img")).into(comicImageView)
        } catch (e: JSONException) {
            e.printStackTrace()
            Toast.makeText(this, "Error displaying comic data.", Toast.LENGTH_SHORT).show()
        }
    }

    // Implement this function
    private fun saveComic(comicObject: JSONObject) {
        val comicJsonString = comicObject.toString()
        try {

            file.writeText(comicJsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            //Notify user of save failure
            Toast.makeText(this, "Failed to save comic", Toast.LENGTH_SHORT).show()
        }

    }


}