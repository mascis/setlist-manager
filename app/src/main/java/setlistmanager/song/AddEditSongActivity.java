package setlistmanager.song;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.setlistmanager.R;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.reactivex.disposables.CompositeDisposable;
import setlistmanager.Injection;
import setlistmanager.ViewModelFactory;
import setlistmanager.data.Song;
import setlistmanager.util.FileUtil;

public class AddEditSongActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_SONG = 1;
    public static final int REQUEST_EDIT_SONG = 2;

    private static final String TAG = AddEditSongActivity.class.getSimpleName();

    private ViewModelFactory viewModelFactory;

    private AddEditSongViewModel addEditSongViewModel;

    private AddEditSongNavigator addEditSongNavigator;

    private final CompositeDisposable disposable = new CompositeDisposable();

    private EditText title;

    private EditText artist;

    private EditText uri;

    private Button selectFileButton;

    private Button saveButton;

    private Button cancelButton;

    private Snackbar snackbarFail;

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

        songId = getIntent().getStringExtra("id");

        if ( songId != null ) {

            actionBar.setTitle(getResources().getString(R.string.edit_song_title));
            addEditSongViewModel.setEditMode(true);
            getSongById(songId);

        } else {

            actionBar.setTitle(getResources().getString(R.string.add_song_title));

        }

        title = (EditText) findViewById(R.id.song_title);
        artist = (EditText) findViewById(R.id.song_artist);
        saveButton = (Button) findViewById(R.id.button_save);
        cancelButton = (Button) findViewById(R.id.button_cancel);
        selectFileButton = (Button) findViewById(R.id.button_select_file);

        snackbarFail = Snackbar.make(findViewById(R.id.addedit_song_layout), getResources().getText(R.string.addedit_song_title_cannot_be_emtpy), Snackbar.LENGTH_LONG);

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

        if ( songTitle == null || songTitle.isEmpty() ) {
            Log.e(TAG, "Title cannot be empty");
            snackbarFail.show();
            return;
        }

        if ( addEditSongViewModel.isEditMode() ) {
            saveSong();
        } else {
            saveSong();
        }

    }

    private void getSongById(String id) {

    }

    private void saveSong() {

    }

}
