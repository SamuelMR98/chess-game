package chess;

import java.util.*;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] board = new ChessPiece[8][8];
    final public ArrayList<ChessMove> history = new ArrayList<>();

    public ChessBoard() {
    }
    public ChessBoard(ChessBoard copy) {
        for (var i = 0; i < 8; i++) {
            System.arraycopy(copy.board[i], 0, board[i], 0, 8);
        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[8][8];
        for (int i = 0; i < 8; i++) {
            board[6][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            board[1][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        }
        board[0][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        board[0][7] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        board[7][0] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        board[7][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);

        board[0][1] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        board[0][6] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        board[7][1] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        board[7][6] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);

        board[0][2] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        board[0][5] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        board[7][2] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        board[7][5] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);

        board[0][3] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        board[7][3] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);

        board[0][4] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        board[7][4] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
    }

    /**
     * Checks if a move is legal
     *
     * @param move the move to check
     * @return true if the move is legal, false otherwise
     */
    public boolean isLegalMove(ChessMove move) {
        var piece = getPiece(move.getStartPosition());

        // Check if piece exists
        if (piece == null) {
            return false;
        }

        // Check if move is castling and if it is valid
        if (isCastling(piece, move)) {
            if (!isValidCastling(piece, move)) {
                return false;
            }
        }

        // Test if this move causes the king to be in check
        var newBoard = new ChessBoard(this);
        newBoard.movePiece(move);
        var king = newBoard.getSquare(piece.getTeamColor(), ChessPiece.PieceType.KING);
        return king == null || !king.isAttacked(newBoard);
    }

    /**
     * Moves a piece on the board
     *
     * @param move the move to make
     */
    public void movePiece(ChessMove move) {
        var piece = getPiece(move.getStartPosition());

        // Handle promotion
        if (move.getPromotionPiece() != null) {
            piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        }
        // Handle castle
        else if ((piece.getPieceType() == ChessPiece.PieceType.KING) &&
                (Math.abs(move.getStartPosition().getColumn() - move.getEndPosition().getColumn()) == 2)) {
            castle(move);
        }
        // Handle en passant
        else if ((piece.getPieceType() == ChessPiece.PieceType.PAWN) &&
                (move.getStartPosition().getColumn() != move.getEndPosition().getColumn()) &&
                (getPiece(move.getEndPosition()) == null)) {
            enPassant(move);
        }

        removePiece(move.getStartPosition());
        addPiece(move.getEndPosition(), piece);

        history.add(move);
    }

    /**
     * En passant move
     * @param move
     *
     */
    public void enPassant(ChessMove move) {
        var passantRow = move.getStartPosition().getRow() == 6 ? 5 : 4;
        var passantPosition = new ChessPosition(passantRow, move.getStartPosition().getColumn());

        removePiece(passantPosition);
    }

    /**
     * Castling move
     * @param move
     */
    public void castle(ChessMove move) {
        var row = move.getEndPosition().getRow();
        var rookMove = new ChessMove(new ChessPosition(row, 1), new ChessPosition(row, 4), null);
        if (move.getEndPosition().getColumn() == 7) {
            rookMove = new ChessMove(new ChessPosition(row, 8), new ChessPosition(row, 6), null);
        }
        movePiece(rookMove);
    }

    /**
     * Removes a piece from the board
     *
     * @param position the position to remove the piece from
     */
    private void removePiece(ChessPosition position) {
        board[position.getRow() - 1][position.getColumn() - 1] = null;
    }

    /**
     * Gets the square with the specified piece type and team color
     *
     * @param teamColor the team color of the piece
     * @param pieceType the type of the piece
     * @return the square with the specified piece type and team color
     */
    public ChessSquare getSquare(ChessGame.TeamColor teamColor, ChessPiece.PieceType pieceType) {
        for (var square : chessSquareCollection()) {
            if (square.getPiece().getTeamColor() == teamColor && square.getPiece().getPieceType() == pieceType) {
                return square;
            }
        }
        return null;
    }

    /**
     * Checks if a piece is being attacked
     * @param position
     * @param teamColor
     * @return
     */
    public boolean isAttacked(ChessPosition position, ChessGame.TeamColor teamColor) {
        return !getAttackingPieces(position, teamColor).isEmpty();
    }

    /** Gets the collection of ChessSquare on the board
     *  @return ChessSquare collection
     */
    public Collection<ChessSquare> chessSquareCollection() {
        var squares = new ArrayList<ChessSquare>();

        for (var i = 0; i < 8; i++) {
            for (var j = 0; j < 8; j++) {
                if (board[i][j] != null) {
                    squares.add(new ChessSquare(board[i][j], new ChessPosition(i + 1, j + 1)));
                }
            }
        }
        return squares;
    }
    /**
     * Gets the attacking pieces
     * @param position
     * @param teamColor
     * @return Collection of attacking pieces
     */
    public Collection<ChessPosition> getAttackingPieces(ChessPosition position, ChessGame.TeamColor teamColor) {
        var attackingPieces = new ArrayList<ChessPosition>();

        for (var probPiece : chessSquareCollection()) {
            if (probPiece.getPiece().getTeamColor() != teamColor) {
                var possibleMoves = probPiece.pieceMoves(this);
                for (var move : possibleMoves) {
                    if (move.getEndPosition().equals(position)) {
                        attackingPieces.add(probPiece.getPosition());
                        break;
                    }
                }
            }
        }
        return attackingPieces;
    }

    public List<ChessMove> getHistory() {
        return history;
    }

    /**
     * Checks if square is empty
     * @param row row of square
     * @param col column of square
     * @return true if square is empty, false otherwise
     */
    public boolean isSquareEmpty(int row, int col) {
        var pieceAt = getPiece(new ChessPosition(row, col));
        return pieceAt == null;
    }

    public ChessMove getLastMove() {
        return history.get(history.size() - 1);
    }

    public boolean isOriginalPosition(ChessPosition pos) {
        for (var bh : getHistory()) {
            if (bh.getStartPosition().equals(pos)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a move is castling
     * @param piece
     * @param move
     * @return true if move is castling, false otherwise
     */
    public boolean isCastling(ChessPiece piece, ChessMove move) {
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            return Math.abs(move.getStartPosition().getColumn() - move.getEndPosition().getColumn()) == 2;
        }
        return false;
    }

    /**
     * Checks if a castling move is valid
     * @param piece
     * @param move
     * @return true if castling move is valid, false otherwise
     */
    public boolean isValidCastling(ChessPiece piece, ChessMove move) {
        if (piece.getPieceType() != ChessPiece.PieceType.KING) {
            return false;
        }

        var color = piece.getTeamColor();
        var teamRow = color == ChessGame.TeamColor.BLACK ? 8 : 1;

        // Check that king is not in check
        if (positionNotAttacked(teamRow, 5, color)) {
            var queenCastle = move.getEndPosition().getColumn() == 3;
            if (queenCastle) {
                return positionNotAttacked(teamRow, 3, color) && positionNotAttacked(teamRow, 4, color);

            } else {
                return positionNotAttacked(teamRow, 6, color) && positionNotAttacked(teamRow, 7, color);
            }
        }
        return false;
    }

    /**
     * Checks if a position is not attacked
     * @param row
     * @param col
     * @param color
     * @return true if position is not attacked, false otherwise
     */
    private boolean positionNotAttacked(int row, int col, ChessGame.TeamColor color) {
        var pos = new ChessPosition(row, col);
        return !isAttacked(pos, color);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard board = (ChessBoard) o;
        return Arrays.deepEquals(this.board, board.board);
    }

    @Override
    public int hashCode() {
        return 31 * Arrays.deepHashCode(board);
    }


    private static final int BLACK = 0;
    private static final int RED = 1;
    private static final int GREEN = 2;
    private static final int YELLOW = 3;
    private static final int BLUE = 4;
    private static final int MAGENTA = 5;
    private static final int CYAN = 6;
    private static final int WHITE = 7;

    private static final String COLOR_RESET = "\u001b[0m";

    private static String color(int foreground, int background) {
        return String.format("\u001b[3%d;4%dm", foreground, background);
    }


    private static String color(int foreground) {
        return String.format("\u001b[1;3%dm", foreground);
    }

    private static final String BORDER = color(BLACK, YELLOW);

    private static final String BOARD_BLACK = color(WHITE, BLACK);
    private static final String BOARD_WHITE = color(BLACK, WHITE);
    private static final String BOARD_HIGHLIGHT = color(GREEN, MAGENTA);

    private static final String BLACK_PIECE = color(RED);
    private static final String WHITE_PIECE = color(GREEN);

    private static final Map<ChessPiece.PieceType, String> pieceMap = Map.of(
            ChessPiece.PieceType.KING, "K",
            ChessPiece.PieceType.QUEEN, "Q",
            ChessPiece.PieceType.BISHOP, "B",
            ChessPiece.PieceType.KNIGHT, "N",
            ChessPiece.PieceType.ROOK, "R",
            ChessPiece.PieceType.PAWN, "P"
    );

    @Override
    public String toString() {
        return toString(ChessGame.TeamColor.WHITE, null);
    }

    public String toString(ChessGame.TeamColor playerColor, Collection<ChessPosition> highlights) {
        var sb = new StringBuilder();
        try {
            var currentSquare = BOARD_WHITE;
            var rows = new int[]{7, 6, 5, 4, 3, 2, 1, 0};
            var columns = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
            var columnsLetters = "    a  b  c  d  e  f  g  h    ";
            if (playerColor == ChessGame.TeamColor.BLACK) {
                columnsLetters = "    h  g  f  e  d  c  b  a    ";
                rows = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
                columns = new int[]{7, 6, 5, 4, 3, 2, 1, 0};
            }
            sb.append(BORDER).append(columnsLetters).append(COLOR_RESET).append("\n");
            for (var i : rows) {
                var row = " " + (i + 1) + " ";
                sb.append(BORDER).append(row).append(COLOR_RESET);
                for (var j : columns) {
                    var squareColor = currentSquare;
                    if (highlights != null && highlights.contains(new ChessPosition(i + 1, j + 1))) {
                        squareColor = BOARD_HIGHLIGHT;
                    }
                    var piece = board[i][j];
                    if (piece != null) {
                        var color = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_PIECE : BLACK_PIECE;
                        var p = pieceMap.get(piece.getPieceType());
                        sb.append(squareColor).append(color).append(" ").append(p).append(" ").append(COLOR_RESET);
                    } else {
                        sb.append(squareColor).append("   ").append(COLOR_RESET);
                    }
                    currentSquare = currentSquare.equals(BOARD_BLACK) ? BOARD_WHITE : BOARD_BLACK;
                }
                sb.append(BORDER).append(row).append(COLOR_RESET);
                sb.append('\n');
                currentSquare = currentSquare.equals(BOARD_BLACK) ? BOARD_WHITE : BOARD_BLACK;
            }
            sb.append(BORDER).append(columnsLetters).append(COLOR_RESET).append("\n");
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return sb.toString();
    }
}
