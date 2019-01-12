package com.example.ignasi94.backtrackingsimple.BBDD;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.ignasi94.backtrackingsimple.Estructuras.Cage;
import com.example.ignasi94.backtrackingsimple.Estructuras.CageDog;
import com.example.ignasi94.backtrackingsimple.Estructuras.Dog;
import com.example.ignasi94.backtrackingsimple.Estructuras.Grafo.DogGraf.TupleDog;
import com.example.ignasi94.backtrackingsimple.Estructuras.Volunteer;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerDog;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerWalks;
import com.example.ignasi94.backtrackingsimple.MergeUtils.MergeUtils;
import com.example.ignasi94.backtrackingsimple.Utils.Constants;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

public class DBAdapter{

    DBHandler dbHandler ;
    private Dog dog;

    public DBAdapter(Context context)
    {
        dbHandler = new DBHandler(context);
    }

    public Dictionary<Integer,Dog> getAllDogsDictionary()
    {
        return getAllDogsDictionary(true);
    }
    public Dictionary<Integer,Dog> getAllDogsDictionary(boolean getFriends)
    {
        Dictionary<Integer,Dog> dogsDictionary = new Hashtable<Integer, Dog>();
        List<Dog> dogs = getAllDogs(getFriends);
        for(int i = 0; i < dogs.size(); ++i)
        {
            Dog dog = dogs.get(i);
            dogsDictionary.put(dog.id,dog);
        }
        return dogsDictionary;
    }

    public Dictionary<Integer,Volunteer> getAllVolunteersDictionary()
    {
        Dictionary<Integer,Volunteer> volunteersDictionary = new Hashtable<Integer, Volunteer>();
        List<Volunteer> volunteers = getAllVolunteers();
        for(int i = 0; i < volunteers.size(); ++i)
        {
            Volunteer volunteer = volunteers.get(i);
            this.getDogFavourites(volunteer);
            volunteersDictionary.put(volunteer.id,volunteer);
        }
        return volunteersDictionary;
    }

    public Dictionary<Integer,Cage> getAllCagesDictionary() {
            return this.getAllCagesDictionaryByZone(null);
    }

    public Dictionary<Integer,Cage> getAllCagesDictionaryByZone(String zone)
    {
        Dictionary<Integer,Cage> cagesDictionary = new Hashtable<Integer, Cage>();
        List<Cage> cages = getAllCagesByZone(zone);
        for(int i = 0; i < cages.size(); ++i)
        {
            Cage cage = cages.get(i);
            cagesDictionary.put(cage.id,cage);
        }
        return cagesDictionary;
    }

    //Devuelve una lista con todos los perros
    public List<Dog> getAllDogs()
    {
        return this.getAllDogsByZone(null, true, null);
    }

    public List<Dog> getAllDogs(boolean getFriends)
    {
        return this.getAllDogsByZone(null, getFriends, null);
    }

    public List<Dog> getAllDogsByZone(String zone) {
        return this.getAllDogsByZone(zone, true, null);
    }

