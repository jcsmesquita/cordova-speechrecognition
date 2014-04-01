Android XSpeechRecognizer plugin for Cordova/Phonegap
===================================

Aim
-----------------------------------------------------
Implements Android's speech recognition without the google dialog popup.
Recognition is done in one-shot mode, this plugin is doesn't implement continuous recognition.

This plugin is heavily based on the following implementations:
* [SpeechRecognitionPlugin](https://github.com/macdonst/SpeechRecognitionPlugin)
* [SpeechRecognize](https://github.com/poiuytrez/SpeechRecognizer)

A lot of credit is due to the developers involved in those two projects. I would not have been able to work on this if it weren't for those two projects as reference. This is also the first cordova plugin I write, so there is definitely room for improvement and contributions!

Installation for cordova>=3.0.0
-----------------------------------------------------
```bash
cordova create myApp
cd myApp
cordova platform add android
cordova plugin add https://github.com/jcsmesquita/cordova-speechrecognition
```

Development and Debugging
-----------------------------------------------------

When developing this plugin my crude workflow involved commiting the project git repo and pushing it to github, then reloading the plugin and running the cordova android app. For reference, here are the scripts I used:
```bash
git commit -am "dev";git push https://jcsmesquita@github.com/jcsmesquita/cordova-speechrecognition
```
```bash
cordova plugin remove com.phonegap.plugins.speech;cordova plugin add https://github.com/jcsmesquita/cordova-speechrecognition; cordova run android --device
```
There are probably much better ways to about doing this!

Full example
----------------
```html
<!DOCTYPE html>
<html>
    <head>
        <title>XSpeechRecognizer plugin demo</title>
        <script type="text/javascript" src="cordova.js"></script>
    </head>
    <body>

        <script type="text/javascript">

            function onDeviceReady(){
              console.log("Device is ready");
            }

            function recognizeSpeech() {
              var maxMatches = 5;
              var language = "en-US";
              window.plugins.speechrecognizer.start(resultCallback, errorCallback, maxMatches, language);
            }

            function stopRecognition(){
              window.plugins.speechrecognizer.stop(resultCallback, errorCallback);
              
            }

            function resultCallback (result){
              console.log(result);
              alert(result.results[0][0].transcript);
            }

            function errorCallback(error){
              console.log(error);
            }

            // Show the list of the supported languages
            function getSupportedLanguages() {
              window.plugins.speechrecognizer.getSupportedLanguages(function(languages){
                // display the json array
                alert(languages);
              }, function(error){
                alert("Could not retrieve the supported languages : " + error);
              });
            }

            document.addEventListener("deviceready", onDeviceReady, true);
        </script>

        <button onclick="recognizeSpeech();">Start recognition</button>
        <button onclick="stopRecognition();">Stop recognition</button>
        <button onclick="getSupportedLanguages();">Get Supported Languages</button>
    </body>
</html>
```

Roadmap
----------------
* A trully cross-platform plugin for Speech Recognition doesn't exist yet. This project could be the base for extending this funcitonality across all possible platforms - iOS, Windows Phone, Blackberry and so on.
* Allow for continuous recognition.

License
----------------

The MIT License

Copyright (c) 2011-2013  
João Mesquita (github.com/jcsmesquita)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
