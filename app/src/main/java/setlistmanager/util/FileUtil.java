package setlistmanager.util;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by User on 19.12.2017.
 */

public final class FileUtil {

    private static final String TAG = FileUtil.class.getSimpleName();

    private static final MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

    public static final String MIME_TYPE_PDF = mimeTypeMap.getMimeTypeFromExtension("pdf");
    public static final String MIME_TYPE_PLAIN_TEXT = mimeTypeMap.getMimeTypeFromExtension("txt");
    public static final String MIME_TYPE_IMAGES = "image/*";

    public static final int READ_REQUEST_CODE = 42;

    public static String[] getSuppportedMimeTypes() {

        String[] mimetypes = {MIME_TYPE_PDF, MIME_TYPE_PLAIN_TEXT, MIME_TYPE_IMAGES};

        return mimetypes;

    }

    public static String getPathFromUri( Context context, Uri uri ) {

        String path = null;
        String selection = null;
        String[] selectionArgs = null;

        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {

            if (isExternalStorageDocument(uri)) {

                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return getExternalStoragePath() + "/" + split[1];

            } else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final String[] split = id.split(":");
                return split[1];

                /*
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                */

            } else if (isMediaDocument(uri)) {

                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("image".equals(type)) {

                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

                } else if ("video".equals(type)) {

                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

                } else if ("audio".equals(type)) {

                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

                }

                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }

        return path;

    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static String getExternalStoragePath() {

        String removableStoragePath;

        File fileList[] = new File("/storage/").listFiles();

        for (File file : fileList) {

            if(!file.getAbsolutePath().equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath()) && file.isDirectory() && file.canRead()) {

                return file.toString();

            }

        }

        return Environment.getExternalStorageDirectory().toString();

    }

    public static String getStringFromFile( String path ) throws FileNotFoundException, IOException {

        try {

            File file = new File(path);

            InputStream inputStream = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder out = new StringBuilder();
            String newLine = System.getProperty("line.separator");
            String line;

            while ( (line = reader.readLine()) != null ) {

                out.append(line);
                out.append(newLine);

            }

            inputStream.close();
            reader.close();

            return out.toString();


        } catch (FileNotFoundException e ) {

            e.printStackTrace();
            return null;

        } catch (IOException e) {

            e.printStackTrace();
            return null;

        }

    }

    public static boolean isPdf( Uri uri ) {

        if ( uri == null ) {
            return false;
        }

        if ( uri.toString().endsWith("pdf") ) {
            return true;
        }

        return false;

    }

    public static boolean isPlainText( Uri uri ) {

        if ( uri == null ) {
            return false;
        }
        
        if ( uri.toString().endsWith("txt") ) {
            return true;
        }

        return false;

    }

    public static boolean isImage( Uri uri ) {

        if ( uri == null ) {
            return false;
        }

        String[] split = uri.toString().split("\\.");

        int lastIndex = split.length - 1;
        String extension = split[lastIndex];
        String mimeType = mimeTypeMap.getMimeTypeFromExtension(extension);

        if ( mimeType.startsWith("image") ) {
            return true;
        }

        return false;

    }

}
