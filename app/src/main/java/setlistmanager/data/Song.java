package setlistmanager.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

/**
 * Created by User on 17.12.2017.
 */

@Entity(tableName = "songs")
public class Song {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "songId")
    String id;

    @NonNull
    @ColumnInfo(name = "title")
    String title;

    @Nullable
    @ColumnInfo(name = "artist")
    String artist;

    @Nullable
    @ColumnInfo(name = "filepath")
    String filepath;

    @NonNull
    @ColumnInfo(name = "createdAt")
    Date createdAt;

    @NonNull
    @ColumnInfo(name = "modifiedAt")
    Date modifiedAt;

    public Song(@NonNull String id, @NonNull String title, String artist, String filepath, @NonNull Date createdAt, @NonNull Date modifiedAt) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.filepath = filepath;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @Nullable
    public String getArtist() {
        return artist;
    }

    public void setArtist(@Nullable String artist) {
        this.artist = artist;
    }

    @Nullable
    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(@Nullable String filepath) {
        this.filepath = filepath;
    }

    @NonNull
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@NonNull Date createdAt) {
        this.createdAt = createdAt;
    }

    @NonNull
    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(@NonNull Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}
