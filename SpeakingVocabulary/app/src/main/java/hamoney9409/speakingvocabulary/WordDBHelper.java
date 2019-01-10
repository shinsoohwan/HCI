package hamoney9409.speakingvocabulary;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import hamoney9409.speakingvocabulary.dataformat.DEACTIVE_WORD_INFO;
import hamoney9409.speakingvocabulary.dataformat.WORD_DATA;

/**
 * Created by 상범 on 2017-05-21.
 */

public class WordDBHelper extends SQLiteOpenHelper
{
    private static final int DBVERSION = 4;
    private static final String DBNAME = "wordDB.db";

    private static WordDBHelper singleton = null;
    public static WordDBHelper getInstance(Context context)
    {
        if (singleton != null)
            return singleton;

        //  아직 context 인자 저장은 없지만, 저장이 생길때를 대비해 본인도 싱글턴이라 누수걱정이 없는 ApplicationContext를 인자로 넘겨준다.
        return singleton = new WordDBHelper(context.getApplicationContext(), DBNAME, null, DBVERSION);
    }

    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    private WordDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // SQLite는 이걸 적어줘야 외래키가 작동한다.
        db.execSQL("PRAGMA foreign_keys = ON;");

        // 영단어 테이블 생성
        /*
            CREATE TABLE words
            (
                guid   INTEGER PRIMARY KEY AUTOINCREMENT,
                englishWord    TEXT,
                mean  TEXT
            );
        */
        db.execSQL
        (
            "CREATE TABLE words " +
            "(" +
                "guid   INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "englishWord    TEXT, " +
                "mean  TEXT" +
            ");"
        );

        // 활성화된 영단어 목록 테이블 생성

        /*
            CREATE TABLE word_actives
            (
                wordGuid   INTEGER,
                startDate     TEXT,     -- 형식은 ISO8601
                updateDate     TEXT,    -- 형식은 ISO8601
                remainCount     INTEGER,
                FOREIGN KEY     (wordGuid) REFERENCES words (guid) ON DELETE CASCADE
            );
        */
        db.execSQL
        (
            "CREATE TABLE word_actives" +
            "(" +
                "wordGuid   INTEGER REFERENCES words (guid), " +
                "startDate     TEXT, " + // 형식은 ISO8601
                "updateDate     TEXT, " + // 형식은 ISO8601
                "remainCount     INTEGER, " +
                "FOREIGN KEY     (wordGuid) REFERENCES words (guid) ON DELETE CASCADE " +
            ");"
        );

        // 비활성화된 영단어 목록 테이블 생성
        /*
            CREATE TABLE word_deactives
            (
                wordGuid   INTEGER REFERENCES words (guid),
                level     INTEGER,
                score     INTEGER,
                updateDate     TEXT,
                FOREIGN KEY     (wordGuid) REFERENCES words (guid) ON DELETE CASCADE
            );
         */
        db.execSQL
        (
            "CREATE TABLE word_deactives" +
            "(" +
                "wordGuid   INTEGER REFERENCES words (guid), " +
                "level     INTEGER, " + // 단계 (1~4단계 이후 파기됨)
                "score     INTEGER, " +
                "updateDate     TEXT, " + // 형식은 ISO8601
                "FOREIGN KEY     (wordGuid) REFERENCES words (guid) ON DELETE CASCADE " +
            ");"
        );

        // 최종 업데이트일
        /*
            CREATE TABLE last_updated_date
            (
                lastUpdatedDate    TEXT     -- 형식은 ISO8601
            );
         */
        db.execSQL
        (
            "CREATE TABLE last_updated_date" +
            "(" +
                "lastUpdatedDate    TEXT" + // 형식은 ISO8601
            ");"
        );

