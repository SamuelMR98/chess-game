package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    TeamColor teamTurn;

    public ChessGame() {
        this.teamTurn = TeamColor.WHITE;
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
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece piece = getBoard().getPiece(startPosition);
        // check if piece is null
        if (piece == null) {
            return null;
        }
        // check if piece is on the right team
        if (piece.getTeamColor() != getTeamTurn()) {
            return null;
        }
        // calculate valid moves for each type of piece
        validMoves = switch (piece.getPieceType()) {
            case PAWN -> validPawnMoves(startPosition);
            case ROOK -> validRookMoves(startPosition);
            case KNIGHT -> validKnightMoves(startPosition);
            case BISHOP -> validBishopMoves(startPosition);
            case QUEEN -> validQueenMoves(startPosition);
            case KING -> validKingMoves(startPosition);
        };

        return validMoves;
    }

    private Collection<ChessMove> validKingMoves(ChessPosition startPosition) {
        throw new RuntimeException("Not implemented");
    }

    private Collection<ChessMove> validQueenMoves(ChessPosition startPosition) {
        throw new RuntimeException("Not implemented");
    }

    private Collection<ChessMove> validBishopMoves(ChessPosition startPosition) {
        throw new RuntimeException("Not implemented");
    }

    private Collection<ChessMove> validKnightMoves(ChessPosition startPosition) {
        throw new RuntimeException("Not implemented");
    }

    private Collection<ChessMove> validRookMoves(ChessPosition startPosition) {
        throw new RuntimeException("Not implemented");
    }

    private Collection<ChessMove> validPawnMoves(ChessPosition startPosition) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        throw new RuntimeException("Not implemented");
    }
}
