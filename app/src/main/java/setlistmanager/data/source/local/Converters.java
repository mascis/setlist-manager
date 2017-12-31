package setlistmanager.data.source.local;

import android.arch.persistence.room.TypeConverter;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import setlistmanager.data.Song;

/**
 * Created by User on 14.12.2017.
 */

public class Converters {

    private static final String TAG = Converters.class.getSimpleName();

    @TypeConverter
    public static Date fromTimestamp( Long timestamp ) {

        return timestamp == null ? null : new Date(timestamp);

    }

    @TypeConverter
    public static Long dateToTimestamp( Date date ) {

        return date == null ? null : date.getTime();

    }

    @TypeConverter
    public static ArrayList<String> arrayListfromString( String value ) {

        if ( value == null ) {
            return null;
        }

        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return new Gson().fromJson(value, type);

    }

    @TypeConverter
    public static String arrayListToString( ArrayList<String> list ) {

        if ( list == null ) {
            return null;
        }

        Gson gson = new Gson();
        String json = gson.toJson(list);

        return json;

    }

    @TypeConverter
    public static List<String> listfromString(String value ) {

        if ( value == null ) {
            return null;
        }

        Type type = new TypeToken<List<String>>() {}.getType();

        return new Gson().fromJson(value, type);

    }

    @TypeConverter
    public static String listToString( List<String> list ) {

        if ( list == null ) {
            return null;
        }

        Gson gson = new Gson();
        String json = gson.toJson(list);

        return json;

    }

}
