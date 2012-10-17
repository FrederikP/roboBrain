// Do not remove the include below
#include "ArduinoSensorGraph.h"

// The measurement we're taking
#define DISTANCE 1
#define TEMPERATURE 2
#define ERROR 3
#define NOTREADY 4
#define TIMEOUT 5

extern HardwareSerial Serial;
MeetAndroid meetAndroid;
URMSerial urm;
int sensor = 11;
//Baud rate (BT module is configured with this rate)
long BAUD_RATE = 115200;

int US_FRONT_RX = 2; //Ultrasonic sensor output  1
int US_FRONT_TX = 3; //Ultrasonic sensor output 2

void setup() {
	// use the baud rate your bluetooth module is configured to
	Serial.begin(BAUD_RATE);

	// we initialize pin 11 as an input pin
	pinMode(sensor, INPUT);

	urm.begin(US_FRONT_RX, US_FRONT_TX, 9600);
}

void loop() {
	meetAndroid.receive(); // you need to keep this in your loop() to receive events

	// read input pin and send result to Android
	//meetAndroid.send(digitalRead(sensor));
	int value = getUSMeasurement();

	meetAndroid.send(value);

	// add a little delay otherwise the phone is pretty busy
	delay(10000);
}

int getUSMeasurement() {
	int value; // This value will be populated
	// Request a distance reading from the URM37
	switch (urm.requestMeasurementOrTimeout(DISTANCE, value)) // Find out the type of request
	{
	case DISTANCE: // Double check the reading we recieve is of DISTANCE type
		return value;
		break;
	case TEMPERATURE:
		Serial.println("Wrong reading type: Temperature");
		break;
	case ERROR:
		Serial.println("Error");
		break;
	case NOTREADY:
		Serial.println("Not Ready");
		break;
	case TIMEOUT:
		Serial.println("Timeout");
		break;
	}

	return -1;
}

