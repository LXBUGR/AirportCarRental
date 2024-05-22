# AirportCarRental

Autovermietung am Flughafen

Ein Flughafen besteht aus 2 Terminals (Passagiere kommen an und fliegen ab) und einer Vermietstation (Abholung und Rückgabe von Mietwagen). Alle besitzen eine eigene FIFO-Queue. Ein Bus verkehrt zwischen diesen Punkten, und zwar von der Vermietstation (Start bei Simulationsbeginn) über Terminal 1 und Terminal 2 wieder zurück zur Vermietstation. Er kann 20 Personen mitnehmen. Alle an einem Terminal ankommenden Passagiere wollen zur Vermietstation und verlassen diese dann mit einem Mietwagen. Alle an der Vermietstation ankommenden Passagiere, die einen Mietwagen zurück geben, wollen zu einem der beiden Terminals und fliegen dann ab. Erreicht der Bus eine der Stationen, so steigen zuerst alle Fahrgäste für diese Station aus (FIFO). Anschließend steigen wartende Passagiere ein, maximal bis die Platzkapazität des Buses erreicht ist. Der Bus verweilt mindestens 5 Minuten an einer Station. Findet nach 5 Minunten kein aus- oder einsteigen statt, fährt er sofort ab.

Folgende Fragen sind dazu interessant (etwa über eine Zeitdauer von 80 Stunden): jeweils mittlere und maximale Anzahl in jeder Warteschlange, Wartezeit in jeder Warteschlange, Anzahl der Passagiere im Bus. Weiters auch jeweils minimale, maximale und mittlere Dauer für eine Runde des Buses, Dauer des Aufenthalts an jeder Station, Dauer einer Person im System (gegliedert nach Station der Ankunft im System).

# PS-Termin 22.5.2024 
     Bericht zu Überlegungen zur Simulationsaufgabe, offene Fragen besprechen (z.B. Klärung von Details), Gedanken zum verwendeten Modellierungsstil (Prozess-/Ereignis-orientiert), Erweiterungen angeben/vorschlagen
     noch kein fertiges Modell oder Implementierung der Simulationsaufgabe vorgesehen
     keine schriftliche Abgabe.

# Abschlusspräsentationen 19./26.6.2024

    Einteilung der Präsentationstermine erfolgt am 22.5.
    Präsentation besteht aus 
    Vorstellung des Modells und der Fragestellungen
    Umsetzung (Kernpunkte im Code)
    Simulationsergebnisse inkl. Erläuterungen, Interpretationen dazu.

# Anforderungen für ein Simulationsprojekt

  Modellerstellung
    Simulation dazu (Ereignis- oder Prozess-orientiert)
    Ergebnisse präsentieren und begründen/interpretieren
    Software kurz erläutern
    Präsentation ist von allen Teammitgliedern aktiv zu gestalten!
    pro Präsentation ein Team für Fragen verantwortlich!

  Abgabe

    Programmcode (lauffähig, der Aufgabe entsprechend)
    Kurzbeschreibung des Modells (pdf-Datei)
    Ergebnisse inklusive Kurzkommentar (pdf-Datei)


