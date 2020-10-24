//Programa que configura un soft-AP en la ESP8266

#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <ESP8266mDNS.h>
#include <EEPROM.h>
#include <Wire.h>
#include <Adafruit_MLX90614.h>
#include "Adafruit_MQTT.h"
#include "Adafruit_MQTT_Client.h"

#define AIO_SERVER      "io.adafruit.com"
#define AIO_SERVERPORT  1883                   // use 8883 for SSL
#define AIO_USERNAME    "Biott"
#define AIO_KEY         "9ee5aaa9c2d6431e9937053d699db414"

const char* password = "parrilla";
int ctrSw1 = 14;
int ctrSw2 = 2;
int ctrSw3 = 0;
int ctrSw4 = 16;
/*int lecLed1 = 15;
int lecLed2 = 13;
int lecLed3 = 12;*/
//int encendido = 10;
//int interr = 9;
int tiempo = 100;
int addr = 0;
int conexion = 0;
int temperatura;
boolean conectado = false;
boolean cambioModo = true;
boolean modoAPSTA = false;
String redWiFiString;
String passwordWiFiString;
const char htmlPage[]= R"=====(
<HTML>
  <HEAD>
    <TITLE> PRUEBA IP </TITLE>
  </HEAD>
  <BODY>@ON@@TEMP@@LOCK@@F1@@F2@@F3@@F4@@F5@@F6@@T@</BODY></HTML>)=====";
String ip;
boolean stOnOff = false;
boolean stBloqueo = false;
boolean accessMQTT = false;
boolean inicio = false;
boolean L1 = false;
boolean L2 = false;
boolean L3 = false;
boolean L4 = false;
boolean L5 = false;
boolean L6 = false;
boolean L7 = false;
boolean L8 = false;
boolean L9 = false;

ESP8266WebServer server(80);
WiFiClient client;
Adafruit_MQTT_Client mqtt(&client, AIO_SERVER, AIO_SERVERPORT, AIO_USERNAME, AIO_KEY);

Adafruit_MQTT_Subscribe swOn = Adafruit_MQTT_Subscribe(&mqtt, AIO_USERNAME"/feeds/Encendido");
Adafruit_MQTT_Subscribe swOff = Adafruit_MQTT_Subscribe(&mqtt, AIO_USERNAME"/feeds/Apagado");
Adafruit_MQTT_Subscribe swBloqueo = Adafruit_MQTT_Subscribe(&mqtt, AIO_USERNAME "/feeds/Bloqueo");
Adafruit_MQTT_Subscribe swDesbloqueo = Adafruit_MQTT_Subscribe(&mqtt, AIO_USERNAME "/feeds/Desbloqueo");
Adafruit_MQTT_Subscribe swMas = Adafruit_MQTT_Subscribe(&mqtt, AIO_USERNAME "/feeds/Mas");
Adafruit_MQTT_Subscribe swMenos = Adafruit_MQTT_Subscribe(&mqtt, AIO_USERNAME "/feeds/Menos");
Adafruit_MQTT_Subscribe swFuncion = Adafruit_MQTT_Subscribe(&mqtt, AIO_USERNAME "/feeds/Funcion");

Adafruit_MLX90614 mlx = Adafruit_MLX90614();

void setup()
{
  Serial.begin(115200);
  configurarHW();
  leeEEPROM();
  configurarSTA();
  server.on("/", handleRoot);
  server.begin();
  mlx.begin();
}

