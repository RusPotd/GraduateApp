package com.r.graduateregistration.presentation.graduate_register

import android.print.PrintJob;
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.r.graduateregistration.R
import java.io.File
import javax.xml.transform.ErrorListener
import javax.xml.transform.TransformerException


class WebFragment : Fragment() {

    var graduate_number = "";
    var graduate_responce = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        graduate_number = requireArguments().getString("grad_number")!!
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
        var save_btn = v.findViewById<Button>( R.id.btn_save_changes)

        web_form.webViewClient = WebViewClient()

        web_form.apply {
            loadUrl("https://online.bamu.ac.in/election2022/online-forms/graduates.php")
            settings.javaScriptEnabled = true
        }

        val file = provideOutputMainDirectory(thiscontext)

        save_btn.setOnClickListener {
            graduate_responce = web_form.url.toString()
            Toast.makeText(requireActivity(), graduate_responce, Toast.LENGTH_LONG).show()
            uploaddatatodb(web_form)
        }

       /* web_form.setDownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->
            val request = DownloadManager.Request(Uri.parse(url))
            request.setMimeType(mimeType)
            val cookies: String = CookieManager.getInstance().getCookie(url)
            request.addRequestHeader("cookie", cookies)
            request.addRequestHeader("User-Agent", userAgent)
            request.setDescription("Downloading file....")
            request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType))
            request.allowScanningByMediaScanner()
            var filename = URLUtil.guessFileName(url, contentDisposition, mimeType).toString()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(
                "$file/padd",
                filename
            )

            Toast.makeText(requireActivity(), filename, Toast.LENGTH_LONG).show()

            val dm = thiscontext.getSystemService(DOWNLOAD_SERVICE) as DownloadManager?
            dm!!.enqueue(request)
            Toast.makeText(
                requireActivity(),
                "Downloading File",
                Toast.LENGTH_SHORT
            ).show()
        }*/
    }

    // object of print job
    var printJob: PrintJob? = null

    // a boolean to check the status of printing
    var printBtnPressed = false

    private fun PrintTheWebPage(webView: WebView) {

        // set printBtnPressed true
        printBtnPressed = true

        // Creating  PrintManager instance
        val printManager = requireActivity().getSystemService(Context.PRINT_SERVICE) as PrintManager?

        // setting the name of job
        val jobName = graduate_number+"_graduate_form"

        // Creating  PrintDocumentAdapter instance
        val printAdapter = webView.createPrintDocumentAdapter(jobName)

        assert(printManager != null)
        printJob = printManager!!.print(
            jobName, printAdapter,
            PrintAttributes.Builder().build()
        )
    }

    private fun provideOutputMainDirectory(appContext: Context): File {
        return appContext.getExternalFilesDir("").let {
            File(it, "padavidhar").apply { mkdirs() }
        }
    }

    private fun uploaddatatodb(web_form: WebView) {
        var url = "https://padvidhar.com/add-form-pdf";

        val request: StringRequest =
            object : StringRequest(
                Request.Method.POST, url,
                Response.Listener<String?> { response ->
                    Toast.makeText(requireActivity(), response.toString(), Toast.LENGTH_LONG).show()
                    PrintTheWebPage(web_form)
                    findNavController().navigate(R.id.action_webFragment_to_mainFragment)

                }, object : ErrorListener, Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError) {
                        Toast.makeText(
                            requireActivity(),
                            error.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }

                    override fun warning(p0: TransformerException?) {
                        TODO("Not yet implemented")
                    }

                    override fun error(p0: TransformerException?) {
                        TODO("Not yet implemented")
                    }

                    override fun fatalError(p0: TransformerException?) {
                        TODO("Not yet implemented")
                    }
                }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): MutableMap<String, String>? {
                    val map: MutableMap<String, String> = HashMap()

                    map["number"] = graduate_number
                    map["form"] = graduate_responce

                    return map
                }
            }
        val queue = Volley.newRequestQueue(getActivity())
        queue.add(request)
    }
}