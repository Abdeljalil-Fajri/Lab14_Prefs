package com.example.datavaultlab.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datavaultlab.R;
import com.example.datavaultlab.cache.TempStore;
import com.example.datavaultlab.external.ExternalStore;
import com.example.datavaultlab.files.NotesJsonStore;
import com.example.datavaultlab.files.TextFileStore;
import com.example.datavaultlab.model.Note;
import com.example.datavaultlab.prefs.UserPrefs;
import com.example.datavaultlab.prefs.VaultPrefs;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DataVaultLab";
    private final List<String> languages =
            Arrays.asList("en", "fr", "ar", "es");

    private EditText etUsername, etToken;
    private Spinner  spLanguage;
    private Switch   swDarkMode;
    private TextView tvOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsername = findViewById(R.id.etUsername);
        etToken    = findViewById(R.id.etToken);
        spLanguage = findViewById(R.id.spLanguage);
        swDarkMode = findViewById(R.id.swDarkMode);
        tvOutput   = findViewById(R.id.tvOutput);

        Button btnSavePrefs = findViewById(R.id.btnSavePrefs);
        Button btnLoadPrefs = findViewById(R.id.btnLoadPrefs);
        Button btnSaveNotes = findViewById(R.id.btnSaveNotes);
        Button btnLoadNotes = findViewById(R.id.btnLoadNotes);
        Button btnExport    = findViewById(R.id.btnExport);
        Button btnClear     = findViewById(R.id.btnClear);

        ArrayAdapter<String> langAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                languages);
        spLanguage.setAdapter(langAdapter);

        btnSavePrefs.setOnClickListener(v -> savePreferences());
        btnLoadPrefs.setOnClickListener(v -> loadPreferences());
        btnSaveNotes.setOnClickListener(v -> saveNotes());
        btnLoadNotes.setOnClickListener(v -> loadNotes());
        btnExport.setOnClickListener(v -> exportToExternal());
        btnClear.setOnClickListener(v -> clearEverything());

        loadPreferences();
    }

    private void savePreferences() {
        try {
            String username = etUsername.getText().toString().trim();
            String language = languages.get(
                    Math.max(0, spLanguage.getSelectedItemPosition()));
            String theme = swDarkMode.isChecked() ? "dark" : "light";

            UserPrefs.save(this, username, language, theme, false);

            String token = etToken.getText().toString().trim();
            if (!token.isEmpty()) {
                VaultPrefs.storeToken(this, token);
            }

            try {
                TempStore.write(this, "session_cache.txt",
                        "user=" + username + " lang=" + language
                                + " theme=" + theme);
            } catch (Exception ignored) {}

            Log.d(TAG, "Prefs saved — username=" + username
                    + " lang=" + language + " theme=" + theme);

            tvOutput.setText(
                    "Preferences saved.\n" +
                            "username : " + username + "\n" +
                            "language : " + language + "\n" +
                            "theme    : " + theme + "\n" +
                            "token    : " + (token.isEmpty()
                            ? "not provided" : "stored encrypted")
            );
        } catch (Exception e) {
            tvOutput.setText("Save error: " + e.getMessage());
            Log.e(TAG, "Save error", e);
        }
    }

    private void loadPreferences() {
        try {
            UserPrefs.Snapshot snap = UserPrefs.load(this);

            etUsername.setText(snap.username);
            swDarkMode.setChecked("dark".equals(snap.theme));

            int idx = languages.indexOf(snap.language);
            spLanguage.setSelection(idx >= 0 ? idx : 0);

            int tokenLength = 0;
            try {
                String token = VaultPrefs.fetchToken(this);
                tokenLength = token == null ? 0 : token.length();
            } catch (Exception ignored) {}

            Log.d(TAG, "Prefs loaded — username=" + snap.username
                    + " lang=" + snap.language
                    + " theme=" + snap.theme
                    + " tokenLength=" + tokenLength);

            tvOutput.setText(
                    "Preferences loaded.\n" +
                            "username    : " + snap.username + "\n" +
                            "language    : " + snap.language + "\n" +
                            "theme       : " + snap.theme + "\n" +
                            "tokenLength : " + tokenLength
            );
        } catch (Exception e) {
            tvOutput.setText("Load error: " + e.getMessage());
            Log.e(TAG, "Load error", e);
        }
    }

    private void saveNotes() {
        try {
            List<Note> notes = Arrays.asList(
                    new Note(1, "Android Tips",
                            "Always use MODE_PRIVATE for internal storage."),
                    new Note(2, "Security Note",
                            "Never log sensitive tokens or passwords."),
                    new Note(3, "Study Reminder",
                            "Review SharedPreferences vs EncryptedSharedPreferences.")
            );

            NotesJsonStore.save(this, notes);
            TextFileStore.write(this, "memo.txt",
                    "Notes saved successfully (UTF-8).");

            Log.d(TAG, "Notes saved — count=" + notes.size());

            tvOutput.setText(
                    "Notes saved to internal storage.\n" +
                            "File  : notes.json\n" +
                            "Count : " + notes.size() + "\n" +
                            "Memo  : memo.txt written"
            );
        } catch (Exception e) {
            tvOutput.setText("Save notes error: " + e.getMessage());
            Log.e(TAG, "Save notes error", e);
        }
    }

    private void loadNotes() {
        try {
            List<Note> notes = NotesJsonStore.load(this);

            String memo;
            try {
                memo = TextFileStore.read(this, "memo.txt");
            } catch (Exception e) {
                memo = "(memo.txt not found)";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Notes loaded from internal storage.\n");
            sb.append("Memo   : ").append(memo).append("\n");
            sb.append("Count  : ").append(notes.size()).append("\n\n");
            for (Note n : notes) {
                sb.append("[").append(n.id).append("] ")
                        .append(n.title).append("\n")
                        .append("  ").append(n.content).append("\n");
            }

            Log.d(TAG, "Notes loaded — count=" + notes.size());
            tvOutput.setText(sb.toString());
        } catch (Exception e) {
            tvOutput.setText("Load notes error: " + e.getMessage());
            Log.e(TAG, "Load notes error", e);
        }
    }

    private void exportToExternal() {
        try {
            String timestamp = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .format(new Date());

            List<Note> notes = NotesJsonStore.load(this);

            StringBuilder exportContent = new StringBuilder();
            exportContent.append("DataVault Export\n");
            exportContent.append("Exported at: ").append(timestamp).append("\n");
            exportContent.append("Notes count: ").append(notes.size()).append("\n\n");
            for (Note n : notes) {
                exportContent.append("[").append(n.id).append("] ")
                        .append(n.title).append("\n")
                        .append("  ").append(n.content).append("\n");
            }

            String path = ExternalStore.write(this,
                    "export_notes.txt", exportContent.toString());

            tvOutput.setText(
                    "Exported to external storage.\n" +
                            "Path      : " + path + "\n" +
                            "Timestamp : " + timestamp + "\n" +
                            "Notes     : " + notes.size()
            );

            Log.d(TAG, "Exported to: " + path);

        } catch (Exception e) {
            tvOutput.setText("Export error: " + e.getMessage());
            Log.e(TAG, "Export error", e);
        }
    }

    private void clearEverything() {
        try {
            UserPrefs.clear(this);

            try { VaultPrefs.clear(this); }
            catch (Exception ignored) {}

            NotesJsonStore.delete(this);
            TextFileStore.delete(this, "memo.txt");
            ExternalStore.delete(this, "export_notes.txt");

            int purged = TempStore.purge(this);

            etUsername.setText("");
            etToken.setText("");
            swDarkMode.setChecked(false);
            spLanguage.setSelection(0);

            Log.d(TAG, "Everything cleared — cache purged: " + purged);

            tvOutput.setText(
                    "Everything cleared.\n" +
                            "user_prefs   : cleared\n" +
                            "vault_prefs  : cleared\n" +
                            "notes.json   : deleted\n" +
                            "memo.txt     : deleted\n" +
                            "export_notes : deleted\n" +
                            "cache purged : " + purged + " file(s)"
            );
        } catch (Exception e) {
            tvOutput.setText("Clear error: " + e.getMessage());
            Log.e(TAG, "Clear error", e);
        }
    }
}