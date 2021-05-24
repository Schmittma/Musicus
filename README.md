# Musicus

Dies ist der vollständige Source Code zur Studienarbeit Musicus.

Das System kann mit einer eigenen main versehen werden, sollten bestimmmte Funktionalitäten nicht gewünscht oder fehlerhaft sein.
Das Kernsystem (in *MainThread*) lässt sich theoretisch unabhängig verwenden.

## Changelog

*24.05.2021 / 1.1*:
- Einige Objekte werden nun gefiltert und nicht in der bounding box auswertung ausgegeben (Bsp. G-Schlüssel).
- Das Programm versucht zusammenhängende Noten in einzelne Noten aufzuspalten. Dies ist nur in der Bounding box auswertung ersichtlich.

*20.05.2021*:
- Das Programm gibt die bounding boxen der Objekte in einem JSON-Format unter "object_detection/bounding_boxes/bounding_boxes.json" im jeweiligen score aus.
- Das Programm legt zu der JSON-Datei auch ein Bild zu jedem System, in welchem diese Bounding boxen noch einmal eingezeichnet sind. Dies kann zum  Verständniss oder der Verifikation verwendet werden.

*20.05.2021 & 11.05.2021*:
- Die Meta-Dateien wurden angepasst und die Quelldateien wurden in eine Maven-Konforme struktur gebracht 

## Importieren

Das System verwendet Maven als Abhängigkeitsmanager. Um die Importierung einfacher zu machen, wurden die IntelliJ und Eclipse metadaten (.settings, .idea, .classpath, etc.) mit in dieses Git-Repository hochgeladen.

## Hinweis für die Nutzung mit Eclipse

Sollte das Projekt auf Grund von Maven nicht korrekt laufen, ist es möglich, alle src Dateien in ein neues Projekt zu kopieren und ohne Maven dependencies zu arbeiten. Selbstverständlich ist es dann nicht möglich, Klassen aus diesen Maven dependencies zu verwenden

## Ordnerstruktur *src*
```
|--- start
|      |--- Start.java           Enthält main(), initialisiert die Ordnerstrukutr und verwaltet das Multithreading
|      |--- MainThread.java      Kernsystem der OMR Architektur
|      |--- Globals.java         Diverse globale Variablen

|--- general                     Enthält die in der Studienarbeit beschriebenen Hilfsklassen

|--- utils
|      |--- Util.java            Diverse Hilfsfunktionen (z.B. True 2D-Array Copy)
|      |--- UtilMath.java        Array-Math, sowie Interpolationsalgorithmen
|      |--- ImageConverter.java  Konvertierung zwischen verschiedenen Datenformaten, welche im Programm verwendet werden

|--- interfaces                  Enthält die beschriebenen Schnittstellen-Definitionen

|--- binarization
|--- systemdetection
|--- stafflinedetection          Implementierung der Algorithmen
|--- stafflineremoval
|--- objectdetection
```
