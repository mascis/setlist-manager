package setlistmanager.setlist;

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

public class AddEditSetlistActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_SETLLIST = 1;
    public static final int REQUEST_EDIT_SETLIST = 2;

    private static final String TAG = AddEditSetlistActivity.class.getSimpleName();

    private ViewModelFactory viewModelFactory;

    private AddEditSetlistViewModel addEditSetlistViewModel;

    private AddEditSetlistNavigator addEditSetlistNavigator;

    private final CompositeDisposable disposable = new CompositeDisposable();

    private EditText setlistName;

    private EditText setlistLocation;

    private DatePicker setlistDate;

    private Button saveButton;

    private Button cancelButton;

    private Snackbar snackbarFail;

    private String setlistId;

    private Setlist setlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_setlist);

        viewModelFactory = Injection.provideViewModelFactory(this, this);
        addEditSetlistViewModel = ViewModelProviders.of(this, viewModelFactory).get(AddEditSetlistViewModel.class);
        addEditSetlistNavigator = addEditSetlistViewModel.getAddEditSetlistNavigator();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        setlistId = getIntent().getStringExtra("id");

        if ( setlistId != null ) {
            actionBar.setTitle(getResources().getString(R.string.edit_setlist_title));
            addEditSetlistViewModel.setEditMode(true);
            getSetlistById(setlistId);
        } else {
            actionBar.setTitle(getResources().getString(R.string.add_setlist_title));
        }

        setlistName = (EditText) findViewById(R.id.setlist_name);
        setlistLocation = (EditText) findViewById(R.id.setlist_location);
        setlistDate = (DatePicker) findViewById(R.id.setlist_date);
        saveButton = (Button) findViewById(R.id.button_save);
        cancelButton = (Button) findViewById(R.id.button_cancel);

        snackbarFail = Snackbar.make(findViewById(R.id.addedit_setlist_layout), getResources().getText(R.string.addedit_setlist_name_cannot_be_emtpy), Snackbar.LENGTH_LONG);

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
        addEditSetlistNavigator.onCancel();
    }

    public void onSaveClicked() {

        String name = setlistName.getText().toString();

        if ( name == null || name.isEmpty() ) {
            Log.e(TAG, "Name cannot be empty");
            snackbarFail.show();
            return;
        }

        String location = setlistLocation.getText().toString();
        int date = setlistDate.getDayOfMonth();
        int month = setlistDate.getMonth();
        int year = setlistDate.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date);

        if ( addEditSetlistViewModel.isEditMode() ) {
            saveSetlist(setlist, name, location, calendar.getTime());
        } else {
            saveSetlist(null, name, location, calendar.getTime());
        }

    }

    private void getSetlistById(String id) {

        addEditSetlistViewModel.getSetlistById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Setlist>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Setlist _setlist) {

                        setlist = _setlist;

                        if ( !setlist.equals(null) ) {
                            setlistName.setText(setlist.getName());

                            if ( !setlist.getLocation().equals(null) ) {
                                setlistLocation.setText(setlist.getLocation());
                            }

                            if ( !setlist.getDate().equals(null) ) {

                                Calendar c = Calendar.getInstance();
                                c.setTime(setlist.getDate());
                                int year = c.get(Calendar.YEAR);
                                int month = c.get(Calendar.MONTH);
                                int day = c.get(Calendar.DATE);
                                setlistDate.updateDate(year, month, day);

                            }
                        }

                    }

                    @Override
                    public void onError(Throwable e) {

                        Log.e(TAG, "onError");
                        Log.i(TAG, "error fetching setlist");

                    }
                });

    }

    private void saveSetlist(Setlist setlist, String name, String location, Date date) {

        disposable.add(
                addEditSetlistViewModel.saveSetlist(setlist, name, location, date)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                new Action() {
                                    @Override
                                    public void run() throws Exception {

                                        addEditSetlistNavigator.onSetlistSaved();

                                    }
                                },
                                new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        Log.e(TAG, "Unable to add setlist", throwable);
                                    }
                                }
                        ));

    }

}
