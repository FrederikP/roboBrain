// Only modify this file to include
// - function definitions (prototypes)
// - include files
// - extern variable definitions
// In the appropriate section

#ifndef ArduinoSensorGraph_H_
#define ArduinoSensorGraph_H_
#include "Arduino.h"
#include "HardwareSerial.h"
//add your includes for the project ArduinoSensorGraph here
#include <MeetAndroid.h>
#include <URMSerial.h>

//end of add your includes here
#ifdef __cplusplus
extern "C" {
#endif
void loop();
void setup();
#ifdef __cplusplus
} // extern "C"
#endif

//add your function definitions for the project ArduinoSensorGraph here

int getUSMeasurement();
void sendData();


//Do not add code below this line
#endif /* ArduinoSensorGraph_H_ */
