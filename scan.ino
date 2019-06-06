#include <Wire.h>  //ajout de la librairie I2C
void setup() {
  Wire.begin();
  Serial.begin(9600);
  while (!Serial);
  Serial.println("\nI2CScanner");
}

void loop() {

  byte error, adress; 
  int nDevices;
  Serial.print("Scanning...");
  nDevices = 0;
  for (adress = 1; adress<127; adress++)
  {
    Wire.beginTransmission(adress);
    error = Wire.endTransmission();
    if(error ==0)
    {
      Serial.print("I2C device found at adress 0x");
      if (adress<16)
      
        Serial.print("0");
        Serial.print(adress,HEX);
        nDevices++;
    }
      else if (error == 4)
      {
        Serial.print("unknown error at adress 0x");
        if (adress<16)
        Serial.print ("0");
        Serial.println(adress,HEX);
      }
    }
      if (nDevices == 0)
        Serial.println ("No I2C devices found\n");
      else
        Serial.println("done\n");
      delay(3000);
    }
