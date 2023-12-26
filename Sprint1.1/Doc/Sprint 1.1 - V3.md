### Goal Sprint 1.1
TicketHandler + ServiceAccessGUI.
> [!NOTE]- Descrizione
> In questa seconda parte del primo sprint verrà trattata la logica dei ticket, i componenti e la sicurezza associati.

Modello dello [[Sprint 1.0 - V3|sprint precedente]].
![[ArchitetturaLogica_Sprint1.0-V2.png]]
### Requisiti
![[ColdStorageServiceRoomAnnoted.png]]
[[Cold Storage Service - Natali V3#Requisiti|Requisiti]]

### Analisi dei Requisiti
[[Cold Storage Service - Natali V3#Analisi preliminare dei requisiti|requisiti sprint 0]]

### Analisi del Problema
##### Compiti di TicketHandler
TicketHandler si occuperà di:
1) verificare se è possibile generare il Ticket richiesto;
2) generare i Ticket;
3) verificare se il Ticket ricevuto è scaduto o meno.
> [!NOTE]- motivazioni
> 1) Principio di singola responsabilità: Il TicketHandler ha la responsabilità di gestire i Ticket, di conseguenza è corretto che sia quest'ultimo ad occuparsi sia di generare i Ticket richiesti sia di verificarne la validità.
>2) Motivi di sicurezza: si preferisce assegnare la verifica al TicketHandler, avendo lui tutte le informazioni del driver necessarie per generare e verificare i Ticket stessi (ad esempio l'istante di emissione o l'id del driver associato al ticket).

La separazione di TicketHandler e Controller porta l'utente a dover potenzialmente interagire con due entità diverse del sistema. Decidiamo di introdurre un componente intermedio per nascondere questa complessità dal lato dell'utente secondo il modello del __pattern facade__.

Il sistema sarà dunque ampliato secondo la seguente __Architettura logica__: 
![[ArchitetturaLogica_Sprint1.1.png]]
##### Protocollo di richiesta e generazione del ticket
![[Sprint1.1/Doc/cicloVitaMessaggi.png]]
1) Inizia con una request/response dal driver verso TicketHandler
```
Request depositRequest : depositRequest(PESO)
Reply accept : accept(TICKET)
Reply reject : reject(NO_PARAM)
```

2) TicketHandler chiede a ColdRoom se c'è abbastanza spazio;
```
Request weightrequest : weightrequest(PESO)
Reply weightOK : weightOK( NO_PARAM )
Reply weightKO : weightKO( NO_PARAM )
```
3) Se c'è abbastanza spazio, ColdRoom aggiorna i propri attributi;
4) Se TicketHandler riceve True genera il ticket e lo invia a ServiceAccessGui, altrimenti rejected;
5) Arrivato in INDOOR, il driver, invia il Ticket a TicketHandler. Il TicketHandler verifica il **TICKETTIME** e restituisce Ok / Rejected;
```
Request checkmyticket : checkmyticket(TICKET)
Reply	ticketchecked : ticketchecked(BOOL)
```
6) Se la richiesta viene approvata ServiceAccessGUI invia "loaddone" al Controller per notificare che il FridgeTruck è pronto, col peso da scaricare. Il Controller risponderà con "chargetaken".
```
Request loaddone : loaddone(PESO)
Reply 	chargetaken : chargetaken(NO_PARAM)
```
##### Quando viene inviato il "charge taken"?
"chargetaken" viene inviato dal Controller subito dopo la "doJob" associata alla richiesta.
> [!NOTE]- motivazioni
> 1) Supponiamo che quest'ultimo scarichi la merce in una piattaforma dedicata, dalla quale il DDR robot preleverà il cibo e lo scaricherà in ColdRoom in uno o più volte a seconda della quantità di materiale dichiarata.
> 2) Al driver non interessa sapere se il TransportTrolley ha avuto problematiche durante il trasporto del materiale, quindi il "charge taken" può essere inviato prima che il TransportTrolley comunichi al Controller se il carico/scarico in ColdRoom è terminato. 

Ricevuta la "chargetaken" il driver può uscire dal sistema considerando la transizione conclusa con successo.
##### Problema del peso ipotetico
Un driver potrebbe inviare la richiesta di un Ticket prima che un secondo driver, a cui è stato generato un Ticket in precedenza, abbiano scaricato.
Rischio di emettere un ticket per un peso non realmente disponibile nel momento di scarico.

