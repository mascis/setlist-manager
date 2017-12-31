package setlistmanager.song;

import android.app.Activity;
import android.os.Bundle;

import setlistmanager.screenslide.ScreenSlideActivity;
import setlistmanager.setlist.AddEditSetlistActivity;
import setlistmanager.setlist.SetlistsActivity;
import setlistmanager.util.BaseNavigator;

/**
 * Created by User on 17.12.2017.
 */

public class SongsNavigator {

    private BaseNavigator navigationProvider;

    public SongsNavigator(BaseNavigator navigationProvider) {
        this.navigationProvider = navigationProvider;
    }

    void addSong() {
        navigationProvider.startActivityForResult(AddEditSongActivity.class, AddEditSongActivity.REQUEST_ADD_SONG);
    }

    void editSong(String songId) {
        navigationProvider.startActivityForResultWithExtra(AddEditSongActivity.class, AddEditSongActivity.REQUEST_EDIT_SONG, "id", songId);
    }

    void toSetlists() {
        navigationProvider.startActivityForResult(SetlistsActivity.class, 1);
    }

    void toScreenSlider(Bundle bundle) {
        navigationProvider.startActivityForResultWithBundle(ScreenSlideActivity.class, Activity.RESULT_OK, bundle);
    }
}
