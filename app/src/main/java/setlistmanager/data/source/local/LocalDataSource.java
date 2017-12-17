package setlistmanager.data.source.local;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;
import setlistmanager.data.Setlist;
import setlistmanager.data.Song;
import setlistmanager.data.source.DataSource;

/**
 * Created by User on 14.12.2017.
 */

public class LocalDataSource implements DataSource {

    private static volatile LocalDataSource INSTANCE;

    private SetlistDao setlistDao;

    private SongDao songDao;

    private LocalDataSource( @NonNull SetlistDao _setlistDao, @NonNull SongDao _songDao ) {

        setlistDao = _setlistDao;
        songDao = _songDao;

    }

    public static LocalDataSource getInstance( @NonNull SetlistDao setlistDao, @NonNull SongDao songDao  ) {

        if ( INSTANCE == null ) {

            synchronized ( LocalDataSource.class ) {

                if ( INSTANCE == null ) {
                    INSTANCE = new LocalDataSource( setlistDao, songDao );
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

    @Override
    public Flowable<List<String>> getSetlistSongs(@NonNull String setlistId) {
        return setlistDao.getSetlistSongs(setlistId);
    }

    @Override
    public Flowable<List<Song>> getSongs() {
        return songDao.getSongs();
    }

    @Override
    public Single<Song> getSongById(String songId) {
        return songDao.getSongById(songId);
    }

    @Override
    public void insertSong(Song song) {
        songDao.insertSong(song);
    }

    @Override
    public void updateSong(Song song) {
        songDao.updateSong(song);
    }

    @Override
    public int deleteSongById(String songId) {
        return songDao.deleteSongById(songId);
    }

    @Override
    public void deleteSongs() {
        songDao.deleteSongs();
    }
}
