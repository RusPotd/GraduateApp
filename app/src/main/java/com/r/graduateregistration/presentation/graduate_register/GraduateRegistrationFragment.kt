package com.r.graduateregistration.presentation.graduate_register

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.github.dhaval2404.imagepicker.ImagePicker
import com.r.graduateregistration.R
import com.r.graduateregistration.databinding.FragmentGraduateRegistrationBinding
import com.r.graduateregistration.presentation.main.MainViewModel
import kotlinx.coroutines.launch
import org.json.JSONArray
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
    var encodeNameChangeString: String? = null
    var degree : JSONArray? = null
    private val url = "https://padvidhar.com/add-user-data"
    var selectedGender = "";
    var selectedDegree = "";
    var selectedDistrict = "";
    var selectedTaluka = "";

    var university = "bamu";
    var district: Array<String>? = null;
    var taluka: Array<String>? = null;
    var name_change_check_list : CheckBox? = null;

    var karyakartId = "";
    private val mainViewModel: MainViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGraduateRegistrationBinding.inflate(inflater, container, false)

        binding.backArrow.setOnClickListener {
            findNavController().navigateUp()
        }

        val url = "https://padvidhar.com/fetch-degrees"

        //fetch and show degrees
        /*val queue = Volley.newRequestQueue(requireContext())

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null.toString(),
            { response ->

                try {
                    degree = response.getJSONArray("degree")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, { // below line is use to display a toast message along with our error.
                Toast.makeText(getActivity(), "Fail to get data..", Toast.LENGTH_SHORT).show()
            })

        queue.add<JSONObject>(jsonObjectRequest)*/

        binding.supportingDocument.visibility = View.GONE

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

        if(university == "bamu"){
            district = resources.getStringArray(R.array.Bamu_district)
        }

        // access the spinner
        val spinnerDistrict = binding.spinnerDistrict
        if (spinnerDistrict != null) {
            val adapterDistrict = getActivity()?.let {
                ArrayAdapter(
                    it,
                    R.layout.spinner_item, district!!
                )
            }
            spinnerDistrict.adapter = adapterDistrict

            spinnerDistrict.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    selectedDistrict = district!![position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }

        taluka = resources.getStringArray(
            getTalukaArrayString()
        )

        // access the spinner
        val spinnerTaluka = binding.spinnerTaluka
        if (spinnerTaluka != null) {
            val adapterTaluka = getActivity()?.let {
                ArrayAdapter(
                    it,
                    R.layout.spinner_item, taluka!!
                )
            }
            spinnerTaluka.adapter = adapterTaluka

            spinnerTaluka.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    selectedTaluka = taluka!![position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }

        name_change_check_list = binding.isNameChange

        name_change_check_list!!.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.supportingDocument.visibility = View.VISIBLE
        }

        return binding.root
    }

    private fun getTalukaArrayString(): Int {
        var value = R.array.Beed;

        if(selectedDistrict == "Aurangabad"){
            value = R.array.Aurangabad;
        }
        else if(selectedDistrict == "Jalna"){
            value = R.array.Jalna;
        }
        else if(selectedDistrict == "Osmanabad"){
            value = R.array.Osmanabad;
        }

        return value
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            val userDetail = mainViewModel.getUserDetails()
            if (userDetail != null) {
                karyakartId = userDetail.originID
            }
        }

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

        binding.uploadNameChangeBtn.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .start(300)
        }

        binding.btnSaveChanges.setOnClickListener{
            //Toast.makeText(getActivity(), "District : "+selectedDistrict+" Taluka : "+selectedTaluka, Toast.LENGTH_LONG)
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
            300 -> {
                encodeNameChangeString = Base64.encodeToString(bytesofimage, Base64.DEFAULT)
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

                    val bundle = Bundle()
                    bundle.putString("grad_number", binding.mobileNum.text.toString())
                    findNavController().navigate(R.id.action_graduateRegistrationFragment_to_webFragment, bundle)

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
                    map["nm"] = binding.name.text.toString()
                    map["mob"] = binding.mobileNum.text.toString()
                    map["gender"] = selectedGender
                    map["district"] = selectedDistrict
                    map["taluka"] = selectedTaluka
                    map["refer"] = karyakartId
                    map["degree_nm"] = selectedDegree
                    map["degree"] = encodeImageString!!
                    map["adhar"] = encodeAdharString!!

                    if(encodeNameChangeString != null){
                        map["name_change"] = encodeNameChangeString!!
                    }

                    return map
                }
            }
        val queue = Volley.newRequestQueue(getActivity())
        queue.add(request)
    }

}