Per risolvere il problema definiamo due pesi diversi: 
1)  Peso effettivo : quantità (peso) di cibo contenuto in ColdRoom nell'istante attuale. Aggiornato dopo l'azione del TransportTrolley.
2) Peso promesso : quantità di peso richiesta dai driver tramite Ticket non ancora scaricato, incrementato dopo l'emissione di un ticket e decrementato dopo l'azione del Transport Trolley o a seguito della scadenza della validità di un Ticket.

Useremo la somma dei due pesi per validare o meno una richiesta di emissione ticket.
```
QActor coldroom context ctxcoldstoragearea {
	[#
		var PesoEffettivo = 0
		var PesoPromesso = 0 
		var MAXW = 50
	#]
	
	State updateWeight {
		onMsg ( updateWeight : updateWeight(P_EFF, P_PRO) ) {
			[# PesoEffettivo += P_EFF 
			   PesoPromesso -= P_PRO
			#]
		}
	}
	
	State checkweight {
		onMsg(weightrequest : weightrequest(PESO)){
			[# var PesoRichiesto = PESO #]
			
			if [# PesoEffettivo + PesoPromesso + PesoRichiesto  <= MAXW #]	{
				[# PesoPromesso += PesoRichiesto #]
				replyTo weightrequest with weightOK : weightOK( NO_PARAM)
			} else {
				replyTo weightrequest with weightKO : weightKO( NO_PARAM )
			}
		}
	}
}
```
##### Problema del peso fantasma
A seguito della scadenza di un Ticket, il Transport Trolley non si farà carico della richiesta e il peso promesso del ticket rimarrà considerato in Cold Room.
##### Gestione dei Ticket scaduti
L'eliminazione dei ticket scaduti viene fatta per necessità.

All'arrivo di una richiesta di emissione del Ticket, se lo spazio calcolato non fosse sufficiente si verifica il TICKETTIME associato ai Ticket generati e non ancora scaricati.

In presenza di Ticket scaduti allora il TicketHandler procederà ad aggiornare il peso promesso tramite dispatch "updateWeight".
In questo modo risolviamo anche il [[Sprint 1.1 - V3#Problema del peso fantasma|problema del peso fantasma]].
##### Quando e da chi vengono aggiornati i pesi in ColdRoom?
1) Terminata l'azione del Transport Trolley, Controller aggiorna i due pesi tramite dispatch. Viene passata la quantità da decrementare dal peso promesso e la quantità da incrementare al peso effettivo.

2) Caso particolare: i pesi sono aggiornati da TicketHandler tramite dispatch "updateWeight" nella [[Sprint 1.1 - V3#Gestione dei Ticket scaduti|gestione dei ticket scaduti]].
##### Sicurezza dei Ticket
Dall'analisi della sicurezza sono apparse le seguenti vulnerabilità:
1) Bisogna assicurarsi che chi richiede il ticket sia l'unico a poterlo usare.
2) Ulteriori dati potrebbero essere visibili ad un utente malevolo (Peso scaricato, ecc...).
3) Un ticket non deve essere riutilizzabile da un qualsiasi utente.
4) Possibile DoS di un utente che richiede troppi ticket e occupa tutto il peso disponibile.
5) Possibile modifica malevola del Ticket dopo l'emissione.

Dei punti definiti, parlando col committente, dovremmo rispettare solo 1 e 3.
> [!NOTE]- soluzioni possibili
> 1) Devo assicurarmi che la risposta con il ticket generato venga inviata solo a chi ha fatto la richiesta iniziale e non sia visibile anche agli altri utenti collegati.
> 
> 3) Avendo già l'elenco dei ticket emessi in TicketHandler per controllare i ticket scaduti posso imporre che ogni ticket che ricevo debba essere dentro quella lista e rimuoverlo appena lo ricevo, in questo modo un ticket non può essere presentato più di una volta.
##### Gestione dei parametri di sistema
TICKETTIME è un parametro variabile al lancio del sistema. Definiamo un file di configurazione con i valori da caricare al lancio (AppConfig.json):
```json
{  
 "TicketTime": "600"  
}
```

```
QActor tickethandler context ctxcoldstoragearea {
	[# var TICKETTIME = GetTicketTimeFromConfigFile(); #]
	...
}
```
##### ServiceAccessGUI
Progettare le GUI come attori non è ottimale, dobbiamo progettarla come un componente alieno al sistema che si interfacci con esso.
Per fare ciò ci appoggiamo alla tecnologia di SPRING che permette l'interazione tramite web e la gestione di molti utenti collegati contemporaneamente.

