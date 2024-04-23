package com.rev1.licenseplate.recognizer;/*
 * *
 *  * Created by Husayn on 22/10/2021, 5:04 PM
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 22/10/2021, 2:29 PM
 *
 */

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;
public class PlateRepository {
    private PlateDao dao;
    private LiveData<List<PlateModal>> allPlate;
    // creating a constructor for our variables
    // and passing the variables to it.

    public PlateRepository(Application application) {
        LicenseDatabase database = LicenseDatabase.getInstance(application);
        dao = database.PlateDao();
        allPlate = dao.getAllPlate();
    }

    // creating a method to insert the data to our database.
    public void insert(PlateModal model) {
        new InsertCourseAsyncTask(dao).execute(model);
    }

    // creating a method to update data in database.
    public void update(PlateModal model) {
        new UpdateCourseAsyncTask(dao).execute(model);
    }

    // creating a method to delete the data in our database.
    public void delete(PlateModal model) {
        new DeleteCourseAsyncTask(dao).execute(model);
    }

    // below is the method to delete all the courses.
    public void deleteAllPlate() {
        new DeleteAllCoursesAsyncTask(dao).execute();
    }

    // below method is to read all the courses.
    public LiveData<List<PlateModal>> getAllPlate() {
        return allPlate;
    }
    public LiveData<PlateModal> findbyPlate(String nombor) {
        return dao.findbyPlateNumber(nombor);
    }
    // we are creating a async task method to insert new course.
    private static class InsertCourseAsyncTask extends AsyncTask<PlateModal, Void, Void> {
        private PlateDao dao;

        private InsertCourseAsyncTask(PlateDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(PlateModal... model) {
            // below line is use to insert our modal in dao.
            dao.insert(model[0]);
            return null;
        }
    }

    // we are creating a async task method to update our course.
    private static class UpdateCourseAsyncTask extends AsyncTask<PlateModal, Void, Void> {
        private PlateDao dao;

        private UpdateCourseAsyncTask(PlateDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(PlateModal... models) {
            // below line is use to update
            // our modal in dao.
            dao.update(models[0]);
            return null;
        }
    }

    // we are creating a async task method to delete course.
    private static class DeleteCourseAsyncTask extends AsyncTask<PlateModal, Void, Void> {
        private PlateDao dao;

        private DeleteCourseAsyncTask(PlateDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(PlateModal... models) {
            // below line is use to delete
            // our course modal in dao.
            dao.delete(models[0]);
            return null;
        }
    }

    // we are creating a async task method to delete all courses.
    private static class DeleteAllCoursesAsyncTask extends AsyncTask<Void, Void, Void> {
        private PlateDao dao;
        private DeleteAllCoursesAsyncTask(PlateDao dao) {
            this.dao = dao;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            // on below line calling method
            // to delete all courses.
            dao.deleteAll();
            return null;
        }
    }
}
