package com.example.camerablocker;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private DevicePolicyManager dpm;
    private ComponentName adminComponent;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        adminComponent = new ComponentName(this, DeviceAdmin.class);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(40, 40, 40, 40);

        statusText = new TextView(this);
        statusText.setTextSize(18);
        statusText.setGravity(Gravity.CENTER);
        statusText.setPadding(0, 0, 0, 40);
        updateStatus();

        Button btnAdmin = new Button(this);
        btnAdmin.setText("1. Enable Admin Permission");
        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!dpm.isAdminActive(adminComponent)) {
                        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
                        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "ক্যামেরা ব্লক করার পারমিশন দিন");
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Admin is already active!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        Button btnDisableCamera = new Button(this);
        btnDisableCamera.setText("2. Block Camera");
        btnDisableCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (dpm.isAdminActive(adminComponent)) {
                        dpm.setCameraDisabled(adminComponent, true);
                        Toast.makeText(MainActivity.this, "Camera Blocked Successfully!", Toast.LENGTH_SHORT).show();
                        updateStatus();
                    } else {
                        Toast.makeText(MainActivity.this, "Please enable admin permission first!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Failed to block: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        Button btnEnableCamera = new Button(this);
        btnEnableCamera.setText("3. Unblock Camera");
        btnEnableCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (dpm.isAdminActive(adminComponent)) {
                        dpm.setCameraDisabled(adminComponent, false);
                        Toast.makeText(MainActivity.this, "Camera Unblocked!", Toast.LENGTH_SHORT).show();
                        updateStatus();
                    } else {
                        Toast.makeText(MainActivity.this, "Admin not active", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        layout.addView(statusText);
        layout.addView(btnAdmin);
        layout.addView(btnDisableCamera);
        layout.addView(btnEnableCamera);

        setContentView(layout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatus();
    }

    private void updateStatus() {
        try {
            boolean isAdmin = dpm.isAdminActive(adminComponent);
            boolean isCameraDisabled = isAdmin && dpm.getCameraDisabled(adminComponent);

            if (!isAdmin) {
                statusText.setText("Admin Status: NOT ACTIVE\nClick button 1");
                statusText.setTextColor(Color.RED);
            } else if (isCameraDisabled) {
                statusText.setText("Admin Status: ACTIVE\nCamera: BLOCKED");
                statusText.setTextColor(Color.GREEN);
            } else {
                statusText.setText("Admin Status: ACTIVE\nCamera: ENABLED\nClick button 2");
                statusText.setTextColor(Color.BLUE);
            }
        } catch (Exception e) {
            statusText.setText("Status error: " + e.getMessage());
        }
    }
}
