package setlistmanager.setlist;

import android.app.Activity;

import java.util.Map;

import setlistmanager.song.AddEditSongActivity;
import setlistmanager.util.BaseNavigator;

/**
 * Created by User on 17.12.2017.
 */

public class AddSongsToSetlistNavigator {

    private BaseNavigator navigationProvider;

    public AddSongsToSetlistNavigator(BaseNavigator navigationProvider) {
        this.navigationProvider = navigationProvider;
    }

    void onSuccess(Map<String, String> extras) {
        navigationProvider.startActivityForResultWithExtrasBundle(SetlistSongsActivity.class, Activity.RESULT_OK, extras);
    }

    void onCancel() {
        navigationProvider.finishActivity();
    }

    void editSong(String songId) {
        navigationProvider.startActivityForResultWithExtra(AddEditSongActivity.class, AddEditSongActivity.REQUEST_EDIT_SONG, "id", songId);
    }

}
