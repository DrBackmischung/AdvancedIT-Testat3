# Testat
bearbeitet von 	
Mathis Neunzig (Matrikelnummer 2240587) | Nathalie Möck (Matrikelnummer 7163124)

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

## Ausgangspunkt

Es existieren zwei Dateien, veg.txt mit Gemüsesorten und fruit.txt mit Obstsorten.

veg.txt

``` java
Mais
Paprika
Salat
Karotte
```

fruit.txt

``` java
Banane
Apfel
Orange
Mandarine
```

In den folgenden Testfällen werden diese Datein geändert. Mit den geänderten Daten wird dann auch im darauffolgenden Testfall gearbeitet.

Um den Monitor der Datei zu testen, schläft jeder Worker für 2 Sekunden, nachdem dieser die startReading() bzw startWriting()-Methode ausgeführt hat.

Die Ausgaben des Clients sind aus zwei Teilen bestehend. Zuerst werden alle Kommantos aufgelistet, die gleichzeitig ausgeführt werden (oder im Falle einer manuellen Eingabe manuell vom User eingegeben worden sind). Darunter befinden sich die Antworten des Servers.

## Beispiel 1: Mehrere Lese-Zugriffe auf eine Datei

Das Lesen aus einer Datei soll parallel möglich sein.

### Ausgabe Client

``` java
READ veg.txt,1
READ veg.txt,2
READ veg.txt,3
READ veg.txt,4
Paprika
Mais
Karotte
Salat
```

### Ausgabe Server

``` java
Server auf Port 5999 gestartet!
[0] Worker gestartet
[1] Worker gestartet
[2] Worker gestartet
[3] Worker gestartet
[0] Worker startet mit der Bearbeitung einer Request
[1] Worker startet mit der Bearbeitung einer Request
[2] Worker startet mit der Bearbeitung einer Request
[3] Worker startet mit der Bearbeitung einer Request
[3] Worker bearbeitet: READ veg.txt,4
[1] Worker bearbeitet: READ veg.txt,2
[0] Worker bearbeitet: READ veg.txt,1
[2] Worker bearbeitet: READ veg.txt,3
[0] Worker hat die Bearbeitung abgeschlossen
[2] Worker hat die Bearbeitung abgeschlossen
[1] Worker hat die Bearbeitung abgeschlossen
[3] Worker hat die Bearbeitung abgeschlossen
```

### Auswertung

Das parallele Lesen ist möglich, die Antworten des Servers werden gleichzeitig empfangen, da sich alle Worker gleichzeitig im lesenden Zustand befinden dürfen und sich nicht gegenseitig ausschließen.

## Beispiel 2: Mehrere Schreib-Zugriffe auf eine Datei

Es soll möglich sein, dass gleichzeitig ausgeführte Schreibe-Anfragen abgearbeitet werden. Dabei müssen die Worker jeweils aufeinander warten, bis der sich zuvor im Monitor befindlicher Worker endWriting() aufruft und diesen verlässt.

### Ausgabe Client

``` java
WRITE veg.txt,1,Sellerie
WRITE veg.txt,2,Tomate
WRITE veg.txt,3,Kartoffel
WRITE veg.txt,4,Brokkoli
Status 200: WRITE executed (Brokkoli)
Status 200: WRITE executed (Tomate)
Status 200: WRITE executed (Kartoffel)
Status 200: WRITE executed (Sellerie)
```

### Ausgabe Server

``` java
Server auf Port 5999 gestartet!
[0] Worker gestartet
[1] Worker gestartet
[2] Worker gestartet
[3] Worker gestartet
[0] Worker startet mit der Bearbeitung einer Request
[1] Worker startet mit der Bearbeitung einer Request
[2] Worker startet mit der Bearbeitung einer Request
[3] Worker startet mit der Bearbeitung einer Request
[3] Worker bearbeitet: WRITE veg.txt,4,Brokkoli
[2] Worker bearbeitet: WRITE veg.txt,3,Kartoffel
[0] Worker bearbeitet: WRITE veg.txt,1,Sellerie
[1] Worker bearbeitet: WRITE veg.txt,2,Tomate
[3] Worker hat die Bearbeitung abgeschlossen
[1] Worker hat die Bearbeitung abgeschlossen
[2] Worker hat die Bearbeitung abgeschlossen
[0] Worker hat die Bearbeitung abgeschlossen
```

