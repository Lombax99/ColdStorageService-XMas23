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

3. a ==ServiceAcessGUI== that allows an human being to see the ==current weigth== of the material stored in the ColdRoom and to send to the ==ColdStorageService== a request to store new **FW** kg of food. If the request is accepted, the services return a ==ticket== that expires after a prefixed amount of time (**TICKETTIME** secs) and provides a field to enter the ticket number when a Fridge truck is at the INDOOR of the service.

### Analisi dei Requisiti
Definizioni:
- ==Service Area==: Area rettangolare piana racchiusa entro quattro pareti. Procedendo dal bordo superiore e muovendoci in senso orario, i nomi delle pareti sono: wallUp, wallRight, wallDown, wallLeft. All'interno del Service Area il transport trolley è libero di muoversi. La stanza ha dimensione Lato-Lungo * lato-corto (L * l).
- ==HOME==: Locazione all'interno della Service Area dove il transport trolley il trova rivolto verso il basso nell'angolo superiore sinistro. La Home è la zona della Service Area in cui il robot si troverà all'avvio e in ogni periodo di attesa di nuove richieste.
- ==INDOOR port==: Locazione all'interno della Service Area in cui un camion si presenta per far caricare la merce al transport trolley. Si trova nell'angolo in basso a sinistra della Service Area.
- ==ColdRoom Container==: Contenitore fisico posizionato all'interno della Service Area in una posizione fissa. In questo elemento il transport trolley è in grado di depositare cibo fino ad un massimo di MAXW kg. ColdRoom Container rappresenta un ostacolo per il transport trolley, ciò vuol dire che non può muoversi nella posizione in cui l'elemento è localizzato.
- ==Porta della ColdRoom==: Lato del ColdRoom Container tramite li quale è possibile depositare il cibo. Corrisponde al lato del container rivolto verso il basso della Service Area. Il transport trolley dovrà posizionarsi davanti alla porta della ColdRoom per poter depositare al suo interno il cibo.
- ==DDR robot==: *Differential Drive Robot*, vedi [DDR](https://www.youtube.com/watch?v=aE7RQNhwnPQ).
- ==Transport trolley==: DDR quadrato di lunghezza RD in grado di compiere le seguenti mosse: 
	- movimento avanti e indietro
	- rotazione sul posto a 360°
	- carica e scarica del cibo
   Come primo prototipo utilizzeremo il seguente robot fisico: https://github.com/XANA-Hub/ProgettoTT.git. Utilizzando il robot fisico si ha che il valore RD sarà più grande dell'effettiva lunghezza del robot per permetterne la rotazione.
- ==Food-load==: quantità di cibo (in kg) che il robot dovrà scaricare dal Fridge Truck e mettere in ColdRoom Container.
- ==ServiceAcessGUI==:
- ==ColdStorageService==:
- ==ticket==: 
- ==current weigth==: Quantità di cibo contenuto in ColdRoom definito in base al peso.

### Analisi del Problema
- Protocollo di richiesta del ticket:
	1) Inizia con una req/resp verso ColdStorageService a cui viene passato il peso da scaricare
	2) CSS chiede a coldRoom se c'è abbastanza spazio
	3) Se c'è abbastanza spazio, ColdRoom aggiorna il peso ipotetico e ritorna True, altrimenti False e non aggiorna un cazzo di niente
	4) Se CSS riceve true genera il ticket e lo invia come risposta a ServiceAccessGui altrimenti risponde Rejected
- Utilizzo del ticket:
	Ticket viene mandato a Controller tramite req/resp che verifica il **TICKETTIME** e restituisce approved/rejected. Se la richiesta viene approvata Controller invia a ColdRoom un update weight tramite dispatch che aggiorna il peso effettivo.
	NOTA: ticket deve contenere il peso da scaricare oltre che al **TICKETTIME**
- Se il robottino crash?
	Sticazzi tanto nessuno lo fa ripartire quindi uno stronzo (sistemista) dovrà metterci mano, noi non lo facciamo, quelli sono cazzi vostri.
