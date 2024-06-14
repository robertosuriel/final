package com.example.frontflix;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseManager {
    private final SQLiteDatabase database;

    public DatabaseManager(Context context) {
        database = new DatabaseHelper(context).getWritableDatabase();
    }

    public boolean addUser(String email, String senha) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_EMAIL, email);
        values.put(DatabaseHelper.COLUMN_SENHA, senha);
        values.put(DatabaseHelper.COLUMN_FAVORITOS, ""); // Inicializa a coluna favoritos com uma lista vazia

        long result = database.insert(DatabaseHelper.TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean authenticateUser(String email, String senha) {
        String[] columns = {DatabaseHelper.COLUMN_EMAIL};
        String selection = DatabaseHelper.COLUMN_EMAIL + " = ? AND " + DatabaseHelper.COLUMN_SENHA + " = ?";
        String[] selectionArgs = {email, senha};

        Cursor cursor = database.query(DatabaseHelper.TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        boolean authenticated = cursor.getCount() > 0;
        cursor.close();
        return authenticated;
    }

    public void addFavorite(String email, int movieId) {
        Cursor cursor = database.rawQuery("SELECT " + DatabaseHelper.COLUMN_FAVORITOS + " FROM " + DatabaseHelper.TABLE_USERS + " WHERE " + DatabaseHelper.COLUMN_EMAIL + " = ?", new String[]{email});
        if (cursor.moveToFirst()) {
            String favoritos = cursor.getString(0);
            if (!favoritos.isEmpty()) {
                favoritos += "," + movieId;
            } else {
                favoritos = String.valueOf(movieId);
            }

            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.COLUMN_FAVORITOS, favoritos);

            database.update(DatabaseHelper.TABLE_USERS, contentValues, DatabaseHelper.COLUMN_EMAIL + " = ?", new String[]{email});
        }
        cursor.close();
    }

    public void removeFavorite(String email, int movieId) {
        Cursor cursor = database.rawQuery("SELECT " + DatabaseHelper.COLUMN_FAVORITOS + " FROM " + DatabaseHelper.TABLE_USERS + " WHERE " + DatabaseHelper.COLUMN_EMAIL + " = ?", new String[]{email});
        if (cursor.moveToFirst()) {
            String favoritos = cursor.getString(0);
            List<String> favoritosList = new ArrayList<>(Arrays.asList(favoritos.split(",")));
            favoritosList.remove(String.valueOf(movieId));
            String updatedFavoritos = TextUtils.join(",", favoritosList);

            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.COLUMN_FAVORITOS, updatedFavoritos);

            database.update(DatabaseHelper.TABLE_USERS, contentValues, DatabaseHelper.COLUMN_EMAIL + " = ?", new String[]{email});
        }
        cursor.close();
    }

    public List<Integer> getFavorites(String email) {
        List<Integer> favoriteIds = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT " + DatabaseHelper.COLUMN_FAVORITOS + " FROM " + DatabaseHelper.TABLE_USERS + " WHERE " + DatabaseHelper.COLUMN_EMAIL + " = ?", new String[]{email});
        if (cursor.moveToFirst()) {
            String favoritos = cursor.getString(0);
            if (!favoritos.isEmpty()) {
                String[] favoritosArray = favoritos.split(",");
                for (String id : favoritosArray) {
                    favoriteIds.add(Integer.parseInt(id));
                }
            }
        }
        cursor.close();
        return favoriteIds;
    }
}
