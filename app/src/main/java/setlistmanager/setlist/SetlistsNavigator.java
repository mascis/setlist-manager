package setlistmanager.setlist;

import android.app.Activity;

import java.util.Map;

import setlistmanager.song.SongsActivity;
import setlistmanager.util.BaseNavigator;

/**
 * Created by User on 15.12.2017.
 */

public class SetlistsNavigator {

    private final BaseNavigator navigationProvider;

    public SetlistsNavigator( BaseNavigator navigationProvider ) {
        this.navigationProvider = navigationProvider;
    }

    void addSetlist() {
        navigationProvider.startActivityForResult(AddEditSetlistActivity.class, AddEditSetlistActivity.REQUEST_ADD_SETLLIST);
    }

    void editSetlist(String id) {
        navigationProvider.startActivityForResultWithExtra(AddEditSetlistActivity.class, AddEditSetlistActivity.REQUEST_EDIT_SETLIST, "id", id);
    }

    void toSongs() {
        navigationProvider.startActivityForResult(SongsActivity.class, 1);
    }

    void toSetlistSongs(Map<String, String> extras) {
        navigationProvider.startActivityForResultWithExtrasBundle(SetlistSongsActivity.class, Activity.RESULT_OK, extras);
    }

}
