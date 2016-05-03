package com.example.giotto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AppSettings extends Activity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    config configuration = new config();

    String ip;
    EditText bdurl, bdport, client_id, client_secret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);

        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();

        bdurl = (EditText) findViewById(R.id.bdurl);
        bdport = (EditText) findViewById(R.id.bdport);

        bdurl.setText(pref.getString("bdurl", "http://128.2.113.192"));
        bdport.setText(pref.getString("bdport", "82"));

        client_id = (EditText) findViewById(R.id.client_id);
        client_secret = (EditText) findViewById(R.id.client_secret);

        editor.putString("client_id", client_id.getText().toString());
        editor.putString("client_secret", client_secret.getText().toString());
        editor.apply();

        final Button finished = (Button) findViewById(R.id.finished);

        finished.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                editor.putString("bdurl", bdurl.getText().toString());
                editor.putString("bdport", bdport.getText().toString());

                ip = bdurl.getText().toString().trim() + ":" + bdport.getText().toString().trim();

                editor.putString("ip", ip);
                editor.apply();

                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);
            }

        });

    }

}
