package zaid.catchthenyancat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.TextView;


public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "SCORES.DB", factory, version);
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

    public void insert_score(int count)
    {
        ContentValues cv = new ContentValues();
        cv.put("SCORE", count);
        this.getWritableDatabase().insertOrThrow("SCORE","", cv);
    }

    public void delete_score(int id)
    {
        this.getWritableDatabase().delete("SCORES","ID='"+id+"'", null);
    }

    public void update_score(int old_score, int new_score)
    {
        this.getWritableDatabase().execSQL("UPDATE SCORES SET SCORE = '"+new_score+"' WHERE SCORE = '"+old_score+"'");
        //https://www.youtube.com/watch?v=vW4LZ1TRsRM
    }

    public void top(TextView top_score)
    {
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT SCORE FROM SCORES ORDER BY DESC LIMIT 1", null);
        top_score.setText("");
        while (cursor.moveToNext()){
            top_score.append(cursor.getString(1));
        }
    }
}
