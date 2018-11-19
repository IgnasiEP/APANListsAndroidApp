package com.example.ignasi94.backtrackingsimple.BBDD;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ignasi94.backtrackingsimple.Estructuras.Cage;
import com.example.ignasi94.backtrackingsimple.Estructuras.Dog;
import com.example.ignasi94.backtrackingsimple.Estructuras.Volunteer;
import com.example.ignasi94.backtrackingsimple.Utils.Constants;

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
        String[] columns = {dbHandler.KEY_VOLUNTEER_NAME, dbHandler.KEY_VOLUNTEER_PHONE, dbHandler.KEY_VOLUNTEER_DAY, dbHandler.KEY_VOLUNTEER_OBSERVATIONS};
        Cursor cursor = db.query(dbHandler.TABLE_VOLUNTEERS, columns, null, null, null, null, null);
        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(dbHandler.KEY_VOLUNTEER_NAME));
            String phone = cursor.getString(cursor.getColumnIndex(dbHandler.KEY_VOLUNTEER_PHONE));
            String volunteerDay = cursor.getString(cursor.getColumnIndex(dbHandler.KEY_VOLUNTEER_DAY));
            String observations = cursor.getString(cursor.getColumnIndex(dbHandler.KEY_VOLUNTEER_OBSERVATIONS));

            Volunteer volunteer = new Volunteer(name, phone, volunteerDay, observations);
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
    }

    public void onUpgrade()
    {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        dbHandler.onUpgrade(db,2,2);
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
            db.execSQL(insertVolunteers + 6 + ",'Lídia','S');");
            db.execSQL(insertVolunteers + 7 + ",'Alba1','S');");
            db.execSQL(insertVolunteers + 8 + ",'Alba2','S');");
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

            db.execSQL(CREATE_DOGS_TABLE);
            db.execSQL(CREATE_CAGES_TABLE);
            db.execSQL(CREATE_VOLUNTEERS_TABLE);
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

            //Create tables again
            onCreate(db);
        }
    }
}





