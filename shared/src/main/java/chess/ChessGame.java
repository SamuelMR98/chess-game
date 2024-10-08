package chess;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard board;

    public ChessGame() {
        this.teamTurn = TeamColor.WHITE;
        this.board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        var validMoves = new ArrayList<ChessMove>();
        var piece = board.getPiece(startPosition);

        // check if piece is not null
        if (piece != null) {
            var possibleMoves = piece.pieceMoves(board, startPosition);

            // filter out invalid moves and illegal moves
            for (var move : possibleMoves) {
                if (board.isLegalMove(move)) {
                    validMoves.add(move);
                }
            }
        }

        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        var piece = board.getPiece(move.getStartPosition());
        // check if piece is not null
        if (piece != null) {
            // che if is my turn
            if (piece.getTeamColor() == teamTurn) {
                var validMoves = piece.pieceMoves(board, move.getStartPosition());
                if (validMoves.contains(move)) {
                    if (board.isLegalMove(move)) {
                        board.movePiece(move);
                        teamTurn = teamTurn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
                        return;
                    }
                }
            }
        }
        throw new InvalidMoveException(String.format("Invalid move: %s", move));
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        var kingSquare = board.getSquare(teamColor, ChessPiece.PieceType.KING);
        if (kingSquare == null) {
            return false;
        }

        return kingSquare.isAttacked(board);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        var kingSquare = board.getSquare(teamColor, ChessPiece.PieceType.KING);
        if (kingSquare == null) {
            return false;
        }
        if (!kingSquare.isAttacked(board)) {
            return false;
        }

        // Check for valid moves for all pieces
        for (var square : board.chessSquareCollection()) {
            if (square.getPiece().getTeamColor() == teamColor) {
                for (var move : square.pieceMoves(board)) {
                    var newBoard = new ChessBoard(board);
                    newBoard.movePiece(move);
                    var newKingSquare = newBoard.getSquare(teamColor, ChessPiece.PieceType.KING);

                    if (!newKingSquare.isAttacked(newBoard)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        var kingSquare = board.getSquare(teamColor, ChessPiece.PieceType.KING);
        if (kingSquare == null) {
            return false;
        }
        if (kingSquare.isAttacked(board)) {
            return false;
        }

        // Check for valid moves for all pieces
        for (var squares : board.chessSquareCollection()) {
            if (squares.getPiece().getTeamColor() == teamColor) {
                var validMoves = validMoves(squares.getPosition());
                if (!validMoves.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = new ChessBoard(board);
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    public static ChessGame createGame(String serializedGame) {
        return new Gson().fromJson(serializedGame, ChessGame.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        var game = (ChessGame) o;

        if (!Objects.equals(board, game.board)) {
            return false;
        }
        return teamTurn == game.teamTurn;
    }

    @Override
    public int hashCode() {
        int result = board != null ? board.hashCode() : 0;
        result = 31 * result + (teamTurn != null ? teamTurn.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
