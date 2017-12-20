package setlistmanager.data.source;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;
import setlistmanager.data.Setlist;
import setlistmanager.data.Song;

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

    Flowable<List<String>> getSetlistSongs(String setlistId);

    Flowable<List<Song>> getSongs();

    Flowable<List<Song>> getSongsById(List<String> songIds);

    Single<Song> getSongById(String songId);

    void insertSong(Song song);

    void updateSong(Song song);

    int deleteSongById(String songId);

    void deleteSongs();

}
