package setlistmanager.screenslide;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.setlistmanager.R;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import setlistmanager.util.FileUtil;

/**
 * Created by User on 22.12.2017.
 */

public class ScreenSlidePageFragmentDocx extends Fragment {

    private static final String TAG = ScreenSlidePageFragmentDocx.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup txtView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page_docx, container, false);

        TextView textView = (TextView) txtView.findViewById(R.id.docxView);
        textView.setText( getArguments().getString("content"));
        //textView.setTypeface(Typeface.SERIF);
        textView.setTextSize(12);

        return txtView;

    }

    public static ScreenSlidePageFragmentDocx newInstance(Context context, Uri uri) {

        ScreenSlidePageFragmentDocx fragmentDocx = new ScreenSlidePageFragmentDocx();

        String path = FileUtil.getPathFromUri(context, uri);

        try {

            XWPFDocument docx = new XWPFDocument(new FileInputStream(path));



            XWPFHeader xwpfHeader = docx.getHeaderFooterPolicy().getHeader(XWPFHeaderFooterPolicy.FIRST);

            Log.i(TAG, "header: " + xwpfHeader.getText());



            XWPFWordExtractor wordExtractor = new XWPFWordExtractor(docx);
            String content = wordExtractor.getText();
            Bundle bundle = new Bundle();
            bundle.putString("content", content);
            fragmentDocx.setArguments(bundle);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return fragmentDocx;

    }

}
