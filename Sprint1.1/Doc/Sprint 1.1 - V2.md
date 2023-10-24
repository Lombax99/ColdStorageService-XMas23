### Goal Sprint 1.1
In questa seconda fase ci concentriamo sul processo di emissione dei ticket e le valutazioni di sicurezza annesse.
[[Sprint 1.0|Link allo sprint 1]]
### Requisiti
A company intends to build a ColdStorageService, composed of a set of elements:

1. a ==service area== (rectangular, flat) that includes:[[Sprint 1.1]]
    - an ==INDOOR port==, to enter food (fruits, vegetables, etc. )
    - a ==ColdRoom container==, devoted to store food, upto **MAXW** kg .
    The ColdRoom is positioned within the service area, as shown in the following picture:

![[ColdStorageServiceRoomAnnoted.png]]

2. a ==DDR robot== working as a ==transport trolley==, that is intially situated in its ==HOME location==. The transport trolley has the form of a square of side length **RD**.
    The transport trolley is used to perform a deposit action that consists in the following phases:
    1. pick up a ==food-load== from a Fridge truck located on the INDOOR
    2. go from the INDOOR to the ==PORT of the ColdRoom==
    3. deposit the food-load in the ColdRoom

3. a ==ServiceAcessGUI== that allows an human being to see the ==current weigth== of the material stored in the ColdRoom and to send to the ==ColdStorageService== a request to store new **FW** kg of food. If the request is accepted, the services return a ==ticket== that expires after a prefixed amount of time (**TICKETTIME** secs) and provides a field to enter the ticket number when a Fridge truck is at the INDOOR of the service.

### Analisi dei Requisiti
##### ==Service Area==
Area rettangolare di dimensione L * l. L'area sarà suddivisa in una griglia con coordinate.

`NOTE: L'area è piana, racchiusa entro quattro pareti. Procedendo dal bordo superiore e muovendoci in senso orario, i nomi delle pareti saranno: wallUp, wallRight, wallDown, wallLeft. All'interno del Service Area il transport trolley è libero di muoversi.La stanza è rettangolare ed ha dimensione Lato-Lungo * lato-corto (L * l). Per definire la posizione del robot in ogni momento l'area è divisa in una griglia con coordinate crescenti associate a partire dall'angolo in alto a sinistra`

##### ==HOME==
Zona della Service Area corrispondente alle coordinate (0,0)

`NOTE: Locazione all'interno della Service Area dove il transport trolley si trova rivolto verso il basso, nell'angolo superiore sinistro. La Home è la zona della Service Area in cui il robot si troverà all'avvio e in ogni periodo di attesa di nuove richieste.`

##### ==INDOOR port==
Zona della Service Area corrispondente alle coordinate (0,MAX)

`NOTE: Locazione all'interno della Service Area in cui un camion scarica la merce da far caricare al transport trolley. Si trova nell'angolo in basso a sinistra della Service Area. Le coordinate crescono allontanadosi dalla HOME, INDOOR port si trova a distanza massima sull'asse Y`

##### ==ColdRoom Container==
Attore in posizione fissa (x,y) in Service Area in grado di ricevere e contenere cibo da un lato specifico. Ha una capienza pari a MAXW kg.

`NOTE: Contenitore fisico posizionato all'interno della Service Area in una posizione fissa. In questo elemento il transport trolley è in grado di depositare cibo fino ad un massimo di MAXW kg. ColdRoom Container rappresenta un ostacolo all'interno della Service Area per il transport trolley, ciò vuol dire che non può muoversi attraverso la posizione in cui l'elemento è localizzato, per semplicità supporremo che il container occupi interamente una sola coordinata di Service Area.`

##### ==Porta della ColdRoom==
Lato della ColdRoom che si affaccia sull'area di coordinate (x, y+1). Transport Trolley dovrà trovarsi in questa posizione per interagire con ColdRoom.

`NOTE: Lato del ColdRoom Container tramite li quale è possibile depositare il cibo. Corrisponde al lato del container rivolto verso il basso della Service Area. Il transport trolley dovrà posizionarsi davanti alla porta della ColdRoom per poter depositare al suo interno il cibo overo in corrispondenza delle coordinate (x, y+1).`

