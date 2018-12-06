package com.example.ignasi94.backtrackingsimple.BBDD;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.ignasi94.backtrackingsimple.Estructuras.Cage;
import com.example.ignasi94.backtrackingsimple.Estructuras.Dog;
import com.example.ignasi94.backtrackingsimple.Estructuras.Volunteer;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerDog;
import com.example.ignasi94.backtrackingsimple.Estructuras.VolunteerWalks;
import com.example.ignasi94.backtrackingsimple.Utils.Constants;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

public class DBAdapter{

    DBHandler dbHandler ;
    public DBAdapter(Context context)
    {
        dbHandler = new DBHandler(context);
    }

    public Dictionary<Integer,Dog> getAllDogsDictionary()
    {
        Dictionary<Integer,Dog> dogsDictionary = new Hashtable<Integer, Dog>();
        List<Dog> dogs = getAllDogs();
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
            volunteersDictionary.put(volunteer.id,volunteer);
        }
        return volunteersDictionary;
    }

    public List<Dog> getAllDogs()
    {
        List<Dog> dogs = new ArrayList<Dog>();
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        String[] columns = {dbHandler.KEY_DOG_ID,dbHandler.KEY_DOG_NAME, dbHandler.KEY_DOG_ID_CAGE, dbHandler.KEY_DOG_AGE, dbHandler.KEY_DOG_LINK, dbHandler.KEY_DOG_SPECIAL, dbHandler.KEY_DOG_WALKTYPE, dbHandler.KEY_DOG_OBSERVATIONS};
        Cursor cursor =db.query(dbHandler.TABLE_DOGS,columns,null, null,null,null,null);
        StringBuffer buffer= new StringBuffer();
        while (cursor.moveToNext())
        {
            int id = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_DOG_ID));
            String name = cursor.getString(cursor.getColumnIndex(dbHandler.KEY_DOG_NAME));
            int cage = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_DOG_ID_CAGE));
            int age = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_DOG_AGE));
            String link = cursor.getString(cursor.getColumnIndex(dbHandler.KEY_DOG_LINK));
            String specialString = cursor.getString(cursor.getColumnIndex(dbHandler.KEY_DOG_SPECIAL));
            String walkTypeString = cursor.getString(cursor.getColumnIndex(dbHandler.KEY_DOG_WALKTYPE));
            String observations = cursor.getString(cursor.getColumnIndex(dbHandler.KEY_DOG_OBSERVATIONS));
            Boolean special;
            Short walkType;
            if(specialString == "true")
            {
                special = true;
            }
            else
            {
                special = false;
            }
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
        return dogs;
    }

    public List<Cage> getAllCages()
    {
        List<Cage> cages = new ArrayList<Cage>();
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        String[] columns = {dbHandler.KEY_CAGE_ID, dbHandler.KEY_CAGE_NUM, dbHandler.KEY_CAGE_ZONE};
        Cursor cursor =db.query(dbHandler.TABLE_CAGES,columns,null, null,null,null,null);
        StringBuffer buffer= new StringBuffer();
        while (cursor.moveToNext())
        {
            int id = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_CAGE_ID));
            int num = cursor.getInt(cursor.getColumnIndex(dbHandler.KEY_CAGE_NUM));
            String zone = cursor.getString(cursor.getColumnIndex(dbHandler.KEY_CAGE_ZONE));

            Cage cage = new Cage(id,num,zone);
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
                    contentValues.put(DBHandler.KEY_WALKS_ID, id);
                    contentValues.put(DBHandler.KEY_WALKS_ROW, i);
                    contentValues.put(DBHandler.KEY_WALKS_COLUMN, j);
                    contentValues.put(DBHandler.KEY_WALKS_DOG_ID, walksT[i][j-1].id);
                }
                db.insert(DBHandler.TABLE_WALKS, null,contentValues);
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
    }

    public void onUpgrade()
    {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        dbHandler.onUpgrade(db,2,2);
    }

    public void CleanSolutionsTables()
    {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        dbHandler.onUpgrade(db,2,2, true);
    }

    public void CleanSelectedVolunteers()
    {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        dbHandler.CleanSelectedVolunteers(db);
    }

    static class DBHandler extends SQLiteOpenHelper {
        //All static variables
        //Database Version
        private static final int DATABASE_VERSION = 2;
        //Database Name
        private static final String DATABASE_NAME = "apan";

        //Dogs table name
        private static final String TABLE_DOGS = "dogs";
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
            db.execSQL(insertCages + 12 + "," + 1 + "," + "'PATIOS');");
            db.execSQL(insertCages + 13 + "," + 2 + "," + "'PATIOS');");
            db.execSQL(insertCages + 14 + "," + 3 + "," + "'PATIOS');");
            db.execSQL(insertCages + 15 + "," + 1 + "," + "'CUARENTENAS');");
            db.execSQL(insertCages + 16 + "," + 2 + "," + "'CUARENTENAS');");
            db.execSQL(insertCages + 17 + "," + 3 + "," + "'CUARENTENAS');");
        }

        private void insertDogs(SQLiteDatabase db)
        {
            String insertDogs = "INSERT INTO " + TABLE_DOGS + "(" + KEY_DOG_ID + "," + KEY_DOG_NAME + "," + KEY_DOG_ID_CAGE + "," + KEY_DOG_WALKTYPE +") VALUES(";
            //0 No sale
            //1 Interior
            //2 Exterior
            db.execSQL(insertDogs + "1,'Puyol',1,2);");
            db.execSQL(insertDogs + "2,'Vida',2,2);");
            db.execSQL(insertDogs + "3,'Trixie',3,2);");
            db.execSQL(insertDogs + "4,'Thor',4,2);");
            db.execSQL(insertDogs + "5,'Looney',5,2);");
            db.execSQL(insertDogs + "6,'Atenea',6,1);");
            db.execSQL(insertDogs + "7,'Quim',7,2);");
            db.execSQL(insertDogs + "8,'Kratos',7,2);");
            db.execSQL(insertDogs + "9,'Rista',8,2);");
            db.execSQL(insertDogs + "10,'Milady',9,2);");
            db.execSQL(insertDogs + "11,'Maxi',10,2);");
            db.execSQL(insertDogs + "12,'Nika',11,2);");
            db.execSQL(insertDogs + "13,'Mara',12,2);");
            db.execSQL(insertDogs + "14,'Geralt',13,1);");
            db.execSQL(insertDogs + "15,'Ralts',14,2);");
            db.execSQL(insertDogs + "16,'Luc',15,2);");
            db.execSQL(insertDogs + "17,'Pontos',16,2);");
            db.execSQL(insertDogs + "18,'Chelin',17,2);");
            db.execSQL(insertDogs + "19,'Dogos',18,2);");
            db.execSQL(insertDogs + "20,'Chelsea',19,2);");
            db.execSQL(insertDogs + "21,'Argus',20,1);");
            db.execSQL(insertDogs + "22,'Jess',20,1);");
            db.execSQL(insertDogs + "23,'Max',21,2);");
            db.execSQL(insertDogs + "24,'Canela',21,2);");
            db.execSQL(insertDogs + "25,'Blacky',21,1);");
            db.execSQL(insertDogs + "26,'Titus',22,2);");
            db.execSQL(insertDogs + "27,'Amiguets',22,2);");
            db.execSQL(insertDogs + "28,'Miam',23,0);");
            db.execSQL(insertDogs + "29,'Dardo',23,0);");

            db.execSQL(insertDogs + "30,'Vito',24,2);");
            db.execSQL(insertDogs + "31,'Corleone',24,2);");
            db.execSQL(insertDogs + "32,'Ter',24,0);");
            db.execSQL(insertDogs + "33,'Perla',24,2);");

            db.execSQL(insertDogs + "34,'Canelo',26,2);");
            db.execSQL(insertDogs + "35,'Saga',26,2);");
            db.execSQL(insertDogs + "36,'Tunes',26,2);");
            db.execSQL(insertDogs + "37,'Sira',26,0);");

            db.execSQL(insertDogs + "38,'Pésol',29,2);");
            db.execSQL(insertDogs + "39,'Cristal',29,2);");
            db.execSQL(insertDogs + "40,'Bull',30,2);");
            db.execSQL(insertDogs + "41,'Maya',30,2);");
            db.execSQL(insertDogs + "42,'Stracciatela',31,2);");

            db.execSQL(insertDogs + "43,'Mar',32,2);");
            db.execSQL(insertDogs + "44,'Roc',32,2);");
        }

        private void insertVolunteers(SQLiteDatabase db)
        {
            String insertVolunteers = "INSERT INTO " + TABLE_VOLUNTEERS + "(" + KEY_VOLUNTEER_ID + "," + KEY_VOLUNTEER_NAME + "," + KEY_VOLUNTEER_DAY +") VALUES(";
            db.execSQL(insertVolunteers + 1 + ",'Ignasi','S');");
            db.execSQL(insertVolunteers + 2 + ",'Esther','S');");
            db.execSQL(insertVolunteers + 3 + ",'Sònia','S');");
            db.execSQL(insertVolunteers + 4 + ",'Àlex','S');");
            db.execSQL(insertVolunteers + 5 + ",'Guillem','S');");
            db.execSQL(insertVolunteers + 6 + ",'Lídia','D');");
            db.execSQL(insertVolunteers + 7 + ",'Alba1','S');");
            db.execSQL(insertVolunteers + 8 + ",'Alba2','D');");
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
                    + KEY_SELECTEDVOLUNTEERS_WALK_5 + " BOOLEAN,"
                    + KEY_SELECTEDVOLUNTEERS_NWALKS + " INTEGER" + ")";

            db.execSQL(CREATE_DOGS_TABLE);
            db.execSQL(CREATE_CAGES_TABLE);
            db.execSQL(CREATE_VOLUNTEERS_TABLE);
            db.execSQL(CREATE_WALKS_TABLE);
            db.execSQL(CREATE_CLEAN_TABLE);
            db.execSQL(CREATE_SELECTED_VOLUNTEERS_TABLE);
            this.insertCages(db);
            this.insertDogs(db);
            this.insertVolunteers(db);
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

            //Create tables again
            onCreate(db);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion, boolean saveSolutions) {
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
    }
}






