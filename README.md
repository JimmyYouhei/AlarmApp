# AlarmApp

Alarm App  was made to learn more about activity , intent , pending intent , SQLite , Broadcast and broadcast receiver , service .... of Android

## Version:
-	Target SDK: API 27: Android 8.1 Oreo . By the time this app was being made , the API 28 was not yet stable.
-	Min SDK: 17


## How to install 
-	If you have an Android OS phone please go to [google app store](https://play.google.com/store/apps/details?id=vn.org.quan.hong.nguyen.myalarmclock) to install
-	If you want to work with the source code then open your android studio and use  use _new_ -> _Project From Version Control_ -> _Git_ and paste _https://github.com/JimmyYouhei/AlarmApp.git_

## How to use
-	Push on the "+" icon on the main screen to choose a new alarm.
-	Push and hold an existing alarm to see the "edit” or “delete” menu. Then choose 1 of the option or push outside to cancel the menu
   - If Push on the “edit”, it will take you to a screen to choose a new alarm
   - If Push on the “delete”, the alarm will simply gone
-	On the main alarm screen the alarm can only be set up by pushing the "Off" button to change to "On" state
    -	If the alarm can be triggered this day then it will be. If not, the alarm will be triggered the next day
    -	To deactivate the alarm push on the "On" Button to change to the "Off" state
-	When the alarm is triggered, a piece of music will be played but the music volume will be very low so please let your phone's volume as high as possible
    -	To turn off the music please push on the "On" button OF THE RIGHT ALARM. If you push the "On" Button of other alarm it will not affect the current alarm
    -	If an alarm is running and a new alarm is triggered. The old alarm will be automatically off while the new alarm is on to resolve any conflict


## Features:
-	Each View in the Adapter is independent from each other . Meaning the alarm from 1 View cannot be canceled by hitting off button from other Alarm
-	The app can receive multi-alarm but only 1 will play at a time (eg: 1.30pm and 1.31pm)
-	No duplicate alarm: all is checked before added to List
-	the alarm is stored by SQLite
-	The SQLite is managed by Room Persistence Library which is quicker easier to use than normal method and is recommended by google instead of SQLite API
-	The app is coded so that if the alarm cannot be triggered today, it will be triggered tomorrow
-	Make many methods and even a class to reuse code as much as possible
-	The app also uses background thread to manage Room Library to avoid freezing the UI thread (also called the main thread)


## Possible issues
-	Hard back button is not throughout testing
-	by the time this project was completed, my Samsung phone is broken so the app cannot be tested on a real phone and of course I cannot test the set alarm for tomorrow features
-	Memory leak is handled but because I cannot test for all case so I cannot be 100% sure

## Known Issue
-	Somehow when importing the 2 projects and then build them on another computer if the instant run features of Android Studio is active the app will fail to build
-	The volume of the alarm is low due to the volume of the mp3 I choose is very low


## Special Note
-	My project is quite complex. As a result I have also included comment on my code for more detail
-	It is my first time to make multi-threads app . Learning Android thread was one of the most difficult areas I have ever done so far . Although the scope was very narrow and I knew java already, it took me 2 full weeks just to learn about Android Threads. For your information , learning Unity and learning C# for scripting to make Flappy Bird took just about 1 week for me
-	To work with SQLite in Android you can code normally or use Room Persistence Library. And Room (which is recommended by Google by the time I was making the app) was very quick and easy to use than just code SQLite normally (for my next project I used both as a way to compare and it did not take long for me to notice the difference)
-	When working with threads and Room you will have to worry about memory leak. I also have code to handle memory leak but cannot be tested for all case
-	This is also my first time to work with Recycler View .At first, the Recycler View seem to very complex but after times it is very convenience and each elements of the Recycler View can be manipulated (For example: each toggle button on the Recycler View )
-	This is also my first time to work with Intent , pending intent , Broadcast , Broadcast Receiver and service
-	By the time I was making the app the API 28 : Android 9.0(Pie) was not yet stable and I had a lot of error when working with constraint layout of this API


## License
Free Software that follows the MIT License. More detail can be seen [here](https://github.com/JimmyYouhei/AlarmApp/blob/master/LICENSE)