void loop(){
  if(!conectado && cambioModo){
    configurarAP();
    cambioModo = false;
  }
  
  server.handleClient();
  
  if(accessMQTT){
    MQTT_connect();
    accessMQTT = false;
    inicio = true;
  }
  
  while(WiFi.status() == WL_CONNECTED && inicio){
    server.handleClient();
    
    if(!mqtt.connected())
      MQTT_connect();
    
    //digitalWrite(interr, LOW);
    //delay(300);
    
    if(Serial.available())
     Serial.println(Serial.readStringUntil('#'));
    
    /*if(digitalRead(encendido)){
      delay(250);
      if(digitalRead(encendido))
        L9 = true;
        
      else
        L9 = false;      
    }*/
    
    Adafruit_MQTT_Subscribe *subscription;
    while ((subscription = mqtt.readSubscription(200))) {
      if (subscription == &swOn) {
        if(!stOnOff){
          Serial.print(F("Got: "));
          Serial.println((char *)swOn.lastread);
          controlSwitches(0,0,1,tiempo);
          stOnOff = true;
        }
      }
      
       if (subscription == &swOff) {
        if(stOnOff){
          Serial.print(F("Got: "));
          Serial.println((char *)swOff.lastread);
          controlSwitches(0,0,1,tiempo);
          stOnOff = false;
        }
      }
        
      if (subscription == &swBloqueo) {
        if(!stBloqueo){
          Serial.print(F("Got: "));
          Serial.println((char *)swBloqueo.lastread);
          controlSwitches(1,1,0,tiempo);
          stBloqueo = true;
        }
      }
      
      if (subscription == &swDesbloqueo) {
        if(stBloqueo){
          Serial.print(F("Got: "));
          Serial.println((char *)swDesbloqueo.lastread);
          controlSwitches(1,1,0,tiempo);
          stBloqueo = false;
        }
      }
      
      if (subscription == &swMas) {
        if(!stBloqueo){
          Serial.print(F("Got: "));
          Serial.println((char *)swMas.lastread);
          for(int i = 0; i < 2; i++){
            controlSwitches(1,0,0,tiempo);
            delay(100);
          }
        }
      }
      
      if (subscription == &swMenos) {
        if(!stBloqueo){
          Serial.print(F("Got: "));
          Serial.println((char *)swMenos.lastread);
          int msj = atoi((char *)swMenos.lastread);
          for(int i = 0; i < 2; i++){
            controlSwitches(0,1,1,tiempo);
            delay(100);
          }
        }
      }
      
      if (subscription == &swFuncion) {
        if(!stBloqueo){
          Serial.print(F("Got: "));
          Serial.println((char *)swFuncion.lastread);
          int msj = atoi((char *)swFuncion.lastread);
          controlSwitches(1,0,1,tiempo);
        }
      }
    }
  }
}

/*void interrupcion(){
  int suma = 0;
  if (digitalRead(lecLed1))
    suma += 1;
  if (digitalRead(lecLed2))
    suma += 2;
  if (digitalRead(lecLed3))
    suma += 4;

  switch (suma){
   case 0:
    L1 = !L1;
    break;
   case 1:
    L2 = !L2;
    break;
   case 2:
    L3 = !L3;
    break;
   case 3:
    L4 = !L4;
    break;
   case 4:
    L5 = !L5;
    break;
   case 5:
    L6 = !L6;
    break;
   case 6:
    L7 = !L7;
    break;
   case 7:
    L8 = !L8;
    break;
   default:
    break;
  }
}*/

