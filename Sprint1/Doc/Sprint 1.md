### Requisiti
A company intends to build a ==ColdStorageService==, composed of a set of elements:

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
- ==DDR robot==:
- ==transport trolley==:
- ==HOME location==:
- ==food-load==:
- ==PORT of the ColdRoom==:

Domande:
- ==Gestione degli ostacoli?== NO
- In questo primo sprint diamo per scontato che l'arrivo di un camion sia magicamente imparato dal controller al momento opportuno (di conseguenza il processo partirà da un segnale generato dal controller)
- DDR robot: il mio robottino fisico

### Analisi del Problema
- Chi manda i segnali al robot?
	Introduciamo in nuovo attore "Controller" che si occupi di mandare i comandi al robot e gestire la logica applicativa. Il controller saprà magicamente quando arriva un camion.
- Quali comandi capisce il robot?
	L'unico comando mandato dal controller è "doYourFuckingJob"
- Come parliamo con il robottino? Cosa ci può dare il committente a proposito?
	Il robot riceve segnali da socket tcp [[Link al protocollo del robot]]
- Chi traduce "doYourFuckingJob" in una serie di comandi comprendibili al Robot?
	Attore TransportTrolley
- Che tipo di segnali mando? Dispatch o Req-Resp?
	Controller manda un segnale di dispatch.
- Come comunicano basic robot e coldroom?
	Non lo fanno, ci pensa il controller.
- Quando viene aggiornato il peso della ColdRoom (e da chi)?
	Viene aggiornato dal controller quando questo invia il dispatch al robot con un altro dispatch.
- Come fa il robottino a sapere dov'è e dove deve andare?
	Dividiamo la stanza in una griglia di quadrati di lato RD (lunghezza del robot). Le coordinate del robot indicheranno il quadrato in cui si trova. L'origine (0, 0) sarà la posizione di home. Coordinate crescenti.
- Quando viene fatta la mappatura della stanza?
	Appena viene avviato il robot prima di leggere qualsiasi richiesta dal controller.
- Coordinate? Come le generiamo? Che unità di misura?
- Quando controlla il robottino se ci sono altre richieste?
	Appena scarica, prima di tornare in home, come da requisiti (o se è in home)

NOTA: cambiare il nome di Robot in Transport Trolley.