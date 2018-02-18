package setlistmanager.setlist;

import android.app.Activity;
import android.os.Bundle;

import java.util.Map;

import setlistmanager.main.MainActivity;
import setlistmanager.screenslide.ScreenSlideActivity;
import setlistmanager.song.AddEditSongActivity;
import setlistmanager.song.SongsActivity;
import setlistmanager.util.BaseNavigator;

/**
 * Created by User on 15.12.2017.
 */

public class SetlistSongsNavigator {

    private final BaseNavigator navigationProvider;

    public SetlistSongsNavigator(BaseNavigator navigationProvider ) {
        this.navigationProvider = navigationProvider;
    }

    void editSong(String id) {
        navigationProvider.startActivityForResultWithExtra(AddEditSongActivity.class, Activity.RESULT_OK, AddEditSongActivity.EXTRA_SONG_ID, id);
    }

    void addSongsToSetlist(Map<String, String> extras) {
        navigationProvider.startActivityForResultWithExtrasBundle(AddSongsToSetlistActivity.class, Activity.RESULT_OK, extras);
    }

    void toScreenSlider(Bundle bundle) {
        navigationProvider.startActivityForResultWithBundle(ScreenSlideActivity.class, Activity.RESULT_OK, bundle);
    }

    void onBackPressed() {

        //navigationProvider.startActivityForResult(MainActivity.class, Activity.RESULT_OK);
        navigationProvider.startActivityForResult(SetlistsActivity.class, Activity.RESULT_OK);
    }

}
