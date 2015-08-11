package io.mh175.mancala;

import android.app.Activity;
import android.os.Bundle;

public class GameActivity extends Activity {

    /**
     * Called on activity creation
     * @param savedInstanceState Bundle saved from a previous onDestroy
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
    }
}
