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
- Protocollo di richiesta del ticket:
	1) Inizia con una req/resp da parte del driver tramite ServiceAccessGUI verso ColdStorageService, a cui viene passato il peso da scaricare;
	2) ColdStorageService chiede a ColdRoom se c'è abbastanza spazio per depositare la quantità di cibo dichiarata dal driver;
	3) Se c'è abbastanza spazio, ColdRoom aggiorna il peso ipotetico e ritorna True, altrimenti False e non aggiorna un cazzo di niente;
	4) Se ColdStorageService riceve True genera il ticket e lo invia come risposta a ServiceAccessGui, altrimenti risponde Rejected
- Utilizzo del ticket:
	Ticket viene mandato a ColdStorageService tramite req/resp che verifica il **TICKETTIME** e restituisce approved/rejected. Se la richiesta viene approvata ServiceAccessGUI invia tramite req/resp al Controller la richiesta "load done" per notificare al Controller che il FridgeTruck è pronto a scaricare. Dopo di che attende una risposta "charge taken" da parte del Controller. 
- Problema, la sicurezza:
	- Dobbiamo assicurarci che chi richiede il ticket sia l'unico a poterlo usare.
	- Tutti vedono l'emissione di un ticket, ci sta bene? possibile violazione della privacy
	- Fare in modo che un ticket non sia riutilizzabile? possibile DoS, usiamo ticket sequenziali?
	- Fare in modo che la risposta ad una richiesta arrivi al camionista che l'ha mandata e solo a lui anche se la richiesta arriva da un dispositivo alieno.
- Cosa succede se viene fatta una richiesta mentre le prime non sono ancora state scaricate?
	potrebbe venire accettata anche se dovrebbe essere rifiutata, per risolvere il problema definiamo due pesi diversi: 
	1) Un peso ipotetico che indica il peso ottenuto completate tutte le richieste.
	2) Il peso effettivo in coldRoom aggiornato solo dopo che il controller riceve il ticket.
	Questi due pesi si troveranno entrambi in ColdRoom (se un giorno ci saranno due punti di accesso il peso futuro deve essere in comune)
- Se scade un ticket e non viene scaricato il peso, in ColdRoom comunque rimane segnato il peso ipotetico:
	ColdStorageService si occupa di mantenere in memoria i ticket emessi con il relativo istante, quando viene fatta una richiesta ma lo spazio ipotetico in ColdRoom è pieno CSS controlla che non ci siano ticket scaduti che non hanno mai scaricato e, se presenti, aggiorna coldRoom di conseguenza (rimuove il peso del ticket scaduto e se possibile aggiorna col nuovo ticket). Diventa necessario che Controller faccia sapere a CSS quando viene accattato un ticket (sempre tramite dispatch). NOTA: se il dispatch fallisce? Va tutto a puttane ma non ce ne preoccupiamo perchè non ne vale la pena.
- Contesti:
	- ColdStorageService gira sullo stesso contesto di Controller
	- TransportTrolley, ColdRoom e ServiceAccessGui avranno un contesto a parte per ciascuno
- ServiceAccessGUI deve dare la possibilità di vedere il peso in ColdRoom all'utente, lo facciamo come Req/Resp o come osservatore costantemente aggiornato?
	La cosa migliore sarebbe metterlo in ascolto dei cambiamenti a ColdRoom, ColdRoom diventa observable. In alternativa Req/Resp di deposit weigth fa una richiesta per sapere il peso in coldRoom. In entrambi i casi usiamo il peso ipotetico.
	
- Chi si occupa di verificare la validità del Ticket presentato dal driver? Overo chi si occupa di controllare se il Ticket è scaduto o meno?
	La responsabilità di ricevere il Ticket da ServiceAccessGUI e di verificarne la validità può essere affidata all'attore TicketHandler oppure al Controller. 
	Abbiamo deciso di affidare la verifica del Ticket all'attore TicketHandler per le seguenti motivazioni:
	- principio di singola responsabilità: gli attori devono avere una singola responabilità e un solo motivo per cambiare. Il Controller ha la responsabilità di sapere quando il driver è pronto per scaricare per notificarlo al TransportTrolley in modo tale che quest'ultimo avvii il servizio di carico e scarico merce da parte del DDR robot. Il TicketHandler ha la responsabilità di gestire i  Ticket, di conseguenza è corretto che sia quest'ultimo ad occuparsi non solo di generare i Ticket richiesti dai driver ma anche di verificarne la validità quando ne riceve uno. 
	- problemi di sicurezza: i Ticket permettono ai FridgeTruck di depositare la merce in ColdRoom tramite il DDR robot e sono assegnati al singolo FridgeTruck. Per motivi di sicurezza i Ticket si preferisce assegnare la verifica al TicketHandler, essendo un'informazione privata del driver generata dal TicketHandler stessi.
### Architettura logica
![[ArchitetturaLogica_Sprint2.png]]

- [x] facciamo uno schemino che fa per bene tutti i passaggi nello scambio dei messaggi 
![[cicloVitaMessaggi.png]]
- [ ] mettere in ordine le domande, in modo tale che tutto abbia un senso
ATTENZIONE: COLDSTORAGESERVICE rinominato TicketHandler

NEW:

- Driver Distratto?
	- problema del peso ipotetico: dato che comunque ad ogni nuova richiesta cicliamo per vedere se ci sono ticket scaduti, tanto vale usare solo il peso effettivo e aggiungere la somma dello spazio promesso nei ticket ancora non riscattati.
	- Poichè il peso ipotetico deve essere sempre visibile nella GUI, io ho bisogno di venere in ColdRoom sia il peso ipotetico che quello effettivo, questo vuol dire anche che quando il robot finisce il lavoro devo aggiornare sia il peso effettivo sia il peso ipotetico di ColdRoom, i cazzi arrivano quando il peso dichiarato nel ticket e il peso dichiarato in "load done" sono diversi.
NOTE: ha senso che il camionista se ne vada quando riceve "charge taken", non è compito del camionista gestire il caso in cui il robottino ha avuto dei problemi.
Altra domanda: chi manda al camionista il messaggio "charge taken"?
Quando mando la doJob al Robot da parte del controller, poichè sto lavorando con request/responce posso anche mandare al camionista la "charge taken", di conseguenza può essere il controller a mandare il messaggio (e posso anche gestire un minimo di sicurezza... da ragionare meglio su questo aspetto)
==chiediamo  natali per conferma==

- Driver Distratto?
	- problema del peso ipotetico: dato che comunque ad ogni nuova richiesta cicliamo per vedere se ci sono ticket scaduti, tanto vale usare solo il peso effettivo e aggiungere la somma dello spazio promesso nei ticket ancora non riscattati.

