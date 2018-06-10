#include <SoftwareSerial.h>
#include <Servo.h>

int servoPin = 7; //꼭 수정하기

SoftwareSerial bluetooth(2, 3);
Servo servo;

int angle = 0; //모터 각도
int maxAngle = 30;
char val;
int count = 0;

int countVal(char tempval)
{
  if(tempval == 'a' && tempval == 'c') 
  {
    count = 1;
  }
  else {
    count = 0;
  }
}

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  //HC-06 setup
  bluetooth.begin(9600);
  //servoMotor setup
  servo.attach(servoPin);
}

void loop() {
  // put your main code here, to run repeatedly:
  val = (char)bluetooth.read();
  countVal(val);
  servo.write(160);
  delay(1000);
  servo.write(1);
  delay(1000);
//  if(bluetooth.available() && count == 1)
//  {
//    Serial.write(val);
//    if(val == 'a')
//    {
//      for(angle = 0; angle < maxAngle*2; angle++)
//      {
//        servo.write(angle);
//        delay(30);
//      }
//    } 
//    else if(val == 'c') 
//    {
//      int temp = angle;
//      for(;angle > temp - maxAngle*2;angle--)
//      {
//        servo.write(angle);
//        delay(15);
//      }
//    }
//  }
}

