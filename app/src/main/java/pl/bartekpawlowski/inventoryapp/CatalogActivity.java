package pl.bartekpawlowski.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.bartekpawlowski.inventoryapp.data.ProductContract.ProductEntry;
import pl.bartekpawlowski.inventoryapp.data.ProductDbHelper;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // Logging tag
    private final static String LOG_TAG = CatalogActivity.class.getSimpleName();
    // Loader ID
    private final static int LOADER_ID = 1;

    // Get Views from layout using Butterknife
    // List to populate items from database
    @BindView(R.id.catalog_list)
    ListView productList;
    // View to show when list is empty
    @BindView(R.id.empty_list_container)
    LinearLayout emptyListView;

    // DB helper call
    private ProductDbHelper mDbHelper;

    // ProductCursorAdapter call
    private ProductCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        ButterKnife.bind(this);

        mDbHelper = new ProductDbHelper(this);

        // Call adapter
        mAdapter = new ProductCursorAdapter(this, null);
        // Set adapter to ListView
        productList.setAdapter(mAdapter);
        // Set empty view for ListView
        productList.setEmptyView(emptyListView);

        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                Uri uri = ContentUris.withAppendedId(ProductEntry.CONTENT_PATH, id);
                intent.setData(uri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = new String[]{
                ProductEntry._ID,
                ProductEntry.COLUMN_NAME,
                ProductEntry.COLUMN_PRICE,
                ProductEntry.COLUMN_QUANTITY
        };

        return new CursorLoader(this, ProductEntry.CONTENT_PATH, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
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
