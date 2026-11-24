package com.sami.expenses;

import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.webkit.*;
import android.widget.Toast;
import org.json.JSONObject;
import java.util.ArrayList;

public class MainActivity extends Activity {
    WebView w;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        w = new WebView(this);
        setContentView(w);
        WebSettings s = w.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        w.setWebChromeClient(new WebChromeClient());
        w.addJavascriptInterface(new Bridge(), "Android");
        w.loadUrl("file:///android_asset/index.html");
    }

    class Bridge {
        @JavascriptInterface public void copy(String t) {
            ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("t", t));
            runOnUiThread(() -> Toast.makeText(MainActivity.this, "تم النسخ", Toast.LENGTH_SHORT).show());
        }
        @JavascriptInterface public void startVoice() {
            runOnUiThread(() -> {
                Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-EG");
                try { startActivityForResult(i, 7); }
                catch (Exception e) { Toast.makeText(MainActivity.this, "الإدخال الصوتي غير متاح على الجهاز", Toast.LENGTH_LONG).show(); }
            });
        }
    }

    @Override protected void onActivityResult(int rq, int rs, Intent d) {
        super.onActivityResult(rq, rs, d);
        if (rq == 7 && rs == RESULT_OK && d != null) {
            ArrayList<String> r = d.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (r != null && !r.isEmpty()) w.evaluateJavascript("onVoice(" + JSONObject.quote(r.get(0)) + ")", null);
        }
    }
}
