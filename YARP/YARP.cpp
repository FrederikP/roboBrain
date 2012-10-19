// Do not remove the include below
#include "YARP.h"

//Set timed event modes
#define NONE 0
#define STOPDELAY 1

//Library references
extern HardwareSerial Serial;
MeetAndroid meetAndroid;
Servo usservo;

//Port numbers
int E1 = 5; //M1 Speed Control
int E2 = 6; //M2 Speed Control
int M1 = 4; //M1 Direction Control
int M2 = 7; //M1 Direction Control

int IR_BACK = 11; //Infrared sensor back
int SERVO_US = 10; //Servo for ultrasonic sensor movement
int URPWM = 3; // US PWM Output 0-25000US,Every 50US represent 1cm

long BAUD_RATE_BT = 115200;

int SERVO_MIDDLE = 95;
int SERVO_RIGHT = 50;
int SERVO_LEFT = 140;

int TURN_SPEED = 150; //constant because turn is given in angular value from android.

//For timed events like stopping with delay and turning
unsigned long millisTimer = 0;
int eventTimerMode = NONE;

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

	//Register functions for events sent from Android Brain
	meetAndroid.registerFunction(advanceAtSpeed, 'A');
	meetAndroid.registerFunction(backOffAtSpeed, 'B');
	meetAndroid.registerFunction(stopWithDelay, 'S');
	meetAndroid.registerFunction(turnLeftWithAngle, 'L');
	meetAndroid.registerFunction(turnRightWithAngle, 'R');
}

// The loop function is called in an endless loop
void loop() {
	int startTime = millis();
	//makes sure that data send from android to robot are received and according funtions are called
	meetAndroid.receive();

	//Give sensor readings to Android Command Center:
	String prefix;
	int value = getUSMeasurement();
	if (value) {
		String prefix = "FRONTPROX:";
		sendData(prefix, value);
	}

	value = getIRMeasurement();
	if (value) {
		prefix = "BACKPROX:";
		sendData(prefix, value);
	}

	//For motor events involving time like stopping with delay
	switch (eventTimerMode) {
	case NONE:
		break;
	case STOPDELAY:
		if (millisTimer < millis()) {
			stop();
			millisTimer = 0;
			eventTimerMode = NONE;
		}
		break;
	default:
		eventTimerMode = 0;
		millisTimer = 0;
		break;
	}

	//Constant rate of loop process
	unsigned long timeConsumed = millis() - startTime;
	unsigned long toDelay = 100 - timeConsumed;
	if (toDelay > 0) {
		delay(toDelay);
	}
}

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
void setServoTo(int angle) {
	usservo.write(angle);
}

//Functions registered for events sent from Android
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
	int delay = angle * 15;
	if (delay > 0) {
		if (eventTimerMode == 0) {
			turn_L(TURN_SPEED, TURN_SPEED);
			millisTimer = millis() + delay;
			eventTimerMode = STOPDELAY;
		} else {
			Serial.print("More than one timed event not possible.");
		}
	}
}
void turnRightWithAngle(byte flag, byte numOfValues) {
	int angle = meetAndroid.getInt();
	int delay = angle * 15;
	if (delay > 0) {
		if (eventTimerMode == 0) {
			turn_R(TURN_SPEED, TURN_SPEED);
			millisTimer = millis() + delay;
			eventTimerMode = STOPDELAY;
		} else {
			Serial.print("More than one timed event not possible.");
		}
	}
}

void sendData(const String& prefix, int value) {
	String toTrans = prefix + value;
	int length = toTrans.length() + 1;
	char charBuf[length];
	toTrans.toCharArray(charBuf, length);
	meetAndroid.send(charBuf);
}
