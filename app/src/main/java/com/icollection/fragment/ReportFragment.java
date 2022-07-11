package com.icollection.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.icollection.R;
import com.icollection.util.AppUtil;
import com.icollection.util.Utility;

/**
 * Created by am on 1/30/18.
 */

public class ReportFragment extends Fragment {
    private View view;
    private Bitmap bitBitmap = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    private WebView myWebView;
    private boolean perror = false;

    public boolean onBackPressed() {
        if (myWebView.canGoBack()){
            myWebView.goBack();
            return true;
        }
        return false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.point, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);


        myWebView = (WebView) view.findViewById(R.id.web);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.addJavascriptInterface(new WebAppInterface(getActivity()), "MyExact");
        myWebView.setWebViewClient(new AppWebViewClients(swipeRefreshLayout));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()  {
                //swipeRefreshLayout.setRefreshing(false);
                myWebView.reload();
            }
        });
        //swipeRefreshLayout.setRefreshing(true);
        //myWebView.loadUrl(AppUtil.BASE_URL + "report?u="+ Utility.getSetting(getActivity(), Utility.MD5("US"), ""));


        if (getArguments()!=null ){
            String title = String.valueOf(getArguments().getString("TITLE")).toLowerCase();
            if (title.contains("anda")){
                myWebView.loadUrl("http://mobilekamm.ddns.net:8080/report/untukanda.php?coll="+ Utility.getSetting(getActivity(), Utility.MD5("US"), ""));
                return view;
            }
        }

        myWebView.loadUrl(AppUtil.BASE_HOST + "mapkamm/imob/3?coll="+ Utility.getSetting(getActivity(), Utility.MD5("US"), ""));
        return view;
    }



    public class WebAppInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /** Show a toast from the web page */
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }



    }
    public class AppWebViewClients extends WebViewClient {
        private SwipeRefreshLayout swipeRefreshLayout;

        public AppWebViewClients(SwipeRefreshLayout swipeRefreshLayout) {
            this.swipeRefreshLayout=swipeRefreshLayout;
            //swipeRefreshLayout.setRefreshing(true);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            view.setVisibility(View.VISIBLE);
            return true;
        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Log.i("Web", description);//net::ERR_INTERNET_DISCONNECTED
            if (description.contains("net::ERR_INTERNET_DISCONNECTED")){
                perror = true;
            }
            perror = true;
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            //if (view.getTitle().contains("Web page not available"))//404 Not Found
            Log.i("Web", view.getTitle());
            if (view.getTitle().contains("Web page not available")){
                Toast.makeText(view.getContext(), "Periksa Jaringan Internet anda", Toast.LENGTH_SHORT).show();
                view.setVisibility(View.INVISIBLE);
            }else if (view.getTitle().contains("404 Not Found")) {
                view.setVisibility(View.INVISIBLE);
                Toast.makeText(view.getContext(), "Terjadi Kendala di Server", Toast.LENGTH_SHORT).show();
            }else if (perror){
                view.setVisibility(View.INVISIBLE);

            }else{
                view.setVisibility(View.VISIBLE);
            }
            if (swipeRefreshLayout!=null){
                swipeRefreshLayout.setRefreshing(false);
            }
            perror = false;
        }
    }


}
