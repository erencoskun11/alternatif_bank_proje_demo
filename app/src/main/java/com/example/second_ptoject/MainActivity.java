package com.example.second_ptoject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnStart;
    TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = findViewById(R.id.btnStart);
        tvStatus = findViewById(R.id.tvStatus);

        btnStart.setText("Dudak okumayı başlat"); // büyük buton metni

        btnStart.setOnClickListener(v -> {
            tvStatus.setText("Ağız okuma başlatıldı");
            btnStart.setEnabled(false);
            // hemen 2. ekrana geç
            startActivity(new Intent(MainActivity.this, SecondActivity.class));
            btnStart.setEnabled(true);
        });
    }
}


