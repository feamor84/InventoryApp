package pl.bartekpawlowski.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import pl.bartekpawlowski.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Helper class use to manage database creation and upgrade
 */

public class ProductDbHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "Warehouse.db";
    private final static int DB_VERSION = 1;

    // String to build one table in database
    private final static String CREATE_TABLE = "CREATE TABLE " + ProductEntry.TABLE_NAME + " (" +
            ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ProductEntry.COLUMN_NAME + " TEXT NOT NULL, " +
            ProductEntry.COLUMN_PRICE + " REAL NOT NULL, " +
            ProductEntry.COLUMN_QUANTITY + " INTEGER NOT NULL, " +
            ProductEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, " +
            ProductEntry.COLUMN_SUPPLIER_EMAIL + " TEXT NOT NULL, " +
            ProductEntry.COLUMN_IMAGE_URI + " TEXT );";
    // String to clear database
    private final static String DROP_TABLE = "DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME + " ;";

    public ProductDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_TABLE);
    }
}
