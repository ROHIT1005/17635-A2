To build OrdersUI client for ws, we need to set CLASSPATH to include json-20211205.jar in order .

```
$ cd ws
$ javac -classpath "json-20211205.jar" *.java
$ java -classpath ".;json-20211205.jar" OrdersUI.java
```
