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
Area rettangolare piana racchiusa entro quattro pareti. Procedendo dal bordo superiore e muovendoci in senso orario, i nomi delle pareti sono: wallUp, wallRight, wallDown, wallLeft. All'interno del Service Area il transport trolley è libero di muoversi. 
La stanza è rettangolare ed ha dimensione Lato-Lungo * lato-corto (L * l).

##### ==HOME==
Locazione all'interno della Service Area dove il transport trolley si trova rivolto verso il basso nell'angolo superiore sinistro. La Home è la zona della Service Area in cui il robot si troverà all'avvio e in ogni periodo di attesa di nuove richieste.

##### ==INDOOR port==
Locazione all'interno della Service Area in cui un camion si presenta per far caricare la merce al transport trolley. Si trova nell'angolo in basso a sinistra della Service Area.

##### ==ColdRoom Container==
Contenitore fisico posizionato all'interno della Service Area in una posizione fissa. 
In questo elemento il transport trolley è in grado di depositare cibo fino ad un massimo di MAXW kg. ColdRoom Container rappresenta un ostacolo all'interno della Service Area per il transport trolley, ciò vuol dire che non può muoversi nella posizione in cui l'elemento è localizzato.

##### ==Porta della ColdRoom==
Lato del ColdRoom Container tramite li quale è possibile depositare il cibo. Corrisponde al lato del container rivolto verso il basso della Service Area. Il transport trolley dovrà posizionarsi davanti alla porta della ColdRoom per poter depositare al suo interno il cibo.

##### ==DDR robot==
*Differential Drive Robot*, vedi [DDR](https://www.youtube.com/watch?v=aE7RQNhwnPQ).

##### ==Transport trolley==
DDR quadrato di lunghezza RD in grado di compiere i seguenti comandi: 
	- movimento avanti e indietro
	- rotazione sul posto a 360°
	- carica e scarica del cibo
   Come primo prototipo utilizzeremo il seguente robot fisico: https://github.com/XANA-Hub/ProgettoTT.git. Utilizzando il robot fisico si ha che il valore RD sarà più grande dell'effettiva lunghezza del robot per permetterne la rotazione.
   
##### ==Food-load==
quantità di cibo (in kg) che il robot dovrà scaricare dal Fridge Truck e mettere in ColdRoom Container.

##### ==Current weight==
Quantità di cibo attualmente contenuto in ColdRoom definito in base al peso.

##### ==ServiceAccesGUI==
ServiceAccesGUI permette ai driver di:
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
Il Ticket viene generato dal ColdStorageService a seguito di una richiesta effettuata da un driver tramite ServiceAccessGUI. Il Ticket rappresenta il permesso di scarico concesso ad un determinato FridgeTruck, valutando il peso della quantità di cibo da scaricare dichiarato dal driver ed il current weight.
Ogni Ticket è caratterizzato dai seguenti parametri:
- ticket time: tempo di validità del ticket generato;
- peso della quantità di cibo da scaricare dichiarato dal driver a cui viene assegnato il ticket;
- identificativo del driver a cui è assegnato il ticket;
- codice univoco che identifica il ticket generato.

### Analisi del Problema
![[ArchitetturaLogica_Sprint2.png]]
- Chi si occupa della generazione e della verifica di validità dei Ticket?
	Introduciamo un nuovo attore "TicketHandler" che si occupi di:
		1) verificare se è possibile generare il Ticket richiesto;
		2) generare i Ticket;
		3) verificare se il Ticket ricevuto è valido temporalmente, ovvero se è scaduto o meno.
- Protocollo di richiesta e generazione del ticket:
	1) Inizia con una request/response da parte del driver tramite ServiceAccessGUI verso TicketHandler, a cui viene passato il peso da scaricare;
	2) TicketHandler chiede a ColdRoom se c'è abbastanza spazio per depositare la quantità di cibo dichiarata dal driver sempre tramite Request/Response, la quale viene passata come parametro;
	3) Se c'è abbastanza spazio, ColdRoom aggiorna i propri attributi in modo tale da memorizzare che una quantità di peso è riservata al driver in questione che ne ha fatto richiesta e ritorna True, altrimenti False;
	4) Se TicketHandler riceve True genera il ticket e lo invia come risposta a ServiceAccessGui, altrimenti risponde Rejected
