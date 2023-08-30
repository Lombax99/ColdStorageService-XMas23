### Goal Sprint 2
In questa seconda fase ci concentriamo sul processo di emissione dei ticket e le valutazioni di sicurezza annesse.
[[Sprint 1|Link allo sprint 1]]
### Requisiti
A company intends to build a ColdStorageService, composed of a set of elements:

1. a ==service area== (rectangular, flat) that includes:[[Sprint 2]]
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
DDR contenuto in un quadrato di lunghezza RD in grado di compiere i seguenti comandi: 
	- step: `sposta il robot nella casella di fronte`
	- turn_left: `muove l'orientamento del robot di 90° verso sinistra`
	- turn_right: `muove l'orientamento del robot di 90° verso destra`
	- load_food: `carica il cibo (da Indoor)`
	- unload_food: `scarica il cibo (in ColdRoom)`

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
- ticket time: istante di emissione del ticket;
- peso della quantità di cibo da scaricare dichiarato dal driver;
- identificativo del driver a cui è assegnato il ticket;
- codice univoco che identifica il ticket generato.

### Analisi del Problema

![[ArchitetturaLogica_Sprint2.png]]

- ==Chi si occupa della generazione e della verifica di validità dei Ticket?==
	Introduciamo un nuovo attore "TicketHandler" che si occupi di:
		1) verificare se è possibile generare il Ticket richiesto;
		2) generare i Ticket;
		3) verificare se il Ticket ricevuto è scaduto o meno.

- ==Protocollo di richiesta e generazione del ticket:==
![[Sprint2/Doc/cicloVitaMessaggi.png]] 
	1) Inizia con una request/response da parte del driver tramite ServiceAccessGUI verso TicketHandler, a cui viene passato il peso da scaricare;
	2) TicketHandler chiede a ColdRoom se c'è abbastanza spazio per depositare la quantità di cibo dichiarata dal driver sempre tramite request/response, la quale viene passata come parametro;
	3) Se c'è abbastanza spazio, ColdRoom aggiorna i propri attributi in modo tale da memorizzare che una quantità di peso è riservata al driver in questione che ne ha fatto richiesta e risponde True, altrimenti False;
	4) Se TicketHandler riceve True genera il ticket e lo invia come risposta a ServiceAccessGui, altrimenti risponde Rejected
	5) Una volta arrivato in INDOOR, il driver, invia il Ticket a TicketHandler tramite Request/Response. Il TicketHandler verifica il **TICKETTIME** e restituisce Ok / Rejected, effettua quindi la verifica di validità temporale del Ticket. 
	6) Se la richiesta viene approvata ServiceAccessGUI invia tramite Request/Response al Controller la richiesta "load done" per notificare al Controller che il FridgeTruck è pronto e per inviare il peso effettivo che intende scaricare. Dopo di che attende una risposta "charge taken" da parte del Controller.
	
- ==Problema, la sicurezza:==
	- Dobbiamo assicurarci che chi richiede il ticket sia l'unico a poterlo usare.
	- Tutti vedono l'emissione di un ticket, ci sta bene? possibile violazione della privacy o copia.
	- Fare in modo che un ticket non sia riutilizzabile? possibile DoS, usiamo ticket sequenziali?
	- Fare in modo che la risposta ad una richiesta arrivi al camionista che l'ha mandata e solo a lui anche se la richiesta arriva da un dispositivo alieno.
	
- ==Come avviene la memorizzazione del peso in ColdRoom?==
	E' necessario prendere in considerazione il seguente problema:
	Un driver potrebbe inviare la richiesta di un Ticket prima che driver, a cui è stato generato un Ticket in precedenza, abbiano scaricato.
	Ciò potrebbe essere un problema nel caso in cui la somma tra il peso attuale contenuto in ColdRoom e il peso della quantità di materiale che intendono scaricare i driver che ancora non sono arrivati in INDOOR ma che hanno già un Ticket corrisponde al peso totale contenibile da ColdRoom.
	
	Per risolvere il problema definiamo due pesi diversi: 
	1)  Peso effettivo : quantità (peso) di cibo contenuto in ColdRoom nell'istante attuale, aggiornato solo dopo che il DDR robot ha terminato il carico/scarico merce di un determinato driver.
	2) Peso "promesso" : quantità di peso richiesta tramite Ticket dai driver che ancora però non hanno completato lo scarico, aggiornato dopo che il DDR robot ha terminato il carico/scarico merce di un determinato driver oppure quando la verifica del tickettime di un determinato Ticket ha un esito negativo.
	Questi due pesi si troveranno entrambi in ColdRoom (se un giorno ci saranno due punti di accesso il peso futuro deve essere in comune).
	Per verificare se accettare o meno una richiesta da parte di un driver è necessario confrontare la somma dei due pesi con il peso massimo contenibile in ColdRoom.

- ==Cosa succede se scade uno o più un Ticket prima che i driver a cui sono associati arrivino in INDOOR, la somma tra il peso effettivo e peso promesso in ColdRoom corrisponde al peso massimo e arriva una richiesta di generazione di un Ticket? Quest'ultima viene rifiutata?==
	Rifiutare la richiesta non sarebbe corretto dato che è presente almeno un Ticket scaduto il cui driver associato ancora non si è presentato in INDOOR e per il quale è ancora riservata una quantità di peso in ColdRoom che non verrà mai utilizzata da quel determinato FridgeTruck.
	Il problema è stato risolto nel seguente modo:
	
	TicketHandler mantiene in memoria i Ticket emessi e non ancora verificati con il relativo istante, quando viene fatta una richiesta ma la somma tra peso effettivo e promesso in ColdRoom corrisponde al peso massimo allora TicketHandler controlla che non ci siano ticket scaduti che non hanno mai scaricato e, se presenti, aggiorna ColdRoom di conseguenza (rimuove il peso del ticket scaduto e se possibile aggiorna col nuovo ticket). 
	
