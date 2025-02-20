package com.patatedouce.openmotor;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonManager {
    private String filePath;
    private JSONObject jsonObject;

    public JsonManager(String filePath) {
        this.filePath = filePath;
        loadJson();
    }

    private void loadJson() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            jsonObject = new JSONObject(content);
        } catch (IOException e) {
            System.err.println("Erreur de lecture du fichier : " + e.getMessage());
        }
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void saveJson() {
        try {
            Files.write(Paths.get(filePath), jsonObject.toString(4).getBytes());
        } catch (IOException e) {
            System.err.println("Erreur d'écriture du fichier : " + e.getMessage());
        }
    }
}