void handleRoot(){
  if(modoAPSTA){
    limpiaEEPROM();
    redWiFiString = server.arg("SSID");
    passwordWiFiString = server.arg("password");
    escribeEEPROM();
  }
  
  String webPage = htmlPage;
  String response = server.arg("IPAP");
  String boton = server.arg("Boton");
  String respuesta = server.arg("status");
  
  if(response=="1"){
    server.send(200, "text/html", WiFi.localIP().toString().c_str());
    WiFi.mode(WIFI_STA);
    accessMQTT = true;
  }
  
  else if(boton=="1"){
    controlSwitches(0,0,1,tiempo);
    server.send(200, "text/html", "Enviado");
  }
  
  else if(boton=="2"){
    controlSwitches(0,1,0,tiempo);
    server.send(200, "text/html", "Enviado");
  }
  
  else if(boton=="3"){
    controlSwitches(0,1,1,tiempo);
    server.send(200, "text/html", "Enviado");
  }
  
  else if(boton=="4"){
    controlSwitches(1,0,0,tiempo);
    server.send(200, "text/html", "Enviado");
  }
  
  else if(boton=="5"){
    controlSwitches(1,0,1,tiempo);
    server.send(200, "text/html", "Enviado");
  }
  
  else if(boton.substring(0,1)=="6"){
    if(boton.substring(1)=="1"){
      controlSwitches(1,1,0,tiempo);
    }
     
    else{
      controlSwitches(1,1,0,tiempo);
    }
    server.send(200, "text/html", "Enviado");
  }
  
  else if (respuesta == "1"){
    if (L9)
     webPage.replace("@ON@", "1");
     
    else
     webPage.replace("@ON@", "0");
     
    if (L1)
     webPage.replace("@TEMP@", "1");
     
    else
     webPage.replace("@TEMP@", "0");
    
    if (L2)
     webPage.replace("@LOCK@", "1");
     
    else
     webPage.replace("@LOCK@", "0");
    
    if (L3)
     webPage.replace("@F1@", "1");
     
    else
     webPage.replace("@F1@", "0");
     
    if (L4)
     webPage.replace("@F2@", "1");
     
    else
     webPage.replace("@F2@", "0");
     
    if (L5)
     webPage.replace("@F3@", "1");
     
    else
     webPage.replace("@F3@", "0");
     
    if (L6)
     webPage.replace("@F4@", "1");
     
    else
     webPage.replace("@F4@", "0");
     
    if (L7)
     webPage.replace("@F5@", "1");
     
    else
     webPage.replace("@F5@", "0");
     
    if (L8)
     webPage.replace("@F6@", "1");
     
    else
     webPage.replace("@F6@", "0");
   
    Serial.print("Temperatura = ");
    int temperatura = mlx.readObjectTempC();
    Serial.print(temperatura);
    Serial.println(" C");
    
    webPage.replace("@T@", String(temperatura));
     
     server.send(200, "text/html", webPage);
  }

  else{
    configurarSTA();
    server.send(200, "text/html", String(WiFi.localIP()));
  }
}

//Método que configura un punto de acceso en la ESP8266
void configurarAP()
{
  WiFi.disconnect();
  WiFi.mode(WIFI_AP_STA);
  
  //Para tener un SSID única se agregan los dos últimos
  //bytes de la dirección MAC
  uint8_t mac[WL_MAC_ADDR_LENGTH];
  WiFi.softAPmacAddress(mac);
  String macID = String(mac[WL_MAC_ADDR_LENGTH - 2], HEX) +
                 String(mac[WL_MAC_ADDR_LENGTH - 1], HEX);
  macID.toUpperCase();
  
  String ssidString = "biott" + macID + "Parrilla";

  char ssidChar[redWiFiString.length() + 1];
  memset(ssidChar, 0, ssidString.length() + 1);

  for (int i=0; i<ssidString.length(); i++)
    ssidChar[i] = ssidString.charAt(i);
                 
  WiFi.softAP(ssidChar, password, 1, false);  
}

//Método que configura el hardware del ESP8266
void configurarHW()
{
  //pinMode(interr, INPUT);
  //attachInterrupt(digitalPinToInterrupt(interr), interrupcion, RISING);
  //pinMode(interr, OUTPUT);
  pinMode(ctrSw1, OUTPUT);
  pinMode(ctrSw2, OUTPUT);
  pinMode(ctrSw3, OUTPUT);
  pinMode(ctrSw4, OUTPUT);
  /*pinMode(lecLed1, INPUT);
  pinMode(lecLed2, INPUT);
  pinMode(lecLed3, INPUT);
  pinMode(encendido, INPUT);*/
}

//Método que configura en modo estación la ESP8266
//y se conecta a un AP
void configurarSTA()
{
  if(!modoAPSTA)
    WiFi.mode(WIFI_STA);
  
  char ssidChar[redWiFiString.length() + 1];
  memset(ssidChar, 0, redWiFiString.length() + 1);

  for (int i=0; i<redWiFiString.length(); i++){
    ssidChar[i] = redWiFiString.charAt(i);
    //Serial.print(ssidChar[i]);
  }
  
  //Serial.print("\n");
    
  char passwordChar[passwordWiFiString.length() + 1];
  memset(passwordChar, 0, passwordWiFiString.length() + 1);

  for (int i=0; i<passwordWiFiString.length(); i++){
    passwordChar[i] = passwordWiFiString.charAt(i);
    //Serial.print(passwordChar[i]);
  }
  
  //Serial.print("\n");
  
  Serial.print(ip);
  
  WiFi.begin(ssidChar, passwordChar);
  
  Serial.println(WiFi.localIP());
  
  //Espera de 10 s para conectarse a la red
  while (WiFi.status() != WL_CONNECTED && conexion < 100){
    conexion++;
    delay(100);
  }
  
  if(conexion < 100){
    Serial.println("Conectado a la red WiFi");
    Serial.print("Direccion IP: ");
    Serial.println(WiFi.localIP());
    Serial.printf("MAC: %s\n", WiFi.macAddress().c_str());
    configurarMQTT();
    conectado = true;
    accessMQTT = true;
    modoAPSTA = false;
  }
  
  else{
    Serial.println("Error de conexion a la red WiFi");
    conectado = false;
    modoAPSTA = true;
    conexion = 0;
  }
}

