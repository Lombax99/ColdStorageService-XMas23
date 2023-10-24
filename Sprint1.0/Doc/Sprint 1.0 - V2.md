### Prodotto dello Sprint 0
È stata individuata un'architettura logica iniziale che definisca le macro-entità del sistema e le loro interazioni, [[Cold Storage Service - Natali V2|link al modello precedente]].
![[Architettura_Sprint0_V2.png]]
### Goal Sprint 1
1) Transport Trolley + ColdStorageService
> [!NOTE]- Descrizione
> Lo scopo del primo sprint è produrre una prima versione funzionante del core dell'applicazione. Questo comprende ColdStorageService con la logica di gestione dei Ticket e il TransportTrolley funzionante.
> A questa parte deve essere affiancata una mock version della ServiceAccessGUI per la fase di testing.

### Requisiti relativi allo sprint corrente
A company intends to build a ColdStorageService, composed of a set of elements:

1. a ==service area== (rectangular, flat) that includes:
    - an ==INDOOR port==, to enter food (fruits, vegetables, etc. )
    - a ==ColdRoom container==, devoted to store food, upto **MAXW** kg .
    The ColdRoom is positioned within the service area, as shown in the following picture:

![[ColdStorageServiceRoomAnnoted.png]]

2. a ==DDR robot== working as a ==transport trolley==, that is intially situated in its ==HOME location==. The transport trolley has the form of a square of side length **RD**.
    The transport trolley is used to perform a deposit action that consists in the following phases:
    1. pick up a ==food-load== from a Fridge truck located on the INDOOR
    2. go from the INDOOR to the ==PORT of the ColdRoom==
    3. deposit the food-load in the ColdRoom

