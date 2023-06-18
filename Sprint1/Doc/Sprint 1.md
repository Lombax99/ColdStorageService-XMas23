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
- ==Service Area==: Area rettangolare piana all'interno del quale il transport trolley è libero di muoversi. Ha dimensione Lato-Lungo * lato-corto (L * l).
- ==INDOOR port==: Luogo della Service Area in cui un camion si presenta per far scaricare la merce al transport trolley. Si trova nell'angolo in basso a sinistra della Service Area.
- ==ColdRoom Container==: Elemento fisico presente all'interno della Service Area in una posizione fissa (posizione attraverso la quale il robot non può muoversi). In questo elemento il transport trolley è in grado di depositare cibo fino ad un massimo di MAXW kg.
- ==PORT of the ColdRoom==: Lato del ColdRoom Container dal quale è possibile depositare il cibo. Corrisponde al lato rivolto verso il basso della Service Area.
- ==DDR robot==: *Differential Drive Robot*, see [DDR](https://www.youtube.com/watch?v=aE7RQNhwnPQ).
- ==transport trolley==: DDR quadrato di lunghezza RD in grado di compiere le seguenti mosse: 
	- movimento avanti e indietro
	- rotazione sul posto a 360°
	- raccolta e scarica del cibo
   Come primo prototipo utilizzeremo il seguente robot fisico: https://github.com/XANA-Hub/ProgettoTT.git.
	`NOTA: nel caso del robot fisico RD dovrà essere più lungo dell'effettiva lunghezza del robot per permetterne la rotazione a 360°`
- ==HOME location==: Zona della Service Area in cui il robot si troverà all'avvio e in ogni periodo di attesa di nuove richieste. Corrisponde all'angolo in alto a sinistra della Service Area.
- ==food-load==: quantità di cibo di peso (in kg) variabile che il robot dovrà scaricare dal Fridge Truck e mettere in ColdRoom Container.


Domande:
- ==Gestione degli ostacoli?== NO
- Chi segnala l'arrivo del Firdge Truck?

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
- Come comunicano TransportTrolley e ColdRoom?
	Non lo fanno, ci pensa il controller.
- Quando viene aggiornato il peso della ColdRoom (e da chi)?
	Viene aggiornato dal controller quando questo invia il dispatch a TransportTrolley con un altro dispatch.
- ==In caso di fallimento del DDR robot a portare a termine un comando cosa deve succedere? Riusciamo a tornare al peso precedente? Se il controller ha ricevuto 3 richieste, le ha girate tutte al DDR robot e a Cold Room e poi si accorge che (solo o non solo) la prima non è andata a buon fine come correggo il peso di Cold Room?==
- Come fa il Transport Trolley a sapere dov'è e dove deve andare?
	Dividiamo la stanza in una griglia di quadrati di lato RD (lunghezza del DDR robot). Le coordinate del Transport Trolley indicheranno il quadrato in cui si trova. L'origine (0, 0) sarà la posizione di home. Coordinate crescenti verso il basso e verso destra.
- Quando viene fatta la mappatura della stanza?
	Appena viene avviato il DDR robot prima di leggere qualsiasi richiesta dal controller.
- ==Coordinate? Come le generiamo? Che unità di misura?==
- Quando controlla il TransportTrolley se ci sono altre richieste?
	Appena scarica, prima di tornare in home, come da requisiti (o se è in home)


