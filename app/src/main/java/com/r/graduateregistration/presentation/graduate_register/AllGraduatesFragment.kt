package com.r.graduateregistration.presentation.graduate_register

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import com.r.graduateregistration.R
import com.r.graduateregistration.databinding.FragmentAllGraduatesBinding
import com.r.graduateregistration.domain.models.GraduateData
import com.r.graduateregistration.presentation.graduate_register.util.GraduateAdapter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.text.Typography.degree


class AllGraduatesFragment : Fragment() {
    private var _binding: FragmentAllGraduatesBinding? = null
    private val binding get() = _binding!!

    var bitmap: Bitmap? = null
    var encodeImageString: String? = null
    var encodeAdharString: String? = null
    var degree: JSONArray? = null
    var selectedGender = "";
    var selectedDegree = "";

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllGraduatesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val url = "https://padvidhar.com/fetch-graduates/1"


        binding.allGraduate.layoutManager = LinearLayoutManager(requireContext())


        //fetch and show degrees
        val queue = Volley.newRequestQueue(requireContext())

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null.toString(),
            { response ->

                try {
                    degree = response.getJSONArray("graduates")


                    val gson = GsonBuilder().create()
                    val allGraduatesList =
                        gson.fromJson(degree.toString(), Array<GraduateData>::class.java).toList()


                    val adapter = GraduateAdapter(allGraduatesList)

                    binding.allGraduate.adapter = adapter


                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, { // below line is use to display a toast message along with our error.
                Toast.makeText(getActivity(), "Fail to get data..", Toast.LENGTH_SHORT).show()
            })

        queue.add<JSONObject>(jsonObjectRequest)
    }

}