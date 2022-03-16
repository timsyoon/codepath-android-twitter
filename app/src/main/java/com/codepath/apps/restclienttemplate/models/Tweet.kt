package com.codepath.apps.restclienttemplate.models

import android.os.Parcelable
import com.codepath.apps.restclienttemplate.TimeFormatter
import kotlinx.parcelize.Parcelize
import org.json.JSONArray
import org.json.JSONObject

@Parcelize
class Tweet(
    var user: User? = null,
    var createdAt: String = "",
    var relativeTimestamp: String = "",
    var body: String = ""
) : Parcelable {
    companion object {
        fun fromJson(jsonObject: JSONObject) : Tweet {
            val rawTimestamp = jsonObject.getString("created_at")
            val relTimestamp = TimeFormatter.getTimeDifference(rawTimestamp)

            val tweet = Tweet()
            tweet.user = User.fromJson(jsonObject.getJSONObject("user"))
            tweet.createdAt = rawTimestamp
            tweet.relativeTimestamp = relTimestamp
            tweet.body = jsonObject.getString("text")
            return tweet
        }

        fun fromJsonArray(jsonArray: JSONArray): List<Tweet> {
            val tweets = ArrayList<Tweet>()
            for (i in 0 until jsonArray.length()) {
                tweets.add(fromJson(jsonArray.getJSONObject(i)))
            }
            return tweets
        }
    }
}