package setlistmanager.setlist;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import setlistmanager.data.Song;
import setlistmanager.data.source.local.LocalDataSource;

/**
 * Created by User on 17.12.2017.
 */

public class SetlistSongsViewModel extends ViewModel {

    private static final String TAG = SetlistSongsViewModel.class.getSimpleName();

    private final LocalDataSource localDataSource;

    private final SetlistSongsNavigator setlistSongsNavigator;

    public SetlistSongsViewModel(LocalDataSource localDataSource, SetlistSongsNavigator setlistSongsNavigator) {
        this.localDataSource = localDataSource;
        this.setlistSongsNavigator = setlistSongsNavigator;

    }

    public SetlistSongsNavigator getSetlistSongsNavigator() {
        return setlistSongsNavigator;
    }

    public Flowable<List<String>> getSetlistSongs(@NonNull final String setlistId) {

        return localDataSource.getSetlistSongs(setlistId);

    }

    public Flowable<List<Song>> getSetlistSongsById(@NonNull List<String> songIds) {

        return localDataSource.getSongsById(songIds);

    }

    public Completable deleteSongFromSetlist(@NonNull final String songId, @NonNull final String setlistId) {

        /*
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {



            }
        });
        */

        return null;
    }
}
