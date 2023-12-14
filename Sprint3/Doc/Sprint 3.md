### Goal Sprint 3
ServiceStatusGui e grafiche migliorate [[Sprint 3]]
> [!NOTE]- Descrizione
> Nel terzo sprint ci occuperemo della ServiceStatusGUI e delle interfacce grafiche finali.

Modello dello [[Sprint 2|sprint precedente]].
![[Sprint2/Codice/ColdStorage/coldstorage2arch.png]]
### Requisiti

![[ColdStorageServiceRoomAnnoted.png]]
[[Cold Storage Service - Natali V3#Requisiti|Requisiti]]

### Analisi dei Requisiti
[[Cold Storage Service - Natali V3#Analisi preliminare dei requisiti|requisiti sprint 0]]

### Domande al committente:
Stato del robot? macrostati: movimento, in home, ecc... --> usiamo mappetta e coordinate
altrimenti tutto più preciso con coordinate, per il cliente non è necessario quest'ultimo punto dipende da noi.

### Analisi del Problema
##### Cosa implica lo stato del servizio?
Lo stato del servizio comprende:
- Lo stato e la posizione del TransportTrolley.
- Lo stato della ColdRoom (peso corrente su totale).
- Il numero di richieste negate dall'inizio del servizio.
##### Numero di richieste negate non è un dato che abbiamo
##### Rendo tutti i componenti observable
Devo usare una righettina nuova nel qak: "updateResource [# planner.robotOnMap() #]"
##### Dato iniziale quando carico il componente
Richieste singole tramite facade
##### Facade?
Ci serve? Ne usiamo una nuova o sfruttiamo quella che già abbiamo?
### Test Plan
1) Testiamo che tutto si aggiorni correttamente
2) A seguito di una richiesta rifiutata devono aumentare le richieste rifiutate
3) Nel caso di loadDone fallita la posizione stampata deve essere diversa da home
4) All'avvio della Gui devono essere caricati i parametri attuali del sistema
### Progettazione
##### Facade aggiornato
##### La classe per la parte web
##### Ticket Handler per contare quanti ticket vengono rifiutati
### Deployment
#### Deployment on RaspberryPi 3B/3B+
![[RaspPin.png]]
##### Led
- braccino corto: pin fisico 39 (GND)
- braccino lungo: pin fisico 40 (GPIO21)
##### Sonar
- VCC : pin fisico 4 (+5v)
- GND : pin fisico 6 (GND)
- TRIG: pin fisico 11 (GPIO 17)
- ECHO: pin fisico 13 (GPIO 27)

1) Avvia main alarm
#### Main system deployment
1) Avviare il container itunibovirtualrobot23 su docker
	Viene lanciato l'ambiente virtuale con il robot all'indirizzo http://localhost:8090/
3) In intellij avviare il file MainCtxColdStorageArea.kt del progetto coldStorage
4) Avviare la parte web

# 
----------------

| Lica Uccini              | Luca Lombardi              | Giacomo Romanini              |
| ------------------------ | -------------------------- | ----------------------------- |
| ![[LisaUccini.png\|180]] | ![[LucaLombardi.jpg\|245]] | ![[GiacomoRomanini.jpg\|180]] |
| [github: LisaIU00](https://github.com/LisaIU00)    | [github: Lombax99](https://github.com/Lombax99)             | [github: RedDuality](https://github.com/RedDuality)                              |

