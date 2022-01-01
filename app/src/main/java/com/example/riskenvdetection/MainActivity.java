package com.example.riskenvdetection;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.riskenvdetection.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private ActivityMainBinding binding;

    private Button mDetectDual, mDetectPosed;
    private TextView mResult;

    PosedDetection mPosedDetection = new PosedDetection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mResult = (TextView) findViewById(R.id.result);
        mDetectDual = (Button) findViewById(R.id.dual);
        mDetectPosed = (Button) findViewById(R.id.posed);

        mDetectDual.setOnClickListener(this);
        mDetectPosed.setOnClickListener(this);

    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */

    @Override
    public void onClick(View view) {
        String result = null;
        switch (view.getId()) {
            case R.id.dual:
                result = DualOpenDection.detect(this);
                break;
            case R.id.posed:
                result = mPosedDetection.detect();
                break;
            default:
                return;
        }
        mResult.setText(result);
    }
}