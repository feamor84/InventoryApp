package pl.bartekpawlowski.inventoryapp;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Helper class to hold Views from list_item layout and pass it to ProductCursorAdapter.
 * This class improves performance of the application.
 */

public class ViewHolder {
    // Declare Views with Butterknife from list_item layout
    @BindView(R.id.list_product_name)
    TextView mProductName;

    @BindView(R.id.list_product_price)
    TextView mProductPrice;

    @BindView(R.id.list_product_qty)
    TextView mProductQty;

    @BindView(R.id.list_product_sale_button)
    Button mProductSaleButton;

    public ViewHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
