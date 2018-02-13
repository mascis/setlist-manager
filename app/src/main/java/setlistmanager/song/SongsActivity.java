package setlistmanager.song;

import android.app.DialogFragment;
import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import setlistmanager.setlist.SetlistSongsRecyclerViewAdapter;
import setlistmanager.util.ConfirmDialogFragment;

public class SongsActivity extends AppCompatActivity implements ConfirmDialogFragment.ConfirmDialogListener, SongRecyclerViewAdapter.ItemClickListener {

    private static final String TAG = SongsActivity.class.getSimpleName();

    private ViewModelFactory viewModelFactory;

    private SongsViewModel songsViewModel;

    private SongsNavigator songsNavigator;

    private final CompositeDisposable disposable = new CompositeDisposable();

    private RecyclerView recyclerView;

    private FloatingActionButton floatingActionButton;

    private RecyclerView.Adapter adapter;

    private RecyclerView.LayoutManager layoutManager;

    private List<Song> dataset;

    private DrawerLayout drawerLayout;

    private NavigationView navigationView;

    private ActionBarDrawerToggle drawerToggle;

    private Toast toastDeleteSuccessful;

    private Toast toastDeleteFailed;

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.songs_title));

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };

        drawerLayout.addDrawerListener(drawerToggle);

        navigationView = (NavigationView) findViewById(R.id.navigation);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                selectDrawerItem(item);
                return true;

            }
        });

        viewModelFactory = Injection.provideViewModelFactory(this, this);
        songsViewModel = ViewModelProviders.of(this, viewModelFactory).get(SongsViewModel.class);
        songsNavigator = songsViewModel.getSongsNavigator();

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_add);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songsNavigator.addSong();
            }
        });

        toastDeleteSuccessful = Toast.makeText(getApplicationContext(), getResources().getText(R.string.song_deleted_successfully), Toast.LENGTH_LONG);
        toastDeleteFailed = Toast.makeText(getApplicationContext(), getResources().getText(R.string.song_deleted_successfully), Toast.LENGTH_LONG);

        dataset = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        adapter = new SongRecyclerViewAdapter(this, dataset);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getSongs();
    }

    @Override
    public void onItemClick(int position) {

        toScreenSlide(position, false);
        /*
        Bundle bundle = new Bundle();

        bundle.putString(ScreenSlideActivity.EXTRA_START_POSITION, String.valueOf(position));
        bundle.putString(ScreenSlideActivity.EXTRA_NUM_PAGES, String.valueOf(dataset.size()));
        bundle.putSerializable(ScreenSlideActivity.EXTRA_NUM_ITEMS, (Serializable) dataset);

        songsNavigator.toScreenSlider(bundle);
        */

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);

    }

    private boolean selectDrawerItem(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.nav_setlists:
                songsNavigator.toSetlists();
                return true;

            case R.id.nav_songs:
                return true;

            case R.id.nav_settings:
                return true;

            default:
                return true;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if ( drawerToggle.onOptionsItemSelected(item)){
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
                toScreenSlide(position, true);
                return true;

            case R.id.remove:
                showConfirmDialog(song);
                return true;

            default:
                return super.onContextItemSelected(item);
        }

    }

    public void showConfirmDialog(Song song) {

        String title = getResources().getString(R.string.confirm_dialog_delete_song_title);
        String question = getResources().getString(R.string.confirm_dialog_delete_song_message);
        String songTitle = song.getTitle();
        String note = getResources().getString(R.string.confirm_dialog_delete_song_message_note);
        String message = question + " " + songTitle + "? " + note;

        DialogFragment confirmDialog = ConfirmDialogFragment.instance(title, message);
        confirmDialog.show(getFragmentManager(), "ConfirmDialog");

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

        Song song;

        try {
            song = dataset.get(getAdapterPosition());
            deleteSongFromSetlists(song.getId());
            deleteSong(song.getId());
        } catch (Exception e) {
            Log.e(TAG, "Deleting song failed");
            toastDeleteFailed.show();
        }

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

        dialog.dismiss();

    }

    private void toScreenSlide(int position, boolean single) {

        Bundle bundle = new Bundle();

        if ( single ) {

            bundle.putString(ScreenSlideActivity.EXTRA_START_POSITION, "0");
            bundle.putString(ScreenSlideActivity.EXTRA_NUM_PAGES, "1");
            List<Song> song = new ArrayList<>();
            song.add(dataset.get(position));
            bundle.putSerializable(ScreenSlideActivity.EXTRA_NUM_ITEMS, (Serializable) song);

        } else {

            bundle.putString(ScreenSlideActivity.EXTRA_START_POSITION, String.valueOf(position));
            bundle.putString(ScreenSlideActivity.EXTRA_NUM_PAGES, String.valueOf(dataset.size()));
            bundle.putSerializable(ScreenSlideActivity.EXTRA_NUM_ITEMS, (Serializable) dataset);

        }

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

    @Override
    protected void onStop() {
        super.onStop();

        disposable.clear();
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
