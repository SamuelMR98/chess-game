package server;

import chess.ChessGame;
public record JoinRequest (ChessGame.TeamColor color, int gameId){
}
