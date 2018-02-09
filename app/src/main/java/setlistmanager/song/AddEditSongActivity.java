package setlistmanager.song;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.setlistmanager.R;

import java.util.Date;

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
import setlistmanager.util.FileUtil;

public class AddEditSongActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_SONG = 1;
    public static final int REQUEST_EDIT_SONG = 2;

    public static final String EXTRA_SONG_ID = "id";

    private static final String TAG = AddEditSongActivity.class.getSimpleName();

    private ViewModelFactory viewModelFactory;

    private AddEditSongViewModel addEditSongViewModel;

    private AddEditSongNavigator addEditSongNavigator;

    private final CompositeDisposable disposable = new CompositeDisposable();

    private EditText title;

    private EditText artist;

    private EditText filepath;

    private String songUri;

    private Button selectFileButton;

    private Button saveButton;

    private Button cancelButton;

    private Snackbar snackbarFail;

    private Toast toastSaveFailed;

    private Toast toastNotSupported;

    private String songId;

    private Song song;

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
        filepath = (EditText) findViewById(R.id.song_file_path);
        selectFileButton = (Button) findViewById(R.id.button_select_file);
        saveButton = (Button) findViewById(R.id.button_save);
        cancelButton = (Button) findViewById(R.id.button_cancel);

        snackbarFail = Snackbar.make(findViewById(R.id.addedit_song_layout), getResources().getText(R.string.addedit_song_title_cannot_be_emtpy), Snackbar.LENGTH_LONG);
        toastSaveFailed = Toast.makeText(getApplicationContext(), getResources().getText(R.string.addedit_save_failed), Toast.LENGTH_LONG);

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

                String path = FileUtil.getPathFromUri(getApplicationContext(), uri);
                filepath.setText(path);

            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
        String songFilepath = filepath.getText().toString();

        if ( songTitle == null || songTitle.isEmpty() || songFilepath == null || songFilepath.isEmpty() ) {
            //snackbarFail.show();
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
                                    String path = FileUtil.getPathFromUri(getApplicationContext(), Uri.parse( song.getUri()));
                                    filepath.setText( path );

                                }

                            }

                        }

                        @Override
                        public void onError(Throwable e) {

                            Log.e(TAG, "Error fetching song", e);
                        }
                    });


    }

    private void saveSong(Song song, String title, String artist, String uri) {

        Log.i(TAG, "title = " + title + ", artist = " + artist + ", uri = " + uri);

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

                            }
                        })
        );

    }

}
