import chess.*;
import server.*;

public class Main {
    public static void main(String[] args) {
        //var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        //System.out.println("♕ 240 Chess Server: " + piece);
        System.out.println("♕ 240 Chess Server");
        var port = new Server().run(8080);
        System.out.println("♕ 240 Chess Server running on port: " + port);
    }
}