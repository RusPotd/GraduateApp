package com.r.graduateregistration.presentation.main

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import com.r.graduateregistration.R
import com.r.graduateregistration.databinding.FragmentMainBinding
import com.r.graduateregistration.domain.models.GraduateData
import com.r.graduateregistration.presentation.login.WelcomeActivity
import com.r.graduateregistration.presentation.main.util.MainUiEvents
import kotlinx.coroutines.flow.collectLatest
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()

    var degree: JSONArray? = null

    private var fullName = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        getGraduateCount()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.circleImageView.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_editProfileFragment)
        }

        lifecycleScope.launchWhenStarted {

            mainViewModel.eventFlow.collectLatest { authEvents ->

                when (authEvents) {
                    MainUiEvents.OnWelcome -> {
                        val openWelcomeActivity = Intent(requireActivity(), WelcomeActivity::class.java)
                        startActivity(openWelcomeActivity)
                        requireActivity().finish()
                    }
                    MainUiEvents.OnLoggedIn -> {

                    }
                    else -> Unit
                }
            }

        }

        lifecycleScope.launchWhenStarted {
            if (mainViewModel.isUserLoggedIn()) {
                setUpUser()
            }
        }

        binding.txtLogout.setOnClickListener {
            mainViewModel.onEvent(MainUiEvents.OnWelcome)
        }

        binding.addNewGraduate.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_graduateRegistrationFragment)
        }

        binding.showAllGraduate.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_allGraduatesFragment)
        }


        binding.txtUserName.text = fullName


    }

    private fun getGraduateCount() {
        val url = "https://padvidhar.com/fetch-graduates/1"

        //fetch graduates
        val queue = Volley.newRequestQueue(requireContext())

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null.toString(),
            { response ->

                try {
                    degree = response.getJSONArray("graduates")


                    val gson = GsonBuilder().create()
                    val allGraduatesList =
                        gson.fromJson(degree.toString(), Array<GraduateData>::class.java).toList()

                    binding.registrationCount.text = allGraduatesList.size.toString()


                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, { // below line is use to display a toast message along with our error.
                Toast.makeText(requireContext(), "Fail to get data..", Toast.LENGTH_SHORT).show()
            })

        queue.add<JSONObject>(jsonObjectRequest)
    }

    private suspend fun setUpUser() {

        val userDetails = mainViewModel.getUserDetails()
        if (userDetails != null) {
            fullName = userDetails.fullName
            binding.txtUserName.text = fullName
        }

    }

    fun init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requireActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                Log.d("permission", "permission denied to WRITE_EXTERNAL_STORAGE - requesting it")
                val permissions = arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permissions, 1)
            }
        }

//        database.collection("updates").get().addOnCompleteListener(object:
//            OnCompleteListener<QuerySnapshot> {
//            override fun onComplete(p0: Task<QuerySnapshot>) {
//                if(p0.isSuccessful){
//                    for(data in p0.result!!){
//                        var avail = data.get("available")
//                        val versionCode: String = BuildConfig.VERSION_NAME
//                        if(avail != versionCode){
//                            MaterialAlertDialogBuilder(this@MainActivity)
//                                .setTitle("Update Available !!")
//                                .setMessage("Compulsory update avaiable,  please update app to latest version to continue!!!")
//                                .setPositiveButton("update",
//                                    DialogInterface.OnClickListener { p0, p1 ->
//                                        val browserIntent = Intent(
//                                            Intent.ACTION_VIEW,
//                                            Uri.parse(data.get("path").toString())
//
//                                        )
//                                        startActivity(browserIntent)
//                                        this@MainActivity.finish()
//                                        exitProcess(0)
//                                    })
//                                .setNegativeButton("No", DialogInterface.OnClickListener{ p0, p1 ->
//                                    this@MainActivity.finish()
//                                    exitProcess(0)
//                                })
//                                .show()
//                        }
//                    }
//                }
//            }
//
//        })

    }

}