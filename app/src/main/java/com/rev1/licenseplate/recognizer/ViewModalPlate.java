package com.rev1.licenseplate.recognizer;/*
 * *
 *  * Created by Husayn on 22/10/2021, 5:04 PM
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 22/10/2021, 2:29 PM
 *
 */

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ViewModalPlate extends AndroidViewModel {
    private PlateRepository repository;

    // below line is to create a variable for live
    // data where all the courses are present.
    private LiveData<List<PlateModal>> allplate;

    // constructor for our view modal.
    public ViewModalPlate(@NonNull Application application) {
        super(application);
        repository = new PlateRepository(application);
        allplate = repository.getAllPlate();
    }

    // below method is use to insert the data to our repository.
    public void insert(PlateModal model) {
        repository.insert(model);
    }

    // below line is to update data in our repository.
    public void update(PlateModal model) {
        repository.update(model);
    }

    // below line is to delete the data in our repository.
    public void delete(PlateModal model) {
        repository.delete(model);
    }

    // below method is to delete all the courses in our list.
    public void deleteAllCourses() {
        repository.deleteAllPlate();
    }

    // below method is to get all the courses in our list.
    public LiveData<List<PlateModal>> getAllplate() {
        return allplate;
    }
    public LiveData<PlateModal> findbyPlate(String nombor) {
        return repository.findbyPlate(nombor);
    }
}
