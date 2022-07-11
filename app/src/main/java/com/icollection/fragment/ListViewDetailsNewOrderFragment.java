package com.icollection.fragment;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.icollection.AppController;
import com.icollection.R;

import com.icollection.modelservice.OrderItem;
import com.icollection.util.ImageUtil;



/**
 * Created by Mounzer on 8/23/2017.
 */

public class ListViewDetailsNewOrderFragment extends Fragment {
    private static final String LOG_TAG = ListViewDetailsNewOrderFragment.class.getSimpleName();
    public static final String ARG_ITEM_ID = "order_list";
    ImageView image;
    TextView category;
    OrderItem order;
    private View parentView;
    private FloatingActionButton fab;
    AppController appController;

    /**
     * Create a new instance for ListViewDetailsNewOrderFragment
     * @param order a News object
     * @return a new instance for ListViewDetailsNewOrderFragment
     */
    public static ListViewDetailsNewOrderFragment newInstance(OrderItem order) {
        ListViewDetailsNewOrderFragment listViewDetailsNewOrderFragment = new ListViewDetailsNewOrderFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("orderSelected", order);
        listViewDetailsNewOrderFragment.setArguments(bundle);
        return listViewDetailsNewOrderFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the user interface layout for this Fragment.
        View rootView = inflater.inflate(R.layout.fragment_listview_details_first_design, container, false);

        parentView = rootView.findViewById(R.id.parent);
        appController = (AppController) getActivity().getApplication();

        order = (OrderItem) getArguments().getParcelable("orderSelected");

        image = (ImageView) rootView.findViewById(R.id.image);
        category =  rootView.findViewById(R.id.category);
        if (order.getStatus().equals("2")) {
            category.setBackgroundResource(R.drawable.tags_background_green);
        } else if (order.getStatus().equals("0")) {
            category.setBackgroundResource(R.drawable.tags_background_yellow);
        } else if (order.getStatus().equals("1")) {
            category.setBackgroundResource(R.drawable.tags_background_red);
        } else if (order.getStatus().equals("Nature")) {
            category.setBackgroundResource(R.drawable.tags_background_purple);
        } else {
            category.setBackgroundResource(R.drawable.tags_background_orange);
        }

        //image order
        ImageUtil.displayImage(image, "", null);

        switch (order.getStatus()){
            case "0":
                category.setText("Order Baru");
                break;
            case "1":
                category.setText("Belum Bayar");
                break;
            case "2":
                category.setText("Sudah Bayar");
                break;

        }



        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fabToggle();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (appController.isAddedToFavorites(order)) {
                    appController.removeFromFavorites(order);
                    Snackbar.make(parentView, "Removed from favorites", Snackbar.LENGTH_SHORT).show();
                } else {
                    appController.addToFavorites(order);
                    Snackbar.make(parentView, "Added to favorites", Snackbar.LENGTH_SHORT).show();
                }
                fabToggle();
            }
        });

        return rootView;

    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_listview_first_design_details, menu);
        for(int i = 0; i < menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorTextSecondary), PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_share) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = getString(R.string.share_format,
                    order.getNama(),
                    order.getAlamat1());
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }
        return super.onOptionsItemSelected(item);
    }

    private void fabToggle() {
        if (appController.isAddedToFavorites(order)) {
            fab.setImageResource(R.mipmap.ic_favorite_white_24dp);
        } else{
            fab.setImageResource(R.mipmap.ic_favorite_border_white_24dp);
        }
    }
}
