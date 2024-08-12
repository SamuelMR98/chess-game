# My notes

##  sequencediagram.org Phase 2 UML Sequence Diagram

```plantuml
actor Client
participant Server
participant Handler
participant Service
participant DataAccess
database db

entryspacing 0.9

group #gray Clear application #white
Client -> Server: [DELETE] /db
Server -> Handler: ClearDatabase()
Handler -> Service: clearDatabase()
Service -> DataAccess: clearAll()
DataAccess -> db: Remove all Users, Games, AuthTokens
DataAccess --> Service: Success
Service --> Handler: Success
Handler --> Server: {}
Server --> Client: 200\n{}
end

group #navy Register #white
Client -> Server: [POST] /user\n{"username":" ", "password":" ", "email":" "}
Server -> Handler: RegisterUser(RegisterRequest)
Handler -> Service: register(RegisterRequest)
Service -> DataAccess: getUser(username)
DataAccess -> db: Find UserData by username
DataAccess --> Service: null
Service -> DataAccess: createUser(userData)
DataAccess -> db: Add UserData
Service -> DataAccess: createAuth(authData)
DataAccess -> db: Add AuthData
Service --> Handler: RegisterResult
Handler --> Server: {"username" : " ", "authToken" : " "}
Server --> Client: 200\n{"username" : " ", "authToken" : " "}
end

group #orange Login #white
Client -> Server: [POST] /session\n{username, password}
Server -> Handler: LoginUser(LoginRequest)
Handler -> Service: login(LoginRequest)
Service -> DataAccess: getUser(username)
DataAccess -> db: Find UserData by username
DataAccess --> Service: UserData
Service -> DataAccess: validatePassword(username, password)
DataAccess -> db: Check Password
DataAccess --> Service: Valid/Invalid
Service --> Handler: LoginResult
Handler --> Server: {"username" : " ", "authToken" : " "}
Server --> Client: 200\n{"username" : " ", "authToken" : " "}
end

group #green Logout #white
Client -> Server: [DELETE] /session\nauthToken
Server -> Handler: LogoutUser(authToken)
Handler -> Service: logout(authToken)
Service -> DataAccess: invalidateAuth(authToken)
DataAccess -> db: Remove AuthToken
DataAccess --> Service: Success
Service --> Handler: Success
Handler --> Server: {}
Server --> Client: 200\n{}
end

group #red List Games #white
Client -> Server: [GET] /game\nauthToken
Server -> Handler: ListGames(authToken)
Handler -> Service: listGames(authToken)
Service -> DataAccess: getAllGames()
DataAccess -> db: Retrieve all games
DataAccess --> Service: GamesList
Service --> Handler: GamesList
Handler --> Server: {"games": [{"gameID": 1234, "whiteUsername":"", "blackUsername":"", "gameName":""}]}
Server --> Client: 200\n{"games": [{"gameID": 1234, "whiteUsername":"", "blackUsername":"", "gameName":""}]}
end

group #purple Create Game #white
Client -> Server: [POST] /game\nauthToken\n{gameName}
Server -> Handler: CreateGame(CreateGameRequest)
Handler -> Service: createGame(CreateGameRequest)
Service -> DataAccess: createGame(gameName)
DataAccess -> db: Add New Game
DataAccess --> Service: gameID
Service --> Handler: CreateGameResult
Handler --> Server: {"gameID": 1234}
Server --> Client: 200\n{"gameID": 1234}
end

group #yellow Join Game #black
Client -> Server: [PUT] /game\nauthToken\n{playerColor, gameID}
Server -> Handler: JoinGame(JoinGameRequest)
Handler -> Service: joinGame(JoinGameRequest)
Service -> DataAccess: verifyGameExists(gameID)
DataAccess -> db: Find Game by gameID
DataAccess --> Service: GameExists/NotExists
Service -> DataAccess: joinGame(gameID, playerColor)
DataAccess -> db: Update Game with playerColor
DataAccess --> Service: Success/Failure
Service --> Handler: JoinGameResult
Handler --> Server: {}
Server --> Client: 200\n{}
end

```