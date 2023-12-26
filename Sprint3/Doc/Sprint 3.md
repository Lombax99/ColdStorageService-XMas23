### Goal Sprint 3
ServiceStatusGui e grafiche migliorate.
> [!NOTE]- Descrizione
> Nel terzo sprint ci occuperemo della ServiceStatusGUI e delle interfacce grafiche finali.

Modello dello [[Sprint 2|sprint precedente]].
![[Sprint2/Codice/ColdStorage/coldstorage2arch.png]]

### Requisiti
![[ColdStorageServiceRoomAnnoted.png]]
[[Cold Storage Service - Natali V3#Requisiti|Requisiti]]

### Analisi dei Requisiti
[[Cold Storage Service - Natali V3#Analisi preliminare dei requisiti|requisiti sprint 0]]

### Domande al committente:
Per la posizione quanto dobbiamo essere precisi? Serve sapere la posizione corrente ad ogni step? per il cliente non è necessario quest'ultimo punto dipende da noi. --> per semplicità forniamo le coordinate ad ogni cambiamento del macrostato (in home, fase di load, fase di unload, posizione in caso di errore)

### Analisi del Problema
##### Cosa implica lo stato del servizio?
Lo stato del servizio comprende:
- Lo stato e la posizione del TransportTrolley.
- Lo stato della ColdRoom (peso corrente su totale).
- Il numero di richieste negate dall'inizio del servizio.
##### Numero di richieste negate
Aggiorniamo il ticket handler per tener traccia delle richieste negate
```
QActor tickethandler context ctxcoldstoragearea {
	[# var Rejected = 0	#]
	...
	
	State reject {
		[# Rejected++ #]
		...
}
```
##### Rendo tutti i componenti observable
Stato del TransportTrolley (sfruttiamo RobotPos):
```
updateResource [# planner.robotOnMap() #]
```

ColdRoom:
```
updateResource[# "" + PesoEffettivo + "_" + PesoPromesso + ""#]
```

TicketHandler:
```
updateResource [# "" + Rejected #]
```
##### Caricare i dati iniziali nella GUI
All'avvio della ServiceStatusGui per visualizzare i valori possiamo:
- Aspettare che i componenti aggiornino i propri valori normalmente
- Richiedere esplicitamente il valore corrente
Decidiamo di fare richiesta esplicita poiché in mancanza di richieste da parte degli utenti potrei dover aspettare un tempo indefinito prima di vedere lo stato del sistema.
##### Come mandare le richieste
Sfruttiamo la facade già creata in precedenza per richiedere al sistema i valori iniziali per la ServiceStatusGui.
##### Architettura logica dopo l'analisi del problema
Possiamo definire la gui come un attore (mock GUI) che poi verrà sostituito
![[Sprint3/Codice/ColdStorage3/coldstorage3arch.png]]
![[ArchitetturaWebSprint3.png]]
### Test Plan
1) A seguito di una richiesta rifiutata devono aumentare le richieste rifiutate.
2) Nel caso di loadDone fallita la posizione stampata deve essere diversa da home.
3) All'avvio della Gui devono essere caricati i parametri attuali del sistema.

### Progettazione
[[Sprint3/Codice/ColdStorage3/src/coldstorage.qak|coldstorage.qak]]
##### Facade aggiornato
```
QActor facade context ctxcoldstoragearea {
	[#
		var Ticket = ""
		var PesoEff = 0
		var PesoProm = 0
		var Valid = true
	#]
	
	State s0 initial{
		delegate "getrejectedtickets" to tickethandler
		delegate "getrobotstate" to robotpos
		delegate "getweight" to coldroom
		
		println("ColdStorageFacade - in attesa") color blue
		printCurrentMessage
	} Goto work
	
	State work {
	}Transition t0  whenRequest depositRequestF-> depositreqhandler
				whenRequest loaddoneF -> loadcontroller
				whenRequest checkmyticketF -> checktickethandler
				whenRequest getweightF -> getweightcoldroom
	
	...
}
```
##### La classe per la parte web
Il server spring della ServiceStatusGUI è composto da un [[ControllerStatusGui.java]] e una serie di observer per i parametri da monitorare:
- [[Sprint3/Codice/statusgui/src/main/java/unibo/statusgui/ColdRoomObserver.java|ColdRoomObserver]]
- [[RobotPosObserver.java]]
- [[TicketHandlerObserver.java]]

Socket Handler: [[Sprint3/Codice/statusgui/src/main/java/unibo/statusgui/WebSocketHandler.java|WebSocketHandler.java]]
##### Ticket Handler si aggiorna per contare quanti ticket vengono rifiutati
```
QActor tickethandler context ctxcoldstoragearea {
	
	[#	
		...		
		var Rejected = 0
	#]
		
	State s0 initial{
		...
		updateResource [# "" + Rejected #]
	} Goto work
	
	...
	
	State reject {
		[# Rejected++ #]
		updateResource [# "" + Rejected #]
		replyTo depositRequest with reject : reject( reject )
	} Goto work
	
	...
	
	State sendrejectedticketnumber{
		onMsg(getrejectedtickets : getrejectedtickets(NO_PARAM)){
			replyTo getrejectedtickets with rejectedtickets : rejectedtickets($Rejected)
		}
	}Goto work
}
```
##### HTML page
[[Sprint3/Codice/statusgui/src/main/resources/templates/static/ServiceStatusGui.html|ServiceStatusGui.html]] 

### Deployment
#### Deployment on RaspberryPi 3B/3B+
![[RaspPin.png]]
##### Led
- braccino corto: pin fisico 39 (GND)
- braccino lungo: pin fisico 40 (GPIO21)
##### Sonar
- VCC : pin fisico 4 (+5v)
- GND : pin fisico 6 (GND)
- TRIG: pin fisico 11 (GPIO 17)
- ECHO: pin fisico 13 (GPIO 27)

1) Avvia main alarm
#### Main system deployment
1) Avviare il container itunibovirtualrobot23 su docker
	Viene lanciato l'ambiente virtuale con il robot all'indirizzo http://localhost:8090/
