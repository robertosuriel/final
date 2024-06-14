package com.example.frontflix;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class DetailsActivity extends AppCompatActivity {
    private TextView tvTitle, tvOverview;
    private ImageView imgPoster;
    private Button btnAddToFavorites, btnRemoveFromFavorites, btnTrailer, btnBack;
    private String movieTitle, movieOverview, moviePosterPath;
    private int movieId;
    private String userEmail;
    private DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_page);

        tvTitle = findViewById(R.id.movieTitle);
        tvOverview = findViewById(R.id.movieOverview);
        imgPoster = findViewById(R.id.moviePoster);
        btnAddToFavorites = findViewById(R.id.btnAddToFavorites);
        btnRemoveFromFavorites = findViewById(R.id.btnRemoveFromFavorites);
        btnTrailer = findViewById(R.id.btnTrailer);
        btnBack = findViewById(R.id.btnBack);

        // Obtém os detalhes do filme do Intent
        Intent intent = getIntent();
        movieId = intent.getIntExtra("movie_id", -1);
        movieTitle = intent.getStringExtra("movie_title");
        movieOverview = intent.getStringExtra("movie_overview");
        moviePosterPath = intent.getStringExtra("movie_poster_path");
        userEmail = intent.getStringExtra("user_email");

        // Exibe os detalhes do filme
        tvTitle.setText(movieTitle);
        tvOverview.setText(movieOverview);
        Glide.with(this).load("https://image.tmdb.org/t/p/w500" + moviePosterPath).into(imgPoster);

        dbManager = new DatabaseManager(this);

        // Verifica se o filme está nos favoritos
        boolean isFavorite = dbManager.getFavorites(userEmail).contains(movieId);
        btnAddToFavorites.setVisibility(isFavorite ? View.GONE : View.VISIBLE);
        btnRemoveFromFavorites.setVisibility(isFavorite ? View.VISIBLE : View.GONE);

        // Adiciona o filme aos favoritos
        btnAddToFavorites.setOnClickListener(v -> {
            dbManager.addFavorite(userEmail, movieId);
            btnAddToFavorites.setVisibility(View.GONE);
            btnRemoveFromFavorites.setVisibility(View.VISIBLE);
        });

        // Remove o filme dos favoritos
        btnRemoveFromFavorites.setOnClickListener(v -> {
            dbManager.removeFavorite(userEmail, movieId);
            btnAddToFavorites.setVisibility(View.VISIBLE);
            btnRemoveFromFavorites.setVisibility(View.GONE);
        });

        // Abre o YouTube para procurar o trailer
        btnTrailer.setOnClickListener(v -> {
            String query = "trailer " + movieTitle;
            Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/results?search_query=" + Uri.encode(query)));
            startActivity(youtubeIntent);
        });

        // Voltar para a tela anterior
        btnBack.setOnClickListener(v -> finish());
    }
}
