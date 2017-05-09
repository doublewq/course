package com.mxn.soul.flowingdrawer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/4/17.
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {
	public static final String CREATE_KC = "create table KC_table ("
			+ "kc_xq text, "
			+ "kc_Name text, "
			+ "kc_week text, "
			+ "kc_lesson text,"
			+ "kc_info text)";

	public static final String CREATE_Teacher = "create table Teacher_table ("
			+ "Teach_xq text, "
			+ "Teach_Name text, "
			+ "Teach_week text, "
			+ "Teach_lesson text,"
			+ "Teach_info text)";

	public static final String CREATE_Room = "create table Room_table ("
			+ "Room_xq text, "
			+ "Room_Name text, "
			+ "Room_week text, "
			+ "Room_lesson text,"
			+ "Room_info text)";

	public static final String CREATE_Grade = "create table Grade_table ("
			+ "Grade_xq text, "
			+ "Grade_Name text, "
			+ "Grade_week text, "
			+ "Grade_lesson text,"
			+ "Grade_info text)";

	private Context mContext;

	public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);
		this.mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		sqLiteDatabase.execSQL(CREATE_KC);//执行sql语句创建数据库
		sqLiteDatabase.execSQL(CREATE_Teacher);//执行sql语句创建数据库
		sqLiteDatabase.execSQL(CREATE_Room);//执行sql语句创建数据库
		sqLiteDatabase.execSQL(CREATE_Grade);//执行sql语句创建数据库
		//Toast.makeText(mContext,"创建成功",Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

	}
}
