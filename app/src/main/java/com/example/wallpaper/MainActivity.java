package com.example.wallpaper;

import android.Manifest;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PICK_IMAGES_CODE = 1;

    private Button btnPickImage, btnSetBackground;
    private ImageView imageView;
    private ArrayList<Uri> imageUris;
    private SharedPreferences sharedPreferences;
    private UnlockReceiver unlockReceiver;
    private LockScreenReceiver lockScreenReceiver;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) pickImageIntent();
                else Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize components
        imageUris = new ArrayList<>();
        sharedPreferences = getSharedPreferences("WallpaperPrefs", MODE_PRIVATE);
        unlockReceiver = new UnlockReceiver();
        lockScreenReceiver = new LockScreenReceiver();

        // Initialize views
        btnPickImage = findViewById(R.id.btnPickImage);
        btnSetBackground = findViewById(R.id.setbackground);
        imageView = findViewById(R.id.imageView);

        // Register receivers
        try {
            registerReceiver(unlockReceiver, new IntentFilter(Intent.ACTION_USER_PRESENT));
            lockScreenReceiver.register(this);
        } catch (Exception e) {
            Log.e(TAG, "Receiver registration error: " + e.getMessage());
        }

        // Start foreground service
        startService(new Intent(this, WallpaperService.class));

        // Request battery optimization exemption
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (pm != null && !pm.isIgnoringBatteryOptimizations(getPackageName())) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }

        // Button click listeners
        btnPickImage.setOnClickListener(v -> {
            if (isImagePermissionGranted()) pickImageIntent();
            else requestImagePermission();
        });

        btnSetBackground.setOnClickListener(v -> setWallpaperBoth());
    }

    private boolean isImagePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestImagePermission() {
        String permission = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;
        requestPermissionLauncher.launch(permission);
    }

    private void pickImageIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Images"), PICK_IMAGES_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGES_CODE && resultCode == Activity.RESULT_OK && data != null) {
            imageUris.clear();
            try {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri uri = data.getClipData().getItemAt(i).getUri();
                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        imageUris.add(uri);
                    }
                } else if (data.getData() != null) {
                    Uri uri = data.getData();
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    imageUris.add(uri);
                }

                // Save URIs to SharedPreferences
                Set<String> uriStrings = new HashSet<>();
                for (Uri uri : imageUris) {
                    uriStrings.add(uri.toString());
                }
                sharedPreferences.edit()
                        .putStringSet("imageUris", uriStrings)
                        .putInt("currentIndex", 0)
                        .apply();

                displayFirstImage();
            } catch (SecurityException e) {
                Toast.makeText(this, "Permission Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void displayFirstImage() {
        if (!imageUris.isEmpty() && imageView != null) {
            imageView.setImageURI(imageUris.get(0));
        }
    }

    private void setWallpaperBoth() {
        try {
            int currentIndex = sharedPreferences.getInt("currentIndex", 0);
            if (currentIndex >= imageUris.size()) currentIndex = 0;

            Uri currentUri = imageUris.get(currentIndex);
            WallpaperManager wm = WallpaperManager.getInstance(this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), currentUri);
                wm.setBitmap(bitmap);
            }

            updateIndex(currentIndex);
            Toast.makeText(this, "Both Screens Set!", Toast.LENGTH_SHORT).show();

        } catch (IOException | SecurityException | IndexOutOfBoundsException e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void updateIndex(int currentIndex) {
        int newIndex = (currentIndex + 1) % imageUris.size();
        sharedPreferences.edit().putInt("currentIndex", newIndex).apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(unlockReceiver);
            unregisterReceiver(lockScreenReceiver);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Receiver not registered: " + e.getMessage());
        }
    }
}