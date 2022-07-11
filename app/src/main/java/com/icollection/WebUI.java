package com.icollection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.icollection.fragment.ListViewNewOrderFragment;
import com.icollection.fragment.ListViewRekapBayarFragment;
import com.icollection.util.Utility;

import java.io.File;

public class WebUI extends AppCompatActivity {
    private View view;
    private Bitmap bitBitmap = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    private WebView myWebView;
    private boolean perror = false;
    private ProgressBar progressBar ;
    public Uri imageUri;
    private static final int FILECHOOSER_RESULTCODE   = 2888;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web);
        view = findViewById(R.id.mparent);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        progressBar = (ProgressBar) view.findViewById(R.id.pPro);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setTitle(getIntentStringExtra("title"));
        //setTitleUp();
        ColorStateList iconsColorStates = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked},
                        new int[]{android.R.attr.state_checked}
                },
                new int[]{
                        Color.parseColor("#ff0000ff"),
                        Color.parseColor("#ff0000ff")
                });

        ColorStateList textColorStates = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked},
                        new int[]{android.R.attr.state_checked}
                },
                new int[]{
                        Color.parseColor("#ff0000ff"),
                        Color.parseColor("#ff0000ff")
                });

        BottomNavigationView navigation   = (BottomNavigationView) findViewById(R.id.navigation1);
        /*navigation = new BottomNavigationView(this);
        navigation.getMenu().clear();
        navigation.inflateMenu(R.menu.navigation);
        ((FrameLayout)findViewById(R.id.nav)).addView(navigation);*/
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
         navigation.setItemIconTintList(iconsColorStates);
        navigation.setItemTextColor(textColorStates);

        /*((SwipeRefreshLayoutX)swipeRefreshLayout).setCanChildScrollUpCallback(new SwipeRefreshLayoutX.CanChildScrollUpCallback() {
            @Override
            public boolean canSwipeRefreshChildScrollUp() {
                //true == > swipeRefreshLayout.setEnabled(false);
                //false == > swipeRefreshLayout.setEnabled(true);
                return myWebView.getScrollY() > 0;
            }
        });*/
        myWebView = (WebView) view.findViewById(R.id.web);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);//LOAD_NO_CACHE
        myWebView.addJavascriptInterface(new WebAppInterface(this), "Tomcat");
        myWebView.setWebViewClient(new AppWebViewClients(swipeRefreshLayout));

        myWebView.getSettings().setAllowFileAccess(true);
        myWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100){
                    progressBar.setVisibility(View.GONE);
                }else{
                    progressBar.setProgress(newProgress);
                }

            }

            // Android 5.0.1
            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    FileChooserParams fileChooserParams) {


                if (Build.VERSION.SDK_INT >= 21) {
                    String[] acceptTypes = fileChooserParams.getAcceptTypes();

                    String acceptType = "";
                    for (int i = 0; i < acceptTypes.length; ++ i) {
                        if (acceptTypes[i] != null && acceptTypes[i].length() != 0)
                            acceptType += acceptTypes[i] + ";";
                    }
                    if (acceptType.length() == 0)
                        acceptType = "*/*";

                    final ValueCallback<Uri[]> finalFilePathCallback = filePathCallback;

                    ValueCallback<Uri> vc = new ValueCallback<Uri>() {

                        @Override
                        public void onReceiveValue(Uri value) {

                            Uri[] result;
                            if (value != null)
                                result = new Uri[]{value};
                            else
                                result = null;

                            finalFilePathCallback.onReceiveValue(result);

                        }
                    };

                    openFileChooser(vc, acceptType, "filesystem");


                    return true;
                }else{
                    return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
                }

            }

            // openFileChooser for Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType){
                // Update message
                mUploadMessage = uploadMsg;
                try{
                    // Create AndroidExampleFolder at sdcard
                    File imageStorageDir = new File( Environment.getExternalStoragePublicDirectory(  Environment.DIRECTORY_PICTURES) , "AndroidExampleFolder");
                    if (!imageStorageDir.exists()) {
                        // Create AndroidExampleFolder at sdcard
                        imageStorageDir.mkdirs();
                    }
                    // Create camera captured image file path and name
                    File file = new File(imageStorageDir + File.separator + "IMG_"   + String.valueOf(System.currentTimeMillis())  + ".jpg");
                    mCapturedImageURI = Uri.fromFile(file);
                    // Camera capture image intent
                    final Intent captureIntent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE);
                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("image/*");
                    // Create file chooser intent
                    Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
                    // Set camera intent to file chooser
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS , new Parcelable[] { captureIntent });
                    // On select image call onActivityResult method of activity
                    startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
                }
                catch(Exception e){
                    Toast.makeText(getBaseContext(), "Exception:"+e,Toast.LENGTH_LONG).show();
                }

            }
            // openFileChooser for Android < 3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsg){
                openFileChooser(uploadMsg, "");
            }
            //openFileChooser for other Android versions
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooser(uploadMsg, acceptType);
            }
            public boolean onConsoleMessage(ConsoleMessage cm) {
                onConsoleMessage(cm.message(), cm.lineNumber(), cm.sourceId());
                return true;
            }
            public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                //Log.d("androidruntime", "Show console messages, Used for debugging: " + message);
            }
            public void onPermissionRequest(final PermissionRequest request) {
                int t=0;
                if (Build.VERSION.SDK_INT >= 21) {
                    request.grant(request.getResources());
                } else {

                }
                //L.d("onPermissionRequest");
                //request.grant(request.getResources());
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()  {
                //swipeRefreshLayout.setRefreshing(false);
                myWebView.reload();
            }
        });

        progressBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setEnabled(false);

        if (getIntentStringExtra("content").equalsIgnoreCase("hide")){
            myWebView.setVisibility(View.INVISIBLE);
        }else{
            myWebView.loadUrl(getIntentStringExtra("url"));
        }
    }


    public void onBackPressed() {
        if (myWebView.canGoBack()){
            myWebView.goBack();
        }else{
            super.onBackPressed();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_BARCODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String textBarcode = data.getStringExtra("TEXT");
                //myWebView.evaluateJavascript("javascript:onbarcode('"+ Utility.escapeSQL(textBarcode)+"');", null);
                String js = "javascript:onbarcode('"+  (textBarcode)+ "')";
                if (Build.VERSION.SDK_INT >= 19) {
                    myWebView.evaluateJavascript(js, null);
                } else {
                    myWebView.loadUrl(js);
                }
            }
        }
        if(requestCode==FILECHOOSER_RESULTCODE)  {
            if (null == this.mUploadMessage) {
                return;
            }
            Uri result=null;
            try{
                if (resultCode != RESULT_OK) {
                    result = null;
                } else {
                    // retrieve from the private variable if the intent is null
                    result = data == null ? mCapturedImageURI : data.getData();
                }
            }catch(Exception e){
                Toast.makeText(getApplicationContext(), "activity :"+e, Toast.LENGTH_LONG).show();
            }

            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;

        }
    }
    private void evaluateJavaScript(String javascript) {
        if (myWebView != null) {
            String js = "javascript:" + javascript;
            if (Build.VERSION.SDK_INT >= 19) {
                myWebView.evaluateJavascript(js, null);
            } else {
                myWebView.loadUrl(js);
            }
        }
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navbayar:
                    try {
                        String latlon =  getIntentStringExtra("pbayar");

                        String enclatlon = Utility.urlEncode(latlon);
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + enclatlon + "&mode=m");

                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return true;
                case R.id.navalamat:
                    try {
                        String latlon =  getIntentStringExtra("palamat");

                        String enclatlon = Utility.urlEncode(latlon);
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + enclatlon + "&mode=m");

                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return true;
                case R.id.navsurvey:
                    try {
                        String latlon =  getIntentStringExtra("psurvey");

                        String enclatlon = Utility.urlEncode(latlon);
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + enclatlon + "&mode=m");

                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return true;
            }
            return false;
        }
    };
    private static final int   REQUEST_BARCODE = 12;
    public String getIntentStringExtra(String key) {
        return getIntentStringExtra(getIntent(), key);
    }
    public String getIntentStringExtra(Intent intent, String key) {
        if (intent!=null && intent.getStringExtra(key)!=null){
            return intent.getStringExtra(key);
        }
        return "";
    }
    @Override
    protected void onStart() {
        super.onStart();
        //myWebView.getViewTreeObserver().addOnScrollChangedListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //myWebView.getViewTreeObserver().removeOnScrollChangedListener(this);
    }

    public void onScrollChanged() {
        if (myWebView.getScrollY() == 0) {
            swipeRefreshLayout.setEnabled(true);
        } else {
            swipeRefreshLayout.setEnabled(false);
        }
    }

    public class WebAppInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }


        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }
        @JavascriptInterface
        public void loadUrl(String url) {
            myWebView.loadUrl(url);
        }

        @JavascriptInterface
        public void scanbarcode() {
            Log.i("scanbarcode",Thread.currentThread().getName());
           // Intent intent = new Intent(WebActivity.this, BarcodeActivity.class);
            //startActivityForResult(intent, REQUEST_BARCODE);
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
            progressBar.setVisibility(View.VISIBLE);
            //progressBar.setProgress(0);
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
