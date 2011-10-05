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

int  serIn;             // var that will hold the bytes-in read from the serialBuffer
char serInString[100];  // array that will hold the different bytes  100=100characters;
                        // -> you must state how long the array will be else it won't work.
int  serInIndx  = 0;    // index of serInString[] in which to insert the next incoming byte
int  serOutIndx = 0;    // index of the outgoing serInString[] array;


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
void readSerialString () {
    int sb;   
    if(Serial.available()) { 
       //Serial.print("reading Serial String: ");     //optional confirmation
       while (Serial.available()){ 
          sb = Serial.read();             
          serInString[serInIndx] = sb;
          serInIndx++;
          //serialWrite(sb);                        //optional confirmation
       }
       //Serial.println();
    }  
}

//print the string all in one time
//this func as well uses global variables
void printSerialString() {
   if( serInIndx > 0) {
      Serial.print("Arduino memorized that you said: ");     
      //loop through all bytes in the array and print them out
      for(serOutIndx=0; serOutIndx < serInIndx; serOutIndx++) {
          Serial.print( serInString[serOutIndx] );    //print out the byte at the specified index
          //serInString[serOutIndx] = "";            //optional: flush out the content
      }        
      //reset all the functions to be able to fill the string back with content
      serOutIndx = 0;
      serInIndx  = 0;
      Serial.println();
   }

}


void setup() {
  Serial.begin(9600);
  Serial.println("Hello World");

  acc.powerOn();  
}

void loop () {
  //simple feedback from Arduino
  
  //read the serial port and create a string out of what you read
  //readSerialString(serInString, serInIndx);
  readSerialString();
  
  //do somenthing else perhaps wait for other data or read another Serial string
  byte msg[100];
  if (acc.isConnected()) {
    Serial.println ("Accessory is connected");
    int len = acc.read(msg, sizeof(msg), 1);
    Serial.print("Readed bytes: ");
    Serial.println(len);
    if (len > 0) {
      Serial.write(msg, len);
      Serial.println();
     }
    
  } else {
    Serial.println ("Accessory is not connected");
  }
  
  //try to print out collected information. it will do it only if there actually is some info.
//  printSerialString();
   if( serInIndx > 0) {
     acc.write(serInString, serInIndx);
     
      Serial.print("Arduino memorized that you said: ");     
      //loop through all bytes in the array and print them out
      for(serOutIndx=0; serOutIndx < serInIndx; serOutIndx++) {
          Serial.print( serInString[serOutIndx] );    //print out the byte at the specified index
          //serInString[serOutIndx] = "";            //optional: flush out the content
      }        
      //reset all the functions to be able to fill the string back with content
      serOutIndx = 0;
      serInIndx  = 0;
      Serial.println();
   }
  
  //slows down the visualization in the terminal
  delay(1000);
}

