package setlistmanager.setlist;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;

import java.util.Date;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import setlistmanager.data.Setlist;
import setlistmanager.data.source.local.LocalDataSource;

/**
 * Created by User on 15.12.2017.
 */

public class AddEditSetlistViewModel extends ViewModel {

    private final LocalDataSource dataSource;

    private final AddEditSetlistNavigator addEditSetlistNavigator;

    private boolean isEditMode;

    public AddEditSetlistViewModel(LocalDataSource _dataSource, AddEditSetlistNavigator _addEditSetlistNavigator, boolean _isEditMode) {

        dataSource = _dataSource;
        addEditSetlistNavigator = _addEditSetlistNavigator;
        isEditMode = _isEditMode;

    }

    public AddEditSetlistNavigator getAddEditSetlistNavigator() {
        return addEditSetlistNavigator;
    }

    public boolean isEditMode() {
        return isEditMode;
    }

    public void setEditMode(boolean editMode) {
        isEditMode = editMode;
    }

    public Completable saveSetlist(@Nullable final Setlist _setlist, @NonNull final String setlistName, @Nullable final String location, @Nullable final Date date) {

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

    }

    public Single<Setlist> getSetlistById(@NonNull final String setlistId) {

        return dataSource.getSetlist(setlistId);

    }

}
