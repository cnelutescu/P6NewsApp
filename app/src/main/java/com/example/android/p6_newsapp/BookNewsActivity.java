package com.example.android.p6_newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * BookNewsActivity implements the LoaderCallbacks interface,
 * along with a generic parameter specifying what the loader will return
 * (in this case an Book - a list of Book objects)
 */
public class BookNewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    public static final String LOG_TAG = BookNewsActivity.class.getName();

    /**
     * Constant value for the book loader ID. We can choose any integer.
     * This is only really relevant if we were using multiple loaders in the same activity
     */
    private static final int BOOK_LOADER_ID = 1;

    /**
     * Constant values to form URL to retrieve data from the Guardian data set
     */
    private static final String THEGUARDIAN_REQUEST_URL = "http://content.guardianapis.com/search?";
    private static final String SECTION = "section";
    private static final String ORDER_BY = "order-by";
    private static final String NEWEST = "newest";
    private static final String SHOW_TAGS = "show-tags";
    private static final String CONTRIBUTOR = "contributor";
    private static final String SHOW_FIELDS = "show-fields";
    private static final String THUMBNAIL = "thumbnail";
    private static final String PAGE_SIZE = "page-size";
    private static final String API_KEY = "api-key";
    private static final String KEY = "5b253f61-670e-44c9-9cab-8fe015dbbfc0";

    /**
     * Adapter for the list of books
     */
    private BookAdapter mAdapter;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_news);

        // Find a reference to the {@link ListView} in the layout
        ListView bookListView = (ListView) findViewById(R.id.list);

        // We need to hook up the TextView as the empty view of the ListView (no book found!).
        // We can use the ListView setEmptyView() method.
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        bookListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of books as input
        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        // Set the adapter on the {@link ListView}
        // so the list of books can be populated in the user interface
        bookListView.setAdapter(mAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected book.
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current book that was clicked on
                Book currentBook = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri bookUri = Uri.parse(currentBook.getUrl());

                // Create a new intent to view the book URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        //              Check state of network connectivity
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            // Initialize the loader 1. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    /**
     * If LoaderManager has determined that the loader with our specified ID isn't running
     * will create a new one with onCreateLoader()
     *
     * @param i      our specified ID for the loader
     * @param bundle of arguments
     * @return BookLoader
     */
    @Override
    // onCreateLoader instantiates and returns a new Loader for the given ID
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {

        // Read preferences from storage
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // GetString retrieves a String value from the preferences.
        // The second parameter is the default value for this preference.
        String pageSize = sharedPrefs.getString(
                getString(R.string.settings_page_size_key),
                getString(R.string.settings_page_size_default));
        // limit
        int intValue = Integer.parseInt(pageSize);
        if (intValue > 50) {
            pageSize = "50";
        }

        String section = sharedPrefs.getString(
                getString(R.string.settings_section_key),
                getString(R.string.settings_section_default)
        );

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(THEGUARDIAN_REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter(SECTION, section);
        uriBuilder.appendQueryParameter(ORDER_BY, NEWEST);
        uriBuilder.appendQueryParameter(SHOW_TAGS, CONTRIBUTOR);
        uriBuilder.appendQueryParameter(SHOW_FIELDS, THUMBNAIL);
        uriBuilder.appendQueryParameter(PAGE_SIZE, pageSize);
        uriBuilder.appendQueryParameter(API_KEY, KEY);

        // Return the completed uri
        // Create a new loader for the given URL
        return new BookLoader(this, uriBuilder.toString());
    }

    /**
     * We need onLoadFinished(), where we'll do exactly what we did in onPostExecute()
     * and use the book data to update our UI - by updating the dataset in the adapter.
     *
     * @param loader object
     * @param books  a list of Book objects
     */
    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {

        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No books found."
        mEmptyStateTextView.setText(R.string.no_books);

        // Clear the adapter of previous book data
        mAdapter.clear();

        // If there is a valid list of {@link Book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        }
    }

    /**
     * We need onLoaderReset() when we are informed that the data from our loader is no longer valid.
     * This isn't actually a case that's going to come up with our simple loader,
     * but the correct thing to do is to remove all the book data from our UI
     * by clearing out the adapter’s data set.
     *
     * @param loader the loader we use
     */
    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }


    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // This method is called whenever an item in the options menu is selected.
    // This method passes the MenuItem that is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();  // determine which item ID was selected and what action to take
        if (id == R.id.action_settings) {   // our menu only has one item: action_settings
            // open the SettingsActivity via an intent
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
