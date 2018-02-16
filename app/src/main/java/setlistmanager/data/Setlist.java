package setlistmanager.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by User on 14.12.2017.
 */

@Entity(tableName = "setlists")
public class Setlist implements Serializable {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "setlistId")
    private String id;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @Nullable
    @ColumnInfo(name = "location")
    private String location;

    @Nullable
    @ColumnInfo(name = "date")
    private Date date;

    @NonNull
    @ColumnInfo(name = "createdAt")
    private Date createdAt;

    @NonNull
    @ColumnInfo(name = "modifiedAt")
    private Date modifiedAt;

    @Nullable
    @ColumnInfo(name = "songs")
    private List<String> songs;

    @Ignore
    public Setlist(@NonNull String name, @Nullable String location,
                   @Nullable Date date, @NonNull Date createdAt, @NonNull Date modifiedAt,
                   @Nullable List<String> songs) {

        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.location = location;
        this.date = date;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.songs = songs;

    }

    public Setlist(@NonNull String id, @NonNull String name, @Nullable String location,
                   @Nullable Date date, @NonNull Date createdAt, @NonNull Date modifiedAt,
                   @Nullable List<String> songs) {

        this.id = id;
        this.name = name;
        this.location = location;
        this.date = date;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.songs = songs;

    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Nullable
    public String getLocation() {
        return location;
    }

    public void setLocation(@Nullable String location) {
        this.location = location;
    }

    @Nullable
    public Date getDate() {
        return date;
    }

    public void setDate(@Nullable Date date) {
        this.date = date;
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

    @Nullable
    public List<String> getSongs() {
        return songs;
    }

    public void setSongs(@Nullable List<String> songs) {
        this.songs = songs;
    }

    @Override
    public String toString() {
        return "Setlist with name " + name;
    }

}