        /*
            INSERT INTO last_updated_date ( lastUpdatedDate ) VALUES ( %현재날짜% )
         */
        ContentValues values = new ContentValues();
        values.put("lastUpdatedDate", new SimpleDateFormat().format(new Date())); //
        db.insert("last_updated_date", null, values);
    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if (oldVersion == 2)
        {
            db.execSQL
            (
                "ALTER TABLE word_deactives ADD COLUMN updateDate TEXT DEFAULT '" + new SimpleDateFormat().format(new Date()) + "' "
            );
        }
        if (oldVersion == 3)
        {
            db.execSQL
            (
                "UPDATE word_actives SET remainCount = 0 "
            );
        }
    }

    public String[] getInsertedMeans(String englishWord)
    {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<String> meanData = new ArrayList();
        /*
            SELECT mean
            FROM words
            WHERE englishWord = %등록할 영단어%
         */
        Cursor cursor = db.rawQuery
        (
            "SELECT mean "
            + "FROM words "
            + "WHERE englishWord = '" + englishWord + "'",
            null
        );

        while(cursor.moveToNext())
        {
            meanData.add
            (
                cursor.getString(cursor.getColumnIndex("mean")) // mean이 아닌 그냥 최초 컬럼을 선택하게 하면 최적화 가능
            );
        }

        cursor.close();

        return meanData.toArray(new String[meanData.size()]);
    }

    public void insert(String englishWord, String mean) throws SQLException
    {
        // 영단어 테이블 생성
        SQLiteDatabase db = getWritableDatabase();

        try
        {
            db.beginTransaction();
            ContentValues values = new ContentValues();

            // WORDS 테이블에 추가

            /*
                INSERT INTO words (englishWord, mean) VALUES (%영단어%, %뜻%)
             */
            values.put("englishWord", englishWord);
            values.put("mean", mean);
            long guid = db.insert("words", null, values);
            values.clear();

            // WORD_ACTIVES 테이블에 추가
            /*
                INSERT INTO word_actives (wordGuid, startDate, updateDate, remainCount) VALUES (%단어 guid%, %현재날짜%, %현재날짜%, 14)
             */
            String currentDate = new SimpleDateFormat().format(new Date());
            values.put("wordGuid", guid);
            values.put("startDate", currentDate);
            values.put("updateDate", currentDate);
            values.put("remainCount", 0);
            db.insert("word_actives", null, values);
            values.clear();

            db.setTransactionSuccessful();
        }
        finally
        {
            db.endTransaction();
        }
    }

    public void setDeactive(String englishWord, String mean) throws SQLException
    {
        // 영단어 테이블 생성
        SQLiteDatabase db = getWritableDatabase();

        try
        {
            db.beginTransaction();
            ContentValues values = new ContentValues();

            // WORD_ACTIVES 테이블에서 제거
            db.delete
            (
                "word_actives",
                "wordGuid IN "
                + "( "
                    + "SELECT guid "
                    + "FROM words "
                    + "WHERE englishWord=? AND mean=? "
                + ") ",
                new String[]{englishWord, mean}
            );

            int guid = getGuid(db, englishWord, mean);

            // WORD_DEACTIVES 테이블에 추가
            String currentDate = new SimpleDateFormat().format(new Date());
            values.put("wordGuid", guid);
            values.put("level", 1);
            values.put("score", 0);
            values.put("updateDate", currentDate);
            db.insert("word_deactives", null, values);
            values.clear();

            db.setTransactionSuccessful();
        }
        finally
        {
            db.endTransaction();
        }
    }

    public void setActive(String englishWord, String mean) throws SQLException
    {
        // 영단어 테이블 생성
        SQLiteDatabase db = getWritableDatabase();

        try
        {
            db.beginTransaction();
            ContentValues values = new ContentValues();

            // WORD_DEACTIVES 테이블에서 제거
            db.delete
            (
                "word_deactives",
                "wordGuid IN "
                + "( "
                    + "SELECT guid "
                    + "FROM words "
                    + "WHERE englishWord=? AND mean=? "
                + ") ",
                new String[]{englishWord, mean}
            );

            int guid = getGuid(db, englishWord, mean);

            // WORD_ACTIVES 테이블에 추가
            String currentDate = new SimpleDateFormat().format(new Date());
            values.put("wordGuid", guid);
            values.put("startDate", currentDate);
            values.put("updateDate", currentDate);
            values.put("remainCount", 0);
            db.insert("word_actives", null, values);
            values.clear();

            db.setTransactionSuccessful();
        }
        finally
        {
            db.endTransaction();
        }
    }

    private int getGuid(SQLiteDatabase db, String englishWord, String mean)
    {
        Cursor cursor = db.rawQuery
        (
            "SELECT guid "
            + "FROM words "
            + "WHERE englishWord = '" + englishWord + "' AND mean = '" + mean + "'",
            null
        );

        while(cursor.moveToNext())
        {
            return cursor.getInt(cursor.getColumnIndex("guid")); // guid가 아닌 그냥 최초 컬럼을 선택하게 하면 최적화 가능
        }

        return -1;
    }

    public int remove(String englishWord, String mean)
    {
        /*
            DELETE FROM words
            WHERE englishWord = %영단어% AND mean = %뜻%
         */

        SQLiteDatabase db = getWritableDatabase();
        return db.delete("words", "englishWord=? AND mean=?", new String[]{englishWord, mean});
    }

    private ArrayList<WORD_DATA> getWordList(String query)
    {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<WORD_DATA> wordData = new ArrayList();

        Cursor cursor = db.rawQuery(query, null);

        while(cursor.moveToNext())
        {
            wordData.add
            (
                new WORD_DATA
                (
                    cursor.getString(cursor.getColumnIndex("englishWord")),
                    cursor.getString(cursor.getColumnIndex("mean"))
                )
            );
        }

        cursor.close();

        return wordData;
    }

    private ArrayList<DEACTIVE_WORD_INFO> getDeactiveWordList(String query)
    {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<DEACTIVE_WORD_INFO> wordData = new ArrayList();

        Cursor cursor = db.rawQuery(query, null);

        while(cursor.moveToNext())
        {
            Date date;
            try
            {
                 date = new SimpleDateFormat().parse(cursor.getString(cursor.getColumnIndex("updateDate")));
            }
            catch(ParseException e)
            {
                Log.e("ParseError", e.toString());
                date = new Date();
            }

            wordData.add
            (
                new DEACTIVE_WORD_INFO
                (
                    new WORD_DATA
                    (
                        cursor.getString(cursor.getColumnIndex("englishWord")),
                        cursor.getString(cursor.getColumnIndex("mean"))
                    ),
                    date,
                    cursor.getInt(cursor.getColumnIndex("score"))
                )
            );
        }

        cursor.close();

        return wordData;
    }

    public ArrayList<DEACTIVE_WORD_INFO> getDetailActiveWordList()
    {
        return getDetailActiveWordList(
            "SELECT w.englishWord, w.mean, wd.remainCount, wd.updateDate \n"
            + "FROM words w, word_actives wd \n"
            + "WHERE w.guid = wd.wordGuid "
        );
    }

    private ArrayList<DEACTIVE_WORD_INFO> getDetailActiveWordList(String query)
    {

        SQLiteDatabase db = getReadableDatabase();
        ArrayList<DEACTIVE_WORD_INFO> wordData = new ArrayList();

        Cursor cursor = db.rawQuery(query, null);

        while(cursor.moveToNext())
        {
            Date date;
            try
            {
                date = new SimpleDateFormat().parse(cursor.getString(cursor.getColumnIndex("updateDate")));
            }
            catch(ParseException e)
            {
                Log.e("ParseError", e.toString());
                date = new Date();
            }

            wordData.add
            (
                new DEACTIVE_WORD_INFO
                (
                    new WORD_DATA
                    (
                        cursor.getString(cursor.getColumnIndex("englishWord")),
                        cursor.getString(cursor.getColumnIndex("mean"))
                    ),
                    date,
                    cursor.getInt(cursor.getColumnIndex("remainCount"))
                )
            );
        }

        cursor.close();

        return wordData;
    }
    public void increaseDeactveWordScore(String englishWord, String mean, int score)
    {
        SQLiteDatabase db = getWritableDatabase();

        /*
        UPDATE word_deactives
        SET score = score + 1
        WHERE wordGuid IN
        (
            SELECT guid
            FROM words
            WHERE englishWord='grape' AND mean='포도'
        )
         */
        db.execSQL(
            "UPDATE word_deactives\n"
            + "SET score = score + " + score + "\n"
            + "WHERE wordGuid IN\n"
            + "(\n"
            + "\tSELECT guid \n"
            + "\tFROM words \n"
            + "\tWHERE englishWord='"+ englishWord + "' AND mean='" + mean + "' \n"
            + ")"
        );
    }

    public void increaseActveWordScore(String englishWord, String mean, int score)
    {
        SQLiteDatabase db = getWritableDatabase();

        /*
        UPDATE word_actives
        SET remainCount = remainCount + 1
        WHERE wordGuid IN
        (
            SELECT guid
            FROM words
            WHERE englishWord='apple' AND mean='사과'
        )
         */
        db.execSQL(
            "UPDATE word_actives\n"
            + "SET remainCount = remainCount + " + score + "\n"
            + "WHERE wordGuid IN\n"
            + "(\n"
            + "\tSELECT guid \n"
            + "\tFROM words \n"
            + "\tWHERE englishWord='"+ englishWord + "' AND mean='" + mean + "' \n"
            + ")"
        );
    }

    public ArrayList<WORD_DATA> getAllWordList()
    {
        return getWordList
        (
            "SELECT englishWord, mean " +
            "FROM words"
        );
    }

    public ArrayList<WORD_DATA> getActiveWordList()
    {
        return getWordList
        (
            "SELECT w.englishWord, w.mean " +
            "FROM words w, word_actives wa " +
            "WHERE w.guid = wa.wordGuid "
        );
    }

    // 모든 레벨의 deactive word를 가져옴
    @Deprecated
    public ArrayList<WORD_DATA> getDeactiveWordList()
    {
        return getWordList
        (
            "SELECT w.englishWord, w.mean " +
            "FROM words w, word_deactives wa " +
            "WHERE w.guid = wa.wordGuid "
        );
    }

    // 일정 레벨의 deactive word를 가져옴
    public ArrayList<WORD_DATA> getDeactiveWordListByLevel(int level)
    {
        return getWordList
        (
            "SELECT w.englishWord, w.mean " +
            "FROM words w, word_deactives wd " +
            "WHERE w.guid = wd.wordGuid AND wd.level = " + level + " "
        );
    }

    /*
        SELECT w.englishWord, w.mean
        FROM words w
        WHERE w.englishWord IN
        (
            SELECT w.englishWord
            FROM words w, word_deactives wd
            WHERE w.guid = wd.wordGuid
            GROUP BY w.englishWord
            ORDER BY random()
            LIMIT %개수%
        )
        ORDER BY w.englishWord
    */
    public ArrayList<DEACTIVE_WORD_INFO> getDeactiveWordListByEnglishWordSampling(int number)
    {
        return getDeactiveWordList
        (
            "SELECT w.englishWord, w.mean, wd.score, wd.updateDate "
            + "FROM words w, word_deactives wd "
            + "WHERE w.englishWord IN "
            + "( "
                + "SELECT w.englishWord "
                + "FROM words w, word_deactives wd "
                + "WHERE w.guid = wd.wordGuid "
                + "GROUP BY w.englishWord "
                + "ORDER BY random() "
                + "LIMIT " + number + " "
            + ") AND w.guid = wd.wordGuid "
            + "ORDER BY w.englishWord "
        );
    }

    public String executeRawQuery(String query)
    {
        SQLiteDatabase db = getWritableDatabase();
        StringBuffer output = new StringBuffer(query.length());

        ArrayList<String> meanData = new ArrayList();

        // 쿼리 실행!
        try
        {
            Cursor cursor = db.rawQuery(query, null);

            while(cursor.moveToNext())
            {
                for(int i=0; true; )
                {
                    switch(cursor.getType(i))
                    {
                        case Cursor.FIELD_TYPE_NULL:
                            output.append(cursor.getColumnName(i) + ":null");
                            break;
                        case Cursor.FIELD_TYPE_STRING:
                            output.append(cursor.getColumnName(i) + ":(string)" + cursor.getString(i));
                            break;
                        case Cursor.FIELD_TYPE_INTEGER:
                            output.append(cursor.getColumnName(i) + ":(integer)" + cursor.getInt(i));
                            break;
                        case Cursor.FIELD_TYPE_FLOAT:
                            output.append(cursor.getColumnName(i) + ":(float)" + cursor.getFloat(i));
                            break;
                        case Cursor.FIELD_TYPE_BLOB:
                            output.append(cursor.getColumnName(i) + ":(blob)" + cursor.getBlob(i));
                            break;
                    }

                    i++;
                    if (i<cursor.getColumnCount())
                    {
                        output.append(", ");
                    }
                    else
                    {
                        break;
                    }
                }
                output.append(System.lineSeparator());
            } // while(cursor.moveToNext())

            cursor.close();
        }
        catch(SQLiteException e)
        {
            try
            {
                // 여기 탈일은 없을거라고 봄
                db.execSQL(query);
                output.append("query executed but cannot output. error:");
                output.append(e.toString());
                output.append(System.lineSeparator());
            }
            catch(SQLException e2)
            {
                output.append(e2.toString());
                output.append(System.lineSeparator());

            } // catch(SQLException e2)

        } // catch(SQLiteException e)

        return output.toString();
    }
}
