import chess.*;
import ui.ChessClient;

import static util.EscapeSequences.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        //var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        //System.out.println("♕ 240 Chess Client: " + piece);

        try {
            var serverName = args.length > 0 ? args[0] : "localhost:8080";

            ChessClient client = new ChessClient(serverName);
            System.out.println("♕ Welcome to 240 Chess. Type 'Help' to get started. ♕");
            System.out.println("  Connected to server: " + serverName);
            Scanner scanner = new Scanner(System.in);

            var res = "";
            while (!res.equals("quit")) {
                client.printPrompt();
                String input = scanner.nextLine();

                try {
                    res = client.eval(input);
                    System.out.println(RESET_TEXT_COLOR + res);
                } catch (Throwable e) {
                    System.out.println(e.getMessage());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to connect to the server");
        }

    }
}