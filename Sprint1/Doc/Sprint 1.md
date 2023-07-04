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

- ==Service Area==: Area rettangolare piana racchiusa entro quattro pareti. Procedendo dal bordo superiore e muovendoci in senso orario, i nomi delle pareti sono: wallUp, wallRight, wallDown, wallLeft. All'interno del Service Area il transport trolley è libero di muoversi. La stanza ha dimensione Lato-Lungo * lato-corto (L * l).
- ==HOME==: Locazione all'interno della Service Area dove il transport trolley il trova rivolto verso il basso nell'angolo superiore sinistro. La Home è la zona della Service Area in cui il robot si troverà all'avvio e in ogni periodo di attesa di nuove richieste.
- ==INDOOR port==: Locazione all'interno della Service Area in cui un camion si presenta per far caricare la merce al transport trolley. Si trova nell'angolo in basso a sinistra della Service Area.
- ==ColdRoom Container==: Contenitore fisico posizionato all'interno della Service Area in una posizione fissa. In questo elemento il transport trolley è in grado di depositare cibo fino ad un massimo di MAXW kg. ColdRoom Container rappresenta un ostacolo per il transport trolley, ciò vuol dire che non può muoversi nella posizione in cui l'elemento è localizzato. (mi interessa la coordinata per il software)
- ==Porta della ColdRoom==: Lato del ColdRoom Container tramite li quale è possibile depositare il cibo. Corrisponde al lato del container rivolto verso il basso della Service Area. Il transport trolley dovrà posizionarsi davanti alla porta della ColdRoom per poter depositare al suo interno il cibo.
- ==DDR robot==: *Differential Drive Robot*, vedi [DDR](https://www.youtube.com/watch?v=aE7RQNhwnPQ).
- ==Transport trolley==: DDR quadrato di lunghezza RD in grado di compiere i seguenti comandi: 
	- movimento avanti e indietro
	- rotazione sul posto a 360°
	- carica e scarica del cibo
   Come primo prototipo utilizzeremo il seguente robot fisico: https://github.com/XANA-Hub/ProgettoTT.git. Utilizzando il robot fisico si ha che il valore RD sarà più grande dell'effettiva lunghezza del robot per permetterne la rotazione.
- ==Food-load==: quantità di cibo (in kg) che il robot dovrà scaricare dal Fridge Truck e mettere in ColdRoom Container.


### Analisi del Problema
- Chi manda i comandi al Transport Trolley?
	Introduciamo un nuovo attore "Controller" che si occupi di mandare i comandi al Transport Trolley e gestire la logica applicativa. 
	In questo primo sprint supponiamo che il controller sia a conoscenza dell'istante di arrivo dei Fridge Truck (di conseguenza il processo partirà da un segnale generato dal controller).
- Quali comandi (inviati dal Controller) è in grado di comprendere il Transport Trolley?
	L'unico comando mandato dal controller è "move", nell'istante in cui arriva un Fridge Truck.
- Come parliamo con il DDR robot? Cosa ci può dare il committente a proposito?
	Il robot riceve messaggio da socket tcp [Link al protocollo del robot](https://github.com/XANA-Hub/ProgettoTT/blob/main/Sprint%201.md).
- Chi traduce "move" in una serie di comandi comprendibili al DDR robot?
	Introduciamo l'attore TransportTrolley per svolgere questo compito. Si occupa di ricevere i comandi dal controller, interpretare quest'ultimi e comunicare con il robot fisico.
- Che tipo di segnali mando? Dispatch o Req-Resp?
	Controller manda un segnale di dispatch, dal momento in cui il controller non è interessato a ricevere una risposta dal robot.
- Vogliamo sapere se il comando è andato a buon fine? Se si che tipo di segnale è la risposta?
	No, non siamo interessati a ricevere alcun tipo di risposta da parte del Transport Trolley.
- Potrebbe essere utile sapere se il robot interrompe il suo corretto andamento?
	Sì, il Trasport Trolley emette l'evento "robot rotto" dopo un tempo definito x che è nello stato di scaricamento.
- Come comunicano TransportTrolley e ColdRoom?
	Transport Trolley e ColdRoom non comunicano direttamente ma solo tramite Controller.
- Quando viene aggiornato il peso della ColdRoom (e da chi)?
	Viene aggiornato dal Controller quando questo invia il dispatch a TransportTrolley con un altro dispatch.
- Come fa il Transport Trolley a sapere dov'è e dove deve andare?
	Dividiamo la stanza in una griglia di quadrati di lato RD (lunghezza del DDR robot, in realtà per utilizzare il robot fisico sarà necessario prendere in considerazione una griglia di quadrati di lato poco più grande di RD, in modo tale da permettere al robot di girarsi). 
	Le coordinate del Transport Trolley indicheranno il quadrato in cui si trova. L'origine (0, 0) sarà la posizione di Home. Coordinate crescenti verso il basso e verso destra.
- Quando viene fatta la mappatura della stanza?
	La mappatura della stanza viene fatta al momento dell'avviamento del DDR robot, prima di leggere qualsiasi richiesta dal controller.
- Quando controlla il TransportTrolley se ci sono altre richieste?
	Appena scarica, prima di tornare in home, come da requisiti (o se è in home)

