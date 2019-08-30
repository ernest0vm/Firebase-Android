package com.ernestovaldez.keyboardshortcuts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class VoteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        int shortcutId = intent.getIntExtra(MainActivity.SHORTCUT_ID, -1);
        int voteType = intent.getIntExtra(MainActivity.VOTE_TYPE, -1);

        //TODO handle the vote type
        int i = 1 + 1;
    }
}
