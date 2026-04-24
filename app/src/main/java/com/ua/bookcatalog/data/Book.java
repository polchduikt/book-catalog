package com.ua.bookcatalog.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "books")
public class Book implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String author;
    private int year;
    private String genre;
    private int pages;
    private float rating;
    private boolean favorite;

    public Book(String title, String author, int year, String genre, int pages) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.genre = genre;
        this.pages = pages;
        this.rating = 0f;
        this.favorite = false;
    }

    protected Book(Parcel in) {
        id = in.readInt();
        title = in.readString();
        author = in.readString();
        year = in.readInt();
        genre = in.readString();
        pages = in.readInt();
        rating = in.readFloat();
        favorite = in.readByte() != 0;
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(author);
        dest.writeInt(year);
        dest.writeString(genre);
        dest.writeInt(pages);
        dest.writeFloat(rating);
        dest.writeByte((byte) (favorite ? 1 : 0));
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getYear() { return year; }
    public String getGenre() { return genre; }
    public int getPages() { return pages; }
    public float getRating() { return rating; }
    public boolean isFavorite() { return favorite; }

    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setYear(int year) { this.year = year; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setPages(int pages) { this.pages = pages; }
    public void setRating(float rating) { this.rating = rating; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }
}