##### ==DDR robot==
*Differential Drive Robot*, vedi [DDR](https://www.youtube.com/watch?v=aE7RQNhwnPQ).

##### ==Transport trolley==
Transport trolley è un DDR robot. I comandi che è in grado di compiere sono descritti nell'apposita [documentazione](file:///C:/Users/lomba/Desktop/iss23/iss23Material/html/BasicRobot23.html) .

##### ==Food-load==
Carico (in kg) che il robot caricherà da Indoor e depositerà in ColdRoom Container.

##### ==Current weight==
Quantità di cibo attualmente contenuto in ColdRoom definito in base al peso.

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

##### ==Ticket==
Il Ticket, su richiesta di un driver tramite ServiceAccessGUI, viene generato dal ColdStorageService. Il Ticket rappresenta il permesso di scarico concesso ad un determinato FridgeTruck.
Ogni Ticket è caratterizzato dai seguenti parametri:
- start time: istante di emissione del ticket;
- peso della quantità di cibo da scaricare dichiarato dal driver;
- ~~identificativo del driver a cui è assegnato il ticket;~~
- codice univoco che identifica il ticket generato.

### Analisi del Problema
- [ ] Vediamo il pattern facade (mettiamo qualcosa che fa da facciata). Aggiungo un nuovo componente ColdStorageFacade in modo tale che la gui si interfacci con un solo componente. Si aggiunge quindi un nuovo attore tra ServiceAccessGui e i due componenti TicketHandler e Controller. Per fare questo cerca info su pattern facade e spring

![[ArchitetturaLogica_Sprint1.1.png]]

- ==Generazione e della verifica di validità dei Ticket?==
	Introduciamo un nuovo attore "TicketHandler" che si occupi di:
		1) verificare se è possibile generare il Ticket richiesto;
		2) generare i Ticket;
		3) verificare se il Ticket ricevuto è scaduto o meno.
	
```
è stata affidata la verifica dei Ticket al TicketHandler per le seguenti motivazioni:
1) principio di singola responsabilità: Il TicketHandler ha la responsabilità di gestire i Ticket, di conseguenza è corretto che sia quest'ultimo ad occuparsi sia di generare i Ticket richiesti sia di verificarne la validità. 
2) motivi disicurezza: si preferisce assegnare la verifica al TicketHandler, avendo lui tutte le informazioni del driver necessarie per generare e verificare i Ticket stessi (ad esempio l'istante di emissione o l'id del driver associato al ticket).
```
	
- ==Problema del peso ipotetico==
	Un driver potrebbe inviare la richiesta di un Ticket prima che un secondo driver, a cui è stato generato un Ticket in precedenza, abbiano scaricato.
	Rischio di emettere un ticket per un peso non realmente disponibile nel momento di scarico.
	
	Per risolvere il problema definiamo due pesi diversi: 
	1)  Peso effettivo : quantità (peso) di cibo contenuto in ColdRoom nell'istante attuale. Aggiornato dopo l'azione del TransportTrolley.
	2) Peso "promesso" : quantità di peso richiesta dai driver tramite Ticket non ancora scaricato, incrementato dopo l'emissione di un ticket e decrementato dopo l'azione del Trasport Trolley o a seguito della scadenza della validità di un Ticket.
	
	Questi due pesi si troveranno entrambi in ColdRoom (se un giorno ci saranno due punti di accesso il peso futuro deve essere in comune).
	Useremo la somma dei due pesi per validare o meno una richiesta di emissione ticket.
	
- ==Problema del peso fantasma==
	A seguito della scadenza di un Ticket, il Transport Trolley non si farà carico della richiesta e il peso promesso del ticket rimarrà considerato il Cold Room.
	
- ==Quando e da chi vengono aggiornati i pesi in ColdRoom?==
	1) Terminata l'azione del Transport Trolley, peso promesso e peso effettivo verranno aggiornati tramite dispatch di Controller. In particolar modo viene passata la quantità da decrementare dal peso "promesso" e la quantità da incrementare al peso effettivo, i due valori possono essere diversi a causa del problema del driver distratto ([[Cold Storage Service - Natali#Il problema del driver distratto |see Driver Distratto]]).
	
	2) Caso particolare: i pesi sono aggiornati da TicketHandler tramite dispatch "updateWeight":
		All'arrivo di una richiesta di emissione del Ticket, se lo spazio calcolato non fosse sufficiente si verifica il TICKETTIME associato ai Ticket generati e non ancora scaricati.
		In presenza di Ticket scaduti allora il TicketHandler procederà ad aggiornare il peso.
		In questo modo risolviamo anche il problema del ==peso fantasma==
	
