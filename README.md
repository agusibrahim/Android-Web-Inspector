## Android Web Inspector
![image](https://github.com/agusibrahim/Android-Web-Inspector/blob/master/art/JointPics_20170521_104538.PNG?raw=true)

<a href='https://play.google.com/store/apps/details?id=ai.agusibrahim.xhrlog&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png'/></a>

Terinspirasi dari Google Chrome DevTool, banyak faselitas Uji untuk pengembang yang disediakan. Di Android sendiri bisa kita lakukan inspeksi halaman dengan cara Remote melalui Wi-Fi atau melalui USB dengan ADB Protokol.
Namun inspeksi melalui remote tersebut tentu saja dilakukan di PC/Laptop, tidak bisa standalone.
Disini dijelaskan bagiaimana cara inspeksi halaman seperti Inspecting Element/DOM, XHR logger dan Network logger di WebView Android.

Android menyediakan fungsi Javascript Interface yang memungkinkan kita untuk membuat fungsi javascript melalui java, atau memanggil fungsi java dari javascript dan memanggil fungsi javascript melalui java. *ah ribet penjelasannya* 😂

Baiklah, berikut langkah-langkahnya:

### Membuat Callback
Pertama kita membuat Javascript Interface menggunakan Java, dimana saat fungsi JS itu dipanggil, maka akan mentrigger fungsi di Java, ini berguna untuk mewadahi data yang sudah didapat dari hasil intercept.
```java
class MyJavaScriptInterface{
		@JavascriptInterface
		@SuppressWarnings("unused")
		public void now(String content){
			android.util.Log.d("xhr", content);
		}
}
``` 
Tambahkan _MyJavaScriptInterface_ ke WebView
```java
webView.addJavascriptInterface(new MyJavaScriptInterface(), "callme");
``` 
Panggil dengan javascript
```javascript
callme.now("Hello Bro");
``` 
Atau membuat fungsi JS seperti jQuery yaitu simbol dolar, tapi kita memakai double dolar
```java
webView.addJavascriptInterface(new MyJavaScriptInterface(), "$$");
``` 
Panggil dengan javascript
```javascript
$$.now("Hello Bro");
``` 

### Overriding XMLHttpRequest
Yap, dengan melakukan override pada fungsi XHR ini kita dapat melempar data yang kita inginkan ke JS Interface yang sudah kita buat. Bagaimana cara override request dan response dari XHR? Saya mendapatkan caranya disini
* Add a "hook" to all AJAX requests on a page [[...]](http://stackoverflow.com/questions/5202296/add-a-hook-to-all-ajax-requests-on-a-page)
 http://stackoverflow.com/a/27363569

Inject JS untuk Override XHR setiap halaman selesai dimuat, seperti ini
```java
@Override
public void onPageFinished(WebView view, String url) {
    // xhr override
    view.loadUrl("javascript:function injek(){window.hasovrde=1;var e=XMLHttpRequest.prototype.open;XMLHttpRequest.prototype.open=function(ee,nn,aa){this.addEventListener('load',function(){$$.log(this.responseText, nn, JSON.stringify(arguments))}),e.apply(this,arguments)}};if(window.hasovrde!=1){injek();}");
    super.onPageFinished(view, url);
}
``` 
Diatas saya membuat fungsi *injek*, intinya agar script di inject sekali saja jika memang belum pernah melakukan inject JS diatas. Oya, script sengaja di Minify menggunakan layanan online http://refresh-sf.com/, atau kamu bisa meng-unminify disini http://unminify.com/
## Inspect Element
![image](https://developer.chrome.com/devtools/images/elements-panel.png)
> Google Chrome DevTools

Selanjutnya adalah Inspect Element atau DOM, dalam Google Chrome DevTools faselitas ini ada di panel pertama. Disini kita bisa melihat source code dari setiap element, misalnya dengan cara klik Kanan di Chrome pada element yang dikehendaki, trus pilih Inspect. 

Di Android kita juga akan melakukannya, yaitu dengan cara men-Tap element yang dikehendaki (misal sebuah form), maka source code dari element tersebut akan muncul, dan kita juga bisa mengeditnya.
```javascript
document.addEventListener('click', function(e) {
    e.preventDefault();
    e.stopPropagation();
    var elem = document.elementFromPoint(e.clientX, e.clientY);
    alert(elem.parentElement.outerHTML)
}, true);
```

Fungsi JS diatas yaitu untuk mendapatkan Element dari setiap yang kita Klik/Tap dalam halaman web. Dengan *preventDefault* serta  *stopPropagation* membuat fungsi default klik di nonaktifkan, misalnya klik di sebuah link maka kita tidak diarahkan ke link tersebut.
Seperti biasa, inject JS diatas saat laman selesai dimuat. Seperti ini:
```javascript
@Override
public void onPageFinished(WebView view, String url) {
    // click to Element
    view.loadUrl("javascript:function injek2(){window.touchblock=0,window.dummy1=1,document.addEventListener('click',function(n){if(1==window.touchblock){n.preventDefault();n.stopPropagation();var t=document.elementFromPoint(n.clientX,n.clientY);window.ganti=function(n){t.outerHTML=n},window.gantiparent=function(n){t.parentElement.outerHTML=n},$$.print(t.parentElement.outerHTML, t.outerHTML)}},!0)}1!=window.dummy1&&injek2();");
    // xhr override
    view.loadUrl("javascript:function injek(){window.hasovrde=1;var e=XMLHttpRequest.prototype.open;XMLHttpRequest.prototype.open=function(ee,nn,aa){this.addEventListener('load',function(){$$.log(this.responseText, nn, JSON.stringify(arguments))}),e.apply(this,arguments)}};if(window.hasovrde!=1){injek();}");
    super.onPageFinished(view, url);
}
```

#### Bahaimana mengedit Element dari hasil klik?
Jika JS diatas di Minify, terlihat ada fungsi global *ganti* dan *gantiparent*, jadi setiap Element dari hasil click bisa kita edit menggunakan kedua fungsi tersebut.

## DOWNLOAD DEMO
-** asap **
