#include <SoftwareSerial.h>
#include <Servo.h>

int servoPin = 7; //꼭 수정하기

SoftwareSerial bluetooth(2, 3);
Servo servo;

int angle = 0; //모터 각도
int maxAngle = 30;
char val;
int count = 0;


void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  //HC-06 setup
  bluetooth.begin(9600);
  //servoMotor setup
  servo.attach(servoPin);
}

void loop() {
  // 수평
  // servo.write(60);
  // 불 켬
  // servo.write(10);
  // 불 끔
  // servo.write(110);
  
  if(bluetooth.available())
  {
    if(count == 0)
    {
      servo.write(60);
      count = 1;
    }
    val = (char)bluetooth.read();
    Serial.write(val);
    if(val == 'a')
    {
      servo.write(10);
    } 
    else if(val == 'b') 
    {
      servo.write(110);
    }
  }
}

