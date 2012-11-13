/*******************************************************************************
 *RoboBrain - Control your Arduino Robots per Android Device
 *Copyright (c) 2012 Frederik Petersen.
 *All rights reserved.
 *
 *This file is part of RoboBrain.
 *
 *    RoboBrain is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    RoboBrain is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *    FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with RoboBrain.  If not, see <http://www.gnu.org/licenses/>.
 *
 *Contributors:
 *    Frederik Petersen - Project Owner, initial Implementation
 *
 *Uses MeetAndroid and Servo library. Thanks to the creators!
 *******************************************************************************/

/*
 * This sketch is being developed in a Eclipse environment which was configured to allow Arduino programming. There
 * are several tutorials that you can find in the www for setting up Eclipse for Arduino development.
 *
 * But there is the option to copy the code and use the easy-to-use Arduino SDK.
 * It should work out of the box, just remove the #include below.
 *
 */

// Do not remove the include below
#include "YARP.h"

//RGB LED stuff
#define RED A2
#define GREEN A3
#define BLUE1 A4
#define BLUE2 A5

//Set timed event modes
#define NONE 0
#define STOPDELAY 1

//Motor Port numbers
int E1 = 5; //M1 Speed Control
int E2 = 6; //M2 Speed Control
int M1 = 4; //M1 Direction Control
int M2 = 7; //M1 Direction Control

//Other hardware port numbers
int IR_BACK = 11; //Infrared sensor back
int SERVO_US = 10; //Servo for ultrasonic sensor movement
int URPWM = 3; // US PWM Output 0-25000US,Eqvery 50US represent 1cm

//Baud Rate of Bluetooth shield. 115200 works well for me,
//but I had to configure my shield to work at that rate
long BAUD_RATE_BT = 115200;

//Some values for setting servo to specific positions
int SERVO_MIDDLE = 95;
int SERVO_RIGHT = 50;
int SERVO_LEFT = 140;

//Speed at which to turn
int TURN_SPEED = 240; //constant because turn is given in angular value from android.

//How many milli seconds does it take for the robot to turn one degree at the speed above
int TURN_MILLIS_PER_DEG = 12;

//For timed events like stopping with delay and turning
unsigned long millisTimer = 0;
int eventTimerMode = NONE;

//Used to communicate with Android
MeetAndroid meetAndroid;

//Control the servo with this object
Servo usservo;

//The setup function is called once at startup of the sketch
void setup() {
	//set to baud rate of bluetooth model
	Serial.begin(BAUD_RATE_BT);

	//init Infrared Pin for backside proximity sensor
	pinMode(IR_BACK, INPUT);

	//init Motor Pins
	int i;
	for (i = 5; i <= 7; i++)
		pinMode(i, OUTPUT);

	//Attach Servo for ultrasonix sensor movement
	usservo.attach(SERVO_US);
	//Set servo to middle
	usservo.write(SERVO_MIDDLE);

	//Register functions for events sent from Android "Brain"
	meetAndroid.registerFunction(advanceAtSpeed, 'A');
	meetAndroid.registerFunction(backOffAtSpeed, 'B');
	meetAndroid.registerFunction(stopWithDelay, 'S');
	meetAndroid.registerFunction(turnLeftWithAngle, 'L');
	meetAndroid.registerFunction(turnRightWithAngle, 'R');
	meetAndroid.registerFunction(setServo, 'C');
	meetAndroid.registerFunction(setLED, 'D');

	//Set RGB LED pins to output
	pinMode(RED, OUTPUT);
	pinMode(GREEN, OUTPUT);
	pinMode(BLUE1, OUTPUT);
	pinMode(BLUE2, OUTPUT);

	//Set Color of LED to off
	analogWrite(RED, 0);
	analogWrite(GREEN, 0);
	analogWrite(BLUE1, 0);
	analogWrite(BLUE2, 0);

	//Send to console
	String toCons = "CONSOLE:Robot initialized";
	sendData(toCons);

}

// The loop function is called in an endless loop
void loop() {
	long startTime = millis();
	//makes sure that data send from android to robot are received and according funtions are called
	meetAndroid.receive();

	//Give sensor readings to Android Command Center:
	int value = getUSMeasurement();
	String prefix = "FRONTPROX:";
	sendData(prefix, value);

	value = getIRMeasurement();
	prefix = "BACKPROX:";
	sendData(prefix, value);

	//For motor events involving time like stopping with delay
	switch (eventTimerMode) {
	case NONE:
		break;
	case STOPDELAY:
		if (millisTimer < millis()) {
			stop();
			millisTimer = 0;
			eventTimerMode = NONE;
			String sendThis = "STOPPEDAFTERDELAY";
			sendData(sendThis);
		}
		break;
	default:
		eventTimerMode = NONE;
		millisTimer = 0;
		break;
	}

	//Making sure that new line is started for every loop run
	Serial.println();

	//Constant rate of loop process
	long timeConsumed = millis() - startTime;
	long toDelay = 100 - timeConsumed;
	if (toDelay > 0 && toDelay < 100) {
		delay(toDelay);
	} else if (toDelay > 100) {
		delay(100);
	}
}

