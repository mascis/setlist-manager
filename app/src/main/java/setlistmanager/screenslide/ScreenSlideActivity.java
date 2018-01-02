package setlistmanager.screenslide;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.setlistmanager.R;

import java.util.ArrayList;
import java.util.List;

import setlistmanager.data.Song;
import setlistmanager.util.FileUtil;

public class ScreenSlideActivity extends FragmentActivity {

    private static final String TAG = ScreenSlideActivity.class.getSimpleName();

    public static final String EXTRA_START_POSITION = "start_position";
    public static final String EXTRA_NUM_PAGES = "num_pages";
    public static final String EXTRA_NUM_ITEMS = "items";

    private int start_position = 0;

    private int num_pages = 0;

    private List<Song> songs;

    private ViewPager viewPager;

    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);

        if ( FileUtil.isExternalStorageReadable() ) {

            if ( !FileUtil.hasPermissionToReadExternalStorage(this) ) {

                Log.i(TAG, "No permission to read external storage. Requesting permission...");

                /*
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    Log.i(TAG, "shouldShowRequestPermissionRationale...");

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {
                */
                    try {

                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, FileUtil.PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                    } catch (Exception e) {

                        e.printStackTrace();
                        throw e;

                    }

                //}

            } else {

                init();

            }

        } else {

            Log.i(TAG, "External storage not readable");

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case FileUtil.PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    init();

                } else {

                    finish();

                }

                return;
            }

        }

    }

    @Override
    public void onBackPressed() {

        if ( viewPager == null ) {

            super.onBackPressed();

        } else {

            if (viewPager.getCurrentItem() == 0) {

                super.onBackPressed();

            } else {

                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);

            }
        }

    }

    private List<Fragment> getFragments() {

        List<Fragment> fragments = new ArrayList<>();

        if ( songs == null ) {

            ScreenSlidePageFragmentEmpty screenSlidePageFragmentEmpty = new ScreenSlidePageFragmentEmpty();
            fragments.add(screenSlidePageFragmentEmpty);
            return fragments;

        }

        for( int i = 0; i < songs.size(); i++ ) {

            Song song = songs.get(i);
            String uriString = song.getUri();
            Uri uri = Uri.parse(uriString);

            if ( uri != null && uriString != null ) {

                if (FileUtil.isImage(getApplicationContext(), uri) ) {

                    ScreenSlidePageFragmentImg screenSlidePageFragmentImg = ScreenSlidePageFragmentImg.newInstance(getApplicationContext(), uriString);
                    fragments.add(screenSlidePageFragmentImg);

                } else if (FileUtil.isPlainText(getApplicationContext(), uri) ) {

                    ScreenSlidePageFragmentTxt screenSlidePageFragmentTxt = ScreenSlidePageFragmentTxt.newInstance(getApplicationContext(), uri);
                    fragments.add(screenSlidePageFragmentTxt);

                } else if (FileUtil.isPdf(getApplicationContext(), uri) ) {

                    ScreenSlidePageFragmentPdf screenSlidePageFragmentPdf = ScreenSlidePageFragmentPdf.newInstance(getApplicationContext(), uriString);
                    fragments.add(screenSlidePageFragmentPdf);

                } else {

                    ScreenSlidePageFragmentEmpty screenSlidePageFragmentEmpty = new ScreenSlidePageFragmentEmpty();
                    fragments.add(screenSlidePageFragmentEmpty);

                }

            } else {

                ScreenSlidePageFragmentEmpty screenSlidePageFragmentEmpty = new ScreenSlidePageFragmentEmpty();
                fragments.add(screenSlidePageFragmentEmpty);

            }

        }

        return fragments;

    }

    private void init() {

        Bundle extras = getIntent().getExtras();

        if ( extras.getString(EXTRA_NUM_PAGES) != null ) {

            num_pages = Integer.parseInt(extras.getString(EXTRA_NUM_PAGES));

        }

        if ( extras.getSerializable(EXTRA_NUM_ITEMS) != null ) {

            songs = (List) extras.getSerializable(EXTRA_NUM_ITEMS);

        }

        viewPager = (ViewPager) findViewById(R.id.pager);
        List<Fragment> fragments = getFragments();

        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(pagerAdapter);

        if ( extras.getString(EXTRA_START_POSITION) != null ) {

            start_position = Integer.parseInt(extras.getString(EXTRA_START_POSITION));
            viewPager.setCurrentItem(start_position);

        }

    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> fragments;

        public ScreenSlidePagerAdapter(FragmentManager fragmentManager, List<Fragment> fragments) {
            super(fragmentManager);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {

            return this.fragments.get(position);

        }

        @Override
        public int getCount() {
            return num_pages;
        }

    }

}