### Auswertung

Das Schreiben funktioniert. Die Nachrichten treffen mit einem zeitlichen Abstand an, weil die Worker sich gegenseitig ausschließen und nur einer gleichzeitig in die Datei schreiben darf. Die Datei sieht wie folgt aus:

``` java
Sellerie
Tomate
Kartoffel
Brokkoli
```

## Beispiel 3: Mehrere Lese- und Schreib-Zugriffe auf eine Datei

Die Schreiberpriorität soll gewährleistet sein. Wenn Lese- und Schreib-Zugriffe parallel abgeschickt werden, sollen die Schreib-Zugriffe priorisiert werden.

### Ausgabe Client

``` java
WRITE veg.txt,2,Ruccula
READ veg.txt,1
WRITE veg.txt,1,Steckrübe
READ veg.txt,1
Status 200: WRITE executed (Ruccula)
Status 200: WRITE executed (Steckrübe)
Steckrübe
Steckrübe
```

### Ausgabe Server

``` java
Server auf Port 5999 gestartet!
[0] Worker gestartet
[1] Worker gestartet
[2] Worker gestartet
[3] Worker gestartet
[0] Worker startet mit der Bearbeitung einer Request
[1] Worker startet mit der Bearbeitung einer Request
[2] Worker startet mit der Bearbeitung einer Request
[3] Worker startet mit der Bearbeitung einer Request
[0] Worker bearbeitet: WRITE veg.txt,2,Ruccula
[2] Worker bearbeitet: WRITE veg.txt,1,Steckrübe
[1] Worker bearbeitet: READ veg.txt,1
[3] Worker bearbeitet: READ veg.txt,1
[0] Worker hat die Bearbeitung abgeschlossen
[2] Worker hat die Bearbeitung abgeschlossen
[3] Worker hat die Bearbeitung abgeschlossen
[1] Worker hat die Bearbeitung abgeschlossen
```

### Auswertung

Die Schreiberpriorität wurde eingehalten. Zuerst werden die Schreib-Zugriffe abgearbeitet, dann die Lese-Zugriffe. Dabei finden die Schreib-Zugriffe wieder zeitversetzt nacheinander statt, die Lese-Zugriffe dann gleichzeitig. Die Datei sieht wie folgt aus:

``` java
Steckrübe
Ruccula
Kartoffel
Brokkoli
```

## Beispiel 4: Mehrere Lese-Zugriffe auf mehrere Dateien

Beispiel 1 soll auf möglich sein, wenn aus unterschiedlichen Dateien gelesen wird.

### Ausgabe Client

``` java
READ veg.txt,1
READ veg.txt,2
READ fruit.txt,3
READ fruit.txt,4
Steckrübe
Ruccula
Orange
Mandarine
```

### Ausgabe Server

``` java
Server auf Port 5999 gestartet!
[0] Worker gestartet
[1] Worker gestartet
[2] Worker gestartet
[3] Worker gestartet
[0] Worker startet mit der Bearbeitung einer Request
[1] Worker startet mit der Bearbeitung einer Request
[2] Worker startet mit der Bearbeitung einer Request
[3] Worker startet mit der Bearbeitung einer Request
[1] Worker bearbeitet: READ veg.txt,2
[3] Worker bearbeitet: READ fruit.txt,4
[0] Worker bearbeitet: READ veg.txt,1
[2] Worker bearbeitet: READ fruit.txt,3
[0] Worker hat die Bearbeitung abgeschlossen
[1] Worker hat die Bearbeitung abgeschlossen
[2] Worker hat die Bearbeitung abgeschlossen
[3] Worker hat die Bearbeitung abgeschlossen
```