- Utilizzo del ticket:
	Una volta arivato in INDOOR, il driver, invia il Ticket a TicketHandler tramite Request/Response. Il TicketHandler verifica il **TICKETTIME** e restituisce Ok / Rejected, effettua quindi la verifica di validità temporale del Ticket. 
	Se la richiesta viene approvata ServiceAccessGUI invia tramite Request/Response al Controller la richiesta "load done" per notificare al Controller che il FridgeTruck è pronto e per inviare il peso effettivo che intende scaricare. Dopo di che attende una risposta "charge taken" da parte del Controller.
	
![[Sprint2/Doc/cicloVitaMessaggi.png]] 

- Problema, la sicurezza:
	- Dobbiamo assicurarci che chi richiede il ticket sia l'unico a poterlo usare.
	- Tutti vedono l'emissione di un ticket, ci sta bene? possibile violazione della privacy
	- Fare in modo che un ticket non sia riutilizzabile? possibile DoS, usiamo ticket sequenziali?
	- Fare in modo che la risposta ad una richiesta arrivi al camionista che l'ha mandata e solo a lui anche se la richiesta arriva da un dispositivo alieno.
- Cosa succede se viene fatta una richiesta prima che i driver che hanno fatto le richieste precedenti abbiano scaricato?
	La richiesta potrebbe venire accettata anche se dovrebbe essere rifiutata in quanto l'elaborazione delle richieste precedenti non è ancora terminata, per risolvere il problema definiamo due pesi diversi: 
	1)  Peso effettivo : quantità (peso) di cibo contenuto in ColdRoom nell'istante attuale, aggiornato solo dopo che il DDR robot ha terminato il carico/scarico merce di un determinato driver.
	2) Peso "promesso" : quantità di peso richiesta tramite Ticket dai driver che ancora però non hanno completato lo scarico, aggiornato dopo che il DDR robot ha terminato il carico/scarico merce di un determinato driver oppure quando la verifica del tickettime di un determinato Ticket ha un esito negativo.
	Questi due pesi si troveranno entrambi in ColdRoom (se un giorno ci saranno due punti di accesso il peso futuro deve essere in comune).
	Per verificare se accettare o meno una richiesta da parte di un driver è necessario confrontare la somma dei due pesi con il peso massimo contenibile in ColdRoom.
- Cosa succede se scade almeno un ticket prima che il driver a cui è associato arrivi in INDOOR, la somma tra il peso effettivo e peso promesso in ColdRoom corrisponde al peso massimo e arriva una richiesta quest'ultima viene rifiutata?
	Rifiutare la richiesta non sarebbe corretto dato che è presente almeno un Ticket scaduto il cui driver associato ancora non si è presentato in INDOOR e per il quale è ancora riservata una quantità di peso in ColdRoom la quale non verrà mai utilizzata da quel determinato FridgeTruck.
	Il problema è stato risolto nel seguente modo:
	TicketHandler mantiene in memoria i ticket emessi e non ancora verificati con il relativo istante, quando viene fatta una richiesta ma la somma tra peso effettivo e promesso in ColdRoom corrisponde al peso massimo allora TicketHandler controlla che non ci siano ticket scaduti che non hanno mai scaricato e, se presenti, aggiorna ColdRoom di conseguenza (rimuove il peso del ticket scaduto e se possibile aggiorna col nuovo ticket). 
- Contesti:
	- TicketHandler è contenuto sullo stesso contesto di Controller
	- TransportTrolley, ColdRoom e ServiceAccessGui avranno un contesto a parte per ciascuno
