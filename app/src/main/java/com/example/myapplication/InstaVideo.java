package com.example.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

public class InstaVideo {

    public static void downloadVideo(final Activity context, String postUrl, final ImageView instaImg, final TextView textView, final Boolean bool, final ProgressDialog dialog) {

        String replacedUrl;
        final String[] finalVideoUrl = new String[1];

        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(context);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    postUrl, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    JSONObject Obj1 = null;
                    try {
                        Obj1 = response.getJSONObject("graphql");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JSONObject Obj2 = null;
                    try {
                        Obj2 = Obj1.getJSONObject("shortcode_media");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        finalVideoUrl[0] = Obj2.getString("video_url");
                        Glide.with(context)
                                .load(finalVideoUrl[0])
                                .into(instaImg);
                        textView.setText("Downloading..");
                        dialog.setTitle("Downloading..");
                        Util.download(finalVideoUrl[0], Util.RootDirectoryInstagram, context, System.currentTimeMillis() + ".mp4",dialog);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (bool){
                            context.finish();
                        }
                        try {
                            finalVideoUrl[0] = Obj2.getString("display_url");
                            Glide.with(context)
                                    .load(finalVideoUrl[0])
                                    .into(instaImg);
                            textView.setText("Downloading..");
                            dialog.setTitle("Downloading..");
                            Util.download(finalVideoUrl[0], Util.RootDirectoryInstagram, context, System.currentTimeMillis() + ".png",dialog);
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                        }
                        Toast.makeText(context, "Photo is Downloaded", Toast.LENGTH_SHORT).show();
                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VideoURLErrors", "Something went wrong" + error);
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                    if (bool){
                        context.finish();
                    }
                }
            });

            requestQueue.add(jsonObjectRequest);

        }


    }
