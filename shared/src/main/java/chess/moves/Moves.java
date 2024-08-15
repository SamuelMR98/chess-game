package chess.moves;

import chess.ChessPiece;

import java.util.HashMap;

import static chess.ChessPiece.PieceType.*;

public class Moves {
    static private final HashMap<ChessPiece.PieceType, Movement> MOVES = new HashMap<>();

    static {
        MOVES.put(BISHOP, new BishopMoves());
        MOVES.put(KING, new KingMoves());
        MOVES.put(KNIGHT, new KnightMoves());
        MOVES.put(QUEEN, new QueenMoves());
        MOVES.put(ROOK, new RookMoves());
        MOVES.put(PAWN, new PawnMoves());
    }

    public static Movement getMovement(ChessPiece.PieceType pieceType) {
        return MOVES.get(pieceType);
    }
}
