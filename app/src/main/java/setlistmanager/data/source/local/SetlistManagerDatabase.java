package setlistmanager.data.source.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import setlistmanager.data.Setlist;

/**
 * Created by User on 14.12.2017.
 */
@Database(entities = {Setlist.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class SetlistManagerDatabase extends RoomDatabase {

    private static volatile SetlistManagerDatabase INSTANCE;

    public abstract SetlistDao setlistDao();

    public static SetlistManagerDatabase getInstance(Context context ) {

        if ( INSTANCE == null ) {

            synchronized ( SetlistManagerDatabase.class ) {

                if ( INSTANCE == null ) {

                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            SetlistManagerDatabase.class, "SetlistManager.db")
                            .build();

                }

            }

        }

        return INSTANCE;

    }

}
