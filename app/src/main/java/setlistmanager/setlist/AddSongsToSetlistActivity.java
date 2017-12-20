package setlistmanager.setlist;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

    private ViewModelFactory viewModelFactory;

    private AddSongsToSetlistViewModel addSongsToSetlistViewModel;

    private AddSongsToSetlistNavigator addSongsToSetlistNavigator;

    private final CompositeDisposable disposable = new CompositeDisposable();

    private List<Song> dataset;

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.songs_title));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        setlistId = getIntent().getStringExtra(SETLIST_ID);

        viewModelFactory = Injection.provideViewModelFactory(this, this);
        addSongsToSetlistViewModel = ViewModelProviders.of(this, viewModelFactory).get(AddSongsToSetlistViewModel.class);
        addSongsToSetlistNavigator = addSongsToSetlistViewModel.getAddSongsToSetlistNavigator();

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

    private int getAdapterPosition() {

        int position = -1;

        try {

            AddSongsToSetlistRecyclerViewAdapter adapter = (AddSongsToSetlistRecyclerViewAdapter) recyclerView.getAdapter();
            position = adapter.getPosition();

        } catch (Exception e) {

            Log.e(TAG, "Error getting setlist list item");
            e.printStackTrace();

        }

        return position;
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

                        Log.i(TAG, "getSetlist successful");
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

                                dataset.clear();

                                if ( songs != null ) {
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

        AddSongsToSetlistRecyclerViewAdapter adapter = (AddSongsToSetlistRecyclerViewAdapter) recyclerView.getAdapter();

        List<String> selectedSongs = new ArrayList<>();

        for( int i = 0; i < adapter.getItemCount(); i++ ) {

            AddSongsToSetlistRecyclerViewAdapter.ViewHolder holder = (AddSongsToSetlistRecyclerViewAdapter.ViewHolder) recyclerView.getChildViewHolder(recyclerView.getChildAt(i));

            Log.i(TAG, "pos: " + i + ", is checked: " + holder.checkBox.isChecked());

            if ( holder.checkBox.isChecked() ) {
                selectedSongs.add(dataset.get(i).getId());
            }
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

        Log.i(TAG, "Updated song list:");
        for( String songId : updatedSongList ) {
            Log.i(TAG, songId);
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
}