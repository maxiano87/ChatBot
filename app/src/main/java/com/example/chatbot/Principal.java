package com.example.chatbot;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;


import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Metadata;
import ai.api.model.Result;
import ai.api.model.Status;

import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Principal extends AppCompatActivity implements AIListener, View.OnClickListener {
    private ImageButton btnVoice;
    private ImageButton sendMessage;
    private AIService aiService;
    private static final int REQUEST_INTERNET = 200;

    TextToSpeech t1;

    public static String TAG = "AndroidDialogflow";

    List<Message> list;
    ListView myList;
    EditText message;
    AIConfiguration config;

    public static String API_KEY = "f743feaa075627ccdd57162a5c8950b0cb2047f4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.principal);
        btnVoice = (ImageButton) findViewById(R.id.send_speacker);
        list = new ArrayList<>();

        validateOS();

        config = new AIConfiguration(API_KEY,
                AIConfiguration.SupportedLanguages.Russian,
                AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(this, config);
        aiService.setListener(this);
        myList = (ListView) findViewById(R.id.chat);
        sendMessage = (ImageButton) findViewById(R.id.send_message);
        sendMessage.setOnClickListener(this);

        btnVoice.setOnClickListener(this);
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    Locale locSpanish = new Locale("rus", "KGZ");
                    t1.setLanguage(locSpanish);
                }
            }
        });

        message = (EditText) findViewById(R.id.text_message);
    }

    private void validateOS() {
        if (ContextCompat.checkSelfPermission(Principal.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Principal.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_INTERNET);
            ActivityCompat.requestPermissions(Principal.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_INTERNET);
        }
    }

    @Override
    public void onResult(AIResponse response) {
        Result result = response.getResult();
        final Status status = response.getStatus();
        Log.i(TAG, "Status code: " + status.getCode());
        Log.i(TAG, "Status type: " + status.getErrorType());
        final Metadata metadata = result.getMetadata();
        if (metadata != null) {
            Log.i(TAG, "Intent id: " + metadata.getIntentId());
            Log.i(TAG, "Intent name: " + metadata.getIntentName());
        }
        String parameterString = "";
        if (result.getParameters() != null && !result.getParameters().isEmpty()) {
            for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                parameterString += "(" + entry.getKey() + ", " + entry.getValue();
            }
        }
        String cad = response.getResult().getFulfillment().getSpeech().toString();
        if (cad.compareTo("") == 0) {
            cad = "Некоторые данные вышли по ошибке";
        }
        // text to speech get text response
        t1.speak(cad, TextToSpeech.QUEUE_FLUSH, null);
        list.add(new Message(result.getResolvedQuery(), 1));
        list.add(new Message(cad, 0));
        MessageAdapter adap = new MessageAdapter(list, this);
        myList.setAdapter(adap);
    }

    @Override
    public void onError(AIError error) {
        Log.e(TAG, error.toString());
    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_speacker:
                aiService.startListening();
                break;
            case R.id.send_message:
                sendText(message.getText().toString());
                break;
        }
    }

    void sendText(String query) {
        final AIConfiguration config = new AIConfiguration(API_KEY,
                AIConfiguration.SupportedLanguages.Russian,
                AIConfiguration.RecognitionEngine.System);
        final AIDataService aiDataService = new AIDataService(this, config);
        final AIRequest aiRequest = new AIRequest();
        aiRequest.setQuery(query);

        /*new AsyncTask<AIRequest, Void, AIResponse>() {
            private final ProgressDialog dialog = new ProgressDialog(Principal.this);

            protected void onPreExecute() {
                this.dialog.setMessage("Отправка сообщения...");
                this.dialog.show();
                super.onPreExecute();
            }

            @Override
            protected AIResponse doInBackground(AIRequest... requests) {
                final AIRequest request = requests[0];
                try {
                    final AIResponse response = aiDataService.request(aiRequest);
                    return response;
                } catch (AIServiceException e) {
                }
                return null;
            }

            @Override
            protected void onPostExecute(AIResponse aiResponse) {
                if (aiResponse != null) {
                    // process aiResponse here
                    Result result = aiResponse.getResult();
                    String cad = aiResponse.getResult().getFulfillment().getSpeech().toString();
                    if (cad.compareTo("") == 0) {
                        cad = "Некоторые данные вышли по ошибке";
                    }
                    t1.speak(cad, TextToSpeech.QUEUE_FLUSH, null);
                    list.add(new Message(result.getResolvedQuery(), 1));
                    list.add(new Message(cad, 0));
                    MessageAdapter adap = new MessageAdapter(list, Principal.this);
                    myList.setAdapter(adap);
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    message.setText("");

                }
            }
        }.execute(aiRequest);*/
    }
}
