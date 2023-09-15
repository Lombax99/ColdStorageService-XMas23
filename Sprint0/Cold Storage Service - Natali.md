### Goal dello Sprint 0
- individuare la struttura principale e le macro-entità del sistema e le loro interazioni.
- definire un piano di lavoro.
- ecc...

### Requisiti
A company intends to build a ColdStorageService, composed of a set of elements:

1. a service area (rectangular, flat) that includes:
    - an INDOOR port, to enter food (fruits, vegetables, etc. )
    - a ColdRoom container, devoted to store food, upto **MAXW** kg .
    The ColdRoom is positioned within the service area, as shown in the following picture:

![[ColdStorageServiceRoomAnnoted.png]]

2. a ==DDR robot== working as a transport trolley, that is intially situated in its HOME location. The transport trolley has the form of a square of side length **RD**.
    The transport trolley is used to perform a deposit action that consists in the following phases:
    1. pick up a food-load from a Fridge truck located on the INDOOR
    2. go from the INDOOR to the PORT of the ColdRoom
    3. deposit the food-load in the ColdRoom

3. a ServiceAcessGUI that allows an human being to see the current weigth of the material stored in the ColdRoom and to send to the ColdStorageService a request to store new **FW** kg of food. If the request is accepted, the services return a ==ticket== that expires after a prefixed amount of time (==**TICKETTIME** secs==) and provides a field to enter the ticket number when a Fridge truck is at the INDOOR of the service.

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
    
3. When the service accepts a ticket, the transport trolley reaches the INDOOR, picks up the food, sends the **charge taken** message and then goes to the ColdRoom to store the food.
    
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
quando un agente esterno (driver) invia il ticket per indurre il servizio a scaricare il truck, si SUPPPONE GARANTITO che il carico del truck sia UGUALE del carico indicato nella prenotazione.
Ciò in quanto non vi sono sensori (bilance , etc) che possano fornire il valore del carico effettivo sul Truck.

### Analisi preliminare dei requisiti

![[Architettura_Sprint0.png]]
 NOTA: da decidere se aggiungere un collegamento indefinito tra Transport Trolley e Led + Sonar e se collegare allo stesso modo Cold Storage Service con Transport Trolley, inoltre potremmo dover aggiungere il Basic Robot come entità associata a Transport Trolley.
 NOTA 2: fare il grafico con gli script python del prof? Vorrebbe dire fare il qak... che palle.
##### ==Service Area==
Area rettangolare di dimensione L * l. L'area sarà suddivisa in una griglia con coordinate.
Abbiamo richiesto al committente le dimensioni in metri della Service Area: 9m * 6m .

##### ==HOME==
Zona della Service Area corrispondente alle coordinate (0,0) 

##### ==INDOOR port==
Zona della Service Area corrispondente alle coordinate (0,5)

##### ==Porta della ColdRoom==
Lato della ColdRoom che si affaccia sull'area di coordinate (6,2). Transport Trolley dovrà trovarsi in questa posizione per interagire con ColdRoom.
##### ==ColdRoom Container==
Contenitore in posizione fissa in Service Area,  il cui punto di accesso è la Porta della ColdRoom, in grado di ricevere e contenere cibo da un lato specifico. Ha una capienza pari a MAXW kg.
Abbiamo richiesto al committente la capienza massima e la grandezza del container. Dalla risposta del committente è venuto fuori che:
- MAXW corrisponde a 100 kg
- la grandezza di ColdRoom Container è 1m * 1m

