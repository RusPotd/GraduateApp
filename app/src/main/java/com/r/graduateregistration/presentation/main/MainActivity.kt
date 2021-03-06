package com.r.graduateregistration.presentation.main

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.r.graduateregistration.BuildConfig
import com.r.graduateregistration.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database:FirebaseFirestore

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseFirestore.getInstance()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                Log.d("permission", "permission denied to WRITE_EXTERNAL_STORAGE - requesting it")
                val permissions = arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permissions, 1)
            }
        }

        database.collection("updates").get().addOnCompleteListener(object: OnCompleteListener<QuerySnapshot>{
            override fun onComplete(p0: Task<QuerySnapshot>) {
                if(p0.isSuccessful){
                    for(data in p0.result!!){
                        var avail = data.get("available")
                        val versionCode: String = BuildConfig.VERSION_NAME
                        if(avail != versionCode){
                            MaterialAlertDialogBuilder(this@MainActivity)
                                .setTitle("Update Available !!")
                                .setMessage("Compulsory update avaiable,  please update app to latest version to continue!!!")
                                .setPositiveButton("update",
                                    DialogInterface.OnClickListener { p0, p1 ->
                                        val browserIntent = Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse(data.get("path").toString())

                                        )
                                        startActivity(browserIntent)
                                        this@MainActivity.finish()
                                        exitProcess(0)
                                    })
                                .setNegativeButton("No", DialogInterface.OnClickListener{ p0, p1 ->
                                    this@MainActivity.finish()
                                    exitProcess(0)
                                })
                                .show()
                        }
                    }
                }
            }

        })



    }


}