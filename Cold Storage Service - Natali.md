### Requisiti
A company intends to build a ColdStorageService, composed of a set of elements:

1. a service area (rectangular, flat) that includes:
    - an INDOOR port, to enter food (fruits, vegetables, etc. )
    - a ColdRoom container, devoted to store food, upto **MAXW** kg .
    The ColdRoom is positioned within the service area, as shown in the following picture:

![[ColdStorageServiceRoomAnnoted.png]]

2. a DDR robot working as a transport trolley, that is intially situated in its HOME location. The transport trolley has the form of a square of side length **RD**.
    The transport trolley is used to perform a deposit action that consists in the following phases:
    1. pick up a food-load from a Fridge truck located on the INDOOR
    2. go from the INDOOR to the PORT of the ColdRoom
    3. deposit the food-load in the ColdRoom

3. a ServiceAcessGUI that allows an human being to see the current weigth of the material stored in the ColdRoom and to send to the ColdStorageService a request to store new **FW** kg of food. If the request is accepted, the services return a ticket that expires after a prefixed amount of time (**TICKETTIME** secs) and provides a field to enter the ticket number when a Fridge truck is at the INDOOR of the service.

4. a ServiceStatusGUI that allows a Service-manager (an human being) to supervises the state of the service.

#### Alarm requirements

The system includes a Sonar and a Led connected to a RaspberryPi.
The Sonar is used as an ‘alarm device’: when it measures a distance less that a prefixed value **DLIMT**, the transport trolley must be stopped; it will be resumed when Sonar detects again a distance higher than **DLIMT**.

The Led is used as a _warning devices_, according to the following scheme:
- the Led is **off** when the transport trolley is at HOME
- the Led **blinks** while the transport trolley is moving
- the Led is **on** when transport trolley is stopped

### Service users story

The story of the ColdStorageService can be summarized as follows:

1. A Fridge truck driver uses the _ServiceAcessGUI_ to send a request to store its load of **FW** kg. If the request is accepted, the driver drives its truck to the INDOOR of the service, before the ticket exipration time **TICKETTIME**.
    
2. When the truck is at the INDOOR of the service, the driver uses the _ServiceAcessGUI_ to enter the ticket number and waits until the message **charge taken** (sent by the ColdStorageService) appears on the _ServiceAcessGUI_. At this point, the truck should leave the INDOOR.
    
3. ==When the service accepts a ticket, the transport trolley reaches the INDOOR, picks up the food, sends the **charge taken** message and then goes to the ColdRoom to store the food.==
    
4. When the deposit action is terminated, the transport trolley accepts another ticket (if any) or returns to HOME.
    
5. While the transport trolley is moving, the Alarm requirements should be satisfied. However, the transport trolley should not be stopped if some prefixed amount of time (**MINT** msecs) is not passed from the previous stop.
    
6. A _Service-manager_ migtht use the ServiceStatusGUI to see:
    - the **current state** of the transport trolley and it **position** in the room;
    - the **current weigth** of the material stored in the ColdRoom;
    - the **number of store-requests rejected** since the start of the service.

## Analisi del TF23

Nelle discussioni con il committente, sono emerse alcune problematiche:
- Il problema del load-time lungo.
- Il problema del driver distratto (non coerente, rispetto alle due fasi: scarico preceduto da prenotazione).
- Il problema del driver malevolo.
- Il problema di garantire che una risposta venga sempre inviata sempre solo a chi ha fatto la richiesta, anche quando la richiesta è inviata da un ‘alieno’ come una pagine HTML

### Il problema del load-time lungo
Il problema del load-time lungo è stato affrontato da Arnaudo/Munari con l’idea di inviare due messaggi di ‘risposta’ (una per dire al driver che il ticket inviato è valido e una per inviare `chargeTaken`). A questo fine hanno fatto uso diretto della connessione TCP stabilita da una versione prototipale dell’accessGui fatta come GUI JAVA.
Per consentire questa possibilità anche a livello di modellazione qak, in _ActorBasicFsm_ è stato introdotto il metodo storeCurrentRequest() che permette di ricordare la richiesta corrente (cancellata da una _replyTo_). Questo però è un trucco/meccanismo che potrebbe risultare pericoloso.
Meglio affrontare il problema dal punto di vista logico, impostando una interazione a DUE-FASI tra driver e service (compito che può svolgere la _serviceAcessGui_).
- FASE1: il driver invia il ticket e attenda una risposta (immediata) come ad esempio `ticketaccepted/ticketrejected`
- FASE2: il driver invia la richiesta `loaddone` e attenda la risposta (`chargeTaken` o fallimento per cause legate al servizio)

### Il problema del driver distratto
Questo problema ha indotto il committente ad affermare che:
quando un agente esterno (driver) invia il ticket per indurre il servizio a scaricare il truck, si SUPPPONE GARANTITO che il carico del truck sia UGUALE (o al più MINORE) del carico indicato nella prenotazione.
Ciò in quanto non vi sono sensori (bilance , etc) che possano fornire il valore del carico effettivo sul Truck.
#

### Analisi preliminare dei requisiti

![[Architettura_Sprint0.png]]

- per ==doJob==, ==weight==, ==Ticket== e ==RichiestaDeposito== abbiamo bisogno di risposta --> Req/Resp
- ServiceStatusGui si comportarà come un ==Observer== [StreamQActor](C:/Users/lomba/Desktop/iss23/iss23Material/html/QakActors23.html) per aggiornarsi dinamicamente


### Divisione in Sprint
1) Basic Robot + Controller e Cold Storage [[Sprint 1]]
	Lo scopo del primo sprint è avere una prima versione del robot funzionante con la logica di gestione delle richieste a lui relative. Si tratta del core del progetto, senza quello il resto non ha motivo di esistere.
2) Gestione dei ticket: Fake SerAccGui + Cold Storage Service [[Sprint 2]]
	Nel secondo sprint implementeremo il sistema di ticketing, implementeremo una prima interfaccia utente solo per i test. Probabilmente la parte che richiederà più tempo, conviene farla prima di aggiungere led e sonar per non complicare la progettazione.
3) Led e Sonar [[Sprint 3]]
	Nel terzo sprint implementeremo il sistema di led e sonar con tutta la logica rimanente. Dovrebbe essere facilmente implementabile sopra a tutto quello che già è stato creato senza richiedere alcuna modifica.
4) Service Status Gui e grafica bellina [[Sprint 4]]
	L'ultimo sprint si occuperà della service status gui e delle interfacce grafiche finali. Potrebbe richiedere un refactoring parziale delle componenti da osservare, da tenere in considerazione durante lo sviluppo.

### Divisione dei compiti
Ogni Sprint verrà affrontato insieme con divisione dei compiti specifica valutata di volta in volta.

### Valutazione dei tempi
- Sprint 1 con primo esempio funzionante: 1 settimana
- Sprint 2 completato e funzionante: entro la prima settimana di Agosto
- Sprint 3: entro la fine di agosto
- Sprint 4: entro metà settembre


- [x] Primo schema logico dei soli requisiti (non con le modifiche mandate per email)
- [x] Scriviamo il motivo della divisione in sprint 
- [x] e una valutazione dei tempi 
- [x] divisione dei compiti, strategia e motivazioni


