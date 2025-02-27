package com.example.wallpaper;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class UnlockReceiver extends BroadcastReceiver {
    private static final String TAG = "UnlockReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            resetIndex(context);
            return;
        }

        if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
            changeHomeScreenWallpaper(context);
        }
    }

    private void resetIndex(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("WallpaperPrefs", Context.MODE_PRIVATE);
        prefs.edit().putInt("currentIndex", 0).apply();
    }

    private void changeHomeScreenWallpaper(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences("WallpaperPrefs", Context.MODE_PRIVATE);
            Set<String> uriStrings = prefs.getStringSet("imageUris", new HashSet<>());

            if (uriStrings.isEmpty()) return;

            int index = prefs.getInt("currentIndex", 0) % uriStrings.size();
            Uri currentUri = Uri.parse(uriStrings.toArray(new String[0])[index]);

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), currentUri);
            WallpaperManager wm = WallpaperManager.getInstance(context);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                wm.clear(WallpaperManager.FLAG_SYSTEM);
                wm.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM);
            }

            // Update index for next wallpaper
            int newIndex = (index + 1) % uriStrings.size();
            prefs.edit().putInt("currentIndex", newIndex).apply();

        } catch (IOException | SecurityException | IllegalArgumentException e) {
            Log.e(TAG, "Error: " + e.getMessage());
            Toast.makeText(context, "Auto-home screen change failed", Toast.LENGTH_SHORT).show();
        }
    }
}