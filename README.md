Jingle Location
=================

Jingle is a lightweight sensory system designed to be deployed on embedded systems.

### Table of Contents  
[Introduction] (#introduction)   
[Materials](#materials)     
[Features](#features)    

<a name="introduction"/>
## Introduction

Jingle is a sensory network designed to deduce the context of a user in a minimally invasive manner. This is an extension of the <a href="https://github.com/EStorm21/Jingle">Jingle Base</a>. JingleLocation is a plug and play extension that adds location tracking to the Jingle network. 


<a name="materials"/>
## Materials

JingleLocation requires a single Android device with GPS tracking running Android 16 or newer. The app is currently not available on Google Play, so developer mode must be enabled and the app must be installed manually. 

<a name="features"/>
## Features

Jingle Location is designed to both report your current location to the Jingle network for use by other nodes, as well as serve as an intelligent notification device. 

The device interfaces with the local Google Calendar on the Android device. The app can then determine whether or not you need a reminder of the event, based on your current location and the location of the event in question. 

JingleLocation provides a robust, minimal input interface to gradually learn where your events take place. As you go to events, you enter the name of your location into JingleLocation which can then record your current GPS location and associate it with this location name. By including location names in your calendar events, Jingle can detect where the events are set to take place. 

Additionally, you can set locations where you always want to be notified of new events. 

Finally, by configuring your JingleBase, you can send texts to your phone to remind you of events. 
