package ai.agusibrahim.xhrlog;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.*;
import android.support.design.widget.NavigationView;
import android.os.Build;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.graphics.*;
import android.webkit.JavascriptInterface;
import android.widget.TextView;
import android.widget.EditText;
import android.view.inputmethod.EditorInfo;
import android.widget.ListView;
import java.util.*;
import android.widget.ArrayAdapter;
import android.webkit.WebResourceResponse;
import android.support.v7.widget.SearchView;
import android.widget.Toast;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.AdapterView;
import android.widget.*;
import android.support.design.widget.BottomSheetBehavior;
import android.webkit.WebChromeClient;
import java.util.concurrent.*;

public class MainActivity extends AppCompatActivity {
	private Toolbar toolbar;
	private TextView loggr;
	private EditText jsinput;
	private ListView netw;
	private NavigationView xhrslide, netlogslide;
	private List<String> netdata=new ArrayList<String>();
	private WebView web;
	private ArrayAdapter<String> adapter;
	private SearchView urlView;
	private DrawerLayout drawr;
	private MenuItem clearmenu;
	private View homs;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		netw=(ListView) findViewById(R.id.mainactivityListView1);
		jsinput=(EditText) findViewById(R.id.mainactivityEditText1);
		loggr=(TextView) findViewById(R.id.loggr);
		homs=findViewById(R.id.homs);
		drawr=(DrawerLayout) findViewById(R.id.drawer_layout);
		setSupportActionBar(toolbar);
		netw.setAdapter(adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, netdata));
		web=(WebView) findViewById(R.id.webv);
		xhrslide = (NavigationView) findViewById(R.id.naView);
		netlogslide = (NavigationView) findViewById(R.id.naView1);
		navigationinit();
		jsinput.setOnKeyListener(new View.OnKeyListener() {
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
						(keyCode == KeyEvent.KEYCODE_ENTER)) {
						String js=jsinput.getText().toString().replaceAll(";$","");
						web.loadUrl(String.format("javascript:%s%s%s",js.startsWith("console.")?"":"console.log(", js, js.startsWith("console.")?"":");"));
						
						jsinput.setText("");
						return true;
					}
					return false;
				}
			});
		webinit();
		// copy url saat listview di klik
		netw.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
				@Override
				public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4) {
					setClipboard((String)p1.getItemAtPosition(p3));
					Toast.makeText(MainActivity.this, "Copied to Clipboard",0).show();
					p1.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
					return true;
				}
			});
		// load url saat listview log di klik
		netw.setOnItemClickListener(new AdapterView.OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
					web.loadUrl((String)p1.getItemAtPosition(p3));
				}
			});
	}

	private void webinit() {
		// atur webview agar support js
		web.getSettings().setJavaScriptEnabled(true);
		// agar bisa di inspect melalui chrome di pc
		web.setWebContentsDebuggingEnabled(true);
		// register fungsi javascript
		web.addJavascriptInterface(new MyJavaScriptInterface(), "$$");
		// navigation callback
		web.setWebViewClient(new WebViewClient() {
				@Override
				public void onPageStarted(WebView view, String url, Bitmap favicon) {
					setTitle("Loading...");
					super.onPageStarted(view, url, favicon);
				}
				@Override
				public WebResourceResponse shouldInterceptRequest(WebView view, final String url) {
					// capture semua request ke listview
					view.post(new Runnable(){
							@Override
							public void run() {
								netdata.add(url);
								adapter.notifyDataSetChanged();
							}
						});
					return super.shouldInterceptRequest(view, url);
				}
				// saat halaman selesai di load, inject js
				@Override
				public void onPageFinished(WebView view, String url) {
					setTitle(view.getTitle());
					view.loadUrl("javascript:function injek3(){window.hasdir=1;window.dir=function(n){var r=[];for(var t in n)'function'==typeof n[t]&&r.push(t);return r}};if(window.hasdir!=1){injek3();}");
					view.loadUrl("javascript:function injek2(){window.touchblock=0,window.dummy1=1,document.addEventListener('click',function(n){if(1==window.touchblock){n.preventDefault();n.stopPropagation();var t=document.elementFromPoint(n.clientX,n.clientY);window.ganti=function(n){t.outerHTML=n},window.gantiparent=function(n){t.parentElement.outerHTML=n},$$.print(t.parentElement.outerHTML, t.outerHTML)}},!0)}1!=window.dummy1&&injek2();");
					view.loadUrl("javascript:function injek(){window.hasovrde=1;var e=XMLHttpRequest.prototype.open;XMLHttpRequest.prototype.open=function(ee,nn,aa){this.addEventListener('load',function(){$$.log(this.responseText, nn, JSON.stringify(arguments))}),e.apply(this,arguments)}};if(window.hasovrde!=1){injek();}");
					super.onPageFinished(view, url);
				}
			});
		web.setWebChromeClient(new WebChromeClient(){
			@Override
			public boolean onConsoleMessage(android.webkit.ConsoleMessage consoleMessage) {
				loggr.append(consoleMessage.message()+"\n--------------------\n");
				return false;
			}
		});
	}

	private void navigationinit() {
		// hilangkan bayangan hitam (semacam bug atau apa, yang pasti ini mengganggu)
		if (xhrslide != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
			xhrslide.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
					@Override
					public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
						return insets;
					}
				});
		}
		if (netlogslide != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
			netlogslide.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
					@Override
					public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
						return insets;
					}
				});
		}
		// drawer listener, untuk munculin menu clear log (trash icon)
		drawr.setDrawerListener(new DrawerLayout.DrawerListener(){
				@Override
				public void onDrawerSlide(View p1, float p2) {
					// TODO: Implement this method
				}
				@Override
				public void onDrawerOpened(View p1) {
					// saat drawer terbuka
					if(drawr.isDrawerOpen(Gravity.RIGHT))
						setTitle("XHR Logs");
					else if(drawr.isDrawerOpen(Gravity.LEFT))
						setTitle("Network Logs");
					clearmenu.setVisible(true);
				}
				@Override
				public void onDrawerClosed(View p1) {
					// saat drawer ditutup
					setTitle(web.getTitle());
					clearmenu.setVisible(false);
				}
				@Override
				public void onDrawerStateChanged(int p1) {
					// TODO: Implement this method
				}
			});
	}

	@Override
	public void onBackPressed() {
		// browser bisa di back, lakukan back. jika tidak maka lakukan back pada aplikasi (keluar)
		if(web.canGoBack()){
			web.goBack();
		}else{
			super.onBackPressed();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		// menu url input
		final MenuItem urlmenu=menu.findItem(R.id.goto_url);
		// menu clear (trash icon)
		clearmenu=menu.findItem(R.id.menu_clear);
		// sembunyikan menu clear
		clearmenu.setVisible(false);
		// menu input url sebagai SearchView
		urlView = (SearchView) urlmenu.getActionView();
		urlView.setQueryHint("Goto URL");
		// saat icon > (right chevron) di klik maka set url di url input sesuai url dari webview
		urlView.setOnSearchClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View p1) {
					urlView.setQuery( web.getUrl(), false);
				}
			});
		urlView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
				@Override
				// saat teken enter di input url
				public boolean onQueryTextSubmit(String urlinput) {
					// ada validasi url menggunakan regex, agar teks yang di input benar-benar url yang valid
					if(!urlinput.trim().matches("https?://.*")){
						urlinput="http://"+urlinput.trim();
					}
					if(android.util.Patterns.WEB_URL.matcher(urlinput).matches()){
						web.loadUrl(urlinput);
						MenuItemCompat.collapseActionView(urlmenu);
						homs.setVisibility(View.GONE);
						web.setVisibility(View.VISIBLE);
					}else Toast.makeText(MainActivity.this, "Invalid URL",0).show();
					return false;
				}
				@Override
				public boolean onQueryTextChange(String p1) {
					return false;
				}
			});
		return super.onCreateOptionsMenu(menu);
	}

	// menu click handler
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.menu_xhrlog:
				drawr.closeDrawer(Gravity.LEFT);
				drawr.openDrawer(Gravity.RIGHT);
				break;
			case R.id.menu_netlog:
				drawr.closeDrawer(Gravity.RIGHT);
				drawr.openDrawer(Gravity.LEFT);
				break;
			case R.id.menu_touchinspect:
				// saat menu Touch Inscpector di klik, maka inject js yang sudah diatur
				web.loadUrl("javascript:window.touchblock=!window.touchblock;setTimeout(function(){$$.blocktoggle(window.touchblock)}, 100);");
				break;
			case R.id.menu_exit:
				finish();
				break;
			case R.id.menu_clear:
				// konfirmasi sebelum clear log
				AlertDialog.Builder dlg=new AlertDialog.Builder(this);
				dlg.setTitle("Clear Confirm");
				dlg.setMessage("Clear logs?");
				dlg.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface p1, int p2) {
							if(drawr.isDrawerOpen(Gravity.RIGHT))
								loggr.setText("");
							else if(drawr.isDrawerOpen(Gravity.LEFT))
								netdata.clear();
							adapter.notifyDataSetChanged();
						}
					});
				dlg.setNegativeButton("No", null);
				dlg.show();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	// dialog source code view/edit
	private void showSourceDialog(final String s, final String r){
		View v=getLayoutInflater().inflate(R.layout.source, null);
		final EditText ed=(EditText)v.findViewById(R.id.sourceEditText1);
		ed.setText(r);
		ed.setTag(false);
		AlertDialog.Builder dl=new AlertDialog.Builder(this);
		dl.setTitle("Source");
		dl.setView(v);
		dl.setPositiveButton("Save", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface p1, int p2) {
					web.loadUrl("javascript:window.ganti"+((boolean)ed.getTag()?"parent":"")+"('"+ed.getText().toString()+"');");
				}
			});
		dl.setNeutralButton("Parent", null);
		dl.setNegativeButton("Close", null);
		AlertDialog dlg=dl.show();
		final Button prntBtn=dlg.getButton(AlertDialog.BUTTON_NEUTRAL);
		prntBtn.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View p1) {
					ed.setText((boolean)ed.getTag()?r:s);
					prntBtn.setText((boolean)ed.getTag()?"Perent":"Inner");
					ed.setTag(!(boolean)ed.getTag());
				}
			});
	}
	
	// js interface
	class MyJavaScriptInterface {
		@JavascriptInterface
		@SuppressWarnings("unused")
		public void log(final String content, final String url, final String arg) {
			web.post(new Runnable(){
					@Override
					public void run() {
						loggr.append(String.format("REQ: %s\nARG: %s\nRESP: %s\n--------------------\n",url,arg, content));
					}
				});
		}
		@JavascriptInterface
		@SuppressWarnings("unused")
		public void print(final String contentparent, final String content) {
			web.post(new Runnable(){
					@Override
					public void run() {
						showSourceDialog(contentparent, content);
					}
				});
		}
		@JavascriptInterface
		@SuppressWarnings("unused")
		public void blocktoggle(final String val){
			web.post(new Runnable(){
					@Override
					public void run() {
						Toast.makeText(MainActivity.this, val.matches("(1|true)")?"Touch Inspector Activated":"Touch Inspector Deactivated",1).show();
					}
				});

		}
	}
	
	// fungsi set clipboard utk semua versi API
	private void setClipboard(String text) {
		if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			clipboard.setText(text);
		} else {
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
			clipboard.setPrimaryClip(clip);
		}
	}
}
/*
function injek2() {
    window.dummy1 = 1;
    document.addEventListener('click', function(e) {
        if (1 == window.touchblock) {
            e.preventDefault();
            var t = document.elementFromPoint(e.clientX, e.clientY);
			window.ganti=function(s){
				t.outerHTML=s;
			}
            $$.print(t.outerHTML)
        }
    }, !0);
};
if (window.dummy1 != 1) {
    injek2();
}

function injek3() {
    window.hasdir = 1;
    window.dir = function(n) {
        var r = [];
        for (var t in n) 'function' == typeof n[t] && r.push(t);
        return r
    }
};
if (window.hasdir != 1) {
    injek3();
}
*/
