#include <SPI.h>
#include <Ethernet.h>
#include <EEPROM.h>
 

long timeOn=0;
long timeOff=0;
long cons =0;
long samplecount=0;
long consAvg=0;
long totalCons = 0;
float vpc=4.8828125;
bool sensorOn = true;
int month=0;

String buffer = "";
String myreply = "";


//pins 2 3 4 5 => relay control
//pin A1 => sensor data

int outlet1Relay = 2;
int outlet2Relay = 3;
int sensorOn_Relay = 6;
int sensorOff_Relay = 7;
int VCC1 = 10;
int VCC2 = 11;

#define sensorData  A1

// Set the MAC address
byte mac[] = {0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED};

// Set the IP address
IPAddress ip(192, 168, 1, 178);

// Start a server at port 80 (http)
EthernetServer server(80);



void reply(EthernetClient client){
  client.println("HTTP/1.1 200 ok");
  client.println();
}

void setup() {
  // Open serial communications
  Serial.begin(9600);
  // start the Ethernet connection and the server
  Ethernet.begin(mac, ip);
  server.begin();

  pinMode(outlet1Relay, OUTPUT);
  pinMode(outlet2Relay, OUTPUT);
  pinMode(sensorOn_Relay, OUTPUT);
  pinMode(sensorOff_Relay, OUTPUT);
  pinMode(VCC1,OUTPUT);
  pinMode(VCC2,OUTPUT);
  pinMode(sensorData,INPUT);
}


void loop() {
  digitalWrite(VCC1, HIGH);
  digitalWrite(VCC2,LOW);


  
  // Check if client connected
  EthernetClient client = server.available();
  if (client)
   checkClientRequest(client);
  
  if(sensorOn && millis() > timeOn + 60000){
    //turn off sensor
    digitalWrite(sensorOff_Relay, HIGH);
    digitalWrite(sensorOn_Relay, LOW);

    timeOff=millis();
    sensorOn= false;
    consAvg = cons/samplecount;
    totalCons = EEPROMReadlong(0)+consAvg;
    EEPROMWritelong(0,totalCons);
    
    }else if(!sensorOn && millis() > timeOff + 240000){
      //turn on sensor
      digitalWrite(sensorOn_Relay, HIGH);
      digitalWrite(sensorOff_Relay, LOW);
      timeOn=millis();
      samplecount = 0;
      cons=0;
      sensorOn = true;
    }else if(sensorOn && millis() > timeOn + (6000*samplecount)){
    //read sensor
    digitalWrite(sensorOn_Relay, HIGH);
    /////////////////////////////////////////////////////////
    //sq??????? 
    cons += sq(analogRead(sensorData)-512);
    //////////////////////////////////////////////////////////
    samplecount++;
  }
}


long EEPROMReadlong(long address) {
  long four = EEPROM.read(address);
  long three = EEPROM.read(address + 1);
  long two = EEPROM.read(address + 2);
  long one = EEPROM.read(address + 3);
 
  return ((four << 0) & 0xFF) + ((three << 8) & 0xFFFF) + ((two << 16) & 0xFFFFFF) + ((one << 24) & 0xFFFFFFFF);
}

void EEPROMWritelong(int address, long value) {
  byte four = (value & 0xFF);
  byte three = ((value >> 8) & 0xFF);
  byte two = ((value >> 16) & 0xFF);
  byte one = ((value >> 24) & 0xFF);
 
  EEPROM.write(address, four);
  EEPROM.write(address + 1, three);
  EEPROM.write(address + 2, two);
  EEPROM.write(address + 3, one);
}

void checkClientRequest(EthernetClient client){
   boolean currentLineIsBlank = true;
   while (client.connected()) {
     if (client.available()) {
       char c = client.read();// Read the data of the client
       buffer += c; // Store the data in a buffer
       if (c == '\n' && currentLineIsBlank) {
         // if 2x new line ==> Request ended
         // send a standard http response header
         client.println("HTTP/1.1 200 Ok");
         client.println("Content-Type: text/html");
         client.println("Connection: close");
         client.println(myreply);
         client.println(); // Blank line ==> end response
         delay(1);
         client.stop();
       }
       if (c == '\n') { // if New line
         currentLineIsBlank = true;
         buffer = "";  // Clear buffer
       }else if (c == '\r') { 
         handelRequest(client);
       }else{
         currentLineIsBlank = false;
       }
     }
   }
 }



void handelRequest(EthernetClient client){
  if(buffer.indexOf("GET /?device1=1")>=0) { 
    reply(client);
    myreply="device1=1";
    digitalWrite(outlet1Relay, HIGH); // device 1 > on
    }
  if(buffer.indexOf("GET /?device1=0")>=0) {
    reply(client);
    myreply="device1=0";
    digitalWrite(outlet1Relay, LOW); // device 1 > off
    }
  if(buffer.indexOf("GET /?device2=1")>=0) {
    reply(client);
    myreply="device2=1";
    digitalWrite(outlet2Relay, HIGH); // device 2 > on
    }
  if(buffer.indexOf("GET /?device2=0")>=0) {
    reply(client);
    myreply="device2=0"; 
    digitalWrite(outlet2Relay, LOW); // device 2 > off
    }
  if(buffer.indexOf("GET /?device3=1")>=0) {
    reply(client);
    myreply="device3=1";
    digitalWrite(4, HIGH); // device 3 > on
    }
  if(buffer.indexOf("GET /?device3=0")>=0) {
    reply(client);
    myreply="device3=0";
    digitalWrite(4, LOW); // device 3 > off
    }
  if(buffer.indexOf("GET /?device4=1")>=0) {
    reply(client);
    myreply="device4=1";
    digitalWrite(5, HIGH); // device 3 > on
    }
  if(buffer.indexOf("GET /?device4=0")>=0) {
    reply(client);
    myreply="device4=0";
    digitalWrite(5, LOW); // device 3 > off
    }
  if(buffer.indexOf("GET /?DevicesStates")>=0) { 
    if(digitalRead(outlet1Relay)==HIGH){
      reply(client);
      myreply += "device1=1";
     }else if(digitalRead(outlet1Relay)==LOW){
      reply(client);
      myreply += "device1=0";
      }
            
    if(digitalRead(outlet2Relay)==HIGH){
     reply(client);
     myreply += "device2=1";
     }else if(digitalRead(outlet2Relay)==LOW){
       reply(client);
       myreply += "device2=0";
       }
     if(digitalRead(4)==HIGH){
      client.println("HTTP/1.1 200 ok");
      client.println();
      myreply += "device3=1";
      }else if(digitalRead(4)==LOW){
         reply(client);
         myreply += "device3=0";
         }
     if(digitalRead(5)==HIGH){
       reply(client);
       myreply += "device4=1";
     }else if(digitalRead(5)==LOW){
       reply(client);
       myreply += "device4=0";
     }
  }
  if(buffer.indexOf("GET /?getCons")>=0){
   reply(client);
   //myreply = ("cons=" + 100);
   myreply = "cons=";
   myreply += EEPROMReadlong(0);     
  }
}
