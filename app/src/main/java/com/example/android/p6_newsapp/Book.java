package com.example.android.p6_newsapp;

/**
 * An {@link Book} object contains information related to a single earthquake.
 */
public class Book {

    /**
     * Section name of the article
     */
    private String mSectionName;

    /**
     * Date and time of the article
     */
    private String mDate;

    /**
     * Title of the article
     */
    private String mTitle;

    /**
     * Author of the article
     */
    private String mAuthor;

    /**
     * Website URL of the books news
     */
    private String mUrl;

    /**
     * Url for image thumbnail of the article
     */
    private String mThumbnailUrl;

    /**
     * Constructs a new {@link Book} object.
     *
     * @param sectionName  is the Section name of the article
     * @param title        is the Title of the article
     * @param date         is the Date and time of the article
     * @param author       is the Author of the article
     * @param url          is the website URL to find more details about the book news
     * @param thumbnailUrl is the Url for image thumbnail of the article
     */
    public Book(String sectionName, String title, String date, String author, String url, String thumbnailUrl) {
        mSectionName = sectionName;
        mTitle = title;
        mDate = date;
        mAuthor = author;
        mUrl = url;
        mThumbnailUrl = thumbnailUrl;
        // mImage = image;
    }

    /**
     * Returns the sectionName of the article.
     */
    public String getSectionName() {
        return mSectionName;
    }

    /**
     * Returns the Title of the article
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Returns the date and time of the book news article
     */
    public String getDate() {
        return mDate;
    }

    /**
     * Returns the Author of the article
     */
    public String getAuthor() {
        return mAuthor;
    }

    /**
     * Returns the website URL to find more information about the earthquake.
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * Returns the thumbnail for the article
     */
    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

}