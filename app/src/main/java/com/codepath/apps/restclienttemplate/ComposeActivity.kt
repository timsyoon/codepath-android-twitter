package com.codepath.apps.restclienttemplate

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class ComposeActivity : AppCompatActivity() {

    lateinit var etCompose: EditText
    lateinit var btnTweet: Button
    lateinit var tvCharacterCount: TextView

    var charactersRemaining = CHARACTER_LIMIT

    lateinit var client: TwitterClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        etCompose = findViewById(R.id.etTweetCompose)
        btnTweet = findViewById(R.id.btnTweet)
        tvCharacterCount = findViewById(R.id.tvCharacterCount)
        tvCharacterCount.text = charactersRemaining.toString()
        tvCharacterCount.setTextColor(Color.GRAY)

        client = TwitterApplication.getRestClient(this)

        // Handles the user's click on the tweet button
        btnTweet.setOnClickListener {

            // Grab the content of edittext (etCompose)
            val tweetContent = etCompose.text.toString()

            // 1. Make sure the tweet isn't empty
            if (tweetContent.isEmpty()) {
                Toast.makeText(this, "Empty tweets not allowed!", Toast.LENGTH_SHORT)
                    .show()
            }

            // 2. Make sure the tweet is under character count
            else if (tweetContent.length > 280) {
                Toast.makeText(this, "Tweet is too long! Limit is 280 characters", Toast.LENGTH_SHORT)
                    .show()
            }

            else {
                client.publishTweet(tweetContent, object : JsonHttpResponseHandler() {

                    override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                        Log.i(TAG, "Successfully published tweet!")

                        val tweet = Tweet.fromJson(json.jsonObject)

                        val intent = Intent()
                        intent.putExtra("tweet", tweet)
                        setResult(RESULT_OK, intent)
                        finish()
                    }

                    override fun onFailure(
                        statusCode: Int,
                        headers: Headers?,
                        response: String?,
                        throwable: Throwable?
                    ) {
                        Log.e(TAG, "Failed to publish tweet", throwable)
                    }
                })
            }
        }

        // Handles the text being changed in the EditText
        etCompose.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.i(TAG, "In beforeTextChanged().")
                Log.i(TAG, "   count: $count")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.i(TAG, "In onTextChanged().")
                Log.i(TAG, "   count: $count")
                charactersRemaining = CHARACTER_LIMIT - count
                tvCharacterCount.text = charactersRemaining.toString()
                if (charactersRemaining < 0) {
                    tvCharacterCount.setTextColor(Color.RED)
                } else {
                    tvCharacterCount.setTextColor(Color.GRAY)
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                Log.i(TAG, "In afterTextChanged().")
            }
        })
    }

    companion object {
        val TAG = "ComposeActivity"
        const val CHARACTER_LIMIT = 280
    }
}