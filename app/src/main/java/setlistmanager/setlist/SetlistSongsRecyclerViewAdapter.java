package setlistmanager.setlist;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.setlistmanager.R;

import java.util.List;

import setlistmanager.data.Song;
import setlistmanager.helper.ItemTouchHelperViewHolder;
import setlistmanager.helper.OnStartDragListener;


/**
 * Created by User on 15.12.2017.
 */

public class SetlistSongsRecyclerViewAdapter extends RecyclerView.Adapter<SetlistSongsRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = SetlistSongsRecyclerViewAdapter.class.getSimpleName();

    public interface ItemClickListener {
        void onItemClick(int position);
    }

    private ItemClickListener itemClickListener;
    private List<Song> dataset;
    private Context context;
    private int position;
    private final OnStartDragListener dragStartListener;

    public SetlistSongsRecyclerViewAdapter(Context context, List<Song> dataset, OnStartDragListener onStartDragListener) {

        this.context = context;
        this.dataset = dataset;
        this.dragStartListener = onStartDragListener;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        itemClickListener = (ItemClickListener) parent.getContext();

        View textView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.setlist_songs_list_item, parent, false);

        return new ViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.dragHandle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if ( event.getAction() == MotionEvent.ACTION_DOWN) {
                    dragStartListener.onStartDrag(holder);
                }

                return false;
            }

        });

        holder.options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPosition(position);
                view.showContextMenu();
            }
        });

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

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, ItemTouchHelperViewHolder {

        public TextView title;
        public TextView artist;
        public TextView options;
        public TextView dragHandle;

        public ViewHolder(View view) {
            super(view);
            this.dragHandle = (TextView) view.findViewById(R.id.songs_list_item_drag_handle);
            this.title = (TextView) view.findViewById(R.id.songs_list_item_title);
            this.artist = (TextView) view.findViewById(R.id.songs_list_item_artist);
            this.options = (TextView) view.findViewById(R.id.options_icon);
            this.itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

            // groupId, itemId, order, titleRes
            contextMenu.add(Menu.NONE, R.id.edit, Menu.NONE, R.string.context_menu_edit);
            contextMenu.add(Menu.NONE, R.id.remove, Menu.NONE, R.string.context_menu_remove);

        }

    }
}
