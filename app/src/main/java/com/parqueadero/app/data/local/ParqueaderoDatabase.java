package com.parqueadero.app.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

// Importamos el convertidor de fechas que creaste hace un rato
import com.parqueadero.app.utils.Converters;

@Database(entities = {Mensualidad.class, Estancia.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class ParqueaderoDatabase extends RoomDatabase {

    public abstract ParqueaderoDao parqueaderoDao();

    private static volatile ParqueaderoDatabase INSTANCE;

    public static ParqueaderoDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (ParqueaderoDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    ParqueaderoDatabase.class, "parqueadero_db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}