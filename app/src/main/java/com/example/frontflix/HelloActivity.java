package com.example.frontflix;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HelloActivity extends AppCompatActivity {
    private static final String TAG = "HelloActivity";
    private EditText editTextMovieTitle;
    private ImageButton imageButtonFetchMovie;
    private RecyclerView recyclerView;
    private Adapter moviesAdapter;
    private ExecutorService executorService;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_inicial);

        editTextMovieTitle = findViewById(R.id.editText);
        imageButtonFetchMovie = findViewById(R.id.imageButton);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        Intent intent = getIntent();
        userEmail = intent.getStringExtra("email");

        moviesAdapter = new Adapter(this, new ArrayList<>(), false, userEmail);
        recyclerView.setAdapter(moviesAdapter);
        executorService = Executors.newSingleThreadExecutor();

        imageButtonFetchMovie.setOnClickListener(v -> {
            String movieTitle = editTextMovieTitle.getText().toString();
            if (!movieTitle.isEmpty()) {
                fetchMovieData(movieTitle).observe(HelloActivity.this, movies -> {
                    if (movies != null && !movies.isEmpty()) {
                        moviesAdapter.setMovieList(movies);
                    } else {
                        Toast.makeText(HelloActivity.this, "No movies found", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(HelloActivity.this, "Por favor, digite o nome de um filme.", Toast.LENGTH_SHORT).show();
            }
        });

        Button buttonSair = findViewById(R.id.button_sair);
        Button buttonFavoritos = findViewById(R.id.button_favoritos);

        buttonSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        buttonFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFavoritos();
            }
        });
    }

    private void logout() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void openFavoritos() {
        Intent intent = new Intent(this, FavoritosActivity.class);
        intent.putExtra("email", userEmail);
        startActivity(intent);
    }

    private LiveData<List<MovieItem>> fetchMovieData(String movieTitle) {
        MutableLiveData<List<MovieItem>> liveData = new MutableLiveData<>();

        executorService.execute(() -> {
            try {
                String encodedTitle = URLEncoder.encode(movieTitle, "UTF-8");
                URL url = new URL("https://api.themoviedb.org/3/search/movie?query=" + encodedTitle + "&language=pt-BR&api_key=YOUR_API_KEY");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI2NjVhZmExM2I4ZjRiZTFlMmUxYjVkN2JkYzlhYzQ0OCIsInN1YiI6IjY2Njg0OTkxOTE0Yjg4OTA3YWU5Zjg0ZSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.uaQuHYRaxcCdeuhIItTGLGStMGybUH0xZx1HVgLJBbk");

                Log.d(TAG, "Request URL: " + url.toString());

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                Log.d(TAG, "Response: " + response.toString());

                JSONObject json = new JSONObject(response.toString());
                JSONArray results = json.getJSONArray("results");

                List<MovieItem> movieList = new ArrayList<>();
                for (int i = 0; i < results.length(); i++) {
                    JSONObject movieJson = results.getJSONObject(i);
                    MovieItem movie = new MovieItem();
                    movie.setId(movieJson.getInt("id"));
                    movie.setTitle(movieJson.getString("title"));
                    String posterPath = movieJson.has("poster_path") ? movieJson.getString("poster_path") : "";
                    movie.setPosterPath(posterPath);
                    String overview = movieJson.has("overview") ? movieJson.getString("overview") : "";
                    movie.setOverview(overview); // Add overview to MovieItem
                    movieList.add(movie);
                }

                liveData.postValue(movieList);
            } catch (Exception e) {
                Log.e(TAG, "Error fetching movie data", e);
                liveData.postValue(null);
            }
        });

        return liveData;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
