################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
D:/dev/Sketchbook/libraries/URM37/SoftwareSerial.cpp \
D:/dev/Sketchbook/libraries/URM37/URMSerial.cpp 

OBJS += \
./URM37/SoftwareSerial.o \
./URM37/URMSerial.o 

CPP_DEPS += \
./URM37/SoftwareSerial.d \
./URM37/URMSerial.d 


# Each subdirectory must supply rules for building sources it contributes
URM37/SoftwareSerial.o: D:/dev/Sketchbook/libraries/URM37/SoftwareSerial.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"D:\dev\arduino\hardware\arduino\cores\arduino" -I"D:\dev\arduino\hardware\arduino\variants\standard" -I"D:\dev\android_dev\robot_ws\ArduinoSensorGraph" -I"D:\dev\Sketchbook\libraries\MeetAndroid" -I"D:\dev\Sketchbook\libraries\URM37" -D__IN_ECLIPSE__=1 -DUSB_VID= -DUSB_PID= -DARDUINO=101 -Wall -Os -ffunction-sections -fdata-sections -fno-exceptions -g -mmcu=atmega328p -DF_CPU=16000000UL -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)"  -c -o "$@" -x c++ "$<"
	@echo 'Finished building: $<'
	@echo ' '

URM37/URMSerial.o: D:/dev/Sketchbook/libraries/URM37/URMSerial.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"D:\dev\arduino\hardware\arduino\cores\arduino" -I"D:\dev\arduino\hardware\arduino\variants\standard" -I"D:\dev\android_dev\robot_ws\ArduinoSensorGraph" -I"D:\dev\Sketchbook\libraries\MeetAndroid" -I"D:\dev\Sketchbook\libraries\URM37" -D__IN_ECLIPSE__=1 -DUSB_VID= -DUSB_PID= -DARDUINO=101 -Wall -Os -ffunction-sections -fdata-sections -fno-exceptions -g -mmcu=atmega328p -DF_CPU=16000000UL -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)"  -c -o "$@" -x c++ "$<"
	@echo 'Finished building: $<'
	@echo ' '


