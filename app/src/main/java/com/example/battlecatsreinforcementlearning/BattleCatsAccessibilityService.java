package com.example.battlecatsreinforcementlearning;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.Random;

public class BattleCatsAccessibilityService extends AccessibilityService {

    private static final String TAG = "BattleCatsAI";
    private static volatile BattleCatsAccessibilityService instance;

    private boolean isBotRunning = false;
    private Handler handler;
    private Runnable gameLoop;
    private Random random;

    // Game state variables
    private static final int SCREEN_WIDTH = 1080;
    private static final int SCREEN_HEIGHT = 1920;
    private long lastActionTime = 0;
    private static final long ACTION_DELAY = 2000; // 2 seconds between actions

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
        handler = new Handler(Looper.getMainLooper());
        random = new Random();

        Log.d(TAG, "Accessibility service connected");

        // Initialize game loop
        gameLoop = () -> {
            if (isBotRunning) {
                processGameState();
                handler.postDelayed(gameLoop, 500); // Check every 500ms
            }
        };
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!isBotRunning) return;

        // Log accessibility events for debugging
        if (event.getPackageName() != null && "jp.co.ponos.battlecats".contentEquals(event.getPackageName())) {
            Log.d(TAG, "Battle Cats event: " + event.getEventType());
        }
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "Accessibility service interrupted");
    }

    public static BattleCatsAccessibilityService getInstance() {
        return instance;
    }

    public void startBot() {
        isBotRunning = true;
        Log.d(TAG, "Bot started");
        handler.post(gameLoop);
    }

    public void stopBot() {
        isBotRunning = false;
        Log.d(TAG, "Bot stopped");
        handler.removeCallbacks(gameLoop);
    }

    private void processGameState() {
        try {
            // Get current screen content
            AccessibilityNodeInfo rootNode = getRootInActiveWindow();
            if (rootNode == null) return;

            // Simple AI logic for now - this is where RL will go later
            performBasicGameActions();

        } catch (Exception e) {
            Log.e(TAG, "Error processing game state", e);
        }
    }

    private void performBasicGameActions() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastActionTime < ACTION_DELAY) {
            return; // Too soon since last action
        }

        // Simple random actions for now (replace with RL later)
        int action = random.nextInt(4);

        switch (action) {
            case 0:
                // Deploy basic cat (bottom left area)
                performTap(200, SCREEN_HEIGHT - 200);
                Log.d(TAG, "Deployed basic cat");
                break;
            case 1:
                // Deploy tank cat (second from left)
                performTap(400, SCREEN_HEIGHT - 200);
                Log.d(TAG, "Deployed tank cat");
                break;
            case 2:
                // Deploy ranged cat (middle)
                performTap(600, SCREEN_HEIGHT - 200);
                Log.d(TAG, "Deployed ranged cat");
                break;
            case 3:
                // Use cat cannon (if available)
                performTap(SCREEN_WIDTH - 100, 200);
                Log.d(TAG, "Used cat cannon");
                break;
        }

        lastActionTime = currentTime;
    }

    private void performTap(float x, float y) {
        Path path = new Path();
        path.moveTo(x, y);

        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(path, 0, 100));

        dispatchGesture(gestureBuilder.build(), new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                Log.d(TAG, "Tap completed at (" + x + ", " + y + ")");
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                Log.d(TAG, "Tap cancelled");
            }
        }, null);
    }

    // Method to capture screen for RL training data
    public Bitmap captureScreen() {
        // This would capture the current screen
        // Implementation depends on your screen capture method
        // For now, return null - we'll implement this in the next phase
        return null;
    }

    // Method to get current game state features
    public GameState getCurrentGameState() {
        // Extract relevant features from the screen
        // This will be crucial for RL training
        return new GameState();
    }

    // Simple game state class
    public static class GameState {
        public int money = 0;
        public int enemyCount = 0;
        public int catCount = 0;
        public boolean cannonReady = false;
        public float baseHealth = 100f;
        public float enemyBaseHealth = 100f;

        // Add more state variables as needed
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
        stopBot();
    }
}