- Problema, la sicurezza:
	- Dobbiamo assicurarci che chi richiede il ticket sia l'unico a poterlo usare.
	- Tutti vedono l'emissione di un ticket, ci sta bene? possibile violazione della privacy
	- Fare in modo che un ticket non sia riutilizzabile? possibile DoS, usiamo ticket sequenziali?
	- Fare in modo che la risposta ad una richiesta arrivi al camionista che l'ha mandata e solo a lui anche se la richiesta arriva da un dispositivo alieno.
- Cosa succede se viene fatta una richiesta mentre le prime non sono ancora state scaricate?
	potrebbe venire accettata anche se dovrebbe essere rifiutata, per risolvere il problema definiamo due pesi diversi: 
	1) Un peso ipotetico che indica il peso ottenuto completate tutte le richieste.
	2) Il peso effettivo in coldRoom aggiornato solo dopo che il controller riceve il ticket.
	Questi due pesi si troveranno entrambi in coldStorageRoom (se un giorno ci saranno due punti di accesso il peso futuro deve essere in comune)
- Se scade un ticket e non viene scaricato il peso, in ColdRoom comunque rimane segnato il peso ipotetico:
	Diciamo che ColdStorageService si occupa di mantenere in memoria i ticket emessi con il relativo momento, quando viene fatta una richiesta ma lo spazio ipotetico in ColdRoom è pieno CSS controlla che non ci siano ticket scaduti che non hanno mai scaricato e, se presenti, aggiorna coldRoom di conseguenza (rimuove il peso del ticket scaduto e se possibile aggiorna col nuovo ticket). Diventa necessario che Controller faccia sapere a CSS quando viene accattato un ticket (sempre tramite dispatch). NOTA: se il dispatch fallisce? Va tutto a puttane ma non ce ne preoccupiamo perchè non ne vale la pena.
- Contesti:
	- CSS gira sullo stesso contesto di Controller
	- TransportTrolley, ColdRoom e ServiceAccessGui avranno un contesto a parte per ciascuno
- SAG deve dare la possibilità di vedere il peso in ColdRoom all'utente, lo facciamo come Req/Resp o come osservatore costantemente aggiornato?
	La cosa migliore sarebbe metterlo in ascolto dei cambiamenti a ColdRoom, ColdRoom diventa observable. In alternativa Req/Resp di deposit weigth fa una richiesta per sapere il peso in coldRoom.
	In entrambi i casi usiamo il peso ipotetico altrimenti l'utente crede di essere scemo quando la richiesta viene rifiutata ma il peso sembra starci...

- [ ] facciamo uno schemino che fa per bene tutti i passaggi nello scambio dei messaggi 

NEW:
Dobbiamo decidere chi si occupa di controllare se il ticket è scaduto:
- opz1: controller --> perchè?
- opz2: gestoreTicket (chi ha emesso il ticket) --> perchè?
	- principio di singola responsabilità
	- problemi di sicurezza
	- problemi di parallelismo
- ciclo di vita dei messaggi al camionista?
	- emetto il ticket
	- arrivo --> ticket valido
	- send "load done" (request/responce mi garantisce di rispondere alla persona giusta)
	- recive "charge taken"
	- poi il robot lavora
	- il camion se ne và quando ricceve charge taken o quando riceve job done?
NOTE: ha senso che il camionista se ne vada quando riceve "charge taken", non è compito del camionista gestire il caso in cui il robottino ha avuto dei problemi.
Altra domanda: chi manda al camionista il messaggio "charge taken"?
Quando mando la doJob al Robot da parte del controller, poichè sto lavorando con request/responce posso anche mandare al camionista la "charge taken", di conseguenza può essere il controller a mandare il messaggio (e posso anche gestire un minimo di sicurezza... da ragionare meglio su questo aspetto)
==chiediamo  natali per conferma==


- Driver Distratto?
	- problema del peso ipotetico: dato che comunque ad ogni nuova richiesta cicliamo per vedere se ci sono ticket scaduti, tanto vale usare solo il peso effettivo e aggiungere la somma dello spazio promesso nei ticket ancora non riscattati.
	- 




da aggiungere diagramma ciclo di vita ticket