package setlistmanager.song;

import setlistmanager.util.BaseNavigator;

/**
 * Created by User on 15.12.2017.
 */

public class AddEditSongNavigator {

    private final BaseNavigator navigationProvider;

    public AddEditSongNavigator(BaseNavigator navigationProvider ) {
        this.navigationProvider = navigationProvider;
    }

    void onSongSaved() {

        navigationProvider.finishActivityWithResult(AddEditSongActivity.RESULT_CODE_DATA_CHANGED);

    }

    void onCancel() {

        navigationProvider.finishActivity();

    }

}
