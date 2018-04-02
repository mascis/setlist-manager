package setlistmanager.song;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.setlistmanager.R;

import setlistmanager.screenslide.OnExitListener;
import setlistmanager.util.FileUtil;

/**
 * Created by User on 22.12.2017.
 */

public class ThumbnailFragmentImg extends Fragment {

    private static final String TAG = ThumbnailFragmentImg.class.getSimpleName();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup imgView = (ViewGroup) inflater.inflate(R.layout.thumbnail_img, container, false);

        ImageView imageView = (ImageView) imgView.findViewById(R.id.thumbnailImage);

        imageView.setImageBitmap(BitmapFactory.decodeFile(getArguments().getString("pathName")));

        return imgView;

    }

    public static ThumbnailFragmentImg newInstance(Context context, String uriString) {

        ThumbnailFragmentImg fragmentImg = new ThumbnailFragmentImg();

        Uri uri = Uri.parse(uriString);
        String pathName = FileUtil.getPathFromUri(context, uri);

        Bundle bundle = new Bundle();
        bundle.putString("pathName", pathName);

        fragmentImg.setArguments(bundle);

        return fragmentImg;

    }

}
