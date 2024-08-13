package chess.moves;

import chess.ChessPiece;

import java.util.HashMap;

import static chess.ChessPiece.PieceType.*;

public class Moves {
    static private final HashMap<ChessPiece.PieceType, Movement> moves = new HashMap<>();

    static {
        moves.put(BISHOP, new BishopMoves());
        moves.put(KING, new KingMoves());
        moves.put(KNIGHT, new KnightMoves());
        moves.put(QUEEN, new QueenMoves());
        moves.put(ROOK, new RookMoves());
        moves.put(PAWN, new PawnMoves());
    }

    public static Movement getMovement(ChessPiece.PieceType pieceType) {
        return moves.get(pieceType);
    }
}
