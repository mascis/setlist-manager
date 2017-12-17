package setlistmanager.data.source.local;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by User on 14.12.2017.
 */

public class Converters {

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

}
