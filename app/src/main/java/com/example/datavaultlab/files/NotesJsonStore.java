package com.example.datavaultlab.files;

import android.content.Context;

import com.example.datavaultlab.model.Note;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class NotesJsonStore {

    public static final String FILE_NAME = "notes.json";

    private NotesJsonStore() {}

    public static void save(Context context, List<Note> notes) throws Exception {
        JSONArray array = new JSONArray();
        for (Note n : notes) {
            JSONObject obj = new JSONObject();
            obj.put("id",      n.id);
            obj.put("title",   n.title);
            obj.put("content", n.content);
            array.put(obj);
        }
        String json = array.toString();
        try (var fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) {
            fos.write(json.getBytes(StandardCharsets.UTF_8));
        }
    }

    public static List<Note> load(Context context) {
        try (var fis = context.openFileInput(FILE_NAME)) {
            byte[] bytes = fis.readAllBytes();
            String json  = new String(bytes, StandardCharsets.UTF_8);
            JSONArray array = new JSONArray(json);
            List<Note> list = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                list.add(new Note(
                        obj.getInt("id"),
                        obj.getString("title"),
                        obj.getString("content")
                ));
            }
            return list;
        } catch (Exception e) {
            return List.of();
        }
    }

    public static boolean delete(Context context) {
        return context.deleteFile(FILE_NAME);
    }
}