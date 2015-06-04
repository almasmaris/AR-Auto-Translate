package edu.sfsu.cs.orange.ocr.language;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;


public class DatabaseHelper extends SQLiteAssetHelper {
	
	private static final String DB_NAME = "db_dictionary";
	private static final int DB_VER = 2;
	
	private static final String TB_DATA = "tb_english";
	private static final String COL_ID = "_id";
	private static final String COL_WORD = "word";
	private static final String COl_TRANSLATION = "translation";
	
	private static DatabaseHelper dbInstance;
	private static SQLiteDatabase db;
	
	private DatabaseHelper(Context context){
		super(context, DB_NAME, null, DB_VER);
	}
	
	public static DatabaseHelper getInstance(Context context){
		if(dbInstance == null){
			dbInstance = new DatabaseHelper(context);
			db = dbInstance.getWritableDatabase();
			
		}
		return dbInstance;
	}
	
	public synchronized void close(){
		super.close();
		if(dbInstance != null){
			dbInstance.close();
		}
	}
	
	public String findData(String english){
		Cursor cursor = db.query(TB_DATA, new String[] {COL_WORD}, COL_WORD + " LIKE '"+english.toLowerCase()+"'", null, null, null, null);
		
		if(cursor.getCount() >= 1){
			cursor.moveToLast();
			return cursor.getString(0);
		}
		
		return null;
	}
	
	public String findTranslation(String english){
		Cursor cursor = db.query(TB_DATA, new String[] {COl_TRANSLATION}, COL_WORD + " LIKE '"+english.toLowerCase()+"'", null, null, null, null);
		
		if(cursor.getCount() >= 1){
			int count = cursor.getCount();
			cursor.moveToLast();
			return cursor.getString(0);
		}
		
		return null;
	}
	
}