- ServiceAccessGUI deve dare la possibilità di vedere il peso in ColdRoom all'utente, lo facciamo come Req/Resp o come osservatore costantemente aggiornato?
	La cosa migliore sarebbe metterlo in ascolto dei cambiamenti a ColdRoom, ColdRoom diventa observable. In alternativa Req/Resp di deposit weigth fa una richiesta per sapere il peso in coldRoom. In entrambi i casi usiamo la somma tra peso effettivo e peso promesso.
	
- Chi si occupa di verificare la validità del Ticket presentato dal driver? Ovvero chi si occupa di controllare se il Ticket è scaduto o meno?
	La responsabilità di ricevere il Ticket da ServiceAccessGUI e di verificarne la validità può essere affidata all'attore TicketHandler oppure al Controller. 
	Abbiamo deciso di affidare la verifica del Ticket all'attore TicketHandler per le seguenti motivazioni:
	- principio di singola responsabilità: gli attori devono avere una singola responabilità e un solo motivo per cambiare. Il Controller ha la responsabilità di sapere quando il driver è pronto per scaricare per notificarlo al TransportTrolley in modo tale che quest'ultimo avvii il servizio di carico e scarico merce da parte del DDR robot. Il TicketHandler ha la responsabilità di gestire i  Ticket, di conseguenza è corretto che sia quest'ultimo ad occuparsi non solo di generare i Ticket richiesti dai driver ma anche di verificarne la validità quando ne riceve uno. 
	- problemi di sicurezza: i Ticket permettono ai FridgeTruck di depositare la merce in ColdRoom tramite il DDR robot e sono assegnati al singolo FridgeTruck. Per motivi di sicurezza i Ticket si preferisce assegnare la verifica al TicketHandler, essendo un'informazione privata del driver generata dal TicketHandler stessi.
- Quando e da chi vengono aggiornati i pesi in ColdRoom?
	Entrambi i pesi (effettivo e "promesso") vengono aggiornati quando il DDR robot ha terminato lo scarico del materiale precedentemente contenuto in un determinato driver. 
	Una volta che il Controller ha ricevuto una response da parte del TrasportTrolley relativa ad una precedente request "doJob", invia a ColdRoom un dispatch tramite il quale notifica a quest'ultimo la quantità peso da modificare sia per quanto riguarda quello effettivo che quello "promesso". In particolar modo viene passata la quantità da decrementare dal peso "promesso" e la quantità da incrementare al peso effettivo, i due valori possono essere diversi a causa del problema del driver distratto [non so come fare collegamento con la spiegazione del dirver distratto in Sprint0]
- Quando il driver può uscire dal sistema?
	Il driver può uscire dal sistema quando ha scaricato tutta la merce contenuta, ovvero quando riceve dal Controller la response "charge taken" associata ad una precedente request "load done".
- Quando viene inviato il "charge taken"?
	Il "charge taken" può essere inviato al driver in corrispondenza all'istante con il quale il Controller invia al TransportTrolley la request "doJob" associata alla rischiesta del driver in questione oppure una volta che il TransportTrolley invia al Controller la response relativa allo scarico del cibo contenuto nel FridgeTruck in questione.
	Al driver non interessa sapere se il robot ha avuto problematiche o meno, ovvero se lo scarico è andato a buon fine o meno, quindi il "charge taken" potrebbe essere inviato prima. Ma se il DDR Robot prende il cibo direttamente da dentro il FridgeTruck allora è necessario che quest'ultimo aspetti che il DDR Robot abbia terminato le operazioni per poter ricevere il "charge taken" ed uscire dal sistema.
	[chidere al professore-->NO IPOTIZIAMO NOI]
	[coda perchè possono arrivare più load done insieme]


NOTE:
Per quanto riguarda l'implementazione è necessario un ServiceAccessGUI per ogni camion che si presenta, in quanto tutte le richieste e comunicazioni sono sincrone bloccanti. Ad ogni ServiceAccessGUI deve essere associata una grafica html. Che tecnologia utilizzare?
