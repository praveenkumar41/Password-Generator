package com.example.passwordgenerator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SwitchCompat;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.androdocs.httprequest.HttpRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    TextView password1,password0;
    ImageButton copy0,copy1;
    AppCompatSpinner length;
    SwitchCompat numeric,spchars,upper;
    Button generate;
    RelativeLayout clayout;
    int pnum=0,pspchars=0,pupper=0;
    ArrayList<Integer> numbers = new ArrayList<Integer>();
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        password0 = findViewById(R.id.password0);
        password1 = findViewById(R.id.password1);
        copy0 = findViewById(R.id.copy0);
        copy1 = findViewById(R.id.copy1);
        length = findViewById(R.id.length);
        numeric = findViewById(R.id.num);
        spchars = findViewById(R.id.spchars);
        upper = findViewById(R.id.upper);
        generate = findViewById(R.id.generate);
        clayout = findViewById(R.id.clayout);

        for(int i=8;i<=32;i++) {
            numbers.add(i);
        }
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item, numbers);
        length.setAdapter(adapter);


        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Password Generator");
        mProgressDialog.setMessage("Please wait, we are generating strong password for you...");
        mProgressDialog.setCancelable(true);


        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkinfo = cm.getActiveNetworkInfo();

                if(networkinfo != null && networkinfo.isConnected()==true) {
                    new ApiProcess1().execute();
                }
                else
                {
                    display_internet_label();
                }
            }
        });

        copy0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getclipboard(password0.getText().toString());
            }
        });

        copy1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getclipboard(password1.getText().toString());
            }
        });
    }


    public void getclipboard(String s)
    {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text",s);
        clipboard.setPrimaryClip(clipData);
        Toast.makeText(getApplicationContext(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
    }

    private void display_internet_label()
    {

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("oops!")
                .setMessage("Check your internet connection")
                .setPositiveButton(android.R.string.ok, null)
                .setCancelable(false)
                .create();

        dialog.show();
    }

    public class ApiProcess1 extends AsyncTask<String, Void, String>
    {
        @Override
        protected void onPreExecute() {

            mProgressDialog.show();
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... strings) {

            if(numeric.isChecked())
            {
                pnum = 1;
            }
            else
            {
                pnum = 0;
            }
            if(spchars.isChecked())
            {
                pspchars = 1;
            }
            else
            {
                pspchars =0;
            }

            if(upper.isChecked())
            {
                pupper = 1;
            }
            else{
                pupper =0;
            }

            String response = HttpRequest.excuteGet("https://api.happi.dev/v1/generate-password?apikey=5641a3JTNIuBCWdeWFXrG1EJvsgF6Vf2bq5ANffO7i1fMdxuYytsN8HT&limit=2&length="
                    +length.getSelectedItem().toString()+"&num="+pnum+"&upper="+pupper+"&symbols="+pspchars+"");
            return response;
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                JSONObject jsonObject = new JSONObject(s);
                password0.setText(jsonObject.getJSONArray("passwords").get(0).toString());
                password1.setText(jsonObject.getJSONArray("passwords").get(1).toString());
                mProgressDialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}