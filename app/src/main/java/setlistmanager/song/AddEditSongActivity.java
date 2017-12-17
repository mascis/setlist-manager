package setlistmanager.song;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.setlistmanager.R;

import java.util.Calendar;
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
import setlistmanager.data.Setlist;
import setlistmanager.data.Song;

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

    private EditText filepath;

    private Button fileChooserButton;

    private Button imageGalleryButton;

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

        title = (EditText) findViewById(R.id.song_title);
        artist = (EditText) findViewById(R.id.song_artist);
        saveButton = (Button) findViewById(R.id.button_save);
        cancelButton = (Button) findViewById(R.id.button_cancel);

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

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {



        Log.i(TAG, "onActivityResult");
        Log.i(TAG, "requestCode: " + requestCode + ", resultCode: " + resultCode + ", data: " + data.getStringExtra("id"));

        if ( requestCode == REQUEST_EDIT_SONG ) {

            getActionBar().setTitle(getResources().getString(R.string.edit_song_title));

            songId = data.getStringExtra("id");
            addEditSongViewModel.setEditMode(true);
            getSongById(songId);

        } else {

            getActionBar().setTitle(getResources().getString(R.string.add_song_title));

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
