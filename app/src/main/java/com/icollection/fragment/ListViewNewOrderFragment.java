package com.icollection.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;



import com.icollection.APIInterfacesRest;
import com.icollection.AppController;
import com.icollection.BHCActivity;
import com.icollection.ListViewNewOrder;
import com.icollection.LoginActivity;
import com.icollection.R;
import com.icollection.adapter.ListViewNewOrderAdapter;
import com.icollection.modelservice.BHC;
import com.icollection.modelservice.Order;
import com.icollection.modelservice.OrderItem;
import com.icollection.modelservice.OrderItem_Table;
import com.icollection.tracker.GpsTrackerIntentService;
import com.icollection.util.APIClient;
import com.icollection.util.AppUtil;
import com.icollection.util.Messagebox;
import com.icollection.util.Utility;
import com.naa.data.Nson;
import com.naa.data.UtilityAndroid;
import com.naa.utils.InternetX;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.property.IProperty;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Mounzer on 8/23/2017.
 */

public class ListViewNewOrderFragment extends Fragment implements ListViewNewOrderAdapter.OnItemClickListener, SearchView.OnQueryTextListener, Runnable{
    private static final String LOG_TAG = ListViewNewOrderFragment.class.getSimpleName();
    public static final String ARG_ITEM_ID = "order_list";
    private static final String DATA_STATE = "data_state";
    public static final String ALL_ID_STATE = "state_all_id";
    RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    LinearLayoutManager layoutManager;
    private ListViewNewOrderAdapter listViewNewOrderAdapter;
    private ArrayList<OrderItem> orderItems;
    private OnItemSelectedListener listener;
    private Context context;

    @Override
    public void run() {
        getOrderList(swipeRefreshLayout);
    }


    /**
     * Callback used to communicate with ListViewNewOrderFragment to Determine the Current Layout.
     * ListViewFirstDesignActivity implements this interface and communicates with ListViewNewOrderFragment.
     */
    public interface OnItemSelectedListener {
        public void onItemSelected(OrderItem news, View view);
    }

    @Override
    public void onResume() {
        super.onResume();
        getOrderList(swipeRefreshLayout);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the user interface layout for this Fragment.
        View rootView = inflater.inflate(R.layout.fragment_listview_first_design, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);


        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String title = String.valueOf(bundle.getString("TITLE"));

        }

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        /*// save all data for orientation change.
        if (savedInstanceState != null) {
            // We will restore the state of data list when the activity is re-created
            orderItems = savedInstanceState.getParcelableArrayList(DATA_STATE);
        } else {
            // Create a list of news and populate it with hardcoded data.
            getOrderList(null);
        }*/

