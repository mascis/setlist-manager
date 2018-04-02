package setlistmanager.song;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.setlistmanager.R;

import java.io.IOException;

import setlistmanager.screenslide.OnExitListener;
import setlistmanager.util.FileUtil;

/**
 * Created by User on 22.12.2017.
 */

public class ThumbnailFragmentPlaceholder extends Fragment {

    private OnExitListener onExitListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup txtView = (ViewGroup) inflater.inflate(R.layout.thumbnail_placeholder, container, false);

        TextView textView = (TextView) txtView.findViewById(R.id.thumbnailPlaceholder);
        textView.setText( getArguments().getString("content"));

        return txtView;
    }

    public static ThumbnailFragmentPlaceholder newInstance() {

        ThumbnailFragmentPlaceholder fragmentPlaceholder = new ThumbnailFragmentPlaceholder();

        String content = "NOT SELECTED";

        Bundle bundle = new Bundle();
        bundle.putString("content", content);

        fragmentPlaceholder.setArguments(bundle);

        return fragmentPlaceholder;

    }
}
