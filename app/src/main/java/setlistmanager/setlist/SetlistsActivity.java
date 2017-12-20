package setlistmanager.setlist;

import android.app.DialogFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import setlistmanager.Injection;
import setlistmanager.ViewModelFactory;
import setlistmanager.data.Setlist;
import setlistmanager.util.ConfirmDialogFragment;

import com.setlistmanager.R;

public class SetlistsActivity extends AppCompatActivity implements ConfirmDialogFragment.ConfirmDialogListener, SetlistRecyclerViewAdapter.ItemClickListener {

    private static final String TAG = SetlistsActivity.class.getSimpleName();

    private ViewModelFactory viewModelFactory;

    private SetlistsViewModel setlistsViewModel;

    private SetlistsNavigator setlistsNavigator;

    private final CompositeDisposable disposable = new CompositeDisposable();

    private RecyclerView recyclerView;

    private FloatingActionButton floatingActionButton;

    private RecyclerView.Adapter adapter;

    private RecyclerView.LayoutManager layoutManager;

    private List<Setlist> dataset;

    private DrawerLayout drawerLayout;

    private NavigationView navigationView;

    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setlists);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.setlists_title));

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
        setlistsViewModel = ViewModelProviders.of(this, viewModelFactory).get(SetlistsViewModel.class);
        setlistsNavigator = setlistsViewModel.getSetlistsNavigator();

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_add);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setlistsNavigator.addSetlist();
            }
        });

        dataset = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        adapter = new SetlistRecyclerViewAdapter(this, dataset);
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
        getSetlists();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    private boolean selectDrawerItem(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.nav_setlists:
                Log.i(TAG, "Setlists clicked in nav menu");
                return true;

            case R.id.nav_songs:
                Log.i(TAG, "Songs clicked in nav menu");
                setlistsNavigator.toSongs();
                return true;

            case R.id.nav_settings:
                Log.i(TAG, "Settings clicked in nav menu");
                return true;

            default:
                return true;

        }
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

        if ( drawerToggle.onOptionsItemSelected(item)){
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
                showConfirmDialog(setlist);
                return true;

            default:
                return super.onContextItemSelected(item);
        }

    }

    public void showConfirmDialog(Setlist setlist) {

        String title = getResources().getString(R.string.confirm_dialog_delete_setlist_title);
        String question = getResources().getString(R.string.confirm_dialog_delete_setlist_message);
        String setlistName = setlist.getName();
        String message = question + " " + setlistName + "?";

        DialogFragment confirmDialog = ConfirmDialogFragment.instance(title, message);
        confirmDialog.show(getFragmentManager(), "ConfirmDialog");

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

        Setlist setlist;

        try {
            setlist = dataset.get(getAdapterPosition());
            deleteSetlist(setlist.getId());
        } catch (Exception e) {
            Log.e(TAG, "Deleting setlist failed");
        }

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

        dialog.dismiss();

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

    @Override
    protected void onStop() {
        super.onStop();

        disposable.clear();
    }

}