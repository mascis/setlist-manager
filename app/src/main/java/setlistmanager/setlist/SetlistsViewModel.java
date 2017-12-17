package setlistmanager.setlist;


import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;


import java.util.Date;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import setlistmanager.data.Setlist;
import setlistmanager.data.source.local.LocalDataSource;
import setlistmanager.util.BaseNavigator;

/**
 * Created by User on 15.12.2017.
 */

public class SetlistsViewModel extends ViewModel {

    private static final String TAG = SetlistsViewModel.class.getSimpleName();

    private final LocalDataSource dataSource;

    private SetlistsNavigator setlistsNavigator;

    public SetlistsViewModel(LocalDataSource _dataSource, SetlistsNavigator _setlistsNavigator ) {

        dataSource = _dataSource;
        setlistsNavigator = _setlistsNavigator;

    }

    public SetlistsNavigator getSetlistsNavigator() {
        return setlistsNavigator;
    }

    public void setSetlistsNavigator(SetlistsNavigator setlistsNavigator) {
        this.setlistsNavigator = setlistsNavigator;
    }

    /*
     *  METHODS
     */

    public Flowable<List<Setlist>> getSetlists() {

        return dataSource.getSetlists();
    }

    public Completable duplicateSetlist(@NonNull final Setlist setlistToCopy) {

        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {

                String name = setlistToCopy.getName() + "(copy)";
                Date now = new Date();

                Setlist setlist = new Setlist(name, setlistToCopy.getLocation(), setlistToCopy.getDate(), now, now, setlistToCopy.getSongs());

                dataSource.insertSetlist(setlist);

            }
        });

    }

    public Completable deleteSetlist(@NonNull final String setlistId) {

        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {

                dataSource.deleteSetlist(setlistId);

            }
        });

    }


}
