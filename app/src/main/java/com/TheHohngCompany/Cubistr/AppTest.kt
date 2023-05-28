package com.TheHohngCompany.Cubistr

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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


class AppTest : AppCompatActivity() {


    lateinit var webView: WebView

    var imagesUri: ValueCallback<Array<Uri>>? = null
    lateinit var startForResult: ActivityResultLauncher<Intent>

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


        //то что новое

        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {

                imagesUri = filePathCallback
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent.resolveActivity(packageManager) != null) {
                    try {
                        takePictureIntent.putExtra("PhotoPath", createImageFile())
                    } catch (_: IOException) {
                    }
                }
                val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
                contentSelectionIntent.type = "image/*"
                val intentArray = arrayOf(takePictureIntent)
                val bibitpdas = Intent(Intent.ACTION_CHOOSER)
                bibitpdas.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                bibitpdas.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
                startForResult.launch(bibitpdas)

                return true
            }
        }

        webView.loadUrl(" http://tsapptest.xyz/")

        startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (imagesUri == null && result.data == null && result.resultCode == RESULT_CANCELED) {
                    imagesUri?.onReceiveValue(null)
                    return@registerForActivityResult
                }
                if (result.data?.data?.toString()?.startsWith("content://") == true) {
                    imagesUri?.onReceiveValue(arrayOf(Uri.parse(result.data?.dataString)))
                } else {
                    val camasdphotasdas = result.data?.extras?.get("data") as? Bitmap
                    camasdphotasdas?.let {
                        val bytes = ByteArrayOutputStream()
                        it.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                        val asdfjkljasdf = MediaStore.Images.Media.insertImage(
                            contentResolver,
                            it,
                            "a",
                            "a"
                        )
                        imagesUri?.onReceiveValue(arrayOf(Uri.parse(asdfjkljasdf)))
                        imagesUri = null
                    }
                }
            }

        //конец

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
            return Intent(context, AppTest::class.java)
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