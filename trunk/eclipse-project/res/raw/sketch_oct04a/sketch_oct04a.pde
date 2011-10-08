#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>

AndroidAccessory acc("Stanfy",
		     "ArduinoTesting",
		     "ArduinoTesting",
		     "1.0",
		     "http://www.android.com",
		     "0000000212345678");

/* ------------------------------------------------
* SERIAL COM - HANDELING MULTIPLE BYTES inside ARDUINO - 03_function development
* by beltran berrocal
*
* this prog establishes a connection with the pc and waits for it to send him
* a long string of characters like "hello Arduino!". 
* Then Arduino informs the pc that it heard the whole sentence
*
* the same as examlpe 02 but it deploys 2 reusable functions.
* for doing the same job. 
* readSerialString() and  printSerialString()
* the only problem is that they use global variables instead of getting them passed 
* as parameters. this means that in order to reuse this code you should also copy
* the 4 variables instantiated at the beginning of the code.
* Another problem is that if you expect more than one string at a time 
* you will have to duplicate and change names to all variables as well as the functions.
* Next version should have the possibility to pass the array as a parameter to the function.
*
* created 15 Decembre 2005;
* copyleft 2005 Progetto25zero1  <http://www.progetto25zero1.com>
*
* --------------------------------------------------- */

const int bufferSize = 256;

char serInString[bufferSize];  // array that will hold the different bytes  100=100characters;
                        // -> you must state how long the array will be else it won't work.
int  serInIndx  = 0;    // index of serInString[] in which to insert the next incoming byte
int  serOutIndx = 0;    // index of the outgoing serInString[] array;
byte mac[] = { 0x20, 0xCF, 0x30, 0x9C, 0x39, 0x2A };
byte ip[] = { 10,1,1,30 };
byte gateway[] = { 10,1,1,1 };
byte subnet[] = { 255, 255, 255, 0 };
byte server[4];
byte inited;
/*read a string from the serial and store it in an array
//you must supply the array variable and the index count
void readSerialString (char *strArray, int indx) {
    int sb;                               //declare local serial byte before anything else
    Serial.print("reading Serial String: ");   
    if(serialAvailable()) {     
       while (serialAvailable()){ 
          sb = serialRead();             
          strArray[indx] = sb;
          indx++;
          serialWrite(sb);
       }
    }  
    Serial.println();
}
*/

//read a string from the serial and store it in an array
//this func uses globally set variable so it's not so reusable
//I need to find the right syntax to be able to pass to the function 2 parameters:
// the stringArray and (eventually) the index count
void readSerialString() {
  int sb;
  serInIndx = 0; // reset the buffer  
  while (Serial.available()) { 
    sb = Serial.read();             
    serInString[serInIndx++] = sb;
    //serialWrite(sb);                        //optional confirmation
  }
}

//print the string all in one time
//this func as well uses global variables
void printSerialString() {
  Serial.print("Arduino memorized that you said: ");     
  //loop through all bytes in the array and print them out
  for(int i = 0; i < serInIndx; i++) {
    Serial.print(serInString[i]);    //print out the byte at the specified index
  }        
  Serial.println();
}


void setup() {
  Serial.begin(9600);
  Serial.println("Running");
  
  pinMode(13, OUTPUT);   

  acc.powerOn();  
}

void processAccInput(byte* msg, int len) {
  if(!inited) {
    server[0] = msg[0];
    server[1] = msg[1];
    server[2] = msg[2];
    server[3] = msg[3];
    inited = 1;
    Serial.println("Server ip defined");
    return;
  }  
  Serial.print("Read bytes: ");
  Serial.println(len);
  Serial.println("%%Input from acc:%%");
  Serial.write(msg, len);
  Serial.println();
}

void loop () {
  //simple feedback from Arduino
  
  //read the serial port and create a string out of what you read
  readSerialString();
  
  //do somenthing else perhaps wait for other data or read another Serial string
  byte msg[100];
  if (acc.isConnected()) {
    digitalWrite(13, HIGH);
    int len = acc.read(msg, sizeof(msg), 1);
    if (len > 0) { processAccInput(msg, len); }
  } else {
    digitalWrite(13, LOW);
  }
  
  // transmit data to the accessory
  if (serInIndx > 0) {
    acc.write(serInString, serInIndx);
    printSerialString();
  }
  
}

