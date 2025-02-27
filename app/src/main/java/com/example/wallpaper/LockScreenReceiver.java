package com.example.wallpaper;

import android.app.KeyguardManager;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class LockScreenReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
            KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            if (keyguardManager != null && keyguardManager.isKeyguardLocked()) {
                setLockScreenWallpaper(context);
            }
        }
    }

    private void setLockScreenWallpaper(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("WallpaperPrefs", Context.MODE_PRIVATE);
        Set<String> uriStrings = prefs.getStringSet("imageUris", new HashSet<>());
        ArrayList<Uri> images = new ArrayList<>();

        for (String s : uriStrings) {
            images.add(Uri.parse(s));
        }

        if (images.isEmpty()) return;

        int index = prefs.getInt("currentIndex", 0) % images.size();

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), images.get(index));
            WallpaperManager wm = WallpaperManager.getInstance(context);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                wm.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);
            }

            int newIndex = (index + 1) % images.size();
            prefs.edit().putInt("currentIndex", newIndex).apply();

        } catch (IOException | SecurityException e) {
            Log.e("LockScreenReceiver", "Wallpaper change failed: " + e.getMessage());
        }
    }

    public void register(Context context) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        context.registerReceiver(this, filter);
    }
}