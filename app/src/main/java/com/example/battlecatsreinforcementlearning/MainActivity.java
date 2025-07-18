package com.example.battlecatsreinforcementlearning;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private TextView statusText;
    private Button enableAccessibilityButton;
    private Button startBotButton;
    private Button stopBotButton;
    private Button overlayPermissionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupButtonListeners();
        updateStatus();
    }

    private void initializeViews() {
        statusText = findViewById(R.id.statusText);
        enableAccessibilityButton = findViewById(R.id.enableAccessibilityButton);
        startBotButton = findViewById(R.id.startBotButton);
        stopBotButton = findViewById(R.id.stopBotButton);
        overlayPermissionButton = findViewById(R.id.overlayPermissionButton);
    }

    private void setupButtonListeners() {
        enableAccessibilityButton.setOnClickListener(v -> openAccessibilitySettings());

        overlayPermissionButton.setOnClickListener(v -> requestOverlayPermission());

        startBotButton.setOnClickListener(v -> startBot());

        stopBotButton.setOnClickListener(v -> stopBot());
    }

    private void openAccessibilitySettings() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
        Toast.makeText(this, "Please enable 'Battle Cats RL Bot' in Accessibility settings", Toast.LENGTH_LONG).show();
    }

    private void requestOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        } else {
            Toast.makeText(this, "Overlay permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void startBot() {
        if (isAccessibilityServiceEnabled() && Settings.canDrawOverlays(this)) {
            Intent serviceIntent = new Intent(this, BattleCatsAIService.class);
            startService(serviceIntent);

            // Signal the accessibility service to start
            if (BattleCatsAccessibilityService.getInstance() != null) {
                BattleCatsAccessibilityService.getInstance().startBot();
            }

            Toast.makeText(this, "Bot started! Switch to Battle Cats", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please enable accessibility service and overlay permission first", Toast.LENGTH_LONG).show();
        }
    }

    private void stopBot() {
        Intent serviceIntent = new Intent(this, BattleCatsAIService.class);
        stopService(serviceIntent);

        if (BattleCatsAccessibilityService.getInstance() != null) {
            BattleCatsAccessibilityService.getInstance().stopBot();
        }

        Toast.makeText(this, "Bot stopped", Toast.LENGTH_SHORT).show();
    }

    private boolean isAccessibilityServiceEnabled() {
        return BattleCatsAccessibilityService.getInstance() != null;
    }

    private void updateStatus() {
        String status = "Status:\n";
        status += "Accessibility Service: " + (isAccessibilityServiceEnabled() ? "✓ Enabled" : "✗ Disabled") + "\n";
        status += "Overlay Permission: " + (Settings.canDrawOverlays(this) ? "✓ Granted" : "✗ Not granted") + "\n";
        statusText.setText(status);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatus();
    }
}