package setlistmanager.main;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import setlistmanager.Injection;
import setlistmanager.ViewModelFactory;
import setlistmanager.data.Song;
import setlistmanager.setlist.AddEditSetlistActivity;
import setlistmanager.song.AddEditSongActivity;
import setlistmanager.song.SongRecyclerViewAdapter;
import setlistmanager.song.SongsFragment;
import setlistmanager.util.BaseNavigator;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int TAB_POSITION_SETLISTS = 0;
    private static final int TAB_POSITION_SONGS = 1;
    private static final int TAB_NUM_COUNT = 2;
    public static final String TAB_POSITION_KEY = "tabPos";


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private ViewModelFactory viewModelFactory;
    private MainActivityNavigator mainActivityNavigator;
    private MainActivityViewModel mainActivityViewModel;

    private List<Song> allSongs;
    private List<Song> dataset;

    private CompositeDisposable disposable = new CompositeDisposable();

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        allSongs = new ArrayList<>();
        dataset = new ArrayList<>();

        viewModelFactory = Injection.provideViewModelFactory(this, this);
        mainActivityViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel.class);
        mainActivityNavigator = mainActivityViewModel.getMainActivityNavigator();

        sharedPreferences = this.getSharedPreferences(TAB_POSITION_KEY, Context.MODE_PRIVATE);


    }

    private void init() {

        Log.i(TAG, "init...");

        List<Fragment> fragments = new ArrayList<>();
        SongsFragment songsFragment = SongsFragment.newInstance(dataset);

        PlaceholderFragment placeholderFragment = PlaceholderFragment.newInstance(1);

        fragments.add(placeholderFragment);
        fragments.add(songsFragment);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), fragments);

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
    protected void onStart() {
        super.onStart();
        getSongs();
    }

    private void getSongs() {

        disposable.add(
                mainActivityViewModel.getSongs()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<Song>>() {

                            @Override
                            public void accept(List<Song> songs) throws Exception {

                                Log.i(TAG, "get songs DONE");

                                allSongs.clear();
                                allSongs.addAll(songs);
                                dataset.clear();
                                dataset.addAll(songs);
                                init();

                            }
                        }, new Consumer<Throwable>() {

                            @Override
                            public void accept(Throwable throwable) throws Exception {

                                Log.e(TAG, "Unable to get songs", throwable);

                            }

                        })

        );

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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        List<Fragment> fragments;

        public SectionsPagerAdapter(FragmentManager fm, List<Fragment> fragments) {

            super(fm);
            this.fragments = fragments;

        }

        @Override
        public Fragment getItem(int position) {

            Log.i(TAG, "TAB POSITION = " + position);

            return this.fragments.get(position);
            /*
            if ( position == TAB_POSITION_SETLISTS ) {
                return this.fragments.get(TAB_POSITION_SETLISTS);
            } else {
                return this.fragments.get(TAB_POSITION_SONGS);
            }
            */

        }

        @Override
        public int getCount() {
            return TAB_NUM_COUNT;
        }

    }
}
