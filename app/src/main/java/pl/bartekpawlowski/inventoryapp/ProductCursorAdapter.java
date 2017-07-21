package pl.bartekpawlowski.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

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
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.mProductName.setText(cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME)));
        viewHolder.mProductPrice.setText(cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRICE)));
        viewHolder.mProductQty.setText(cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_QUANTITY)));
    }
}