### Auswertung

Die selbe Verarbeitung wie in Beispiel 1 wird erziehlt, auch wenn verschiedene Dateien benutzt werden.

## Beispiel 5: Mehrere Schreib-Zugriffe auf mehrere Dateien

Beispiel 2 soll auch möglich sein, wenn verschiedene Dateien und dementsprechend auch verschiedene Monitoren verwendet werden.

### Ausgabe Client

``` java
WRITE fruit.txt,1,Birne
WRITE fruit.txt,2,Orange
WRITE fruit.txt,1,Kaki
WRITE veg.txt,4,Pok Choi
Status 200: WRITE executed (Birne)
Status 200: WRITE executed (Pok Choi)
Status 200: WRITE executed (Kaki)
Status 200: WRITE executed (Orange)
```

### Ausgabe Server

``` java
Server auf Port 5999 gestartet!
[0] Worker gestartet
[1] Worker gestartet
[2] Worker gestartet
[3] Worker gestartet
[0] Worker startet mit der Bearbeitung einer Request
[0] Worker bearbeitet: WRITE fruit.txt,1,Birne
[1] Worker startet mit der Bearbeitung einer Request
[1] Worker bearbeitet: WRITE fruit.txt,2,Orange
[2] Worker startet mit der Bearbeitung einer Request
[2] Worker bearbeitet: WRITE fruit.txt,1,Kaki
[3] Worker startet mit der Bearbeitung einer Request
[3] Worker bearbeitet: WRITE veg.txt,4,Pok Choi
[0] Worker hat die Bearbeitung abgeschlossen
[3] Worker hat die Bearbeitung abgeschlossen
[2] Worker hat die Bearbeitung abgeschlossen
[1] Worker hat die Bearbeitung abgeschlossen
```

### Auswertung

Die Abarbeitung aus Beispiel 2 wird auch hier erziehlt. Dabei fällt auf, dass die ersten beiden Schreib-Zugriffe gleichzeitig passieren und die Antwort vom Server gleichzeitig kommt. Das liegt daran, dass beide Worker gleichzeitig arbeiten, da sie verschiedene Dateien bearbeiten, was parallel erlaubt ist. Die Dateien sehen wie folgt aus:

``` java
Steckrübe
Ruccula
Kartoffel
Pok Choi
```

``` java
Kaki
Orange
Orange
Mandarine
```

## Beispiel 6: Mehrere Lese- und Schreib-Zugriffe auf mehrere Dateien

Beispiel 3 soll auch möglich sein, wenn verschiedene Dateien verwendet werden. Hier muss wieder auf Schreiberpriorität geachtet werden. Diese gilt jedoch nur in der selben Datei, ist also nicht global übergreifend auf alle Dateien gültig.

### Ausgabe Client

``` java
READ fruit.txt,1
WRITE veg.txt,1,Rotkohl
READ veg.txt,1
WRITE veg.txt,2,Rosenkohl
Kaki
Status 200: WRITE executed (Rotkohl)
Status 200: WRITE executed (Rosenkohl)
Rotkohl
```

### Ausgabe Server

``` java
Server auf Port 5999 gestartet!
[0] Worker gestartet
[1] Worker gestartet
[2] Worker gestartet
[3] Worker gestartet
[0] Worker startet mit der Bearbeitung einer Request
[0] Worker bearbeitet: READ fruit.txt,1
[1] Worker startet mit der Bearbeitung einer Request
[1] Worker bearbeitet: WRITE veg.txt,1,Rotkohl
[2] Worker startet mit der Bearbeitung einer Request
[2] Worker bearbeitet: READ veg.txt,1
[3] Worker startet mit der Bearbeitung einer Request
[3] Worker bearbeitet: WRITE veg.txt,2,Rosenkohl
[0] Worker hat die Bearbeitung abgeschlossen
[1] Worker hat die Bearbeitung abgeschlossen
[3] Worker hat die Bearbeitung abgeschlossen
[2] Worker hat die Bearbeitung abgeschlossen
```

