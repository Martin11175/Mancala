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

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * Game activity handling interation with the user.
 */
// TODO: Replace all boolean player / side representations with enums for readability
public class GameActivity extends Activity {
    GameSpace game; // The game's current state.

    // Default number of beans per pot for a game of Mancala
    public static final int DEFAULT_BEANS = 4;

    /**
     * Setup view on activity creation.
     *
     * @param savedInstanceState Bundle saved from a previous onDestroy.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //TODO: Allow save and return through onClose / onDestroy
        game = new GameSpace(DEFAULT_BEANS);

        // TODO: Randomise starting player
        setTurn(true);
    }

    /**
     * Handler called when window focus changes to or away from this activity.
     *
     * @param hasFocus Whether this boolean has received focus or not.
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // If we got focus, set KitKat style fullscreen immersion for the game.
        if (hasFocus) {
            this.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    /**
     * Handler called when an actionable (non-scoring) pot is clicked.
     *
     * @param pot The view that was interacted with (enables us to identify the pot).
     */
    public void potClick(View pot) {
        GameSpace.GameState state;

        // Identify the pot number clicked and inform the game state of the move
        switch (pot.getId()) {
            case R.id.pot_1_1:
            case R.id.pot_2_1:
                state = game.makeMove(1);
                break;
            case R.id.pot_1_2:
            case R.id.pot_2_2:
                state = game.makeMove(2);
                break;
            case R.id.pot_1_3:
            case R.id.pot_2_3:
                state = game.makeMove(3);
                break;
            case R.id.pot_1_4:
            case R.id.pot_2_4:
                state = game.makeMove(4);
                break;
            case R.id.pot_1_5:
            case R.id.pot_2_5:
                state = game.makeMove(5);
                break;
            case R.id.pot_1_6:
            case R.id.pot_2_6:
                state = game.makeMove(6);
                break;
            default: // Top-level activity, so don't throw an exception, but it should be logged
                Log.w(getResources().getText(R.string.app_name).toString(),
                        "Unauthorised object attempting to call GameActivity.potClick");
                return;
        }

        // Process the response from the underlying game state
        switch (state) {
            // TODO: Fill out how to handle a game ending
            case PLAYER_1_WIN:
                break;
            case PLAYER_2_WIN:
                break;
            case TIE:
                break;
            case PLAYER_1_TURN:
                setTurn(true);
                break;
            case PLAYER_2_TURN:
                setTurn(false);
                break;
            default: // Again, this is top-level, so must be logged
                Log.e(getResources().getText(R.string.app_name).toString(),
                        "Unknown game state entered");
        }
    }

    /**
     * Private helper function for displaying which player's turn it is.
     *
     * @param player True for player 1, false for player 2.
     */
    private void setTurn(boolean player) {
        // Disable / Re-enable the opponent's / player's buttons respectively
        if (player) {
            this.findViewById(R.id.pot_1_1).setClickable(true);
            this.findViewById(R.id.pot_1_2).setClickable(true);
            this.findViewById(R.id.pot_1_3).setClickable(true);
            this.findViewById(R.id.pot_1_4).setClickable(true);
            this.findViewById(R.id.pot_1_5).setClickable(true);
            this.findViewById(R.id.pot_1_6).setClickable(true);
            this.findViewById(R.id.pot_2_1).setClickable(false);
            this.findViewById(R.id.pot_2_2).setClickable(false);
            this.findViewById(R.id.pot_2_3).setClickable(false);
            this.findViewById(R.id.pot_2_4).setClickable(false);
            this.findViewById(R.id.pot_2_5).setClickable(false);
            this.findViewById(R.id.pot_2_6).setClickable(false);
        } else {
            this.findViewById(R.id.pot_1_1).setClickable(false);
            this.findViewById(R.id.pot_1_2).setClickable(false);
            this.findViewById(R.id.pot_1_3).setClickable(false);
            this.findViewById(R.id.pot_1_4).setClickable(false);
            this.findViewById(R.id.pot_1_5).setClickable(false);
            this.findViewById(R.id.pot_1_6).setClickable(false);
            this.findViewById(R.id.pot_2_1).setClickable(true);
            this.findViewById(R.id.pot_2_2).setClickable(true);
            this.findViewById(R.id.pot_2_3).setClickable(true);
            this.findViewById(R.id.pot_2_4).setClickable(true);
            this.findViewById(R.id.pot_2_5).setClickable(true);
            this.findViewById(R.id.pot_2_6).setClickable(true);
        }
    }

    /**
     * Update the UI to represent the new game state.
     */
    private void updateBoard() {
        // TODO: Tie together underlying game state and UI
    }
}
