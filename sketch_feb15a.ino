#include <SPI.h>
#include <Ethernet.h>
int pin = 0;
String reply = "";
// Set the MAC address
byte mac[] = {0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED};

// Set the IP address
IPAddress ip(192, 168, 1, 177);

// Start a server at port 80 (http)
EthernetServer server(80);

void setup() {
  // Open serial communications
  Serial.begin(9600);

  // start the Ethernet connection and the server
  Ethernet.begin(mac, ip);
  server.begin();

  // Pin 2 - 4 output (leds)
  pinMode(2, OUTPUT);
  pinMode(3, OUTPUT);
  pinMode(4, OUTPUT);
 
  
}


void loop() {
  // Check if client connected
  EthernetClient client = server.available();
  
  if (client) { // If there is a client...
    boolean currentLineIsBlank = true;
    String buffer = ""; // A buffer for the GET request
    
    while (client.connected()) {

      if (client.available()) {
        char c = client.read();// Read the data of the client
        buffer += c; // Store the data in a buffer
        
        if (c == '\n' && currentLineIsBlank) {// if 2x new line ==> Request ended
          // send a standard http response header
          client.println("HTTP/1.1 200 OK");
          client.println(reply);
          client.println("Content-Type: text/html");
          client.println("Connection: close");
          client.println(); // Blank line ==> end response
          break;
        }
        if (c == '\n') { // if New line
          currentLineIsBlank = true;
          buffer = "";  // Clear buffer
        } else if (c == '\r') { // If cariage return...
          //Read in the buffer if there was send "GET /?..."
          if(buffer.indexOf("GET /?led1=1")>=0) { // If led1 = 1
            digitalWrite(4, HIGH); // led 1 > on
            
          }
          if(buffer.indexOf("GET /?led1=0")>=0) { // If led1 = 0
            digitalWrite(4, LOW); // led 1 > off

          }
          if(buffer.indexOf("GET /?led2=1")>=0) { // If led2 = 1
            digitalWrite(2, HIGH); // led 2 > on
            pin=1;
            reply="led2=1";
            client.println("HTTP/1.1 200 led2=1");
          client.println("Content-Type: text/html");
          client.println("Connection: close");
          client.println();
          }
          if(buffer.indexOf("GET /?led2=0")>=0) { // If led2 = 0
            digitalWrite(2, LOW); // led 2 > off
            pin=0;
            reply="led2=0";
            client.println("HTTP/1.1 200 led2=0");
          client.println("Content-Type: text/html");
          client.println("Connection: close");
          client.println();
          }
          if(buffer.indexOf("GET /?led3=1")>=0) { // If led3 = 1
            digitalWrite(3, HIGH); // led 3 > on
          }
          if(buffer.indexOf("GET /?led3=0")>=0) { // If led3 = 0
            digitalWrite(3, LOW); // led 3 > off
          }
          if(buffer.indexOf("GET /?DevicesState")>=0) { // If led3 = 0
            if(pin==1){
              reply = "led2=1";
            }else {
              reply = "led2=0";
            }
         
client.println();
          }
        } else {
          currentLineIsBlank = false;
        }
      }
    }
    delay(1);
    client.stop();
  }
}
