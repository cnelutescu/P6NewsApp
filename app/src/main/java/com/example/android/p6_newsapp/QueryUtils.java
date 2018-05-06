package com.example.android.p6_newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving book data from The Guardian.
 */
public final class QueryUtils {

    // JSON data
    private static final String RESPONSE = "response";
    private static final String RESULTS = "results";
    private static final String SECTION_NAME = "sectionName";
    private static final String WEB_PUBLICATION_DATE = "webPublicationDate";
    private static final String WEB_TITLE = "webTitle";
    private static final String WEB_URL = "webUrl";
    private static final String FIELDS= "fields";
    private static final String THUMBNAIL = "thumbnail";
    private static final String TAGS = "tags";

    // Tag for data item not available
    private static final String NOT_AVAILABLE = "N/A";

    /** Tag for the log messages */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the Guardian dataset and return a list of {@link Book} objects.
     */
    public static List<Book> fetchBookData(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Book}s
        // Return the list of {@link Book}s
        return extractResultsFromJson(jsonResponse);
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a the JSON String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Query the Guardian dataset and return a list of {@link Book} objects.
     */
    private static List<Book> extractResultsFromJson(String bookJSON) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding books to
        List<Book> books = new ArrayList<>();
        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(bookJSON);
            // Extract the JSONObject associated with the key called "response",
            JSONObject responseJSONObject = baseJsonResponse.getJSONObject(RESPONSE);
            // Extract the JSONArray associated with the key called "results"
            JSONArray bookArray = responseJSONObject.getJSONArray(RESULTS);
            // For each book in the bookArray, create an {@link Book} object
            for (int i = 0; i < bookArray.length(); i++) {

                // Get a single book at position i within the list of books
                JSONObject currentBook = bookArray.getJSONObject(i);

                // Extract the value for the key called "sectionName"
                String sectionName = currentBook.optString(SECTION_NAME);

                // Extract the value for the key called "webPublicationDate"
                String date = currentBook.optString(WEB_PUBLICATION_DATE);

                // Extract the value for the key called "webTitle"
                String title = currentBook.optString(WEB_TITLE);

                // Extract the value for the key called "webUrl"
                String url = currentBook.optString(WEB_URL);

                // Extract the JSONArray associated with the key called "tags"
                String authorName = NOT_AVAILABLE;
                if (currentBook.has(TAGS)) {
                    JSONArray tagsArray = currentBook.getJSONArray(TAGS);
                    if (!tagsArray.isNull(0)) {
                        // Get the tags at position 0
                        JSONObject currentTag = tagsArray.getJSONObject(0);
                        if (currentBook.has(WEB_TITLE)) {
                            // Extract the Author Name
                            authorName = currentTag.getString(WEB_TITLE);
                        }
                    }
                }
                // Extract the JSONObject associated with the key called "fields",
                JSONObject currentFields = currentBook.getJSONObject(FIELDS);
                // Extract the value for the key called "thumbnailUrl" used to display the article thumbnail
                String thumbnailUrl = currentFields.optString(THUMBNAIL);

                // Create a new {@link Book} object with the sectionName, title, time, authorName,
                // and thumbnailUrl from the JSON response.
                Book book = new Book(sectionName, title, date, authorName, url, thumbnailUrl);

                // Add the new {@link Book} to the list of books.
                books.add(book);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        }
        // Return the list of books
        return books;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

}