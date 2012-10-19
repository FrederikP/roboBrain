################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
D:/dev/Sketchbook/libraries/MeetAndroid/MeetAndroid.cpp 

OBJS += \
./MeetAndroid/MeetAndroid.o 

CPP_DEPS += \
./MeetAndroid/MeetAndroid.d 


# Each subdirectory must supply rules for building sources it contributes
MeetAndroid/MeetAndroid.o: D:/dev/Sketchbook/libraries/MeetAndroid/MeetAndroid.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"D:\dev\arduino\hardware\arduino\cores\arduino" -I"D:\dev\arduino\hardware\arduino\variants\standard" -I"D:\dev\android_dev\robot_ws\YARP" -I"D:\dev\Sketchbook\libraries\MeetAndroid" -I"D:\dev\Sketchbook\libraries\URM37" -I"D:\dev\arduino\libraries\Servo" -D__IN_ECLIPSE__=1 -DUSB_VID= -DUSB_PID= -DARDUINO=101 -Wall -Os -ffunction-sections -fdata-sections -fno-exceptions -g -mmcu=atmega328p -DF_CPU=16000000UL -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)"  -c -o "$@" -x c++ "$<"
	@echo 'Finished building: $<'
	@echo ' '