> Rinviato a Sprint successivo ([[Sprint 1.0 - V2#Analisi del Problema|see below]])
3. a ServiceAcessGUI that allows an human being to see the current weigth of the material stored in the ColdRoom and to send to the ColdStorageService a request to store new **FW** kg of food. If the request is accepted, the services return a ticket that expires after a prefixed amount of time (**TICKETTIME** secs) and provides a field to enter the ticket number when a Fridge truck is at the INDOOR of the service.

### Service users story
> Rinviato a Sprint successivo ([[Sprint 1.0 - V2#Analisi del Problema|see below]])

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

### Analisi del TF23
Nelle discussioni con il committente, sono emerse alcune problematiche:
- Il problema del load-time lungo.
- Il problema del driver distratto (non coerente, rispetto alle due fasi: scarico preceduto da prenotazione).
- Il problema del driver malevolo.
- Il problema di garantire che una risposta venga sempre inviata sempre solo a chi ha fatto la richiesta, anche quando la richiesta è inviata da un ‘alieno’ come una pagine HTML
#### Il problema del load-time lungo
> Rinviato a Sprint successivo ([[Sprint 1.0 - V2#Analisi del Problema|see below]])

Il problema del load-time lungo è stato affrontato da Arnaudo/Munari con l’idea di inviare due messaggi di ‘risposta’ (una per dire al driver che il ticket inviato è valido e una per inviare `chargeTaken`). A questo fine hanno fatto uso diretto della connessione TCP stabilita da una versione prototipale dell’accessGui fatta come GUI JAVA.
Per consentire questa possibilità anche a livello di modellazione qak, in _ActorBasicFsm_ è stato introdotto il metodo storeCurrentRequest() che permette di ricordare la richiesta corrente (cancellata da una _replyTo_). Questo però è un trucco/meccanismo che potrebbe risultare pericoloso.
Meglio affrontare il problema dal punto di vista logico, impostando una interazione a DUE-FASI tra driver e service (compito che può svolgere la _serviceAcessGui_).
- FASE1: il driver invia il ticket e attenda una risposta (immediata) come ad esempio `ticketaccepted/ticketrejected`
- FASE2: il driver invia la richiesta `loaddone` e attenda la risposta (`chargeTaken` o fallimento per cause legate al servizio)
#### Il problema del driver distratto
Questo problema ha indotto il committente ad affermare che:
quando un agente esterno (driver) invia il ticket per indurre il servizio a scaricare il truck, si SUPPPONE GARANTITO che il carico del truck sia UGUALE al carico indicato nella prenotazione.
Ciò in quanto non vi sono sensori (bilance , etc) che possano fornire il valore del carico effettivo sul Truck.

### Analisi dei Requisiti
[[Cold Storage Service - Natali V2#Analisi preliminare dei requisiti|requisiti sprint 0]]

> [!NOTA]- domanda
> Possiamo limitarci a mettere il riferimento allo sprint 0 o dobbiamo riportare tutto? 

### Analisi del Problema

NOTA: nel file della Cate e di Longhi è stato introdotto qua il concetto di usare una griglia.
Non viene definito nelle definizioni una coppia di coordinate, solo in questo momento vengo ridefiniti i componenti come entità nella griglia a determinate coordinate.
Sempre qua viene detto che il robot ha una step che permette di implementare il sistema agevolmente. Forse era nella progettazione?

NOTA: anche qua possiamo aggiungere pezzi di codice del qak per essere più machine understandable. Solo in progettazione con riferimento ai problemi nell'Analisi.

NOTA: Ma tutti i parametri che abbiamo tipo MAXW TICKETTIME, da dove vengono? Li mettiamo hardcoded o facciamo un file di config da leggere (magari un bel JSON...)?

##### 

- ==Separazione delle responsabilità di ColdStorageService==
	ColdStorageService è un componente caratterizzato da troppe responsabilità, abbiamo quindi deciso di sostituirlo con 2 attori:
	- Controller, il quale si occupa di gestire il robot ed aggiornare il peso di ColdRoom.
	- TicketHandler, il quale si occupa di gestire il ciclo di vita dei Ticket.
	Abbiamo quindi deciso di dividere lo Sprint1 in due fasi.
	La progettazione di TicketHandler viene rimandata al prossimo Sprint ([[Sprint 1.1]]). 

![[ArchitetturaLogica_Sprint1.0.png]]


- ==Chi manda i comandi al Transport Trolley?==
	Introduciamo un nuovo attore "Controller" che si occupi di mandare i comandi al Transport Trolley e gestire la logica applicativa. 
	In questo primo sprint supponiamo che il Controller sia a conoscenza dell'istante di arrivo dei Fridge Truck in INDOOR, di conseguenza il servizio partirà da un segnale generato dal controller).
	
- ==Quali comandi è in grado di comprendere il Transport Trolley?==
	L'unico comando mandato dal controller è "doJob", inviato solo dopo che un camion ha finito di scaricare...
	
- ==Chi traduce "doJob" in una serie di comandi comprendibili al DDR robot?==
	L'attore TransportTrolley si occupa di svolgere questo compito. Ricevere il comando "doJob" dal Controller, lo interpreta e comunica al robot le operazioni da eseguire.
	
- ==Come avviene la comunicazione tra Controller e Transport Trolley? Dispatch o Req-Resp?==
	Controller manda un segnale di Req-Resp al Transport Trolley, ovvero rimane in attesa di una risposta da quest'ultimo. In quanto è necessario sapere se il servizio richiesto è andato a buon fine oppure se il DDR robot ha avuto problematiche che lo hanno interrotto.
	
- ==Come comunicano TransportTrolley e ColdRoom?==
	Transport Trolley e ColdRoom non comunicano direttamente ma solo tramite Controller.
	
- ==Quando viene aggiornato il peso della ColdRoom (e da chi)?==
	Se il servizio è andato a buon fine allora il Controller aggiorna il peso della ColdRoom tramite Dispatch.
	
- ==Come fa il Transport Trolley a sapere dov'è e dove deve andare?==
	Dividiamo la stanza in una griglia di quadrati di lato RD (lunghezza del DDR robot). 
	Le coordinate del Transport Trolley indicheranno il quadrato in cui si trova. L'origine (0, 0) sarà la posizione di Home. Coordinate crescenti verso il basso e verso destra.
	
- ==Quando viene fatta la mappatura della stanza?==
	La mappatura della stanza deve essere fatta a priori e fornita al sistema.
	
- ==Quando controlla il TransportTrolley se ci sono altre richieste?==
	Come da requisiti controllare se ci sono altre richieste viene fatto:
	- Quando il DDR robot risulta fermo in HOME, in attesa di nuovi comandi.
	- Dopo aver scaricato la merce nella ColdRoom, prima di tornare in HOME.
	
- ==Il robot ha un peso massimo?== 
	Sì, il DDR robot ha un peso massimo trasportabile. Il carico che il robot deve prendere dal camion può essere maggiore del peso trasportabile dal DDR robot. In tal caso sarà il robot a decidere quanti giri fare in base al peso che deve essere trasportato.
	NOTA: abbiamo definito con il committente che il peso da scaricare sia sempre minore o uguale al peso massimo trasportabile.
	
![[Sprint1.0/Doc/coldstoragearch.png | 300]]

NOTE: test plan?
definizione di mockGUI per fare i test ecc...

### Progettazione
NOTE: dividiamo un po' il codice e aggiungiamo qualche commento legato ai problemi di sopra
``` qak
System coldstorage

//-----------------------------------------------------------------------

Request doJob : doJob(KG)
Reply jobdone : jobdone(NO_PARAM)
Reply robotDead : robotDead(NO_PARAM)

Dispatch updateWeight : updateWeight(PESO)



//-----------------------------------------------------------------------

Context ctxcoldstoragearea ip [host="localhost" port=8040]

//-----------------------------------------------------------------------

Context ctxbasicrobot ip [host="127.0.0.1" port=8020] 

ExternalQActor transporttrolley context ctxbasicrobot




QActor controller context ctxcoldstoragearea {

	[# var KG = 0
		#]
		
	State s0 initial {
		printCurrentMessage
		}
	Goto mockRequest
	
	State mockRequest {
		[#
			KG = Math.floor(Math.random() *(20 - 10 + 1) + 10).toInt()
			
		#]
		printCurrentMessage
		request transporttrolley -m doJob : doJob($KG)
		
	} Transition endjob whenReply robotDead -> handlerobotdead
						whenReply jobdone -> jobdone
	
	State jobdone{
		forward coldroom -m updateWeight : updateWeight($KG)
		//ChargeTaken
	} Transition repeat whenTime 15000 -> mockRequest
	
	State handlerobotdead{
		printCurrentMessage
	}
}



QActor coldroom context ctxcoldstoragearea {
	[#
		var PesoEffettivo = 0
	#]
	
	State s0 initial {
		printCurrentMessage
	} Transition update whenMsg updateWeight -> updateWeight
	
	State updateWeight {
		printCurrentMessage
		onMsg ( updateWeight : updateWeight(PESO) ) {
			[# PesoEffettivo += payloadArg(0).toInt() 
				#]
		}
		println("peso aggiornato")
		println("nuovo peso: $PesoEffettivo")
	} Transition update whenMsg updateWeight -> updateWeight
}




```


- [x] Tutorial su come far partire la DEMO (passaggi e cose varie)

1) Avviare il container itunibovirtualrobot23 su docker
	Viene lanciato l'ambiente virtuale con il robot all'indirizzo http://localhost:8090/
2) In intellij avviare il file MainCtxbasicrobot.kt del progetto BasicRobot
3) In intellij avviare il file MainCtxColdStorageArea.kt del progetto coldStorage