        getOrderList(swipeRefreshLayout);
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
        outState.putParcelableArrayList(DATA_STATE, orderItems);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_listview_first_design, menu);
        for(int i = 0; i < menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if(drawable != null) {
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
        listViewNewOrderAdapter.setNewsFilter(filteredDoctorList);
        return true;
    }

    /**
     * Filtered the list of news and return the filtered list.
     * @param orderArrayList the actually list of news.
     * @param query the new content of the query text field.
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
        listener.onItemSelected(obj, view);
    }
    Nson nbhc;
    ArrayList<OrderItem> orderList;
    public void getOrderList(final SwipeRefreshLayout swipe) {
        nbhc = Nson.readNson(UtilityAndroid.getSetting(getActivity(), "BHC","{}"));

        if (getActivity() instanceof  ListViewNewOrder){
            ((ListViewNewOrder)getActivity()).afterReload();
        }

        if (swipe!=null){
            swipe.setRefreshing(true);
        }
        if (AppController.getMode().equalsIgnoreCase("all")) {
            ArrayList<OrderItem> orderLists;
            if (swipe!=null){
                swipe.setRefreshing(false);
            }
            orderLists = (ArrayList)SQLite.select().from(OrderItem.class).orderBy(OrderBy.fromString(" status ASC, kodepos ASC")).queryList();
            if(orderLists == null ){
                orderLists = new ArrayList<OrderItem>();
            }
            orderItems = orderLists ;
            if(orderLists!=null ){//&& orderLists.size()>0
                listViewNewOrderAdapter = new ListViewNewOrderAdapter(getActivity(), orderLists,nbhc);
                recyclerView.setAdapter(listViewNewOrderAdapter);
                listViewNewOrderAdapter.setOnItemClickListener(ListViewNewOrderFragment.this);
            }
            return;
        }


        orderList   = new ArrayList<OrderItem>();
        ArrayList<OrderItem> pendingData = (ArrayList)SQLite.select().from(OrderItem.class)
                .where(OrderItem_Table.status.in("pending_janji","pending_bayar"))
                .queryList();

        String nopsb ="";
        for(int x= 0 ;x < pendingData.size();x++){
            nopsb = nopsb+pendingData.get(x).getNoPsb()+",";
        }
        if (nopsb.length()>0) {
            nopsb = nopsb.substring(0, nopsb.length() - 1);
        }
        String versi = LoginActivity.getVersionCode(getActivity());
        APIInterfacesRest apiInterface = APIClient.getClient().create(APIInterfacesRest.class);
        //Log.i("Kx",AppController.getImei(), AppController.getToken());
        Call<Order> call3;
        if (AppController.getMode().equalsIgnoreCase("new")) {
            call3 = apiInterface.getOrder(AppController.getImei(), AppController.getToken(),"0, 1, 2", "status", 0, "100",AppController.getUsername(),nopsb, versi);

            /*Call<BHC> callbhc = apiInterface.getBHC(AppController.getImei(), AppController.getToken() );
            callbhc.enqueue(new Callback<BHC>() {
                @Override
                public void onResponse(Call<BHC> call, Response<BHC> response) {
                    try {
                        BHC order = response.body();
                        if(response.isSuccessful()) {
                            if (response.body()!=null) {
                                 Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_LONG).show();
                            }
                        } else {   }
                    }catch (Exception e){}
                }


                @Override
                public void onFailure(Call<BHC> call, Throwable t) {
                     //nothing
                }

            });*/

            Messagebox.newTask(new Messagebox.DoubleRunnable() {
                String res;
                @Override
                public void run() {
                    Map<String, String> args = new HashMap<>();
                    args.put("otp",  "" );
                    res = (InternetX.postHttpConnection( AppUtil.BASE_URL + "apicoll/bhc_para/?key="+  AppController.getToken(), args));
                    //GpsTrackerIntentService.sendBHC(getActivity());//bypass
                }

                @Override
                public void runUI() {
                    Nson nson = Nson.readNson(res);
                    if (nson.get("status").asString().equalsIgnoreCase("true")){
                        //Toast.makeText(getActivity(), res, Toast.LENGTH_LONG).show();
                        Log.i("Exp_pa", res);
                        try {
                            /*FileOutputStream fileOutputStream = new FileOutputStream(Environment.getExternalStorageDirectory()
                                    + File.separator+"bhc");*/
                            FileOutputStream fileOutputStream = new FileOutputStream(new File(com.naa.data.Utility.getAppContext().getExternalFilesDir(null),   "bhc"));
                            nson.get("data").toJson(fileOutputStream);
                            fileOutputStream.close();
                        }catch (Exception e){}
                    }else{

                    }


                }
            });
        }else {
            call3 = apiInterface.getOrder(AppController.getImei(), AppController.getToken(),"1, 2", "status", 0, "100", AppController.getUsername(),nopsb, versi);
        }
        call3.enqueue(new Callback<Order>() {
                          @Override
                          public void onResponse(Call<Order> call, Response<Order> response) {
                              try {
                                  Order order = response.body();
                                  if(response.isSuccessful()) {
                                      if (response.body()!=null) {
                                          // Toast.makeText(LoginActivity.this,userList.getToken().toString(),Toast.LENGTH_LONG).show();
                                          orderItems = (ArrayList<OrderItem>) order.getDataOrder().getOrderItem();
                                          deleteDbUnused();

                                          //rubah dulu
                                          /*listViewNewOrderAdapter = new ListViewNewOrderAdapter(getActivity().getApplicationContext(), orderItems);
                                          recyclerView.setAdapter(listViewNewOrderAdapter);
                                          listViewNewOrderAdapter.setOnItemClickListener(ListViewNewOrderFragment.this);
                                          return;*/
                                          //return;
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
                                  if (AppController.getMode().equalsIgnoreCase("new")) {
                                      orderLists = (ArrayList)SQLite.select().from(OrderItem.class)
                                              .where(OrderItem_Table.status.in("0","1", "2"))
                                              .orderBy(OrderBy.fromString(" status ASC, kodepos ASC")).queryList();
                                  }else {
                                      orderLists = (ArrayList)SQLite.select().from(OrderItem.class)
                                              .where(OrderItem_Table.status.in("1","2","pending_janji","pending_bayar"))
                                              .orderBy(OrderBy.fromString(" status ASC, kodepos ASC")).queryList();
                                  }
                                  if(orderLists == null ){
                                      orderLists = new ArrayList<OrderItem>();
                                  }
                                  orderItems = orderLists ;
                                  xBHC(orderItems);
                                  if(orderLists!=null){// && orderLists.size()>0
                                      listViewNewOrderAdapter = new ListViewNewOrderAdapter(getActivity().getApplicationContext(), orderLists, nbhc);
                                      recyclerView.setAdapter(listViewNewOrderAdapter);
                                      listViewNewOrderAdapter.setOnItemClickListener(ListViewNewOrderFragment.this);
                                  }
                              }catch (Exception e){}

                              if (swipe!=null){
                                  swipe.setRefreshing(false);
                              }
                              LoginActivity.autoLogout(getActivity(),response);
                          }

                          ArrayList<OrderItem> orderLists;
                          @Override
                          public void onFailure(Call<Order> call, Throwable t) {
                              if (swipe!=null){
                                  swipe.setRefreshing(false);
                              }
                              if (getActivity()!=null){
                                  Toast.makeText(getActivity(),"Terjadi gangguan koneksi",Toast.LENGTH_LONG).show();
                              }else{
                                  return;
                              }
                              call.cancel();

                              if (AppController.getMode().equalsIgnoreCase("new")) {
                                   orderLists = (ArrayList)SQLite.select().from(OrderItem.class)
                                           .where(OrderItem_Table.status.in("0","1", "2"))
                                           .orderBy(OrderBy.fromString(" status ASC, kodepos ASC")).queryList();
                              }else {
                                  orderLists = (ArrayList)SQLite.select().from(OrderItem.class)
                                          .where(OrderItem_Table.status.in("1","2", "pending_janji", "pending_bayar"))
                                          .orderBy(OrderBy.fromString(" status ASC, kodepos ASC")).queryList();
                              }
                              if(orderLists == null ){
                                  orderLists = new ArrayList<OrderItem>();
                              }
                              orderItems = orderLists ;
                              xBHC(orderItems);
                              if(orderLists!=null ){//&& orderLists.size()>0
                                  listViewNewOrderAdapter = new ListViewNewOrderAdapter(getActivity().getApplicationContext(), orderLists, nbhc);
                                  recyclerView.setAdapter(listViewNewOrderAdapter);
                                  listViewNewOrderAdapter.setOnItemClickListener(ListViewNewOrderFragment.this);
                              }


                          }

                      });

        /*Call<String> vaCall = APIClient.getPlainClient().create(APIInterfacesRest.class).getVA(AppController.getImei(), AppController.getToken(), AppController.getUsername() );//"Cgm099"
        vaCall.enqueue(new Callback<String> () {
            @Override
            public void onResponse(Call<String>  call, Response<String>  response) {
                try {
                    String s = String.valueOf(response.body());
                    UtilityAndroid.setSetting(getActivity(),"VA", s);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<String>  call, Throwable t) {
                t.printStackTrace();
            }
        });*/


        ArrayList<OrderItem> allData = (ArrayList)SQLite.select( ).from(OrderItem.class)
                .queryList();
        StringBuilder stringBuilder = new StringBuilder();
        for(int x= 0 ;x < allData.size();x++){
            if (x>=1){
                stringBuilder.append(",");
            }
            stringBuilder.append( String.valueOf(allData.get(x).getNoPsb()) );
        }
        allData.clear();
        Call<String> vaCall = APIClient.getPlainClient().create(APIInterfacesRest.class).getVAs(AppController.getImei(), AppController.getToken(), stringBuilder.toString());//"Cgm099"
        vaCall.enqueue(new Callback<String> () {
            @Override
            public void onResponse(Call<String>  call, Response<String>  response) {
                try {
                    String s = String.valueOf(response.body());
                    UtilityAndroid.setSetting(getActivity(),"VA", s);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<String>  call, Throwable t) {
                t.printStackTrace();
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
    public static boolean existBHC(Nson nbhc, OrderItem order){
        /*if (nbhc.get("data").containsKey(order.getNoPsb() + BHCActivity.getStatusBHC(order.getStatusBayar()))) {
            return true;
        }
        return false;*/
        return BHCActivity.existBHC(nbhc,order);
    }
    public void xBHC(ArrayList<OrderItem> orderItems){
        if (AppController.getMode().equalsIgnoreCase("new")) {
            Nson nbhc = Nson.readNson(UtilityAndroid.getSetting(getActivity(), "BHC","{}"));
            int l  = orderItems.size();
            for (int i = 0; i < l; i++) {
                int pos = l - i - 1;//orderItems.size() - i - 1;
                if (pos < orderItems.size()) {
                    if (orderItems.get(pos).getStatus().equalsIgnoreCase("2")) {
                        if (existBHC(nbhc, orderItems.get(pos))) {
                            orderItems.remove(pos);
                        }
                    } else if (orderItems.get(pos).getStatus().equalsIgnoreCase("pending_janji")) {
                        if (existBHC(nbhc, orderItems.get(pos))) {
                            orderItems.remove(pos);
                        }
                    } else if (orderItems.get(pos).getStatus().equalsIgnoreCase("pending_bayar")) {
                        if (existBHC(nbhc, orderItems.get(pos))) {
                            orderItems.remove(pos);
                        }
                    }
                }
            }
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
                .executeTransaction(new ProcessModelTransaction.Builder<>(
                        new ProcessModelTransaction.ProcessModel<OrderItem>() {
                            public void processModel(OrderItem orderItem, DatabaseWrapper wrapper) {
                                orderItem.save();
                            }
                        }).addAll(orderItems).build())  ;
        /*FlowManager.getDatabase(AppController.class)
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
                        Log.i("onSuccess", Thread.currentThread().getName());

                       // Toast.makeText(getActivity().getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                })
                .success(new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {
                        Log.i("onSuccess", Thread.currentThread().getName());
                        //Toast.makeText(getActivity(),"Data Tersimpan",Toast.LENGTH_SHORT).show();
                        if (AppController.getMode().equalsIgnoreCase("new")) {
                            orderItems = (ArrayList)SQLite.select().from(OrderItem.class)
                                    .where(OrderItem_Table.status.in("0","1"))
                                    .orderBy(OrderBy.fromString(" status ASC, kodepos ASC")).queryList();
                        }else {
                            orderItems = (ArrayList)SQLite.select().from(OrderItem.class)
                                    .where(OrderItem_Table.status.in("1","2","pending_janji","pending_bayar"))
                                    .orderBy(OrderBy.fromString(" status ASC, kodepos ASC")).queryList();
                        }
                        if(orderItems == null ){
                            orderItems = new ArrayList<OrderItem>();
                        }

                        if(orderItems!=null){// && orderLists.size()>0
                            listViewNewOrderAdapter = new ListViewNewOrderAdapter(getActivity().getApplicationContext(), orderItems);
                            recyclerView.setAdapter(listViewNewOrderAdapter);
                            listViewNewOrderAdapter.setOnItemClickListener(ListViewNewOrderFragment.this);
                        }

                    }
                }).build().execute();*/


    }

    public void deleteDbUnused(){
        ArrayList<OrderItem> orderDelete;

        if (AppController.getMode().equalsIgnoreCase("new")) {
            orderDelete   = (ArrayList)SQLite.select().from(OrderItem.class)
                    .where(OrderItem_Table.status.in("1", "0","2"))//tambah
                    .queryList();
        }else{
            orderDelete   = (ArrayList)SQLite.select().from(OrderItem.class)
                    .where(OrderItem_Table.status.in("1","2" ))
                    .queryList();
        }
       /* FlowManager.getDatabase(AppController.class).beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                new ProcessModelTransaction.ProcessModel<OrderItem>() {
                    public void processModel(OrderItem orderItem1, DatabaseWrapper wrapper) {
                        orderItem1.delete();
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
                }).build().execute();*/
        FlowManager.getDatabase(AppController.class).executeTransaction(new ProcessModelTransaction.Builder<>(
                new ProcessModelTransaction.ProcessModel<OrderItem>() {
                    public void processModel(OrderItem orderItem1, DatabaseWrapper wrapper) {
                        orderItem1.delete();
                    }

                }).addAll(orderDelete).build()  );
                savedb();


    }

}
