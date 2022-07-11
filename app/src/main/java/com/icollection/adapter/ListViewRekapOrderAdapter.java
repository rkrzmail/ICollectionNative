package com.icollection.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.icollection.AppController;
import com.icollection.R;
import com.icollection.modelservice.OrderItem;
import com.icollection.util.AnimationUtil;
import com.icollection.util.AppUtil;

import java.util.ArrayList;

/**
 * Created by Mounzer on 8/22/2017.
 */

public class ListViewRekapOrderAdapter extends RecyclerView.Adapter<ListViewRekapOrderAdapter.ItemViewHolder> {
    private ArrayList<OrderItem> orderList;
    private LayoutInflater mInflater;
    private Context mContext;
    private int lastPosition = -1;
    private boolean onAttach = true;
    private OnItemClickListener mOnItemClickListener;

    public ListViewRekapOrderAdapter(Context mContext, ArrayList<OrderItem> orderItem) {
        this.mContext = mContext;
        this.orderList = orderItem;
        this.mInflater = LayoutInflater.from(mContext);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
       // private ImageView image;
        private TextView title, description, category,nopsb,nokwitansi,nopol,notel,bhc;
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
    @Override
    public void onBindViewHolder(ItemViewHolder holder, final int position) {
        final OrderItem order = orderList.get(position);
        holder.title.setText(order.getNama()+"( "+order.getTypeKendaraan()+" )");
        holder.description.setText(order.getAlamat1());
        if(AppController.getRekap().equalsIgnoreCase("bayar")){
            holder.category.setText("Rp."+AppUtil.formatCurrency(order.getJmlTotTerbayar()));
        }else{
            holder.category.setText("Rp."+AppUtil.formatCurrency(order.getJmlAngsuran()));
        }

        holder.nopsb.setText("No PSB :"+order.getNoPsb() +  ListViewNewOrderAdapter.getDateX(order) );
        holder.nokwitansi.setText("No Kwitansi :"+order.getNoKwitansi());
        holder.nopol.setText("Nopol :"+order.getNopol() );
        holder.notel.setText("Telepon :"+order.getTelepon());

        holder.bhc.setBackgroundResource(R.drawable.tags_background_red);
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
       /* switch (order.getStatus()){
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

        }
*/


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
