package com.eugenebaturov.gaymer.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.eugenebaturov.gaymer.DotaRepository
import com.eugenebaturov.gaymer.R
import com.eugenebaturov.gaymer.model.entities.Team
import com.eugenebaturov.gaymer.utils.Constants.Companion.KEY_TEAM_ID
import com.eugenebaturov.gaymer.utils.Constants.Companion.TAG_RESPONSE
import com.eugenebaturov.gaymer.viewmodels.team.TeamListViewModel
import com.eugenebaturov.gaymer.viewmodels.team.TeamListViewModelFactory
import com.squareup.picasso.Picasso
import kotlin.properties.Delegates

class TeamActivity : AppCompatActivity() {

    private lateinit var logoImageView: ImageView
    private lateinit var titleImageView: TextView
    private lateinit var ratingTextView: TextView
    private lateinit var winMatchTextView: TextView
    private lateinit var loseMatchTextView: TextView

    private lateinit var viewModel: TeamListViewModel
    private lateinit var viewModelFactory: TeamListViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team)


        val repository = DotaRepository.get()

        viewModelFactory = TeamListViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(TeamListViewModel::class.java)

        findViewsById()
    }

    override fun onStart() {
        super.onStart()

        val id = intent.getIntExtra(KEY_TEAM_ID, 4)
        viewModel.getCurrentTeam(id)

        viewModel.currentTeamResponse.observe(this, Observer { response ->
            if (response.isSuccessful) {
                val team = response.body()!!
                updateUI(team)
            } else {
                Log.e(TAG_RESPONSE, response.errorBody().toString())
            }

        })
    }

    private fun findViewsById() {
        logoImageView = findViewById(R.id.team_logo)
        titleImageView = findViewById(R.id.team_name)
        ratingTextView = findViewById(R.id.team_rating)
        winMatchTextView = findViewById(R.id.win_count)
        loseMatchTextView = findViewById(R.id.lose_count)
    }

    private fun updateUI(team: Team) {
        Picasso.get().load(team.logoUrl).into(logoImageView)
        titleImageView.text = team.teamName
        ratingTextView.text = team.rating.toString()
        winMatchTextView.text = team.winsMatch.toString()
        loseMatchTextView.text = team.loseMatch.toString()
    }
}