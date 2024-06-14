package com.example.frontflix;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class YouTubeResponse {
    @SerializedName("items")
    public List<Item> items;

    public static class Item {
        @SerializedName("id")
        public Id id;

        public static class Id {
            @SerializedName("videoId")
            public String videoId;
        }
    }
}

