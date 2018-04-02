package setlistmanager.song;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.setlistmanager.R;

import java.io.File;
import java.io.Serializable;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import setlistmanager.Injection;
import setlistmanager.ViewModelFactory;
import setlistmanager.data.Song;
import setlistmanager.screenslide.ScreenSlideActivity;
import setlistmanager.util.FileUtil;
import setlistmanager.util.Theme;

public class AddEditSongActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_SONG = 1;
    public static final int REQUEST_EDIT_SONG = 2;
    public static final int RESULT_CODE_DATA_CHANGED = 200;

    public static final String EXTRA_SONG_ID = "id";

    private static final String TAG = AddEditSongActivity.class.getSimpleName();

    private ViewModelFactory viewModelFactory;

    private AddEditSongViewModel addEditSongViewModel;

    private AddEditSongNavigator addEditSongNavigator;

    private final CompositeDisposable disposable = new CompositeDisposable();

    private EditText title;

    private EditText artist;

    //private TextView filepath;

    private String songUri;

    private Button selectFileButton;

    private Button saveButton;

    private Button cancelButton;

    private Toast toastSaveFailed;

    private Toast toastSaveError;

    private Toast toastFetchError;

    private Toast toastNotSupported;

    private String songId;

    private Song song;

    private RelativeLayout thumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_song);

        viewModelFactory = Injection.provideViewModelFactory(this, this);
        addEditSongViewModel = ViewModelProviders.of(this, viewModelFactory).get(AddEditSongViewModel.class);
        addEditSongNavigator = addEditSongViewModel.getAddEditSongNavigator();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        songId = getIntent().getStringExtra(EXTRA_SONG_ID);

        if ( songId != null ) {

            actionBar.setTitle(getResources().getString(R.string.edit_song_title));
            addEditSongViewModel.setEditMode(true);
            getSongById(songId);

        } else {

            actionBar.setTitle(getResources().getString(R.string.add_song_title));

        }

        title = (EditText) findViewById(R.id.song_title);
        artist = (EditText) findViewById(R.id.song_artist);
        //filepath = (TextView) findViewById(R.id.song_file_path);
        selectFileButton = (Button) findViewById(R.id.button_select_file);
        saveButton = (Button) findViewById(R.id.button_save);
        cancelButton = (Button) findViewById(R.id.button_cancel);
        createPlaceholderThumbnail();

        toastSaveFailed = Toast.makeText(getApplicationContext(), getResources().getText(R.string.addedit_save_failed), Toast.LENGTH_LONG);
        toastSaveError = Toast.makeText(getApplicationContext(), getResources().getText(R.string.addedit_save_error), Toast.LENGTH_LONG);
        toastFetchError = Toast.makeText(getApplicationContext(), getResources().getText(R.string.addedit_fetch_error), Toast.LENGTH_LONG);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveClicked();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCancelClicked();
            }
        });

        selectFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performFileSearch();
            }
        });

    }

    public void performFileSearch() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        intent.addCategory(Intent.CATEGORY_OPENABLE);

        intent.setType("*/*");

        intent.putExtra(Intent.EXTRA_MIME_TYPES, FileUtil.getSuppportedMimeTypes());

        startActivityForResult(intent, FileUtil.READ_REQUEST_CODE);

    }

    public void createPlaceholderThumbnail() {

        Fragment placeholder = ThumbnailFragmentPlaceholder.newInstance();
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.thumbnail, placeholder);
        fragmentTransaction.commitAllowingStateLoss();

    }

    public void createThumbnail(Context context, Uri uri) {

        Fragment fragment = null;

        if ( FileUtil.isImage(context, uri) ) {

            fragment = ThumbnailFragmentImg.newInstance(context, uri.toString());

        } else if ( FileUtil.isPdf(context, uri) ) {

            fragment = ThumbnailFragmentPdf.newInstance(context, uri.toString());

        } else if (FileUtil.isPlainText(context, uri) ) {

            fragment = ThumbnailFragmentTxt.newInstance(context, uri);

        }

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.thumbnail, fragment);
        fragmentTransaction.commitAllowingStateLoss();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        if ( requestCode == FileUtil.READ_REQUEST_CODE && resultCode == Activity.RESULT_OK ) {

            Uri uri = null;

            if (resultData != null) {

                uri = resultData.getData();

                if ( FileUtil.isGoogleDriveDocument(uri) ) {

                    toastNotSupported = Toast.makeText(getApplicationContext(), "Google Drive not yet supported", Toast.LENGTH_SHORT);
                    toastNotSupported.show();
                    return;

                }

                songUri = uri.toString();

                createThumbnail(getApplicationContext(), uri);

                //String path = FileUtil.getPathFromUri(getApplicationContext(), uri);
                //filepath.setText(path);

            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        title = (EditText) findViewById(R.id.song_title);
        artist = (EditText)findViewById(R.id.song_artist);

        title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if ( b ) {

                    Theme.setEditTextBorderFocus(view);

                } else {

                    Theme.setEditTextBorderNormal(view);
                }
            }
        });

        artist.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if ( b ) {

                    Theme.setEditTextBorderFocus(view);

                } else {

                    Theme.setEditTextBorderNormal(view);
                }
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        disposable.clear();
    }

    public void onCancelClicked() {
        addEditSongNavigator.onCancel();
    }

    public void onSaveClicked() {

        String songTitle = title.getText().toString();
        String songArtist = artist.getText().toString();
        //String songFilepath = filepath.getText().toString();

        /*
        if ( songTitle == null || songTitle.isEmpty() || songFilepath == null || songFilepath.isEmpty() ) {
            toastSaveFailed.show();
            return;
        }
        */

        if ( songTitle == null || songTitle.isEmpty() || songUri == null || songUri.isEmpty() ) {
            toastSaveFailed.show();
            return;
        }

        if ( addEditSongViewModel.isEditMode() ) {
            saveSong(song, songTitle, songArtist, songUri);
        } else {
            saveSong(null, songTitle, songArtist, songUri);
        }

    }

    private void getSongById(String id) {

            addEditSongViewModel.getSongById(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Song>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(Song _song) {

                            song = _song;

                            if ( song != null ) {

                                title.setText(song.getTitle());

                                if( song.getArtist() != null ) {
                                    artist.setText(song.getArtist());
                                }

                                if( song.getUri() != null ) {

                                    songUri = song.getUri();
                                    //String path = FileUtil.getPathFromUri(getApplicationContext(), Uri.parse( song.getUri()));
                                    //filepath.setText( path );

                                }

                            }

                        }

                        @Override
                        public void onError(Throwable e) {

                            Log.e(TAG, "Error fetching song", e);
                            toastFetchError.show();

                        }

                    });

    }

    private void saveSong(Song song, String title, String artist, String uri) {

        disposable.add(
                addEditSongViewModel.saveSong(song, title, artist, uri)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action() {
                            @Override
                            public void run() throws Exception {

                                Log.i(TAG, "Song saved successfully");
                                addEditSongNavigator.onSongSaved();

                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {

                                Log.e(TAG, "Error saving song", throwable);
                                toastSaveError.show();

                            }
                        })
        );

    }

}
