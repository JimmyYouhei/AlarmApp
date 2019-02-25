# AlarmApp

This is an Alarm App . It was made to learn more about activity , intent , pending intent , SQLite , Broadcast and broadcast receiver , service ....

## Version:
- Target SDK: API 27: Android 8.1 Oreo . 
By the time this app was made , the API 28 was not yet stable.

- Min SDK: 17

## How to install 

## Features:
First it is a alarm app with more detail:
- each View in the Adapter is independent from each other . Meaning the alarm from 1 View can not be cancel by hitting off button from other Alarm 
- the app can received multi alarm but only 1 will play at a time (eg: 1.30pm and 1.31pm)
- No duplicate alarm: all is checked before added to List
- the alarm is stored by SQLite
- The SQLite is managed by Room Persistence Library which is quicker easier to use than normal method and is recommended by google instead of SQLite API
- The app is coded so that if the alarm can not be triggered today , it will be triggered tomorrow
- Make many methods and even a class to reuse code
- The app also use background thread to manage Room Library to avoid freeze the UI thread (also call the main thread)

## Possible issues
- Hard back button is not throughout testing
- by the time this project was completed,	my Samsung phone is broken so the app is not tested on a real phone and of course  I cannot test the _set alarm for tomorrow features_



Note: please leave your window volume at least 70 or more as the alarm sound is intentionally to be low in volume 

