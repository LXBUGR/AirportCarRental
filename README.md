# AirportCarRental

Autovermietung am Flughafen

Ein Flughafen besteht aus 2 Terminals (Passagiere kommen an und fliegen ab) und einer Vermietstation (Abholung und Rückgabe von Mietwagen). Alle besitzen eine eigene FIFO-Queue. Ein Bus verkehrt zwischen diesen Punkten, und zwar von der Vermietstation (Start bei Simulationsbeginn) über Terminal 1 und Terminal 2 wieder zurück zur Vermietstation. Er kann 20 Personen mitnehmen. Alle an einem Terminal ankommenden Passagiere wollen zur Vermietstation und verlassen diese dann mit einem Mietwagen. Alle an der Vermietstation ankommenden Passagiere, die einen Mietwagen zurück geben, wollen zu einem der beiden Terminals und fliegen dann ab. Erreicht der Bus eine der Stationen, so steigen zuerst alle Fahrgäste für diese Station aus (FIFO). Anschließend steigen wartende Passagiere ein, maximal bis die Platzkapazität des Buses erreicht ist. Der Bus verweilt mindestens 5 Minuten an einer Station. Findet nach 5 Minunten kein aus- oder einsteigen statt, fährt er sofort ab.

Folgende Fragen sind dazu interessant (etwa über eine Zeitdauer von 80 Stunden): jeweils mittlere und maximale Anzahl in jeder Warteschlange, Wartezeit in jeder Warteschlange, Anzahl der Passagiere im Bus. Weiters auch jeweils minimale, maximale und mittlere Dauer für eine Runde des Buses, Dauer des Aufenthalts an jeder Station, Dauer einer Person im System (gegliedert nach Station der Ankunft im System).

# PS-Termin 22.5.2024 Bericht zu Überlegungen zur Simulationsaufgabe, offene Fragen besprechen (z.B. Klärung von Details), Gedanken zum verwendeten Modellierungsstil (Prozess-/Ereignis-orientiert), Erweiterungen angeben/vorschlagen
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