- ==Protocollo di richiesta e generazione del ticket:==
![[Sprint1.1/Doc/cicloVitaMessaggi.png]]
	1) Inizia con una request/response da parte del driver tramite ServiceAccessGUI verso TicketHandler, a cui viene passato il peso da scaricare;
	2) TicketHandler chiede a ColdRoom se c'è abbastanza spazio per depositare la quantità di cibo dichiarata dal driver sempre tramite request/response, la quale viene passata come parametro;
	3) Se c'è abbastanza spazio, ColdRoom aggiorna i propri attributi in modo tale da memorizzare che una quantità di peso è riservata al driver in questione che ne ha fatto richiesta e risponde True, altrimenti False;
	4) Se TicketHandler riceve True genera il ticket e lo invia come risposta a ServiceAccessGui, altrimenti risponde Rejected
	5) Una volta arrivato in INDOOR, il driver, invia il Ticket a TicketHandler tramite Request/Response. Il TicketHandler verifica il **TICKETTIME** e restituisce Ok / Rejected, effettua quindi la verifica di validità temporale del Ticket. 
	6) Se la richiesta viene approvata ServiceAccessGUI invia tramite Request/Response al Controller la richiesta "load done" per notificare al Controller che il FridgeTruck è pronto, insieme al peso da scarcare. Dopo di che attende una risposta "charge taken" da parte del Controller.
	
- ==Quando il driver può uscire dal sistema?==
	Il driver può uscire dal sistema quando ha scaricato tutta la merce contenuta, ovvero quando riceve dal Controller la response "charge taken" associata ad una precedente request "load done".
	
- ==Quando viene inviato il "charge taken"?==
	"Charge taken" viene inviato dal Controller subito dopo la "doJob" associata alla richiesta.
```
Motivazioni:
1) Supponiamo che quest'ultimo scarichi la merce in una piattaforma dedicata, dalla quale il DDR robot preleverà il cibo e lo scaricherà in ColdRoom in uno o più volte a seconda della quantità di materiale dichiarata.
2) Al driver non interessa sapere se il TransportTrolley ha avuto problematiche durante il trasporto del materiale, quindi il "charge taken" può essere inviato prima che il TransportTrolley comunichi al Controller se il carico/scarico in ColdRoom è terminato. 
```
	
