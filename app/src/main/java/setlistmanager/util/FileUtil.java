package setlistmanager.util;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
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
import java.nio.file.Files;
import java.nio.file.LinkOption;

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
    public static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 200;
    public static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 201;

    public static String[] getSuppportedMimeTypes() {

        String[] mimetypes = {MIME_TYPE_PDF, MIME_TYPE_PLAIN_TEXT, MIME_TYPE_IMAGES};

        return mimetypes;

    }

    public static String getPathFromUri( Context context, Uri uri ) {

        if ( uri == null ) {
            return null;
        }

        String path = null;
        String selection = null;
        String[] selectionArgs = null;

        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {

            if ( isExternalStorageDocument(uri) ) {

                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];

            } else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

            } else if (isMediaDocument(uri)) {

                final String docId = DocumentsContract.getDocumentId(uri);

                final String[] split = docId.split(":");
                final String type = split[0];

                if ("image".equals(type)) {

                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

                }

                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }

        }

        if ( "content".equalsIgnoreCase(uri.getScheme()) ) {

            String[] projection = {
                    MediaStore.Images.Media.DATA
            };

            Cursor cursor = null;

            try {

                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);

                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                if (cursor.moveToFirst()) {

                    return cursor.getString(column_index);

                }

            } catch (Exception e) {

            }

        } else if ( "file".equalsIgnoreCase(uri.getScheme()) ) {

            return uri.getPath();

        }

        return null;

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

    public static boolean isExternalStorageWritable() {

        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {

            return true;

        }

        return false;

    }

    public static boolean isExternalStorageReadable() {

        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {

            return true;

        }

        return false;

    }

    public static boolean hasPermissionToReadExternalStorage(Activity activity) {

        int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if ( permissionCheck == PackageManager.PERMISSION_GRANTED ) {
            return true;
        }

        return false;

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

    public static boolean isReadableFile( String path ) {

        if ( path == null ) {
            return false;
        }

        File file = new File(path);

        if ( file.isFile() && file.canRead() ) {
            return true;
        }

        return false;
    }

    public static boolean isPdf( Context context, Uri uri ) {

        String path = getPathFromUri(context, uri);

        if ( !isReadableFile(path) ) {
            return false;
        }

        if ( path.endsWith("pdf") ) {
            return true;
        }

        return false;

    }

    public static boolean isPlainText( Context context, Uri uri ) {

        String path = getPathFromUri(context, uri);

        if ( !isReadableFile(path) ) {
            return false;
        }

        if ( path.endsWith("txt") ) {
            return true;
        }

        return false;

    }

    public static boolean isImage( Context context, Uri uri ) {

        String path = getPathFromUri(context, uri);

        if ( !isReadableFile(path) ) {
            return false;
        }

        String[] split = path.split("\\.");

        int lastIndex = split.length - 1;
        String extension = split[lastIndex];
        String mimeType = mimeTypeMap.getMimeTypeFromExtension(extension);

        if ( mimeType != null && mimeType.startsWith("image")) {
            return true;
        }

        return false;

    }

}
