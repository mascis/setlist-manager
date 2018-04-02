package setlistmanager.song;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.setlistmanager.R;

import java.io.File;

import setlistmanager.util.FileUtil;

/**
 * Created by User on 22.12.2017.
 */

public class ThumbnailFragmentPdf extends Fragment {

    private static final String TAG = ThumbnailFragmentPdf.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup pdfView = (ViewGroup) inflater.inflate(R.layout.thumbnail_pdf, container, false);

        final PDFView content = (PDFView) pdfView.findViewById(R.id.thumbnailPdf);

        File file = new File(getArguments().getString("pathName"));

        content.fromFile(file)
                .onRender(new OnRenderListener() {
                    @Override
                    public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {
                        content.fitToWidth(0);
                    }
                })
                .onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int nbPages) {
                        //content.fitToWidth();
                        //content.fitToWidth(0);


                    }
                });

        return pdfView;

    }

    public static ThumbnailFragmentPdf newInstance(Context context, String uriString) {

        ThumbnailFragmentPdf fragmentPdf = new ThumbnailFragmentPdf();

        Bundle bundle = new Bundle();

        Uri uri = Uri.parse(uriString);

        String pathName = FileUtil.getPathFromUri(context, uri);

        bundle.putString("pathName", pathName);

        fragmentPdf.setArguments(bundle);

        return fragmentPdf;

    }
}
