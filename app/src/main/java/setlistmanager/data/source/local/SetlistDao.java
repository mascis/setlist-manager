package setlistmanager.data.source.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;
import setlistmanager.data.Setlist;

/**
 * Created by User on 14.12.2017.
 */

@Dao
public interface SetlistDao {

    @Query("SELECT * FROM setlists")
    Flowable<List<Setlist>> getSetlists();

    @Query("SELECT * FROM setlists WHERE setlistId = :setlistId")
    Single<Setlist> getSetlistById(String setlistId );

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSetlist( Setlist setlist );

    @Update
    void updateSetlist( Setlist setlist );

    @Query("DELETE FROM setlists WHERE setlistId = :setlistId")
    int deleteSetlistById( String setlistId );

    @Query("DELETE FROM setlists")
    void deleteSetlists();

}
