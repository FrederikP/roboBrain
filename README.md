# roboBrain
Let an Android device control your Arduino via bluetooth

roboBrain is an Android library that makes communication between Arduino and Android devices more comfortable 
by adding new abstraction layers. It was created for a project at my university during my bachelor studies. 
It may need some more work if you want to use it, but it provides a nice boilerplate for these kinds of projects.
roboBrain depends on the Amarino framework which handles the raw bluetooth communication. 
Please see http://www.amarino-toolkit.net/ for reference.

roboBrain also allows you to use OpenCV functionality on the android device. 

# Project Structure

RoboBrainLib - The core library containing all the reusable stuff.

RoboBrain - Example app using the library

YARP - Example Arduino Code for my robot

# Testing
The robobrain example app and library are tested thoroughly as can be seen in the Jenkins Job: https://upseil.com/jenkins/job/RoboBrainTest/

# Builds
Build information for the example app is available here: https://upseil.com/jenkins/job/RoboBrain/
