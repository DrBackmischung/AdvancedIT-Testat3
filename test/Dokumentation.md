# Testat
bearbeitet von 	
Mathis Neunzig (Matrikelnummer 2240587)

## Aufgabe

In dieser Aufgabe soll ein File-Server für Textdateien entwickelt werden.
Vereinfachend gehen wir davon aus, dass dem Server ein festes, bereits existierendes Basisverzeichnis
zugeordnet ist, in dem sich alle verwalteten Dateien befinden und dass er die notwendigen Zugriffsrechte
besitzt. Die Textdateien sind dabei zeilenweise organisiert und beginnen mit Zeilennummer.
Der Server soll als Worker-Pool-Server auf Port 5999 Aufträge in Form von Strings mit ”READ
filename,line no” entgegennehmen, wobei line no eine positive ganze Zahl sein muss. Daraufhin
wird vom Server die Datei filename geöffnet, die Zeile line no ausgelesen und zurückgeschickt.
Außerdem soll der Server auch das Kommando ”WRITE filename,line no,data” verstehen, bei
dem die Zeile line no durch data (kann Kommas und Leerzeichen enthalten) ersetzt werden soll.
Falls sich im Basisverzeichnis des Servers keine solche Datei befindet oder keine entsprechende Zeile
vorhanden ist, soll an den Client eine Fehlermeldung zurückgesendet werden.
Achten Sie darauf, dass nebenläufige Zugriffe konsistente Dateien hinterlassen. Implementieren Sie hierzu
das Zweite Leser-Schreiber-Problem (mit Schreiberpriorität) mit dem Java Monitorkonzept!

Implementieren Sie den Server sowie einen kleinen Test-Client. Verwenden Sie Java und UDP!
Testen Sie die Nebenläufigkeit und das Einhalten der Schreiberpriorität durch geeignete Szenarien und
dokumentieren Sie die Testfälle!

## Disclaimer

Ein paar der folgenden Testfälle sind nicht zwingend erfolderlich, da verschiedene Teilkomponenten schon in anderen Testfällen getestet werden, jedoch sind im Folgenden trotzdem alle der Vollständigkeit halber angegeben.

## Beispiel 1: Mehrere Lese-Zugriffe auf eine Datei

### Ausgabe Client

### Ausgabe Server

### Auswertung

## Beispiel 2: Mehrere Schreib-Zugriffe auf eine Datei

### Ausgabe Client

### Ausgabe Server

### Auswertung

## Beispiel 3: Mehrere Lese- und Schreib-Zugriffe auf eine Datei

### Ausgabe Client

### Ausgabe Server

### Auswertung

## Beispiel 4: Mehrere Lese-Zugriffe auf mehrere Dateien

### Ausgabe Client

### Ausgabe Server

### Auswertung

## Beispiel 5: Mehrere Schreib-Zugriffe auf mehrere Dateien

### Ausgabe Client

### Ausgabe Server

### Auswertung

## Beispiel 6: Mehrere Lese- und Schreib-Zugriffe auf mehrere Dateien

### Ausgabe Client

### Ausgabe Server

### Auswertung

## Beispiel 7: Mehrere Lese-Zugriffe auf eine Datei mit verschiedenen Client-Threads

### Ausgabe Client

### Ausgabe Server

### Auswertung

## Beispiel 8: Mehrere Schreib-Zugriffe auf eine Datei mit verschiedenen Client-Threads

### Ausgabe Client

### Ausgabe Server

### Auswertung

## Beispiel 9: Mehrere Lese- und Schreib-Zugriffe auf eine Datei mit verschiedenen Client-Threads

### Ausgabe Client

### Ausgabe Server

### Auswertung

## Beispiel 10: Mehrere Lese-Zugriffe auf mehrere Dateien mit verschiedenen Client-Threads

### Ausgabe Client

### Ausgabe Server

### Auswertung

## Beispiel 11: Mehrere Schreib-Zugriffe auf mehrere Dateien mit verschiedenen Client-Threads

### Ausgabe Client

### Ausgabe Server

### Auswertung

## Beispiel 12: Mehrere Lese- und Schreib-Zugriffe auf mehrere Dateien mit verschiedenen Client-Threads

### Ausgabe Client

### Ausgabe Server

### Auswertung

## Beispiel 13: Zu große Datenpakete

### Ausgabe Client

### Ausgabe Server

### Auswertung

## Beispiel 14: Datei beim Lese-Zugriff nicht gefunden

### Ausgabe Client

### Ausgabe Server

### Auswertung

## Beispiel 15: Datei beim Schreib-Zugriff nicht gefunden

### Ausgabe Client

### Ausgabe Server

### Auswertung

## Beispiel 16: Zeile beim Lese-Zugriff nicht gefunden

### Ausgabe Client

### Ausgabe Server

### Auswertung

## Beispiel 17: Zeile beim Schreib-Zugriff nicht gefunden

### Ausgabe Client

### Ausgabe Server

### Auswertung

## Beispiel 18: Unvollständiger READ Befehl

### Ausgabe Client

### Ausgabe Server

### Auswertung

## Beispiel 19: Unvollständiger WRITE Befehl

### Ausgabe Client

### Ausgabe Server

### Auswertung

## Beispiel 20: Keine Argumente im Befehl

### Ausgabe Client

### Ausgabe Server

### Auswertung

## Beispiel 21: Keine Zahl als Zeilennummer beim Lesen

### Ausgabe Client

### Ausgabe Server

### Auswertung

## Beispiel 22: Keine Zahl als Zeilennummer beim Schreiben

### Ausgabe Client

### Ausgabe Server

### Auswertung

## Beispiel 23: Negative Zahl als Zeilennummer beim Lesen

### Ausgabe Client

### Ausgabe Server

### Auswertung

## Beispiel 24: Negative Zahl als Zeilennummer beim Schreiben

### Ausgabe Client

### Ausgabe Server

### Auswertung

## Beispiel 25: Ungültiger Befehl

### Ausgabe Client

### Ausgabe Server

### Auswertung

## Beispiel 26: Abbruch durch Client

### Ausgabe Client

### Ausgabe Server

### Auswertung
