package com.icollection.fragment;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.icollection.APIInterfacesRest;
import com.icollection.AppController;
import com.icollection.DeviceListActivity;
import com.icollection.LoginActivity;
import com.icollection.PrintGraphics;
import com.icollection.R;
import com.icollection.adapter.ListViewRekapOrderAdapter;
import com.icollection.modelservice.Order;
import com.icollection.modelservice.OrderItem;
import com.icollection.modelservice.OrderItem_Table;
import com.icollection.tracker.GpsTrackerIntentService;
import com.icollection.util.APIClient;
import com.icollection.util.AppUtil;
import com.icollection.util.Messagebox;
import com.icollection.util.Utility;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import com.sdk.bluetooth.android.BluetoothPrinter;
import com.zj.btsdk.BluetoothService;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Mounzer on 8/23/2017.
 */

public class ListViewRekapBayarFragment extends Fragment implements ListViewRekapOrderAdapter.OnItemClickListener,
        SearchView.OnQueryTextListener, Runnable {
    private static final String LOG_TAG = ListViewRekapBayarFragment.class.getSimpleName();
    public static final String ARG_ITEM_ID = "order_rekap";
    private static final String DATA_STATE = "data_state";
    RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    LinearLayoutManager layoutManager;
    private ListViewRekapOrderAdapter listViewRekapOrderAdapter;
    private ArrayList<OrderItem> orderItems;
    private OnItemSelectedListener listener;
    private Context context;
    private TextView txtJumlahBayar,txtLabel;
    private TextView txtJumlahBayar2,txtLabel2, txtLabel3;
    private Button btnScan, btnPrint;

    /**
     * Callback used to communicate with ListViewNewOrderFragment to Determine the Current Layout.
     * ListViewFirstDesignActivity implements this interface and communicates with ListViewNewOrderFragment.
     */
    public interface OnItemSelectedListener {
        public void onItemSelected(OrderItem news, View view);
    }


    @Override
    public void run() {
        getOrderList(swipeRefreshLayout);
    }

    @Override
    public void onResume() {
        super.onResume();
        getOrderList(null);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        if (context instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement OnItemSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        closBT();

        if (mPrinter!=null) {
            try {
                mPrinter.close();
            } catch (Exception e) { }
        }
        super.onDetach();
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
        View rootView = inflater.inflate(R.layout.fragment_listview_rekap, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        txtJumlahBayar = (TextView) rootView.findViewById(R.id.txtJumlahUang);
        txtJumlahBayar2= (TextView) rootView.findViewById(R.id.txtJumlahUang2);
        txtLabel  = (TextView) rootView.findViewById(R.id.txtLabel);
        txtLabel2 = (TextView) rootView.findViewById(R.id.txtLabel2);
        txtLabel3 = (TextView) rootView.findViewById(R.id.txtLabel3);
        btnScan = (Button) rootView.findViewById(R.id.btnScan);
        btnPrint = (Button) rootView.findViewById(R.id.btnPrint);

        if (AppController.getMode().equalsIgnoreCase("new")){
            txtLabel2.setVisibility(View.GONE);
            txtJumlahBayar2.setVisibility(View.GONE);
        }

        if(AppController.getRekap().equalsIgnoreCase("bayar")){
            txtLabel.setText("Uang anda adalah :");
            txtJumlahBayar2.setVisibility(View.GONE);
        }else{
            txtLabel.setText("Uang yang belum tertagih adalah :");
            txtLabel2.setVisibility(View.GONE);
            txtJumlahBayar2.setVisibility(View.GONE);
        }

        if (SelectedBDAddress.equalsIgnoreCase("")) {
            btnPrint.setEnabled(false);
        } else {
            btnScan.setText("Paired to:" + SelectedBDAddress);
        }

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (isBluetoothAvailable(getContext())) {
                    Intent serverIntent = null;
                    serverIntent = new Intent(getContext(), DeviceListActivity.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                } else {
                    ShowMessageBox("Bluetooth anda harus diaktifkan terlebih dahulu!", "Perhatian");

                }
            }
        });
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (orderItems == null) {
                    ShowMessageBox2("Tidak ada data rekap! (Internet bermasalh)", "Perhatian");
                }else if(orderItems.size()>0) {
                    printData();
                }else{
                    ShowMessageBox2("Tidak ada data rekap!", "Perhatian");
                }

            }

        });

        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // save all data for orientation change.
        if (savedInstanceState != null) {
            // We will restore the state of data list when the activity is re-created
            orderItems = savedInstanceState.getParcelableArrayList(DATA_STATE);
        } else {
            // Create a list of news and populate it with hardcoded data.
            getOrderList(null);
        }


        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);

                getOrderList(swipeRefreshLayout);

                /*if (Utility.getSetting(getActivity(),  "pend","").equalsIgnoreCase("") && Utility.getSetting(getActivity(),  "pendb","").equalsIgnoreCase("")) {
                }else{
                    Intent intent = new Intent(getActivity(), GpsTrackerIntentService.class);
                    intent.putExtra("smode","auto");
                    getActivity().startService(intent);
                }*/
            }
        });

        return rootView;

    }

    /**
     * Before the activity is destroyed, onSaveInstanceState() gets called.
     * The onSaveInstanceState() method saves the list of data.
     *
     * @param outState bundle
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (orderItems!=null) {
            outState.putParcelableArrayList(DATA_STATE, orderItems);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_listview_first_design, menu);
        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorTextSecondary), PorterDuff.Mode.SRC_ATOP);
            }
        }


        final MenuItem item = menu.findItem(R.id.action_search);
        // get the reference of searchView widget and set onQueryTextListener on it .
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // call filter method and get the return filtered list, this which in-turn passed
        // as a argument while make call to setDoctorsFilter of DoctorsListAdapter.
        final ArrayList<OrderItem> filteredDoctorList = filter(orderItems, newText);
        listViewRekapOrderAdapter.setNewsFilter(filteredDoctorList);
        return true;
    }

    /**
     * Filtered the list of news and return the filtered list.
     *
     * @param orderArrayList the actually list of news.
     * @param query          the new content of the query text field.
     * @return
     */
    private ArrayList<OrderItem> filter(ArrayList<OrderItem> orderArrayList, String query) {
        query = query.toLowerCase();

        final ArrayList<OrderItem> filteredDoctorList = new ArrayList<>();
        for (OrderItem order : orderArrayList) {
            final String text = order.getNama().toLowerCase();
            if (text.contains(query)) {
                filteredDoctorList.add(order);
            }else if (order.contains(query)) {
                filteredDoctorList.add(order);
            }
        }
        return filteredDoctorList;
    }

    @Override
    public void onItemClick(View view, OrderItem obj, int position) {
        // Send the event to the main activity to Determine the Current Layout and take action.
        view.setTag("position:"+position);
        listener.onItemSelected(obj, view);
    }

    ArrayList<OrderItem> orderList;

    public void getOrderList(final SwipeRefreshLayout swipe) {
        orderList = new ArrayList<OrderItem>();

        ArrayList<OrderItem> pendingData = (ArrayList)SQLite.select().from(OrderItem.class)
                .where(OrderItem_Table.status.in("pending_janji","pending_bayar"))
                .orderBy(OrderBy.fromString(" status ASC, kodepos ASC")).queryList();

        String nopsb ="";

        for(int x= 0 ;x < pendingData.size();x++){

            nopsb = nopsb+pendingData.get(x).getNoPsb()+",";
        }
        String versi = LoginActivity.getVersionCode(getActivity());
        if (nopsb.length()>0) {
            nopsb = nopsb.substring(0, nopsb.length() - 1);
        }
        APIInterfacesRest apiInterface = APIClient.getClient().create(APIInterfacesRest.class);
        Call<Order> call3;
        if (AppController.getMode().equalsIgnoreCase("new")) {
            call3 = apiInterface.getOrder(AppController.getImei(), AppController.getToken(),"0", "status", 0, "100",AppController.getUsername(),nopsb, versi);
        } else {
            if(AppController.getRekap().equalsIgnoreCase("bayar")){
                call3 = apiInterface.getOrder(AppController.getImei(), AppController.getToken(),"2", "status", 0, "100",AppController.getUsername(),nopsb, versi);
            }else{
                call3 = apiInterface.getOrder(AppController.getImei(), AppController.getToken(),"1, 0", "status", 0, "100",AppController.getUsername(),nopsb, versi);
            }
          /*  call3 = apiInterface.getOrder(AppController.getImei(), AppController.getToken(),"2, 1, 0", "status", 0, "100", AppController.getUsername(),nopsb);
             */
        }
        call3.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                Order order = response.body();

                if (swipe != null) {
                    swipe.setRefreshing(false);
                }

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        //  Toast.makeText(LoginActivity.this,userList.getToken().toString(),Toast.LENGTH_LONG).show();
                        orderItems = (ArrayList<OrderItem>) order.getDataOrder().getOrderItem();


                        deleteDbUnused();



                        /*listViewRekapOrderAdapter = new ListViewRekapOrderAdapter(getActivity().getApplicationContext(), orderItems);
                        recyclerView.setAdapter(listViewRekapOrderAdapter);*/
                        //listViewRekapOrderAdapter.setOnItemClickListener(ListViewRekapBayarFragment.this);

                        return;
                    }
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        if (getActivity()!=null)
                            Toast.makeText(getActivity().getApplicationContext(), jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        if (getActivity()!=null)
                            Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

                /*if (AppController.getMode().equalsIgnoreCase("new")) {
                    orderLists = (ArrayList) SQLite.select().from(OrderItem.class)
                            .where(OrderItem_Table.status.is("0"))
                            .orderBy(OrderBy.fromString(" status ASC, kodepos ASC")).queryList();
                } else {
                    if (AppController.getRekap().equalsIgnoreCase("bayar")){
                        orderLists = (ArrayList) SQLite.select().from(OrderItem.class)
                                .where(OrderItem_Table.status.in("2", "pending_bayar"))
                                .orderBy(OrderBy.fromString(" status ASC, kodepos ASC")).queryList();
                    }else{
                        orderLists = (ArrayList) SQLite.select().from(OrderItem.class)
                                .where(OrderItem_Table.status.in("1","0", "pending_janji"))
                                .orderBy(OrderBy.fromString(" status ASC, kodepos ASC")).queryList();
                    }
                }
                if (orderLists != null && orderLists.size() > 0) {
                    listViewRekapOrderAdapter = new ListViewRekapOrderAdapter(getActivity().getApplicationContext(), orderLists);
                    recyclerView.setAdapter(listViewRekapOrderAdapter);
                    //listViewRekapOrderAdapter.setOnItemClickListener(ListViewRekapBayarFragment.this);
                }*/
                calculate();
                LoginActivity.autoLogout(getActivity(),response);
            }


            ArrayList<OrderItem> orderLists;

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                if (swipe != null) {
                    swipe.setRefreshing(false);
                }
                if (getActivity()!=null){
                    Toast.makeText(getActivity(), "Terjadi gangguan koneksi", Toast.LENGTH_LONG).show();
                }else{
                    return;
                }

                call.cancel();

                /*if (AppController.getMode().equalsIgnoreCase("new")) {
                    orderLists = (ArrayList) SQLite.select().from(OrderItem.class)
                            .where(OrderItem_Table.status.is("0"))
                            .orderBy(OrderBy.fromString(" status ASC, kodepos ASC")).queryList();
                } else {

                    if (AppController.getRekap().equalsIgnoreCase("bayar")){
                        orderLists = (ArrayList) SQLite.select().from(OrderItem.class)
                                .where(OrderItem_Table.status.in("2", "pending_bayar"))
                                .orderBy(OrderBy.fromString(" status ASC, kodepos ASC")).queryList();
                    }else{
                        orderLists = (ArrayList) SQLite.select().from(OrderItem.class)
                                .where(OrderItem_Table.status.in("1","0", "pending_janji"))
                                .orderBy(OrderBy.fromString(" status ASC, kodepos ASC")).queryList();
                    }

                }


                if (orderLists != null && orderLists.size() > 0) {
                    listViewRekapOrderAdapter = new ListViewRekapOrderAdapter(getActivity().getApplicationContext(), orderLists);
                    recyclerView.setAdapter(listViewRekapOrderAdapter);
                    //listViewRekapOrderAdapter.setOnItemClickListener(ListViewRekapBayarFragment.this);
                }*/

                calculate();


            }

        });


