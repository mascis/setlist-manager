package setlistmanager.setlist;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import setlistmanager.data.Setlist;
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

        if ( songIds == null || songIds.isEmpty() ) {
            return null;
        }

        return localDataSource.getSongsById(songIds);

    }

    public Single<Setlist> getSetlist(@NonNull final String setlistId) {

        return localDataSource.getSetlist(setlistId);

    }

    public Completable updateSetlistSongs(@NonNull final Setlist setlist, @Nullable final List<String> songs) {

        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {

                setlist.setSongs(songs);

                localDataSource.updateSetlist(setlist);

            }
        });

    }

}
