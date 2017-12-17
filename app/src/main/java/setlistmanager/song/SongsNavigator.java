package setlistmanager.song;

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

    }

    void toSetlists() {
        navigationProvider.startActivityForResult(SetlistsActivity.class, 1);
    }
}
