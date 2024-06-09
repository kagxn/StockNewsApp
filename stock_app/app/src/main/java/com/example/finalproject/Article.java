package com.example.finalproject;

import java.io.Serializable;

public class Article implements Serializable {
    private String title;
    private String description;
    private String snippet;
    private String url;
    private String imageurl;
    private String publishedat;
    private String source;
    private String symbol;
    private String name;
    private double sentimentscore;
    private String highlights;

    // Boş kurucu (Firestore tarafından gereklidir)
    public Article() {}

    // Parametreli kurucu
    public Article(String title, String description, String snippet, String url, String imageurl,
                   String publishedat, String source, String symbol, String name,
                   double sentimentscore, String highlights) {
        this.title = title;
        this.description = description;
        this.snippet = snippet;
        this.url = url;
        this.imageurl = imageurl;
        this.publishedat = publishedat;
        this.source = source;
        this.symbol = symbol;
        this.name = name;
        this.sentimentscore = sentimentscore;
        this.highlights = highlights;
    }

    // Getter ve Setter metodları
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSnippet() { return snippet; }
    public void setSnippet(String snippet) { this.snippet = snippet; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getImageUrl() { return imageurl; }
    public void setImageUrl(String image_url) { this.imageurl = image_url; }

    public String getPublishedAt() { return publishedat; }
    public void setPublishedAt(String published_at) { this.publishedat = published_at; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getSentimentScore() { return sentimentscore; }
    public void setSentimentScore(double sentiment_score) { this.sentimentscore = sentiment_score; }

    public String getHighlights() { return highlights; }
    public void setHighlights(String highlights) { this.highlights = highlights; }
}
