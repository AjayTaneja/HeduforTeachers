package com.herokuapp.codemasters.heduforparents;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends AppCompatActivity {

    private EditText usernameEt;
    private EditText passwordEt;
    private ImageView loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameEt = (EditText) findViewById(R.id.username);
        passwordEt = (EditText) findViewById(R.id.password);
        loginButton = (ImageView) findViewById(R.id.loginButton);

        final RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "http://codemasters.herokuapp.com/app/student/signin";
        queue.start();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> params = new HashMap<String, String>();
                final String username = usernameEt.getText().toString();
                String password = passwordEt.getText().toString();
                params.put("username", username);
                params.put("password", password);

                final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Toast.makeText(MainActivity.this, response.getJSONObject("school").getString("name"), LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    Toast.makeText(MainActivity.this, "Error in parsing json" + e.toString(), LENGTH_LONG).show();
                                }
                                try {
                                    if (username.equals(response.getString("username"))){

                                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString(TEACHER_NAME_KEY, response.getString("name"));
                                        editor.putString(TEACHER_CLASS_KEY, response.getString("class"));
                                        editor.putString(TEACHER_USERNAME_KEY, response.getString("username"));
                                        editor.putString(TEACHER_SCHOOL_KEY, response.getJSONObject("school").getString("name"));
                                        editor.putString(TEACHER_SCHOOL_ID_KEY, response.getJSONObject("school").getString("id"));
                                        editor.apply();

                                        openNextActivity();

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, "Volley error" + error.toString(), LENGTH_LONG).show();
                            }
                        }
                );
                queue.add(jsonObjectRequest);
            }
        });
    }
}
