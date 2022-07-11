package com.icollection.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.icollection.BHCActivity;
import com.icollection.R;
import com.icollection.modelservice.News;
import com.icollection.modelservice.OrderItem;
import com.icollection.util.AnimationUtil;
import com.icollection.util.ImageUtil;
import com.naa.data.Nson;


import java.util.ArrayList;

/**
 * Created by Mounzer on 8/22/2017.
 */

public class ListViewNewOrderAdapter extends RecyclerView.Adapter<ListViewNewOrderAdapter.ItemViewHolder> {
    private ArrayList<OrderItem> orderList;
    private LayoutInflater mInflater;
    private Context mContext;
    private int lastPosition = -1;
    private boolean onAttach = true;
    private OnItemClickListener mOnItemClickListener;
    private Nson bhc;

    public ListViewNewOrderAdapter(Context mContext, ArrayList<OrderItem> orderItem, Nson bhc) {
        this.mContext = mContext;
        this.orderList = orderItem;
        this.mInflater = LayoutInflater.from(mContext);
        this.bhc = bhc;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        // private ImageView image;
        private TextView title, description, category, nopsb, nokwitansi, nopol, notel, bhc;
        private MaterialRippleLayout layout_parent;

        public ItemViewHolder(View itemView) {
            super(itemView);

            //   image = (ImageView) itemView.findViewById(R.id.image);

            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            category = (TextView) itemView.findViewById(R.id.category);
            bhc = (TextView) itemView.findViewById(R.id.bhc);
            nopsb = (TextView) itemView.findViewById(R.id.nopsb);
            nokwitansi = (TextView) itemView.findViewById(R.id.nokwitansi);
            nopol = (TextView) itemView.findViewById(R.id.nopol);
            notel = (TextView) itemView.findViewById(R.id.notel);
            layout_parent = (MaterialRippleLayout) itemView.findViewById(R.id.layout_parent);
        }

    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_listview_first_design, parent, false);
        return new ItemViewHolder(view);
    }

    public static String getDateX(OrderItem order) {
        if (order.getTgl_kirim2()!=null){
            return    " (" + String.valueOf(order.getTgl_kirim2()) + ")";
        }else if (order.getText2()!=null){
            return    " (" + String.valueOf(order.getText2()) + ")";
        }
        return   "";
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, final int position) {
        final OrderItem order = orderList.get(position);
        holder.title.setText(order.getNama() + " (" + order.getTypeKendaraan() + ")");
        holder.description.setText(order.getAlamat1());
        holder.category.setText(order.getStatus());
        holder.nopsb.setText("No PSB :" + order.getNoPsb() +  ListViewNewOrderAdapter.getDateX(order)  );
        holder.nokwitansi.setText("No Kwitansi :" + order.getNoKwitansi());
        holder.nopol.setText("Nopol :" + order.getNopol());
        holder.notel.setText("Telepon :" + order.getTelepon());

        holder.bhc.setBackgroundResource(R.drawable.tags_background_yellow);
        if (BHCActivity.existBHC(bhc, order)) { //if (bhc.get("data").containsKey(order.getNoPsb() + BHCActivity.getStatusBHC(order.getStatusBayar()))){
            holder.bhc.setBackgroundResource(R.drawable.tags_background_green);
            holder.layout_parent.setTag("EXIST");
        } else {
            holder.layout_parent.setTag("");
        }
        if (order.getStatus().equals("2")) {
            holder.category.setBackgroundResource(R.drawable.tags_background_green);
        } else if (order.getStatus().equals("0")) {
            holder.category.setBackgroundResource(R.drawable.tags_background_yellow);
        } else if (order.getStatus().equals("1")) {
            holder.category.setBackgroundResource(R.drawable.tags_background_red);
        } else if (order.getStatus().equals("Nature")) {
            holder.category.setBackgroundResource(R.drawable.tags_background_purple);
        } else {
            holder.category.setBackgroundResource(R.drawable.tags_background_orange);
        }
        switch (order.getStatus()) {
            case "0":
                holder.category.setText("Order Baru");
                break;
            case "1":
                holder.category.setText("Belum Bayar");
                break;
            case "2":
                holder.category.setText("Sudah Bayar");
                break;
            case "pending_janji":
                holder.category.setText("Pending Sent");
                break;
            case "pending_bayar":
                holder.category.setText("Pending Sent");
                break;
            case "99":
                holder.category.setText("Pending Success");
                break;
            default:
                holder.category.setText(order.getStatus());
        }

        //NEW FLAG
        if (order.getStatus().equalsIgnoreCase("0")) {
            holder.category.setText("ORDER BARU");
            holder.category.setBackgroundResource(R.drawable.tags_background_yellow);
        } else if (String.valueOf(order.getStatusBayar()).trim().equals("GAGAL")) {
            holder.category.setBackgroundResource(R.drawable.tags_background_grey);
            holder.category.setText("GAGAL");
        } else if (String.valueOf(order.getStatusBayar()).trim().equals("BAYAR")) {
            holder.category.setText("BAYAR");
            holder.category.setBackgroundResource(R.drawable.tags_background_green);
        } else if (String.valueOf(order.getStatusBayar()).trim().equals("SUDAH BAYAR")) {
            holder.category.setText("SUDAH BAYAR");
            holder.category.setBackgroundResource(R.drawable.tags_background_green2);
        } else if (String.valueOf(order.getStatusBayar()).trim().equals("TB")) {
            holder.category.setText("TB");
            holder.category.setBackgroundResource(R.drawable.tags_background_greenblue);
        } else if (String.valueOf(order.getStatusBayar()).trim().equals("JB")) {
            holder.category.setText("JB");
            holder.category.setBackgroundResource(R.drawable.tags_background_red);
        } else if (String.valueOf(order.getStatusBayar()).trim().startsWith("TK ")) {
            holder.category.setText(order.getStatusBayar());
            holder.category.setBackgroundResource(R.drawable.tags_background_purple);
        } else if (String.valueOf(order.getStatusBayar()).trim().startsWith("LAIN")) {
            holder.category.setText(order.getStatusBayar());
            holder.category.setBackgroundResource(R.drawable.tags_background_blue);
        } else {
            holder.category.setText("*" + (order.getStatusBayar()));
            holder.category.setBackgroundResource(R.drawable.tags_background_yellow);
        }
        switch (order.getStatus()) {
            case "pending_janji":
                holder.category.setText("Pend. " + holder.category.getText().toString());
                break;
            case "pending_bayar":
                holder.category.setText("Pend. " + holder.category.getText().toString());
                break;
            case "99":
                holder.category.setText("Pend.. " + holder.category.getText().toString());
                break;
            default:
        }

        //image order
        //ImageUtil.displayImage(holder.image, "", null);

        holder.layout_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, order, position);
                }
            }
        });

        setAnimation(holder.itemView, position);

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                onAttach = false;
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void setNewsFilter(ArrayList<OrderItem> mOrderList) {
        orderList = new ArrayList<>();
        orderList.addAll(mOrderList);
        notifyDataSetChanged();
    }

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            AnimationUtil.animateFadeIn(view, onAttach ? position : -1);
            lastPosition = position;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, OrderItem obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }
}
