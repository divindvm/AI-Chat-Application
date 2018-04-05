package com.example.divindivakaran.myaichat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import com.google.gson.JsonElement;
import java.util.Map;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import ai.api.AIServiceException;
import ai.api.RequestExtras;
import ai.api.android.AIDataService;
import ai.api.android.GsonFactory;
import com.google.gson.Gson;


public class AIChat extends AppCompatActivity implements AIListener{


    private Toolbar mToolbar;
    private TextView mResultView;
    private ImageButton mListenbtn;
    private ImageButton mSendBtn;
    private EditText mInputview;
    public AIService aiService;
    public AIDataService aiDataService;
    private TextView mQuerryView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aichat);



        mToolbar=(Toolbar)findViewById(R.id.chatboat_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Google AI");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mResultView=(TextView)findViewById(R.id.result_view);
        mListenbtn=(ImageButton)findViewById(R.id.listen_btn);
        mSendBtn=(ImageButton)findViewById(R.id.send_btn);
        mInputview=(EditText)findViewById(R.id.input_text);
        mQuerryView=(TextView)findViewById(R.id.input_querry_view);


        final AIConfiguration config = new AIConfiguration("9fbbc0ff33b246aa83449b8e0f0c9ae3",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
        aiService.setListener(this);



        mListenbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                aiService.startListening();

            }
        });

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final String queryString = String.valueOf(mInputview.getText());

                new DoTextRequestTask().execute(queryString);

                mInputview.setText("");

            }
        });



    }
    private void initService(final LanguageConfig selectedLanguage) {
        final AIConfiguration.SupportedLanguages lang = AIConfiguration.SupportedLanguages.fromLanguageTag(selectedLanguage.getLanguageCode());
        final AIConfiguration config = new AIConfiguration(selectedLanguage.getAccessToken(),
                lang,
                AIConfiguration.RecognitionEngine.System);


        aiDataService = new AIDataService(this, config);
    }




    public void onResult(AIResponse response) {
        Result result = response.getResult();

        // Get parameters
        String parameterString = "";
        if (result.getParameters() != null && !result.getParameters().isEmpty()) {
            for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
            }
        }
        final String speech = result.getFulfillment().getSpeech();
        Log.i("djfs", "Speech: " + speech);


        // Show results in TextView.
//        mResultView.setText("Query:" + result.getResolvedQuery() +
//                "\nAction: " + result.getAction() +
//                "\nParameters: " + parameterString+"\nResponse from server: "+speech);

        mResultView.setText(speech);
        mQuerryView.setText(result.getResolvedQuery());

    }

    class DoTextRequestTask extends AsyncTask<String, Void, AIResponse> {
        private Exception exception = null;
        protected AIResponse doInBackground(String... text) {
            AIResponse resp = null;
            try {
                resp = aiService.textRequest(text[0], new RequestExtras());
                // might depend on you implementation ; find out how to
                // retrieve the AIService instance and replace "aiDialog.getAIService()"
            } catch (Exception e) {
                this.exception = e;
            }
            return resp;
        }
        @Override
        protected void onPostExecute(final AIResponse response) {
            if (response != null) {
                onResult(response);
            } else {
                mResultView.setText("Errorrr");
            }
        }
    }



    @Override
    public void onError(AIError error) {

        mResultView.setText(error.toString());

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
}
