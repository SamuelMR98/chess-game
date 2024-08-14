# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```

## Sequence Diagram for phase 2
`https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=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`

### Phase 3 listGame diffs

```json
[{gameID=815, gameName='Lonely', whiteUsername='b', blackUsername='null'}, {gameID=816, gameName='GG', whiteUsername='c', blackUsername='a'}, {gameID=814, gameName='I'm numbah one!', whiteUsername='null', blackUsername='a'}, {gameID=817, gameName='All by myself', whiteUsername='c', blackUsername='c'}]
[{gameID=814, gameName='I'm numbah one!', whiteUsername='null', blackUsername='null'}, {gameID=815, gameName='Lonely', whiteUsername='null', blackUsername='null'}, {gameID=816, gameName='GG', whiteUsername='null', blackUsername='null'}, {gameID=817, gameName='All by myself', whiteUsername='null', blackUsername='null'}]

```