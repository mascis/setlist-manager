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
import setlistmanager.data.Song;

/**
 * Created by User on 14.12.2017.
 */

@Dao
public interface SongDao {

    @Query("SELECT * FROM songs")
    Flowable<List<Song>> getSongs();

    @Query("SELECT * FROM songs WHERE songId NOT IN (:songIds)")
    Flowable<List<Song>> getAvailableSongs(List<String> songIds);

    @Query("SELECT * FROM songs WHERE songId IN (:songIds)")
    Flowable<List<Song>> getSongsById(List<String> songIds);

    @Query("SELECT * FROM songs WHERE songId = :songId")
    Single<Song> getSongById(String songId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSong(Song song);

    @Update
    void updateSong(Song song);

    @Query("DELETE FROM songs WHERE songId = :songId")
    int deleteSongById(String songId);

    @Query("DELETE FROM songs")
    void deleteSongs();

}
