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
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.ArrayList;

/**
 * Game activity handling interation with the user.
 */
public class GameActivity extends Activity {
    private GameSpace game; // The game's current state
    // Collections of player's pots (index 0 being the scoring pot)
    private List<View> player1Pots, player2Pots;

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

        // Store arrays of pots for manipulation through iteration
        player1Pots = new ArrayList<View>() {{
            add(findViewById(R.id.pot_1_0));
            add(findViewById(R.id.pot_1_1));
            add(findViewById(R.id.pot_1_2));
            add(findViewById(R.id.pot_1_3));
            add(findViewById(R.id.pot_1_4));
            add(findViewById(R.id.pot_1_5));
            add(findViewById(R.id.pot_1_6));
        }};

        player2Pots = new ArrayList<View>() {{
            add(findViewById(R.id.pot_2_0));
            add(findViewById(R.id.pot_2_1));
            add(findViewById(R.id.pot_2_2));
            add(findViewById(R.id.pot_2_3));
            add(findViewById(R.id.pot_2_4));
            add(findViewById(R.id.pot_2_5));
            add(findViewById(R.id.pot_2_6));
        }};

        //TODO: Allow save and return through onClose / onDestroy
        game = new GameSpace(DEFAULT_BEANS, GameSpace.Player.P1);

        // TODO: Randomise starting player
        updateBoard(GameSpace.Player.P1);
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
        int potNum;

        // Identify the pot number clicked and inform the game state of the move
        if (player1Pots.indexOf(pot) != -1) {
            potNum = player1Pots.indexOf(pot);
        } else if (player2Pots.indexOf(pot) != -1) {
            potNum = player2Pots.indexOf(pot);
        } else {
            // Top-level activity, so don't throw an exception, but it should be logged
            Log.w(getResources().getText(R.string.app_name).toString(),
                    "Unauthorised object attempting to call GameActivity.potClick");
            return;
        }

        // Process the response from the underlying game state
        switch (game.makeMove(potNum)) {
            case PLAYER_1_WIN:
                Toast.makeText(this, "Player 1 Wins!", Toast.LENGTH_LONG).show();
                break;
            case PLAYER_2_WIN:
                Toast.makeText(this, "Player 2 Wins!", Toast.LENGTH_LONG).show();
                break;
            case TIE:
                Toast.makeText(this, "Tie!", Toast.LENGTH_LONG).show();
                break;
            case PLAYER_1_TURN:
                updateBoard(GameSpace.Player.P1);
                break;
            case PLAYER_2_TURN:
                updateBoard(GameSpace.Player.P2);
                break;
            default: // Again, this is top-level, so must be logged
                Log.e(getResources().getText(R.string.app_name).toString(),
                        "Unknown game state entered");
        }
    }

    /**
     * Update the UI to represent the new game state.
     *
     * @param player True for player 1, false for player 2.
     */
    private void updateBoard(GameSpace.Player player) {
        // Disable / Re-enable the opponent's / player's buttons respectively
        boolean isPlayer1 = (player == GameSpace.Player.P1);
        for(int i = 1; i < GameSpace.NUM_POTS; i++) {
            player1Pots.get(i).setEnabled(isPlayer1);
            player2Pots.get(i).setEnabled(!isPlayer1);
        }

        int[] player1Beans = game.getBeans(GameSpace.Player.P1);
        int[] player2Beans = game.getBeans(GameSpace.Player.P2);

        // Iterate through the pots and update their displayed values
        for(int i = 0; i < player1Pots.size(); i++) {
            ((TextView) player1Pots.get(i)).setText(Integer.toString(player1Beans[i]));
            ((TextView) player2Pots.get(i)).setText(Integer.toString(player2Beans[i]));

            // Disable pots with 0 beans in them
            if (player1Beans[i] == 0)
                player1Pots.get(i).setEnabled(false);
            if (player2Beans[i] == 0)
                player2Pots.get(i).setEnabled(false);
        }
    }
}
