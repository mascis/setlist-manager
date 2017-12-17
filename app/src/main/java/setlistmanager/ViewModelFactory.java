package setlistmanager;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import setlistmanager.data.source.local.LocalDataSource;
import setlistmanager.setlist.AddEditSetlistNavigator;
import setlistmanager.setlist.AddEditSetlistViewModel;
import setlistmanager.setlist.SetlistsNavigator;
import setlistmanager.setlist.SetlistsViewModel;
import setlistmanager.song.AddEditSongNavigator;
import setlistmanager.song.AddEditSongViewModel;
import setlistmanager.song.SongsNavigator;
import setlistmanager.song.SongsViewModel;
import setlistmanager.util.BaseNavigator;

/**
 * Created by User on 15.12.2017.
 */

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final LocalDataSource localDataSource;
    private final BaseNavigator baseNavigator;

    public ViewModelFactory(LocalDataSource _localDataSource, BaseNavigator _baseNavigator) {

        localDataSource = _localDataSource;
        baseNavigator = _baseNavigator;

    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        if ( modelClass.isAssignableFrom(SetlistsViewModel.class) ) {

            SetlistsNavigator setlistsNavigator = new SetlistsNavigator( baseNavigator );
            return (T) new SetlistsViewModel( localDataSource, setlistsNavigator );

        } else if ( modelClass.isAssignableFrom(AddEditSetlistViewModel.class) ) {

            AddEditSetlistNavigator addEditSetlistNavigator = new AddEditSetlistNavigator(baseNavigator);
            return (T) new AddEditSetlistViewModel( localDataSource, addEditSetlistNavigator, false );

        } else if ( modelClass.isAssignableFrom(SongsViewModel.class) ) {

            SongsNavigator songsNavigator = new SongsNavigator(baseNavigator);
            return (T) new SongsViewModel( localDataSource, songsNavigator );

        } else if ( modelClass.isAssignableFrom(AddEditSongViewModel.class) ) {

            AddEditSongNavigator addEditSongNavigator = new AddEditSongNavigator(baseNavigator);
            return (T) new AddEditSongViewModel( localDataSource, addEditSongNavigator, false );

        }

        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
