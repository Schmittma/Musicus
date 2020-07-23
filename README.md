# Musicus

Dies ist der vollständige Source Code zur Studienarbeit Musicus.

Das System kann mit einer eigenen main versehen werden, sollten bestimmmte Funktionalitäten nicht gewünscht oder fehlerhaft sein.
Das Kernsystem (in *MainThread*) lässt sich theoretisch unabhängig verwenden.

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
