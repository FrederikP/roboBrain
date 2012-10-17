// Do not remove the include below
#include "bluetoothconf.h"

extern HardwareSerial Serial;

/*
 Based on example code from others

 Uses softserial on D9 (RxD),D10 (TxD) to talk to bluetooth modem
 */

#define RxD 6
#define TxD 7
SoftwareSerial blueToothSerial(RxD, TxD);
char incoming;
int displayed;
void setup() {
	Serial.begin(9600);
	delay(1000);
	pinMode(RxD, INPUT);
	pinMode(TxD, OUTPUT);
	setupBlueToothConnection();
	Serial.println("Started");
	displayed = 0;
}

void setupBlueToothConnection() {
	blueToothSerial.begin(115200); //Set BluetoothV3 baud rate to 38400
	delay(1000);
}

void loop() {
	if (displayed != 1) {
		Serial.println("Keep it coming...");
		displayed = 1;
	}
	int wut;
	if (blueToothSerial.available()) {
		wut = Serial.read();
//		String starter = "To Serial: ";
//		Serial.println(starter + wut);
		Serial.write(blueToothSerial.read());
	}
	if (Serial.available()) {
		wut = Serial.read();
//		String starter = "To BT: ";
//		Serial.println(starter + wut);
		blueToothSerial.write(wut);
	}
}
