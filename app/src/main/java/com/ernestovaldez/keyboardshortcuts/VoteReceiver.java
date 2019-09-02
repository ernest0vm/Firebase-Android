package com.ernestovaldez.keyboardshortcuts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ernestovaldez.keyboardshortcuts.DTO.Shortcut;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

public class VoteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        int shortcutId = intent.getIntExtra(MainActivity.SHORTCUT_ID, -1);
        int voteType = intent.getIntExtra(MainActivity.VOTE_TYPE, -1);
        Shortcut shortcut = (Shortcut) intent.getSerializableExtra(MainActivity.SHORTCUT);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference().child("root");

        if (voteType == MainActivity.VOTE_UP){
            int newVote = shortcut.getUpVote() + 1;
            reference.child(shortcut.getKey()).child("upVote").setValue(newVote);
        }else if (voteType == MainActivity.VOTE_DOWN){
            int newVote = shortcut.getDownVote() + 1;
            reference.child(shortcut.getKey()).child("downVote").setValue(newVote);
        }
    }
}
