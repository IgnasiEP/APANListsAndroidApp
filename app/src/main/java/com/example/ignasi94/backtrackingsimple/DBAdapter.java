package com.example.ignasi94.backtrackingsimple;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBAdapter{

    DBHandler dbHandler ;
    public DBAdapter(Context context)
    {
        dbHandler = new DBHandler(context);
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
            Dog dog = new Dog(name,cage,age,link,special,walkType,observations);
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

    public List<Volunteer> getAllVolunteers()
    {
        List<Volunteer> volunteers = new ArrayList<Volunteer>();
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        String[] columns = {dbHandler.KEY_VOLUNTEER_NAME, dbHandler.KEY_VOLUNTEER_PHONE, dbHandler.KEY_VOLUNTEER_DAY, dbHandler.KEY_VOLUNTEER_OBSERVATIONS};
        Cursor cursor =db.query(dbHandler.TABLE_VOLUNTEERS,columns,null, null,null,null,null);
        StringBuffer buffer= new StringBuffer();
        while (cursor.moveToNext())
        {
            String name = cursor.getString(cursor.getColumnIndex(dbHandler.KEY_VOLUNTEER_NAME));
            String phone = cursor.getString(cursor.getColumnIndex(dbHandler.KEY_VOLUNTEER_PHONE));
            String volunteerDay = cursor.getString(cursor.getColumnIndex(dbHandler.KEY_VOLUNTEER_DAY));
            String observations = cursor.getString(cursor.getColumnIndex(dbHandler.KEY_VOLUNTEER_OBSERVATIONS));

            Volunteer volunteer = new Volunteer(name,phone,volunteerDay,observations);
            volunteers.add(volunteer);
        }
        return volunteers;
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
            db.execSQL(insertDogs + "1,'Gachas',1,2);");
            db.execSQL(insertDogs + "2,'Vida',1,2);");
            db.execSQL(insertDogs + "3,'Trixie',2,2);");
            db.execSQL(insertDogs + "4,'Atenea',3,1);");
            db.execSQL(insertDogs + "5,'Kratos',4,2);");
            db.execSQL(insertDogs + "6,'Quim',4,1);");
            db.execSQL(insertDogs + "7,'Tones',5,2);");
            db.execSQL(insertDogs + "8,'Straciatella',5,2);");
            db.execSQL(insertDogs + "9,'Nika',6,2);");
            db.execSQL(insertDogs + "10,'Luc',7,2);");
            db.execSQL(insertDogs + "11,'Geralt',8,1);");
            db.execSQL(insertDogs + "12,'Chelsea',9,2);");
            db.execSQL(insertDogs + "13,'Blacky',10,1);");
            db.execSQL(insertDogs + "14,'Max',10,2);");
            db.execSQL(insertDogs + "15,'Canela',10,2);");
            db.execSQL(insertDogs + "16,'Nelson',11,2);");
            db.execSQL(insertDogs + "17,'Neit',11,2);");
            db.execSQL(insertDogs + "18,'GosPati1',12,2);");
            db.execSQL(insertDogs + "19,'GosPati2',13,2);");
            db.execSQL(insertDogs + "20,'GosPati3',14,2);");
            db.execSQL(insertDogs + "21,'GosQuarentena1',15,2);");
            db.execSQL(insertDogs + "22,'GosQuarentena2',16,2);");
            db.execSQL(insertDogs + "23,'GosQuarentena3',17,2);");
        }

        private void insertVolunteers(SQLiteDatabase db)
        {
            String insertVolunteers = "INSERT INTO " + TABLE_VOLUNTEERS + "(" + KEY_VOLUNTEER_ID + "," + KEY_VOLUNTEER_NAME + "," + KEY_VOLUNTEER_DAY +") VALUES(";
            db.execSQL(insertVolunteers + 1 + ",'Voluntario1','S');");
            db.execSQL(insertVolunteers + 2 + ",'Voluntario2','S');");
            db.execSQL(insertVolunteers + 3 + ",'Voluntario3','S');");
            db.execSQL(insertVolunteers + 4 + ",'Voluntario4','S');");
            db.execSQL(insertVolunteers + 5 + ",'Voluntario5','S');");
            db.execSQL(insertVolunteers + 6 + ",'Voluntario6','S');");
            db.execSQL(insertVolunteers + 7 + ",'Voluntario7','S');");
            db.execSQL(insertVolunteers + 8 + ",'Voluntario8','S');");
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





