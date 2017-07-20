package pl.bartekpawlowski.inventoryapp.data;

import android.provider.BaseColumns;

/**
 * Class created to handle all names of constants connected with database queries
 */

public class ProductContract {

    /**
     * Private constructor to avoid call class by accident
     */
    private ProductContract() {
    }

    ;

    public static class ProductEntry implements BaseColumns {

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
    }
}
