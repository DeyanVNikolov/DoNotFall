package net.didakamaybe.donotfall.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class VersionChecker {
    public static final String VERSION = "1.0.1";
    public static final String MOD_ID = "donotfall";
    public static final String NAME = "DoNotFall";
    public static final String DESCRIPTION = "A mod that prevents you from falling into the void";
    public static final String URL = "https://donotfall.deyannikolov.eu/index.json";
    public static final String UPDATE_URL = "https://modrinth.com/mod/donotfall";

    public static String checkVersion() {
        String latestVersion = getLatestVersion();
        if (VERSION.equals(latestVersion)) {
            System.out.println("You are using the latest version of DoNotFall!");
            return latestVersion;
        } else {
            if (latestVersion.equals("error")) {
                System.out.println("There was an error while checking for updates!");
                return "error";
            } else {
                System.out.println("There is a new version of DoNotFall available! You are using " + VERSION + " and the latest version is " + latestVersion);
                System.out.println("You can download it from " + UPDATE_URL);
                return latestVersion;
            }

        }
    }

    private static String getLatestVersion() {
        try {
            URL url = new URL(URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(content.toString(), JsonObject.class);

            return jsonObject.get("ver").getAsString();



        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
}
