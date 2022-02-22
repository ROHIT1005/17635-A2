To build OrdersUI client for ws, we need to set CLASSPATH to include json-20211205.jar in order .

Linux/Mac
```
$ cd ws
$ export CLASSPATH=.:$(pwd)/json-20211205.jar
$ javac *.java
$ java OrdersUI
```

Windows:
$ cd ws
$ javac -classpath "json-20211205.jar" *.java
$ java -classpath ".;json-20211205.jar" OrdersUI.java

