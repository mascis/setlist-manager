package setlistmanager.setlist;

import setlistmanager.util.BaseNavigator;

/**
 * Created by User on 15.12.2017.
 */

public class AddEditSetlistNavigator {

    private final BaseNavigator navigationProvider;

    public AddEditSetlistNavigator( BaseNavigator navigationProvider ) {
        this.navigationProvider = navigationProvider;
    }

    void onSetlistSaved() {
        navigationProvider.startActivityForResult(SetlistsActivity.class, 0);
    }

    void onCancel() {
        navigationProvider.startActivityForResult(SetlistsActivity.class, 0);
    }

}
