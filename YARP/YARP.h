// Only modify this file to include
// - function definitions (prototypes)
// - include files
// - extern variable definitions
// In the appropriate section

#ifndef YARP_H_
#define YARP_H_
#include "Arduino.h"
//add your includes for the project YARP here
#include "HardwareSerial.h"
#include <MeetAndroid.h>
#include <Servo.h>


//Library references
extern HardwareSerial Serial;

//end of add your includes here
#ifdef __cplusplus
extern "C" {
#endif
void loop();
void setup();
#ifdef __cplusplus
} // extern "C"
#endif

//add your function definitions for the project YARP here

//Communication functions
void sendData(const String& prefix, int value);
void sendMessage(const String& prefix, const String& message);
void sendData(const String& text);

//Sensor functions
int getUSMeasurement();
int getIRMeasurement();

//Motor functions:
void stop(void);
void advance(char a, char b);
void back_off(char a, char b);
void turn_L(char a, char b);
void turn_R(char a, char b);

//Servo functions:
void setServoRight();
void setServoLeft();
void setServoTo(int angle);
void setServoMiddle();

//Functions registered for Android Events
void advanceAtSpeed(byte flag, byte numOfValues);
void backOffAtSpeed(byte flag, byte numOfValues);
void stopWithDelay(byte flag, byte numOfValues);
void turnLeftWithAngle(byte flag, byte numOfValues);
void turnRightWithAngle(byte flag, byte numOfValues);
void setServo(byte flag, byte numOfValues);
void setLED(byte flag, byte numOfValues);

//LED functions
void setLEDtoColor(int red = 0, int green = 0, int blue = 0);


//Do not add code below this line
#endif /* YARP_H_ */