//Methods to read sensor data

int getUSMeasurement() {
	int value; // This value will be populated
	unsigned long LowLevelTime = pulseIn(URPWM, LOW);

	if (LowLevelTime == 50000) // the reading is invalid.
			{
		Serial.print("Invalid");
		value = -1;
	} else {
		value = LowLevelTime / 50; // every 50us low level stands for 1cm
	}

	return value;
}

int getIRMeasurement() {
	int backBool = digitalRead(IR_BACK);
	return backBool;
}

//Motor control methods

void stop(void) {
	digitalWrite(E1, LOW);
	digitalWrite(E2, LOW);
}
void advance(char a, char b) {
	analogWrite(E1, a); //PWM Speed Control
	digitalWrite(M1, LOW);
	analogWrite(E2, b);
	digitalWrite(M2, LOW);
}
void back_off(char a, char b) {
	analogWrite(E1, a);
	digitalWrite(M1, HIGH);
	analogWrite(E2, b);
	digitalWrite(M2, HIGH);
}
void turn_L(char a, char b) {
	analogWrite(E1, a);
	digitalWrite(M1, LOW);
	analogWrite(E2, b);
	digitalWrite(M2, HIGH);
}
void turn_R(char a, char b) {
	analogWrite(E1, a);
	digitalWrite(M1, HIGH);
	analogWrite(E2, b);
	digitalWrite(M2, LOW);
}
void setServoRight() {
	usservo.write(SERVO_RIGHT);
}
void setServoLeft() {
	usservo.write(SERVO_LEFT);
}
void setServoMiddle() {
	usservo.write(SERVO_MIDDLE);
}
void setServoTo(int angle) {
	usservo.write(angle);
}

//Functions registered for events sent from Android

void setServo(byte flag, byte numOfValues) {
	if (numOfValues == 0) {
		setServoMiddle();
	} else {
		int angle = meetAndroid.getInt();
		setServoTo(angle);
	}
}

void advanceAtSpeed(byte flag, byte numOfValues) {
	int speed = meetAndroid.getInt();
	advance(speed, speed);
}

void backOffAtSpeed(byte flag, byte numOfValues) {
	int speed = meetAndroid.getInt();
	back_off(speed, speed);
}

void stopWithDelay(byte flag, byte numOfValues) {
	int delay = meetAndroid.getInt();
	if (delay <= 0) {
		stop();
	} else {
		if (eventTimerMode == 0) {
			millisTimer = millis() + delay;
			eventTimerMode = STOPDELAY;
		} else {
			Serial.print("More than one timed event not possible.");
		}
	}
}

void turnLeftWithAngle(byte flag, byte numOfValues) {
	int angle = meetAndroid.getInt();
	int delay = angle * TURN_MILLIS_PER_DEG;
	if (delay > 0) {
		if (eventTimerMode != NONE) {
			stop();
		}
		turn_L(TURN_SPEED, TURN_SPEED);
		millisTimer = millis() + delay;
		eventTimerMode = STOPDELAY;
	}
}
void turnRightWithAngle(byte flag, byte numOfValues) {
	int angle = meetAndroid.getInt();
	int delay = angle * TURN_MILLIS_PER_DEG;
	if (delay > 0) {
		if (eventTimerMode != NONE) {
			stop();
		}
		turn_R(TURN_SPEED, TURN_SPEED);
		millisTimer = millis() + delay;
		eventTimerMode = STOPDELAY;
	}
}

void setLED(byte flag, byte numOfValues) {
	int num = numOfValues - 0;
	if (num != 3) {
//		sendMessage("CONSOLE:", "LED TURNED OFF");
		setLEDtoColor();
	} else {
		int colors[num];
		meetAndroid.getIntValues(colors);
		int red = colors[0];
		int green = colors[1];
		int blue = colors[2];
//		String starter = "LED TURNED TO ";
//		String comma = ",";
//		String dot = ".";
//		String message = starter + red + comma + green + comma + blue + dot;
//		sendMessage("CONSOLE:", message);
		setLEDtoColor(colors[0], colors[1], colors[2]);
	}

}

//Methods for sending data to Android

void sendData(const String& prefix, int value) {
	String toTrans = prefix + value;
	int length = toTrans.length() + 1;
	char charBuf[length];
	toTrans.toCharArray(charBuf, length);
	meetAndroid.send(charBuf);
}

void sendMessage(const String& prefix, const String& message) {
	String toTrans = prefix + message;
	int length = toTrans.length() + 1;
	char charBuf[length];
	toTrans.toCharArray(charBuf, length);
	meetAndroid.send(charBuf);
}

void sendData(const String& text) {
	int length = text.length() + 1;
	char charBuf[length];
	text.toCharArray(charBuf, length);
	meetAndroid.send(charBuf);
}

//LED methods

void setLEDtoColor(int red, int green, int blue) {
	//Set Color to white
	analogWrite(RED, red);
	analogWrite(GREEN, green);
	analogWrite(BLUE1, blue);
	//Too much blue with the second LED
	//analogWrite(BLUE2, blue);
}
