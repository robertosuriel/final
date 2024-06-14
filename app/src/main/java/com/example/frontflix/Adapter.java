package com.example.frontflix;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.MovieViewHolder> {
    private Context context;
    private List<MovieItem> movieList;
    private boolean isFavoriteScreen;
    private String userEmail;

    public Adapter(Context context, List<MovieItem> movieList, boolean isFavoriteScreen, String userEmail) {
        this.context = context;
        this.movieList = movieList;
        this.isFavoriteScreen = isFavoriteScreen;
        this.userEmail = userEmail;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_sugestoes, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        MovieItem movie = movieList.get(position);
        holder.tvMovieTitle.setText(movie.getTitle());
        Glide.with(context).load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath()).into(holder.imgPoster);

        holder.imgPoster.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailsActivity.class);
            intent.putExtra("movie_id", movie.getId());
            intent.putExtra("movie_title", movie.getTitle());
            intent.putExtra("movie_overview", movie.getOverview());
            intent.putExtra("movie_poster_path", movie.getPosterPath());
            intent.putExtra("user_email", userEmail);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public void setMovieList(List<MovieItem> movieList) {
        this.movieList = movieList;
        notifyDataSetChanged();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPoster;
        TextView tvMovieTitle;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.imgUrl);
            tvMovieTitle = itemView.findViewById(R.id.name);
        }
    }
}
