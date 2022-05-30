package com.r.graduateregistration.presentation.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.r.graduateregistration.databinding.ActivityMainBinding
import com.r.graduateregistration.presentation.login.WelcomeActivity
import com.r.graduateregistration.presentation.main.util.MainUiEvents
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        lifecycleScope.launch {
//            if (mainViewModel.isUserLoggedIn()) {
//                mainViewModel.getUserId()
//                mainViewModel.getUserDetails()
//            }
//        }


    }
}