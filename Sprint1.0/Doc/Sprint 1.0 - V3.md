### Prodotto dello Sprint 0
È stata individuata un'architettura logica iniziale che definisca le macro-entità del sistema e le loro interazioni, [[Cold Storage Service - Natali V3|link al modello precedente]].
![[Architettura_Sprint0_V2.png]]
### Goal Sprint 1
1) Transport Trolley + ColdStorageService
> [!NOTE]- Descrizione
> Lo scopo del primo sprint è produrre una prima versione funzionante del core dell'applicazione. Questo comprende ColdStorageService con la logica di gestione dei Ticket e il TransportTrolley funzionante.
> A questa parte deve essere affiancata una mock version della ServiceAccessGUI per la fase di testing.

### Requisiti relativi allo sprint corrente

![[ColdStorageServiceRoomAnnoted.png]]
[[Cold Storage Service - Natali V3#Requisiti|Requisiti]]

### Analisi dei Requisiti
[[Cold Storage Service - Natali V3#Analisi preliminare dei requisiti|analisi requisiti sprint 0]]

### Analisi del Problema
##### Responsabilità di ColdStorageService
ColdStorageService è un componente caratterizzato da troppe responsabilità, l'analista ritiene opportuno suddividerlo in 2 attori:
	- Controller: si occupa di gestire il robot ed aggiornare il peso di ColdRoom.
	- TicketHandler: si occupa di gestire il ciclo di vita dei Ticket.

Nello sprint corrente ci occuperemo solo del Controller. La logica di gestione dei ticket è rimandata allo sprint successivo ([[Sprint 1.1 - V3]])

Cerchiamo quindi di realizzare solo la parte corrispondente a Controller, ColdRoom e TransportTrolley della seguente __Architettura logica__: 
![[ArchitetturaLogica_Sprint1.0-V2.png]]

Che si traduce con la seguente;
![[ArchitetturaLogica_Sprint1.0.png]]
##### Messaggio per Transport Trolley
Introduciamo un nuovo messaggio "doJob" di tipo Req/Res inviato dal controller.
```
Request doJob : doJob(KG)
Reply jobdone : jobdone(NO_PARAM)
Reply robotDead : robotDead(NO_PARAM)
```

> [!NOTE]- motivazioni
> Definiamo il seganle come un req/res poichè vogliamo sapere se il servizio richiesto è andato a buon fine oppure se il DDR robot ha avuto problematiche che lo hanno interrotto prima di proseguire con una seconda doJob.
> 
> Limitiamo il controller ad un semplice comando di doJob, non è compito suo sapere quali operazioni deve compiere il robot per portare a termine il lavoro, è compito del robot stesso.

__ATTENZIONE__: la risposta deve essere inviata appena il carico è rilasciato nella ColdRoom e non quando il robot torna alla home per requisiti.
##### Aggiornamento peso ColdRoom
Se il servizio è andato a buon fine e viene restituita una "jobdone" allora il Controller aggiorna il peso della ColdRoom tramite Dispatch.
```
Dispatch updateWeight : updateWeight(PESO)
```
##### Da "doJob" a comandi per TransportTrolley
Dalla [documentazione](https://github.com/anatali/issLab23/blob/main/iss23Material/html/BasicRobot23.html) fornita è chiaro che il basicRobot non possa ricevere il comando "doJob".
Risulta necessario aggiungere un componente intermedio che traduca la "doJob" in una serie di comandi, TransportTrolley si occuperà di quello.
Allo stesso modo è anche evidente la mancanza di un comando per caricare e scaricare i materiali trattati in basicRobot, non sarebbe quindi sufficiente in un caso reale.
##### Posizione nella Service Area
Per definire la posizione del TransportTrolley e permettere il movimento autonomo dividiamo la stanza in una griglia di quadrati di lato RD (lunghezza del DDR robot). 
La [[Cold Storage Service - Natali V3#HOME|Home]] corrisponderà all'origine (0, 0). Useremo coordinate crescenti verso il basso e verso destra.
![[ImmagineGrigliaConCoordinate.png]]
Date le dimensioni dell'area, Service Area sarà divisa in una griglia 4 x 6.
ColdRoom si troverà in posizione (5, 2).

Il [TransportTrolley](https://github.com/anatali/issLab23/blob/main/iss23Material/html/BasicRobot23.html) fornito possiede già il supporto a questo tipo di tecnologia. La mappatura della stanza deve essere fatta a priori e fornita tramite file all'avvio.
##### Peso massimo trasportabile
Dopo discussioni con il committente è stato decretato che il peso da scaricare non sarà mai maggiore del peso trasportabile del robot fisico. 
##### Architettura logica dopo l'analisi del problema
![[Sprint1.1/Doc/coldstorage2arch.png]]

### TestPlan
Durante la face di testing dovranno essere verificati i seguenti casi:
- Verifichiamo che a seguito di richieste ben formate il robot ritorni nella HOME inviando il messaggio corretto (jobdone).
- Verifichiamo che richieste con peso superiore al disponibile vengano scartate correttamente.
- Verifichiamo che in caso il robot subisca dei problemi il sistema si fermi correttamente.

Codice primo test:
[[Sprint1.0/Codice/ColdStorage/test/TestService.java|TestService]]
``` kotlin
@Test  
public void mainUseCaseTest(){  
    //connect to port  
    try{  
        Socket client= new Socket("localhost", 8040);  
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));  
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));  
  
        //send message  
        out.write("msg(doJob,request,test,trasporttrolley,doJob(5),1)\n");  
        out.flush();  
        //wait for response  
        String response= in.readLine();  
        System.out.println(response);  
        assertTrue(response.contains("jobdone"));  
  
  
    }catch(Exception e){  
        fail();  
        System.out.println(e.getStackTrace());  
    }  
}
```

### Progettazione
[[Sprint1.0/Codice/ColdStorage/src/coldstorage.qak|coldstorage]]
##### Sistema di coordinate
Sia RD l'unità di misura
``` kotlin
Home = (0, 0)
Indoor = (0, 4)
ColdRoom = (5, 2)
ColdRoomPORT = (5, 3)      # posizione del robot per poter scaricare
Service Area = {
	height = 5             # asse x (0 -> 4)
	lenght = 7             # asse y (0 -> 6)
}
```
![[ImmagineGrigliaCorrectSize.png]]
##### Definizione messaggi e contesti
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
```
__NOTA:__ in questo momento ColdRoom è definita nello stesso contesto di Controller, in futuro potrebbe non essere così (dipende dall'implementazione fisica della ColdRoom).
##### Controller
``` qak
QActor controller context ctxcoldstoragearea {

	[# var KG = 0 #]
	
	State s0 initial { printCurrentMessage }
	Goto mockRequest
	
	# generiamo una richiesta casuale per il testing
	State mockRequest {
		[# KG = Math.random() #]
		request transporttrolley -m doJob : doJob($KG)
	} Transition endjob whenReply robotDead -> handlerobotdead
						whenReply jobdone -> jobdone
	
	State jobdone{
		forward coldroom -m updateWeight : updateWeight($KG)
	} Transition repeat whenTime 15000 -> mockRequest
	
	State handlerobotdead{
		printCurrentMessage
	}
}
```
##### ColdRoom
``` qak
QActor coldroom context ctxcoldstoragearea {
	[# var PesoEffettivo = 0 #]
	
	State s0 initial { printCurrentMessage }
	Transition update whenMsg updateWeight -> updateWeight
	
	State updateWeight {
		printCurrentMessage
		onMsg ( updateWeight : updateWeight(PESO) ) {
			[# PesoEffettivo += payloadArg(0).toInt() #]
		}
	} Transition update whenMsg updateWeight -> updateWeight
}
```
##### TransportTrolley
```
QActor transporttrolley context ctxcoldstoragearea {
	[# var Peso = 0 #]
	
	State s0 initial{
		forward robotpos -m setrobotstate : setpos(0,0,down)         //set Home pos
	} Transition ready whenMsg robotready -> work
	
	State work{
		println("robot waiting") color green
	} Transition startworking whenRequest doJob -> startjob          //wait for doJob
	
	State startjob{
		onMsg(doJob : doJob( KG )){
			[# Peso = payloadArg(0).toInt()	#]
			println("peso ricevuto: $Peso") color green
		}
	} Goto movingtoarrival
	
	State movingtoarrival{
		request robotpos -m moverobot : moverobot(0,4)                //move to indoor	
	} Transition gofetch whenReply moverobotdone -> movingtocoldroom
	
	State movingtocoldroom{
		request robotpos -m moverobot : moverobot(5,3)                //move to coldroom
	} Transition godrop whenReply moverobotdone -> waitforjob
	
//alla fine di waitforjob mandiamo la risposta "jobdone" e attendiamo per verificare //che non ci siano altre richieste "doJob" da portare avanti prima di tornare alla Home
	State waitforjob {
		replyTo doJob with jobdone : jobdone( 1 )
		println("transporttrolley ! aspetto") color green
	} Transition gofetchagain 
			whenTime 3000 -> goinghome                               //torna alla Home
			whenRequest doJob -> startjob                            //torna a scaricare
	
	State goinghome{
		request robotpos -m moverobot : moverobot(0,0)               // Home pos
		forward robotpos -m setdirection : dir(down)
	}	Goto work
}
```

### Deployment
1) Avviare il container itunibovirtualrobot23 su docker
	Viene lanciato l'ambiente virtuale con il robot all'indirizzo http://localhost:8090/
2) In intellij avviare il file MainCtxbasicrobot.kt del progetto BasicRobot
3) In intellij avviare il file MainCtxColdStorageArea.kt del progetto coldStorage


# 
----------------
[Repo Github](https://github.com/Lombax99/ColdStorageService-XMas23)

| Lica Uccini              | Luca Lombardi              | Giacomo Romanini              |
| ------------------------ | -------------------------- | ----------------------------- |
| ![[LisaUccini.png\|180]] | ![[LucaLombardi.jpg\|245]] | ![[GiacomoRomanini.jpg\|180]] |
| [github: LisaIU00](https://github.com/LisaIU00)    | [github: Lombax99](https://github.com/Lombax99)             | [github: RedDuality](https://github.com/RedDuality)                              |

