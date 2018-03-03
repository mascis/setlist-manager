package setlistmanager.util;

import android.support.v7.widget.RecyclerView;

import com.setlistmanager.R;

/**
 * Created by User on 3.3.2018.
 */

public final class Theme {

    public static final int LIST_ITEM_ACTIVE = R.color.colorActive;
    public static final int LIST_ITEM_UNACTIVE = R.drawable.list_item_divider;

    public static void setListItemActive(RecyclerView.ViewHolder holder) {

        holder.itemView.setBackgroundResource(LIST_ITEM_ACTIVE);

    }

    public static void setListItemUnactive(RecyclerView.ViewHolder holder) {

        holder.itemView.setBackgroundResource(LIST_ITEM_UNACTIVE);

    }

}
