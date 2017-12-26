package setlistmanager.setlist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.setlistmanager.R;

import java.util.List;

import setlistmanager.data.Song;


/**
 * Created by User on 15.12.2017.
 */

public class SetlistSongsRecyclerViewAdapter extends RecyclerView.Adapter<SetlistSongsRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = SetlistSongsRecyclerViewAdapter.class.getSimpleName();

    public interface ItemClickListener {
        public void onItemClick(int position);
    }

    private ItemClickListener itemClickListener;
    private List<Song> dataset;
    private Context context;
    private int position;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        public TextView title;
        public TextView artist;

        public ViewHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.songs_list_item_title);
            this.artist = (TextView) view.findViewById(R.id.songs_list_item_artist);
            view.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

            // groupId, itemId, order, titleRes
            contextMenu.add(Menu.NONE, R.id.edit, Menu.NONE, R.string.context_menu_edit);
            contextMenu.add(Menu.NONE, R.id.remove, Menu.NONE, R.string.context_menu_remove);

        }
    }

    public SetlistSongsRecyclerViewAdapter(Context context, List<Song> dataset) {

        this.context = context;
        this.dataset = dataset;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        itemClickListener = (ItemClickListener) parent.getContext();

        View textView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.songs_list_item, parent, false);

        return new ViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onItemClick(holder.getLayoutPosition());
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
            String title = song.getTitle();
            String artist = song.getArtist() == null ? "" : song.getArtist();

            holder.title.setText(title);
            holder.artist.setText(artist);

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
        super.onViewRecycled(holder);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}