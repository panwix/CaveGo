package com.panwix.cavego;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ScoreServer {
	private OpenHelper openHelper;
	public ScoreServer(Context context){
		this.openHelper=new OpenHelper(context);
	}
	
	public void save(int score){
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.execSQL("insert into highscore(score) value("+score+")");
		db.close();
	}
	
	public int find(Integer id){
		int sid = 0;
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from highscore where id=?", new String[]{id.toString()});
		if(cursor.moveToFirst()){
			 sid = cursor.getInt(cursor.getColumnIndex("id"));
		}
		return sid;
	}
	public void update(int score){
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.execSQL("update highscore set score =? where id=?",new Object[]{score,1});
	}
}
