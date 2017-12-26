package setlistmanager.screenslide;

import android.content.Context;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup pdfView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page_pdf, container, false);

        final PDFView content = (PDFView) pdfView.findViewById(R.id.pdfView);

        File file = new File(getArguments().getString("pathName"));

        content.fromFile(file).onLoad(new OnLoadCompleteListener() {
            @Override
            public void loadComplete(int nbPages) {

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
