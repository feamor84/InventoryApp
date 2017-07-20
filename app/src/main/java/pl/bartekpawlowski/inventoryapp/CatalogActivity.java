package pl.bartekpawlowski.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import pl.bartekpawlowski.inventoryapp.data.ProductContract.ProductEntry;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductEntry.COLUMN_NAME, "Butter");
        contentValues.put(ProductEntry.COLUMN_PRICE, "1.11");
        contentValues.put(ProductEntry.COLUMN_QUANTITY, "1");
        contentValues.put(ProductEntry.COLUMN_SUPPLIER_NAME, "Alsan");
        contentValues.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, "aas@vvv.com");
        contentValues.put(ProductEntry.COLUMN_IMAGE_URI, "Obrazek");
        Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_PATH, contentValues);
        Log.i("insert", newUri.toString());

        Cursor cursor = getContentResolver().query(ProductEntry.CONTENT_PATH, null, null, null, null);
        Log.i("Wynik", cursor.toString());
        cursor.close();

        ContentValues contentValues1 = new ContentValues();
        contentValues1.put(ProductEntry.COLUMN_SUPPLIER_NAME, "Mlekpol");
        int row = getContentResolver().update(ProductEntry.CONTENT_PATH, contentValues1, null, null);

        Log.i("Update", String.valueOf(row));

        Log.i("type", getContentResolver().getType(ProductEntry.CONTENT_PATH));

        int del = getContentResolver().delete(ProductEntry.CONTENT_PATH, null, null);

        Log.i("delete", String.valueOf(del));

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * Method to handle FloatingActionButton click to start EditorActivity
     * Instead creating new onClickListener to handle only one action
     *
     * @param view from FAB
     */
    public void onFabClick(View view) {
        Intent intent = new Intent(this, EditorActivity.class);
        startActivity(intent);
    }
}
