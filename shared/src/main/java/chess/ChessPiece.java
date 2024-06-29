package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return switch (type) {
            case KING -> kingMoves(board, myPosition);
            case QUEEN -> queenMoves(board, myPosition);
            case BISHOP -> bishopMoves(board, myPosition);
            case KNIGHT -> knightMoves(board, myPosition);
            case ROOK -> rookMoves(board, myPosition);
            case PAWN -> pawnMoves(board, myPosition);
        };
    }

    /**
     * Calculates all the positions a king can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        // move up (only if in bounds, not blocked by own piece) (it can move up if there is an enemy piece)
        if (myPosition.getRow() < 8 && (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn())) == null || board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn())).getTeamColor() != pieceColor)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()), null));
        }

        // move down (only if in bounds, not blocked by own piece) (it can move down if there is an enemy piece)
        if (myPosition.getRow() > 1 && (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn())) == null || board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn())).getTeamColor() != pieceColor)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()), null));
        }

        // move left (only if in bounds, not blocked by own piece) (it can move left if there is an enemy piece)
        if (myPosition.getColumn() > 1 && (board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 1)) == null || board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 1)).getTeamColor() != pieceColor)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 1), null));
        }

        // move right (only if in bounds, not blocked by own piece) (it can move right if there is an enemy piece)
        if (myPosition.getColumn() < 8 && (board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1)) == null || board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1)).getTeamColor() != pieceColor)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1), null));
        }

        // move up left (only if in bounds, not blocked by own piece) (it can move up left if there is an enemy piece)
        if (myPosition.getRow() < 8 && myPosition.getColumn() > 1 && (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1)) == null || board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1)).getTeamColor() != pieceColor)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1), null));
        }

        // move up right (only if in bounds, not blocked by own piece) (it can move up right if there is an enemy piece)
        if (myPosition.getRow() < 8 && myPosition.getColumn() < 8 && (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1)) == null || board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1)).getTeamColor() != pieceColor)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1), null));
        }

        // move down left (only if in bounds, not blocked by own piece) (it can move down left if there is an enemy piece)
        if (myPosition.getRow() > 1 && myPosition.getColumn() > 1 && (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1)) == null || board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1)).getTeamColor() != pieceColor)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1), null));
        }

        // move down right (only if in bounds, not blocked by own piece) (it can move down right if there is an enemy piece)
        if (myPosition.getRow() > 1 && myPosition.getColumn() < 8 && (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1)) == null || board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1)).getTeamColor() != pieceColor)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1), null));
        }

        return moves;
    }

    /**
     * Calculates all the positions a queen can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        // move up (move up until it hits a piece or the edge of the board) (add each move to the list) (if it hits an enemy piece, it can move there) (if it hits an ally piece, it cannot move there)
        for (int i = myPosition.getRow() + 1; i <= 8; i++) {
            if (board.getPiece(new ChessPosition(i, myPosition.getColumn())) == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(i, myPosition.getColumn()), null));
            } else if (board.getPiece(new ChessPosition(i, myPosition.getColumn())).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(i, myPosition.getColumn()), null));
                break;
            } else {
                break;
            }
        }

        // move down (move down until it hits a piece or the edge of the board) (add each move to the list) (if it hits an enemy piece, it can move there) (if it hits an ally piece, it cannot move there)
        for (int i = myPosition.getRow() - 1; i >= 1; i--) {
            if (board.getPiece(new ChessPosition(i, myPosition.getColumn())) == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(i, myPosition.getColumn()), null));
            } else if (board.getPiece(new ChessPosition(i, myPosition.getColumn())).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(i, myPosition.getColumn()), null));
                break;
            } else {
                break;
            }
        }

        // move left (move left until it hits a piece or the edge of the board) (add each move to the list) (if it hits an enemy piece, it can move there) (if it hits an ally piece, it cannot move there)
        for (int i = myPosition.getColumn() - 1; i >= 1; i--) {
            if (board.getPiece(new ChessPosition(myPosition.getRow(), i)) == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), i), null));
            } else if (board.getPiece(new ChessPosition(myPosition.getRow(), i)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), i), null));
                break;
            } else {
                break;
            }
        }

        // move right (move right until it hits a piece or the edge of the board) (add each move to the list) (if it hits an enemy piece, it can move there) (if it hits an ally piece, it cannot move there)
        for (int i = myPosition.getColumn() + 1; i <= 8; i++) {
            if (board.getPiece(new ChessPosition(myPosition.getRow(), i)) == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), i), null));
            } else if (board.getPiece(new ChessPosition(myPosition.getRow(), i)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), i), null));
                break;
            } else {
                break;
            }
        }

        // move up left (move up left until it hits a piece or the edge of the board) (add each move to the list) (if it hits an enemy piece, it can move there) (if it hits an ally piece, it cannot move there)
        for (int i = myPosition.getRow() + 1, j = myPosition.getColumn() - 1; i <= 8 && j >= 1; i++, j--) {
            if (board.getPiece(new ChessPosition(i, j)) == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
            } else if (board.getPiece(new ChessPosition(i, j)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
                break;
            } else {
                break;
            }
        }

        // move up right (move up right until it hits a piece or the edge of the board) (add each move to the list) (if it hits an enemy piece, it can move there) (if it hits an ally piece, it cannot move there)
        for (int i = myPosition.getRow() + 1, j = myPosition.getColumn() + 1; i <= 8 && j <= 8; i++, j++) {
            if (board.getPiece(new ChessPosition(i, j)) == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
            } else if (board.getPiece(new ChessPosition(i, j)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
                break;
            } else {
                break;
            }
        }

        // move down left (move down left until it hits a piece or the edge of the board) (add each move to the list) (if it hits an enemy piece, it can move there) (if it hits an ally piece, it cannot move there)
        for (int i = myPosition.getRow() - 1, j = myPosition.getColumn() - 1; i >= 1 && j >= 1; i--, j--) {
            if (board.getPiece(new ChessPosition(i, j)) == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
            } else if (board.getPiece(new ChessPosition(i, j)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
                break;
            } else {
                break;
            }
        }

        // move down right (move down right until it hits a piece or the edge of the board) (add each move to the list) (if it hits an enemy piece, it can move there) (if it hits an ally piece, it cannot move there)
        for (int i = myPosition.getRow() - 1, j = myPosition.getColumn() + 1; i >= 1 && j <= 8; i--, j++) {
            if (board.getPiece(new ChessPosition(i, j)) == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
            } else if (board.getPiece(new ChessPosition(i, j)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
                break;
            } else {
                break;
            }
        }

        return moves;
    }

    /**
     * Calculates all the positions a bishop can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        // move up left (move up left until it hits a piece or the edge of the board) (add each move to the list) (if it hits an enemy piece, it can move there) (if it hits an ally piece, it cannot move there)
        for (int i = myPosition.getRow() + 1, j = myPosition.getColumn() - 1; i <= 8 && j >= 1; i++, j--) {
            if (board.getPiece(new ChessPosition(i, j)) == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
            } else if (board.getPiece(new ChessPosition(i, j)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
                break;
            } else {
                break;
            }
        }

        // move up right (move up right until it hits a piece or the edge of the board) (add each move to the list) (if it hits an enemy piece, it can move there) (if it hits an ally piece, it cannot move there)
        for (int i = myPosition.getRow() + 1, j = myPosition.getColumn() + 1; i <= 8 && j <= 8; i++, j++) {
            if (board.getPiece(new ChessPosition(i, j)) == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
            } else if (board.getPiece(new ChessPosition(i, j)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
                break;
            } else {
                break;
            }
        }

        // move down left (move down left until it hits a piece or the edge of the board) (add each move to the list) (if it hits an enemy piece, it can move there) (if it hits an ally piece, it cannot move there)
        for (int i = myPosition.getRow() - 1, j = myPosition.getColumn() - 1; i >= 1 && j >= 1; i--, j--) {
            if (board.getPiece(new ChessPosition(i, j)) == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
            } else if (board.getPiece(new ChessPosition(i, j)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
                break;
            } else {
                break;
            }
        }

        // move down right (move down right until it hits a piece or the edge of the board) (add each move to the list) (if it hits an enemy piece, it can move there) (if it hits an ally piece, it cannot move there)
        for (int i = myPosition.getRow() - 1, j = myPosition.getColumn() + 1; i >= 1 && j <= 8; i--, j++) {
            if (board.getPiece(new ChessPosition(i, j)) == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
            } else if (board.getPiece(new ChessPosition(i, j)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
                break;
            } else {
                break;
            }
        }

        return moves;
    }

    /**
     * Calculates all the positions a knight can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        // move up left (only if in bounds, not blocked by own piece) (it can move up left if there is an enemy piece)
        if (myPosition.getRow() < 8 && myPosition.getColumn() > 2 && (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 2)) == null || board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 2)).getTeamColor() != pieceColor)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 2), null));
        }

        // move up right (only if in bounds, not blocked by own piece) (it can move up right if there is an enemy piece)
        if (myPosition.getRow() < 8 && myPosition.getColumn() < 7 && (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 2)) == null || board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 2)).getTeamColor() != pieceColor)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 2), null));
        }

        // move down left (only if in bounds, not blocked by own piece) (it can move down left if there is an enemy piece)
        if (myPosition.getRow() > 1 && myPosition.getColumn() > 2 && (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 2)) == null || board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 2)).getTeamColor() != pieceColor)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 2), null));
        }

        // move down right (only if in bounds, not blocked by own piece) (it can move down right if there is an enemy piece)
        if (myPosition.getRow() > 1 && myPosition.getColumn() < 7 && (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 2)) == null || board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 2)).getTeamColor() != pieceColor)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 2), null));
        }

        // move left up (only if in bounds, not blocked by own piece) (it can move left up if there is an enemy piece)
        if (myPosition.getRow() < 7 && myPosition.getColumn() > 1 && (board.getPiece(new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() - 1)) == null || board.getPiece(new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() - 1)).getTeamColor() != pieceColor)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() - 1), null));
        }

        // move left down (only if in bounds, not blocked by own piece) (it can move left down if there is an enemy piece)
        if (myPosition.getRow() > 2 && myPosition.getColumn() > 1 && (board.getPiece(new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() - 1)) == null || board.getPiece(new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() - 1)).getTeamColor() != pieceColor)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() - 1), null));
        }

        // move right up (only if in bounds, not blocked by own piece) (it can move right up if there is an enemy piece)
        if (myPosition.getRow() < 7 && myPosition.getColumn() < 8 && (board.getPiece(new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() + 1)) == null || board.getPiece(new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() + 1)).getTeamColor() != pieceColor)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() + 1), null));
        }

        // move right down (only if in bounds, not blocked by own piece) (it can move right down if there is an enemy piece)
        if (myPosition.getRow() > 2 && myPosition.getColumn() < 8 && (board.getPiece(new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() + 1)) == null || board.getPiece(new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() + 1)).getTeamColor() != pieceColor)) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() + 1), null));
        }

        return moves;
    }

    /**
     * Calculates all the positions a rook can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        // move up (move up until it hits a piece or the edge of the board) (add each move to the list) (if it hits an enemy piece, it can move there) (if it hits an ally piece, it cannot move there)
        for (int i = myPosition.getRow() + 1; i <= 8; i++) {
            if (board.getPiece(new ChessPosition(i, myPosition.getColumn())) == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(i, myPosition.getColumn()), null));
            } else if (board.getPiece(new ChessPosition(i, myPosition.getColumn())).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(i, myPosition.getColumn()), null));
                break;
            } else {
                break;
            }
        }

        // move down (move down until it hits a piece or the edge of the board) (add each move to the list) (if it hits an enemy piece, it can move there) (if it hits an ally piece, it cannot move there)
        for (int i = myPosition.getRow() - 1; i >= 1; i--) {
            if (board.getPiece(new ChessPosition(i, myPosition.getColumn())) == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(i, myPosition.getColumn()), null));
            } else if (board.getPiece(new ChessPosition(i, myPosition.getColumn())).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(i, myPosition.getColumn()), null));
                break;
            } else {
                break;
            }
        }

        // move left (move left until it hits a piece or the edge of the board) (add each move to the list) (if it hits an enemy piece, it can move there) (if it hits an ally piece, it cannot move there)
        for (int i = myPosition.getColumn() - 1; i >= 1; i--) {
            if (board.getPiece(new ChessPosition(myPosition.getRow(), i)) == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), i), null));
            } else if (board.getPiece(new ChessPosition(myPosition.getRow(), i)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), i), null));
                break;
            } else {
                break;
            }
        }

        // move right (move right until it hits a piece or the edge of the board) (add each move to the list) (if it hits an enemy piece, it can move there) (if it hits an ally piece, it cannot move there)
        for (int i = myPosition.getColumn() + 1; i <= 8; i++) {
            if (board.getPiece(new ChessPosition(myPosition.getRow(), i)) == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), i), null));
            } else if (board.getPiece(new ChessPosition(myPosition.getRow(), i)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), i), null));
                break;
            } else {
                break;
            }
        }
        return moves;
    }

    /**
     * Calculates all the positions a pawn can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        // initial white pawn move (move up 2 if it is the first move and there is no piece in front of it (ally or enemy) and there is no piece 2 spaces in front of it (ally or enemy))
        if (pieceColor == ChessGame.TeamColor.WHITE && myPosition.getRow() == 2 && board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn())) == null && board.getPiece(new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn())) == null) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn()), null));
        }

        // initial black pawn move (move down 2 if it is the first move and there is no piece in front of it (ally or enemy) and there is no piece 2 spaces in front of it (ally or enemy))
        if (pieceColor == ChessGame.TeamColor.BLACK && myPosition.getRow() == 7 && board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn())) == null && board.getPiece(new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn())) == null) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn()), null));
        }

        // capture up left (move up left 1 if there is an enemy piece there) (if it hits the edge of the board, it can promote to any piece (add the piece to the promotionPiece parameter))
        if (pieceColor == ChessGame.TeamColor.WHITE && myPosition.getRow() < 8 && myPosition.getColumn() > 1 && board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1)) != null && board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1)).getTeamColor() != pieceColor) {
            if (myPosition.getRow() == 7) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1), ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1), ChessPiece.PieceType.ROOK));
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1), ChessPiece.PieceType.KNIGHT));
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1), ChessPiece.PieceType.BISHOP));
            } else {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1), null));
            }
        }

        // capture up right (move up right 1 if there is an enemy piece there) (if it hits the edge of the board, it can promote to any piece (add the piece to the promotionPiece parameter))
        if (pieceColor == ChessGame.TeamColor.WHITE && myPosition.getRow() < 8 && myPosition.getColumn() < 8 && board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1)) != null && board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1)).getTeamColor() != pieceColor) {
            if (myPosition.getRow() == 7) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1), ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1), ChessPiece.PieceType.ROOK));
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1), ChessPiece.PieceType.KNIGHT));
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1), ChessPiece.PieceType.BISHOP));
            } else {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1), null));
            }
        }

        // capture down left (move down left 1 if there is an enemy piece there) (if it hits the edge of the board, it can promote to any piece (add the piece to the promotionPiece parameter))
        if (pieceColor == ChessGame.TeamColor.BLACK && myPosition.getRow() > 1 && myPosition.getColumn() > 1 && board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1)) != null && board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1)).getTeamColor() != pieceColor) {
            if (myPosition.getRow() == 2) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1), ChessPiece.PieceType.ROOK));
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1), ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1), ChessPiece.PieceType.KNIGHT));
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1), ChessPiece.PieceType.BISHOP));
            } else {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1), null));
            }
        }

        // capture down right (move down right 1 if there is an enemy piece there) (if it hits the edge of the board, it can promote to any piece (add the piece to the promotionPiece parameter))
        if (pieceColor == ChessGame.TeamColor.BLACK && myPosition.getRow() > 1 && myPosition.getColumn() < 8 && board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1)) != null && board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1)).getTeamColor() != pieceColor) {
            if (myPosition.getRow() == 2) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1), ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1), ChessPiece.PieceType.ROOK));
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1), ChessPiece.PieceType.KNIGHT));
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1), ChessPiece.PieceType.BISHOP));
            } else {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1), null));
            }
        }

        // move up (move up 1 if there is no piece in front of it (ally or enemy)) (if it hits the edge of the board, it can promote to any piece (add the piece to the promotionPiece parameter))
        if (pieceColor == ChessGame.TeamColor.WHITE && myPosition.getRow() < 8 && board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn())) == null) {
            if (myPosition.getRow() == 7) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()), ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()), ChessPiece.PieceType.ROOK));
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()), ChessPiece.PieceType.KNIGHT));
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()), ChessPiece.PieceType.BISHOP));
            } else {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()), null));
            }
        }

        // move down (move down 1 if there is no piece in front of it (ally or enemy)) (if it hits the edge of the board, it can promote to any piece (add the piece to the promotionPiece parameter))
        if (pieceColor == ChessGame.TeamColor.BLACK && myPosition.getRow() > 1 && board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn())) == null) {
            if (myPosition.getRow() == 2) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()), ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()), ChessPiece.PieceType.KNIGHT));
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()), ChessPiece.PieceType.ROOK));
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()), ChessPiece.PieceType.BISHOP));
            } else {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()), null));
            }
        }
        return moves;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessPiece that)) return false;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return switch (type) {
            case KING -> pieceColor == ChessGame.TeamColor.WHITE ? "♔" : "♚";
            case QUEEN -> pieceColor == ChessGame.TeamColor.WHITE ? "♕" : "♛";
            case BISHOP -> pieceColor == ChessGame.TeamColor.WHITE ? "♗" : "♝";
            case KNIGHT -> pieceColor == ChessGame.TeamColor.WHITE ? "♘" : "♞";
            case ROOK -> pieceColor == ChessGame.TeamColor.WHITE ? "♖" : "♜";
            case PAWN -> pieceColor == ChessGame.TeamColor.WHITE ? "♙" : "♟";
        };
    }
}
