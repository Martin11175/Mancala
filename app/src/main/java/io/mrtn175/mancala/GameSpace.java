/*
 * Copyright (c) 2015, Martin Higgs (mrtn175)
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose
 * with or without fee is hereby granted, provided that the above copyright notice
 * and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH
 * REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT,
 * OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE,
 * DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS
 * ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package io.mrtn175.mancala;

/**
 * Class containing the current game state and controls actions within it.
 */
public class GameSpace {
    // Current game state
    int[] player1Pots, player2Pots;
    Player playerTurn; // True for player1, false for player2
    int totalBeans; // Total number of beans on the board

    // Enumeration of potential states to finish a move in
    public enum GameState {
        PLAYER_1_TURN, PLAYER_2_TURN, PLAYER_1_WIN, PLAYER_2_WIN, TIE
    }

    // Enumeration of players for readability
    public enum Player {
        P1, P2
    }

    // Number of pots per side (including scoring pots) in a standard game of Mancala
    public static final int NUM_POTS = 7;

    /**
     * Constructor for a new game.
     *
     * @param initBeans The number of starting beans per pot.
     * @param startingPlayer The player to make the first move.
     */
    public GameSpace(int initBeans, Player startingPlayer) {
        player1Pots = new int[NUM_POTS];
        player2Pots = new int[NUM_POTS];
        playerTurn = startingPlayer;

        // SPILL THE BEANS
        for (int i = 1; i < NUM_POTS; i++) {
            player1Pots[i] = initBeans;
            player2Pots[i] = initBeans;
        }

        totalBeans = initBeans * NUM_POTS * 2;
    }

    /**
     * Make the move utilising the beans in the specified pot.
     *
     * @param potNum The pot number from which to take the beans.
     *               (Taken from the current player's pots)
     * @return The state of the game at the end of this move.
     */
    // XXX: Can we shrink these "boardSide" and "playerTurn" checks?
    // TODO: Split into independent chunks
    public GameState makeMove(int potNum) {
        // The side of the board we're currently looking at (true for P1, false for P2)
        Player boardSide = playerTurn;
        int beansInHand;

        // Draw beans from the respective pot
        if (boardSide == Player.P1) {
            beansInHand = player1Pots[potNum];
            player1Pots[potNum] = 0;
        } else {
            beansInHand = player2Pots[potNum];
            player2Pots[potNum] = 0;
        }

        // Place the beans in the following pots
        while (beansInHand > 0) {
            potNum--; // Move towards the player's scoring pot

            // Disallow placing in the opponent's scoring pot
            if (potNum == 0 && boardSide != playerTurn) {
                potNum = NUM_POTS - 1;
                boardSide = nextPlayer(boardSide);
            }
            // Loop to other side after placing in scoring pot
            else if (potNum < 0) {
                potNum = NUM_POTS - 1;
                boardSide = nextPlayer(boardSide);
            }

            if (boardSide == Player.P1)
                player1Pots[potNum]++;
            else
                player2Pots[potNum]++;
            beansInHand--;
        }

        // Check if the final placement was in an empty pot on the player's side
        if (boardSide == playerTurn
                && potNum != 0
                && ((boardSide == Player.P1 && player1Pots[potNum] == 1)
                || player2Pots[potNum] == 1)) {

            /*
             * If so the player takes both the last bean they placed in that pot and
             * all the beans in the pot opposite
             */
            if (playerTurn == Player.P1) {
                player1Pots[0] += player1Pots[potNum];
                player1Pots[0] += player2Pots[NUM_POTS - potNum];
                player1Pots[potNum] = 0;
                player2Pots[NUM_POTS - potNum] = 0;
            } else {
                player2Pots[0] += player1Pots[NUM_POTS - potNum];
                player2Pots[0] += player2Pots[potNum];
                player1Pots[NUM_POTS - potNum] = 0;
                player2Pots[potNum] = 0;
            }

        }

        // Check to see if the opponent can make a move in the next turn
        boolean opponentCanMove = false;
        if (playerTurn == Player.P1) {
            for (int i = 1; i < NUM_POTS; i++) {
                if (player2Pots[i] != 0) {
                    opponentCanMove = true;
                    break;
                }
            }
        } else {
            for (int i = 1; i < NUM_POTS; i++) {
                if (player1Pots[i] != 0) {
                    opponentCanMove = true;
                    break;
                }
            }
        }

        // If not they have been "starved out" so the player collects all of their beans
        if (!opponentCanMove && playerTurn == Player.P1) {
            for (int i = 1; i < NUM_POTS; i++) {
                player1Pots[0] += player1Pots[i];
                player1Pots[i] = 0;
            }
        } else if (!opponentCanMove) {
            for (int i = 1; i < NUM_POTS; i++) {
                player2Pots[0] += player2Pots[i];
                player2Pots[i] = 0;
            }
        }

        // If either player holds more than half of the total beans on the board, they've won
        if (player1Pots[0] > (totalBeans / 2))
            return GameState.PLAYER_1_WIN;
        else if (player2Pots[0] > (totalBeans / 2))
            return GameState.PLAYER_2_WIN;
        else if ((player1Pots[0] == (totalBeans / 2))
                && (player2Pots[0] == (totalBeans / 2)))
            return GameState.TIE;
        else {
            // If a player's move finishes in their scoring pot, they may take again
            if (potNum != 0)
                playerTurn = nextPlayer(playerTurn);
            return playerTurn == Player.P1 ? GameState.PLAYER_1_TURN : GameState.PLAYER_2_TURN;
        }
    }

    /**
     * Retrieve the current distribution of beans over a player's pots.
     *
     * @param player The player for whom to request the pots for (true for P1, false for P2)
     * @return An array of the requested player's pots where index 0 is the scoring pot.
     */
    public int[] getBeans(Player player) {
        return player == Player.P1 ? player1Pots : player2Pots;
    }

    /**
     * Private helper for getting the next player (easier to read than the triple).
     *
     * @param currentPlayer The current player.
     * @return Player 2 if Player 1 was passed, Player 1 otherwise.
     */
    private Player nextPlayer(Player currentPlayer) {
        return currentPlayer == Player.P1 ? Player.P2 : Player.P1;
    }
}
