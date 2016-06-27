package com.xamoom.android.xamoomcontentblocks.ViewHolders;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xamoom.android.xamoomsdk.R;
import com.xamoom.android.xamoomsdk.Resource.ContentBlock;

/**
 * Displays the content heading.
 */
public class ContentHeaderViewHolder extends RecyclerView.ViewHolder {
  private TextView mTitleTextView;
  private WebView mWebView;
  private String mLinkColor = "00F";

  public ContentHeaderViewHolder(View itemView) {
    super(itemView);
    mTitleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
    mWebView = (WebView) itemView.findViewById(R.id.webView);
    mWebView.setBackgroundColor(Color.TRANSPARENT);

    //override to open links in Webbrowser
    mWebView.setWebViewClient(new WebViewClient() {
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.getContext().startActivity(
            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        return true;
      }
    });
  }


  public void setupContentBlock(ContentBlock contentblock){
    mTitleTextView.setVisibility(View.VISIBLE);
    mWebView.setVisibility(View.VISIBLE);

    if(contentblock.getTitle() != null) {
      mTitleTextView.setText(contentblock.getTitle());
    } else {
      mTitleTextView.setVisibility(View.GONE);
      LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mWebView.getLayoutParams();
      params.setMargins(0,0,0,0);
      mWebView.setLayoutParams(params);
    }

    if((contentblock.getText() != null) && !(contentblock.getText().equalsIgnoreCase("<p><br></p>"))) {
      String style = "<style type=\"text/css\">html, body {margin: 0; padding: 0dp;} a {color: #"+mLinkColor+"}</style>";
      String htmlAsString = String.format("%s%s", style, contentblock.getText());
      mWebView.loadDataWithBaseURL(null, htmlAsString, "text/html", "UTF-8", null);
    } else {
      mWebView.setVisibility(View.GONE);
    }
  }

  public void setLinkColor(String mLinkColor) {
    this.mLinkColor = mLinkColor;
  }
}
