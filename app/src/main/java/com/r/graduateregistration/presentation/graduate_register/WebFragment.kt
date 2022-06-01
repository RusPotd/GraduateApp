package com.r.graduateregistration.presentation.graduate_register

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.r.graduateregistration.R
import java.io.File


class WebFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val thisContext = requireContext()

        val v = inflater.inflate(R.layout.fragment_web, container, false)
        webViewSetup(v, thisContext)
        return v;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun webViewSetup(v: View, thiscontext: Context) {
        var web_form = v.findViewById<WebView>( R.id.web_form )
        web_form.webViewClient = WebViewClient()

        web_form.apply {
            loadUrl("https://file-examples.com/index.php/sample-documents-download/sample-pdf-download/")
            settings.javaScriptEnabled = true

        }

        val file = provideOutputMainDirectory(thiscontext)



        web_form.setDownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->
            val request = DownloadManager.Request(Uri.parse(url))
            request.setMimeType(mimeType)
            val cookies: String = CookieManager.getInstance().getCookie(url)
            request.addRequestHeader("cookie", cookies)
            request.addRequestHeader("User-Agent", userAgent)
            request.setDescription("Downloading file....")
            request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType))
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(
                "$file/padd",
                URLUtil.guessFileName(url, contentDisposition, mimeType)
            )

            val dm = thiscontext.getSystemService(DOWNLOAD_SERVICE) as DownloadManager?
            dm!!.enqueue(request)
            Toast.makeText(
                requireActivity(),
                "Downloading File",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun provideOutputMainDirectory(appContext: Context): File {
        return appContext.getExternalFilesDir("").let {
            File(it, "padavidhar").apply { mkdirs() }
        }
    }
}