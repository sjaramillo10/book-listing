package com.sjaramillo10.booklisting

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView


class BooksAdapter internal constructor(context: Activity, books: ArrayList<Book>) :
        ArrayAdapter<Book>(context, 0, books) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = parent.inflate(R.layout.item_book, false)
        }

        val book = getItem(position)

        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        tvTitle.text = book.title

        return itemView
    }

    companion object {
        private val TAG = BooksAdapter::class.java.simpleName
    }
}
