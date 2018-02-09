package setlistmanager.helper;

/**
 * Created by User on 9.2.2018.
 */

public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);

}
