package com.example.second_ptoject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    TextView tvBlack;
    Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        tvBlack = findViewById(R.id.tvBlackScreen);
        tvBlack.setText("Dudak okuma başlatıldı");
        // 5 saniye bekle, sonra 3. ekrana geç
        handler.postDelayed(() -> {
            startActivity(new Intent(SecondActivity.this, ThirdActivity.class));
            finish();
        }, 5000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
