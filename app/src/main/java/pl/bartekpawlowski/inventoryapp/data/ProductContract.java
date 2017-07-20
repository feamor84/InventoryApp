package pl.bartekpawlowski.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Class created to handle all names of constants connected with database queries
 */

public class ProductContract {

    // Building Uri
    public static String CONTENT_AUTHORITIES = "pl.bartekpawlowski.inventoryapp";
    private static Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITIES);
    public static String PRODUCTS_PATH = "products";

    /**
     * Private constructor to avoid call class by accident
     */
    private ProductContract() {
    }

    public static class ProductEntry implements BaseColumns {

        // Final Uri
        public final static Uri CONTENT_PATH = Uri.withAppendedPath(BASE_CONTENT_URI, PRODUCTS_PATH);

        // Products table name
        public final static String TABLE_NAME = "products";

        // Table column names
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_NAME = "name";
        public final static String COLUMN_PRICE = "price";
        public final static String COLUMN_QUANTITY = "quantity";
        public final static String COLUMN_SUPPLIER_NAME = "supplier_name";
        public final static String COLUMN_SUPPLIER_EMAIL = "supplier_email";
        public final static String COLUMN_IMAGE_URI = "image_uri";

        // MIME types
        public final static String LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITIES + "/" + PRODUCTS_PATH;
        public final static String ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITIES + "/" + PRODUCTS_PATH;
    }
}
