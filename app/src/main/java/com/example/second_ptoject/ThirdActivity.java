package com.example.second_ptoject;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class ThirdActivity extends AppCompatActivity implements SensorEventListener {

    TextView tvStatus, tvCenterMsg, tvInstruction;
    android.view.View headView;
    Button btnApprove, btnReject;
    LinearLayout llButtons;

    SensorManager sensorManager;
    Sensor lightSensor;
    float lastLux = 10000f;

    CameraManager cameraManager;
    String cameraIdWithFlash = null;
    boolean flashOn = false;

    ActivityResultLauncher<String> cameraPermissionLauncher;
    Drawable originalBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        tvStatus = findViewById(R.id.tvStatus3);
        tvCenterMsg = findViewById(R.id.tvCenterMsg3);
        tvInstruction = findViewById(R.id.tvInstruction3);
        headView = findViewById(R.id.headView3);
        btnApprove = findViewById(R.id.btnApprove3);
        btnReject = findViewById(R.id.btnReject3);
        llButtons = findViewById(R.id.llButtons3);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }

        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        originalBackground = findViewById(R.id.layoutRoot3).getBackground();

        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) {
                        findCameraWithFlash();
                        tvStatus.setText("Kamera izni verildi. Ortam kontrolü aktif.");
                    } else {
                        tvStatus.setText("Kamera izni yok; flaş kullanılamayacak.");
                    }
                });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            findCameraWithFlash();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }

        tvCenterMsg.setText("Kullanıcının söyledikleri gösterilecek.\n\nOnaylarsanız başınızı sağa yatırın, onaylamazsanız sola.");
        tvInstruction.setText("Lütfen metni kontrol edin ve onay/verin.");
        llButtons.setVisibility(LinearLayout.VISIBLE);

        btnApprove.setOnClickListener(v -> {
            performTiltRight();
            tvStatus.setText("Kullanıcı onayladı — baş sağa yatırıldı.");
            if (flashOn) setFlash(false);
        });

        btnReject.setOnClickListener(v -> {
            performTiltLeft();
            tvStatus.setText("Kullanıcı onaylamadı — baş sola yatırıldı.");
            if (flashOn) setFlash(false);
        });
    }

    private void findCameraWithFlash() {
        try {
            if (cameraManager == null) return;
            for (String id : cameraManager.getCameraIdList()) {
                CameraCharacteristics c = cameraManager.getCameraCharacteristics(id);
                Boolean hasFlash = c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                if (hasFlash != null && hasFlash) {
                    cameraIdWithFlash = id;
                    break;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lightSensor != null && sensorManager != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(android.hardware.SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            lastLux = event.values[0];
            tvStatus.setText(String.format("Işık: %.1f lx", lastLux));
            if (lastLux < 10f && cameraIdWithFlash != null && !flashOn) {
                setFlash(true);
                tvStatus.setText("Karanlık algılandı, flaş açıldı.");
            }
        }
    }

    private void setFlash(boolean on) {
        if (cameraIdWithFlash == null) return;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Kamera izni yok - flaş kullanılamaz.", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            cameraManager.setTorchMode(cameraIdWithFlash, on);
            flashOn = on;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void performTiltRight() {
        ObjectAnimator anim = ObjectAnimator.ofFloat(headView, "rotation", 0f, 25f);
        anim.setDuration(600);
        anim.start();
    }

    private void performTiltLeft() {
        ObjectAnimator anim = ObjectAnimator.ofFloat(headView, "rotation", 0f, -25f);
        anim.setDuration(600);
        anim.start();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (flashOn) setFlash(false);
    }
}
