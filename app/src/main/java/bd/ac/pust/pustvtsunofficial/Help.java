package bd.ac.pust.pustvtsunofficial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

public class Help extends AppCompatActivity {
    WebView web;
    private final String url = "https://www.pust.ac.bd/academic/departments/dept_teachers/D01";
    ConstraintLayout errorLayout;
    Handler mhErrorLayoutHide = null;

    boolean errorOccured = false;
    boolean reloadPressed = false;
    Button retry,goBack;
    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        web = findViewById(R.id.webView);
        errorLayout = findViewById(R.id.cl_error_layout);
        retry = findViewById(R.id.btn_retry);
        goBack = findViewById(R.id.btn_return);
        loading = findViewById(R.id.pd_loding_page);
        loading.setVisibility(View.GONE);

        mhErrorLayoutHide = new Handler(){
            @Override
            public void handleMessage(@NonNull final Message msg) {
                errorLayout.setVisibility(View.GONE);
                super.handleMessage(msg);
            }
        };

        web.setWebViewClient(new MyWebViewClient());
        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        web.setWebChromeClient(getChromeClient());
        web.loadUrl(url);

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading.setVisibility(View.VISIBLE);
                if(reloadPressed&&errorOccured==false){
                    hideErrorLayout();
                }
                reloadPressed = true;
                web.reload();
                errorOccured = false;
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(Help.this,BusLocatorActivity.class);
                finish();
                //startActivity(intent);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if(web.canGoBack()){
            web.goBack();
            return;
        }else{
            finish();
        }
        super.onBackPressed();
    }

    class MyWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onLoadResource(final WebView view, final String url) {
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageFinished(final WebView view, final String url) {
            if(errorOccured == false && reloadPressed){
                hideErrorLayout();
                reloadPressed = false;
            }
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(final WebView view, final WebResourceRequest request,
                                    final WebResourceError error) {
            errorOccured = true;
            showErrorLayout();
            super.onReceivedError(view, request, error);
        }
    }

    private void showErrorLayout(){
        errorLayout.setVisibility(View.VISIBLE);
    }

    private void hideErrorLayout(){
        mhErrorLayoutHide.sendEmptyMessageDelayed(10000,200);
    }

    private WebChromeClient getChromeClient(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);

        return new WebChromeClient(){
            @Override
            public void onProgressChanged(final WebView view, final int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
        };
    }
}