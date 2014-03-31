Android XSpeechRecognizer plugin for Cordova/Phonegap
===================================

This plugin is based on the following implementations:
[https://github.com/macdonst/SpeechRecognitionPlugin]SpeechRecognitionPlugin
[https://github.com/poiuytrez/SpeechRecognizer]SpeechRecognizer

I wasn't able to implement the first plugin at first, the SpeechRecognizer was a good start, but I didn't want the google dialog to popup when the mic was activated.

When developing this plugin my crude workflow involved commiting the project git repo and pushing it to github, then reloading the plugin and running the cordova android app. For reference, here are the scripts I used:

git commit -am "dev";git push https://jcsmesquita@github.com/jcsmesquita/cordova-speechrecognition

cordova plugin remove com.phonegap.plugins.speech;cordova plugin add https://github.com/jcsmesquita/cordova-speechrecognition; cordova run android --device