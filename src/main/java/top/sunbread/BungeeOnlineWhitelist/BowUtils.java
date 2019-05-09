package top.sunbread.BungeeOnlineWhitelist;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Pattern;

public final class BowUtils {

    public static UUID getUUIDbyName(String playerName) {
        if (!isPlayerNameValid(playerName)) return null;
        String json;
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "BungeeOnlineWhitelist");
            con.setDoOutput(true);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            json = in.readLine();
            in.close();
            con.disconnect();
        } catch (IOException e) {
            return null;
        }
        if (json == null) return null;
        JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
        if (!obj.has("id") || !obj.has("name")) return null;
        if (!playerName.equalsIgnoreCase(obj.getAsJsonPrimitive("name").getAsString()))
            return null;
        return BowUtils.String2UUID(obj.getAsJsonPrimitive("id").getAsString());
    }

    public static String UUID2String(UUID uuid) {
        return uuid.toString().toLowerCase().replace("-", "");
    }

    public static UUID String2UUID(String uuidString) {
        if (uuidString.length() != 32) return null;
        try {
            return UUID.fromString(uuidString.substring(0, 8) + "-" +
                    uuidString.substring(8, 12) + "-" +
                    uuidString.substring(12, 16) + "-" +
                    uuidString.substring(16, 20) + "-" +
                    uuidString.substring(20, 32));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static boolean isPlayerNameValid(String playerName) {
        return playerName != null && playerName.length() <= 16 && Pattern.matches("^\\w+$", playerName);
    }

}
