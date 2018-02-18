package setlistmanager.setlist;

import android.arch.lifecycle.ViewModelProviders;
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

import com.setlistmanager.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import setlistmanager.Injection;
import setlistmanager.ViewModelFactory;
import setlistmanager.data.Setlist;

public class SetlistsActivity extends AppCompatActivity implements SetlistRecyclerViewAdapter.SetlistItemClickListener {

    private static final String TAG = SetlistsActivity.class.getSimpleName();

    private ViewModelFactory viewModelFactory;

    private SetlistsViewModel setlistsViewModel;

    private SetlistsNavigator setlistsNavigator;

    private final CompositeDisposable disposable = new CompositeDisposable();

    private RecyclerView recyclerView;

    private FloatingActionButton floatingActionButton;

    private RecyclerView.Adapter adapter;

    private List<Setlist> dataset;

    //private ItemTouchHelper itemTouchHelper;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setlists);

        prepareActionBar();
        prepareFloatingActionButton();
        prepareBottomNavigation();

        viewModelFactory = Injection.provideViewModelFactory(this, this);
        setlistsViewModel = ViewModelProviders.of(this, viewModelFactory).get(SetlistsViewModel.class);
        setlistsNavigator = setlistsViewModel.getSetlistsNavigator();

        dataset = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        adapter = new SetlistRecyclerViewAdapter(this, dataset, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

    private void prepareActionBar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.setlists_title));

    }

    private void prepareFloatingActionButton() {

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_add);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setlistsNavigator.addSetlist();
            }
        });

    }

    private void prepareBottomNavigation() {

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_setlists);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                switch ( id ) {

                    case R.id.action_setlists:
                        return true;

                    case R.id.action_songs:
                        setlistsNavigator.toSongs();
                        return true;

                    case R.id.action_settings:
                        return true;

                }

                return false;

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        getSetlists();
    }

    @Override
    protected void onStop() {
        super.onStop();
        disposable.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
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

            SetlistRecyclerViewAdapter adapter = (SetlistRecyclerViewAdapter)recyclerView.getAdapter();
            position = adapter.getPosition();

        } catch (Exception e) {

            Log.e(TAG, "Error getting setlist list item");
            e.printStackTrace();

        }

        return position;
    }

    @Override
    public void onItemClick(int position) {

        Setlist setlist = dataset.get(position);

        if ( setlist != null ) {

            Map<String, String> extras = new HashMap<>();

            extras.put(SetlistSongsActivity.SETLIST_ID, setlist.getId());
            extras.put(SetlistSongsActivity.SETLIST_NAME, setlist.getName());


            setlistsNavigator.toSetlistSongs(extras);

        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        int position = getAdapterPosition();

        if ( position == -1 ) {
            return false;
        }

        Setlist setlist = dataset.get(position);

        switch ( item.getItemId() ) {

            case R.id.edit:
                setlistsNavigator.editSetlist(setlist.getId());
                return true;

            case R.id.duplicate:
                duplicateSetlist(setlist);
                return true;

            case R.id.remove:
                handleRemove(setlist);
                return true;

            default:
                return super.onContextItemSelected(item);
        }

    }

    private void handleRemove(final Setlist setlist ) {

        String msg = setlist.getName() + " " + getResources().getString(R.string.common_deleted);

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
                    deleteSetlist(setlist.getId());
                }

            }

        });

        snackbar.show();

    }

    private void getSetlists() {

        disposable.add(setlistsViewModel.getSetlists()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Setlist>>() {

                    @Override
                    public void accept(List<Setlist> setlists) throws Exception {

                        dataset.clear();
                        dataset.addAll(setlists);
                        adapter.notifyDataSetChanged();

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "Unable to get setlists", throwable);
                    }
                }));

    }

    private void duplicateSetlist(Setlist setlist) {

        disposable.add(
                setlistsViewModel.duplicateSetlist(setlist)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action() {
                            @Override
                            public void run() throws Exception {

                                Log.i(TAG, "Setlist duplicated successfully");

                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {

                                Log.e(TAG, "Error duplicating setlist");

                            }
                        })
        );

    }

    private void deleteSetlist(String setlistId) {

        disposable.add(
                setlistsViewModel.deleteSetlist(setlistId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action() {
                            @Override
                            public void run() throws Exception {

                                Log.i(TAG, "Setlist deleted successfully");

                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.e(TAG, "Error deleting setlist", throwable);
                            }
                        })
        );

    }



}