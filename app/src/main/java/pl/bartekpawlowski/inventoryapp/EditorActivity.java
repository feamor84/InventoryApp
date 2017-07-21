package pl.bartekpawlowski.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.bartekpawlowski.inventoryapp.data.ProductContract.ProductEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //Logging tag
    private final static String LOG_TAG = EditorActivity.class.getSimpleName();
    // Loader ID
    private final static int LOADER_ID = 0;

    // Binding views using Butterknife
    @BindView(R.id.editor_name)
    EditText mProductName;
    @BindView(R.id.editor_price)
    EditText mProductPrice;
    @BindView(R.id.editor_qty)
    EditText mProductQty;
    @BindView(R.id.editor_supplier_name)
    EditText mSupplierName;
    @BindView(R.id.editor_supplier_email)
    EditText mSupplierEmail;

    @BindView(R.id.editor_decrease)
    Button mProductDecrease;
    @BindView(R.id.editor_increase)
    Button mProductIncrease;
    @BindView(R.id.editor_qty_text_view)
    TextView mProductQtyTextView;

    @BindView(R.id.editor_add_image)
    Button mAddImage;
    @BindView(R.id.editor_image_holder)
    ImageView mImageView;

    Uri mItemUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        ButterKnife.bind(this);

        mItemUri = getIntent().getData();

        if (mItemUri != null) {
            this.setTitle(R.string.product_editor_edit_mode);
            getLoaderManager().initLoader(LOADER_ID, null, this);
        } else {
            invalidateOptionsMenu();
            this.setTitle(R.string.product_editor_add_mode);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        MenuItem deleteProductButton = menu.findItem(R.id.delete_product);

        if (mItemUri == null) {
            deleteProductButton.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_product:
                if (saveProduct(collectFormData())) {
                    finish();
                }
                break;
            case R.id.delete_product:
                if (deleteProduct()) {
                    finish();
                }
                break;
            case android.R.id.home:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private ContentValues collectFormData() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductEntry.COLUMN_NAME, mProductName.getText().toString());
        contentValues.put(ProductEntry.COLUMN_PRICE, mProductPrice.getText().toString());
        contentValues.put(ProductEntry.COLUMN_QUANTITY, mProductQty.getText().toString());
        contentValues.put(ProductEntry.COLUMN_SUPPLIER_NAME, mSupplierName.getText().toString());
        contentValues.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, mSupplierEmail.getText().toString());

        return contentValues;
    }

    private boolean saveProduct(ContentValues contentValues) {

        if (mItemUri != null) {
            int rowsNumber = getContentResolver().update(mItemUri, contentValues, null, null);
            if (rowsNumber != 0) {
                return true;
            }
        } else {
            Uri uri = getContentResolver().insert(ProductEntry.CONTENT_PATH, contentValues);
            if (uri != null) {
                return true;
            }
        }

        return false;
    }

    private boolean deleteProduct() {
        int rowsNumber = getContentResolver().delete(mItemUri, null, null);

        return rowsNumber != 0;
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, mItemUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME);
            int priceIndex = cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRICE);
            int qtyIndex = cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_QUANTITY);
            int supplierNameIndex = cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_SUPPLIER_NAME);
            int supplierEmailIndex = cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_SUPPLIER_EMAIL);

            mProductName.setText(cursor.getString(nameIndex));
            mProductPrice.setText(String.valueOf(cursor.getInt(priceIndex)));
            mProductQty.setText(String.valueOf(cursor.getInt(qtyIndex)));
            mSupplierName.setText(cursor.getString(supplierNameIndex));
            mSupplierEmail.setText(cursor.getString(supplierEmailIndex));
            mProductQtyTextView.setText(String.valueOf(cursor.getInt(qtyIndex)));
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mProductName.setText("");
        mProductPrice.setText("");
        mProductQty.setText("");
        mSupplierName.setText("");
        mSupplierEmail.setText("");
        mProductQtyTextView.setText("");
    }
}
