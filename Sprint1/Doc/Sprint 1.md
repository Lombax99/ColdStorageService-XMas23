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

All'interno della Service Area possono essere presenti ostacoli da gestire per far sì che il transport trolley possa muoversi liberamente nella stanza?
Da requisiti non è richiesta la presenza di alcun ostacolo da gestire.

### Analisi del Problema
- Chi manda i segnali al Transport Trolley?
	Introduciamo in nuovo attore "Controller" che si occupi di mandare i comandi al Transport Trolley e gestire la logica applicativa. In questo primo sprint diamo per scontato che l'arrivo di un Fridge Truck sia magicamente imparato dal controller al momento opportuno (di conseguenza il processo partirà da un segnale generato dal controller).
- Quali comandi capisce il deve inviare il controller a Transport Trolley?
	L'unico comando mandato dal controller è "doJob".
- Come parliamo con il DDR robot? Cosa ci può dare il committente a proposito?
	Il robot riceve segnali da socket tcp [Link al protocollo del robot](https://github.com/XANA-Hub/ProgettoTT/blob/main/Sprint%201.md).
- Chi traduce "doJob" in una serie di comandi comprendibili al DDR robot?
	Introduciamo l'attore TransportTrolley per svolgere questo compito.
- Che tipo di segnali mando? Dispatch o Req-Resp?
	Controller manda un segnale di dispatch.
- ==Vogliamo sapere se il comando è andato a buon fine? Se si che tipo di segnale è la risposta?==
	NO
- Vogliamo sapere quando il robot si rompe?
	Sarebbe utile... ma NO :( ... maybe? YES :)
- Come ~~cazzo~~ lo facciamo?
	Transport Trolley potrebbe emettere l'evento "robot rotto" dopo x tempo che sono nello stato di scaricamento
- Come comunicano TransportTrolley e ColdRoom?
	Non lo fanno, ci pensa il controller.
- Quando viene aggiornato il peso della ColdRoom (e da chi)?
	Viene aggiornato dal controller quando questo invia il dispatch a TransportTrolley con un altro dispatch.
- ==In caso di fallimento del DDR robot a portare a termine un comando cosa deve succedere? Riusciamo a tornare al peso precedente? Se il controller ha ricevuto 3 richieste, le ha girate tutte al DDR robot e a Cold Room e poi si accorge che (solo o non solo) la prima non è andata a buon fine come correggo il peso di Cold Room?==
	In caso di fallimento il peso effettivo non è stato aggiornato e può essere recuperato.
- Come fa il Transport Trolley a sapere dov'è e dove deve andare?
	Dividiamo la stanza in una griglia di quadrati di lato RD (lunghezza del DDR robot). Le coordinate del Transport Trolley indicheranno il quadrato in cui si trova. L'origine (0, 0) sarà la posizione di home. Coordinate crescenti verso il basso e verso destra.
- Quando viene fatta la mappatura della stanza?
	Appena viene avviato il DDR robot prima di leggere qualsiasi richiesta dal controller.
- ==Coordinate? Come le generiamo? Che unità di misura?==
- Quando controlla il TransportTrolley se ci sono altre richieste?
	Appena scarica, prima di tornare in home, come da requisiti (o se è in home)

NOTA: definiamo due pesi diversi:
1) Un peso ipotetico saputo dal controller che indica il peso ottenuto completate tutte le richieste. (in futuro sarà gestito dal ticket granting service)
2) Il peso effettivo in coldRoom aggiornato solo dopo che il robot ha scaricato fisicamente un carico.
Questi due pesi si troveranno entrambi in coldStorageRoom (se un giorno ci saranno due punti di accesso il peso futuro deve essere in comune)
Peso previsto per lo sprint 1 non esiste perchè Controller fa partire richieste valide. Poi lo aggiungeremo nello sprint 2.

Nota: engager rimosso dal basic robot


==link di amichetti: [https://github.com/iss2022-BCR/WasteService/tree/main](https://github.com/iss2022-BCR/WasteService/tree/main "https://github.com/iss2022-BCR/WasteService/tree/main")==