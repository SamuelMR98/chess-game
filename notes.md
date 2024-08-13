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

## Second iteration
(https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5T9qBACu2GADEXsCW8Nx4MMDY2MggckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2tIAogAy1QAq1QC6MAD0xpjCJTAFrOxclOWIKHiy8noGABQAlJhsnNywfd2i5SDhUOO6+iizXcWivYXbSirq65sAqgZbCgDy+6fKaqrHRjrlAEooALYQNEiCAQMBulA0qxU72e51Ul1GUAA4sBfihpA8ntozq93sZvn8AYZgMCYMjURDDlC+jDXvC8AofGBUujHnMaepcZ8YD9-oDiSCGUyGhAANboDTst65FaUlDlMg+F7qA4iKl9BaDMrkRWw+YDJa9GXUIYwADeAF8VT1pYVsuZygAmJxOAA6aAtZk4nm8fkCaGAVFCP3sSFUmVgATSGSyyHM726JoqAAV7mQGq02j5bm7TQAiLOUf2o3OlXMwXMAGnLuHUyWgHBLZcr5b+wCQCEb5ctCeWhQ1S3xIbDlDBUCmwdD4Z+AEcfGowHN+5R47LylAUEPw+ON5PKDO52G5pDDNSsUq4TB7CgwKOpgWoEWUGyz7DOeUAGJITig27bGA6UJ70fTBJUNQpj3KNAfGBK0jlPeRsQuGAQHXOQUFve9tmfBDzzfGAFA4Dgf0obZYKpE4X1pZDUMyQVUimYBGWZbRsMUXC+jxfDCPwpjSOPMD+kWE0J2HKAflUaCsCXZYjRKco8yAlEUDLcomyrXNGKFUV0BU8suytZcbTCHIwEdZ0c3zW5H10tTy001JhTFNAbP09AOG9Xx-ACaB2CvGBaggEMkkjdJMkwO08nA4pExTNMMwMdQEjQCzFOLUty3UmtVDrKAG3S3Nu2i3tBM1coAqC29yq-fd50XfVDKi1U5RgBBAq-KYqrQGrDzIk8KJw2FyivG9bjvKylNYxCpUKTjP2-Uc-wAmBUrEUCjIg4itm0XqVya8oqGJJAtEyJN9Gy+spgW7QqyynKOCPWVoUopCUNGWimIY3iWJA57po+coCKIujSLW2S1hgAA1Q6ODaABJNADuQdz+KM6Syrarq1EkvUhJkxq5LNSzCyUlzmw0pjHJ0mBVP0nsBIi0yYCdV13SJh8SepvSyfsynnM5ptLTcjzfSCdd0H8wLfGYELo3C2NIqKY0tSqOpGhadoEtUJK3R57S0AM4q0Yl+wpdvXWnLq3HdqocHEaOtC6N5z6tItnb4LYwbL2vOjnYcvXJvYmauTmoGvvkf9QnN9AfoGnF1tXHiXfQKGkbhhHobd-qPaor87eOlAfajtAA9fDiuR5QlE79pyY+zjl4727Vzzd9V6q1BVm+kgSe3kwqlfp+XGeZnNBa9TAvE8wJ1yI2pJ1JJS3hlsKGetxNEUaDN7CUnWKb1g33iN2ewzJNRfd5y3NWt23oYd3enLP-3M5kX6hu9j6i5LuOg4-L9Q6ZRbI532jqDfG4NHZ6xTkdNOecn6SlfmABQwIT6qExLHeu39uTXigKgPkJIt7klrlNbuCdkFHywCjVuuNyikMnDjS+DcCZ5nwWoEslQmFKVhtIVhABGB0ABmAALOpKMmRRzWVLGTHQCBQAijESTCR6lmEADl5G5gKs0Pu1o+gMzMizdh5JWEVH0SgThPD+FCPLCI9C400pqPUlImRcjbFk2Uao9Ro93Ljx9F5bAPgoAxEMHAGihgT4pFCjGEyq9laxXTO0ZhO8k7JVZq42xtMioHzbsMYJJ8phBLeigE+3UFx0INGDFQ+0b7vUSQ-V2KMs5TXgYXIBxdCGB3+jAEOVcAGRGaa00uZTmrgKcpAmG8MYF1Ofmgi8r00I5JSU+PpX92mAxgEolAyR56okWfXAZQ0OHSBbn2TJ8BslKXEtjLuDCTTGNMeUXhgjNGGW0YPXRFlmG3JgPcgRHjhZeUsCgYEEANkACkIBfk2YYAIDiQAijlpE2SMUrixLaPE-0zSLIxBCJQOAEBWpQE7C4-ZJYvmPMNsc0FX4ckUrQIUlAs5aolIaorG25SYB51vtUj+sCX5ewQe-Zpn90HtM6cDbQEcemJO2VKXZVdeYjOgRnCZcC2WUCQAAM0sCfaoAAPScKD3nSEFX9Wav8IXioNVK4hjctW6rDKoNoSiIBgB1Xq7lUz9qqo1SfR1YAGjAHvgao1eE4CpBQDCmA6rLxKQjW8YkqEOChHkDXEBzLwYnwUAddsuhuAOqdRmts0ipFiCVTygAVmCmlSkpgGqrCtINZdyhXGwPnM1yQMipGWjY1av0rUsuah3WEbR3wFr8cWx6qNyUVtpRJBAUk269uuaSgeJlXnuh+UAA)

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
Service -> DataAccess: clearUserDAO()
DataAccess -> db: Remove all Users
Service -> DataAccess: clearGameDAO()
DataAccess -> db: Remove all Games
Service -> DataAccess: clearAuthDAO()
DataAccess -> db: Remove all AuthTokens
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
Client -> Server: [POST] /session\n{"username":" ", "password":" "}
Server -> Handler: LoginUser(LoginRequest)
Handler -> Service: login(LoginRequest)
Service -> DataAccess: getUser(username)
DataAccess -> db: Find UserData by username
DataAccess --> Service: UserData
Service -> Service: validatePassword(UserData, password)
Service -> DataAccess: createAuth(authData)
DataAccess -> db: Add AuthData
DataAccess --> Service: Valid/Invalid
Service --> Handler: LoginResult
Handler --> Server: {"username" : " ", "authToken" : " "}
Server --> Client: 200\n{"username" : " ", "authToken" : " "}
end

