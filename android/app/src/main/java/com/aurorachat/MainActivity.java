package com.aurorachat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

    private EditText edText1;
    private Button btn1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        edText1=(EditText)findViewById(R.id.edText1);
        btn1=(Button)findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,TReactActivity.class);
                intent.putExtra("component",edText1.getText()!=null?edText1.getText().toString():"");
                startActivity(intent);
            }
        });
    }
}
