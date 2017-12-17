package setlistmanager.setlist;

import setlistmanager.util.BaseNavigator;

/**
 * Created by User on 15.12.2017.
 */

public class SetlistsNavigator {

    private final BaseNavigator navigationProvider;

    public SetlistsNavigator( BaseNavigator navigationProvider ) {
        this.navigationProvider = navigationProvider;
    }

    void addSetlist() {
        navigationProvider.startActivityForResult(AddEditSetlistActivity.class, AddEditSetlistActivity.REQUEST_ADD_SETLLIST);
    }

    void editSetlist(String id) {
        navigationProvider.startActivityForResultWithExtra(AddEditSetlistActivity.class, AddEditSetlistActivity.REQUEST_EDIT_SETLIST, "id", id);
    }

}
