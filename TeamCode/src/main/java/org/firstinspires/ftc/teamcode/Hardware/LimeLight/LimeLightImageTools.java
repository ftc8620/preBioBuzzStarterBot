package org.firstinspires.ftc.teamcode.Hardware.LimeLight;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.hardware.limelightvision.Limelight3A;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class LimeLightImageTools {
    Limelight3A limeLight;



    public LimeLightImageTools(Limelight3A limeLight) {
        this.limeLight = limeLight;
    }

    public boolean SendNewSnapshotToDashboard() {
        String snapShotName = "snapshot";
        FtcDashboard dashboard = FtcDashboard.getInstance();

        boolean captured = limeLight.captureSnapshot(snapShotName);

        JSONObject obj = snapshotManifest();
        String snapShotFullName ="";

        if (obj != null) {
            try {
                snapShotFullName = findFullName(obj, snapShotName);
            } catch (JSONException e) {
                return false;
            }
            if (snapShotFullName != "") {
                Bitmap snapShot = getBitmapFromSnapShot(snapShotFullName);
                if (snapShot != null) {
                    dashboard.sendImage(snapShot);
                    limeLight.deleteSnapshots();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * find the first occurrence  of a string name in a JSONObject-JSONArray
     *
     * @param fileName full name of file we want to get from limelight snapshot directory
     * @return A Bitmap image if we get it from limelight, or return null
     */
    public Bitmap getBitmapFromSnapShot(String fileName) {
        String imageUrl = "http://172.29.0.1:5801/snapshots/" + fileName; // Replace with your image URL

        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }


    /**
     * find the first occurrence  of a string name in a JSONObject-JSONArray
     *
     * @param obj source to search through
     * @param snapShotName begining of name to search for
     * @return A Sting of the full name, limelight adds some characters to names we provide
     */
    String findFullName(JSONObject obj, String snapShotName) throws JSONException {
        try {
            JSONArray jsonArray = obj.getJSONArray("fileNames");
            for (int i = 0; i < jsonArray.length(); i++) {
                String str = jsonArray.getString(i);
                if (str.contains(snapShotName)) {
                    return str;
                }
            }
        } catch (JSONException e) {
            return "";
        }
        return "";
    }
    /**
     * Sends a GET request to the specified endpoint.
     *
     * @param endpoint The endpoint to send the request to.
     * @return A JSONObject containing the response, or null if the request fails.
     */
    public JSONObject sendGetRequest (String endpoint) {

        // todo  fix this baseUrl (debug and see what it is in the Limelight3A.java
        String baseUrl = "http://172.29.0.1:5807";
        int GETREQUEST_TIMEOUT = 100;
        int CONNECTION_TIMEOUT = 100;

        HttpURLConnection connection = null;
        try {
            String urlString = baseUrl + endpoint;
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(GETREQUEST_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String response = readResponse(connection);
                if (isValidJson(response)) {
                    return new JSONObject(response);
                } else{
                    JSONObject jsonObject = new JSONObject();
                    JSONArray jsonArray = new JSONArray(response);
                    jsonObject.put("fileNames", jsonArray);
                    return jsonObject;
                }
            } else {
                System.out.println("HTTP GET Error: " + responseCode);
            }
        } catch (Exception e) {
            //e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    /**
     * Gets the manifest of available snapshots.
     * This method is not necessary for FTC teams. Marked as private
     *
     * @return A JSONObject containing the snapshot manifest.
     */
    private JSONObject snapshotManifest() {
        return sendGetRequest("/snapshotmanifest");
    }


    /**
     * Reads the response from an HTTP connection.
     *
     * @param connection The HttpURLConnection to read from.
     * @return A String containing the response.
     * @throws IOException If an I/O error occurs.
     */
    private String readResponse(HttpURLConnection connection) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        return response.toString();
    }

    public static boolean isValidJson(String jsonString) {
        try {
            new JSONObject(jsonString);
            return true;
        } catch (JSONException e) {
            return false;
        }
    }


}
