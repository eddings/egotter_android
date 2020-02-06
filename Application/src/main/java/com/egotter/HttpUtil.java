package com.egotter;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {

    public static final String TAG = HttpUtil.class.getSimpleName();
    public static final String URL = "https://egotter.com/api/v1/users/update_instance_id";

    public static void sendInstanceIdToServer(String uid, String instanceId, String accessToken, String accessSecret) {

        try {
            JSONObject json = new JSONObject();
            json.put("uid", uid);
            json.put("instance_id", instanceId);
            json.put("access_token", accessToken);

            new HttpTask(URL, json).execute();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    static class HttpTask extends AsyncTask<Integer, Integer, String> {
        private String url;
        private JSONObject json;

        public HttpTask(String url, JSONObject json) {
            this.url = url;
            this.json = json;
        }

        @Override
        protected String doInBackground(Integer... params) {
            try {
                post(url, json);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "Task Completed.";
        }

        private JSONObject post(String url, JSONObject json) throws IOException, JSONException {
            Log.d(TAG, "post() " + URL + " " + json.toString());

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setInstanceFollowRedirects(false);
            conn.setReadTimeout(3000);
            conn.setConnectTimeout(5000);
            conn.addRequestProperty("User-Agent", "android_egotter");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();

            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.write(json.toString().getBytes("UTF-8"));
            os.flush();
            os.close();

            BufferedReader bufferedReader;
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                bufferedReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
            bufferedReader.close();
            conn.disconnect();

            String jsonString = stringBuilder.toString();
            Log.d(TAG, "post() response = " + jsonString);

            return new JSONObject(jsonString);
        }
    }
}