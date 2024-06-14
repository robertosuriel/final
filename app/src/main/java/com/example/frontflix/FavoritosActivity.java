package com.example.frontflix;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoritosActivity extends AppCompatActivity {
    private static final String TAG = "FavoritosActivity";
    private static final String API_KEY = "YOUR_API_KEY"; // Substitua pela sua chave de API do TMDB
    private static final String BEARER_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI2NjVhZmExM2I4ZjRiZTFlMmUxYjVkN2JkYzlhYzQ0OCIsInN1YiI6IjY2Njg0OTkxOTE0Yjg4OTA3YWU5Zjg0ZSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.uaQuHYRaxcCdeuhIItTGLGStMGybUH0xZx1HVgLJBbk";
    private RecyclerView recyclerView;
    private Adapter favoritesAdapter;
    private String userEmail;
    private DatabaseManager databaseManager;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favoritos);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // Use 3 columns in grid layout

        Button buttonVoltar = findViewById(R.id.button7);
        buttonVoltar.setOnClickListener(v -> finish());

        // Obtém o email do usuário do Intent
        Intent intent = getIntent();
        userEmail = intent.getStringExtra("email");

        databaseManager = new DatabaseManager(this);
        executorService = Executors.newSingleThreadExecutor();

        loadFavoriteMovies();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavoriteMovies();
    }

    private void loadFavoriteMovies() {
        List<Integer> favoriteIds = databaseManager.getFavorites(userEmail);
        if (favoriteIds.isEmpty()) {
            Toast.makeText(this, "Nenhum filme favorito encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        getFavoriteMoviesByIds(favoriteIds).observe(this, movies -> {
            if (movies != null && !movies.isEmpty()) {
                favoritesAdapter = new Adapter(FavoritosActivity.this, movies, true, userEmail);
                recyclerView.setAdapter(favoritesAdapter);
            } else {
                Toast.makeText(FavoritosActivity.this, "Erro ao carregar detalhes dos filmes favoritos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private LiveData<List<MovieItem>> getFavoriteMoviesByIds(List<Integer> favoriteIds) {
        MutableLiveData<List<MovieItem>> liveData = new MutableLiveData<>();
        List<MovieItem> favoriteMovies = new ArrayList<>();

        for (int movieId : favoriteIds) {
            executorService.execute(() -> {
                try {
                    URL url = new URL("https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" + API_KEY + "&language=pt-BR");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setRequestProperty("Authorization", "Bearer " + BEARER_TOKEN);

                    Log.d(TAG, "Request URL: " + url.toString());

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    br.close();

                    Log.d(TAG, "Response: " + response.toString());

                    JSONObject movieJson = new JSONObject(response.toString());
                    MovieItem movie = new MovieItem(
                            movieJson.getInt("id"),
                            movieJson.getString("title"),
                            movieJson.getString("poster_path"),
                            movieJson.getString("overview")
                    );

                    synchronized (favoriteMovies) {
                        favoriteMovies.add(movie);
                        if (favoriteMovies.size() == favoriteIds.size()) {
                            liveData.postValue(favoriteMovies);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error fetching movie data", e);
                }
            });
        }

        return liveData;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
