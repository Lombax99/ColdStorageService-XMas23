### Goal dello Sprint 2
Implementazione di Led e Sonar su RaspberryPi.
> [!NOTE]- Descrizione
> Nel secondo sprint verranno implementati il sistema di led e sonar con la logica ad essi associata. I due componenti si troveranno su un dispositivo esterno e dovranno interagire con il sistema remotamente.

- [ ] potrei voler cambiare questo grafico per mostrare anche la parte legata a Spring...
Modello dello [[Sprint 1.1 - V3|sprint precedente]].
![[ArchitetturaLogica_Sprint1.1.png]]
### Requisiti

![[ColdStorageServiceRoomAnnoted.png]]
[[Cold Storage Service - Natali V3#Requisiti|Requisiti]]
#### Alarm requirements

The system includes a Sonar and a Led connected to a RaspberryPi.
The Sonar is used as an ‘alarm device’: when it measures a distance less that a prefixed value **DLIMT**, the transport trolley must be stopped; it will be resumed when Sonar detects again a distance higher than **DLIMT**.

The Led is used as a _warning devices_, according to the following scheme:
- the Led is **off** when the transport trolley is at HOME
- the Led **blinks** while the transport trolley is moving
- the Led is **on** when transport trolley is stopped

### Analisi dei Requisiti
[[Cold Storage Service - Natali V3#Analisi preliminare dei requisiti|requisiti sprint 0]]

### Domande al Committente
Il committente fornisce software relativo al Led e al Sonar?
Il LED può/deve essere connesso allo stesso RaspberryPi del sonar?
Il valore `DLIMIT` deve essere cablato nel sistema o è bene sia definibile in modo configurabile dall’utente finale?

### Analisi del Problema
- si tratta di realizzare il software per un **sistema distribuito** costituito da due nodi di elaborazione: un RaspberryPi e un PC convenzionale;
- i due nodi di elaborazione devono potersi scambiare informazione via rete, usando supporti WIFI;

- Sonar sarà un dispositivo di Input mentre led sarà un dispositivo di output.

1. è opportuno incapsulare i componenti disponibli entro altri componenti capaci di interagire via rete? Una fonte di ispirazione in questo senso è il concetto di [DigitalTwin](https://en.wikipedia.org/wiki/Digital_twin);
2. dove è più opportuno inserire la ‘businenss logic’? In un oggetto che estende il sonar o il `radarSupport`? Oppure è meglio introdurre un terzo componente?
> Seguendo il [Principio di singola responsabilità](https://it.wikipedia.org/wiki/Principio_di_singola_responsabilit%C3%A0) (e un pò di buon senso) la realizzazione degli use-cases applicativi non deve essere attribuita al software di gestione dei dispositivi di I/O.
> Lasciamo che sia il controller a gestire la logica.
> Il `Controller` deve ricevere in ingresso i dati del sensore `HC-SR04`, elaborarli e inviare comandi al Led.

3. quale forma di interazione è più opportuna? diretta/mediata (aka observer), sincrona/asincrona?



### Test Plan
Stato del led che si aggiorna correttamente.
Controller che modifica correttamente lo stato dopo aver ricevuto un segnale dal sonar.

Il testing di un sonar riguarda due aspetti distinti:
1. il test sul corretto funzionamento del dispositivo in quanto tale. Supponendo di porre di fronte al Sonar un ostacolo a distanza D, il Sonar deve emettere dati di valore D +/- epsilon.
2. il test sul corretto funzionamento del componente software responsabile della trasformazione del dispositivo in un produttore di dati consumabili da un altro componente.

Test implementato: 
Supponendo di porre di fronte al Sonar un ostacolo a distanza D, il BasicRobot deve farmarsi, riparte solo quando non è presente alcun ostacolo di fronte al Sonar di distanza minore di D.
```
@Test  
public void mainUseCaseTest(){  
    //connect to port  
    try{  
        Socket client= new Socket("localhost", 8040);  
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));  
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));  
  
        out.write("msg(depositRequestF,request,test2,facade,depositRequestF(1),1)\n");  
        out.flush();  
        //wait for response  
        String response= in.readLine();  
        response = response.split(",")[4];  
        String ticket = response.replace("acceptF(","");  
        ticket = ticket.replace(")","");  
  
        out.write("msg(checkmyticketF,request,test2,facade,checkmyticketF(" + ticket + "),1)\n");  
        out.flush();  
        String responseC= in.readLine();  
  
        out.write("msg(loaddoneF,request,test2,facade,loaddoneF(1),1)\n");  
        out.flush();  
        String responseL= in.readLine();  
  
  
        System.out.println("sleep 2 seconds");  
        TimeUnit.SECONDS.sleep(4);  
  
        out.write("msg(stop,dispatch,test2,controller,stop(),1)\n");  
  
  
        System.out.println("sleep 1 seconds");  
        TimeUnit.SECONDS.sleep(1);  
  
        out.write("msg(getrobotstate,request,test2,robotpos,getrobotstate(ARG),1)\n");  
        out.flush();  
        String responsePos1= in.readLine();  
        responsePos1 = responsePos1.split(",")[4];  
        System.out.println("pos1: "+responsePos1); //robotstate(pos(0,4),DOWN)  
  
        System.out.println("sleep 1 seconds for pos");  
        TimeUnit.SECONDS.sleep(1);  
  
        out.write("msg(getrobotstate,request,test2,robotpos,getrobotstate(ARG),1)\n");  
        out.flush();  
        String responsePos2= in.readLine();  
        responsePos2 = responsePos2.split(",")[4];  
        System.out.println("pos2: "+responsePos2);   //robotstate(pos(0,4),DOWN)  
  
        assertTrue(responsePos1.equalsIgnoreCase(responsePos2));  
  
        out.write("msg(continue,dispatch,test2,controller,continue(),1)\n");  
  
  
        System.out.println("sleep 1 seconds for pos");  
        TimeUnit.SECONDS.sleep(1);  
  
        out.write("msg(getrobotstate,request,test2,robotpos,getrobotstate(ARG),1)\n");  
        out.flush();  
        String responsePos3= in.readLine();  
  
        System.out.println("pos3: "+responsePos3);  
  
        assertFalse(responsePos1.equalsIgnoreCase(responsePos3));  
  
  
    }catch(Exception e){  
        fail();  
        System.out.println(e.getStackTrace());  
    }  
}
```

### Progettazione
Anche qui faccio uno script che si limita a leggere una singola distanza e lascio che sia l'attore a implementare la logica di invocare il sonar ogni x finché non ottengo un valore minore della soglia, in questo modo posso gestire tutti i parametri con un file di config e modificarli in futuro se necessario.

Segnali? Per il sonar va bene dispatch, so perfettamente a chi devo mandarlo (quindi non event) e non mi serve req/resp perché basta inviargliene altri nel peggior dei casi, farlo req/resp complica troppo per nessun vantaggio.

Gestiamo solo il caso in cui mi arrivano più volte gli stessi messaggi perché perdo i messaggi intermedi (ottengo 2 stop di fila perché perdo il continue in mezzo)

sonar in python
```python
# File: sonar.py
import RPi.GPIO as GPIO
import time

GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)
TRIG = 17
ECHO = 27

GPIO.setup(TRIG,GPIO.OUT)
GPIO.setup(ECHO,GPIO.IN)

GPIO.output(TRIG, False)   #TRIG parte LOW
print ('Waiting a few seconds for the sensor to settle')
time.sleep(2)

while True:
   GPIO.output(TRIG, True)    #invia impulsoTRIG
   time.sleep(0.00001)
   GPIO.output(TRIG, False)

   #attendi che ECHO parta e memorizza tempo
   while GPIO.input(ECHO)==0:
      pulse_start = time.time()

   # register the last timestamp at which the receiver detects the signal.
   while GPIO.input(ECHO)==1:
      pulse_end = time.time()

   pulse_duration = pulse_end - pulse_start
   distance = pulse_duration * 17165   #distance = vt/2
   distance = round(distance, 1)
   #print ('Distance:',distance,'cm')
   print ( distance )
   sys.stdout.flush()   #Importante!
   time.sleep(0.25)
```

sonar in c
``` c
#include <iostream>
#include <wiringPi.h>
#include <fstream>
#include <cmath>
  
//#define TRUE 1
//Wiring Pi numbers for radar with stepper
#define TRIG 0  //4
#define ECHO 2  //5
  
using namespace std;
/*
 * in the directory: of SonarAlone.c:
1)  [ sudo ../../pi-blaster/pi-blaster ] if servo
2)  nano
sudo ./SonarAlone
In nat/servosonar:
sudo java -jar   SonarAloneP2PMain.jar
sudo python radar.py
 */
void setup() {
    wiringPiSetup();
    pinMode(TRIG, OUTPUT);
    pinMode(ECHO, INPUT);
  
    //TRIG pin must start LOW
    digitalWrite(TRIG, LOW);
    delay(30);
}
  
int getCM() {
    //Send trig pulse
    digitalWrite(TRIG, HIGH);
    delayMicroseconds(20);
    digitalWrite(TRIG, LOW);
  
    //Wait for echo start
    while(digitalRead(ECHO) == LOW);
  
    //Wait for echo end
    long startTime = micros();
    while(digitalRead(ECHO) == HIGH);
    long travelTime = micros() - startTime;
  
    //Get distance in cm
    int distance = travelTime / 58;
  
    return distance;
}
  
int main(void) {
    int cm ;    
    setup();
    while(1) {
        cm = getCM();
        cout <<  cm <<   endl;
        delay(300);
    }
    return 0;
}
```

facciamo solo uno script che accende e uno script che spegne e ci pensa l'attore ad invocare lo script secondo bisogno per mostrare lo stato corrente, la logica di lampeggiamento è lasciata all'attore led da gestire e non allo script in shell.

led in python
``` python
#File: LedControl.py
import sys
import RPi.GPIO as GPIO

GPIO.setmode(GPIO.BCM)
GPIO.setup(25,GPIO.OUT)

for line in sys.stdin:
   print(line)
   v = float(line)
   if v <= 10 :
      GPIO.output(25,GPIO.HIGH)
   else:
      GPIO.output(25,GPIO.LOW)
```

led in shellscript (blink)
```sh
echo Unexporting.
echo 25 > /sys/class/gpio/unexport #
echo 25 > /sys/class/gpio/export #
cd /sys/class/gpio/gpio25 #

echo Setting direction to out.
echo out > direction #
echo Setting pin high.
echo 1 > value #
sleep 1 #
echo Setting pin low
echo 0 > value #
sleep 1 #
echo Setting pin high.
echo 1 > value #
sleep 1 #
echo Setting pin low
echo 0 > value #
```
led in shellscript (single turnOn)
``` shell
gpio readall #
echo Setting direction to out
gpio mode 6 out #
echo Write 1
gpio write 6 1 #
sleep 1 #
echo Write 0
gpio write 6 0 #
```
### Deployment



# 
----------------

| Lica Uccini              | Luca Lombardi              | Giacomo Romanini              |
| ------------------------ | -------------------------- | ----------------------------- |
| ![[LisaUccini.png\|180]] | ![[LucaLombardi.jpg\|245]] | ![[GiacomoRomanini.jpg\|180]] |
| [github: LisaIU00](https://github.com/LisaIU00)    | [github: Lombax99](https://github.com/Lombax99)             | [github: RedDuality](https://github.com/RedDuality)                              |

