package setlistmanager.setlist;

import android.app.DialogFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.setlistmanager.R;

import org.apache.commons.collections4.Get;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Observer;
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
import setlistmanager.helper.ItemTouchHelperAdapter;
import setlistmanager.helper.OnStartDragListener;
import setlistmanager.helper.SimpleItemTouchHelperCallback;
import setlistmanager.screenslide.ScreenSlideActivity;
import setlistmanager.util.ConfirmDialogFragment;

public class SetlistSongsActivity extends AppCompatActivity implements ConfirmDialogFragment.ConfirmDialogListener, SetlistSongsRecyclerViewAdapter.ItemClickListener, OnStartDragListener {

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

    Setlist setlist;

    private FloatingActionButton floatingActionButton;

    private ItemTouchHelper itemTouchHelper;

    private Toast reorderStatus;

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
        adapter = new SetlistSongsRecyclerViewAdapter(this, dataset, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        ItemTouchHelperAdapter itemTouchHelperAdapter = new ItemTouchHelperAdapter() {
            @Override
            public boolean onItemMove(int fromPosition, int toPosition) {

                if ( fromPosition != toPosition ) {
                    Collections.swap(dataset, fromPosition, toPosition);
                    adapter.notifyItemMoved(fromPosition, toPosition);

                    reorderSetlistSongs();
                }

                return true;

            }

            @Override
            public void onItemDismiss(int position) {

            }
        };

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(itemTouchHelperAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

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

        reorderStatus = Toast.makeText(getApplicationContext(), getResources().getText(R.string.common_saving), Toast.LENGTH_SHORT);

    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public boolean onSupportNavigateUp() {
        disposable.clear();
        setlistSongsNavigator.onBackPressed();
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        disposable.clear();
    }

    @Override
    protected void onStart() {
        super.onStart();

        disposable.add(
                setlistSongsViewModel.getSetlist(setlistId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Setlist>() {
                            @Override
                            public void accept(Setlist s) throws Exception {
                                setlist = s;
                                new GetSongsAsyncTask().execute();
                            }
                        })
        );

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

    private void toScreenSlide(int position) {

        Bundle bundle = new Bundle();

        bundle.putString(ScreenSlideActivity.EXTRA_START_POSITION, String.valueOf(position));
        bundle.putString(ScreenSlideActivity.EXTRA_NUM_PAGES, String.valueOf(dataset.size()));
        bundle.putSerializable(ScreenSlideActivity.EXTRA_NUM_ITEMS, (Serializable) dataset);

        setlistSongsNavigator.toScreenSlider(bundle);

    }

    @Override
    public void onItemClick(int position) {

        toScreenSlide(position);

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
                toScreenSlide(position);
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

    private void removeSongFromSetlist( String songId ) {

        List<String> songList = setlist.getSongs();
        List<String> updatedSongList = new ArrayList<>();

        for( String id : songList ) {

            if ( !songId.equals(id) ) {

                updatedSongList.add(id);

            }

        }

        List<Object> params = new ArrayList<>();
        params.add(setlist);
        params.add(updatedSongList);

        new RemoveAsyncTask().execute(params);

    }

    private void reorderSetlistSongs() {

        List<Object> params = new ArrayList<>();
        params.add(setlist);

        List<String> reorderedDataset = new ArrayList<>();

        for ( Song song : dataset ) {
            reorderedDataset.add(song.getId());
        }

        params.add(reorderedDataset);

        new ReorderAsyncTask().execute(params);

    }

    private class ReorderAsyncTask extends AsyncTask<List<Object>, Void, List<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            reorderStatus.setText(R.string.common_saving);
            reorderStatus.show();
        }

        @Override
        protected void onPostExecute(List<String> list) {
            super.onPostExecute(list);
            reorderStatus.setText(R.string.common_saved);
            reorderStatus.show();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected List<String> doInBackground(List<Object>[] params) {

            List<Object> list = params[0];

            Setlist setlist = (Setlist)list.get(0);
            List<String> songs = (List<String>) list.get(1);

            setlistSongsViewModel.updateSetlistSongs(setlist, songs);

            return songs;
        }
    }

    private void updateSongList() {

        new GetSongsAsyncTask().execute();

    }

    private class GetSongsAsyncTask extends AsyncTask<Setlist, Void, List<Song>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dataset.clear();
        }

        @Override
        protected void onPostExecute(List<Song> songs) {
            super.onPostExecute(songs);
            dataset.addAll(songs);
            adapter.notifyDataSetChanged();
        }

        @Override
        protected List<Song> doInBackground(Setlist... setlists) {

            final List<Song> setlistSongs = new ArrayList<>();

            if ( setlist != null && setlist.getSongs() != null && !setlist.getSongs().isEmpty() ) {

                Observable.fromIterable(setlist.getSongs()).subscribe(new Consumer<String>() {
                    @Override
                    public void accept(final String s) throws Exception {

                        setlistSongsViewModel.getSongById(s).subscribe(new Consumer<Song>() {
                            @Override
                            public void accept(Song song) throws Exception {

                                setlistSongs.add(song);


                            }
                        });

                    }
                });

            }

            return setlistSongs;

        }
    }

    private class RemoveAsyncTask extends AsyncTask<List<Object>, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dataset.clear();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateSongList();
        }

        @Override
        protected Void doInBackground(List<Object>[] params) {

            List<Object> list = params[0];

            Setlist setlist = (Setlist)list.get(0);
            List<String> songs = (List<String>) list.get(1);

            setlistSongsViewModel.updateSetlistSongs(setlist, songs);

            return null;
        }
    }

}
