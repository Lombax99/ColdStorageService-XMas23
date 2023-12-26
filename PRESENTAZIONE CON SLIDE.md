Scaletta con divisione: 
(buttate giù ogni cosa che vi viene in mente)

Slide1:  (Luca)
Analisi del problema (Sprint 0)
- Motivare divisione Sprint (mm forse solo accennato molto lontanamente)
- Prima architettura
	- spiega divisione contesti e identificazione attori
	- in particolare ColdRoom attore
- ServiceArea come griglia (abbiamo richiesto al committente tutte le varie misure)

Slide2: (Luca)
- Motivazione divisione Sprint1.0 e 1.1
- Da "doJob" a comandi per TransportTrolley

Slide3: (Lisix)
- Presentazione protocollo di richiesta e generazione del ticket e vari reject (mm solo accenno per poi passare ai pesi ipotetici)
- Pesi ipotetici

Slide4:
Sprint 1.0 e 1.1
- Facade (Lisix)
- Spring (Lisix)
- Observer (J)
- Test (J) (secondo me NO)

Slide 5 (J)
Sprint 2 e 3
- Led e Sonar come attori: problema era far in modo che script con basso livello che interagiscono con l'hw riuscissero ad interagire con il resto del sistema. La soluzione era fare gli attori wrapper
- Logica quale stato fa cosa è nell'attore non nello script (scritto nella business logic)
- Sfruttando stessa facade definiamo nuova gui (StatusGui), all'avvio i dati iniziali vengono richiesti poi per il resto del tempo observer 