# PS-Termin 22.5.2024

  1. Ziel der Simulation:
  
    Ziel der Simulation ist es,
    den Betrieb einer Autovermietungsstation am Flughafen mit zwei Terminals und einem Bus,
    der zwischen diesen Punkten verkehrt, zu simulieren.
    
    Es sollen verschiedene Metriken wie Wartezeiten,
    Anzahl der Passagiere in den Warteschlangen und im Bus,
    sowie die Dauer einer Busrunde ermittelt werden.

  2. Offene Fragen und Details:

    Ankunftsverteilung der Passagiere:
    Wie ist die Ankunftsrate der Passagiere an den Terminals und der Rückgabestation? 
    Ist diese konstant, oder unterliegt sie einer bestimmten Verteilung (z.B. Poisson-Verteilung)?
    
    Fahrzeit des Busses zwischen den Stationen:
    Gibt es feste Fahrzeiten zwischen den Stationen oder variieren diese zufällig innerhalb eines bestimmten Bereichs?
    
    Verhalten der Passagiere:
    Wie verhalten sich die Passagiere, wenn der Bus voll ist? 
    Warten sie einfach auf den nächsten Bus?
    
    Abfahrtszeiten des Busses:
    Fährt der Bus nach 5 Minuten ab, auch wenn noch Passagiere in der Warteschlange stehen?
    Oder bleibt er länger, bis alle eingestiegen sind?

  3. Gedanken zum Modellierungsstil:

    Prozessorientierte Modellierung:
    In diesem Ansatz modellieren wir den Ablauf aus Sicht der einzelnen Prozesse.
    Hier werden die Aktivitäten der Passagiere und des Busses als Prozesse betrachtet, die über die Zeit ablaufen.
    Vorteile:
    Intuitive Modellierung:
    Der prozessorientierte Ansatz ist oft intuitiver, da er sich auf die Prozesse konzentriert, die im System ablaufen.
    Dies entspricht häufig dem natürlichen Verständnis des Systems.
    Klarheit der Abläufe: 
    Prozesse werden klar definiert und deren Abläufe sind gut nachvollziehbar.
    Nachteile: 
    Komplexität bei vielen Prozessen:
    Bei einer großen Anzahl von Prozessen kann das Modell komplex und schwer zu verwalten werden.
    Eventuelle Performance-Einbußen: Kann bei sehr vielen parallelen Prozessen langsamer sein.
    
    Ereignisorientierte Modellierung:
    In diesem Ansatz modellieren wir den Ablauf durch Ereignisse, die zu bestimmten Zeitpunkten eintreten.
    Dies wäre eine alternative Betrachtungsweise, die den Fokus auf die Zustandsänderungen im System legt.
    Vorteile: Effizienz:
    Der ereignisorientierte Ansatz ist oft effizienter, da er nur Zustandsänderungen (Ereignisse) behandelt und die Zeiten dazwischen überspringt.
    Skalierbarkeit: Kann besser skalieren, wenn es viele unabhängige Ereignisse gibt.
    Nachteile: Weniger Intuitiv:
    Kann weniger intuitiv sein, da der Fokus auf den Ereignissen liegt und nicht auf den durchgehenden Prozessen.
    Komplexität der Ereignisbehandlung: Ereignismanagement kann komplex werden, wenn viele unterschiedliche Ereignistypen auftreten.
    
    Da die Aufgabe eine kontinuierliche Sequenz von Aktionen mit einem klaren Prozessfluss (Passagiere kommen an, steigen in den Bus, fahren zur Mietstation, usw.) erfordert,
    scheint eine prozessorientierte Simulation geeigneter zu sein. 
    
    Intuitive Abbildung der Abläufe: 
    Der Ablauf des Systems (Passagiere ankommen, warten, steigen in den Bus ein, fahren zur nächsten Station, usw.) lässt sich gut als Prozess modellieren.
    Klare Prozessdefinition: 
    Jeder Schritt (z.B. das Einsteigen in den Bus, das Fahren, das Aussteigen) ist ein klar definierter Prozess, was die Modellierung vereinfacht.
    Fokus auf Prozessabläufe: 
    Die wichtigsten Aspekte der Simulation, wie Wartezeiten und die Anzahl der Passagiere, lassen sich gut durch prozessorientierte Ansätze abbilden.
    
  4. Erweiterungen:

    Variierende Passagierströme: 
    Modellierung von Stoßzeiten und ruhigeren Zeiten,
    um die Auslastung des Busses und die Wartezeiten realistischer zu gestalten.
    
    Zusätzliche Ressourcen: 
    Einbindung von mehreren Bussen oder zusätzlichen Terminals.
    
    Unvorhergesehene Ereignisse:
    Simulation von Störungen wie Busausfällen oder Verspätungen bei Passagieren.


Umsetzung der Simulation für die Autovermietung am Flughafen

Hier ist ein Beispiel für die Implementierung in Java unter Verwendung der DESMO-J-Bibliothek, um die Simulation der Autovermietung am Flughafen zu modellieren.
Hauptklassen und deren Funktionen:

    AirportRentalModel.java
        Initialisiert das Modell, die Parameter und die Verteilungen.
        Definiert die Ankunftsraten und die Fahrzeiten des Busses.
        Startet die Simulation und sammelt die Ergebnisse.

    BusProcess.java
        Modelliert den Bus, der zwischen den Stationen fährt.
        Handhabt das Ein- und Aussteigen der Passagiere.

    PassengerProcess.java
        Modelliert die Passagiere, die zu den Terminals oder zur Mietstation wollen.
        Handhabt das Verhalten der Passagiere in den Warteschlangen.

    StationQueue.java
        Modelliert die FIFO-Warteschlangen an den Terminals und der Mietstation.
        
Erläuterung der wichtigsten Klassen:

    PassengerGenerator: Erzeugt Passagiere, die in die Warteschlangen der Terminals oder der Mietstation eintreten.
    BusProcess: Simuliert den Bus, der die Passagiere zwischen den Terminals und der Mietstation transportiert.
    PassengerProcess: Modelliert die Passagiere, die sich in den Warteschlangen befinden und vom Bus abgeholt werden.
    StationQueue: Modelliert die Warteschlangen an den Terminals und der Mietstation.

Ergebnisse und Interpretation:

    Mittlere und maximale Anzahl in jeder Warteschlange:
        Ermittlung der durchschnittlichen und maximalen Anzahl von Passagieren in den Warteschlangen an den Terminals und der Mietstation.

    Wartezeiten in jeder Warteschlange:
        Berechnung der durchschnittlichen Wartezeiten der Passagiere in den Warteschlangen.

    Anzahl der Passagiere im Bus:
        Erfassung der durchschnittlichen und maximalen Anzahl von Passagieren im Bus während der Simulation.

    Minimale, maximale und mittlere Dauer einer Busrunde:
        Analyse der Zeit, die der Bus für eine komplette Runde zwischen den Terminals und der Mietstation benötigt.

    Dauer des Aufenthalts an jeder Station:
        Untersuchung der Verweildauer des Busses an den einzelnen Stationen.

    Dauer einer Person im System:
        Aufgliederung der gesamten Zeit, die eine Person im System verbringt, je nach Ankunftsstation.
