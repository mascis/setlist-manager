package setlistmanager.setlist;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
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

    public Flowable<List<Song>> getSetlistSongsById(@NonNull final List<String> songIds) {

        List<String> list = new ArrayList<>();

        for(int i = 0; i < songIds.size(); i++ ) {
            list.add(songIds.get(i).substring(2, songIds.get(i).length() - 2));
        }

        return localDataSource.getSongsById(list);

    }

    private String[] toArray(List<String> songIds) {

        String[] array = new String[songIds.size()];

        for( int i = 0; i < songIds.size(); i++ ) {

            array[i] = songIds.get(i);

        }

        return array;

    }

    public Single<Song> getSong(@NonNull final String songId) {

        return localDataSource.getSongById(songId);

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
