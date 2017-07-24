package pl.bartekpawlowski.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import pl.bartekpawlowski.inventoryapp.data.ProductContract.ProductEntry;

/**
 * ContentProvider to exchange information between UI and database
 */

public class ProductProvider extends ContentProvider {

    // Logging tag
    private final static String LOG_TAG = ProductProvider.class.getSimpleName();
    /**
     * Codes for Uri matcher to manage queries from UI
     * <p>
     * PRODUCTS query for all data in the table
     * PRODUCT_ID query for one row in the table
     */
    private final static int PRODUCTS = 100;
    private final static int PRODUCT_ID = 101;
    // Instance of Uri matcher
    private final static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Add rules to match
    static {
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITIES, ProductContract.PRODUCTS_PATH, PRODUCTS);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITIES, ProductContract.PRODUCTS_PATH + "/#", PRODUCT_ID);
    }

    // Instance of DB helper
    private ProductDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ProductDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Wrong Uri in QUERY with: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        Uri itemUri = Uri.EMPTY;

        if (sanitizeData(contentValues)) {
            final int match = sUriMatcher.match(uri);
            switch (match) {
                case PRODUCTS:
                    getContext().getContentResolver().notifyChange(uri, null);
                    itemUri = insertProduct(uri, contentValues);
                    break;
                default:
                    throw new IllegalArgumentException("Wrong Uri in INSERT with: " + uri);
            }
        }

        return itemUri;
    }

    /**
     * Insert method helper to keep clean code in insert() method
     *
     * @param uri           link to content passed from insert() method
     * @param contentValues values passed to insert() method from UI
     * @return Uri          build from query inserted id and passed Uri
     */
    private Uri insertProduct(Uri uri, ContentValues contentValues) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(ProductEntry.TABLE_NAME, null, contentValues);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowNumbers = 0;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                rowNumbers = database.delete(ProductEntry.TABLE_NAME, null, null);
                break;
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowNumbers = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Wrong Uri in DELETE with: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowNumbers;
    }


    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowNumber = 0;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                rowNumber = updateProduct(uri, contentValues, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowNumber = updateProduct(uri, contentValues, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Wrong Uri in UPDATE with: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowNumber;
    }

    /**
     * Helper method to update product to keep readable code in overridden update() method
     *
     * @param uri           link to content provided through update() method from UI
     * @param contentValues values to pass to database
     * @param selection     on witch conditions select rows to update
     * @param selectionArgs selection witch rows should be updated, if null each row will be updated
     *                      if update() modified it only selected row will be updated
     * @return number of updated rows
     */
    private int updateProduct(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        return database.update(ProductEntry.TABLE_NAME, contentValues, selection, selectionArgs);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        String type = "";

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                type = ProductEntry.LIST_TYPE;
                break;
            case PRODUCT_ID:
                type = ProductEntry.ITEM_TYPE;
                break;
            default:
                throw new IllegalArgumentException("Cannot get MIME type from Uri: " + uri);
        }

        return type;
    }

    private boolean sanitizeData(ContentValues contentValues) {
        boolean isValid = true;

        if (contentValues.containsKey(ProductEntry.COLUMN_NAME)) {
            if (contentValues.getAsString(ProductEntry.COLUMN_NAME).isEmpty()) {
                isValid = false;
                throw new IllegalArgumentException("Product name is empty!");
            }
        }

        if (contentValues.containsKey(ProductEntry.COLUMN_PRICE)) {
            if (contentValues.getAsFloat(ProductEntry.COLUMN_PRICE) < 0) {
                isValid = false;
                throw new IllegalArgumentException("Product price is less than 0");
            }
        }

        if (contentValues.containsKey(ProductEntry.COLUMN_QUANTITY)) {
            if (contentValues.getAsInteger(ProductEntry.COLUMN_QUANTITY) < 0) {
                isValid = false;
                throw new IllegalArgumentException("Product quantity is less than 0");
            }
        }

        if (contentValues.containsKey(ProductEntry.COLUMN_SUPPLIER_NAME)) {
            if (contentValues.getAsString(ProductEntry.COLUMN_SUPPLIER_NAME).isEmpty()) {
                isValid = false;
                throw new IllegalArgumentException("Supplier name is empty!");
            }
        }

        if (contentValues.containsKey(ProductEntry.COLUMN_SUPPLIER_EMAIL)) {
            if (contentValues.getAsString(ProductEntry.COLUMN_SUPPLIER_EMAIL).isEmpty()) {
                isValid = false;
                throw new IllegalArgumentException("Supplier email address is empty!");
            }
        }

        return isValid;
    }
}
