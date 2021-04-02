package com.eugenebaturov.gaymer.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.eugenebaturov.gaymer.DotaRepository
import com.eugenebaturov.gaymer.R
import com.eugenebaturov.gaymer.viewmodels.hero.HeroListViewModel
import com.eugenebaturov.gaymer.viewmodels.hero.HeroListViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: HeroListViewModel
    private lateinit var viewModelFactory: HeroListViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val repository = DotaRepository.get()
        viewModelFactory = HeroListViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(HeroListViewModel::class.java)

        viewModel.getHeroes()

        viewModel.myResponse.observe(this, Observer { response ->
            if(response.isSuccessful) {
                Log.d("succses", response.body()?.get(1)?.name.toString()) // Axe
            } else {
                Log.d("CHECKCONNECT", response.errorBody().toString())
            }

        })

    }
}