package setlistmanager.setlist;

import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import com.setlistmanager.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import setlistmanager.Injection;
import setlistmanager.ViewModelFactory;
import setlistmanager.data.Setlist;
import setlistmanager.data.Song;

public class AddSongsToSetlistActivity extends AppCompatActivity {

    private final String TAG = AddSongsToSetlistActivity.class.getSimpleName();

    public static final String SETLIST_ID = "setlistId";
    public static final String SETLIST_NAME = "setlistName";

    private ViewModelFactory viewModelFactory;

    private AddSongsToSetlistViewModel addSongsToSetlistViewModel;

    private AddSongsToSetlistNavigator addSongsToSetlistNavigator;

    private final CompositeDisposable disposable = new CompositeDisposable();

    private List<Song> dataset;

    private List<Song> allSongs;

    private RecyclerView recyclerView;

    private RecyclerView.Adapter adapter;

    private RecyclerView.LayoutManager layoutManager;

    private String setlistId;

    private Setlist setlist;

    private Button saveButton;

    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_songs_to_setlist);

        Bundle extras = getIntent().getExtras();
        setlistId = extras.getString(SETLIST_ID);
        final String setlistName = extras.getString(SETLIST_NAME);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.add_songs_to_setlist_title) + " " + setlistName);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        viewModelFactory = Injection.provideViewModelFactory(this, this);
        addSongsToSetlistViewModel = ViewModelProviders.of(this, viewModelFactory).get(AddSongsToSetlistViewModel.class);
        addSongsToSetlistNavigator = addSongsToSetlistViewModel.getAddSongsToSetlistNavigator();

        allSongs = new ArrayList<>();
        dataset = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        adapter = new AddSongsToSetlistRecyclerViewAdapter(this, dataset);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        saveButton = (Button)findViewById(R.id.button_save);
        cancelButton = (Button)findViewById(R.id.button_cancel);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSongsToSetlist(setlistId);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSongsToSetlistNavigator.onCancel();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupSongList();
    }

    private void getSetlist(String setlistId) {

        addSongsToSetlistViewModel.getSetlist(setlistId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Setlist>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Setlist _setlist) {

                        setlist = _setlist;

                        getAvailableSongs(setlist.getSongs());

                    }

                    @Override
                    public void onError(Throwable e) {

                        Log.e(TAG, "error fetching setlist", e);

                    }
                });

    }

    private void getAvailableSongs(List<String> excludedSongs) {

        disposable.add(
                addSongsToSetlistViewModel.getAvailableSongs(excludedSongs)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<Song>>() {

                            @Override
                            public void accept(List<Song> songs) throws Exception {

                                allSongs.clear();
                                dataset.clear();

                                if ( songs != null ) {
                                    allSongs.addAll(songs);
                                    dataset.addAll(songs);
                                }

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

    private void setupSongList() {

        getSetlist(setlistId);

    }

    private List<String> getSelectedSongs() {

        List<String> selectedSongs = new ArrayList<>();

        try {

            AddSongsToSetlistRecyclerViewAdapter adapter = (AddSongsToSetlistRecyclerViewAdapter) recyclerView.getAdapter();
            selectedSongs = adapter.getSelectedSongs();

        } catch (Exception e) {

            Log.e(TAG, "Error getting setlist list item");
            e.printStackTrace();

        }

        return selectedSongs;

    }

    private void addSongsToSetlist(final String setlistId) {

        List<String> updatedSongList = new ArrayList<>();
        List<String> newSongs = getSelectedSongs();

        if ( newSongs != null ) {
            updatedSongList.addAll(newSongs);
        }

        if ( setlist.getSongs() != null && !setlist.getSongs().isEmpty() ) {
            updatedSongList.addAll(setlist.getSongs());
        }

        disposable.add(
                addSongsToSetlistViewModel.addSongsToSetlist(setlist, updatedSongList)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action() {
                            @Override
                            public void run() throws Exception {

                                Map<String, String> extras = new HashMap<>();

                                extras.put(SetlistSongsActivity.SETLIST_ID, setlist.getId());
                                extras.put(SetlistSongsActivity.SETLIST_NAME, setlist.getName());

                                addSongsToSetlistNavigator.onSuccess(extras);

                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {

                                Log.e(TAG, "Error updating song list", throwable);

                            }
                        })
        );

    }

    @Override
    protected void onStop() {
        super.onStop();

        disposable.clear();
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
}