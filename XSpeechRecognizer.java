/**
 * The MIT License
 *
 *	Copyright (c) 2011-2013
 *	Colin Turner (github.com/koolspin)  
 *	Guillaume Charhon (github.com/poiuytrez)  
 *	
 *	Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *	
 *	The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *	
 *	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */
package com.phonegap.plugins.speech;

import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.LOG;

import android.util.Log;
import android.app.Activity;
import android.content.Intent;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

/**
 * Style and such borrowed from the TTS and PhoneListener plugins
 */
public class XSpeechRecognizer extends CordovaPlugin {
    private static final String TAG = XSpeechRecognizer.class.getSimpleName();
    private static int REQUEST_CODE = 1001;

    private CallbackContext callbackContext;
    private LanguageDetailsChecker languageDetailsChecker;
    private SpeechRecognizer recognizer;

    private void fireRecognitionEvent(ArrayList<String> transcripts, float[] confidences) {
        JSONObject event = new JSONObject();
        JSONArray results = new JSONArray();
        try {
            for(int i=0; i<transcripts.size(); i++) {
                JSONArray alternatives = new JSONArray();
                JSONObject result = new JSONObject();
                result.put("transcript", transcripts.get(i));
                result.put("final", true);
                if (confidences != null) {
                    result.put("confidence", confidences[i]);
                }
                alternatives.put(result);
                results.put(alternatives);
            }
            event.put("type", "result");
            // event.put("emma", null);
            // event.put("interpretation", null);
            event.put("results", results);
        } catch (JSONException e) {
            // this will never happen
        }
        PluginResult pr = new PluginResult(PluginResult.Status.OK, event);
        pr.setKeepCallback(true);
        this.callbackContext.sendPluginResult(pr); 
    }

    private void fireEvent(String type) {
        // callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, "Event"));
        JSONObject event = new JSONObject();
        try {
            event.put("type",type);
        } catch (JSONException e) {
            // this will never happen
        }
        // PluginResult pr = new PluginResult(PluginResult.Status.OK, "event");
        PluginResult pr = new PluginResult(PluginResult.Status.OK, event);
        pr.setKeepCallback(true);
        this.callbackContext.sendPluginResult(pr); 
    }

    class listener implements RecognitionListener          
    {
        public void onReadyForSpeech(Bundle params)
        {
            fireEvent("ready");
            Log.d(TAG, "onReadyForSpeech");
        }
        public void onBeginningOfSpeech()
        {
            fireEvent("start");
            Log.d(TAG, "onBeginningOfSpeech");
        }
        /* RMV Voltage */
        public void onRmsChanged(float rmsdB)
        {
            // fireEvent("rms changed");
            Log.d(TAG, "onRmsChanged");
        }
        public void onBufferReceived(byte[] buffer)
        {
            fireEvent("buffer received");
            Log.d(TAG, "onBufferReceived");
        }
        public void onEndOfSpeech()
        {
            fireEvent("end");
            Log.d(TAG, "onEndofSpeech");
        }
        public void onError(int error)
        {
            fireEvent("error:" + error);
            Log.d(TAG,  "error " +  error);
            // mText.setText("error " + error);
        }
        public void onResults(Bundle results)                   
        {
            String str = new String();
            Log.d(TAG, "onResults " + results);
            ArrayList<String> transcript = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            float[] confidence = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
            if (transcript.size() > 0) {
                Log.d(TAG, "fire recognition event");
                fireRecognitionEvent(transcript, confidence);
            } else {
                Log.d(TAG, "fire no match event");
                fireEvent("nomatch");
            }  
        }
        public void onPartialResults(Bundle partialResults)
        {
            fireEvent("partial results");
             Log.d(TAG, "onPartialResults");
        }
        public void onEvent(int eventType, Bundle params)
        {
            fireEvent("event");
             Log.d(TAG, "onEvent " + eventType);
        }
    }

    //@Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {

        this.callbackContext = callbackContext;
        
        // callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, "Hello World"));
                
        Handler loopHandler = new Handler(Looper.getMainLooper());
        loopHandler.post(new Runnable() {
            @Override
            public void run() {
                recognizer = SpeechRecognizer.createSpeechRecognizer(cordova.getActivity().getBaseContext());
                recognizer.setRecognitionListener(new listener());
            }
            
        });

        Boolean isValidAction = true;


		// Action selector
    	if ("startRecognize".equals(action)) {
            // recognize speech
            startSpeechRecognitionActivity(args);     
        } else if ("getSupportedLanguages".equals(action)) {
        	getSupportedLanguages();
        } else {
            // Invalid action
        	this.callbackContext.error("Unknown action: " + action);
        	isValidAction = false;
        }
    	
        return isValidAction;

    }

    // Get the list of supported languages
    private void getSupportedLanguages() {
    	if (languageDetailsChecker == null){
    		languageDetailsChecker = new LanguageDetailsChecker(callbackContext);
    	}
    	// Create and launch get languages intent
    	Intent detailsIntent = new Intent(RecognizerIntent.ACTION_GET_LANGUAGE_DETAILS);
    	cordova.getActivity().sendOrderedBroadcast(detailsIntent, null, languageDetailsChecker, null, Activity.RESULT_OK, null, null);
		
	}

	/**
     * Fire an intent to start the speech recognition activity.
     *
     * @param args Argument array with the following string args: [req code][number of matches]
     */
    private void startSpeechRecognitionActivity(JSONArray args) {

        int maxMatches = 0;
        String language = Locale.getDefault().toString();

        try {
            if (args.length() > 0) {
            	// Maximum number of matches, 0 means the recognizer decides
                String temp = args.getString(0);
                maxMatches = Integer.parseInt(temp);
            }
            if (args.length() > 1) {
            	// Language
            	language = args.getString(1);
            }
        }
        catch (Exception e) {
            Log.e(TAG, String.format("startSpeechRecognitionActivity exception: %s", e.toString()));
        }

        // Create the intent and set parameters
        final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);

        if (maxMatches > 0)
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, maxMatches);

        Handler loopHandler = new Handler(Looper.getMainLooper());
        loopHandler.post(new Runnable() {

            @Override
            public void run() {
                recognizer.startListening(intent);
            }
            
        });
    }

    /**
     * Handle the results from the recognition activity.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            // Fill the list view with the strings the recognizer thought it could have heard
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            returnSpeechResults(matches);
        }
        else {
            // Failure - Let the caller know
            this.callbackContext.error(Integer.toString(resultCode));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void returnSpeechResults(ArrayList<String> matches) {
        JSONArray jsonMatches = new JSONArray(matches);
        this.callbackContext.success(jsonMatches);
    }
    
}
