package zaid.catchthenyancat;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Z on 14-Oct-17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL("CREATE TABLE SCORES( ID INTEGER PRIMARY KEY AUTOINCREMENT, SCORE INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS SCORES;");
        onCreate(sqLiteDatabase);
    }

    public void insert_score(int score)
    {
        ContentValues cv = new ContentValues();
        cv.put("SCORE", score);
        this.getWritableDatabase().insertOrThrow("SCORE","", cv);
    }

    public void delete_score(int id)
    {
        this.getWritableDatabase().delete("SCORES","ID='"+id+"'", null);
    }

    public void update_score()
    {
        //https://www.youtube.com/watch?v=vW4LZ1TRsRM
    }
}
