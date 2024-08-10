package chess;

import java.util.Collection;

/**
 * Represents a single square on a chess board
 */

public class ChessSquare {
     private ChessPiece piece;
     private ChessPosition position;

    public ChessSquare(ChessPiece piece, ChessPosition position) {
        this.piece = piece;
        this.position = position;
    }

    public ChessPiece getPiece() {
        return piece;
    }

    public ChessPosition getPosition() {
        return position;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board) {
        return piece.pieceMoves(board, position);
    }

    public boolean isAttacked(ChessBoard board) {
        return board.isAttacked(position, piece.getTeamColor());
    }

    @Override
    public String toString() {
        return String.format("%s:%s:%s", position.toString(), piece.getPieceType(), piece.getTeamColor());
    }

}