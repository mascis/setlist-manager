package setlistmanager.data.source;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;
import setlistmanager.data.Setlist;

/**
 * Created by User on 14.12.2017.
 */

public interface DataSource {

    Flowable<List<Setlist>> getSetlists();

    Single<Setlist> getSetlist(@NonNull String setlistId );

    void insertSetlist( @NonNull Setlist setlist );

    void updateSetlist(@NonNull Setlist setlist);

    int deleteSetlist( @NonNull String setlistId );

    void deleteSetlists();
}