- ==Problema della sicurezza:==
	[[Sprint 1.0 - V2#Analisi del TF23|problema del driver malevolo]]
	- Dobbiamo assicurarci che chi richiede il ticket sia l'unico a poterlo usare. NO
	NOTA: la Cate lo ha fatto aggiungendo ai ticket un parametro randomico segreto che solo chi ha ricevuto il ticket può conoscere... non so se sia perchè Natali ha detto loro di farlo o per loro scelta ma non è impossibile da implementare, da discutere.
	- Tutti vedono l'emissione di un ticket, ci sta bene? possibile violazione della privacy o copia. NO
	- Fare in modo che la risposta ad una richiesta arrivi al camionista che l'ha mandata e solo a lui anche se la richiesta arriva da un dispositivo alieno. NO
	- Fare in modo che un singolo camionista non possa continuare a generate ticket all'infinito e occupare tutto il peso ipotetico. Possibile DoS. NO
	- Fare in modo che un ticket non sia riutilizzabile? possibile DoS, usiamo ticket sequenziali? YES
> Io ho già l'elenco dei ticket emessi in TicketHandler per controllare i ticket scaduti ecc..
> Posso imporre che ogni ticket che ricevo debba essere dentro quella lista e rimuoverlo appena lo ricevo, in questo modo un ticket non può essere presentato più di una volta.
	
- ==Aggiornamento peso in ServiceAccessGUI==
	La cosa migliore sarebbe metterlo in ascolto dei cambiamenti a ColdRoom, ColdRoom diventa observable come da analisi preliminari. 
	In alternativa Req/Resp di deposit weigth fa una richiesta per sapere il peso in coldRoom. 
	In entrambi i casi usiamo la somma tra peso effettivo e peso promesso.
	
```
NOTE:
Per quanto riguarda l'implementazione è necessario un ServiceAccessGUI per ogni camion che si presenta, in quanto tutte le richieste e comunicazioni sono sincrone bloccanti. Ad ogni ServiceAccessGUI deve essere associata una grafica html. Che tecnologia utilizzare? SPRING
```


![[Sprint1.1/Doc/coldstorage2arch.png | 350]]

WEB PAGE
![[ChristamsClientWeb.png]]
NOTE sulla gui: usare attori per la gui non è ottimale, dobbiamo progettarla come un componente alieno al sistema che si interfacci con esso.
Opzioni disponibili: SPRING o NODEJS, ma nodejs è brutto, il codice è difficilmente manutenibile e più complesso da sviluppare, usiamo SPRING.

NOTE: anche qui dobbiamo mettere i test

### Progettazione

##### Ticket 
> Rinviato a Sprint successivo ([[Sprint 1.0 - V2#Analisi del Problema|see below]])
```
int TIME
int PESO
int SEQ

Ticket = "T"+"_"+TIME+"_"+PESO+"_"+SEQ           #esempio di ticket: T_1697643071_15_0
```

- ==Contesti:==
	- TicketHandler è contenuto sullo stesso contesto di Controller
	- TransportTrolley, ColdRoom e ServiceAccessGui avranno un contesto a parte per ciascuno
`alla fine questa informazione sarà nel qak`

- Codice della gestione dei ticket
	deve avere una lista che contiene i ticket emessi ecc...

- Codice dei ticket
Il ticket sarà una stringa composta dai seguenti dati:
momento di emissione,
peso della richiesta,
codice univoco.
Il momento di emissione viene eseguito come numero di secondi dal primo gennaio 1970, sarà un long.
Il peso della richiesta non viene modificato, viene salvato in un intero.
il codice univoco è un numero sequenziale di emissione del ticket.
la separazione dei campi avverrà tramite il token "_", che non causa problemi al momento della lettura del ticket.
Sempre per problemi di traduzione del messaggio, il ticket inizia con una lettera: T.

```
System coldstorage2

//-----------------------------------------------------------------------

Request doJob : doJob(KG)
Reply jobdone : jobdone(NO_PARAM)
Reply robotDead : robotDead(NO_PARAM)

//il peso promesso viene sottratto, se va aumentato fornire P_PRO negativo
Dispatch updateWeight : updateWeight(P_EFF, P_PRO)

//-----------------------------------------------------------------------

Request depositRequest : depositRequest(PESO)
Reply accept : accept(TICKET)
Reply reject : reject(NO_PARAM)


Request weightrequest : weightrequest(PESO)
Reply weightOK : weightOK( NO_PARAM )
Reply weightKO : weightKO( NO_PARAM )

Request checkmyticket : checkmyticket(TICKET)
Reply	ticketchecked : ticketchecked(BOOL)

Request loaddone : loaddone(PESO)
Reply 	chargetaken : chargetaken(NO_PARAM)

Request getweight : getweight(NO_PARAM)
Reply 	currentweight : currentweight(PESO_EFF,PESO_PRO)

Dispatch startToDoThings : startToDoThings( NO_PARAM )


//-----------------------------------------------------------------------

Context ctxcoldstoragearea ip [host="localhost" port=8040]

//-----------------------------------------------------------------------

//Context ctxbasicrobot ip [host="127.0.0.1" port=8020] 

//ExternalQActor transporttrolley context ctxbasicrobot




QActor controller context ctxcoldstoragearea {

	[# var PESO = 0
		#]
		
	State s0 initial {
		printCurrentMessage
	} Goto work
	
	State work{
		println("controller - in attesa") color green
	} Transition t0 whenRequest loaddone -> startjob
	
	//senza robot
	State startjob  {
		onMsg(loaddone : loaddone(PESO) ){
			[# PESO = payloadArg(0).toInt()
				#]
			println("controller - startjob dichiarato: $PESO") color green
		}
		
		replyTo loaddone with chargetaken : chargetaken( NO_PARAM )
		forward coldroom -m updateWeight : updateWeight($PESO, $PESO)
		
	} Goto work
	
	/* col robot 
	State startjob  {
		onMsg(loaddone : loaddone(P_EFF, P_DIC) ){
			[# KG = payloadArg(0).toInt()
				P_DIC = payloadArg(1).toInt()
				#]
			println("controller - dichiarato: $P_DIC, effettivo: $KG") color green
		}
		replyTo loaddone with chargetaken : chargetaken( NO_PARAM )
		request transporttrolley -m doJob : doJob($KG)
		
	} Transition endjob whenReply robotDead -> handlerobotdead
						whenReply jobdone -> jobdone
	*/
	State jobdone{
		forward coldroom -m updateWeight : updateWeight($PESO, $PESO)
	} Transition repeat whenTime 15000 -> work
	
	
	State handlerobotdead{
		println("robotdead") color green
		printCurrentMessage
	}
}



QActor coldroom context ctxcoldstoragearea {
	//corrente: quanta roba c'è nella cold room
	//previsto: quanto deve ancora arrivare, ma per cui c'è un biglietto emesso
	[#
		var PesoEffettivo = 0
		var PesoPromesso = 0 
		var MAXW = 50
	#]
	
	State s0 initial {
		printCurrentMessage
	} Goto work
	
	State work{
		
	}Transition update whenMsg updateWeight -> updateWeight
					   whenRequest weightrequest -> checkweight
					   whenRequest getweight -> returnweight
					   
	State updateWeight {
		onMsg ( updateWeight : updateWeight(P_EFF, P_PRO) ) {
			[# PesoEffettivo += payloadArg(0).toInt() 
				PesoPromesso -= payloadArg(1).toInt()
				#]
		}
		println("coldroom update - peso promesso: $PesoPromesso, nuovo peso effettivo: $PesoEffettivo") color green
	} Goto work
	
	State checkweight {
		onMsg(weightrequest : weightrequest(PESO)){
			[# var PesoRichiesto = payloadArg(0).toInt() 
				#]
			println("coldroom - richiesti: $PesoRichiesto, effettivo: $PesoEffettivo, promesso: $PesoPromesso") color green
			
			if [# PesoEffettivo + PesoPromesso + PesoRichiesto  <= MAXW #]	{
				[# PesoPromesso += PesoRichiesto
					#]
					println("coldroom - accettato, peso promesso: $PesoPromesso") color green
				replyTo weightrequest with weightOK : weightOK( NO_PARAM)
			} else {
				println("coldroom - rifiutato") color green
				replyTo weightrequest with weightKO : weightKO( NO_PARAM )
			}
		}
	} Goto work
	
	State returnweight{
		onMsg(getweight : getweight(NO_PARAM)){
			replyTo getweight with currentweight : currentweight($PesoEffettivo, $PesoPromesso)
		}
	} Goto work	
	
}


QActor tickethandler context ctxcoldstoragearea {
	
	[#	
		
		var TICKETTIME = DomainSystemConfig.getTicketTime();
		

		var Token = "_"
		var Ticket = ""
		var Peso = 0
		var Sequenza = 0
		var Accepted = false
		
		var Tickets = mutableSetOf<String>()
		#]
	State s0 initial{
		println("tickethandler - ticketime: $TICKETTIME") color blue
		printCurrentMessage
	} Goto work
	
	
	State work {
	}Transition t0  whenRequest depositRequest -> checkforweight
					whenRequest checkmyticket -> checktheticket
	
	State checkforweight {
		
		onMsg(depositRequest : depositRequest(PESO)){
			
			[# Peso = payloadArg(0).toInt() #]
			println("tickethandler - richiedo $Peso") color blue
			request coldroom -m weightrequest : weightrequest($Peso)
		}
	}Transition t1 whenReply weightKO -> checkdeadlines
					whenReply weightOK -> returnticket
					
	
	State checkdeadlines{
				
		println("tickethandler - rifiutato, controllo i biglietti") color blue
		
		[# var SpazioLiberato = 0
			Accepted = false
			var Now = java.util.Date().getTime()/1000
			
			
			
			
			val it = Tickets.iterator()
    		while (it.hasNext()) {
    			var CurrentTicket = it.next()
    			
    			var TicketTokens = CurrentTicket.split(Token, ignoreCase = true, limit = 0)
    			var StartTime = TicketTokens.get(1).toInt()
				
				
				if( Now > StartTime + TICKETTIME){ //scaduto
					var PesoTicket = TicketTokens.get(2).toInt()
					SpazioLiberato += PesoTicket
					
					it.remove()
				}
        		
    		}
    		


    
			
				
			if (SpazioLiberato >= Peso){ //c'è abbastanza spazio per la richiesta corrente
				SpazioLiberato -= Peso
				Accepted = true
				}
		#]
		println("tickethandler - Spazio Liberato: $SpazioLiberato")
		forward coldroom -m updateWeight : updateWeight(0, $SpazioLiberato)
	} Goto returnticket if [# Accepted #] else reject
	
	State reject {
		println("tickethandler - non c'è comunque posto, vai a casa") color blue
		replyTo depositRequest with reject : reject( reject )
	} Goto work
	
	
	
	State returnticket {
		
		[# Ticket = "T".plus(Token)
			
			var Now = java.util.Date().getTime()/1000
			
			Ticket = Ticket.plus( Now ).plus(Token).plus( Peso ).plus(Token).plus( Sequenza)
			Sequenza++
			
			Tickets.add(Ticket)
			#]
			println("tickethandler - accettato") color blue
			replyTo depositRequest with accept : accept( $Ticket )
	} Goto work
	
	
	
	
	State checktheticket {
		onMsg(checkmyticket : checkmyticket(TICKET)){
			[#	var Ticket = payloadArg(0)
				var Ticketvalid = false;
				
				if(Tickets.contains(Ticket)){
					var StartTime = Ticket.split(Token, ignoreCase = true, limit = 0).get(1).toInt()
					
					var Now = java.util.Date().getTime()/1000
					if( Now < StartTime + TICKETTIME){
						Tickets.remove(Ticket)
						Ticketvalid = true
					}
						
				}
				
				#]
			println("tickethandler - il biglietto è valido? $Ticketvalid") color blue
			replyTo checkmyticket with ticketchecked : ticketchecked($Ticketvalid)
		}
	} Goto work
	
	
}

QActor serviceaccessgui context ctxcoldstoragearea {
	[#	var PESO = 0
		var Ticket = ""
		var Ticketok = false
		#]
	
	State s0 initial {
		printCurrentMessage
		println("SAG - in attesa") color yellow
	} Transition t0 whenMsg startToDoThings -> work
					
	
	
	State work {
		//random tra 10 e 20
		[# PESO = Math.floor(Math.random() *(20 - 10 + 1) + 10).toInt()
			#]
		println("SAG - chiedo $PESO") color yellow
		request tickethandler -m depositRequest : depositRequest($PESO)
		
	} Transition t0 whenReply accept -> gotoindoor
					whenReply reject -> tryagainlater
					
				
					
	State tryagainlater{
		println("SAG - rifiutato") color yellow
	}Transition wait whenTime 5000 -> work
	
	
	
	State gotoindoor{
		onMsg( accept : accept(TICKET)){
			[#	Ticket = payloadArg(0)
				#]
			println("SAG - accettato, Ticket: $Ticket") color yellow
		}
	}Transition t2 whenTime 3000 -> giveticket
	
	State giveticket{
		println("SAG - consegno il biglietto") color yellow
		
		request tickethandler -m checkmyticket : checkmyticket($Ticket)
	}Transition tc whenReply ticketchecked -> checkresponse
	
	State checkresponse {
		onMsg (ticketchecked : ticketchecked(BOOL)){
			[# Ticketok = payloadArg(0).toBoolean()
				# ]
		}
		println("SAG - biglietto accettato? : $Ticketok") color yellow
	} Goto work if [# !Ticketok #] else unloading


	State unloading{
		println("SAG - scarico") color yellow
	}Transition t4 whenTime 3000 -> loaddone
	
	State loaddone {
		request controller -m loaddone : loaddone($PESO)
	} Transition t6 whenReply chargetaken -> work
	
}
```


### Come avviare il test del prof
2) avviare l'immagine Docker webrobot23