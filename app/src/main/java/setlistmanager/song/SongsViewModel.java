package setlistmanager.song;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.functions.Action;
import setlistmanager.data.Song;
import setlistmanager.data.source.local.LocalDataSource;

/**
 * Created by User on 17.12.2017.
 */

public class SongsViewModel extends ViewModel {

    private static final String TAG = SongsViewModel.class.getSimpleName();

    private final LocalDataSource localDataSource;

    private final SongsNavigator songsNavigator;

    public SongsViewModel(LocalDataSource localDataSource, SongsNavigator songsNavigator) {
        this.localDataSource = localDataSource;
        this.songsNavigator = songsNavigator;

    }

    public SongsNavigator getSongsNavigator() {
        return songsNavigator;
    }

    public Flowable<List<Song>> getSongs() {

        return localDataSource.getSongs();

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