##### ==DDR robot==
*Differential Drive Robot*, vedi [DDR](https://www.youtube.com/watch?v=aE7RQNhwnPQ).
[robot](file:///C:/Users/lomba/Desktop/iss23/iss23Material/html/BasicRobot23.html)

##### ==Transport trolley==
Transport trolley è un DDR robot. I comandi che è in grado di compiere sono descritti nell'apposita [documentazione](file:///C:/Users/lomba/Desktop/iss23/iss23Material/html/BasicRobot23.html) .
Abbiamo richiesto al committente la dimensione del transport trolley: corrisponde ad un quadrato di lunghezza RD = 1 m.

##### ==Food-load==
Carico (in kg) che il robot caricherà da Indoor e depositerà in ColdRoom Container.

##### ==Current weight==
Quantità di cibo attualmente contenuto in ColdRoom definito in base al peso.

##### ==TicketTime==
Costante intera che indica il tempo di vita massimo di un Ticket in secondi.

##### ==Ticket==
Il Ticket è una stringa che rappresenta il permesso di scarico concesso ad un determinato FridgeTruck.

##### ==ServiceAccesGUI==
GUI che permette ai driver di:
- visualizzare la quantità di cibo (in peso) contenuta all'interno di ColdRoom;
- richiedere il permesso di scaricare la merce dal Fridge Truck, ovvero richiedere la generazione di un Ticket a lui assegnato da presentare in un secondo momento;
- presentare il Ticket assegnatogli in precedenza nel momento in cui il driver arriva in INDOOR port;
- inviare la richiesta "loadDone" quando il driver è pronto a scaricare, inviando l'effettivo peso contenuto nel Fridge Truck.

##### ==ColdStorageService==
ColdStorageService è un componente del sistema che si occupa di gestire le richieste di scarico merce da parte dei driver. Si occupa quindi di:
- ricevere le richieste di permesso di scarico;
- generare Ticket assegnati al singolo driver che ne ha fatto richiesta;
- ricevere Ticket nel momento in cui il driver arriva in INDOOR;
- verificare la validità dei Ticket ricevuti, ovvero verificare se questi sono scaduti o meno.

NOTA: potremmo creare una tabella delle interazioni con _Sender, Reciver, Descrizione, Body, Note_
- ==WeightRequest==, ==StoCazzoDiTicket==, ==DepositRequest== e ==LoadDone== saranno **Req/Resp**
	- DepositRequest; riceve un ticket come risposta o rejected in caso di fallimento
	- Ticket: deve riceve risposta in caso di ticket accettato o rifiutato
	- LoadDone: riceve ChargeTaken da requisiti
	- WeightRequest: riceve lo spazio disponibile in coldroom
	
- ServiceStatusGui si comportarà come un ==Observer== [StreamQActor](C:/Users/lomba/Desktop/iss23/iss23Material/html/QakActors23.html) 
	`NOTA: SSG dovrà aggiornarsi dinamicamente e presentare i dati correnti ad ogni istante`
	
- Quasi tutto il lavoro passa attraverso ==ColdStorageService==, dalla gestione dei ticket alla logica di controllo del TransportTrolley --> ==Da valutare una possibile divisione in più componenti.==
	
- ==ColdRoom== è stato modellato come un ==Attore==.
	ColdRoom da requisiti aumenta solo di peso, è logico pensare che prima o poi qualcuno debba svuotare il contenitore. Prevediamo quindi che in futuro la ColdRoom debba interagire con componenti esterni al progetto, motivo per il quale è conveniente definire ColdRoom come un Attore.
	Inoltre questa scelta semplifica la gestione dell'interazione tra ColdRoom e ServiceStatusGui.
	EXTRA: si alleggerisce il lavoro di ColdStorageService e si segue il principio di singola responsabilità.
	
- Da requisiti il sistema sarà distribuito su almeno due contesti separati.


### Possibili ulteriori note del committente
Ci sta scrivere a parte le domande e le risposte che abbiamo fatto a Natali, cose come la dimensione della stanza, casi particolari da gestire, come affrontare la sicurezza ecc...

### Divisione in Sprint
1) Basic Robot + Cold StorageService [[Sprint 1.0]]
	- [ ] sistema descrizione 
	`Lo scopo del primo sprint è avere una prima versione del robot funzionante con la logica di gestione delle richieste a lui relative. Si tratta del core del progetto, senza quello il resto non ha motivo di esistere.`
	`Nel secondo sprint implementeremo il sistema di ticketing, implementeremo una prima interfaccia utente solo per i test. Probabilmente la parte che richiederà più tempo, conviene farla prima di aggiungere led e sonar per non complicare la progettazione.`
2) Led e Sonar [[Sprint 2]]
	`Nel terzo sprint implementeremo il sistema di led e sonar con tutta la logica rimanente. Dovrebbe essere facilmente implementabile sopra a tutto quello che già è stato creato senza richiedere alcuna modifica.`
3) Service Status Gui e grafica bellina [[Sprint 3]]
	`L'ultimo sprint si occuperà della service status gui e delle interfacce grafiche finali. Potrebbe richiedere un refactoring parziale delle componenti da osservare, da tenere in considerazione durante lo sviluppo.`

### Divisione dei compiti
Ogni Sprint verrà affrontato insieme con divisione dei compiti specifica valutata di volta in volta.

### Valutazione dei tempi
- Sprint 1 con primo esempio funzionante: 1 settimana
- Sprint 2 completato e funzionante: entro la prima settimana di Agosto
- Sprint 3: entro la fine di agosto
- Sprint 4: entro metà settembre
`NOTA: questa divisione cerca di tener conto delle vacanze estive tenendo i tempi abbastanza laschi ma è probabile sforeremo comunque`
NOTA: fare un piano di lavoro più accurato: con testing e specifiche

NOTA: ci sta mettere i nostri nomi e le info in nel readme di github.


