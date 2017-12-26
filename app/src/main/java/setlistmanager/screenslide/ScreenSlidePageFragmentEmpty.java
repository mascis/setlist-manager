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

import setlistmanager.util.FileUtil;

/**
 * Created by User on 22.12.2017.
 */

public class ScreenSlidePageFragmentEmpty extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup txtView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page_txt, container, false);

        TextView textView = (TextView) txtView.findViewById(R.id.txtView);
        textView.setText(R.string.screen_slider_no_data_available);

        return txtView;

    }

    public static ScreenSlidePageFragmentEmpty newInstance() {

        ScreenSlidePageFragmentEmpty fragmentEmpty = new ScreenSlidePageFragmentEmpty();

        return fragmentEmpty;

    }
}