### Auswertung

Das Programm funktioniert gleich wie in Beispiel 3. Die Schreiberpriorität wird beachtet und Schreib-Zugriffe werden zuerst ausgeführt, wenn Lese- und Schreib-Zugriffe gleichzeitig bzw. schnell nacheinander in verschiedener Reihenfolge angefordert wurden. Der Worker, der jedoch Lese-Zugriffe in einer anderen Datei hat, die keine Schreib-Zugriffe hat, wird von der Priorisierung der ersten Datei nicht beeinflusst, dort passiert die Priorisieurng unabhängig davon. Deswegen ist die erste Antwort eine Antwort auf einen Lese-Zugriff einer Datei, die nur Lese-Zugriffe hat, der zeitlich mit dem Schreib-Zugriff einer anderen Datei eintrifft. Die Dateien sehen wie folgt aus:

``` java
Rotkohl
Rosenkohl
Kartoffel
Pok Choi
```

``` java
Kaki
Orange
Orange
Mandarine
```

## Beispiel 7: Datei beim Lese-Zugriff nicht gefunden

Wird eine Datei beim Lesen nicht gefunden, soll das dem Client gesagt werden.

### Ausgabe Client

``` java
READ beer.txt,1
Status 404: Not found (File not found)
```

### Ausgabe Server

``` java
Server auf Port 5999 gestartet!
[0] Worker gestartet
[1] Worker gestartet
[2] Worker gestartet
[3] Worker gestartet
[0] Worker startet mit der Bearbeitung einer Request
[0] Worker bearbeitet: READ beer.txt,1
[0] Worker hat die Bearbeitung abgeschlossen
```

### Auswertung

Bei einer Datei, die nicht gefunden wird, wird dem Client das auch zurückgegeben.

## Beispiel 8: Datei beim Schreib-Zugriff nicht gefunden

Wird eine Datei beim Schreiben nicht gefunden, soll das dem Client gesagt werden.

### Ausgabe Client

``` java
WRITE beer.txt,1,Astra Rakete
Status 404: Not found (File not found)
```

### Ausgabe Server

``` java
Server auf Port 5999 gestartet!
[0] Worker gestartet
[1] Worker gestartet
[2] Worker gestartet
[3] Worker gestartet
[0] Worker startet mit der Bearbeitung einer Request
[0] Worker bearbeitet: WRITE beer.txt,1,Astra Rakete
[0] Worker hat die Bearbeitung abgeschlossen
```

### Auswertung

Bei einer Datei, die nicht gefunden wird, wird dem Client das auch zurückgegeben.

## Beispiel 9: Zeile beim Lese-Zugriff nicht gefunden

Wenn eine Zeile beim Lesen nicht gefunden wird, soll das dem Client gesagt werden.

### Ausgabe Client

``` java
READ veg.txt,6
Status 404: Not found (Line 6 was not found)
```

### Ausgabe Server

``` java
Server auf Port 5999 gestartet!
[0] Worker gestartet
[1] Worker gestartet
[2] Worker gestartet
[3] Worker gestartet
[0] Worker startet mit der Bearbeitung einer Request
[0] Worker bearbeitet: READ veg.txt,6
[0] Worker hat die Bearbeitung abgeschlossen
```

### Auswertung

Dem Client wird gesagt, wenn eine zu lesende Zeile nicht gefunden wird.

## Beispiel 10: Zeile beim Schreib-Zugriff nicht gefunden

Wenn eine Zeile beim Schreiben "nicht gefunden" wird, also nicht existiert, soll die Datei bis zu der neu einzufügenden Zeile erweitert werden.

### Ausgabe Client

``` java
WRITE fruit.txt,6,Pampelmuse
Status 200: WRITE executed (Pampelmuse)
```