Nello schema iniziale il server Spring prenderà quindi il posto dell'attore ServiceAccessGUI, l'interazione con l'utente avverrà tramite pagine html statiche fornite dal server ad ogni utente che si collega.

![[SpringStructure.png]]
##### Aggiornamento peso in ServiceAccessGUI
La soluzione migliore sarebbe metterlo in ascolto dei cambiamenti a ColdRoom, ColdRoom diventa observable come da analisi preliminari.
In alternativa Req/Resp di deposit weigth fa una richiesta per sapere il peso in coldRoom. 
In entrambi i casi usiamo la somma tra peso effettivo e peso promesso.

__PROBLEMA:__ Usando pagine html statiche, anche mantenendo aggiornato il peso corrente nel server spring l'utente deve ricaricare la pagina per visualizzare il nuovo peso.
Motivo per il quale abbiamo deciso di cambiare verso pagine html dinamiche.
##### Architettura logica dopo l'analisi del problema
![[coldstorage11arch.png]]

### Test Plan
Durante la fase di testing dovranno essere verificati i seguenti casi:
1) Test del processo in condizioni normali
2) Test con ticket scaduto 
3) Test con ticket ripetuto
4) Test con peso superiore al disponibile
5) Controllare che quando il controller aggiorna la ColdRoom il cambiamento di peso risulti nella service access gui.
Ciascuno dei test deve essere superato con più utenti collegati contemporaneamente da uno stesso browser o da browser diversi.

Codice secondo test: [[Sprint1.1/Codice/ColdStorageSprint2/test/TestService.java|TestService]]

### Progettazione
[[Sprint1.1/Codice/ColdStorageSprint2/src/coldstorage.qak|coldstorage qak]]
##### Ticket 
Ticket conterrà TIME, PESO e SEQ. La stringa sarà composta da questi 3 valori separati da "\_" ed inizierà con "T":
``` java
int TIME
int PESO
int SEQ

Ticket = "T"+"_"+TIME+"_"+PESO+"_"+SEQ           #esempio di ticket: T_1697643071_15_0
```

