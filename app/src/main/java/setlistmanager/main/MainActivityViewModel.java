package setlistmanager.main;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.functions.Action;
import setlistmanager.data.Setlist;
import setlistmanager.data.Song;
import setlistmanager.data.source.local.LocalDataSource;
import setlistmanager.song.SongsNavigator;

/**
 * Created by User on 17.12.2017.
 */

public class MainActivityViewModel extends ViewModel {

    private static final String TAG = MainActivityViewModel.class.getSimpleName();

    private final LocalDataSource localDataSource;

    private final MainActivityNavigator mainActivityNavigator;

    public MainActivityViewModel(LocalDataSource localDataSource, MainActivityNavigator mainActivityNavigator) {
        this.localDataSource = localDataSource;
        this.mainActivityNavigator = mainActivityNavigator;

    }

    public MainActivityNavigator getMainActivityNavigator() {
        return mainActivityNavigator;
    }

    public Flowable<List<Song>> getSongs() {

        return localDataSource.getSongs();

    }

    public Flowable<List<Setlist>> getSetlists() {

        return localDataSource.getSetlists();

    }

    public void updateSetlistSongs(@NonNull Setlist setlist, @NonNull final List<String> songs) {

        setlist.setSongs(songs);
        localDataSource.updateSetlist(setlist);

    }

    public Completable deleteSong(@NonNull final String songId) {

        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {

                localDataSource.deleteSongById(songId);

            }
        });

    }
}