### Ausgabe Server

``` java
Server auf Port 5999 gestartet!
[0] Worker gestartet
[1] Worker gestartet
[2] Worker gestartet
[3] Worker gestartet
[0] Worker startet mit der Bearbeitung einer Request
[0] Worker bearbeitet: WRITE fruit.txt,6,Pampelmuse
[0] Worker hat die Bearbeitung abgeschlossen
```

### Auswertung

Die neu zu schreibene Zeile wird an richtiger Stelle an die Datei rangehangen. Diese sieht wie folgt aus:

``` java
Kaki
Orange
Orange
Mandarine

Pampelmuse
```

## Beispiel 11: Unvollständiger READ Befehl

Wenn der READ Befehl mit zu wenig Parametern ausgeführt wird, soll das dem Client gesagt werden.

### Ausgabe Client

``` java
READ veg.txt
Status 406: Not Acceptable (READ needs two parameters: <fileName>,<lineNumber>)
```

### Ausgabe Server

``` java
Server auf Port 5999 gestartet!
[0] Worker gestartet
[1] Worker gestartet
[2] Worker gestartet
[3] Worker gestartet
[0] Worker startet mit der Bearbeitung einer Request
[0] Worker hat die Bearbeitung abgeschlossen
```

### Auswertung

Wenn der READ Befehl mit zu wenig Parametern ausgeführt wird, wird das dem Client gesagt.

## Beispiel 12: Unvollständiger WRITE Befehl

Wenn der WRITE Befehl mit zu wenig Parametern ausgeführt wird, soll das dem Client gesagt werden.

### Ausgabe Client

``` java
WRITE fruit.txt,1
WRITE fruit.txt
Status 406: Not Acceptable (WRITE needs three parameters: <fileName>,<lineNumber>,<text>)
Status 406: Not Acceptable (WRITE needs three parameters: <fileName>,<lineNumber>,<text>)
```

### Ausgabe Server

``` java
Server auf Port 5999 gestartet!
[0] Worker gestartet
[1] Worker gestartet
[2] Worker gestartet
[3] Worker gestartet
[0] Worker startet mit der Bearbeitung einer Request
[0] Worker hat die Bearbeitung abgeschlossen
[1] Worker startet mit der Bearbeitung einer Request
[1] Worker hat die Bearbeitung abgeschlossen
```

### Auswertung

Wenn der WRITE Befehl mit zu wenig Parametern ausgeführt wird, wird das dem Client gesagt.

## Beispiel 13: Keine Argumente im Befehl

Wenn keine Argumente mitgegeben werden, soll das dem Client gesagt werden.

### Ausgabe Client

``` java
READ
WRITE
Status 406: Not Acceptable (Wrong Syntax, use 'READ <fileName>,<lineNumber>' or 'WRITE <fileName>,<lineNumber>,<text>')
Status 406: Not Acceptable (Wrong Syntax, use 'READ <fileName>,<lineNumber>' or 'WRITE <fileName>,<lineNumber>,<text>')
```

### Ausgabe Server

``` java
Server auf Port 5999 gestartet!
[0] Worker gestartet
[1] Worker gestartet
[2] Worker gestartet
[3] Worker gestartet
[0] Worker startet mit der Bearbeitung einer Request
[0] Worker hat die Bearbeitung abgeschlossen
[1] Worker startet mit der Bearbeitung einer Request
[1] Worker hat die Bearbeitung abgeschlossen
```

### Auswertung

Dem Client wird gesagt, wenn er keine Parameter mitgibt.

## Beispiel 14: Keine Zahl als Zeilennummer beim Lesen

Wenn beim Lesen keine Zahl als Zeilennummer mitgegeben wird, soll das dem Client gesagt werden.

### Ausgabe Client

``` java
READ veg.txt,Marmelade
Status 406: Not acceptable (LineNumber has to be an integer with a positive value greater then 1)
```

