package setlistmanager.setlist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.setlistmanager.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import setlistmanager.data.Song;


/**
 * Created by User on 15.12.2017.
 */

public class AddSongsToSetlistRecyclerViewAdapter extends RecyclerView.Adapter<AddSongsToSetlistRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = AddSongsToSetlistRecyclerViewAdapter.class.getSimpleName();

    private List<Song> dataset;
    private Context context;
    private int position;
    private List<String> selectedSongs = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        public TextView title;
        public TextView artist;
        public CheckBox checkBox;

        public ViewHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.add_songs_list_item_title);
            this.artist = (TextView) view.findViewById(R.id.add_songs_list_item_artist);
            this.checkBox = (CheckBox) view.findViewById(R.id.add_songs_list_item_checkbox);
            view.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

            // groupId, itemId, order, titleRes
            contextMenu.add(Menu.NONE, R.id.edit, Menu.NONE, R.string.context_menu_edit);

        }
    }

    public AddSongsToSetlistRecyclerViewAdapter(Context context, List<Song> dataset) {

        this.context = context;
        this.dataset = dataset;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.add_songs_list_item, parent, false);

        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                if ( checked ) {

                    setSelected(dataset.get(position).getId());

                } else {

                    removeSelected(dataset.get(position).getId());

                }

            }

        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                setPosition(holder.getLayoutPosition());
                return false;
            }
        });

        if ( dataset != null || !dataset.isEmpty() ) {

            Song song = dataset.get(position);

            if ( song.getTitle() != null ) {
                String title = song.getTitle();
                holder.title.setText(title);
            }

            if ( song.getArtist() != null ) {
                String artist = song.getArtist();
                holder.artist.setText(artist);
            }

            if ( song.getId() != null ) {
                holder.checkBox.setChecked(isSelected(song.getId()));
            }

        }
    }

    @Override
    public int getItemCount() {

        if ( dataset == null ) {
            return 0;
        }

        return dataset.size();

    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        holder.checkBox.setOnCheckedChangeListener(null);
        super.onViewRecycled(holder);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setSelectedSongs(List<String> selectedSongs ) {
        this.selectedSongs = selectedSongs;
    }

    public List<String> getSelectedSongs() {
        return this.selectedSongs;
    }

    private void setSelected(String songId) {

        if ( !isSelected(songId)) {
            this.selectedSongs.add(songId);
        }

    }

    private void removeSelected(String songId) {

        if ( isSelected(songId) ) {
            this.selectedSongs.remove(songId);
        }

    }

    private boolean isSelected( String songId ) {

        if ( this.selectedSongs.contains(songId) ) {
            return true;
        }

        return false;

    }

}
