package com.TheHohngCompany.Cubistr

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.PersistableBundle
import android.provider.MediaStore
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.TheHohngCompany.Cubistr.databinding.ActivityAppTestBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


class AppTestActivity : AppCompatActivity() {


    private lateinit var webView: WebView

    var imagesUri: ValueCallback<Array<Uri>>? = null
    lateinit var startForResult: ActivityResultLauncher<Intent>

    private val webViewStateKey = "webViewState"
    private var webViewState: Bundle? = null


    private val binding by lazy {
        ActivityAppTestBinding.inflate(layoutInflater)
    }


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        webView = findViewById(R.id.webView)
        webView.webViewClient = MyWebViewClient()
        webView.settings.javaScriptEnabled = true


        webView.webViewClient = MyWebViewClient()


        if (savedInstanceState != null) {
            webViewState = savedInstanceState.getBundle(webViewStateKey)
            webViewState?.let { webView.restoreState(it) }
        } else {
            webView.loadUrl("http://tsapptest.xyz/")
        }

        //то что новое

        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {

                imagesUri = filePathCallback
                val takePictureByCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureByCameraIntent.resolveActivity(packageManager) != null) {
                    try {
                        takePictureByCameraIntent.putExtra("PhotoPath", createImageFile())
                    } catch (_: IOException) {
                    }
                }
                val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
                contentSelectionIntent.type = "image/*"
                val intentArrayOfCameraPictures = arrayOf(takePictureByCameraIntent)
                val chooserIntent = Intent(Intent.ACTION_CHOOSER)
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArrayOfCameraPictures)

                startForResult.launch(chooserIntent)

                return true
            }
        }


        startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (imagesUri == null && result.data == null && result.resultCode == RESULT_CANCELED) {
                    imagesUri?.onReceiveValue(null)
                    return@registerForActivityResult
                }
                if (result.data?.data?.toString()?.startsWith("content://") == true) {
                    val image = arrayOf(Uri.parse(result.data?.dataString))
                    imagesUri?.onReceiveValue(image)
                } else {
                    val imageFromCamera = result.data?.extras?.get("data") as? Bitmap
                    imageFromCamera?.let {
                        val bytes = ByteArrayOutputStream()
                        it.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                        val compressedImage = MediaStore.Images.Media.insertImage(
                            contentResolver,
                            it,
                            "a",
                            "a"
                        )
                        imagesUri?.onReceiveValue(arrayOf(Uri.parse(compressedImage)))
                        imagesUri = null
                    }
                }
            }


        //конец

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Сохранение состояния WebView
        webViewState = Bundle()
        webView.saveState(webViewState!!)
        outState.putBundle(webViewStateKey, webViewState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        // Восстановление состояния WebView
        webViewState = savedInstanceState.getBundle(webViewStateKey)
        webViewState?.let { webView.restoreState(it) }
    }




    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* префикс */
            ".jpg", /* суффикс */
            storageDir /* директория */
        )
    }


    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, AppTestActivity::class.java)
        }
    }


    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }


    private class MyWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            return if (url?.contains(Regex("http://")) == true) {
                false
            } else url?.contains(Regex("https://")) != true
        }


    }


}