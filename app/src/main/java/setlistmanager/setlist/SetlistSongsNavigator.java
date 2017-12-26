package setlistmanager.setlist;

import android.app.Activity;
import android.os.Bundle;

import java.util.Map;

import setlistmanager.screenslide.ScreenSlideActivity;
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

    void editSong(String id) {}

    void toSetlists() {}

    void toSongs() {}

    void addSongsToSetlist(Map<String, String> extras) {
        navigationProvider.startActivityForResultWithExtrasBundle(AddSongsToSetlistActivity.class, Activity.RESULT_OK, extras);
    }

    void toScreenSlider(Bundle bundle) {
        navigationProvider.startActivityForResultWithBundle(ScreenSlideActivity.class, Activity.RESULT_OK, bundle);
    }

}
