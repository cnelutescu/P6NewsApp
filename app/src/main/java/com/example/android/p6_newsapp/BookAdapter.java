package com.example.android.p6_newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * An {@link BookAdapter} knows how to create a list item layout for each Book
 * in the data source (a list of {@link Book} objects).
 * <p>
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user.
 */
public class BookAdapter extends ArrayAdapter<Book> {

    /**
     * Constructs a new {@link BookAdapter}.
     *
     * @param context of the app
     * @param books   is the list of books, which is the data source of the adapter
     */
    public BookAdapter(Context context, List<Book> books) {
        super(context, 0, books);
    }

    /**
     * Returns a list item view that displays information about the book at the given position
     * in the list of books.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_news_list_item, parent, false);
        }

        // Find the book at the given position in the list of books
        Book currentBook = getItem(position);

        // Display the section name of the current book in that TextView
        TextView sectionNameView = (TextView) listItemView.findViewById(R.id.section_name);
        sectionNameView.setText(currentBook.getSectionName());

        // Display the article title of the current book in that TextView
        TextView articleTitleView = (TextView) listItemView.findViewById(R.id.web_title);
        articleTitleView.setText(currentBook.getTitle());

        // Display the author name of the current book in that TextView
        TextView authorNameView = (TextView) listItemView.findViewById(R.id.author_name);
        authorNameView.setText(currentBook.getAuthor());

        // Display the date and time of the current book in that TextView
        TextView dateView = (TextView) listItemView.findViewById(R.id.date);
        String dateTime = currentBook.getDate();
        dateTime = dateTime.replace('T', ' ');
        dateTime = dateTime.substring(0, 16);
        dateView.setText(dateTime);

        // Extra feature implemented: Display the image of the current book news in that ImageView
        // The use of external libraries for the core functionality will not be permitted but
        // this in an extra feature not a core functionality  :)
        String imageUrl = currentBook.getThumbnailUrl();
        // Picasso allows for hassle-free image loading in your application — in one line of code!
        Picasso.get().load(imageUrl).into((ImageView) listItemView.findViewById(R.id.image));

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }

}