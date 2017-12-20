package setlistmanager.setlist;

import android.app.DialogFragment;
import android.arch.lifecycle.ViewModelProviders;
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

import com.setlistmanager.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import setlistmanager.Injection;
import setlistmanager.ViewModelFactory;
import setlistmanager.data.Song;
import setlistmanager.util.ConfirmDialogFragment;

public class SetlistSongsActivity extends AppCompatActivity implements ConfirmDialogFragment.ConfirmDialogListener{

    private final String TAG = SetlistSongsActivity.class.getSimpleName();

    private ViewModelFactory viewModelFactory;

    private SetlistSongsViewModel setlistSongsViewModel;

    private SetlistSongsNavigator setlistSongsNavigator;

    private final CompositeDisposable disposable = new CompositeDisposable();

    private RecyclerView recyclerView;

    private RecyclerView.Adapter adapter;

    private RecyclerView.LayoutManager layoutManager;

    private List<Song> dataset;

    String setlistId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setlist_songs);

        Bundle extras = getIntent().getExtras();
        setlistId = extras.getString("setlistId");
        String setlistName = extras.getString("setlistName");

        if ( setlistId != null ) {
            getSetlistSongs(setlistId);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if ( setlistName != null ) {
            actionBar.setTitle(setlistName);
        } else {
            actionBar.setTitle("Setlist songs");
        }

        viewModelFactory = Injection.provideViewModelFactory(this, this);
        setlistSongsViewModel = ViewModelProviders.of(this, viewModelFactory).get(SetlistSongsViewModel.class);
        setlistSongsNavigator = setlistSongsViewModel.getSetlistSongsNavigator();

        dataset = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        adapter = new SetlistSongsRecyclerViewAdapter(this, dataset);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    /*

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_nav_add, menu);
        return true;
    }
    */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav:
                Log.i(TAG, "Nav button clicked");
                return true;

            case R.id.nav_setlists:
                Log.i(TAG, "Setlists clicked in nav menu");
                setlistSongsNavigator.toSetlists();
                return true;

            case R.id.nav_songs:
                Log.i(TAG, "Songs clicked in nav menu");
                setlistSongsNavigator.toSetlists();
                return true;

            case R.id.nav_settings:
                Log.i(TAG, "Settings clicked in nav menu");
                return true;

            case R.id.add:
                Log.i(TAG, "Add button clicked");

                setlistSongsNavigator.addSongsToSetlist();

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
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
                Log.i(TAG, "Open clicked in context menu");
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
            //deleteSongFromSetlist(song.getId());
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

                                if ( songs != null ) {
                                    getSetlistSongsById(songs);
                                } else {
                                    dataset.clear();
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

    private void getSetlistSongsById(List<String> songIds) {

        disposable.add(
                setlistSongsViewModel.getSetlistSongsById(songIds)
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

}
