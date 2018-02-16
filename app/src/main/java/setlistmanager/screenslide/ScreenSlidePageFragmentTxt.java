package setlistmanager.screenslide;

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

import setlistmanager.data.Song;
import setlistmanager.util.FileUtil;

/**
 * Created by User on 22.12.2017.
 */

public class ScreenSlidePageFragmentTxt extends Fragment {

    private OnExitListener onExitListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup txtView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page_txt, container, false);

        TextView textView = (TextView) txtView.findViewById(R.id.txtView);
        textView.setText( getArguments().getString("content"));

        onExitListener = (OnExitListener) getActivity();

        final LinearLayout exitContainer = (LinearLayout) txtView.findViewById(R.id.exit_container);
        final TextView exitIcon = (TextView) txtView.findViewById(R.id.exit_icon);

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {

                exitIcon.setVisibility(View.GONE);

            }

        };

        txtView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if ( motionEvent.getAction() == MotionEvent.ACTION_DOWN ) {

                    exitIcon.setVisibility(View.VISIBLE);
                    handler.postDelayed(runnable, 5000);

                }

                return false;

            }
        });

        exitIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onExitListener.onExit();
            }
        });

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
