Android XSpeechRecognizer plugin for Cordova/Phonegap
===================================

This plugin is based on the following implementations:
[https://github.com/macdonst/SpeechRecognitionPlugin] SpeechRecognitionPlugin
[https://github.com/poiuytrez/SpeechRecognizer]SpeechRecognizer

When developing this plugin my crude workflow involved commiting the project git repo and pushing it to github, then reloading the plugin and running the cordova android app. For reference, here are the scripts I used:

git commit -am "dev";git push https://jcsmesquita@github.com/jcsmesquita/cordova-speechrecognition

cordova plugin remove com.phonegap.plugins.speech;cordova plugin add https://github.com/jcsmesquita/cordova-speechrecognition; cordova run android --device