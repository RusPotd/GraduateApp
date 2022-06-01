package com.r.graduateregistration.presentation.graduate_register

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.github.dhaval2404.imagepicker.ImagePicker
import com.r.graduateregistration.R
import com.r.graduateregistration.databinding.FragmentGraduateRegistrationBinding
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.xml.transform.ErrorListener
import javax.xml.transform.TransformerException


class GraduateRegistrationFragment : Fragment() {
    private var _binding: FragmentGraduateRegistrationBinding? = null
    private val binding get() = _binding!!

    var bitmap: Bitmap? = null
    var encodeImageString: String? = null
    var encodeAdharString: String? = null
    var degree : JSONArray? = null
    private val url = "https://padvidhar.com/add-user-data"
    var selectedGender = "";
    var selectedDegree = "";

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGraduateRegistrationBinding.inflate(inflater, container, false)

        val url = "https://padvidhar.com/fetch-degrees"

        //fetch and show degrees
        val queue = Volley.newRequestQueue(requireContext())

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null.toString(),
            { response ->

                try {
                    degree = response.getJSONArray("degree")
                    Log.d("Degree", degree.toString()+" Length: "+degree!!.length())

                    var degreeArray : MutableList<String>? = mutableListOf("Dummy")
                    (0 until degree!!.length()).forEach {
                        val book = degree?.getJSONObject(it)
                        Log.d("Book", book!!.get("name").toString())
                        if (book != null) {
                            degreeArray!!.add(book.get("name").toString())
                        }
                    }
                    degreeArray!!.removeAt(0)

                    // access the spinner
                    val spinnerDegree = binding.spinnerDegree
                    if (spinnerDegree != null) {
                        val adapterDegree = getActivity()?.let {
                            ArrayAdapter(
                                it,
                                R.layout.spinner_item, degreeArray!!.toCollection(ArrayList())
                            )
                        }
                        spinnerDegree.adapter = adapterDegree

                        spinnerDegree.onItemSelectedListener = object :
                            AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>,
                                                        view: View, position: Int, id: Long) {
                                selectedDegree = degreeArray!![position]
                            }

                            override fun onNothingSelected(parent: AdapterView<*>) {
                                // write code to perform some action
                            }
                        }
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, { // below line is use to display a toast message along with our error.
                Toast.makeText(getActivity(), "Fail to get data..", Toast.LENGTH_SHORT).show()
            })

        queue.add<JSONObject>(jsonObjectRequest)

        val gender = resources.getStringArray(R.array.Gender)

        // access the spinner
        val spinner = binding.spinnerGender
        if (spinner != null) {
            val adapter = getActivity()?.let {
                ArrayAdapter(
                    it,
                    R.layout.spinner_item, gender
                )
            }
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    selectedGender = gender[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.uploadDegreeBtn.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .start(100)
        }

        binding.uploadAdharBtn.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .start(200)
        }

        binding.btnSaveChanges.setOnClickListener{
            uploaddatatodb()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val uri: Uri = data?.data!!
        Toast.makeText(getActivity(), uri.toString(), Toast.LENGTH_SHORT).show()

        try {
            val inputStream: InputStream? = getActivity()?.getContentResolver()?.openInputStream(uri)
            bitmap = BitmapFactory.decodeStream(inputStream)
            encodeBitmapImage(bitmap!!, requestCode)
        } catch (ex: Exception) {
        }

    }

    private fun encodeBitmapImage(bitmap: Bitmap, requestCode: Int) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val bytesofimage: ByteArray = byteArrayOutputStream.toByteArray()
        when (requestCode) {
            100 -> {
                encodeImageString = Base64.encodeToString(bytesofimage, Base64.DEFAULT)
            }
            200 -> {
                encodeAdharString = Base64.encodeToString(bytesofimage, Base64.DEFAULT)
            }
        }
    }

    private fun uploaddatatodb() {

        val request: StringRequest =
            object : StringRequest(Request.Method.POST, url,
                Response.Listener<String?> { response ->
                    Toast.makeText(
                        requireActivity(),
                        response, Toast.LENGTH_LONG
                    ).show()
                    findNavController().navigate(R.id.action_graduateRegistrationFragment_to_webFragment)

                }, object : ErrorListener, Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError) {
                    Toast.makeText(
                        requireActivity(),
                        error.toString(),
                        Toast.LENGTH_LONG
                    ).show()

                    findNavController().navigate(R.id.action_graduateRegistrationFragment_to_webFragment)

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
                    map["nm"] = binding.name.text.toString()
                    map["mob"] = binding.mobileNum.text.toString()
                    map["gender"] = selectedGender
                    map["degree_nm"] = selectedDegree
                    map["degree"] = encodeImageString!!
                    map["adhar"] = encodeAdharString!!
                    return map
                }
            }
        val queue = Volley.newRequestQueue(getActivity())
        queue.add(request)
    }

}