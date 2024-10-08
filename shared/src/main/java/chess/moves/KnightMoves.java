package chess.moves;

import java.util.Collection;
import java.util.HashSet;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

public class KnightMoves extends Movement{
    @Override
    public Collection<ChessMove> moves(ChessBoard board, ChessPosition position) {
        var moves = new HashSet<ChessMove>();
        calculateMoves(board, position, 2, 1, moves, false);
        calculateMoves(board, position, 2, -1, moves, false);
        calculateMoves(board, position, 1, 2, moves, false);
        calculateMoves(board, position, -1, 2, moves, false);
        calculateMoves(board, position, -2, -1, moves, false);
        calculateMoves(board, position, -2, 1, moves, false);
        calculateMoves(board, position, -1, -2, moves, false);
        calculateMoves(board, position, 1, -2, moves, false);
        return moves;
    }
}
