### Goal Sprint 1
1) Basic Robot + ColdStorageService
2) [[Cold Storage Service - Natali |Link al modello precedente]]

### Requisiti
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

### Analisi dei Requisiti
Definizioni:
`definire in modo più formale e comprensibile alla macchina`
##### ==DDR robot==
*Differential Drive Robot*, vedi [DDR](https://www.youtube.com/watch?v=aE7RQNhwnPQ).
[robot](file:///C:/Users/lomba/Desktop/iss23/iss23Material/html/BasicRobot23.html)
La documentazione introduce il concetto di Step. Sfruttiamo lo Step per muovere il Robot in avanti della sua lunghezza RD.
RD diventa l'unità di misura dello spazio sostituendo i metri.

##### ==Transport trolley==
Transport trolley è un DDR robot. I comandi che è in grado di compiere sono descritti nell'apposita [documentazione](file:///C:/Users/lomba/Desktop/iss23/iss23Material/html/BasicRobot23.html) .

##### ==Service Area==
Area rettangolare di dimensione L * l. L'area sarà suddivisa in una griglia con coordinate la cui unità di misura è RD.

`NOTE: L'area è piana, racchiusa entro quattro pareti. Procedendo dal bordo superiore e muovendoci in senso orario, i nomi delle pareti saranno: wallUp, wallRight, wallDown, wallLeft. All'interno del Service Area il transport trolley è libero di muoversi.La stanza è rettangolare ed ha dimensione Lato-Lungo * lato-corto (L * l). Per definire la posizione del robot in ogni momento l'area è divisa in una griglia con coordinate crescenti associate a partire dall'angolo in alto a sinistra`

##### ==HOME==
Zona della Service Area corrispondente alle coordinate (0,0)

`NOTE: Locazione all'interno della Service Area dove il transport trolley si trova rivolto verso il basso, nell'angolo superiore sinistro. La Home è la zona della Service Area in cui il robot si troverà all'avvio e in ogni periodo di attesa di nuove richieste.`

##### ==INDOOR port==
Zona della Service Area corrispondente alle coordinate (0,MAX)

`NOTE: Locazione all'interno della Service Area in cui un camion scarica la merce da far caricare al transport trolley. Si trova nell'angolo in basso a sinistra della Service Area. Le coordinate crescono allontanadosi dalla HOME, INDOOR port si trova a distanza massima sull'asse Y`

##### ==ColdRoom Container==
Contenitore in posizione fissa (x,y) in Service Area in grado di ricevere e contenere cibo da un lato specifico. Ha una capienza pari a MAXW kg.

`NOTE: Contenitore fisico posizionato all'interno della Service Area in una posizione fissa. In questo elemento il transport trolley è in grado di depositare cibo fino ad un massimo di MAXW kg. ColdRoom Container rappresenta un ostacolo all'interno della Service Area per il transport trolley, ciò vuol dire che non può muoversi attraverso la posizione in cui l'elemento è localizzato, per semplicità supporremo che il container occupi interamente una sola coordinata di Service Area.`

##### ==Porta della ColdRoom==
Lato della ColdRoom che si affaccia sull'area di coordinate (x, y+1). Transport Trolley dovrà trovarsi in questa posizione per interagire con ColdRoom.

`NOTE: Lato del ColdRoom Container tramite li quale è possibile depositare il cibo. Corrisponde al lato del container rivolto verso il basso della Service Area. Il transport trolley dovrà posizionarsi davanti alla porta della ColdRoom per poter depositare al suo interno il cibo overo in corrispondenza delle coordinate (x, y+1).`

##### ==Food-load==
Carico (in kg) che il robot caricherà da Indoor e depositerà in ColdRoom Container.


### Analisi del Problema
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
- [ ] Aggiungere una figura finale generata dal qak automaticamente
### Progettazione
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
		var PesoCorrente = 0
	#]
	
	State s0 initial {
		printCurrentMessage
	} Transition update whenMsg updateWeight -> updateWeight
	
	State updateWeight {
		printCurrentMessage
		onMsg ( updateWeight : updateWeight(PESO) ) {
			[# PesoCorrente += payloadArg(0).toInt() 
				#]
		}
		println("peso aggiornato")
		println("nuovo peso: $PesoCorrente")
	} Transition update whenMsg updateWeight -> updateWeight
}

```


- [ ] Tutorial su come far partire la DEMO (passaggi e cose varie)

1) Avviare il container itunibovirtualrobot23 su docker
	Viene lanciato l'ambiente virtuale con il robot all'indirizzo http://localhost:8090/
2) In intellij avviare il file MainCtxbasicrobot.kt del progetto BasicRobot
3) In intellij avviare il file MainCtxColdStorageArea.kt del progetto coldStorage
