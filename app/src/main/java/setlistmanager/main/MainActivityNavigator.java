package setlistmanager.main;

import android.app.Activity;

import java.util.Map;

import setlistmanager.setlist.AddEditSetlistActivity;
import setlistmanager.setlist.SetlistSongsActivity;
import setlistmanager.song.AddEditSongActivity;
import setlistmanager.song.SongsActivity;
import setlistmanager.util.BaseNavigator;

/**
 * Created by User on 15.12.2017.
 */

public class MainActivityNavigator {

    private final BaseNavigator navigationProvider;

    public MainActivityNavigator(BaseNavigator navigationProvider ) {
        this.navigationProvider = navigationProvider;
    }

    void addSetlist() {
        navigationProvider.startActivityForResult(AddEditSetlistActivity.class, AddEditSetlistActivity.REQUEST_ADD_SETLLIST);
    }

    void addSong() {
        navigationProvider.startActivityForResult(AddEditSongActivity.class, AddEditSetlistActivity.REQUEST_ADD_SETLLIST);
    }

}
