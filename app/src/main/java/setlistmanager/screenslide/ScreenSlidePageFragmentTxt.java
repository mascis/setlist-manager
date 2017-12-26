package setlistmanager.screenslide;

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

import setlistmanager.data.Song;
import setlistmanager.util.FileUtil;

/**
 * Created by User on 22.12.2017.
 */

public class ScreenSlidePageFragmentTxt extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup txtView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page_txt, container, false);

        TextView textView = (TextView) txtView.findViewById(R.id.txtView);
        textView.setText( getArguments().getString("content"));

        return txtView;
    }

    public static ScreenSlidePageFragmentTxt newInstance(Context context, Uri uri) {

        ScreenSlidePageFragmentTxt fragmentTxt = new ScreenSlidePageFragmentTxt();

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
