package com.eugenebaturov.gaymer.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eugenebaturov.gaymer.DotaRepository
import com.eugenebaturov.gaymer.R
import com.eugenebaturov.gaymer.model.entities.Team
import com.eugenebaturov.gaymer.utils.Constants
import com.eugenebaturov.gaymer.utils.Constants.Companion.URL_FOR_PICASSO
import com.eugenebaturov.gaymer.viewmodels.team.TeamListViewModel
import com.eugenebaturov.gaymer.viewmodels.team.TeamListViewModelFactory
import com.squareup.picasso.Picasso

class TeamListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TeamAdapter

    private lateinit var viewModel: TeamListViewModel
    private lateinit var viewModelFactory: TeamListViewModelFactory

    private var callbacks: Callbacks? = null

    private val manager = GridLayoutManager(context, 3)

    interface Callbacks {
        fun onTeamSelected(id: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_team_list, container, false)
        val repository = DotaRepository.get()

        recyclerView = view.findViewById(R.id.recyclerView_teamList)

        viewModelFactory = TeamListViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(TeamListViewModel::class.java)

        return view
    }

    override fun onStart() {
        super.onStart()

        viewModel.getTeams()
        viewModel.myResponse.observe(this, Observer { response ->
            if (response.isSuccessful) {
                val teams = response.body()!!
                setAdapter(teams)
            } else {
                Log.e(Constants.TAG_RESPONSE, response.errorBody().toString())
            }
        })
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    private inner class TeamHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        private lateinit var team: Team

        val teamLogoImageView: ImageView = itemView.findViewById(R.id.team_logo)
        val titleTextView: TextView = itemView.findViewById(R.id.team_name)
        val ratingTextView: TextView = itemView.findViewById(R.id.team_rating)

        fun bind(team: Team) {

            this.team = team

            if (this.team.logoUrl == null) {
                teamLogoImageView.setImageResource(R.drawable.team_unknown)
            } else {
                Picasso.get().load(this.team.logoUrl).into(teamLogoImageView)
            }

            if (this.team.teamName == "") {
                titleTextView.setText(R.string.unknown_team)
            } else {
                titleTextView.text = this.team.teamName
            }
            ratingTextView.text = this.team.rating.toString()
        }

        override fun onClick(v: View?) {
            callbacks?.onTeamSelected(team.id)
        }
    }

    private inner class TeamAdapter(val teams: List<Team>) : RecyclerView.Adapter<TeamHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamHolder {
            val view = layoutInflater.inflate(R.layout.fragment_team_list_item, parent, false)
            return TeamHolder(view)
        }

        override fun getItemCount(): Int {
            return teams.size
        }

        override fun onBindViewHolder(holder: TeamHolder, position: Int) {
            val team = teams[position]
            holder.bind(team)
        }

    }

    private fun setAdapter(teams: List<Team>) {
        recyclerView.layoutManager = manager
        recyclerView.hasFixedSize()
        adapter = TeamAdapter(teams)
        recyclerView.adapter = adapter
    }

    companion object {

        fun newInstance(): TeamListFragment {

            return TeamListFragment()
        }
    }
}