package setlistmanager.song;

import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.setlistmanager.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import setlistmanager.Injection;
import setlistmanager.ViewModelFactory;
import setlistmanager.data.Setlist;
import setlistmanager.data.Song;
import setlistmanager.screenslide.ScreenSlideActivity;

public class SongsActivity extends AppCompatActivity implements SongRecyclerViewAdapter.ItemClickListener {

    private static final String TAG = SongsActivity.class.getSimpleName();

    private ViewModelFactory viewModelFactory;

    private SongsViewModel songsViewModel;

    private SongsNavigator songsNavigator;

    private final CompositeDisposable disposable = new CompositeDisposable();

    private RecyclerView recyclerView;

    private FloatingActionButton floatingActionButton;

    private RecyclerView.Adapter adapter;

    private List<Song> dataset;

    private List<Song> allSongs;

    private Toast toastDeleteSuccessful;

    private Toast toastDeleteFailed;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);

        prepareActionBar();
        prepareFloatingActionButton();
        prepareBottomNavigation();
        prepareToastMessages();

        viewModelFactory = Injection.provideViewModelFactory(this, this);
        songsViewModel = ViewModelProviders.of(this, viewModelFactory).get(SongsViewModel.class);
        songsNavigator = songsViewModel.getSongsNavigator();

        allSongs = new ArrayList<>();
        dataset = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        adapter = new SongRecyclerViewAdapter(this, dataset, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void prepareActionBar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.songs_title));

    }

    private void prepareFloatingActionButton() {

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_add);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songsNavigator.addSong();
            }
        });

    }

    private void prepareBottomNavigation() {

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_songs);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                switch ( id ) {

                    case R.id.action_setlists:
                        songsNavigator.toSetlists();
                        return true;

                    case R.id.action_songs:
                        return true;

                    case R.id.action_settings:
                        return true;

                }

                return false;

            }
        });

    }

    private void prepareToastMessages() {
        toastDeleteSuccessful = Toast.makeText(getApplicationContext(), getResources().getText(R.string.song_deleted_successfully), Toast.LENGTH_LONG);
        toastDeleteFailed = Toast.makeText(getApplicationContext(), getResources().getText(R.string.song_deleted_successfully), Toast.LENGTH_LONG);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getSongs();
    }

    @Override
    protected void onStop() {
        super.onStop();

        disposable.clear();
    }

    @Override
    public void onItemClick(int position) {

        toScreenSlide(position);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu_main, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setSubmitButtonEnabled(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                filterSongs(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filterSongs(s);
                return false;
            }
        });

        return true;

    }

    private void filterSongs(String str) {

        List<Song> filteredSongs = new ArrayList<Song>();
        String searchableString = str.toString();

        for ( Song song : allSongs ) {

            String title = song.getTitle().toLowerCase();
            String artist = song.getArtist().toLowerCase();

            if ( title.contains(searchableString) || artist.contains(searchableString) ) {

                filteredSongs.add(song);

            }

        }

        dataset.clear();
        dataset.addAll(filteredSongs);
        adapter.notifyDataSetChanged();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch ( id ) {

            case R.id.action_settings:
                Log.i(TAG, "Settings clicked...");
                return true;

        }

        return super.onOptionsItemSelected(item);

    }

    private int getAdapterPosition() {

        int position = -1;

        try {

            SongRecyclerViewAdapter adapter = (SongRecyclerViewAdapter) recyclerView.getAdapter();
            position = adapter.getPosition();

        } catch (Exception e) {

            Log.e(TAG, "Error getting setlist list item");
            e.printStackTrace();

        }

        return position;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        int position = getAdapterPosition();

        if ( position == -1 ) {
            return false;
        }

        Song song = dataset.get(position);

        switch ( item.getItemId() ) {

            case R.id.edit:
                songsNavigator.editSong(song.getId());
                return true;

            case R.id.open:
                toScreenSlide(position);
                return true;

            case R.id.remove:
                handleRemove(song);
                return true;

            default:
                return super.onContextItemSelected(item);
        }

    }

    private String createRemoveMessage(Song song) {

        String msg = "";

        if ( song.getArtist() != null && !song.getArtist().isEmpty() ) {

            msg = song.getArtist() + " - " + song.getTitle() + " " + getResources().getString(R.string.common_deleted);

        } else {

            msg = song.getTitle() + " " + getResources().getString(R.string.common_deleted);

        }

        return msg;

    }

    private void handleRemove(final Song song ) {

        String msg = createRemoveMessage(song);

        final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);

        snackbar.setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });

        snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {

                if ( event == DISMISS_EVENT_TIMEOUT ) {
                    deleteSongFromSetlists(song.getId());
                    deleteSong(song.getId());
                }

            }
        });

        snackbar.show();

    }

    private void toScreenSlide(int position) {

        Bundle bundle = new Bundle();

        bundle.putString(ScreenSlideActivity.EXTRA_START_POSITION, String.valueOf(position));
        bundle.putString(ScreenSlideActivity.EXTRA_NUM_PAGES, String.valueOf(dataset.size()));
        bundle.putSerializable(ScreenSlideActivity.EXTRA_NUM_ITEMS, (Serializable) dataset);

        songsNavigator.toScreenSlider(bundle);

    }

    private void getSongs() {

        disposable.add(
                songsViewModel.getSongs()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<Song>>() {

                        @Override
                        public void accept(List<Song> songs) throws Exception {

                            allSongs.clear();
                            allSongs.addAll(songs);
                            dataset.clear();
                            dataset.addAll(songs);
                            adapter.notifyDataSetChanged();

                        }
                    }, new Consumer<Throwable>() {

                        @Override
                        public void accept(Throwable throwable) throws Exception {

                            Log.e(TAG, "Unable to get songs", throwable);

                        }

                    })
        );

    }

    private void deleteSongFromSetlists(final String songId) {

        disposable.add(
                songsViewModel.getSetlists()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<Setlist>>() {
                            @Override
                            public void accept(List<Setlist> setlists) throws Exception {

                                for ( Setlist setlist : setlists ) {

                                    List<String> songIds = setlist.getSongs();

                                    if ( songIds.contains(songId) ) {

                                        List<String> updatedIds = new ArrayList<>();

                                        for ( String id : songIds ) {

                                            if ( !id.equals(songId) ) {
                                                updatedIds.add(id);
                                            }

                                        }

                                        List<Object> params = new ArrayList<>();
                                        params.add(setlist);
                                        params.add(updatedIds);
                                        new UpdateAsyncTask().execute(params);

                                    }

                                }

                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {

                            }
                        }, new Action() {
                            @Override
                            public void run() throws Exception {

                            }
                        })
        );

    }

    private void deleteSong(String songId) {

        disposable.add(
                songsViewModel.deleteSong(songId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action() {
                            @Override
                            public void run() throws Exception {

                                Log.i(TAG, "Song deleted successfully");
                                toastDeleteSuccessful.show();

                            }
                        }, new Consumer<Throwable>() {

                            @Override
                            public void accept(Throwable throwable) throws Exception {

                                Log.e(TAG, "Error deleting song", throwable);
                                toastDeleteFailed.show();

                            }

                        })
        );

    }

    private class UpdateAsyncTask extends AsyncTask<List<Object>, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(List<Object>[] params) {

            List<Object> list = params[0];

            Setlist setlist = (Setlist)list.get(0);
            List<String> songs = (List<String>) list.get(1);

            songsViewModel.updateSetlistSongs(setlist, songs);

            return null;
        }
    }

}