void configurarMQTT(){
  mqtt.subscribe(&swOn);
  mqtt.subscribe(&swOff);
  mqtt.subscribe(&swBloqueo);
  mqtt.subscribe(&swDesbloqueo);
  mqtt.subscribe(&swMas);
  mqtt.subscribe(&swMenos);
  mqtt.subscribe(&swFuncion);
}
  

void MQTT_connect() {
  int8_t ret;

  // Stop if already connected.
  if (mqtt.connected()) {
    return;
  }

  Serial.print("Connecting to MQTT... ");

  uint8_t retries = 3;
  
  while ((ret = mqtt.connect()) != 0) { // connect will return 0 for connected
    Serial.println(mqtt.connectErrorString(ret));
    Serial.println("Retrying MQTT connection in 5 seconds...");
    mqtt.disconnect();
    delay(5000);  // wait 5 seconds
    retries--;
    if (retries == 0) {
      // basically die and wait for WDT to reset me
      while (1);
    }
  }
  Serial.println("MQTT Connected!");
}

void controlSwitches(boolean ctr1, boolean ctr2, boolean ctr3, int tiempo){
  digitalWrite(ctrSw1, ctr1);
  delay(1);
  digitalWrite(ctrSw2, ctr2);
  delay(1);
  digitalWrite(ctrSw3, ctr3);
  delay(1);
  digitalWrite(ctrSw4, 1);
  delay(tiempo);
  digitalWrite(ctrSw1, 0);
  delay(1);
  digitalWrite(ctrSw2, 0);
  delay(1);
  digitalWrite(ctrSw3, 0);
  delay(1);
  digitalWrite(ctrSw4, 0);
  delay(1);
}

void limpiaEEPROM(){
  EEPROM.begin(40);
  
  for(int i = 0; i < 40; i++){
   EEPROM.write(i, 0);
  }
  
  EEPROM.end();
  
  Serial.println("Memoria EEPROM borrada"); 
}

void escribeEEPROM(){
  EEPROM.begin(40);
  int tam = redWiFiString.length();
  char char1[20];
  char char2[20];
  
  addr = 0;
  
  redWiFiString.toCharArray(char1, tam+1);
  
  for(int i = 0; i < tam; i++){
    EEPROM.write(addr+i, char1[i]);
    EEPROM.commit();
  }
  
  for(int i = tam; i < 20; i++){
    EEPROM.write(addr+i, 255);
    EEPROM.commit();
  }
  
  addr = 20;
  
  tam = passwordWiFiString.length();
  
  passwordWiFiString.toCharArray(char2, tam+1);
  
  for(int i = 0; i < tam; i++){
    EEPROM.write(addr+i, char2[i]);
    EEPROM.commit();
  }
  
  for(int i = tam; i < 20; i++){
    EEPROM.write(addr+i, 255);
    EEPROM.commit();
  }
  
  Serial.println("Se hizo escritura en la EEPROM");
}

void leeEEPROM(){
  EEPROM.begin(40);
  byte lectura;
  
  for(int i = 0; i < 20; i++){
    lectura = EEPROM.read(i);
    if(lectura != 255){
      redWiFiString += (char)lectura;
    }
  }
  
  for(int i = 20; i < 40; i++){
    lectura = EEPROM.read(i);
    if(lectura != 255){
      passwordWiFiString += (char)lectura;
    }
  }
  
  //Serial.println(redWiFiString);
  //Serial.println(passwordWiFiString);
  
  Serial.println("\nSe hizo lectura de la EEPROM");
}
  
