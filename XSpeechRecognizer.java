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

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

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

    // public void onCreate(Bundle savedInstanceState) 
    // {
    //     cordova.getActivity().runOnUiThread(new Runnable() {
    //         @Override
    //         public void run() {
    //             sr = SpeechRecognizer.createSpeechRecognizer(this);
    //             // callbackContext.success(); // Thread-safe.
    //         }
    //     });
    //     // return true;
               
    //     // super.onCreate(savedInstanceState);
    //     // sr.setRecognitionListener(new listener());        
    // }

    class listener implements RecognitionListener          
    {
        public void onReadyForSpeech(Bundle params)
        {
             Log.d(TAG, "onReadyForSpeech");
        }
        public void onBeginningOfSpeech()
        {
             Log.d(TAG, "onBeginningOfSpeech");
        }
        public void onRmsChanged(float rmsdB)
        {
             Log.d(TAG, "onRmsChanged");
        }
        public void onBufferReceived(byte[] buffer)
        {
             Log.d(TAG, "onBufferReceived");
        }
        public void onEndOfSpeech()
        {
             Log.d(TAG, "onEndofSpeech");
        }
        public void onError(int error)
        {
             Log.d(TAG,  "error " +  error);
             // mText.setText("error " + error);
        }
        public void onResults(Bundle results)                   
        {
            // String str = new String();
            // Log.d(TAG, "onResults " + results);
            // ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            // for (int i = 0; i < data.size(); i++)
            // {
            //    Log.d(TAG, "result " + data.get(i));
            //    str += data.get(i);
            // }
            // mText.setText("results: "+String.valueOf(data.size()));        
        }
        public void onPartialResults(Bundle partialResults)
        {
             Log.d(TAG, "onPartialResults");
        }
        public void onEvent(int eventType, Bundle params)
        {
             Log.d(TAG, "onEvent " + eventType);
        }
    }

    //@Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {

        callbackContext.sendPluginResult(new PluginResult("Hello World"));
                
        Handler loopHandler = new Handler(Looper.getMainLooper());
        loopHandler.post(new Runnable() {
            @Override
            public void run() {
                recognizer = SpeechRecognizer.createSpeechRecognizer(cordova.getActivity().getBaseContext());
                recognizer.setRecognitionListener(new listener());
            }
            
        });

		Boolean isValidAction = true;

        this.callbackContext= callbackContext;


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
     * @param args Argument array with the following string args: [req code][number of matches][prompt string]
     */
    private void startSpeechRecognitionActivity(JSONArray args) {

        Log.d(TAG, "Hello World");

        int maxMatches = 0;
        String prompt = "";
        String language = Locale.getDefault().toString();

        try {
            if (args.length() > 0) {
            	// Maximum number of matches, 0 means the recognizer decides
                String temp = args.getString(0);
                maxMatches = Integer.parseInt(temp);
            }
            if (args.length() > 1) {
            	// Optional text prompt
                prompt = args.getString(1);
            }
            if (args.length() > 2) {
            	// Optional language specified
            	language = args.getString(2);
            }
        }
        catch (Exception e) {
            Log.e(TAG, String.format("startSpeechRecognitionActivity exception: %s", e.toString()));
        }

        // Create the intent and set parameters
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);

        if (maxMatches > 0)
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, maxMatches);
        if (!prompt.equals(""))
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, prompt);
        cordova.startActivityForResult(this, intent, REQUEST_CODE);
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
