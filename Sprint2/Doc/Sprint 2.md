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

### Progettazione
Anche qui faccio uno script che si limita a leggere una singola distanza e lascio che sia l'attore a implementare la logica di invocare il sonar ogni x finché non ottengo un valore minore della soglia, in questo modo posso gestire tutti i parametri con un file di config e modificarli in futuro se necessario.

Segnali? Per il sonar va bene dispatch, so perfettamente a chi devo mandarlo (quindi non event) e non mi serve req/resp perché basta inviargliene altri nel peggior dei casi, farlo req/resp complica troppo per nessun vantaggio.

Gestiamo solo il caso in cui mi arrivano più volte gli stessi messaggi perché perdo i messaggi intermedi (ottengo 2 stop di fila perché perdo il continue in mezzo)

sonar in python
```python
import RPi.GPIO as GPIO
import time
import sys

GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)
TRIG = 17
ECHO = 27

GPIO.setup(TRIG,GPIO.OUT)
GPIO.setup(ECHO,GPIO.IN)

GPIO.output(TRIG, False)   #TRIG parte LOW
print ('Waiting a few seconds for the sensor to settle')
time.sleep(0,5)

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
   print ( distance )
   sys.stdout.flush()   #Importante!
   time.sleep(0.25)
```

facciamo solo uno script che accende e uno script che spegne e ci pensa l'attore ad invocare lo script secondo bisogno per mostrare lo stato corrente, la logica di lampeggiamento è lasciata all'attore led da gestire e non allo script in shell.

led in python
``` python
#File: LedControl.py
import time
import RPi.GPIO as GPIO

LED_PIN = 21
tempo = 1 #in secondi

#setup
GPIO.setmode(GPIO.BCM)
GPIO.setup(LED_PIN,GPIO.OUT)

#basic blink
GPIO.output(LED_PIN,GPIO.HIGH)
time.sleep(tempo)
GPIO.output(LED_PIN,GPIO.LOW)
time.sleep(tempo)

GPIO.cleanup()
```

### Deployment
#### Deployment on RaspberryPi 3B/3B+
![[Pasted image 20231129180114.png]]
##### Led
- braccino corto: pin fisico 39 (GND)
- braccino lungo: pin fisico 40 (GPIO21)
##### Sonar
- VCC : pin fisico 4 (+5v)
- GND : pin fisico 6 (GND)
- TRIG: pin fisico 11 (GPIO 17)
- ECHO: pin fisico 13 (GPIO 27)


# 
----------------

| Lica Uccini              | Luca Lombardi              | Giacomo Romanini              |
| ------------------------ | -------------------------- | ----------------------------- |
| ![[LisaUccini.png\|180]] | ![[LucaLombardi.jpg\|245]] | ![[GiacomoRomanini.jpg\|180]] |
| [github: LisaIU00](https://github.com/LisaIU00)    | [github: Lombax99](https://github.com/Lombax99)             | [github: RedDuality](https://github.com/RedDuality)                              |

