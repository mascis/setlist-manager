package setlistmanager.setlist;

import android.app.DialogFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.setlistmanager.R;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
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
import setlistmanager.data.source.local.Converters;
import setlistmanager.screenslide.ScreenSlideActivity;
import setlistmanager.util.ConfirmDialogFragment;

public class SetlistSongsActivity extends AppCompatActivity implements ConfirmDialogFragment.ConfirmDialogListener, SetlistSongsRecyclerViewAdapter.ItemClickListener {

    private final String TAG = SetlistSongsActivity.class.getSimpleName();

    public static final String SETLIST_ID = "setlistId";
    public static final String SETLIST_NAME = "setlistName";

    private ViewModelFactory viewModelFactory;

    private SetlistSongsViewModel setlistSongsViewModel;

    private SetlistSongsNavigator setlistSongsNavigator;

    private final CompositeDisposable disposable = new CompositeDisposable();

    private RecyclerView recyclerView;

    private RecyclerView.Adapter adapter;

    private RecyclerView.LayoutManager layoutManager;

    private List<Song> dataset;

    String setlistId;

    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setlist_songs);

        viewModelFactory = Injection.provideViewModelFactory(this, this);
        setlistSongsViewModel = ViewModelProviders.of(this, viewModelFactory).get(SetlistSongsViewModel.class);
        setlistSongsNavigator = setlistSongsViewModel.getSetlistSongsNavigator();

        dataset = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        adapter = new SetlistSongsRecyclerViewAdapter(this, dataset);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Bundle extras = getIntent().getExtras();
        setlistId = extras.getString(SETLIST_ID);
        final String setlistName = extras.getString(SETLIST_NAME);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if ( setlistName != null ) {
            actionBar.setTitle(setlistName);
        } else {
            actionBar.setTitle("Setlist songs");
        }

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_add);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, String> extras = new HashMap<>();
                extras.put(AddSongsToSetlistActivity.SETLIST_ID, setlistId);
                extras.put(AddSongsToSetlistActivity.SETLIST_NAME, setlistName);

                setlistSongsNavigator.addSongsToSetlist(extras);
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
        getSetlistSongs(setlistId);
    }

    private int getAdapterPosition() {

        int position = -1;

        try {

            SetlistSongsRecyclerViewAdapter adapter = (SetlistSongsRecyclerViewAdapter) recyclerView.getAdapter();
            position = adapter.getPosition();

        } catch (Exception e) {

            Log.e(TAG, "Error getting setlist list item");
            e.printStackTrace();

        }

        return position;

    }

    @Override
    public void onItemClick(int position) {

        Bundle bundle = new Bundle();

        bundle.putString(ScreenSlideActivity.EXTRA_START_POSITION, String.valueOf(position));
        bundle.putString(ScreenSlideActivity.EXTRA_NUM_PAGES, String.valueOf(dataset.size()));
        bundle.putSerializable(ScreenSlideActivity.EXTRA_NUM_ITEMS, (Serializable) dataset);

        setlistSongsNavigator.toScreenSlider(bundle);

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
                setlistSongsNavigator.editSong(song.getId());
                return true;

            case R.id.open:
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
        String message = question + " " + songTitle + "?";

        DialogFragment confirmDialog = ConfirmDialogFragment.instance(title, message);
        confirmDialog.show(getFragmentManager(), "ConfirmDialog");

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

        Song song;

        try {
            song = dataset.get(getAdapterPosition());
            removeSongFromSetlist(song.getId());
        } catch (Exception e) {
            Log.e(TAG, "Deleting song failed");
        }

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

        dialog.dismiss();

    }
    @Override
    protected void onStop() {
        super.onStop();

        disposable.clear();
    }

    private void getSetlistSongs(String setlistId) {

        disposable.add(
                setlistSongsViewModel.getSetlistSongs(setlistId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<String>>() {

                            @Override
                            public void accept(List<String> songs) throws Exception {

                                if ( songs.get(0) != null ) {

                                    List<String> songIds = Converters.listfromString(songs.get(0));
                                    getSetlistSongsById(songIds);
                                }

                            }

                        }, new Consumer<Throwable>() {

                            @Override
                            public void accept(Throwable throwable) throws Exception {

                                Log.e(TAG, "Unable to get songs", throwable);

                            }

                        })
        );

    }

    private void getSetlistSongsById(final List<String> songIds) {

        disposable.add(
                setlistSongsViewModel.getSetlistSongsById(songIds)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<Song>>() {

                            @Override
                            public void accept(List<Song> songs) throws Exception {

                                if ( songs != null && !songs.isEmpty() ) {
                                    dataset.clear();
                                    dataset.addAll(songs);
                                    adapter.notifyDataSetChanged();
                                }

                            }
                        }, new Consumer<Throwable>() {

                            @Override
                            public void accept(Throwable throwable) throws Exception {

                                Log.e(TAG, "Unable to get songs", throwable);

                            }

                        })
        );

    }

    private void removeSongFromSetlist( final String songId ) {

        setlistSongsViewModel.getSetlist(setlistId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Setlist>() {
                    @Override
                    public void accept(Setlist setlist) throws Exception {

                        if ( setlist != null ) {

                            List<String> songList = setlist.getSongs();
                            List<String> updatedSongList = new ArrayList<>();

                            for( String id : songList ) {

                               if ( !songId.equals(id) ) {

                                   updatedSongList.add(id);

                               }

                            }

                            updateSetlistSongs(setlist, updatedSongList);

                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        Log.e(TAG, "Could not get setlist with id " + setlistId, throwable);

                    }
                });

    }

    private void updateSetlistSongs(Setlist setlist, final List<String> updatedSongList) {

        disposable.add(
                setlistSongsViewModel.updateSetlistSongs(setlist, updatedSongList)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action() {
                            @Override
                            public void run() throws Exception {
                                Log.i(TAG, "Song removed from list successfully");

                                getSetlistSongsById(updatedSongList);

                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.e(TAG, "Error updating song list", throwable);
                            }
                        })
        );

    }

}