    //Devuelve una lista con todos los perros existentes en la zona 'zone'
    //Si 'zone' = null devuelve una lista con todos los perros
    public List<Dog> getAllDogsByZone(String zone, boolean getFriends, String type)
    {
        String table = null;
        String tableFriends = null;
        if(type == null)
        {
            table = dbHandler.TABLE_DOGS;
            tableFriends = dbHandler.TABLE_DOG_FRIENDS;
        }
        else if(type.equals("TEST"))
        {
            table = dbHandler.TABLE_DOGS_TEST;
            tableFriends = dbHandler.TABLE_DOG_FRIENDS_TEST;
        }
        List<Dog> dogs = new ArrayList<Dog>();
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        String selection = null;
        String[] params = null;
        String[] columns = {dbHandler.KEY_DOG_ID,dbHandler.KEY_DOG_NAME, dbHandler.KEY_DOG_ID_CAGE, dbHandler.KEY_DOG_AGE, dbHandler.KEY_DOG_LINK, dbHandler.KEY_DOG_SPECIAL, dbHandler.KEY_DOG_WALKTYPE, dbHandler.KEY_DOG_OBSERVATIONS};

        Cursor cursor = db.query(table,columns,null, null,null,null,null);
        StringBuffer buffer= new StringBuffer();
        while (cursor.moveToNext())
        {
            int id = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_DOG_ID));
            String name = cursor.getString(cursor.getColumnIndex(dbHandler.KEY_DOG_NAME));
            int cage = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_DOG_ID_CAGE));
            int age = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_DOG_AGE));
            String link = cursor.getString(cursor.getColumnIndex(dbHandler.KEY_DOG_LINK));
            Boolean special = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_DOG_SPECIAL)) > 0;
            String walkTypeString = cursor.getString(cursor.getColumnIndex(dbHandler.KEY_DOG_WALKTYPE));
            String observations = cursor.getString(cursor.getColumnIndex(dbHandler.KEY_DOG_OBSERVATIONS));
            Short walkType;
            if(walkTypeString.contentEquals("0"))
            {
                walkType = 0;
            }
            else if (walkTypeString.contentEquals("1"))
            {
                walkType = 1;
            }
            else
            {
                walkType = 2;
            }
            Dog dog = new Dog(id,name,cage,age,link,special,walkType,observations);
            dogs.add(dog);
        }

        if(zone != null)
        {
            if(!zone.equals(Constants.CAGE_ZONE_NONE)) {
                Dictionary<Integer, Cage> cages = this.getAllCagesDictionaryByZone(zone);
                List<Dog> onlyZoneDogs = new ArrayList<Dog>();
                for (int i = 0; i < dogs.size(); ++i) {
                    Dog dog = dogs.get(i);
                    if (cages.get(dog.idCage) != null && cages.get(dog.idCage).zone.equals(zone)) {
                        onlyZoneDogs.add(dog);
                    }
                }
                dogs = onlyZoneDogs;
            }
            else
            {
                List<Dog> onlyUnassignedDogs = new ArrayList<Dog>();
                for (int i = 0; i < dogs.size(); ++i) {
                    Dog dog = dogs.get(i);
                    if (dog.idCage < 0) {
                        onlyUnassignedDogs.add(dog);
                    }
                }
                dogs = onlyUnassignedDogs;
            }
        }

        if(getFriends) {
            for (int i = 0; i < dogs.size(); ++i) {
                this.getDogFriends(dogs, i, tableFriends);
            }
        }
        return dogs;
    }

    public List<Dog> getAllDogsTest()
    {
        return this.getAllDogsByZone(Constants.CAGE_ZONE_XENILES, true, "TEST");
    }


    public void SaveDogs(List<Dog> dogs)
    {
        for(int i = 0; i < dogs.size(); ++i) {
            Dog dog = dogs.get(i);
            this.SaveOrUpdateDog(dog, DBHandler.TABLE_DOGS, DBHandler.TABLE_DOG_FRIENDS);
        }
    }

    public void SaveDogsTest(List<Dog> dogs)
    {
        for(int i = 0; i < dogs.size(); ++i) {
            Dog dog = dogs.get(i);
            this.SaveOrUpdateDog(dog, DBHandler.TABLE_DOGS_TEST, DBHandler.TABLE_DOG_FRIENDS_TEST);
        }
    }

    public void SaveOrUpdateDog(Dog dog) {
        this.SaveOrUpdateDog(dog, DBHandler.TABLE_DOGS, DBHandler.TABLE_DOG_FRIENDS);
    }

    public void SaveOrUpdateDog(Dog dog, String table, String friendsTable)
    {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        ContentValues initialValues = new ContentValues();

        int maxIdDog = this.GetMaxId(table, dbHandler.KEY_DOG_ID);

        if(dog.id == 0) {
            initialValues.put(DBHandler.KEY_DOG_ID, maxIdDog + 1);
        }
        else
        {
            initialValues.put(DBHandler.KEY_DOG_ID, dog.id);
        }
        initialValues.put(DBHandler.KEY_DOG_NAME, dog.name);
        initialValues.put(DBHandler.KEY_DOG_ID_CAGE, dog.idCage);
        initialValues.put(DBHandler.KEY_DOG_AGE, dog.age);
        initialValues.put(DBHandler.KEY_DOG_LINK, dog.link);
        initialValues.put(DBHandler.KEY_DOG_SPECIAL, dog.special);
        initialValues.put(DBHandler.KEY_DOG_WALKTYPE, dog.walktype);
        initialValues.put(DBHandler.KEY_DOG_OBSERVATIONS, dog.observations);

        int id = (int) db.insertWithOnConflict(table, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
        if (id == -1) {
            db.update(table, initialValues, DBHandler.KEY_DOG_ID + "=?", new String[]{Integer.toString(dog.id)});
        }

        db.delete(friendsTable, DBHandler.KEY_DOGFRIENDS_DOG_ID + "=?", new String[]{Integer.toString(dog.id)});
        db.delete(friendsTable, DBHandler.KEY_DOGFRIENDS_FRIENDDOG_ID + "=?", new String[]{Integer.toString(dog.id)});
        int maxId = this.GetMaxId(friendsTable, dbHandler.KEY_DOGFRIENDS_DOG_ID);

        for(int j = 0; j < dog.friends.size(); ++j)
        {
            Dog friend = dog.friends.get(j);

            for(int z = 0; z < 2; ++z) {
                initialValues = new ContentValues();
                String[] params;
                if(z==0) {
                    initialValues.put(DBHandler.KEY_DOGFRIENDS_DOG_ID, dog.id);
                    initialValues.put(DBHandler.KEY_DOGFRIENDS_FRIENDDOG_ID, friend.id);
                    params = new String[]{Integer.toString(dog.id), Integer.toString(friend.id)};
                }
                else
                {
                    initialValues.put(DBHandler.KEY_DOGFRIENDS_DOG_ID, friend.id);
                    initialValues.put(DBHandler.KEY_DOGFRIENDS_FRIENDDOG_ID, dog.id);
                    params = new String[]{Integer.toString(friend.id), Integer.toString(dog.id)};
                }

                String[] columns = {dbHandler.KEY_DOGFRIENDS_ID};
                String selection = dbHandler.KEY_DOGFRIENDS_DOG_ID + "=? AND " + dbHandler.KEY_DOGFRIENDS_FRIENDDOG_ID + "=?";
                Cursor cursor = db.query(friendsTable, columns, selection, params, null, null, null);
                boolean exists = false;
                int rowId = 0;
                while (cursor.moveToNext()) {
                    exists = true;
                    rowId = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_DOGFRIENDS_ID));
                }

                if (!exists) {
                    initialValues.put(DBHandler.KEY_DOGFRIENDS_ID, maxId+1);
                    db.insertWithOnConflict(friendsTable, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
                    maxId++;
                } else {
                    db.update(friendsTable, initialValues, DBHandler.KEY_DOGFRIENDS_DOG_ID + "=?;", new String[]{Integer.toString(rowId)});
                }
            }
        }
    }

    public void SaveOrUpdateVolunteer(Volunteer volunteer)
    {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        ContentValues initialValues = new ContentValues();

        int maxIdVolunteer = this.GetMaxId(dbHandler.TABLE_VOLUNTEERS, dbHandler.KEY_VOLUNTEER_ID);

        if(volunteer.id == 0) {
            initialValues.put(DBHandler.KEY_VOLUNTEER_ID, maxIdVolunteer + 1);
        }
        else
        {
            initialValues.put(DBHandler.KEY_VOLUNTEER_ID, volunteer.id);
        }
        initialValues.put(DBHandler.KEY_VOLUNTEER_NAME, volunteer.name);
        initialValues.put(DBHandler.KEY_VOLUNTEER_PHONE, volunteer.phone);
        initialValues.put(DBHandler.KEY_VOLUNTEER_DAY, volunteer.volunteerDay);
        initialValues.put(DBHandler.KEY_VOLUNTEER_OBSERVATIONS, volunteer.observations);

        int id = (int) db.insertWithOnConflict(dbHandler.TABLE_VOLUNTEERS, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
        if (id == -1) {
            db.update(dbHandler.TABLE_VOLUNTEERS, initialValues, DBHandler.KEY_VOLUNTEER_ID + "=?", new String[]{Integer.toString(volunteer.id)});
        }

        db.delete(dbHandler.TABLE_DOG_FAVOURITES, DBHandler.KEY_DOGFAVOURITES_VOLUNTEER_ID + "=?", new String[]{Integer.toString(volunteer.id)});
        int maxId = this.GetMaxId(dbHandler.TABLE_DOG_FAVOURITES, dbHandler.KEY_DOGFAVOURITES_ID);

        for(int j = 0; j < volunteer.favouriteDogs.size(); ++j)
        {
            Dog friend = volunteer.favouriteDogs.get(j);

            initialValues = new ContentValues();
            initialValues.put(DBHandler.KEY_DOGFAVOURITES_VOLUNTEER_ID, volunteer.id);
            initialValues.put(DBHandler.KEY_DOGFAVOURITES_DOG_ID, friend.id);

            String[] columns = {dbHandler.KEY_DOGFAVOURITES_ID};
            String selection = dbHandler.KEY_DOGFAVOURITES_VOLUNTEER_ID + "=? AND " + dbHandler.KEY_DOGFAVOURITES_DOG_ID + "=?";
            String[] params = new String[]{Integer.toString(volunteer.id), Integer.toString(friend.id)};
            Cursor cursor = db.query(dbHandler.TABLE_DOG_FAVOURITES,columns,selection, params,null,null,null);
            boolean exists = false;
            int rowId = 0;
            while (cursor.moveToNext()) {
                exists = true;
                rowId = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_DOGFAVOURITES_ID));
            }

            if(!exists)
            {
                initialValues.put(DBHandler.KEY_DOGFAVOURITES_ID, maxId+1);
                db.insertWithOnConflict(dbHandler.TABLE_DOG_FAVOURITES, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
                maxId++;
            }
            else
            {
                db.update(dbHandler.TABLE_DOG_FAVOURITES, initialValues, DBHandler.KEY_DOGFAVOURITES_ID + "=?;", new String[]{Integer.toString(rowId)});
            }
        }
    }

    public void SaveOrUpdateVolunteerTest(Volunteer volunteer)
    {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        ContentValues initialValues = new ContentValues();

        int maxIdVolunteer = this.GetMaxId(dbHandler.TABLE_VOLUNTEERS, dbHandler.KEY_VOLUNTEER_ID);

        if(volunteer.id == 0) {
            initialValues.put(DBHandler.KEY_VOLUNTEER_ID, maxIdVolunteer + 1);
        }
        else
        {
            initialValues.put(DBHandler.KEY_VOLUNTEER_ID, volunteer.id);
        }
        initialValues.put(DBHandler.KEY_VOLUNTEER_NAME, volunteer.name);
        initialValues.put(DBHandler.KEY_VOLUNTEER_PHONE, volunteer.phone);
        initialValues.put(DBHandler.KEY_VOLUNTEER_DAY, volunteer.volunteerDay);
        initialValues.put(DBHandler.KEY_VOLUNTEER_OBSERVATIONS, volunteer.observations);

        int id = (int) db.insertWithOnConflict(dbHandler.TABLE_VOLUNTEERS, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
        if (id == -1) {
            db.update(dbHandler.TABLE_VOLUNTEERS, initialValues, DBHandler.KEY_VOLUNTEER_ID + "=?", new String[]{Integer.toString(volunteer.id)});
        }

        db.delete(dbHandler.TABLE_DOG_FAVOURITES_TEST, DBHandler.KEY_DOGFAVOURITES_VOLUNTEER_ID + "=?", new String[]{Integer.toString(volunteer.id)});
        int maxId = this.GetMaxId(dbHandler.TABLE_DOG_FAVOURITES_TEST, dbHandler.KEY_DOGFAVOURITES_ID);

        for(int j = 0; j < volunteer.favouriteDogs.size(); ++j)
        {
            Dog friend = volunteer.favouriteDogs.get(j);

            initialValues = new ContentValues();
            initialValues.put(DBHandler.KEY_DOGFAVOURITES_VOLUNTEER_ID, volunteer.id);
            initialValues.put(DBHandler.KEY_DOGFAVOURITES_DOG_ID, friend.id);

            String[] columns = {dbHandler.KEY_DOGFAVOURITES_ID};
            String selection = dbHandler.KEY_DOGFAVOURITES_VOLUNTEER_ID + "=? AND " + dbHandler.KEY_DOGFAVOURITES_DOG_ID + "=?";
            String[] params = new String[]{Integer.toString(volunteer.id), Integer.toString(friend.id)};
            Cursor cursor = db.query(dbHandler.TABLE_DOG_FAVOURITES_TEST,columns,selection, params,null,null,null);
            boolean exists = false;
            int rowId = 0;
            while (cursor.moveToNext()) {
                exists = true;
                rowId = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_DOGFAVOURITES_ID));
            }

            if(!exists)
            {
                initialValues.put(DBHandler.KEY_DOGFAVOURITES_ID, maxId+1);
                db.insertWithOnConflict(dbHandler.TABLE_DOG_FAVOURITES_TEST, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
                maxId++;
            }
            else
            {
                db.update(dbHandler.TABLE_DOG_FAVOURITES_TEST, initialValues, DBHandler.KEY_DOGFAVOURITES_ID + "=?;", new String[]{Integer.toString(rowId)});
            }
        }
    }

    public void DeleteDog(Dog dog)
    {
        SQLiteDatabase db = dbHandler.getWritableDatabase();

        db.delete(dbHandler.TABLE_DOGS, DBHandler.KEY_DOG_ID + "=?", new String[]{Integer.toString(dog.id)});
        db.delete(dbHandler.TABLE_DOG_FAVOURITES, DBHandler.KEY_DOGFAVOURITES_DOG_ID + "=?", new String[]{Integer.toString(dog.id)});

        for(int j = 0; j < dog.friends.size(); ++j)
        {
            Dog friend = dog.friends.get(j);


            db.delete(dbHandler.TABLE_DOG_FRIENDS, DBHandler.KEY_DOGFRIENDS_DOG_ID + "=?", new String[]{Integer.toString(dog.id)});
            db.delete(dbHandler.TABLE_DOG_FRIENDS, DBHandler.KEY_DOGFRIENDS_FRIENDDOG_ID + "=?", new String[]{Integer.toString(dog.id)});
        }
    }

    public void DeleteVolunteer(Volunteer volunteer)
    {
        SQLiteDatabase db = dbHandler.getWritableDatabase();

        db.delete(dbHandler.TABLE_VOLUNTEERS, DBHandler.KEY_VOLUNTEER_ID + "=?", new String[]{Integer.toString(volunteer.id)});

        for(int j = 0; j < volunteer.favouriteDogs.size(); ++j)
        {
            Dog friend = volunteer.favouriteDogs.get(j);

            db.delete(dbHandler.TABLE_DOG_FAVOURITES, DBHandler.KEY_DOGFAVOURITES_VOLUNTEER_ID + "=?", new String[]{Integer.toString(volunteer.id)});
        }
    }

    public ArrayList<CageDog> getUnassignedDogs()
    {
        List<Dog> dogs = this.getAllDogsByZone(Constants.CAGE_ZONE_NONE);
        ArrayList<CageDog> result = new ArrayList<CageDog>();

        for(int i = 0; i < dogs.size(); ++i)
        {
            result.add(new CageDog(dogs.get(i), null));
        }
        return result;
    }

    public ArrayList<Dog> getAllInteriorDogsTest()
    {
        List<Dog> dogs = this.getAllDogsTest();
        ArrayList<Dog> result = new ArrayList<Dog>();

        for(int i = 0; i < dogs.size(); ++i)
        {
            if(dogs.get(i).walktype == Constants.WT_INTERIOR)
            {
                result.add(dogs.get(i));
            }
        }

        return result;
    }

    public void updateDogCages(ArrayList<CageDog> cageDogs, boolean isAssigned)
    {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        int actualCageId = -1;
        for(int i = 0; i < cageDogs.size(); ++i)
        {
            CageDog cageDog = cageDogs.get(i);
            if(cageDog.cage != null)
            {
                actualCageId = cageDog.cage.id;
            }

            if(cageDog.dog != null && cageDog.dog.id > 0) {
                ContentValues initialValues = new ContentValues();
                initialValues = new ContentValues();
                initialValues.put(DBHandler.KEY_DOG_ID, cageDog.dog.id);
                //initialValues.put(DBHandler.KEY_DOG_NAME, cageDog.dog.name);
                initialValues.put(DBHandler.KEY_DOG_ID_CAGE, actualCageId);
                /*initialValues.put(DBHandler.KEY_DOG_AGE, cageDog.dog.age);
                initialValues.put(DBHandler.KEY_DOG_LINK, cageDog.dog.link);
                initialValues.put(DBHandler.KEY_DOG_SPECIAL, cageDog.dog.special);
                initialValues.put(DBHandler.KEY_DOG_WALKTYPE, cageDog.dog.walktype);
                initialValues.put(DBHandler.KEY_DOG_OBSERVATIONS, cageDog.dog.observations);*/


                int id = (int) db.insertWithOnConflict(dbHandler.TABLE_DOGS, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
                if (id == -1) {
                    db.update(dbHandler.TABLE_DOGS, initialValues, DBHandler.KEY_DOG_ID + "=?;", new String[]{Integer.toString(cageDog.dog.id)});
                }

            }
        }
    }

    public void getDogFriends(List<Dog> dogs, int position, String table) {
        Dog dog = dogs.get(position);
        List<Dog> friends = new ArrayList<Dog>();
        String[] args = {Integer.toString(dog.id)};
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        String[] columns = {dbHandler.KEY_DOGFRIENDS_ID, dbHandler.KEY_DOGFRIENDS_DOG_ID, dbHandler.KEY_DOGFRIENDS_FRIENDDOG_ID};
        Cursor cursor = db.query(table, columns, dbHandler.KEY_DOGFRIENDS_DOG_ID + "=?", args, null, null, null);
        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_DOGFRIENDS_ID));
            int dogId = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_DOGFRIENDS_DOG_ID));
            int friendDogId = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_DOGFRIENDS_FRIENDDOG_ID));

            if (dog.id == dogId) {
                for (int i = 0; i < dogs.size(); ++i) {
                    if (dogs.get(i).id == friendDogId) {
                        friends.add(dogs.get(i));
                    }
                }
            }
        }
        dog.friends = friends;
    }

    public void getDogFavourites(Volunteer volunteer) {
        Dictionary<Integer,Dog> allDogs = this.getAllDogsDictionary(false);
        ArrayList<Dog> friends = new ArrayList<Dog>();
        String[] args = {Integer.toString(volunteer.id)};
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        String[] columns = {dbHandler.KEY_DOGFAVOURITES_DOG_ID, dbHandler.KEY_DOGFAVOURITES_VOLUNTEER_ID, dbHandler.KEY_DOGFAVOURITES_DOG_ID};
        Cursor cursor = db.query(dbHandler.TABLE_DOG_FAVOURITES, columns, dbHandler.KEY_DOGFAVOURITES_VOLUNTEER_ID + "=?", args, null, null, null);
        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_DOGFAVOURITES_DOG_ID));
            int volunteerId = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_DOGFAVOURITES_VOLUNTEER_ID));
            int favouriteDogId = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_DOGFAVOURITES_DOG_ID));

            Dog dog = allDogs.get(favouriteDogId);
            friends.add(dog);
        }
        volunteer.favouriteDogs = friends;
    }

    public void getDogFavouritesTest(Volunteer volunteer) {
        Dictionary<Integer,Dog> allDogs = this.getAllDogsDictionary(false);
        ArrayList<Dog> friends = new ArrayList<Dog>();
        String[] args = {Integer.toString(volunteer.id)};
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        String[] columns = {dbHandler.KEY_DOGFAVOURITES_DOG_ID, dbHandler.KEY_DOGFAVOURITES_VOLUNTEER_ID, dbHandler.KEY_DOGFAVOURITES_DOG_ID};
        Cursor cursor = db.query(dbHandler.TABLE_DOG_FAVOURITES_TEST, columns, dbHandler.KEY_DOGFAVOURITES_VOLUNTEER_ID + "=?", args, null, null, null);
        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_DOGFAVOURITES_DOG_ID));
            int volunteerId = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_DOGFAVOURITES_VOLUNTEER_ID));
            int favouriteDogId = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_DOGFAVOURITES_DOG_ID));

            Dog dog = allDogs.get(favouriteDogId);
            friends.add(dog);
        }
        volunteer.favouriteDogs = friends;
    }

    //Devuelve una lista con todas las jaulas
    public List<Cage> getAllCages()
    {
        return this.getAllCagesByZone(null);
    }

    //Devuelve una lista con todas las jaulas existentes en la zona 'zone'
    //Si 'zone' = null devuelve una lista con todas las jaulas
    public List<Cage> getAllCagesByZone(String zone)
    {
        List<Cage> cages = new ArrayList<Cage>();
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        String[] columns = {dbHandler.KEY_CAGE_ID, dbHandler.KEY_CAGE_NUM, dbHandler.KEY_CAGE_ZONE};
        String selection = null;
        String[] params = null;
        if(zone != null) {
            selection = dbHandler.KEY_CAGE_ZONE + "=?";
            params = new String[]{zone};
        }
        Cursor cursor =db.query(dbHandler.TABLE_CAGES,columns, selection, params,null,null,null);
        StringBuffer buffer= new StringBuffer();
        while (cursor.moveToNext())
        {
            int id = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_CAGE_ID));
            int num = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_CAGE_NUM));
            String iZone = cursor.getString(cursor.getColumnIndex(dbHandler.KEY_CAGE_ZONE));

            Cage cage = new Cage(id,num,iZone);
            cages.add(cage);
        }
        return cages;
    }

    public List<Volunteer> getAllVolunteers() {
        List<Volunteer> volunteers = new ArrayList<Volunteer>();
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        String[] columns = {dbHandler.KEY_VOLUNTEER_ID, dbHandler.KEY_VOLUNTEER_NAME, dbHandler.KEY_VOLUNTEER_PHONE, dbHandler.KEY_VOLUNTEER_DAY, dbHandler.KEY_VOLUNTEER_OBSERVATIONS};
        Cursor cursor = db.query(dbHandler.TABLE_VOLUNTEERS, columns, null, null, null, null, null);
        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_VOLUNTEER_ID));
            String name = cursor.getString(cursor.getColumnIndex(dbHandler.KEY_VOLUNTEER_NAME));
            String phone = cursor.getString(cursor.getColumnIndex(dbHandler.KEY_VOLUNTEER_PHONE));
            String volunteerDay = cursor.getString(cursor.getColumnIndex(dbHandler.KEY_VOLUNTEER_DAY));
            String observations = cursor.getString(cursor.getColumnIndex(dbHandler.KEY_VOLUNTEER_OBSERVATIONS));

            Volunteer volunteer = new Volunteer(id,name, phone, volunteerDay, observations);
            volunteers.add(volunteer);
        }
        return volunteers;
    }

    public void SaveWalkSolution(Dog[][] walks, List<VolunteerWalks> volunteers)
    {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Dog[][] walksT = new Dog[walks[0].length][walks.length];
        for(int i = 0; i < walksT.length; ++i) {
            for (int j = 0; j < walksT[0].length; ++j) {
                walksT[i][j] = walks[j][i];
            }
        }
        int id = 0;
        for(int i = 0; i < walksT.length; ++i)
        {
            for(int j = 0; j < walksT[0].length + 1; ++j)
            {
                contentValues = new ContentValues();
                if(j == 0)
                {
                    contentValues.put(DBHandler.KEY_WALKS_ID, id);
                    contentValues.put(DBHandler.KEY_WALKS_ROW, i);
                    contentValues.put(DBHandler.KEY_WALKS_COLUMN, j);
                    contentValues.put(DBHandler.KEY_WALKS_VOLUNTEER_ID, volunteers.get(i).id);
                }
                else
                {
                    if(walksT[i][j-1] != null) {
                        contentValues.put(DBHandler.KEY_WALKS_ID, id);
                        contentValues.put(DBHandler.KEY_WALKS_ROW, i);
                        contentValues.put(DBHandler.KEY_WALKS_COLUMN, j);
                        contentValues.put(DBHandler.KEY_WALKS_DOG_ID, walksT[i][j - 1].id);
                    }
                }

                if(contentValues.containsKey(DBHandler.KEY_WALKS_ID)) {
                    db.insert(DBHandler.TABLE_WALKS, null, contentValues);
                }
                ++id;
            }
        }
    }

    public void SaveWalkSolution(ArrayList<VolunteerDog> walks)
    {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        int id = 0;
        int column = 0;
        int row = -1;
        for(int i = 0; i < walks.size(); ++i)
        {
            contentValues = new ContentValues();
            if(walks.get(i).volunteer != null)
            {
                row = row + 1;
                column = 0;
                contentValues.put(DBHandler.KEY_WALKS_ID, id);
                contentValues.put(DBHandler.KEY_WALKS_ROW, row);
                contentValues.put(DBHandler.KEY_WALKS_COLUMN, column);
                contentValues.put(DBHandler.KEY_WALKS_VOLUNTEER_ID, walks.get(i).volunteer.id);
            }
            else if(!walks.get(i).dog.name.isEmpty())
            {
                contentValues.put(DBHandler.KEY_WALKS_ID, id);
                contentValues.put(DBHandler.KEY_WALKS_ROW, row);
                contentValues.put(DBHandler.KEY_WALKS_COLUMN, column);
                contentValues.put(DBHandler.KEY_WALKS_DOG_ID, walks.get(i).dog.id);
            }
            db.insert(DBHandler.TABLE_WALKS, null,contentValues);
            column++;
            ++id;
        }
    }



    public void SaveCleanSolution(ArrayList<ArrayList<Dog>> clean)
    {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        int id = 0;
        for(int i = 0; i < clean.size(); ++i)
        {
            for(int j = 0; j < clean.get(i).size(); ++j)
            {
                contentValues = new ContentValues();
                contentValues.put(DBHandler.KEY_CLEAN_ID, id);
                contentValues.put(DBHandler.KEY_CLEAN_ROW, i);
                contentValues.put(DBHandler.KEY_WALKS_COLUMN, j);
                contentValues.put(DBHandler.KEY_WALKS_DOG_ID, clean.get(i).get(j).id);
                db.insert(DBHandler.TABLE_CLEAN, null,contentValues);
                ++id;
            }
        }
    }

    public void SaveCleanSolution(List<VolunteerDog> clean)
    {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        int id = 0;
        int row = 0;
        int column = 0;
        for(int i = 0; i < clean.size(); ++i)
        {
            if(clean.get(i).dog == null)
            {
                row = clean.get(i).cleanRow - 1;
                column = 0;
            }
            else if (!clean.get(i).dog.name.isEmpty()){
                contentValues = new ContentValues();
                contentValues.put(DBHandler.KEY_CLEAN_ID, id);
                contentValues.put(DBHandler.KEY_CLEAN_ROW, row);
                contentValues.put(DBHandler.KEY_WALKS_COLUMN, column);
                contentValues.put(DBHandler.KEY_WALKS_DOG_ID, clean.get(i).dog.id);
                db.insert(DBHandler.TABLE_CLEAN, null, contentValues);
            }
            ++column;
            ++id;
        }
    }

    public ArrayList<VolunteerDog> GetWalkSolution(int nRows, int nColumns)
    {
        ArrayList<VolunteerDog> walksSolution = new ArrayList<VolunteerDog>();
        VolunteerDog[][] dogMatrix = new VolunteerDog[nRows][nColumns];
        Dictionary<Integer,Dog> dogs = this.getAllDogsDictionary();
        Dictionary<Integer,Volunteer> volunteers = this.getAllVolunteersDictionary();
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        String[] columns = {dbHandler.KEY_WALKS_ID, dbHandler.KEY_WALKS_ROW, dbHandler.KEY_WALKS_COLUMN, dbHandler.KEY_WALKS_VOLUNTEER_ID, dbHandler.KEY_WALKS_DOG_ID};
        Cursor cursor =db.query(dbHandler.TABLE_WALKS,columns,null, null,null,null,null);
        StringBuffer buffer= new StringBuffer();
        while (cursor.moveToNext())
        {
            int id = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_WALKS_ID));
            int row = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_WALKS_ROW));
            int column = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_WALKS_COLUMN));
            int volunteerId = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_WALKS_VOLUNTEER_ID));
            int dogId = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_WALKS_DOG_ID));

            VolunteerDog volunteerDog = new VolunteerDog(dogs.get(dogId),volunteers.get(volunteerId));
            dogMatrix[row][column] = volunteerDog;
        }

        for(int i = 0; i < dogMatrix.length; ++i)
        {
            for(int j = 0; j < dogMatrix[i].length; ++j)
            {
                if(dogMatrix[i][j] == null)
                {
                    walksSolution.add(new VolunteerDog(new Dog(Constants.DEFAULT_DOG_NAME), null));
                }
                else {
                    walksSolution.add(dogMatrix[i][j]);
                }
            }
        }
        return walksSolution;
    }

    public ArrayList<VolunteerDog> GetCleanSolution(int rows, int nColumns)
    {
        ArrayList<VolunteerDog> cleanSolution = new ArrayList<VolunteerDog>();
        ArrayList<ArrayList<VolunteerDog>> cleanMatrix = new ArrayList<ArrayList<VolunteerDog>>();
        for(int i = 0; i < rows; i++)
        {
            cleanMatrix.add(i,new ArrayList<VolunteerDog>());
        }
        Dictionary<Integer,Dog> dogs = this.getAllDogsDictionary();
        Dictionary<Integer,Volunteer> volunteers = this.getAllVolunteersDictionary();
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        String[] columns = {dbHandler.KEY_CLEAN_ID, dbHandler.KEY_CLEAN_ROW, dbHandler.KEY_CLEAN_COLUMN, dbHandler.KEY_CLEAN_DOG_ID};
        Cursor cursor =db.query(dbHandler.TABLE_CLEAN,columns,null, null,null,null,null);
        StringBuffer buffer= new StringBuffer();
        while (cursor.moveToNext())
        {
            int id = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_CLEAN_ID));
            int row = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_CLEAN_ROW));
            int column = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_CLEAN_COLUMN));
            int dogId = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_CLEAN_DOG_ID));

            VolunteerDog volunteerDog = new VolunteerDog(dogs.get(dogId),null);
            cleanMatrix.get(row).add(volunteerDog);
        }

        for(int i = 0; i < cleanMatrix.size(); ++i)
        {
            int dogsToAdd = 0;
            if(cleanMatrix.get(i).size() == 0)
            {
                cleanSolution.add(new VolunteerDog(null, i + 1, true));
                ++dogsToAdd;
            }
            for(int j = 0; j < cleanMatrix.get(i).size(); ++j) {
                if ((dogsToAdd % nColumns) == 0 && j == 0) {
                    cleanSolution.add(new VolunteerDog(null, i + 1, true));
                    ++dogsToAdd;
                } else if ((dogsToAdd % nColumns) == 0) {
                    cleanSolution.add(new VolunteerDog(null, i + 1, false));
                    ++dogsToAdd;
                }
                cleanSolution.add(cleanMatrix.get(i).get(j));
                ++dogsToAdd;
            }
            while((dogsToAdd % nColumns) != 0)
            {
                cleanSolution.add(new VolunteerDog(new Dog(Constants.DEFAULT_DOG_NAME), null));
                ++dogsToAdd;
            }
        }
        return cleanSolution;
    }

    public ArrayList<CageDog> getDogsPerCage(int nColumns, String zone)
    {
        ArrayList<CageDog> cageDogs = new ArrayList<CageDog>();
        List<Dog> dogs = this.getAllDogsByZone(zone);
        Dictionary<Integer,Cage> cages = this.getAllCagesDictionaryByZone(zone);

        Dog[] dogsArray = new Dog[dogs.size()];
        MergeUtils.MergeByIdCage(dogs.toArray(dogsArray));
        List<Dog> dogsList = new ArrayList<Dog>();
        for(int i = 0; i < dogsArray.length; ++i)
        {
            dogsList.add(dogsArray[i]);
        }

        int position = 0;
        int lastDogIdCage = 0;

        if(dogsList.size() > 0)
        {
            lastDogIdCage = dogsList.get(0).idCage - 1;
        }

        int iRow = 1;
        for(int i = 0; i < dogsList.size(); ++i)
        {
            Dog dog = dogsList.get(i);

            if(lastDogIdCage != dog.idCage) {
                if((position % nColumns) != 0) {
                    while ((position % nColumns) != 0) {
                        cageDogs.add(new CageDog(new Dog(Constants.DEFAULT_DOG_NAME), null));
                        ++position;
                    }
                    ++iRow;
                }


                int tmpId = lastDogIdCage + 1;
                while(tmpId != dog.idCage)
                {
                    cageDogs.add(new CageDog(cages.get(tmpId), iRow, true));
                    ++position;
                    if((position % nColumns) != 0)
                    {
                        while ((position % nColumns) != 0) {
                            cageDogs.add(new CageDog(new Dog(Constants.DEFAULT_DOG_NAME), null));
                            ++position;
                        }
                        ++iRow;
                    }

                    tmpId++;
                }
            }

            if ((position % nColumns) == 0 && lastDogIdCage != dog.idCage) {
                cageDogs.add(new CageDog(cages.get(dog.idCage), iRow, true));
                ++position;
            } else if ((position % nColumns) == 0) {
                cageDogs.add(new CageDog(cages.get(dog.idCage), iRow, false));
                ++position;
            }
            cageDogs.add(new CageDog(dog, null));
            ++position;
            if((position % nColumns) == 0)
            {
                ++iRow;
            }
            lastDogIdCage = dog.idCage;
        }

        if((position % nColumns) != 0) {
            while ((position % nColumns) != 0) {
                cageDogs.add(new CageDog(new Dog(Constants.DEFAULT_DOG_NAME), null));
                ++position;
            }
            ++iRow;
        }
        return cageDogs;
    }

    public void SaveSelectedVolunteers(List<VolunteerWalks> volunteers)
    {
        this.CleanSelectedVolunteers();
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        int id = 0;
        int row = 0;
        int column = 0;
        for(int i = 0; i < volunteers.size(); ++i)
        {
            VolunteerWalks volunteer = volunteers.get(i);
            contentValues = new ContentValues();
            contentValues.put(DBHandler.KEY_SELECTEDVOLUNTEERS_ID, id);
            contentValues.put(DBHandler.KEY_SELECTEDVOLUNTEERS_VOLUNTEER_ID, volunteer.id);
            contentValues.put(DBHandler.KEY_SELECTEDVOLUNTEERS_VOLUNTEER_NAME, volunteer.name);
            contentValues.put(DBHandler.KEY_SELECTEDVOLUNTEERS_CLEAN, volunteer.clean);
            contentValues.put(DBHandler.KEY_SELECTEDVOLUNTEERS_WALK_1, volunteer.walk1);
            contentValues.put(DBHandler.KEY_SELECTEDVOLUNTEERS_WALK_2, volunteer.walk2);
            contentValues.put(DBHandler.KEY_SELECTEDVOLUNTEERS_WALK_3, volunteer.walk3);
            contentValues.put(DBHandler.KEY_SELECTEDVOLUNTEERS_WALK_4, volunteer.walk4);
            contentValues.put(DBHandler.KEY_SELECTEDVOLUNTEERS_WALK_5, volunteer.walk5);
            contentValues.put(DBHandler.KEY_SELECTEDVOLUNTEERS_NWALKS, volunteer.nPaseos);

            db.insert(DBHandler.TABLE_SELECTED_VOLUNTEERS, null, contentValues);
            ++id;
        }
    }

    public void SaveSelectedVolunteersTest(List<VolunteerWalks> volunteers)
    {
        this.CleanSelectedVolunteers();
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        int id = 1;
        for(int i = 0; i < volunteers.size(); ++i)
        {
            VolunteerWalks volunteer = volunteers.get(i);
            contentValues = new ContentValues();
            contentValues.put(DBHandler.KEY_SELECTEDVOLUNTEERS_ID, id);
            contentValues.put(DBHandler.KEY_SELECTEDVOLUNTEERS_VOLUNTEER_ID, volunteer.id);
            contentValues.put(DBHandler.KEY_SELECTEDVOLUNTEERS_VOLUNTEER_NAME, volunteer.name);
            contentValues.put(DBHandler.KEY_SELECTEDVOLUNTEERS_CLEAN, volunteer.clean);
            contentValues.put(DBHandler.KEY_SELECTEDVOLUNTEERS_WALK_1, volunteer.walk1);
            contentValues.put(DBHandler.KEY_SELECTEDVOLUNTEERS_WALK_2, volunteer.walk2);
            contentValues.put(DBHandler.KEY_SELECTEDVOLUNTEERS_WALK_3, volunteer.walk3);
            contentValues.put(DBHandler.KEY_SELECTEDVOLUNTEERS_WALK_4, volunteer.walk4);
            contentValues.put(DBHandler.KEY_SELECTEDVOLUNTEERS_WALK_5, volunteer.walk5);
            contentValues.put(DBHandler.KEY_SELECTEDVOLUNTEERS_NWALKS, volunteer.nPaseos);

            db.insert(DBHandler.TABLE_SELECTED_VOLUNTEERS_TEST, null, contentValues);
            ++id;
        }
    }

    public ArrayList<VolunteerWalks> getAllSelectedVolunteers() {
        ArrayList<VolunteerWalks> volunteers = new ArrayList<VolunteerWalks>();
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        String[] columns = {dbHandler.KEY_SELECTEDVOLUNTEERS_VOLUNTEER_ID, dbHandler.KEY_SELECTEDVOLUNTEERS_VOLUNTEER_NAME, dbHandler.KEY_SELECTEDVOLUNTEERS_CLEAN, dbHandler.KEY_SELECTEDVOLUNTEERS_WALK_1,
                            dbHandler.KEY_SELECTEDVOLUNTEERS_WALK_2, dbHandler.KEY_SELECTEDVOLUNTEERS_WALK_3, dbHandler.KEY_SELECTEDVOLUNTEERS_WALK_4, dbHandler.KEY_SELECTEDVOLUNTEERS_WALK_5,
                            dbHandler.KEY_SELECTEDVOLUNTEERS_NWALKS};
        Cursor cursor = db.query(dbHandler.TABLE_SELECTED_VOLUNTEERS, columns, null, null, null, null, null);
        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {
            int idVolunteer = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_SELECTEDVOLUNTEERS_VOLUNTEER_ID));
            String nameVolunteer = cursor.getString(cursor.getColumnIndex(dbHandler.KEY_SELECTEDVOLUNTEERS_VOLUNTEER_NAME));
            int clean = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_SELECTEDVOLUNTEERS_CLEAN));
            int walk1 = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_SELECTEDVOLUNTEERS_WALK_1));
            int walk2 = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_SELECTEDVOLUNTEERS_WALK_2));
            int walk3 = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_SELECTEDVOLUNTEERS_WALK_3));
            int walk4 = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_SELECTEDVOLUNTEERS_WALK_4));
            int walk5 = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_SELECTEDVOLUNTEERS_WALK_5));
            int nPaseos = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_SELECTEDVOLUNTEERS_NWALKS));

            VolunteerWalks volunteer = new VolunteerWalks(idVolunteer, nameVolunteer, clean, walk1, walk2, walk3, walk4, walk5, nPaseos);
            this.getDogFavourites(volunteer);
            volunteers.add(volunteer);
        }
        return volunteers;
    }

    public ArrayList<VolunteerWalks> getAllSelectedVolunteersTest() {
        ArrayList<VolunteerWalks> volunteers = new ArrayList<VolunteerWalks>();
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        String[] columns = {dbHandler.KEY_SELECTEDVOLUNTEERS_VOLUNTEER_ID, dbHandler.KEY_SELECTEDVOLUNTEERS_VOLUNTEER_NAME, dbHandler.KEY_SELECTEDVOLUNTEERS_CLEAN, dbHandler.KEY_SELECTEDVOLUNTEERS_WALK_1,
                dbHandler.KEY_SELECTEDVOLUNTEERS_WALK_2, dbHandler.KEY_SELECTEDVOLUNTEERS_WALK_3, dbHandler.KEY_SELECTEDVOLUNTEERS_WALK_4, dbHandler.KEY_SELECTEDVOLUNTEERS_WALK_5,
                dbHandler.KEY_SELECTEDVOLUNTEERS_NWALKS};
        Cursor cursor = db.query(dbHandler.TABLE_SELECTED_VOLUNTEERS_TEST, columns, null, null, null, null, null);
        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {
            int idVolunteer = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_SELECTEDVOLUNTEERS_VOLUNTEER_ID));
            String nameVolunteer = cursor.getString(cursor.getColumnIndex(dbHandler.KEY_SELECTEDVOLUNTEERS_VOLUNTEER_NAME));
            int clean = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_SELECTEDVOLUNTEERS_CLEAN));
            int walk1 = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_SELECTEDVOLUNTEERS_WALK_1));
            int walk2 = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_SELECTEDVOLUNTEERS_WALK_2));
            int walk3 = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_SELECTEDVOLUNTEERS_WALK_3));
            int walk4 = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_SELECTEDVOLUNTEERS_WALK_4));
            int walk5 = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_SELECTEDVOLUNTEERS_WALK_5));
            int nPaseos = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_SELECTEDVOLUNTEERS_NWALKS));

            VolunteerWalks volunteer = new VolunteerWalks(idVolunteer, nameVolunteer, clean, walk1, walk2, walk3, walk4, walk5, nPaseos);
            this.getDogFavourites(volunteer);
            volunteers.add(volunteer);
        }
        return volunteers;
    }

    public void removeAll()
    {
        // db.delete(String tableName, String whereClause, String[] whereArgs);
        // If whereClause is null, it will delete all rows.
        SQLiteDatabase db = dbHandler.getWritableDatabase(); // helper is object extends SQLiteOpenHelper
        db.delete(DBHandler.TABLE_DOGS, null, null);
        db.delete(DBHandler.TABLE_CAGES, null, null);
        db.delete(DBHandler.TABLE_VOLUNTEERS, null, null);
        db.delete(DBHandler.TABLE_WALKS, null, null);
        db.delete(DBHandler.TABLE_CLEAN, null, null);
        db.delete(DBHandler.TABLE_SELECTED_VOLUNTEERS, null, null);

        db.delete(DBHandler.TABLE_SELECTED_VOLUNTEERS_TEST, null, null);
    }

    public int GetMaxIdDogsTable()
    {
        return this.GetMaxId(dbHandler.TABLE_DOGS, dbHandler.KEY_DOG_ID);
    }

    public int GetMaxIdVolunteersTable()
    {
        return this.GetMaxId(dbHandler.TABLE_VOLUNTEERS, dbHandler.KEY_VOLUNTEER_ID);
    }

    public int GetMaxId(String tableName, String idKey) {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        Cursor getMaxId = db.rawQuery("select max(" + idKey + ") as id from " + tableName, null);
        int maxId = 0;
        while (getMaxId.moveToNext()) {
            maxId = getMaxId.getInt(0);
        }
        return maxId;
    }

    public int GetMinIdDogsTable()
    {
        return this.GetMinId(dbHandler.TABLE_DOGS, dbHandler.KEY_DOG_ID);
    }

    public int GetMinIdVolunteersTable()
    {
        return this.GetMinId(dbHandler.TABLE_VOLUNTEERS, dbHandler.KEY_VOLUNTEER_ID );
    }

    public int GetMinId(String tableName, String idKey) {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        Cursor getMinId = db.rawQuery("select min(" + idKey + ") as id from " + tableName, null);
        int minId = 0;
        while (getMinId.moveToNext()) {
            minId = getMinId.getInt(0);
        }
        return minId;
    }

    public boolean IsFirstTime()
    {
        SQLiteDatabase db = dbHandler.getWritableDatabase();

        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+ dbHandler.TABLE_DOGS +"'", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return false;
            }
            cursor.close();
        }
        return true;
    }

    public void CleanSolutionsTables()
    {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        dbHandler.EraseSolutionTables(db);
    }

    public void onUpgrade()
    {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        if(this.IsFirstTime())
        {
            dbHandler.onUpgrade(db,0,0);
        }
    }

    public void CleanSelectedVolunteers()
    {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        dbHandler.CleanSelectedVolunteers(db);
    }

    public void CleanDogs()
    {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        dbHandler.CleanDogs(db);
    }

    public void CleanTestTables()
    {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        dbHandler.EraseSolutionTables(db);
        dbHandler.CleanTestTables(db);
    }
    static class DBHandler extends SQLiteOpenHelper {
        //All static variables
        //Database Version
        private static final int DATABASE_VERSION = 2;
        //Database Name
        private static final String DATABASE_NAME = "apan";

        //Dogs table name
        private static final String TABLE_DOGS = "dogs";
        private static final String TABLE_DOGS_TEST  = "dogsTest";
        //Dogs table column names
        private static final String KEY_DOG_ID = "id";
        private static final String KEY_DOG_NAME = "name";
        private static final String KEY_DOG_ID_CAGE = "idCage";
        private static final String KEY_DOG_AGE = "age";
        private static final String KEY_DOG_LINK = "link";
        private static final String KEY_DOG_SPECIAL = "special";
        private static final String KEY_DOG_WALKTYPE = "walktype";
        private static final String KEY_DOG_OBSERVATIONS = "observations";

        //Cage table name
        private static final String TABLE_CAGES = "cages";
        //Cage table column names
        private static final String KEY_CAGE_ID = "id";
        private static final String KEY_CAGE_NUM = "numCage";
        private static final String KEY_CAGE_ZONE = "zone";

        //Volunteer table name
        private static final String TABLE_VOLUNTEERS = "volunteers";
        //Volunteer table column names
        private static final String KEY_VOLUNTEER_ID = "id";
        private static final String KEY_VOLUNTEER_NAME = "name";
        private static final String KEY_VOLUNTEER_PHONE = "phone";
        private static final String KEY_VOLUNTEER_DAY = "volunteerDay";
        private static final String KEY_VOLUNTEER_OBSERVATIONS = "observations";

        //Walks table name
        private static final String TABLE_WALKS = "walks";

        //Walks table column names
        private static final String KEY_WALKS_ID = "id";
        private static final String KEY_WALKS_ROW = "irow";
        private static final String KEY_WALKS_COLUMN = "icolumn";
        private static final String KEY_WALKS_DOG_ID = "idDog";
        private static final String KEY_WALKS_VOLUNTEER_ID = "idVolunteer";

        //Clean table name
        private static final String TABLE_CLEAN = "clean";

        //Clean table column names
        private static final String KEY_CLEAN_ID = "id";
        private static final String KEY_CLEAN_ROW = "irow";
        private static final String KEY_CLEAN_COLUMN = "icolumn";
        private static final String KEY_CLEAN_DOG_ID = "idDog";

        //Selected volunteers table name
        private static final String TABLE_SELECTED_VOLUNTEERS = "selectedVolunteers";
        private static final String TABLE_SELECTED_VOLUNTEERS_TEST = "selectedVolunteersTest";

        //Selected volunteers column names
        private static final String KEY_SELECTEDVOLUNTEERS_ID = "id";
        private static final String KEY_SELECTEDVOLUNTEERS_VOLUNTEER_ID = "idVolunteer";
        private static final String KEY_SELECTEDVOLUNTEERS_VOLUNTEER_NAME = "nameVolunteer";
        private static final String KEY_SELECTEDVOLUNTEERS_CLEAN = "clean";
        private static final String KEY_SELECTEDVOLUNTEERS_WALK_1 = "walk1";
        private static final String KEY_SELECTEDVOLUNTEERS_WALK_2 = "walk2";
        private static final String KEY_SELECTEDVOLUNTEERS_WALK_3 = "walk3";
        private static final String KEY_SELECTEDVOLUNTEERS_WALK_4 = "walk4";
        private static final String KEY_SELECTEDVOLUNTEERS_WALK_5 = "walk5";
        private static final String KEY_SELECTEDVOLUNTEERS_NWALKS = "nPaseos";

        //Dog friends table name
        private static final String TABLE_DOG_FRIENDS = "dogFriends";
        private static final String TABLE_DOG_FRIENDS_TEST = "dogFriendsTest";


        //Dog friends column names
        private static final String KEY_DOGFRIENDS_ID = "id";
        private static final String KEY_DOGFRIENDS_DOG_ID = "idDog";
        private static final String KEY_DOGFRIENDS_FRIENDDOG_ID = "idFriendDog";

        //Dog favourites table name
        private static final String TABLE_DOG_FAVOURITES = "dogFavourites";
        private static final String TABLE_DOG_FAVOURITES_TEST = "dogFavouritesTest";

        //Dog favourites column names
        private static final String KEY_DOGFAVOURITES_ID = "id";
        private static final String KEY_DOGFAVOURITES_VOLUNTEER_ID = "idVolunteer";
        private static final String KEY_DOGFAVOURITES_DOG_ID = "idFavouriteDog";

        private void insertCages(SQLiteDatabase db)
        {
            String insertCages = "INSERT INTO " + TABLE_CAGES + "(" + KEY_CAGE_ID + "," + KEY_CAGE_NUM + "," + KEY_CAGE_ZONE +") VALUES(";
            db.execSQL(insertCages + 1 + "," + 1 + "," + "'XENILES');");
            db.execSQL(insertCages + 2 + "," + 2 + "," + "'XENILES');");
            db.execSQL(insertCages + 3 + "," + 3 + "," + "'XENILES');");
            db.execSQL(insertCages + 4 + "," + 4 + "," + "'XENILES');");
            db.execSQL(insertCages + 5 + "," + 5 + "," + "'XENILES');");
            db.execSQL(insertCages + 6 + "," + 6 + "," + "'XENILES');");
            db.execSQL(insertCages + 7 + "," + 7 + "," + "'XENILES');");
            db.execSQL(insertCages + 8 + "," + 8 + "," + "'XENILES');");
            db.execSQL(insertCages + 9 + "," + 9 + "," + "'XENILES');");
            db.execSQL(insertCages + 10 + "," + 10 + "," + "'XENILES');");
            db.execSQL(insertCages + 11 + "," + 11 + "," + "'XENILES');");
            db.execSQL(insertCages + 12 + "," + 12 + "," + "'XENILES');");
            db.execSQL(insertCages + 13 + "," + 13 + "," + "'XENILES');");
            db.execSQL(insertCages + 14 + "," + 14 + "," + "'XENILES');");
            db.execSQL(insertCages + 15 + "," + 15 + "," + "'XENILES');");
            db.execSQL(insertCages + 16 + "," + 16 + "," + "'XENILES');");
            db.execSQL(insertCages + 17 + "," + 17 + "," + "'XENILES');");
            db.execSQL(insertCages + 18 + "," + 18 + "," + "'XENILES');");
            db.execSQL(insertCages + 19 + "," + 19 + "," + "'XENILES');");
            db.execSQL(insertCages + 20 + "," + 20 + "," + "'XENILES');");
            db.execSQL(insertCages + 21 + "," + 21 + "," + "'XENILES');");
            db.execSQL(insertCages + 22 + "," + 22 + "," + "'XENILES');");
            db.execSQL(insertCages + 23 + "," + 23 + "," + "'XENILES');");
            db.execSQL(insertCages + 24 + "," + 24 + "," + "'PATIOS');");
            db.execSQL(insertCages + 25 + "," + 25 + "," + "'PATIOS');");
            db.execSQL(insertCages + 26 + "," + 26 + "," + "'PATIOS');");
            db.execSQL(insertCages + 27 + "," + 27 + "," + "'PATIOS');");
            db.execSQL(insertCages + 28 + "," + 28 + "," + "'PATIOS');");
            db.execSQL(insertCages + 29 + "," + 29 + "," + "'PATIOS');");
            db.execSQL(insertCages + 30 + "," + 30 + "," + "'PATIOS');");
            db.execSQL(insertCages + 31 + "," + 31 + "," + "'PATIOS');");
            db.execSQL(insertCages + 32 + "," + 32 + "," + "'CUARENTENAS');");
            db.execSQL(insertCages + 33 + "," + 33 + "," + "'CUARENTENAS');");
            db.execSQL(insertCages + 34 + "," + 34 + "," + "'CUARENTENAS');");
            db.execSQL(insertCages + 35 + "," + 35 + "," + "'CUARENTENAS');");
            db.execSQL(insertCages + 36 + "," + 36 + "," + "'CUARENTENAS');");
            db.execSQL(insertCages + 37 + "," + 37 + "," + "'CUARENTENAS');");
            db.execSQL(insertCages + 38 + "," + 38 + "," + "'CUARENTENAS');");
            db.execSQL(insertCages + 39 + "," + 39 + "," + "'CUARENTENAS');");
            db.execSQL(insertCages + 40 + "," + 40 + "," + "'CUARENTENAS');");
            db.execSQL(insertCages + 41 + "," + 41 + "," + "'CUARENTENAS');");
            db.execSQL(insertCages + 42 + "," + 42 + "," + "'CUARENTENAS');");
            db.execSQL(insertCages + 43 + "," + 43 + "," + "'CUARENTENAS');");


        }

        private void insertDogs(SQLiteDatabase db)
        {
            String insertDogs = "INSERT INTO " + TABLE_DOGS + "(" + KEY_DOG_ID + "," + KEY_DOG_NAME + "," + KEY_DOG_ID_CAGE + "," + KEY_DOG_WALKTYPE + "," + KEY_DOG_SPECIAL +") VALUES(";
            //0 No sale
            //1 Interior
            //2 Exterior
            db.execSQL(insertDogs + "1,'Lori',1,2,0);");
            db.execSQL(insertDogs + "2,'Puyol',2,2,0);");
            db.execSQL(insertDogs + "3,'Thor',3,2,0);");
            db.execSQL(insertDogs + "4,'Trixie',5,2,0);");
            db.execSQL(insertDogs + "5,'Quim',6,2,0);");
            db.execSQL(insertDogs + "6,'Kratos',6,2,1);");
            db.execSQL(insertDogs + "7,'Maxi',7,1,0);");
            db.execSQL(insertDogs + "8,'Milady',8,2,0);");
            db.execSQL(insertDogs + "9,'Atenea',10,2,0);");
            db.execSQL(insertDogs + "10,'Pèsol',11,2,0);");
            db.execSQL(insertDogs + "11,'Cristal',11,2,0);");
            db.execSQL(insertDogs + "12,'Geralt',12,1,0);");
            db.execSQL(insertDogs + "13,'Ralts',13,2,0);");
            db.execSQL(insertDogs + "14,'Nika',14,2,0);");
            db.execSQL(insertDogs + "15,'Mara',15,2,0);");
            db.execSQL(insertDogs + "16,'Vida',16,2,0);");
            db.execSQL(insertDogs + "17,'Nicky',17,2,0);");
            db.execSQL(insertDogs + "18,'Chelin',18,2,0);");
            db.execSQL(insertDogs + "19,'Chelsea',19,2,0);");
            db.execSQL(insertDogs + "20,'Argus',20,1,0);");
            db.execSQL(insertDogs + "21,'Jess',20,1,0);");
            db.execSQL(insertDogs + "22,'Max',21,2,0);");
            db.execSQL(insertDogs + "23,'Canela',21,2,0);");
            db.execSQL(insertDogs + "24,'Blacky',21,1,0);");
            db.execSQL(insertDogs + "25,'Titus',22,2,0);");
            db.execSQL(insertDogs + "26,'Amiguets',22,1,0);");
            db.execSQL(insertDogs + "27,'Penny',23,1,0);");
            db.execSQL(insertDogs + "28,'Miam',23,1,0);");
            db.execSQL(insertDogs + "29,'Dardo',23,1,0);");

            db.execSQL(insertDogs + "30,'Ter',24,0,0);");
            db.execSQL(insertDogs + "31,'Sweet',24,0,0);");
            db.execSQL(insertDogs + "32,'Dàlia',24,0,0);");

            db.execSQL(insertDogs + "34,'Minimax',25,2,0);");
            db.execSQL(insertDogs + "35,'Dolça',25,2,0);");
            db.execSQL(insertDogs + "36,'Bamba',25,2,0);");
            db.execSQL(insertDogs + "37,'Calipa',25,0,0);");

            db.execSQL(insertDogs + "38,'Widow',26,2,0);");
            db.execSQL(insertDogs + "39,'Onar',26,0,0);");
            db.execSQL(insertDogs + "40,'Marró',26,0,0);");

            db.execSQL(insertDogs + "41,'Toones',27,2,0);");
            db.execSQL(insertDogs + "42,'Canelo',27,2,0);");
            db.execSQL(insertDogs + "43,'Sira',27,0,0);");

            db.execSQL(insertDogs + "44,'Bull',30,2,0);");
            db.execSQL(insertDogs + "45,'Maya',30,2,0);");
            db.execSQL(insertDogs + "46,'6 hunters',29,0,0);");

            db.execSQL(insertDogs + "47,'Roc',32,2,0);");
            db.execSQL(insertDogs + "48,'Ida',32,2,0);");
            db.execSQL(insertDogs + "49,'Juliette',33,2,0);");
            db.execSQL(insertDogs + "50,'Mar',34,2,0);");
            db.execSQL(insertDogs + "51,'Dartacan',35,2,0);");
            db.execSQL(insertDogs + "52,'Silke',37,2,0);");
            db.execSQL(insertDogs + "53,'Stan',37,2,0);");

        }

        private void insertVolunteers(SQLiteDatabase db)
        {
            String insertVolunteers = "INSERT INTO " + TABLE_VOLUNTEERS + "(" + KEY_VOLUNTEER_ID + "," + KEY_VOLUNTEER_NAME + "," + KEY_VOLUNTEER_DAY +") VALUES(";
            db.execSQL(insertVolunteers + 1 + ",'Ignasi','S');");
            db.execSQL(insertVolunteers + 2 + ",'Esther','S');");
            db.execSQL(insertVolunteers + 3 + ",'Sònia','S');");
            db.execSQL(insertVolunteers + 4 + ",'Àlex Bravo','S');");
            db.execSQL(insertVolunteers + 5 + ",'Guillem','S');");
            db.execSQL(insertVolunteers + 6 + ",'Lídia','S');");
            db.execSQL(insertVolunteers + 7 + ",'Sergio','S');");
            db.execSQL(insertVolunteers + 8 + ",'Núria','S');");
            db.execSQL(insertVolunteers + 9 + ",'Laura','S');");
            db.execSQL(insertVolunteers + 10 + ",'Eli','S');");
            db.execSQL(insertVolunteers + 11 + ",'Laia','S');");
            db.execSQL(insertVolunteers + 12 + ",'Dani','S');");
            db.execSQL(insertVolunteers + 13 + ",'Alba Roig','S');");
            db.execSQL(insertVolunteers + 14 + ",'Alba L','S');");

            db.execSQL(insertVolunteers + 15 + ",'Àlex','D');");
            db.execSQL(insertVolunteers + 16 + ",'Andrea','D');");
            db.execSQL(insertVolunteers + 17 + ",'Elena','D');");
            db.execSQL(insertVolunteers + 18 + ",'Asun','D');");
            db.execSQL(insertVolunteers + 19 + ",'Anna B.','D');");
            db.execSQL(insertVolunteers + 20 + ",'Ramón','D');");
            db.execSQL(insertVolunteers + 21 + ",'Elisenda','D');");
            db.execSQL(insertVolunteers + 22 + ",'Montse Mambo','D');");
            db.execSQL(insertVolunteers + 23 + ",'Yannick','D');");
            db.execSQL(insertVolunteers + 24 + ",'Carles','D');");
            db.execSQL(insertVolunteers + 25 + ",'Anna','D');");
            db.execSQL(insertVolunteers + 26 + ",'Alba Rom','D');");
            db.execSQL(insertVolunteers + 27 + ",'Josep','D');");
            db.execSQL(insertVolunteers + 28 + ",'Abel','D');");
            db.execSQL(insertVolunteers + 29 + ",'Jara','D');");
            db.execSQL(insertVolunteers + 30 + ",'Albert','D');");
        }

        private void insertDogFavourites(SQLiteDatabase db)
        {
            String insertVolunteers = "INSERT INTO " + TABLE_DOG_FAVOURITES + "(" + KEY_DOGFAVOURITES_ID + "," + KEY_DOGFAVOURITES_VOLUNTEER_ID + "," + KEY_DOGFAVOURITES_DOG_ID +") VALUES(";


            db.execSQL(insertVolunteers + 1 + "," + 17 + "," + 44 + ");");
            db.execSQL(insertVolunteers + 2 + "," + 15 + "," + 14 + ");");
            db.execSQL(insertVolunteers + 3 + "," + 15 + "," + 5 + ");");
            db.execSQL(insertVolunteers + 4 + "," + 15 + "," + 6 + ");");
            db.execSQL(insertVolunteers + 5 + "," + 15 + "," + 15 + ");");
            db.execSQL(insertVolunteers + 6 + "," + 19 + "," + 16 + ");");
            db.execSQL(insertVolunteers + 7 + "," + 19 + "," + 30 + ");");
            db.execSQL(insertVolunteers + 8 + "," + 22 + "," + 2 + ");");
            db.execSQL(insertVolunteers + 9 + "," + 22 + "," + 38 + ");");
            db.execSQL(insertVolunteers + 10 + "," + 18 + "," + 18 + ");");
            db.execSQL(insertVolunteers + 11 + "," + 21 + "," + 51 + ");");
            db.execSQL(insertVolunteers + 12 + "," + 21 + "," + 10 + ");");
            db.execSQL(insertVolunteers + 13 + "," + 16 + "," + 36 + ");");
            db.execSQL(insertVolunteers + 14 + "," + 16 + "," + 25 + ");");
            db.execSQL(insertVolunteers + 15 + "," + 16 + "," + 5 + ");");
            db.execSQL(insertVolunteers + 16 + "," + 16 + "," + 22 + ");");
            db.execSQL(insertVolunteers + 17 + "," + 7 + "," + 3 + ");");
            db.execSQL(insertVolunteers + 18 + "," + 29 + "," + 25 + ");");
            db.execSQL(insertVolunteers + 19 + "," + 30 + "," + 4 + ");");
            db.execSQL(insertVolunteers + 20 + "," + 30 + "," + 17 + ");");
            db.execSQL(insertVolunteers + 21 + "," + 20 + "," + 45 + ");");
            db.execSQL(insertVolunteers + 22 + "," + 20 + "," + 42 + ");");

            db.execSQL(insertVolunteers + 23 + "," + 1 + "," + 15 + ");");
            db.execSQL(insertVolunteers + 24 + "," + 1 + "," + 6 + ");");
            db.execSQL(insertVolunteers + 25 + "," + 2 + "," + 25 + ");");
            db.execSQL(insertVolunteers + 26 + "," + 2 + "," + 5 + ");");
            db.execSQL(insertVolunteers + 27 + "," + 2 + "," + 6 + ");");
            db.execSQL(insertVolunteers + 28 + "," + 5 + "," + 16 + ");");
            db.execSQL(insertVolunteers + 29 + "," + 6 + "," + 18 + ");");
            db.execSQL(insertVolunteers + 30 + "," + 10 + "," + 10 + ");");
            db.execSQL(insertVolunteers + 31 + "," + 13 + "," + 25 + ");");
            db.execSQL(insertVolunteers + 32 + "," + 14 + "," + 22 + ");");
        }

        private void insertFriendDogs(SQLiteDatabase db)
        {
            String insertFriendDogs = "INSERT INTO " + TABLE_DOG_FRIENDS + "(" + KEY_DOGFRIENDS_ID + "," + KEY_DOGFRIENDS_DOG_ID + "," + KEY_DOGFRIENDS_FRIENDDOG_ID +") VALUES(";
            db.execSQL(insertFriendDogs + 1 + "," + 7 + "," + 25 + ");");
            db.execSQL(insertFriendDogs + 2 + "," + 25 + "," + 7 + ");");;
        }

        public DBHandler(Context context)
        {
            super(context,DATABASE_NAME,null,DATABASE_VERSION);
        }

        //Creating Tables
        @Override
        public void onCreate(SQLiteDatabase db)
        {
            String CREATE_DOGS_TABLE = "CREATE TABLE " + TABLE_DOGS + "("
                    + KEY_DOG_ID + " INTEGER PRIMARY KEY,"
                    + KEY_DOG_NAME + " TEXT,"
                    + KEY_DOG_ID_CAGE + " INTEGER,"
                    + KEY_DOG_AGE + " INTEGER,"
                    + KEY_DOG_LINK + " TEXT,"
                    + KEY_DOG_SPECIAL + " BOOLEAN,"
                    + KEY_DOG_WALKTYPE + " TINYINY,"
                    + KEY_DOG_OBSERVATIONS + " TEXT" + ")";
            String CREATE_CAGES_TABLE = "CREATE TABLE " + TABLE_CAGES + "("
                    + KEY_CAGE_ID + " INTEGER PRIMARY KEY,"
                    + KEY_CAGE_NUM + " INTEGER,"
                    + KEY_CAGE_ZONE + " TEXT" + ")";
            String CREATE_VOLUNTEERS_TABLE = "CREATE TABLE " + TABLE_VOLUNTEERS + "("
                    + KEY_VOLUNTEER_ID + " INTEGER PRIMARY KEY,"
                    + KEY_VOLUNTEER_NAME + " TEXT,"
                    + KEY_VOLUNTEER_PHONE + " TEXT,"
                    + KEY_VOLUNTEER_DAY + " TEXT,"
                    + KEY_VOLUNTEER_OBSERVATIONS + " TEXT" + ")";
            String CREATE_WALKS_TABLE = "CREATE TABLE " + TABLE_WALKS + "("
                    + KEY_WALKS_ID + " INTEGER PRIMARY KEY,"
                    + KEY_WALKS_ROW + " INTEGER,"
                    + KEY_WALKS_COLUMN + " INTEGER,"
                    + KEY_WALKS_DOG_ID + " INTEGER,"
                    + KEY_WALKS_VOLUNTEER_ID + " INTEGER" + ")";
            String CREATE_CLEAN_TABLE = "CREATE TABLE " + TABLE_CLEAN + "("
                    + KEY_CLEAN_ID + " INTEGER PRIMARY KEY,"
                    + KEY_CLEAN_ROW + " INTEGER,"
                    + KEY_CLEAN_COLUMN + " INTEGER,"
                    + KEY_CLEAN_DOG_ID + " INTEGER" + ")";
            String CREATE_SELECTED_VOLUNTEERS_TABLE = "CREATE TABLE " + TABLE_SELECTED_VOLUNTEERS + "("
                    + KEY_SELECTEDVOLUNTEERS_ID + " INTEGER PRIMARY KEY,"
                    + KEY_SELECTEDVOLUNTEERS_VOLUNTEER_ID + " INTEGER,"
                    + KEY_SELECTEDVOLUNTEERS_VOLUNTEER_NAME + " STRING,"
                    + KEY_SELECTEDVOLUNTEERS_CLEAN + " INTEGER,"
                    + KEY_SELECTEDVOLUNTEERS_WALK_1 + " INTEGER,"
                    + KEY_SELECTEDVOLUNTEERS_WALK_2 + " INTEGER,"
                    + KEY_SELECTEDVOLUNTEERS_WALK_3 + " INTEGER,"
                    + KEY_SELECTEDVOLUNTEERS_WALK_4 + " INTEGER,"
                    + KEY_SELECTEDVOLUNTEERS_WALK_5 + " INTEGER,"
                    + KEY_SELECTEDVOLUNTEERS_NWALKS + " INTEGER" + ")";
            String CREATE_DOG_FRIENDS_TABLE = "CREATE TABLE " + TABLE_DOG_FRIENDS + "("
                    + KEY_DOGFRIENDS_ID + " INTEGER PRIMARY KEY,"
                    + KEY_DOGFRIENDS_DOG_ID + " INTEGER,"
                    + KEY_DOGFRIENDS_FRIENDDOG_ID + " INTEGER" + ")";
            String CREATE_DOG_FAVOURITES_TABLE = "CREATE TABLE " + TABLE_DOG_FAVOURITES + "("
                    + KEY_DOGFAVOURITES_ID + " INTEGER PRIMARY KEY,"
                    + KEY_DOGFAVOURITES_VOLUNTEER_ID + " INTEGER,"
                    + KEY_DOGFAVOURITES_DOG_ID + " INTEGER" + ")";

            String CREATE_SELECTED_VOLUNTEERS_TABLE_TEST = "CREATE TABLE " + TABLE_SELECTED_VOLUNTEERS_TEST + "("
                    + KEY_SELECTEDVOLUNTEERS_ID + " INTEGER PRIMARY KEY,"
                    + KEY_SELECTEDVOLUNTEERS_VOLUNTEER_ID + " INTEGER,"
                    + KEY_SELECTEDVOLUNTEERS_VOLUNTEER_NAME + " STRING,"
                    + KEY_SELECTEDVOLUNTEERS_CLEAN + " INTEGER,"
                    + KEY_SELECTEDVOLUNTEERS_WALK_1 + " INTEGER,"
                    + KEY_SELECTEDVOLUNTEERS_WALK_2 + " INTEGER,"
                    + KEY_SELECTEDVOLUNTEERS_WALK_3 + " INTEGER,"
                    + KEY_SELECTEDVOLUNTEERS_WALK_4 + " INTEGER,"
                    + KEY_SELECTEDVOLUNTEERS_WALK_5 + " INTEGER,"
                    + KEY_SELECTEDVOLUNTEERS_NWALKS + " INTEGER" + ")";
            String CREATE_DOG_FAVOURITES_TABLE_TEST = "CREATE TABLE " + TABLE_DOG_FAVOURITES_TEST + "("
                    + KEY_DOGFAVOURITES_ID + " INTEGER PRIMARY KEY,"
                    + KEY_DOGFAVOURITES_VOLUNTEER_ID + " INTEGER,"
                    + KEY_DOGFAVOURITES_DOG_ID + " INTEGER" + ")";
            String CREATE_DOGS_TABLE_TEST = "CREATE TABLE " + TABLE_DOGS_TEST + "("
                    + KEY_DOG_ID + " INTEGER PRIMARY KEY,"
                    + KEY_DOG_NAME + " TEXT,"
                    + KEY_DOG_ID_CAGE + " INTEGER,"
                    + KEY_DOG_AGE + " INTEGER,"
                    + KEY_DOG_LINK + " TEXT,"
                    + KEY_DOG_SPECIAL + " BOOLEAN,"
                    + KEY_DOG_WALKTYPE + " TINYINY,"
                    + KEY_DOG_OBSERVATIONS + " TEXT" + ")";
            String CREATE_DOG_FRIENDS_TABLE_TEST = "CREATE TABLE " + TABLE_DOG_FRIENDS_TEST + "("
                    + KEY_DOGFRIENDS_ID + " INTEGER PRIMARY KEY,"
                    + KEY_DOGFRIENDS_DOG_ID + " INTEGER,"
                    + KEY_DOGFRIENDS_FRIENDDOG_ID + " INTEGER" + ")";

            db.execSQL(CREATE_DOGS_TABLE);
            db.execSQL(CREATE_CAGES_TABLE);
            db.execSQL(CREATE_VOLUNTEERS_TABLE);
            db.execSQL(CREATE_WALKS_TABLE);
            db.execSQL(CREATE_CLEAN_TABLE);
            db.execSQL(CREATE_SELECTED_VOLUNTEERS_TABLE);
            db.execSQL(CREATE_DOG_FRIENDS_TABLE);
            db.execSQL(CREATE_DOG_FAVOURITES_TABLE);

            db.execSQL(CREATE_SELECTED_VOLUNTEERS_TABLE_TEST);
            db.execSQL(CREATE_DOG_FAVOURITES_TABLE_TEST);
            db.execSQL(CREATE_DOGS_TABLE_TEST);
            db.execSQL(CREATE_DOG_FRIENDS_TABLE_TEST);

            this.insertCages(db);
            this.insertDogs(db);
            this.insertVolunteers(db);
            this.insertFriendDogs(db);
            this.insertDogFavourites(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //Drop older table if existed
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOGS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAGES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOLUNTEERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_WALKS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLEAN);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SELECTED_VOLUNTEERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOG_FRIENDS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOG_FAVOURITES);

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SELECTED_VOLUNTEERS_TEST);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOG_FAVOURITES_TEST);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOG_FRIENDS_TEST);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOGS_TEST);
            //Create tables again
            onCreate(db);
        }

        public void CleanSelectedVolunteers(SQLiteDatabase db)
        {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SELECTED_VOLUNTEERS);

            String CREATE_SELECTED_VOLUNTEERS_TABLE = "CREATE TABLE " + TABLE_SELECTED_VOLUNTEERS + "("
                    + KEY_SELECTEDVOLUNTEERS_ID + " INTEGER PRIMARY KEY,"
                    + KEY_SELECTEDVOLUNTEERS_VOLUNTEER_ID + " INTEGER,"
                    + KEY_SELECTEDVOLUNTEERS_VOLUNTEER_NAME + " STRING,"
                    + KEY_SELECTEDVOLUNTEERS_CLEAN + " BOOLEAN,"
                    + KEY_SELECTEDVOLUNTEERS_WALK_1 + " BOOLEAN,"
                    + KEY_SELECTEDVOLUNTEERS_WALK_2 + " BOOLEAN,"
                    + KEY_SELECTEDVOLUNTEERS_WALK_3 + " BOOLEAN,"
                    + KEY_SELECTEDVOLUNTEERS_WALK_4 + " BOOLEAN,"
                    + KEY_SELECTEDVOLUNTEERS_WALK_5 + " BOOLEAN,"
                    + KEY_SELECTEDVOLUNTEERS_NWALKS + " INTEGER" + ")";

            db.execSQL(CREATE_SELECTED_VOLUNTEERS_TABLE);
        }

        public void CleanDogs(SQLiteDatabase db)
        {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOGS);

            String CREATE_DOGS_TABLE = "CREATE TABLE " + TABLE_DOGS + "("
                    + KEY_DOG_ID + " INTEGER PRIMARY KEY,"
                    + KEY_DOG_NAME + " TEXT,"
                    + KEY_DOG_ID_CAGE + " INTEGER,"
                    + KEY_DOG_AGE + " INTEGER,"
                    + KEY_DOG_LINK + " TEXT,"
                    + KEY_DOG_SPECIAL + " BOOLEAN,"
                    + KEY_DOG_WALKTYPE + " TINYINY,"
                    + KEY_DOG_OBSERVATIONS + " TEXT" + ")";

            db.execSQL(CREATE_DOGS_TABLE);
        }

        public void EraseSolutionTables(SQLiteDatabase db)
        {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_WALKS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLEAN);

            String CREATE_WALKS_TABLE = "CREATE TABLE " + TABLE_WALKS + "("
                    + KEY_WALKS_ID + " INTEGER PRIMARY KEY,"
                    + KEY_WALKS_ROW + " INTEGER,"
                    + KEY_WALKS_COLUMN + " INTEGER,"
                    + KEY_WALKS_DOG_ID + " INTEGER,"
                    + KEY_WALKS_VOLUNTEER_ID + " INTEGER" + ")";
            String CREATE_CLEAN_TABLE = "CREATE TABLE " + TABLE_CLEAN + "("
                    + KEY_CLEAN_ID + " INTEGER PRIMARY KEY,"
                    + KEY_CLEAN_ROW + " INTEGER,"
                    + KEY_CLEAN_COLUMN + " INTEGER,"
                    + KEY_CLEAN_DOG_ID + " INTEGER" + ")";

            db.execSQL(CREATE_WALKS_TABLE);
            db.execSQL(CREATE_CLEAN_TABLE);
        }

        public void CleanTestTables(SQLiteDatabase db)
        {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SELECTED_VOLUNTEERS_TEST);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOG_FAVOURITES_TEST);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOG_FRIENDS_TEST);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOGS_TEST);

            String CREATE_SELECTED_VOLUNTEERS_TABLE_TEST = "CREATE TABLE " + TABLE_SELECTED_VOLUNTEERS_TEST + "("
                    + KEY_SELECTEDVOLUNTEERS_ID + " INTEGER PRIMARY KEY,"
                    + KEY_SELECTEDVOLUNTEERS_VOLUNTEER_ID + " INTEGER,"
                    + KEY_SELECTEDVOLUNTEERS_VOLUNTEER_NAME + " STRING,"
                    + KEY_SELECTEDVOLUNTEERS_CLEAN + " BOOLEAN,"
                    + KEY_SELECTEDVOLUNTEERS_WALK_1 + " BOOLEAN,"
                    + KEY_SELECTEDVOLUNTEERS_WALK_2 + " BOOLEAN,"
                    + KEY_SELECTEDVOLUNTEERS_WALK_3 + " BOOLEAN,"
                    + KEY_SELECTEDVOLUNTEERS_WALK_4 + " BOOLEAN,"
                    + KEY_SELECTEDVOLUNTEERS_WALK_5 + " BOOLEAN,"
                    + KEY_SELECTEDVOLUNTEERS_NWALKS + " INTEGER" + ")";

            String CREATE_DOG_FAVOURITES_TABLE_TEST = "CREATE TABLE " + TABLE_DOG_FAVOURITES_TEST + "("
                    + KEY_DOGFAVOURITES_ID + " INTEGER PRIMARY KEY,"
                    + KEY_DOGFAVOURITES_VOLUNTEER_ID + " INTEGER,"
                    + KEY_DOGFAVOURITES_DOG_ID + " INTEGER" + ")";

            String CREATE_DOGS_TABLE_TEST = "CREATE TABLE " + TABLE_DOGS_TEST + "("
                    + KEY_DOG_ID + " INTEGER PRIMARY KEY,"
                    + KEY_DOG_NAME + " TEXT,"
                    + KEY_DOG_ID_CAGE + " INTEGER,"
                    + KEY_DOG_AGE + " INTEGER,"
                    + KEY_DOG_LINK + " TEXT,"
                    + KEY_DOG_SPECIAL + " BOOLEAN,"
                    + KEY_DOG_WALKTYPE + " TINYINY,"
                    + KEY_DOG_OBSERVATIONS + " TEXT" + ")";

            String CREATE_DOG_FRIENDS_TABLE_TEST = "CREATE TABLE " + TABLE_DOG_FRIENDS_TEST + "("
                    + KEY_DOGFRIENDS_ID + " INTEGER PRIMARY KEY,"
                    + KEY_DOGFRIENDS_DOG_ID + " INTEGER,"
                    + KEY_DOGFRIENDS_FRIENDDOG_ID + " INTEGER" + ")";

            db.execSQL(CREATE_SELECTED_VOLUNTEERS_TABLE_TEST);
            db.execSQL(CREATE_DOG_FAVOURITES_TABLE_TEST);
            db.execSQL(CREATE_DOGS_TABLE_TEST);
            db.execSQL(CREATE_DOG_FRIENDS_TABLE_TEST);
        }
    }
}






