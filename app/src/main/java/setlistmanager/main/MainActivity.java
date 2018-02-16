package setlistmanager.main;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.setlistmanager.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import setlistmanager.Injection;
import setlistmanager.ViewModelFactory;
import setlistmanager.data.Setlist;
import setlistmanager.data.Song;
import setlistmanager.setlist.AddEditSetlistActivity;
import setlistmanager.setlist.SetlistsFragment;
import setlistmanager.song.AddEditSongActivity;
import setlistmanager.song.SongRecyclerViewAdapter;
import setlistmanager.song.SongsFragment;
import setlistmanager.util.BaseNavigator;

public class MainActivity extends AppCompatActivity implements SetlistsFragment.OnDataChangedListener, SongsFragment.OnDataChangedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int TAB_POSITION_SETLISTS = 0;
    private static final int TAB_POSITION_SONGS = 1;
    public static final String TAB_POSITION_KEY = "tabPos";
    public static final int RESULT_CODE_DATA_CHANGED = 200;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    private ViewModelFactory viewModelFactory;
    private MainActivityNavigator mainActivityNavigator;
    private MainActivityViewModel mainActivityViewModel;

    private List<Song> allSongs;
    private List<Song> datasetSongs;

    private List<Setlist> datasetSetlists;

    private CompositeDisposable disposable = new CompositeDisposable();

    private SharedPreferences sharedPreferences;

    private SongsFragment songsFragment;
    private SetlistsFragment setlistsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        allSongs = new ArrayList<>();
        datasetSongs = new ArrayList<>();
        datasetSetlists = new ArrayList<>();

        viewModelFactory = Injection.provideViewModelFactory(this, this);
        mainActivityViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel.class);
        mainActivityNavigator = mainActivityViewModel.getMainActivityNavigator();

        sharedPreferences = this.getSharedPreferences(TAB_POSITION_KEY, Context.MODE_PRIVATE);

        new GetSetlistsAsyncTask().execute();
        new GetSongsAsyncTask().execute();

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        int currentItem = mViewPager.getCurrentItem();

        Fragment currentFragment = mSectionsPagerAdapter.getItem(currentItem);

        return currentFragment.onContextItemSelected(item);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode == AddEditSongActivity.RESULT_CODE_DATA_CHANGED ) {
            new GetSongsAsyncTask().execute();
        } else if ( resultCode == AddEditSetlistActivity.RESULT_CODE_DATA_CHANGED ) {
            new GetSetlistsAsyncTask().execute();
        }

    }

    private List<Fragment> getFragments() {

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(getSetlistsFragment());
        fragments.add(getSongsFragment());

        return fragments;

    }

    private void init() {

        if ( getSetlistsFragment() == null || getSongsFragment() == null ) {
            return;
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), getFragments());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                sharedPreferences.edit().putInt(TAB_POSITION_KEY, tab.getPosition()).commit();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int current = mViewPager.getCurrentItem();

                if ( current == TAB_POSITION_SETLISTS ) {

                    mainActivityNavigator.addSetlist();

                } else if ( current == TAB_POSITION_SONGS ) {

                    mainActivityNavigator.addSong();

                }

            }
        });

        mViewPager.setCurrentItem(sharedPreferences.getInt(TAB_POSITION_KEY, 0));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public SongsFragment getSongsFragment() {
        return songsFragment;
    }

    public void setSongsFragment(SongsFragment songsFragment) {
        this.songsFragment = songsFragment;
    }

    public SetlistsFragment getSetlistsFragment() {
        return setlistsFragment;
    }

    public void setSetlistsFragment(SetlistsFragment setlistsFragment) {
        this.setlistsFragment = setlistsFragment;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        List<Fragment> fragments;

        public SectionsPagerAdapter(FragmentManager fm, List<Fragment> fragments) {

            super(fm);
            this.fragments = fragments;

        }

        @Override
        public Fragment getItem(int position) {

            return this.fragments.get(position);

        }

        @Override
        public int getCount() {

            if ( this.fragments == null ) {
                return 0;
            }

            return this.fragments.size();
        }

    }

    private class GetSetlistsAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            super.onPostExecute(aVoid);
            SetlistsFragment setlistsFragment = SetlistsFragment.newInstance(datasetSetlists);
            setSetlistsFragment(setlistsFragment);
            init();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            disposable.add(
                    mainActivityViewModel.getSetlists()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<List<Setlist>>() {

                                @Override
                                public void accept(List<Setlist> setlists) throws Exception {

                                    datasetSetlists.clear();
                                    datasetSetlists.addAll(setlists);

                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Log.e(TAG, "Unable to get setlists", throwable);
                                }
                            }));

            return null;

        }

    }

    private class GetSongsAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            super.onPostExecute(aVoid);
            SongsFragment songsFragment = SongsFragment.newInstance(datasetSongs);
            setSongsFragment(songsFragment);
            init();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            disposable.add(
                    mainActivityViewModel.getSongs()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<List<Song>>() {

                                @Override
                                public void accept(List<Song> songs) throws Exception {

                                    allSongs.clear();
                                    allSongs.addAll(songs);
                                    datasetSongs.clear();
                                    datasetSongs.addAll(songs);

                                }
                            }, new Consumer<Throwable>() {

                                @Override
                                public void accept(Throwable throwable) throws Exception {

                                    Log.e(TAG, "Unable to get songs", throwable);

                                }

                            })

            );

            return null;
        }
    }

    @Override
    public void onDataChanged() {
        Log.i(TAG, "onDataChanged...");
        new GetSetlistsAsyncTask().execute();
    }

    @Override
    public void onSongsDataChanged() {
        Log.i(TAG, "onSongsDataChanged...");
        new GetSongsAsyncTask().execute();
    }
}