2) In intellij avviare il file [[Sprint1.1/Codice/BasicRobotSprint2/src/it/unibo/ctxbasicrobot/MainCtxbasicrobot.kt|MainCtxbasicrobot.kt]] del progetto BasicRobot
3) In intellij avviare il file [[Sprint3/Codice/ColdStorage3/src/it/unibo/ctxcoldstoragearea/MainCtxcoldstoragearea.kt|MainCtxcoldstoragearea.kt]] del progetto ColdStorage
4) In intellij avviare il file [[Sprint2/Codice/Alarm/src/it/unibo/ctxalarm/MainCtxalarm.kt|MainCtxalarm.kt]] del progetto Alarm
5) In intellij avviare il file [[Sprint1.1/Codice/serviceaccessgui/src/main/java/unibo/serviceaccessgui/ServiceaccessguiApplication.java|ServiceaccessguiApplication.java]] del progetto serviceaccessgui. Aprire il client all'indirizzo http://localhost:8085/
6) In intellij avviare il file [[Sprint3/Codice/statusgui/src/main/java/unibo/statusgui/StatusguiApplication.java|ServiceaccessguiApplication.java]] del progetto statusgui. Aprire il client all'indirizzo http://localhost:8075/

# 
----------------
[Repo Github](https://github.com/Lombax99/ColdStorageService-XMas23)

| Lisa Innocenti Uccini              | Luca Lombardi              | Giacomo Romanini              |
| ------------------------ | -------------------------- | ----------------------------- |
| ![[LisaUccini.png\|180]] | ![[LucaLombardi.jpg\|245]] | ![[GiacomoRomanini.jpg\|180]] |
| [github: LisaIU00](https://github.com/LisaIU00)    | [github: Lombax99](https://github.com/Lombax99)             | [github: RedDuality](https://github.com/RedDuality)                              |

