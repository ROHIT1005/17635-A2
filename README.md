To build OrdersUI client for ws, we need to set CLASSPATH to include json-20211205.jar in order .

```
$ cd ws
$ export CLASSPATH=.:$(pwd)/json-20211205.jar
$ javac *.java
$ java OrdersUI
```