### Ausgabe Server

``` java
Server auf Port 5999 gestartet!
[0] Worker gestartet
[1] Worker gestartet
[2] Worker gestartet
[3] Worker gestartet
[0] Worker startet mit der Bearbeitung einer Request
[0] Worker hat die Bearbeitung abgeschlossen
```

### Auswertung

Wird beim Lesen keine Zahl mitgegeben, wird das dem Client gesagt.

## Beispiel 15: Keine Zahl als Zeilennummer beim Schreiben

Wenn beim Schreiben keine Zahl als Zeilennummer mitgegeben wird, soll das dem Client gesagt werden.

### Ausgabe Client

``` java
WRITE veg.txt,Birnenmilchshake,Kürbis
Status 406: Not acceptable (LineNumber has to be an integer with a positive value greater then 1)
```

### Ausgabe Server

``` java
Server auf Port 5999 gestartet!
[0] Worker gestartet
[1] Worker gestartet
[2] Worker gestartet
[3] Worker gestartet
[0] Worker startet mit der Bearbeitung einer Request
[0] Worker hat die Bearbeitung abgeschlossen
```

### Auswertung

Wird beim Lesen keine Zahl mitgegeben, wird das dem Client gesagt.

## Beispiel 16: Negative Zahl als Zeilennummer beim Lesen

Wenn beim Lesen keine positive Zahl als Zeilennummer mitgegeben wird, soll das dem Client gesagt werden.

### Ausgabe Client

``` java
READ veg.txt,-3
Status 406: Not acceptable (LineNumber has to be a positive number >= 1)
```

### Ausgabe Server

``` java
Server auf Port 5999 gestartet!
[0] Worker gestartet
[1] Worker gestartet
[2] Worker gestartet
[3] Worker gestartet
[0] Worker startet mit der Bearbeitung einer Request
[0] Worker hat die Bearbeitung abgeschlossen
```

### Auswertung

Wird beim Lesen keine positive Zahl mitgegeben, wird das dem Client gesagt.

## Beispiel 17: Negative Zahl als Zeilennummer beim Schreiben

Wenn beim Schreiben keine positive Zahl als Zeilennummer mitgegeben wird, soll das dem Client gesagt werden.

### Ausgabe Client

``` java
WRITE veg.txt,-4,Kürbis
Status 406: Not acceptable (LineNumber has to be a positive number >= 1)
```

### Ausgabe Server

``` java
Server auf Port 5999 gestartet!
[0] Worker gestartet
[1] Worker gestartet
[2] Worker gestartet
[3] Worker gestartet
[0] Worker startet mit der Bearbeitung einer Request
[0] Worker hat die Bearbeitung abgeschlossen
```

### Auswertung

Wird beim Schreiben keine positive Zahl mitgegeben, wird das dem Client gesagt.

## Beispiel 18: Ungültiger Befehl

Es gibt nur READ und WRITE als Befehle. Wenn andere Befehle benutzt werden, soll das dem User gesagt werden.

### Ausgabe Client

``` java
KÜRBISKUCHEN
котенок
Status 406: Not Acceptable (Wrong Syntax, use 'READ <fileName>,<lineNumber>' or 'WRITE <fileName>,<lineNumber>,<text>')
Status 406: Not Acceptable (Wrong Syntax, use 'READ <fileName>,<lineNumber>' or 'WRITE <fileName>,<lineNumber>,<text>')
```

### Ausgabe Server

``` java
Server auf Port 5999 gestartet!
[0] Worker gestartet
[1] Worker gestartet
[2] Worker gestartet
[3] Worker gestartet
[0] Worker startet mit der Bearbeitung einer Request
[0] Worker hat die Bearbeitung abgeschlossen
[1] Worker startet mit der Bearbeitung einer Request
[1] Worker hat die Bearbeitung abgeschlossen
```

### Auswertung

Wird ein ungültiger Befehl genutzt, wird das dem Client gesagt.