/*
        News news5 = new News();
        news5.setTitle("The 21st Century’s 100 greatest films");
        news5.setDescription(getString(R.string.long_dummy_text));
        news5.setImage("https://image.ibb.co/fYjzu5/news6.jpg");
        news5.setCategory("Culture");
        news5.setDate("5/8/2017");
        news5.setNumberOfLikes(145);
        news5.setNumberOfViewer(225);
        news5.setMain(true);

        newsList.add(news);
        newsList.add(news1);
        newsList.add(news2);
        newsList.add(news3);
        newsList.add(news4);
        newsList.add(news5);
        newsList.add(news);
        newsList.add(news1);
        newsList.add(news2);
        newsList.add(news3);
        newsList.add(news4);
        newsList.add(news5);
*/

    }

    private void viewX(){
        ArrayList<OrderItem> orderLists;
        if (AppController.getMode().equalsIgnoreCase("new")) {
            orderLists = (ArrayList) SQLite.select().from(OrderItem.class)
                    .where(OrderItem_Table.status.is("0"))
                    .orderBy(OrderBy.fromString(" status ASC, kodepos ASC")).queryList();
        } else {

            if (AppController.getRekap().equalsIgnoreCase("bayar")){
                orderLists = (ArrayList) SQLite.select().from(OrderItem.class)
                        .where(OrderItem_Table.status.in("2", "pending_bayar"))
                        .orderBy(OrderBy.fromString(" status ASC, kodepos ASC")).queryList();
            }else{
                orderLists = (ArrayList) SQLite.select().from(OrderItem.class)
                        .where(OrderItem_Table.status.in("1","0", "pending_janji"))
                        .orderBy(OrderBy.fromString(" status ASC, kodepos ASC")).queryList();
            }

        }

        if (orderLists != null && orderLists.size() > 0) {
            listViewRekapOrderAdapter = new ListViewRekapOrderAdapter(getActivity().getApplicationContext(), orderLists);
            recyclerView.setAdapter(listViewRekapOrderAdapter);
        }
    }

    public void trimX(){
        for (int i = 0; i < orderItems.size(); i++) {
            orderItems.get(i).trimx();
        }
    }
    public void savedb(){
        trimX();
        FlowManager.getDatabase(AppController.class)
                .beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                        new ProcessModelTransaction.ProcessModel<OrderItem>() {
                            @Override
                            public void processModel(OrderItem orderItem, DatabaseWrapper wrapper) {

                                orderItem.save();

                            }

                        }).addAll(orderItems).build())  // add elements (can also handle multiple)
                .error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {
                        //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .success(new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {




                        calculate();
                        //Toast.makeText(getActivity(), "Data Tersimpan", Toast.LENGTH_LONG).show();
                    }
                }).build().execute();


    }

    public void deleteDbUnused(){

        ArrayList<OrderItem> orderDelete;
        if (AppController.getMode().equalsIgnoreCase("new")) {
            orderDelete   = (ArrayList)SQLite.select().from(OrderItem.class)
                    .where(OrderItem_Table.status.in( "0"))
                    .queryList();
        } else {
            if(AppController.getRekap().equalsIgnoreCase("bayar")){
                orderDelete   = (ArrayList)SQLite.select().from(OrderItem.class)
                        .where(OrderItem_Table.status.in( "2"))
                        .queryList();
            }else{
                orderDelete   = (ArrayList)SQLite.select().from(OrderItem.class)
                        .where(OrderItem_Table.status.in( "1","0"))
                        .queryList();
            }
            /*orderDelete   = (ArrayList)SQLite.select().from(OrderItem.class)
                    .where(OrderItem_Table.status.in( "2", "1", "0"))
                    .queryList();*/
        }


        FlowManager.getDatabase(AppController.class)
                .beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                        new ProcessModelTransaction.ProcessModel<OrderItem>() {
                            @Override
                            public void processModel(OrderItem orderItem, DatabaseWrapper wrapper) {

                                orderItem.delete();


                            }

                        }).addAll(orderDelete).build())  // add elements (can also handle multiple)
                .error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {

                    }
                })
                .success(new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {
                        savedb();
                    }
                }).build().execute();




    }

    public void calculate() {

        viewX();

        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<OrderItem> rekapData =null;

        if (AppController.getRekap().equalsIgnoreCase("bayar")){
            rekapData = (ArrayList) SQLite.select().from(OrderItem.class)
                    .where(OrderItem_Table.status.in("2", "pending_bayar"))
                    .queryList();
        }else{
            rekapData = (ArrayList) SQLite.select().from(OrderItem.class)
                    .where(OrderItem_Table.status.in("1","0", "pending_janji"))
                    .queryList();
        }


        int jumlah = 0;int kons = 0;
        for (int x = 0; x < rekapData.size(); x++) {
            if(AppController.getRekap().equalsIgnoreCase("bayar")){
                jumlah += AppUtil.getIntCut(rekapData.get(x).getJmlTotTerbayar());
            }else{
                jumlah += AppUtil.getIntCut(rekapData.get(x).getJmlAngsuran());
            }


        }
        kons = kons + rekapData.size();
        stringBuilder.append ( "Bayar : ");
        stringBuilder.append(rekapData.size());
        stringBuilder.append(" Kons");
        stringBuilder.append("\r\n");
        txtJumlahBayar.setText("Rp." + AppUtil.formatCurrency(jumlah));


        rekapData = (ArrayList) SQLite.select().from(OrderItem.class)
                .where(OrderItem_Table.status.in( "0"  ))
                .queryList();
        stringBuilder.append ( "Blm ada Status : ");
        stringBuilder.append(rekapData.size());
        stringBuilder.append(" Kons");
        stringBuilder.append("\r\n");
        kons = kons + rekapData.size();

        rekapData = (ArrayList) SQLite.select().from(OrderItem.class)
                .where(OrderItem_Table.status.in( "1", "pending_janji"))
                .queryList();
        stringBuilder.append ( "Dgn Status : ");
        stringBuilder.append(rekapData.size());
        stringBuilder.append(" Kons");
        kons = kons + rekapData.size();
        txtLabel2.setText(stringBuilder.toString());


        txtLabel3.setText("Total : "+kons+" Kons");
    }


    //bluetooth

    private void printIT(int i) {
        final StringBuffer sbuff = new StringBuffer("");

        //sbuff.append("  Koperasi Anugrah Mega Mandiri \n");
        if(AppController.getRekap().equalsIgnoreCase("bayar")) {
            sbuff.append("    Rekap Setoran Kolektor \n".toUpperCase());
        }else{
            sbuff.append("    Buku harian Kolektor \n".toUpperCase());
        }



        sbuff.append("").append("\n");
        sbuff.append("").append("\n");
        sbuff.append("Collector Name").append(rightM("Date",10)).append("\n");
        sbuff.append(orderItems.get(0).getNamaAo()).append(rightM(AppUtil.Now().substring(0,10),18 )).append("\n");
        sbuff.append("").append("\n");
        sbuff.append("").append("\n");
        if(AppController.getRekap().equalsIgnoreCase("bayar")) {
            sbuff.append("Total terbayar : ").append(String.valueOf(orderItems.size())).append("\n");
        }else{
            sbuff.append("Belum tertagih : ").append(String.valueOf(orderItems.size())).append("\n");
        }
        sbuff.append("Total tagihan  : ").append(txtJumlahBayar.getText().toString()).append("\n");
        sbuff.append("").append("\n");
        sbuff.append("").append("\n");

        for(int x =0;x<orderItems.size();x++){

           /* sbuff.append(orderItems.get(x).getNama()).append(" ").append("\n");
            sbuff.append(orderItems.get(x).getNoPsb()).append(" ").append("\n");
            sbuff.append(orderItems.get(x).getAlamat1()).append("\n");
            sbuff.append(orderItems.get(x).getNopol()).append("\n");
            if(AppController.getRekap().equalsIgnoreCase("bayar")){
                sbuff.append("Jumlah Terbayar :").append("Rp.").append(AppUtil.formatCurrency(orderItems.get(x).getJmlTotTerbayar())).append("\n");
            }else{
                sbuff.append("Angsuran :").append("Rp.").append(AppUtil.formatCurrency(orderItems.get(x).getJmlAngsuran())).append("\n");
            }
            sbuff.append("").append("\n");*/

            if(AppController.getRekap().equalsIgnoreCase("bayar")){
                sbuff.append(String.valueOf(orderItems.get(x).getNama()).concat("          ").substring(0,10)).append(" ");
                sbuff.append(String.valueOf(orderItems.get(x).getNoPsb()).concat("         ").substring(0,8)).append(" ");

                String sX = "            "+ AppUtil.formatCurrency(orderItems.get(x).getJmlTotTerbayar());

                sbuff.append(  sX.substring(sX.length()-12,sX.length())  ).append("\n");

            }else{
                sbuff.append(String.valueOf(orderItems.get(x).getNama()).concat("          ").substring(0,10)).append(" ");
                sbuff.append(String.valueOf(orderItems.get(x).getNoPsb()).concat("         ").substring(0,8)).append(" ");

                String sX = String.valueOf(orderItems.get(x).getStatusBayar()).trim();
                if (sX.equalsIgnoreCase("")||sX.equalsIgnoreCase("null")){
                    sX = "ORDER BARU";
                }
                sX = "            "+sX;
                sbuff.append(  sX.substring(sX.length()-12,sX.length())  ).append("\n");

            }


        }
        sbuff.append("").append("\n").append("\n").append("\n");


        if (mService!=null  ){
            if (mSocket != null) {
               if (mSocket.isConnected()){
               }else{
                   newBT();
               }
            }else{
                newBT();
            }
        }else{
            newBT();
        }

        if (SelectedDevices.equals("")) {
            printerName = "sprt";
        }


        Messagebox.showProsesBar(getActivity(), new Messagebox.DoubleRunnable() {
            @Override
            public void run() {
                if (printerName.equals("sprt")) {
                    //printSprt("sprt", LogoAsset(getActivity().getApplicationContext(), imageName), sbuff.toString());
                    printSprt("sprt",null, sbuff.toString());

                } else {
                    printBT(SelectedBDAddress, GetLogoNoLogo(getActivity().getApplicationContext(), sbuff.toString()));
                }
            }
            @Override
            public void runUI() {

            }
        });



       /* if (closBT()) {


        }*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closBT();
    }

    public String rightM(String s) {
        return rightM(s, 18);
    }

    public String rightM(String s, int max) {
        s = "                     " + s;
        return s.substring(s.length() - max, s.length());
    }

    public static boolean isBluetoothAvailable(Context context) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled());
    }

    public void ShowMessageBox(final String message, final String title) {
        m_handler.post(new Runnable() {

            @Override
            public void run() {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View view = inflater.inflate(R.layout.messagebox, null);

                TextView textview = (TextView) view.findViewById(R.id.textmsg);

                textview.setText(message);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(title)
                        .setCancelable(false)
                        .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                                Intent intentOpenBluetoothSettings = new Intent();
                                intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                                startActivity(intentOpenBluetoothSettings);
                            }
                        });
                builder.setView(view);
                AlertDialog alert = builder.create();

                alert.show();

            }
        });
    }

    public void ShowMessageBox2(final String message, final String title) {
        m_handler.post(new Runnable() {

            @Override
            public void run() {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View view = inflater.inflate(R.layout.messagebox, null);

                TextView textview = (TextView) view.findViewById(R.id.textmsg);

                textview.setText(message);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(title)
                        .setCancelable(false)
                        .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });
                builder.setView(view);
                AlertDialog alert = builder.create();

                alert.show();

            }
        });
    }

    private String dataPrint = "";
    private byte[] dataPrintb = null;
    public BluetoothService mService = null;
    public BluetoothSocket mSocket;
    public OutputStream mSocketOut = null;

    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;

    private Handler m_handler = new Handler(); // Main thread
    // Intent request codes


    private String SelectedBDAddress = "";
    private String SelectedDevices = "";
    private String curStatusMsg;

    private Hashtable<String, String> buf = new Hashtable<String, String>();
    private ArrayList<Hashtable<String,String>> buffernya ;
    private String status;
    private String rprint;

    public void newBT() {
        mService = new BluetoothService(getActivity(), getHandler()) {
            @Override
            public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
                mSocket = socket;

                super.connected(socket, device);
            }
        };
    }

    public boolean closBT() {
        try {
            if (mSocket != null) {
                mSocket.close();
                return true;
            }
        } catch (Exception e) {
            return true;
        }
        try {
            if (mSocketOut != null) {
                mSocketOut.close();
                return true;
            }
        } catch (Exception e) {
            return true;
        }
        return true;
    }

    public void printBT(String mac, String data) {
        dataPrint = data + "\n\n\n";
        try {
            if (mService.getState() == BluetoothService.STATE_CONNECTED) {
                if (!dataPrint.equals("")) {
                    mService.sendMessage(dataPrint, "GBK");
                }
                dataPrint = "";
            } else {
                mService.connect(mService.getDevByMac(mac));
            }
        } catch (Exception e) {
        }
    }

    public void printBT(String mac, byte[] data) {
        dataPrintb = data;
        try {
            if (mService.getState() == BluetoothService.STATE_CONNECTED) {
                if (mSocketOut == null) {
                    mSocketOut = mSocket.getOutputStream();
                }
                mSocketOut.write(dataPrintb);
                mSocketOut.flush();
                dataPrintb = null;
            } else {
                mService.connect(mService.getDevByMac(mac));
            }
        } catch (Exception e) {
        }
    }

    private BluetoothPrinter mPrinter;

    public void printSprt(String name, byte[] data, String text) {
        try {
            if (mPrinter != null) {
            } else {
                mPrinter = new BluetoothPrinter(name);
            }
            int ret = 0;
            if (mPrinter.isConnected()) {

            } else {
                mPrinter.setCurrentPrintType(BluetoothPrinter.PrinterType.TIII);
                ret = mPrinter.open();
            }

            if (ret == 0) {
                if (data != null) {
                    mPrinter.send(data);
                    mPrinter.setPrinter(4);
                }


                if (text != null) {
                    byte[] charLarge1 = {0x1d, 0x21, 0};
                    mPrinter.send(charLarge1);

                    mPrinter.send(text);
                    mPrinter.setPrinter(4);
                }

                //mPrinter.close();
            } else {
                //Toast.makeText(getContext(), "Connect Failed", Toast.LENGTH_SHORT);
            }
        } catch (Exception e) {
        }
    }

    public Handler getHandler() {
        return mHandler;
    }

    public BluetoothService getBluetoothService() {
        return mService;
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = new Intent();

            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            if (getActivity()!=null)
                            Toast.makeText(getActivity(), "Connect successful", Toast.LENGTH_SHORT).show();
                            try {
                                new Thread(new Runnable() {
                                    public void run() {
                                        try {


                                            if (!dataPrint.equals("")) {
                                                mService.sendMessage(dataPrint, "GBK");
                                            } else if (dataPrintb != null) {
                                                mSocketOut = mSocket.getOutputStream();
                                                mSocketOut.write(dataPrintb);
                                                mSocketOut.flush();
                                            }
                                            dataPrintb = null;
                                            dataPrint = "";
                                        } catch (Exception e) {
                                        }
                                    }
                                }).start();
                            } catch (Exception e) {
                            }

                            break;
                        case BluetoothService.STATE_CONNECTING:

                            break;
                        case BluetoothService.STATE_LISTEN:

                        case BluetoothService.STATE_NONE:

                            break;
                    }
                    break;
                case BluetoothService.MESSAGE_CONNECTION_LOST:
                    if (getActivity()!=null)

                    Toast.makeText(getActivity(), "Device connection was lost", Toast.LENGTH_SHORT).show();
                    //onActivityResult(12345,  00001, intent );
                    break;
                case BluetoothService.MESSAGE_UNABLE_CONNECT:
                    if (getActivity()!=null)
                    Toast.makeText(getActivity(), "Unable to connect device", Toast.LENGTH_SHORT).show();
                    //onActivityResult(12345,  00002, intent );
                    break;
            }
        }

    };



    public byte[] GetLogoNoLogo(Context form, String data) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Restore the line spacing to the default of 30 dots.
        out.write((char) 0x1B);
        out.write('2');
        //out.write((byte)32);

        try {
            out.write(data.getBytes());
        } catch (IOException e) {  }


        return out.toByteArray();
    }
    public byte[] GetLogo(Context form, String data) {
        String logo = "";

        try {

            InputStream is = form.getAssets().open("logo.jpg");//n.png
            Bitmap img0 = BitmapFactory.decodeStream(is);

            boolean[] dots = getBitmapMonoChromeWithScalePrinter(img0);


            ByteArrayOutputStream out = new ByteArrayOutputStream();


            int threshold = 127;

            int multiplier = 570; // this depends on your printer model. for Beiyang you should use 1000
            double scale = (double) (multiplier / (int) img0.getWidth());
            int iheight = (int) (img0.getHeight() * scale);
            int iwidth = (int) (img0.getWidth() * scale);


            int integer = iwidth;
            byte[] width = new byte[4];
            for (int i = 0; i < 4; i++) {
                width[i] = (byte) (integer >>> (i * 8));
            }
            byte[] widthmsb = new byte[]{
                    (byte) (integer >>> 24),
                    (byte) (integer >>> 16),
                    (byte) (integer >>> 8),
                    (byte) integer};


            int offset = 0;


            out.write((char) 0x1B);
            out.write('3');
            out.write((byte) 0);

            while (offset < iheight) {


                //raster error
                //out.write((char)0x1D);
                //out.write((char)0x76);
                // out.write((char)0x30);

                //bit image
                out.write((char) 0x1B);
                out.write('*');         // bit-image mode
                out.write((byte) 33);    // 24-dot double-density
                out.write(width[0]);  // width low byte
                out.write(width[1]);  // width high byte


                for (int x = 0; x < iwidth; ++x) {
                    for (int k = 0; k < 3; ++k) {
                        byte slice = 0;

                        for (int b = 0; b < 8; ++b) {
                            int y = (((offset / 8) + k) * 8) + b;
                            // Calculate the location of the pixel we want in the bit array.
                            // It'll be at (y * width) + x.
                            int i = (y * iwidth) + x;

                            // If the image is shorter than 24 dots, pad with zero.
                            boolean v = false;
                            if (i < dots.length) {
                                v = dots[i];
                            }
                            slice |= (byte) ((v ? 1 : 0) << (7 - b));
                        }

                        out.write(slice);
                    }
                }
                offset += 24;
                out.write((char) 0x0A);
            }


            //fort 12x24
            out.write((char) 0x1B);
            out.write((char) 0x4D);
            out.write((byte) 0);

            // Restore the line spacing to the default of 30 dots.
            out.write((char) 0x1B);
            out.write('2');
            //out.write((byte)32);

            out.write(data.getBytes());
            ;

            return out.toByteArray();
        } catch (Exception e) {
        }


        return new byte[0];
    }


    public boolean[] getBitmapMonoChromeWithScalePrinter(Bitmap img0) {
        int height = img0.getHeight();
        int width = img0.getWidth();


        int threshold = 127;

        int multiplier = 570; // this depends on your printer model. for Beiyang you should use 1000
        double scale = (double) (multiplier / (int) width);
        int xheight = (int) (height * scale);
        int xwidth = (int) (width * scale);
        int dimensions = xwidth * xheight;


        int index = 0;
        boolean[] dots = new boolean[dimensions];

        for (int y = 0; y < xheight; y++) {
            for (int x = 0; x < xwidth; x++) {
                int _x = (int) (x / scale);
                int _y = (int) (y / scale);
                int rgb = img0.getPixel(_x, _y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = (rgb & 0xFF);
                //int gray = (r + g + b) / 3;
                int luminance = (int) (r * 0.3 + g * 0.59 + b * 0.11);
                dots[index] = (luminance < threshold);
                index++;
            }
        }
        return dots;
    }


    public static byte[] Logo(Activity form, String imageName) {
        if (imageName.length() >= 3) {
            try {
                new File("/sdcard/logo.jpg").delete();
            } catch (Exception e) {
            }
            try {
                InputStream is = form.getAssets().open("logo.jpg");
                // Utility.copyFile(is, "/sdcard/logo.jpg");
            } catch (Exception e) {
            }
        }
        PrintGraphics pg = new PrintGraphics();
        pg.initCanvas(540);
        pg.initPaint();
        pg.drawImage(0, 0, "/sdcard/logo.jpg");
        pg.printPng();

        return pg.printDraw();
    }

    private String printerName = "";
    private String imageName = "";


    private String leftsubstring(String s, int max) {
        if (s.length() > max) {
            return s.substring(0, max);
        }
        return s;
    }

    private String rightCcurrency(String s, int max) {
        s = "                                          " + s;
        return s.substring(s.length() - max, s.length());
    }


    public static byte[] PrintImage(Activity form, String imageName) {

        try {
            InputStream is = form.getAssets().open("lg.png", AssetManager.ACCESS_BUFFER);
            Bitmap btm = BitmapFactory.decodeStream(is);
            PrintImage(btm);
        } catch (Exception e) {
        }
        return PrintImage(null);
    }

    public static byte[] PrintImage(Bitmap img0) {
        try {

            int width = img0.getWidth() / 8;
            int heigh = img0.getHeight();


            byte x[] = new byte[width * heigh + 8];


            x[0] = (byte) 29;
            x[1] = (byte) 118;
            x[2] = (byte) 48;
            x[3] = (byte) 0;
            x[4] = (byte) (width);
            x[5] = (byte) 0;
            x[6] = (byte) heigh;
            x[7] = (byte) 0;

            int w = img0.getWidth();
            int hi = img0.getHeight();
            int bytesOfWidth = w / 8 + (w % 8 != 0 ? 1 : 0);

            x[4] = (byte) (bytesOfWidth % 256);
            x[5] = (byte) (bytesOfWidth / 256);
            x[6] = (byte) (hi % 256);
            x[7] = (byte) (hi / 256);


            int ix = 7;
            for (int h = 0; h < heigh; h++) {

                for (int k = 0; k < width; k++) {
                    ix++;
	    		/* 267 */
                    int c0 = img0.getPixel(k * 8 + 0, h);
	    		/*     */
                    int p0;
	    		/* 270 */
                    if (c0 == -1)
	    		/* 271 */ p0 = 0;
	    		/*     */
                    else {
	    		/* 273 */
                        p0 = 1;
	    		/*     */
                    }
	    		/* 275 */
                    int c1 = img0.getPixel(k * 8 + 1, h);
	    		/*     */
                    int p1;
	    		/* 276 */
                    if (c1 == -1)
	    		/* 277 */ p1 = 0;
	    		/*     */
                    else {
	    		/* 279 */
                        p1 = 1;
	    		/*     */
                    }
	    		/* 281 */
                    int c2 = img0.getPixel(k * 8 + 2, h);
	    		/*     */
                    int p2;
	    		/* 282 */
                    if (c2 == -1)
	    		/* 283 */ p2 = 0;
	    		/*     */
                    else {
	    		/* 285 */
                        p2 = 1;
	    		/*     */
                    }
	    		/* 287 */
                    int c3 = img0.getPixel(k * 8 + 3, h);
	    		/*     */
                    int p3;
	    		/* 288 */
                    if (c3 == -1)
	    		/* 289 */ p3 = 0;
	    		/*     */
                    else {
	    		/* 291 */
                        p3 = 1;
	    		/*     */
                    }
	    		/* 293 */
                    int c4 = img0.getPixel(k * 8 + 4, h);
	    		/*     */
                    int p4;
	    		/* 294 */
                    if (c4 == -1)
	    		/* 295 */ p4 = 0;
	    		/*     */
                    else {
	    		/* 297 */
                        p4 = 1;
	    		/*     */
                    }
	    		/* 299 */
                    int c5 = img0.getPixel(k * 8 + 5, h);
	    		/*     */
                    int p5;
	    		/* 300 */
                    if (c5 == -1)
	    		/* 301 */ p5 = 0;
	    		/*     */
                    else {
	    		/* 303 */
                        p5 = 1;
	    		/*     */
                    }
	    		/* 305 */
                    int c6 = img0.getPixel(k * 8 + 6, h);
	    		/*     */
                    int p6;
	    		/* 306 */
                    if (c6 == -1)
	    		/* 307 */ p6 = 0;
	    		/*     */
                    else {
	    		/* 309 */
                        p6 = 1;
	    		/*     */
                    }
	    		/* 311 */
                    int c7 = img0.getPixel(k * 8 + 7, h);
	    		/*     */
                    int p7;
	    		/* 312 */
                    if (c7 == -1)
	    		/* 313 */ p7 = 0;
	    		/*     */
                    else {
	    		/* 315 */
                        p7 = 1;
	    		/*     */
                    }
	    		/* 317 */
                    int value = p0 * 128 + p1 * 64 + p2 * 32 + p3 * 16 + p4 * 8 +
	    		/* 318 */           p5 * 4 + p6 * 2 + p7;
	    		/* 319 */
                    x[ix] = ((byte) value);
	    		/*     */
                }


            }

            return x;
        } catch (Exception e) {
            return ("").getBytes();
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        closBT();

        if (mPrinter != null) {
            try {
                mPrinter.close();
            } catch (Exception e) {
            }
        }
        super.onDestroy();
    }


    public static byte[] LogoAsset(Context form, String imageName) {
        PrintGraphics pg = new PrintGraphics();
        pg.initCanvas(380);
        pg.initPaint();
        try {
            InputStream is = form.getAssets().open("logo.jpg", AssetManager.ACCESS_BUFFER);
            pg.drawImage(0, 0, is);
        } catch (Exception e) {
        }
        //return pg.printDraw();
        return new byte[0];
    }

    private String getdata(String name) {
        String s = buf.get(name);
        return (s != null ? s : "");
    }

    public void printData() {


        printIT(0);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // pastikan close terlebih dahulu


                    SelectedBDAddress = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    SelectedDevices = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_NAME);

                    if(SelectedBDAddress!=null || !SelectedBDAddress.equalsIgnoreCase("")){

                        btnPrint.setEnabled(true);
                        btnScan.setText("Paired to:"+SelectedBDAddress);
                    }else{
                        btnPrint.setEnabled(false);
                        btnScan.setText("Scan");
                    }


                    return;
                }else{
                    btnPrint.setEnabled(false);
                    btnScan.setText("Scan");
                }



                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // connectDevice(data, false);
                }
                break;

            default :
                btnPrint.setEnabled(false);
                btnScan.setText("Scan");
                break;
        }





    }



}