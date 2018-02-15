package setlistmanager;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import setlistmanager.data.source.local.LocalDataSource;
import setlistmanager.main.MainActivity;
import setlistmanager.main.MainActivityNavigator;
import setlistmanager.main.MainActivityViewModel;
import setlistmanager.setlist.AddEditSetlistNavigator;
import setlistmanager.setlist.AddEditSetlistViewModel;
import setlistmanager.setlist.AddSongsToSetlistNavigator;
import setlistmanager.setlist.AddSongsToSetlistViewModel;
import setlistmanager.setlist.SetlistSongsNavigator;
import setlistmanager.setlist.SetlistSongsViewModel;
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

        } else if ( modelClass.isAssignableFrom(SetlistSongsViewModel.class) ) {

            SetlistSongsNavigator setlistSongsNavigator = new SetlistSongsNavigator(baseNavigator);
            return (T) new SetlistSongsViewModel( localDataSource, setlistSongsNavigator );

        } else if ( modelClass.isAssignableFrom(AddSongsToSetlistViewModel.class) ) {

            AddSongsToSetlistNavigator addSongsToSetlistNavigator = new AddSongsToSetlistNavigator(baseNavigator);
            return (T) new AddSongsToSetlistViewModel( localDataSource, addSongsToSetlistNavigator );

        } else if ( modelClass.isAssignableFrom(MainActivityViewModel.class) ) {

            MainActivityNavigator mainActivityNavigator = new MainActivityNavigator(baseNavigator);
            return (T) new MainActivityViewModel( localDataSource, mainActivityNavigator );

        }

        throw new IllegalArgumentException("Unknown ViewModel class");

    }
}
