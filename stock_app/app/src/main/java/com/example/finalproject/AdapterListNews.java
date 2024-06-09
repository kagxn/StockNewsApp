package com.example.finalproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdapterListNews extends RecyclerView.Adapter<AdapterListNews.ArticleViewHolder> {



    private List<Article> articleList;
    private Context context;

    public AdapterListNews(List<Article> articleList, Context context) {
        this.articleList = articleList;
        this.context = context;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Article article = articleList.get(position);
        holder.titleTextView.setText(article.getTitle());
        holder.snippetTextView.setText(article.getSnippet());
        holder.pusblishDate.setText(getDateTime(article.getPublishedAt()));
        holder.source.setText(article.getSource());
        Picasso.get().load(article.getImageUrl()).into(holder.articleImageView);

        // Sentiment değerini al ve yuvarlak view'in rengini ayarla
        double sentimentValue = article.getSentimentScore(); // Article sınıfınıza sentiment değerini ekleyin
        updateSentimentIndicator(holder.sentimentIndicatorView, holder.sentimentTextView, sentimentValue);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*String url = article.getUrl();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
                Toast.makeText(context, article.getPublishedAt() + "", Toast.LENGTH_SHORT).show(); */
                Intent intent = new Intent(holder.itemView.getContext(), NewsFragment.class);
                intent.putExtra("news", articleList.get(holder.getAdapterPosition()));
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    public static class ArticleViewHolder extends RecyclerView.ViewHolder {
        public ImageView articleImageView;
        public TextView titleTextView;
        public TextView snippetTextView;
        public View sentimentIndicatorView;
        public TextView pusblishDate;
        public TextView source;
        public TextView sentimentTextView; // Sentiment TextView

        public ArticleViewHolder(View itemView) {
            super(itemView);
            /*articleImageView = itemView.findViewById(R.id.articleImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            snippetTextView = itemView.findViewById(R.id.snippetTextView);
            sentimentIndicatorView = itemView.findViewById(R.id.sentimentIndicatorView);
            sentimentTextView = itemView.findViewById(R.id.sentimentTextView);*/
            articleImageView = itemView.findViewById(R.id.newsImage);
            titleTextView = itemView.findViewById(R.id.newsTitle);
            snippetTextView = itemView.findViewById(R.id.newsDescription);
            sentimentIndicatorView = itemView.findViewById(R.id.sentimentIndicatorView);
            sentimentTextView = itemView.findViewById(R.id.sentimentTextView);
            pusblishDate = itemView.findViewById(R.id.newsPublishDate);
            source = itemView.findViewById(R.id.newsSourceName);

        }
    }

    private void updateSentimentIndicator(View sentimentIndicatorView, TextView sentimentTextView, double sentimentValue) {
        if (sentimentValue >= 0.5) {
            sentimentIndicatorView.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_shape_green));
            sentimentTextView.setText("Positive");

        } else if (sentimentValue <= -0.5) {
            sentimentIndicatorView.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_shape_red));
            sentimentTextView.setText("Negative");

        } else {
            sentimentIndicatorView.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_shape_gray));
            sentimentTextView.setText("Neutral");

        }
    }
    public static String getDateTime(String dateTimeString) {
        // ISO 8601 formatına göre datetime string'i LocalDateTime'a parse ediyoruz
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME);

        // İstediğimiz formatta datetime string'ini oluşturuyoruz
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }
}
