package setlistmanager.setlist;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.setlistmanager.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import setlistmanager.Injection;
import setlistmanager.data.Setlist;
import setlistmanager.data.Song;
import setlistmanager.data.source.local.LocalDataSource;
import setlistmanager.screenslide.ScreenSlideActivity;
import setlistmanager.song.SongRecyclerViewAdapter;
import setlistmanager.song.SongsNavigator;
import setlistmanager.song.SongsViewModel;
import setlistmanager.util.BaseNavigator;

public class SetlistsFragment extends Fragment implements SetlistRecyclerViewAdapter.SetlistItemClickListener {

    private static final String TAG = SetlistsFragment.class.getSimpleName();

    private static final String ARG_DATASET = "dataset";

    private List<Setlist> dataset;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private SetlistsNavigator setlistsNavigator;

    private Toast toastDeleteSuccessful;
    private Toast toastDeleteFailed;

    private CompositeDisposable disposable = new CompositeDisposable();
    private SetlistsViewModel setlistsViewModel;

    //private OnFragmentInteractionListener mListener;

    public SetlistsFragment() {
        // Required empty public constructor
    }

    public static SetlistsFragment newInstance(List<Setlist> setlists) {

        SetlistsFragment fragment = new SetlistsFragment();

        Bundle args = new Bundle();

        args.putSerializable(ARG_DATASET, (Serializable) setlists);

        fragment.setArguments(args);

        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

            dataset = (List<Setlist>) getArguments().getSerializable(ARG_DATASET);

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View setlistsView = inflater.inflate(R.layout.fragment_setlists, container, false);

        BaseNavigator baseNavigator = Injection.provideNavigator(getActivity());
        setlistsNavigator = new SetlistsNavigator(baseNavigator);

        LocalDataSource localDataSource = Injection.provideLocalDataSource(getContext());

        setlistsViewModel = new SetlistsViewModel(localDataSource, setlistsNavigator);

        toastDeleteSuccessful = Toast.makeText(getContext(), getResources().getText(R.string.song_deleted_successfully), Toast.LENGTH_LONG);
        toastDeleteFailed = Toast.makeText(getContext(), getResources().getText(R.string.song_deleted_successfully), Toast.LENGTH_LONG);

        recyclerView = (RecyclerView) setlistsView.findViewById(R.id.recyclerViewSetlists);
        recyclerView.setHasFixedSize(true);
        adapter = new SetlistRecyclerViewAdapter(getContext(), dataset, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        return setlistsView;

    }

    private int getAdapterPosition() {

        int position = -1;

        try {

            SetlistRecyclerViewAdapter adapter = (SetlistRecyclerViewAdapter) recyclerView.getAdapter();
            position = adapter.getPosition();

        } catch (Exception e) {

            Log.e(TAG, "Error getting setlist list item");
            e.printStackTrace();

        }

        return position;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if ( item.getGroupId() != 0 ) {
            return false;
        }

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
                handleRemove( setlist );
                return true;

            default:
                return super.onContextItemSelected(item);
        }

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

    private void handleRemove(final Setlist setlist ) {

        String msg = getResources().getString(R.string.confirm_dialog_delete_setlist_message);

        final Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);

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
                                adapter.notifyDataSetChanged();

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
    public void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        disposable.clear();
    }

}
