package com.sjaramillo10.booklisting

import android.text.TextUtils
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset
import java.util.ArrayList

/**
 * Helper methods related to requesting and receiving book data from Google Books.
 */
object QueryUtils {
    private val TAG = QueryUtils::class.java.name

    /**
     * Return a list of [Book] objects that has been built up from
     * parsing the given JSON response.
     */
    private fun extractFeatureFromJson(bookJSON: String?): List<Book>? {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(bookJSON)) {
            return null
        }

        // Create an empty ArrayList that we can start adding books to
        val books = ArrayList<Book>()

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            val baseJsonResponse = JSONObject(bookJSON)

            // Extract the JSONArray associated with the key called "features",
            // which represents a list of features (or books).
            val bookArray = baseJsonResponse.getJSONArray("items")

            // For each book in the bookArray, create a {@link Book} object
            for (i in 0 until bookArray.length()) {

                // Get a single earthquake at position i within the list of earthquakes
                val currentBook = bookArray.getJSONObject(i)

                // For a given book, extract the JSONObject associated with the
                // key called "volumeInfo"
                val volumeInfo = currentBook.getJSONObject("volumeInfo")


                // Extract the value for the key called "authors"
                val author = "something" //volumeInfo.getJSONArray("authors")[0]

                // Extract the value for the key called "url"
                val title = volumeInfo.getString("title")

                // Create a new {@link Book} object with the authors and title from the JSON response.
                val book = Book(author, title)

                // Add the new {@link Book} to the list of books.
                books.add(book)
            }

        } catch (e: JSONException) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the book JSON results", e)
        }

        // Return the list of books
        return books
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private fun createUrl(stringUrl: String): URL? {
        var url: URL? = null
        try {
            url = URL(stringUrl)
        } catch (e: MalformedURLException) {
            Log.e(TAG, "Problem building the URL ", e)
        }

        return url
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    @Throws(IOException::class)
    private fun makeHttpRequest(url: URL?): String {
        var jsonResponse = ""

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse
        }

        var urlConnection: HttpURLConnection? = null
        var inputStream: InputStream? = null
        try {
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.readTimeout = 10000
            urlConnection.connectTimeout = 15000
            urlConnection.requestMethod = "GET"
            urlConnection.connect()

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.responseCode == 200) {
                inputStream = urlConnection.inputStream
                jsonResponse = readFromStream(inputStream)
            } else {
                Log.e(TAG, "Error response code: " + urlConnection.responseCode)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Problem retrieving the earthquake JSON results.", e)
        } finally {
            urlConnection?.disconnect()
            inputStream?.close()
        }
        return jsonResponse
    }

    /**
     * Convert the [InputStream] into a String which contains the
     * whole JSON response from the server.
     */
    @Throws(IOException::class)
    private fun readFromStream(inputStream: InputStream?): String {
        val output = StringBuilder()
        if (inputStream != null) {
            val inputStreamReader = InputStreamReader(inputStream, Charset.forName("UTF-8"))
            val reader = BufferedReader(inputStreamReader)
            var line: String? = reader.readLine()
            while (line != null) {
                output.append(line)
                line = reader.readLine()
            }
        }
        return output.toString()
    }

    /**
     * Query the Google Books dataset and return a list of [Book] objects.
     */
    fun fetchEarthquakeData(requestUrl: String): List<Book>? {
        // Create URL object
        val url = createUrl(requestUrl)

        // Perform HTTP request to the URL and receive a JSON response back
        var jsonResponse: String? = null
        try {
            jsonResponse = makeHttpRequest(url)
        } catch (e: IOException) {
            Log.e(TAG, "Problem making the HTTP request.", e)
        }

        // Extract relevant fields from the JSON response and create a list of {@link Books}.
        return extractFeatureFromJson(jsonResponse)
    }

}