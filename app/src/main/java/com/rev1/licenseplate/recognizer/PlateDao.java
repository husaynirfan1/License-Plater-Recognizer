package com.rev1.licenseplate.recognizer;/*
 * *
 *  * Created by Husayn on 22/10/2021, 5:04 PM
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 22/10/2021, 2:29 PM
 *
 */
import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@androidx.room.Dao

public interface PlateDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(PlateModal model);

    @Insert
    void insertAll(PlateModal... modal);

    // below method is use to update
    // the data in our database.
    @Update
    void update(PlateModal model);

    // below line is use to delete a
    // specific course in our database.
    @Delete
    void delete(PlateModal model);

    @Query("DELETE FROM plate_table")
    void deleteAll();

    @Query("SELECT * FROM  plate_table ORDER BY no_plate")
    LiveData<List<PlateModal>> getAllPlate();

    @Query("SELECT * FROM plate_table WHERE no_plate = :nombor")
    LiveData<PlateModal> findbyPlateNumber (String nombor);
}
