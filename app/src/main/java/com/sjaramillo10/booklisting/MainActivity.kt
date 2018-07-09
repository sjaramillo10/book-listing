package com.sjaramillo10.booklisting

import android.app.LoaderManager
import android.content.Context
import android.content.Loader
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<List<Book>>{

    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private val _bookLoaderId = 1

    private lateinit var cm: ConnectivityManager

    /** Adapter for the list of books */
    private var booksAdapter: BooksAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        booksAdapter = BooksAdapter(this, ArrayList())

        listView.adapter = booksAdapter

        cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        btnSearch.setOnClickListener {
            val activeNetwork = cm.activeNetworkInfo
            val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting

            if (isConnected) run {
                // Get a reference to the LoaderManager
                // Initialize the loader. Pass in the int ID constant defined above and pass in null for
                // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
                // because this activity implements the LoaderCallbacks interface).
                loaderManager.initLoader<List<Book>>(_bookLoaderId, null, this)
            } else {
                Toast.makeText(this, "Verify your internet connection", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<Book>> {
        var searchText = "android"

        if(id == _bookLoaderId) {
            searchText = etSearch.text.toString()
        }

        return BooksLoader(this, "https://www.googleapis.com/books/v1/volumes?q=$searchText&maxResults=15")
    }

    override fun onLoadFinished(loader: Loader<List<Book>>, books: List<Book>) {
        booksAdapter?.clear()

        if (!books.isEmpty()) {
            booksAdapter?.addAll(books)
        }
    }

    override fun onLoaderReset(loader: Loader<List<Book>>?) {
        booksAdapter?.clear()
    }
}
