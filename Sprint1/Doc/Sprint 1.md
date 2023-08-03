### Goal Sprint 1
1) Basic Robot + Controller e Cold Storage
2) Link al modello precedente

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
DDR contenuto in un quadrato di lunghezza RD in grado di compiere i seguenti comandi: 
	- step: `sposta il robot nella casella di fronte`
	- turn_left: `muove l'orientamento del robot di 90° verso sinistra`
	- turn_right: `muove l'orientamento del robot di 90° verso destra`
	- load_food: `carica il cibo (da Indoor)`
	- unload_food: `scarica il cibo (in ColdRoom)`


##### ==Food-load==
Carico (in kg) che il robot caricherà da Indoor e depositerà in ColdRoom Container.


### Analisi del Problema

![[Sprint1/Doc/ArchitetturaLogica_Sprint1.png]]

- Chi manda i comandi al Transport Trolley?
	Introduciamo un nuovo attore "Controller" che si occupi di mandare i comandi al Transport Trolley e gestire la logica applicativa. 
	In questo primo sprint supponiamo che il Controller sia a conoscenza dell'istante di arrivo dei Fridge Truck in INDOOR, di conseguenza il servizio partirà da un segnale generato dal controller).
- Quali comandi è in grado di comprendere il Transport Trolley?
	L'unico comando mandato dal controller è "doJob", nell'istante in cui è presente un Fridge Truck nella posizione di INDOOR port pronto a scaricare..
- Come parliamo con il DDR robot? Cosa ci può dare il committente a proposito?
	Il robot riceve messaggio da socket tcp [Link al protocollo del robot](https://github.com/XANA-Hub/ProgettoTT/blob/main/Sprint%201.md).
- Chi traduce "doJob" in una serie di comandi comprendibili al DDR robot?
	L'attore TransportTrolley si occupa di svolgere questo compito. Ricevere il comando "doJob" dal Controller, lo interpreta e comunica al robot fisico (DDR robot) le operazioni da eseguire.
- Come avviene la comunicazione tra Controller e Transport Trolley? Dispatch o Req-Resp?
	Controller manda un segnale di Req-Resp al Transport Trolley, ovvero rimane in attesa di una risposta da quest'ultimo. In quanto è necessario sapere se il servizio richiesto è andato a buon fine oppure se il DDR robot ha avuto problematiche che lo hanno interrotto.
- Vogliamo sapere se il comando è andato a buon fine? Se sì che tipo di segnale è la risposta?
	Sì, è necessario ricevere una risposta da parte del Transport Trolley per sapere se il servizio di carico e scarico è andato a buon fine o meno. Se il robot interrompe il suo corretto andamento gli attori del sistema, in particolar modo il Controller, lo devono sapere.
	Il Transport Trolley riceve quindi una request dal Controller ("doJob") e risponde con una response, in modo tale da notificare al Controller se il servizio di carico e scarico della merce è andato a buon fine o meno.
- Come comunicano TransportTrolley e ColdRoom?
	Transport Trolley e ColdRoom non comunicano direttamente ma solo tramite Controller.
- Quando viene aggiornato il peso della ColdRoom (e da chi)?
	Il peso della ColdRoom viene aggiornato dal Controller quando quest'ultimo riceve dal TransportTrolley la risposta relativa alla richiesta "doJob" fatta precedentemente. Se il servizio è andato a buon fine allora il Controller aggiorna il peso della ColdRoom tramite Dispatch.
- Come fa il Transport Trolley a sapere dov'è e dove deve andare?
	Dividiamo la stanza in una griglia di quadrati di lato RD (lunghezza del DDR robot, in realtà per utilizzare il robot fisico sarà necessario prendere in considerazione una griglia di quadrati di lato poco più grande di RD, in modo tale da permettere al robot di girarsi). 
	Le coordinate del Transport Trolley indicheranno il quadrato in cui si trova. L'origine (0, 0) sarà la posizione di Home. Coordinate crescenti verso il basso e verso destra.
- Quando viene fatta la mappatura della stanza?
	La mappatura della stanza viene fatta al momento dell'avviamento del DDR robot, prima di leggere qualsiasi richiesta dal controller.
- Quando controlla il TransportTrolley se ci sono altre richieste? 
	Come da requisiti il controllo se ci sono altre richieste viene fatto:
	- Quando il DDR robot risulta fermo in HOME, in attesa di nuovi comandi;
	- Dopo aver scaricato la merce nella ColdRoom, prima di tornare in HOME, il TransportTrolley verifica se ci sono altre richieste.
- Il robot ha un peso massimo? 
	Sì, il DDR robot ha un peso massimo trasportabile. Il carico che il robot deve prendere dal camion può essere maggiore del peso trasportabile dal DDR robot. In tal caso sarà il robot a decidere quanti giri fare in base al peso che deve essere trasportato.


