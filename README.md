Android XSpeechRecognizer plugin for Cordova/Phonegap
===================================

This plugin is heavily based on the following implementations:
[SpeechRecognitionPlugin](https://github.com/macdonst/SpeechRecognitionPlugin)
[SpeechRecognize](https://github.com/poiuytrez/SpeechRecognizer)

A lot of credit is due to the developers involved in those two projects. As without their code as reference, I would not have been able to work on this. This is also the first cordova plugin I write, so there is definitely room for improvement and contributions!

The aim of this plugin was to implement Android's speech recognition without the google dialog popup.
Recognition is done as a one-shot, this plugin is not suitable for continuous recognition.

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

and 

```bash
cordova plugin remove com.phonegap.plugins.speech;cordova plugin add https://github.com/jcsmesquita/cordova-speechrecognition; cordova run android --device
```
