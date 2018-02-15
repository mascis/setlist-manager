package setlistmanager.song;

import android.app.DialogFragment;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.TextView;
import android.widget.Toast;

import com.setlistmanager.R;

import org.apache.xmlbeans.impl.xb.xsdschema.BlockSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import setlistmanager.Injection;
import setlistmanager.ViewModelFactory;
import setlistmanager.data.Setlist;
import setlistmanager.data.Song;
import setlistmanager.data.source.local.LocalDataSource;
import setlistmanager.screenslide.ScreenSlideActivity;
import setlistmanager.util.BaseNavigator;
import setlistmanager.util.ConfirmDialogFragment;

public class SongsFragment extends Fragment implements SongRecyclerViewAdapter.ItemClickListener {

    private static final String TAG = SongsFragment.class.getSimpleName();

    private static final String ARG_DATASET = "dataset";

    private List<Song> dataset;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private SongsNavigator songsNavigator;

    private Toast toastDeleteSuccessful;
    private Toast toastDeleteFailed;

    private CompositeDisposable disposable = new CompositeDisposable();
    private SongsViewModel songsViewModel;

    //private OnFragmentInteractionListener mListener;

    public SongsFragment() {
        // Required empty public constructor
    }

    public static SongsFragment newInstance(List<Song> songs) {

        SongsFragment fragment = new SongsFragment();

        Bundle args = new Bundle();

        args.putSerializable(ARG_DATASET, (Serializable) songs);

        fragment.setArguments(args);

        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

            dataset = (List<Song>) getArguments().getSerializable(ARG_DATASET);

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View songsView = inflater.inflate(R.layout.fragment_songs, container, false);

        BaseNavigator baseNavigator = Injection.provideNavigator(getActivity());
        songsNavigator = new SongsNavigator(baseNavigator);

        LocalDataSource localDataSource = Injection.provideLocalDataSource(getContext());

        songsViewModel = new SongsViewModel(localDataSource, songsNavigator);

        toastDeleteSuccessful = Toast.makeText(getContext(), getResources().getText(R.string.song_deleted_successfully), Toast.LENGTH_LONG);
        toastDeleteFailed = Toast.makeText(getContext(), getResources().getText(R.string.song_deleted_successfully), Toast.LENGTH_LONG);

        recyclerView = (RecyclerView) songsView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        adapter = new SongRecyclerViewAdapter(getContext(), dataset, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();



        return songsView;

    }

    private int getAdapterPosition() {

        int position = -1;

        try {

            SongRecyclerViewAdapter adapter = (SongRecyclerViewAdapter) recyclerView.getAdapter();
            position = adapter.getPosition();

        } catch (Exception e) {

            Log.e(TAG, "Error getting setlist list item");
            e.printStackTrace();

        }

        return position;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        int position = getAdapterPosition();

        if ( position == -1 ) {
            return false;
        }

        Song song = dataset.get(position);

        switch ( item.getItemId() ) {

            case R.id.edit:
                songsNavigator.editSong(song.getId());
                return true;

            case R.id.open:
                toScreenSlide(position);
                return true;

            case R.id.remove:

                handleRemove( song );
                return true;

            default:
                return super.onContextItemSelected(item);
        }

    }

    @Override
    public void onItemClick(int position) {

        toScreenSlide(position);

    }

    private void toScreenSlide(int position) {

        Bundle bundle = new Bundle();

        bundle.putString(ScreenSlideActivity.EXTRA_START_POSITION, String.valueOf(position));
        bundle.putString(ScreenSlideActivity.EXTRA_NUM_PAGES, String.valueOf(dataset.size()));
        bundle.putSerializable(ScreenSlideActivity.EXTRA_NUM_ITEMS, (Serializable) dataset);

        songsNavigator.toScreenSlider(bundle);

    }

    private void handleRemove(final Song song ) {

        String confirm = getResources().getString(R.string.confirm_dialog_delete_song_message);
        String note = getResources().getString(R.string.confirm_dialog_delete_song_message_note);
        String msg = confirm + " " + note;

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
                    //deleteSongFromSetlists(song.getId());
                    //deleteSong(song.getId());
                }

                //super.onDismissed(transientBottomBar, event);
            }
        });

        snackbar.show();

    }

    private void deleteSongFromSetlists(final String songId) {

        disposable.add(
                songsViewModel.getSetlists()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<Setlist>>() {
                            @Override
                            public void accept(List<Setlist> setlists) throws Exception {

                                for ( Setlist setlist : setlists ) {

                                    List<String> songIds = setlist.getSongs();

                                    if ( songIds.contains(songId) ) {

                                        List<String> updatedIds = new ArrayList<>();

                                        for ( String id : songIds ) {

                                            if ( !id.equals(songId) ) {
                                                updatedIds.add(id);
                                            }

                                        }

                                        List<Object> params = new ArrayList<>();
                                        params.add(setlist);
                                        params.add(updatedIds);
                                        new UpdateAsyncTask().execute(params);

                                    }

                                }

                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {

                            }
                        }, new Action() {
                            @Override
                            public void run() throws Exception {

                            }
                        })
        );

    }

    private void deleteSong(String songId) {

        disposable.add(
                songsViewModel.deleteSong(songId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action() {
                            @Override
                            public void run() throws Exception {

                                Log.i(TAG, "Song deleted successfully");
                                toastDeleteSuccessful.show();

                            }
                        }, new Consumer<Throwable>() {

                            @Override
                            public void accept(Throwable throwable) throws Exception {

                                Log.e(TAG, "Error deleting song", throwable);
                                toastDeleteFailed.show();

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

    private class UpdateAsyncTask extends AsyncTask<List<Object>, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(List<Object>[] params) {

            List<Object> list = params[0];

            Setlist setlist = (Setlist)list.get(0);
            List<String> songs = (List<String>) list.get(1);

            songsViewModel.updateSetlistSongs(setlist, songs);

            return null;
        }
    }

    /*
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    */

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    */
}