##### Definizione messaggi e contesti
```
System coldstorage2

//-----------------------------------------------------------------------

...

//-----------------------------------------------------------------------

Request depositRequest : depositRequest(PESO)
Reply accept : accept(TICKET) for depositRequest
Reply reject : reject(NO_PARAM) for depositRequest

Request weightrequest : weightrequest(PESO)
Reply weightOK : weightOK( NO_PARAM ) for weightrequest
Reply weightKO : weightKO( NO_PARAM ) for weightrequest

Request checkmyticket : checkmyticket(TICKET)
Reply	ticketchecked : ticketchecked(BOOL) for checkmyticket

Request loaddone : loaddone(PESO)
Reply 	chargetaken : chargetaken(NO_PARAM) for loaddone

Request getweight : getweight(NO_PARAM)
Reply 	currentweight : currentweight(PESO_EFF,PESO_PRO)  for getweight

Dispatch startToDoThings : startToDoThings( NO_PARAM )

//-----------------------------------------------------------------------

Context ctxcoldstoragearea ip [host="localhost" port=8040]

//-----------------------------------------------------------------------
```
##### Controller
Rispetto allo sprint 1 non abbiamo più bisogno della mockRequest e gestiamo il [[Sprint 1.1 - V3#Problema del peso ipotetico|problema del peso ipotetico]].
```
QActor controller context ctxcoldstoragearea {

	[# var P_EFF = 0
	   var P_DIC = 0 
	#]
	
	State s0 initial { printCurrentMessage } Goto work
	
	State work{
		println("controller - in attesa") color green
	} Transition t0 whenRequest loaddone -> startjob
	
	State startjob  {
		onMsg(loaddone : loaddone(PESO) ){
			[# PESO = payloadArg(0).toInt()	#]
		}
		replyTo loaddone with chargetaken : chargetaken( NO_PARAM )
		request transporttrolley -m doJob : doJob($P_EFF)
	} Transition endjob whenReply robotDead -> handlerobotdead
						whenReply jobdone -> jobdone
	
	State jobdone{
		forward coldroom -m updateWeight : updateWeight($P_EFF, $P_PROM)
	} Transition repeat -> work
```
##### ColdRoom
Rispetto allo sprint precedente ColdRoom deve verificare se è presente abbastanza spazio e rispondere di conseguenza.
UpdateWeight inoltre deve essere aggiornato per gestire il [[Sprint 1.1 - V3#Problema del peso ipotetico|problema del peso ipotetico]].
Il peso promesso viene sottratto, se va aumentato fornire __P_PRO negativo__.
```
QActor coldroom context ctxcoldstoragearea {
	...
	
	State checkweight {
		onMsg(weightrequest : weightrequest(PESO)){
			[# var PesoRichiesto = payloadArg(0).toInt() #]
			if [# PesoEffettivo + PesoPromesso + PesoRichiesto  <= MAXW #]	{
				[# PesoPromesso += PesoRichiesto #]
				replyTo weightrequest with weightOK : weightOK( NO_PARAM)
			} else {
				replyTo weightrequest with weightKO : weightKO( NO_PARAM )
			}
		}
	} Goto work
	
	State returnweight{
		onMsg(getweight : getweight(NO_PARAM)){
			replyTo getweight with currentweight : currentweight($PesoEffettivo, $PesoPromesso)
		}
	} Goto work	
   
	State updateWeight {
		onMsg ( updateWeight : updateWeight(P_EFF, P_PRO) ) {
			[#  PesoEffettivo += payloadArg(0).toInt()
				PesoPromesso -= payloadArg(1).toInt()
			#]
		}
	} Goto work
}
```
##### TicketHandler
[[Sprint1.1/Codice/ColdStorageSprint2/src/coldstorage.qak|coldstorage qak]] 
##### Parametrizzazione valori
[[Sprint1.1/Codice/ColdStorageSprint2/resources/DomainSystemConfig.kt|DomainSystemConfig.kt]] 
##### Spring Server: socket e observer
Il server si collegherà agli attori tramite socket o come coapObserver.
Le richieste ajax provenienti dai client verranno inoltrate tramite socket.

Il server è composto di un [[Sprint1.1/Codice/serviceaccessgui/src/main/java/unibo/serviceaccessgui/ApiController.java|ApiController.java]] che sfrutta la classe [[Sprint1.1/Codice/serviceaccessgui/src/main/java/unibo/serviceaccessgui/MessageSender.java|MessageSender]] per inviare messaggi.
Lato client rendiamo le pagine dinamiche tramite [[Sprint1.1/Codice/serviceaccessgui/src/main/java/unibo/serviceaccessgui/ApiController.java|ApiController.java]] 

Gli eventi degli attori osservati tramite observer verranno inoltrati ai client tramite websocket, create all'inizio di ogni sessione. Vedi [[Sprint1.1/Codice/serviceaccessgui/src/main/resources/static/js/dinamic.js|dinamic.js]].
##### HTML page
[[Sprint1.1/ServiceAccessGuiWebPage.html|ServiceAccessGuiWebPage.html]]

### Deployment
1) Avviare il container itunibovirtualrobot23 su docker
	Viene lanciato l'ambiente virtuale con il robot all'indirizzo http://localhost:8090/
2) In intellij avviare il file [[Sprint1.1/Codice/BasicRobotSprint2/src/it/unibo/ctxbasicrobot/MainCtxbasicrobot.kt|MainCtxbasicrobot.kt]] del progetto BasicRobot
3) In intellij avviare il file [[Sprint1.1/Codice/ColdStorageSprint2/src/it/unibo/ctxcoldstoragearea/MainCtxcoldstoragearea.kt|MainCtxcoldstoragearea.kt]] del progetto ColdStorage
4) In intellij avviare il file [[Sprint1.1/Codice/serviceaccessgui/src/main/java/unibo/serviceaccessgui/ServiceaccessguiApplication.java|ServiceaccessguiApplication.java]] del progetto serviceaccessgui. Aprire il client all'indirizzo http://localhost:8085/

# 
----------------
[Repo Github](https://github.com/Lombax99/ColdStorageService-XMas23)

| Lisa Innocenti Uccini              | Luca Lombardi              | Giacomo Romanini              |
| ------------------------ | -------------------------- | ----------------------------- |
| ![[LisaUccini.png\|180]] | ![[LucaLombardi.jpg\|245]] | ![[GiacomoRomanini.jpg\|180]] |
| [github: LisaIU00](https://github.com/LisaIU00)    | [github: Lombax99](https://github.com/Lombax99)             | [github: RedDuality](https://github.com/RedDuality)                              |

