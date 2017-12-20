package setlistmanager.setlist;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import setlistmanager.data.Setlist;
import setlistmanager.data.Song;
import setlistmanager.data.source.local.LocalDataSource;
import setlistmanager.song.SongsNavigator;

/**
 * Created by User on 17.12.2017.
 */

public class AddSongsToSetlistViewModel extends ViewModel {

    private static final String TAG = AddSongsToSetlistViewModel.class.getSimpleName();

    private final LocalDataSource localDataSource;

    private final AddSongsToSetlistNavigator addSongsToSetlistNavigator;

    public AddSongsToSetlistViewModel(LocalDataSource localDataSource, AddSongsToSetlistNavigator addSongsToSetlistNavigator) {
        this.localDataSource = localDataSource;
        this.addSongsToSetlistNavigator = addSongsToSetlistNavigator;

    }

    public AddSongsToSetlistNavigator getAddSongsToSetlistNavigator() {
        return addSongsToSetlistNavigator;
    }

    public Flowable<List<Song>> getAvailableSongs(List<String> songIds) {

        if ( songIds == null || songIds.isEmpty() ) {
            return localDataSource.getSongs();
        }

        return localDataSource.getAvailableSongs(songIds);

    }

    public Single<Setlist> getSetlist(@NonNull final String setlistId) {

        return localDataSource.getSetlist(setlistId);

    }

    public Completable addSongsToSetlist(@NonNull final Setlist setlist, @NonNull final List<String> songs) {

        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {

                setlist.setSongs(songs);
                localDataSource.updateSetlist(setlist);

            }
        });

    }

}
