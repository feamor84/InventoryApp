package pl.bartekpawlowski.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Toast;

import pl.bartekpawlowski.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Adapter to populate ListView in CatalogActivity with database items
 */

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View newView = LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(newView);
        newView.setTag(viewHolder);

        return newView;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        final long itemId = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry._ID));

        viewHolder.mProductName.setText(cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME)));
        viewHolder.mProductPrice.setText(cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRICE)));
        viewHolder.mProductQty.setText(cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_QUANTITY)));

        viewHolder.mProductSaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri itemUri = ContentUris.withAppendedId(ProductEntry.CONTENT_PATH, itemId);
                int itemQuantity = getCurrentQuantity(itemUri, context);
                if (itemQuantity > 0) {
                    decreaseQuantityByOne(itemUri, context, itemQuantity);
                } else {
                    Toast.makeText(context, R.string.toast_quantity_is_zero, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private int getCurrentQuantity(Uri uri, Context context) {
        int itemQuantity = 0;

        String[] projection = new String[]{
                ProductEntry._ID,
                ProductEntry.COLUMN_QUANTITY
        };

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        try {
            if (cursor.moveToFirst()) {
                itemQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_QUANTITY));
            }
        } finally {
            cursor.close();
        }

        return itemQuantity;
    }

    private void decreaseQuantityByOne(Uri uri, Context context, int currentQuantity) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductEntry.COLUMN_QUANTITY, currentQuantity - 1);

        int row = context.getContentResolver().update(uri, contentValues, null, null);
    }
}
