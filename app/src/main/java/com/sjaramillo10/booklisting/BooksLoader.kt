package com.sjaramillo10.booklisting

import android.content.AsyncTaskLoader
import android.content.Context
import android.util.Log

/**
 * Constructs a new [BooksLoader].
 *
 * @param context of the activity
 * @param url to load data from
 */
class BooksLoader (context: Context, private val url: String?) :
        AsyncTaskLoader<List<Book>>(context) {

    companion object {

        /** Tag for log messages  */
        private val TAG = BooksLoader::class.java.name
    }

    override fun onStartLoading() {
        forceLoad()
    }

    override fun loadInBackground(): List<Book>? {
        Log.d(TAG, "URL: $url")

        return if (url == null) {
            null
        } else QueryUtils.fetchEarthquakeData(url)

    }
}