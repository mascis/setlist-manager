package setlistmanager.song;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import setlistmanager.data.Setlist;
import setlistmanager.data.Song;
import setlistmanager.data.source.local.LocalDataSource;
import setlistmanager.setlist.AddEditSetlistNavigator;

/**
 * Created by User on 15.12.2017.
 */

public class AddEditSongViewModel extends ViewModel {

    private final LocalDataSource dataSource;

    private final AddEditSongNavigator addEditSongNavigator;

    private boolean isEditMode;

    public AddEditSongViewModel(LocalDataSource dataSource, AddEditSongNavigator addEditSongNavigator, boolean isEditMode) {
        this.dataSource = dataSource;
        this.addEditSongNavigator = addEditSongNavigator;
        this.isEditMode = isEditMode;
    }

    public AddEditSongNavigator getAddEditSongNavigator() {
        return addEditSongNavigator;
    }

    public boolean isEditMode() {
        return isEditMode;
    }

    public void setEditMode(boolean editMode) {
        isEditMode = editMode;
    }

    public Completable saveSong() {

        /*
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {

                Date now = new Date();

                if ( _setlist == null ) {

                    Setlist setlist = new Setlist(setlistName, location, date, now, now, null);
                    dataSource.insertSetlist(setlist);

                } else {

                    _setlist.setName(setlistName);
                    _setlist.setLocation(location);
                    _setlist.setDate(date);
                    _setlist.setModifiedAt(now);

                    dataSource.updateSetlist(_setlist);

                }

            }
        });
        */
        return null;
    }

    public Single<Song> getSongById(@NonNull final String songId) {

        return dataSource.getSongById(songId);

    }

}
