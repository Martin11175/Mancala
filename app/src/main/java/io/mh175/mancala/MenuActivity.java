package io.mh175.mancala;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import android.content.Intent;

/**
 * A minimally implemented menu activity.
 */
public class MenuActivity extends Activity {

    /**
     * Initialises the menu view on activity creation.
     * @param savedInstanceState Saved state from previous onDestroy call.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    /**
     * Called when the user presses the New Game button to initialise a new GameActivity.
     * @param view The view from which the call was made.
     */
    public void newGame(View view) {
        Intent i = new Intent(this, GameActivity.class);
        startActivity(i);
    }
}
