package pl.bartekpawlowski.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.bartekpawlowski.inventoryapp.data.ProductContract.ProductEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //Logging tag
    private final static String LOG_TAG = EditorActivity.class.getSimpleName();
    // Loader ID
    private final static int LOADER_ID = 0;

    // Conditions for update quality
    private final static int INCREASE_QUANTITY = 1;
    private final static int DECREASE_QUANTITY = 0;

    // Request image capture
    private final static int REQUEST_TAKE_PHOTO = 1;

    // Photo path
    String mCurrentPhotoPath;

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

    @BindView(R.id.product_editor_quantity_heading)
    TextView mQuantityHeader;
    @BindView(R.id.product_editor_quantity_container)
    LinearLayout mQuantityContainer;
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
    Uri mPhotoUri;

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

            // Hide increase and decrease button when editor is in add mode
            mQuantityHeader.setVisibility(View.GONE);
            mQuantityContainer.setVisibility(View.GONE);
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
                return true;
            case R.id.delete_product:
                if (deleteProduct()) {
                    finish();
                }
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
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

    public void increaseQuantityByOne(View view) {
        int quantity = getCurrentQuantity();
        updateQuantity(quantity, INCREASE_QUANTITY);
    }

    public void decreaseQuantityByOne(View view) {
        int quantity = getCurrentQuantity();
        if (quantity > 0) {
            updateQuantity(quantity, DECREASE_QUANTITY);
        } else {
            Toast.makeText(this, R.string.toast_quantity_is_zero_editor_mode, Toast.LENGTH_SHORT).show();
        }
    }

    private int getCurrentQuantity() {
        int qty = 0;
        String[] projection = new String[]{
                ProductEntry._ID,
                ProductEntry.COLUMN_QUANTITY
        };

        Cursor cursor = getContentResolver().query(mItemUri, projection, null, null, null);

        try {
            if (cursor.moveToFirst()) {
                qty = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_QUANTITY));
            }
        } finally {
            cursor.close();
        }

        return qty;
    }

    private void updateQuantity(int quantity, int condition) {
        ContentValues contentValues = new ContentValues();
        switch (condition) {
            case INCREASE_QUANTITY:
                contentValues.put(ProductEntry.COLUMN_QUANTITY, quantity + 1);
                break;
            case DECREASE_QUANTITY:
                contentValues.put(ProductEntry.COLUMN_QUANTITY, quantity - 1);
                break;
            default:
                throw new IllegalArgumentException("Argument condition out of bounds.");
        }

        int row = getContentResolver().update(mItemUri, contentValues, null, null);
    }

    public void takePhoto(View view) {
        dispatchTakePictureIntent();
        galleryAddPic();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "pl.bartekpawlowski.inventoryapp.fileprovider",
                        photoFile);
                Log.i("Photo Uri", photoURI.toString());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mPhotoUri = data.getData();
        Log.i(LOG_TAG, mPhotoUri.toString());
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
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
