package setlistmanager.screenslide;

import android.content.Context;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.github.barteksc.pdfviewer.listener.OnTapListener;
import com.setlistmanager.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import setlistmanager.data.Song;
import setlistmanager.util.FileUtil;

/**
 * Created by User on 22.12.2017.
 */

public class ScreenSlidePageFragmentPdf extends Fragment {

    private static final String TAG = ScreenSlidePageFragmentPdf.class.getSimpleName();

    private OnExitListener onExitListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup pdfView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page_pdf, container, false);

        final PDFView content = (PDFView) pdfView.findViewById(R.id.pdfView);

        File file = new File(getArguments().getString("pathName"));

        onExitListener = (OnExitListener) getActivity();

        final LinearLayout exitContainer = (LinearLayout) pdfView.findViewById(R.id.exit_container);
        final TextView exitIcon = (TextView) pdfView.findViewById(R.id.exit_icon);

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {

                exitIcon.setVisibility(View.GONE);

            }

        };

        OnTapListener onTapListener = new OnTapListener() {
            @Override
            public boolean onTap(MotionEvent e) {

                exitIcon.setVisibility(View.VISIBLE);
                handler.postDelayed(runnable, 3000);

                return true;

            }
        };

        exitIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onExitListener.onExit();
            }
        });

        content.fromFile(file).onTap(onTapListener).onLoad(new OnLoadCompleteListener() {
            @Override
            public void loadComplete(int nbPages) {
                content.fitToWidth();
            }
        }).load();

        return pdfView;

    }

    public static ScreenSlidePageFragmentPdf newInstance(Context context, String uriString) {

        ScreenSlidePageFragmentPdf fragmentPdf = new ScreenSlidePageFragmentPdf();

        Bundle bundle = new Bundle();

        Uri uri = Uri.parse(uriString);

        String pathName = FileUtil.getPathFromUri(context, uri);

        bundle.putString("pathName", pathName);

        fragmentPdf.setArguments(bundle);

        return fragmentPdf;

    }
}