- ==Contesti:==
	- TicketHandler è contenuto sullo stesso contesto di Controller
	- TransportTrolley, ColdRoom e ServiceAccessGui avranno un contesto a parte per ciascuno
	
- ==ServiceAccessGUI deve dare la possibilità di vedere il peso in ColdRoom all'utente, lo facciamo come Req/Resp o come osservatore costantemente aggiornato?==
	La cosa migliore sarebbe metterlo in ascolto dei cambiamenti a ColdRoom, ColdRoom diventa observable. In alternativa Req/Resp di deposit weigth fa una richiesta per sapere il peso in coldRoom. In entrambi i casi usiamo la somma tra peso effettivo e peso promesso.
	
- ==Chi si occupa di verificare la validità del Ticket presentato dal driver? Ovvero chi si occupa di controllare se il Ticket è scaduto o meno?==
	La responsabilità di ricevere il Ticket da ServiceAccessGUI e di verificarne la validità può essere affidata all'attore TicketHandler oppure al Controller. 
	Abbiamo deciso di affidare la verifica del Ticket all'attore TicketHandler per le seguenti motivazioni:
	- principio di singola responsabilità: gli attori devono avere una singola responabilità e un solo motivo per cambiare. Il Controller ha la responsabilità di sapere quando il driver è pronto per scaricare per notificarlo al TransportTrolley in modo tale che quest'ultimo avvii il servizio di carico e scarico merce da parte del DDR robot. Il TicketHandler ha la responsabilità di gestire i  Ticket, di conseguenza è corretto che sia quest'ultimo ad occuparsi non solo di generare i Ticket richiesti dai driver ma anche di verificarne la validità quando ne riceve uno. 
	- problemi di sicurezza: i Ticket permettono ai FridgeTruck di depositare la merce in ColdRoom tramite il DDR robot e sono assegnati al singolo FridgeTruck. Per motivi di sicurezza i Ticket si preferisce assegnare la verifica al TicketHandler, essendo un'informazione privata del driver generata dal TicketHandler stessi.
	
- ==Quando e da chi vengono aggiornati i pesi in ColdRoom?==
	1) Entrambi i pesi (effettivo e "promesso") vengono aggiornati quando il DDR robot ha terminato lo scarico del materiale precedentemente contenuto in un determinato driver. Una volta che il Controller ha ricevuto una response da parte del TrasportTrolley relativa ad una precedente request "doJob", invia a ColdRoom un dispatch tramite il quale notifica a quest'ultimo la quantità peso da modificare sia per quanto riguarda quello effettivo che quello "promesso". In particolar modo viene passata la quantità da decrementare dal peso "promesso" e la quantità da incrementare al peso effettivo, i due valori possono essere diversi a causa del problema del driver distratto [non so come fare collegamento con la spiegazione del dirver distratto in Sprint0].

	2) In un caso particolare i pesi in ColdRoom sono aggiornati anche da TicketHandler tramite un dispatch "updateWeight". Il caso particolare è il seguente:
		se scade uno o più un Ticket prima che i driver a cui sono associati arrivino in INDOOR, la somma tra il peso effettivo e peso promesso in ColdRoom corrisponde al peso massimo e arriva una richiesta di generazione di un Ticket.
		Come spiegato in precedenza se ariva una richiesta di generazione di un Ticket e il peso in ColdRoom corrisponde al peso massimo viene effettuata una verifica del TICKETTIME associato a tutti i Ticket generati ed i cui driver ancora non sono arrivati in INDOOR.
		Nel caso in cui sia presente almeno un Ticket tra quest'ultimi che è scaduto temporalmente allora il TicketHandler procederà ad aggiornare il peso in ColdRoom tramite dispatch.

- ==Quando il driver può uscire dal sistema?==
	Il driver può uscire dal sistema quando ha scaricato tutta la merce contenuta, ovvero quando riceve dal Controller la response "charge taken" associata ad una precedente request "load done".

- ==Quando viene inviato il "charge taken"?==
	Il "charge taken" viene inviato al driver dal Controller in corrispondenza all'istante con il quale il Controller invia al TransporTrolley la request "doJob" associata alla richiesta del driver in questione.
	Le motivazioni di questa scelta sono le seguenti:
	1) Al driver non interessa sapere se il TransportTrolley ha avuto problematiche durante il trasporto del materiale precedentemente conenuto nel FridgeTruck, ovvero se lo scarico è andato a buon fine o meno, quindi il "charge taken" può essere inviato prima che il TransportTrolley comunichi al Controller se il carico/scarico in ColdRoom è terminato e il suo esito. 
	2) Supponiamo che il materiale da portare in ColdRoom non venga prelevato direttamente dal FridgeTruck, supponiamo che quest'ultimo scarichi la merce in una piattaforma dedicata, dalla quale il DDR robot preleverà il cibo e lo scaricherà in ColdRoom in uno o più volte a seconda della quantità di materiale dichiarata.

NOTE:
Per quanto riguarda l'implementazione è necessario un ServiceAccessGUI per ogni camion che si presenta, in quanto tutte le richieste e comunicazioni sono sincrone bloccanti. Ad ogni ServiceAccessGUI deve essere associata una grafica html. Che tecnologia utilizzare?
