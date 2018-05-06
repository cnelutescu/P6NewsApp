package com.example.android.p6_newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Loads a list of books by using an AsyncTask Loader to perform the network request to the given URL.
 * To define the BookLoader class, we extend AsyncTaskLoader and specify List as the generic
 * parameter, which explains what type of data is expected to be loaded. In this case, the loader
 * is loading a list of Book objects.
 */
public class BookLoader extends AsyncTaskLoader<List<Book>> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = BookLoader.class.getName();

    /**
     * Query URL
     */
    private String mUrl;

    /**
     * Constructs a new {@link BookLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     *                We take a String URL in the constructor,
     */
    public BookLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    /**
     * Notice that we also override the onStartLoading() method to call forceLoad() which is a
     * required step to actually trigger the loadInBackground() method to execute.
     */
    @Override
    protected void onStartLoading() {
        forceLoad();    // trigger the loadInBackground() method to execute
    }

    /**
     * This is on a background thread.
     * We'll do the exact same operations as in doInBackground back in BookAsyncTask.
     */
    @Override
    public List<Book> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of books.
        return QueryUtils.fetchBookData(mUrl);
    }

}
