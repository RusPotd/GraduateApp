package com.r.graduateregistration.presentation.main


import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.r.graduateregistration.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database:FirebaseFirestore

    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

    }

//    fun init() {
//        database = FirebaseFirestore.getInstance()
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
//                Log.d("permission", "permission denied to WRITE_EXTERNAL_STORAGE - requesting it")
//                val permissions = arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                requestPermissions(permissions, 1)
//            }
//        }
//
//        database.collection("updates").get().addOnCompleteListener(object: OnCompleteListener<QuerySnapshot>{
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
//
//    }


}