group #green Logout #white
Client -> Server: [DELETE] /session\nauthToken
Server -> Handler: LogoutUser(authToken)
Handler -> Service: validateAuthToken(authToken)
Service -> DataAccess: getAuth(authToken)
DataAccess -> db: Find AuthData by authToken
DataAccess --> Service: AuthTokenValid/Invalid
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
Handler -> Service: validateAuthToken(authToken)
Service -> DataAccess: getAuth(authToken)
DataAccess -> db: Find AuthData by authToken
DataAccess --> Service: AuthTokenValid/Invalid
Service -> DataAccess: getAllGames()
DataAccess -> db: Retrieve all games
DataAccess --> Service: GamesList
Service --> Handler: GamesList
Handler --> Server: {"games": [{"gameID": 1234, "whiteUsername":"", "blackUsername":"", "gameName":""}]}
Server --> Client: 200\n{"games": [{"gameID": 1234, "whiteUsername":"", "blackUsername":"", "gameName":""}]}
end

group #purple Create Game #white
Client -> Server: [POST] /game\nauthToken\n{"gameName":" "}
Server -> Handler: CreateGame(CreateGameRequest)
Handler -> Service: validateAuthToken(authToken)
Service -> DataAccess: getAuth(authToken)
DataAccess -> db: Find AuthData by authToken
DataAccess --> Service: AuthTokenValid/Invalid
Service -> DataAccess: createGame(gameName)
DataAccess -> db: Add New Game
DataAccess --> Service: gameID
Service --> Handler: CreateGameResult
Handler --> Server: {"gameID": 1234}
Server --> Client: 200\n{"gameID": 1234}
end

group #yellow Join Game #black
Client -> Server: [PUT] /game\nauthToken\n{"playerColor":" ", "gameID":1234}
Server -> Handler: JoinGame(JoinGameRequest)
Handler -> Service: validateAuthToken(authToken)
Service -> DataAccess: getAuth(authToken)
DataAccess -> db: Find AuthData by authToken
DataAccess --> Service: AuthTokenValid/Invalid
Service -> DataAccess: verifyGameExists(gameID)
DataAccess -> db: Find Game by gameID
DataAccess --> Service: GameExists/NotExists
Service -> DataAccess: verifyGameNotTaken(gameID)
DataAccess -> db: Check if game is already taken
DataAccess --> Service: GameAvailable/NotAvailable
Service -> DataAccess: joinGame(gameID, username)
DataAccess -> db: Update Game with username
DataAccess --> Service: Success/Failure
Service --> Handler: JoinGameResult
Handler --> Server: {}
Server --> Client: 200\n{}
end

```