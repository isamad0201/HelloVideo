package com.example.android.hellovideo;

import java.util.HashSet;

public class UserData {
    public static String userId, name, email;
    public static HashSet<String> likedVideos;

    public static void clear() {
        userId = null;
        name = null;
        email = null;
        likedVideos = null;
    }
}
