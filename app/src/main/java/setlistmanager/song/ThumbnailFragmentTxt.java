package setlistmanager.song;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.setlistmanager.R;

import java.io.IOException;

import setlistmanager.screenslide.OnExitListener;
import setlistmanager.util.FileUtil;

/**
 * Created by User on 22.12.2017.
 */

public class ThumbnailFragmentTxt extends Fragment {

    private OnExitListener onExitListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup txtView = (ViewGroup) inflater.inflate(R.layout.thumbnail_txt, container, false);

        TextView textView = (TextView) txtView.findViewById(R.id.thumbnailTxt);
        textView.setText( getArguments().getString("content"));

        return txtView;
    }

    public static ThumbnailFragmentTxt newInstance(Context context, Uri uri) {

        ThumbnailFragmentTxt fragmentTxt = new ThumbnailFragmentTxt();

        String path = FileUtil.getPathFromUri(context, uri);

        String content = "";

        try {
            content = FileUtil.getStringFromFile(path);
        } catch (IOException ioe ) {
            ioe.printStackTrace();
        }

        Bundle bundle = new Bundle();
        bundle.putString("content", content);

        fragmentTxt.setArguments(bundle);

        return fragmentTxt;

    }
}
