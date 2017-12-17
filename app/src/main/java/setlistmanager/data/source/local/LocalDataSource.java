package setlistmanager.data.source.local;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;
import setlistmanager.data.Setlist;
import setlistmanager.data.source.DataSource;

/**
 * Created by User on 14.12.2017.
 */

public class LocalDataSource implements DataSource {

    private static volatile LocalDataSource INSTANCE;

    private SetlistDao setlistDao;

    private LocalDataSource( @NonNull SetlistDao _setlistDao ) {

        setlistDao = _setlistDao;

    }

    public static LocalDataSource getInstance( @NonNull SetlistDao setlistDao ) {

        if ( INSTANCE == null ) {

            synchronized ( LocalDataSource.class ) {

                if ( INSTANCE == null ) {
                    INSTANCE = new LocalDataSource( setlistDao );
                }
            }
        }

        return INSTANCE;

    }

    @Override
    public Flowable<List<Setlist>> getSetlists() {
        return setlistDao.getSetlists();
    }

    @Override
    public Single<Setlist> getSetlist(@NonNull String setlistId) {
        return setlistDao.getSetlistById(setlistId);
    }

    @Override
    public void insertSetlist(@NonNull Setlist setlist) {
        setlistDao.insertSetlist(setlist);
    }

    @Override
    public void updateSetlist(@NonNull Setlist setlist) {
        setlistDao.updateSetlist(setlist);
    }

    @Override
    public int deleteSetlist(@NonNull String setlistId) {
        return setlistDao.deleteSetlistById(setlistId);
    }

    @Override
    public void deleteSetlists() {
        setlistDao.deleteSetlists();
    }
}
