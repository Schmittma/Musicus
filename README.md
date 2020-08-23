# Musicus

Dies ist der vollständige Source Code zur Studienarbeit Musicus.

Das System kann mit einer eigenen main versehen werden, sollten bestimmmte Funktionalitäten nicht gewünscht oder fehlerhaft sein.
Das Kernsystem (in *MainThread*) lässt sich theoretisch unabhängig verwenden.

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
