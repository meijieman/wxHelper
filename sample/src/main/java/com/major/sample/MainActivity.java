package com.major.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText mText;
    private TextView mHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_send).setOnClickListener(v->{
            String text = mText.getText().toString().trim();
            if (text.length()!=0) {
                mHistory.append(text + "\n");
                mText.setText("");
            } else {
                Toast.makeText(this, "输入内容为空", Toast.LENGTH_SHORT).show();
            }

        });

        mHistory = findViewById(R.id.tv_history);
        mText = findViewById(R.id.et_main);








    }
}
