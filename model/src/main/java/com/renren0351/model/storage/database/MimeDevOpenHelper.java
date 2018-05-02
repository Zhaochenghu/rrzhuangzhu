package com.renren0351.model.storage.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.renren0351.model.bean.DaoMaster;

import cn.com.leanvision.baseframe.log.DebugLog;

/********************************
 * Created by lvshicheng on 2016/12/12.
 * <p>
 * 数据库管理
 ********************************/
public class MimeDevOpenHelper extends DaoMaster.OpenHelper {

  public MimeDevOpenHelper(Context context, String name) {
    super(context, name);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // TODO 升级数据库走这里
    DebugLog.log("APP ======> Upgrading schema from version %d to %d", oldVersion, newVersion);
//    if (oldVersion == 1) { // 添加表
//      Database gdb = wrap(db);
//      FenceInfoDao.createTable(gdb, true);
//    } else if (oldVersion == 2) { // 添加字段
//      if (checkColumnExists(db, FenceInfoDao.TABLENAME, FenceInfoDao.Properties.TestField.columnName))
//        return;
//      String sql = String.format("ALTER TABLE %s ADD %s TEXT", FenceInfoDao.TABLENAME, FenceInfoDao.Properties.TestField.columnName);
//      db.execSQL(sql);
//    }
  }

  public boolean checkColumnExists(SQLiteDatabase db, String tableName
      , String columnName) {
    boolean result = false;
    Cursor cursor = null;
    try {
      cursor = db.rawQuery("select * from sqlite_master where name = ? and sql like ?"
          , new String[]{tableName, "%" + columnName + "%"});
      result = null != cursor && cursor.moveToFirst();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (null != cursor && !cursor.isClosed()) {
        cursor.close();
      }
    }
    return result;
  }
}
