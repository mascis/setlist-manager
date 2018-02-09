package setlistmanager.setlist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.setlistmanager.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import setlistmanager.Injection;
import setlistmanager.data.Setlist;


/**
 * Created by User on 15.12.2017.
 */

public class SetlistRecyclerViewAdapter extends RecyclerView.Adapter<SetlistRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = SetlistRecyclerViewAdapter.class.getSimpleName();

    public interface ItemClickListener {
        public void onItemClick(int position);
    }

    private static ItemClickListener itemClickListener;
    private List<Setlist> dataset;
    private Context context;
    private int position;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        public TextView name;
        public TextView date;
        public TextView location;
        public TextView options;

        public ViewHolder(View view) {
            super(view);
            this.name = (TextView) view.findViewById(R.id.setlists_list_item_name);
            this.date = (TextView) view.findViewById(R.id.setlists_list_item_date);
            this.location = (TextView) view.findViewById(R.id.setlists_list_item_location);
            this.options = (TextView) view.findViewById(R.id.options_icon);

            view.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

            // groupId, itemId, order, titleRes
            contextMenu.add(Menu.NONE, R.id.edit, Menu.NONE, R.string.context_menu_edit);
            contextMenu.add(Menu.NONE, R.id.duplicate, Menu.NONE, R.string.context_menu_duplicate);
            contextMenu.add(Menu.NONE, R.id.remove, Menu.NONE, R.string.context_menu_remove);

        }

    }

    public SetlistRecyclerViewAdapter(Context context, List<Setlist> dataset) {

        this.context = context;
        this.dataset = dataset;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        itemClickListener = (ItemClickListener) parent.getContext();

        View textView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.setlists_list_item, parent, false);

        return new ViewHolder(textView);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPosition(holder.getLayoutPosition());
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

            Setlist setlist = dataset.get(position);
            String name = setlist.getName();

            Date date = setlist.getDate();
            String dateStr = "";

            if ( date != null ) {

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                String day = String.valueOf(calendar.get(Calendar.DATE));
                String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
                String year = String.valueOf(calendar.get(Calendar.YEAR));

                dateStr = day + "." + month + "." + year;

            }

            String location = setlist.getLocation() == null ? "" : setlist.getLocation();
            holder.name.setText(name);
            holder.date.setText(dateStr);
            holder.location.setText(location);

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
        holder.itemView.setOnClickListener(null);
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
