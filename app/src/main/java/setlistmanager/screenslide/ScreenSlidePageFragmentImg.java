package setlistmanager.screenslide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.setlistmanager.R;

import setlistmanager.data.Song;
import setlistmanager.util.FileUtil;

/**
 * Created by User on 22.12.2017.
 */

public class ScreenSlidePageFragmentImg extends Fragment {

    private static final String TAG = ScreenSlidePageFragmentImg.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup imgView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page_img, container, false);

        ImageView imageView = (ImageView) imgView.findViewById(R.id.imageView);

        imageView.setImageBitmap(BitmapFactory.decodeFile(getArguments().getString("pathName")));


        return imgView;
    }

    public static ScreenSlidePageFragmentImg newInstance(Context context, String uriString) {

        ScreenSlidePageFragmentImg fragmentImg = new ScreenSlidePageFragmentImg();

        Uri uri = Uri.parse(uriString);
        String pathName = FileUtil.getPathFromUri(context, uri);

        Bundle bundle = new Bundle();
        bundle.putString("pathName", pathName);

        fragmentImg.setArguments(bundle);

        return fragmentImg;

    }


}
