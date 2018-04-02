package setlistmanager.util;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.setlistmanager.R;

/**
 * Created by User on 3.3.2018.
 */

public final class Theme {

    public static final int LIST_ITEM_ACTIVE = R.color.colorActive;
    public static final int LIST_ITEM_UNACTIVE = R.drawable.list_item_divider;
    public static final int EDIT_TEXT_BORDER_FOCUSED= R.drawable.edit_text_border_focus;
    public static final int EDIT_TEXT_BORDER_NORMAL= R.drawable.edit_text_border_normal;

    public static void setListItemActive(RecyclerView.ViewHolder holder) {

        holder.itemView.setBackgroundResource(LIST_ITEM_ACTIVE);

    }

    public static void setListItemUnactive(RecyclerView.ViewHolder holder) {

        holder.itemView.setBackgroundResource(LIST_ITEM_UNACTIVE);

    }

    public static void setEditTextBorderFocus(View view) {

        view.setBackgroundResource(EDIT_TEXT_BORDER_FOCUSED);

    }

    public static void setEditTextBorderNormal(View view) {

        view.setBackgroundResource(EDIT_TEXT_BORDER_NORMAL);

    }

}
