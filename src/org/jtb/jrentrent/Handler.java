package org.jtb.jrentrent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONException;

public class Handler {
    private static String readString(Request request) throws IOException {
        BufferedReader reader = null;
        String line = null;
        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL(request.toString());
            HttpURLConnection uc = (HttpURLConnection) url.openConnection();
            uc.setReadTimeout(30 * 1000); // 30 seconds

            if (uc.getResponseCode() != 200) {
                //TODO: android log
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(uc.getInputStream(), "ISO-8859-1"), 8192);
            while ((line = reader.readLine()) != null) {
                result.append(line);
                result.append('\n');
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        return result.toString();
    }

    public Response getResponse(Request request) throws IOException, JSONException {
        String s = readString(request);
        Response r = new Response(s);
        return r;
    }
}
