package com.eugenebaturov.gaymer.ui.fragments

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eugenebaturov.gaymer.DotaRepository
import com.eugenebaturov.gaymer.R
import com.eugenebaturov.gaymer.model.entities.Hero
import com.eugenebaturov.gaymer.utils.Constants.Companion.TAG_RESPONSE
import com.eugenebaturov.gaymer.utils.Constants.Companion.TAG_SUCCESS
import com.eugenebaturov.gaymer.utils.Constants.Companion.URL_FOR_PICASSO
import com.eugenebaturov.gaymer.viewmodels.hero.HeroListViewModel
import com.eugenebaturov.gaymer.viewmodels.hero.HeroListViewModelFactory
import com.squareup.picasso.Picasso

class HeroListFragment: Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HeroAdapter

    private lateinit var viewModel: HeroListViewModel
    private lateinit var viewModelFactory: HeroListViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.fragment_hero_list, container, false)

        recyclerView = view.findViewById(R.id.recyclerView_heroList)

        val repository = DotaRepository.get()
        viewModelFactory = HeroListViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(HeroListViewModel::class.java)

        viewModel.getHeroes()

        viewModel.myResponse.observe(this, Observer { response ->
            if (response.isSuccessful) {
                val heroes = response.body()!!
                Log.d(TAG_SUCCESS, heroes[2].urlImage.toString()) // Axe
                setAdapter(heroes)
            } else {
                Log.e(TAG_RESPONSE, response.errorBody().toString())
            }

        })

        return view
    }

    private inner class HeroHolder(view: View): RecyclerView.ViewHolder(view) {

        private lateinit var hero: Hero

        private val heroImageView: ImageView = itemView.findViewById(R.id.hero_avatar)
        private val heroName: TextView = itemView.findViewById(R.id.hero_name)
        private val heroRole: TextView = itemView.findViewById(R.id.hero_role)
        private val heroClassIcon: ImageView = itemView.findViewById(R.id.type_damage_icon)

        fun bind(hero: Hero) {
            this.hero = hero
            val url = URL_FOR_PICASSO+this.hero.urlImage

            Picasso.get().load(url).into(heroImageView)
            heroName.text = this.hero.name
            heroRole.text = "${this.hero.roles[0]} and ${this.hero.roles.size -1}+"

            when(this.hero.attackType) {

                "Melee" -> {

                    when(this.hero.primaryAttribute) {
                        "str" -> heroClassIcon.setImageResource(R.drawable.ic_str_melee)
                        "agi" -> heroClassIcon.setImageResource(R.drawable.ic_melee)
                        "int" -> heroClassIcon.setImageResource(R.drawable.ic_int_range)
                    }
                }

                "Ranged" -> {

                    when(this.hero.primaryAttribute) {
                        "str" -> heroClassIcon.setImageResource(R.drawable.ic_str_melee)
                        "agi" -> heroClassIcon.setImageResource(R.drawable.ic_range)
                        "int" -> heroClassIcon.setImageResource(R.drawable.ic_int_range)
                    }
                }
            }
        }
    }

    private inner class HeroAdapter(var heroes: List<Hero>): RecyclerView.Adapter<HeroHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeroHolder {
            val view = layoutInflater.inflate(R.layout.fragment_hero_list_item, parent, false)
            return HeroHolder(view)
        }

        override fun getItemCount(): Int {
            return heroes.size
        }

        override fun onBindViewHolder(holder: HeroHolder, position: Int) {
            val hero = heroes[position]
            holder.bind(hero)
        }

    }

    private fun setAdapter(heroes: List<Hero>) {
        adapter = HeroAdapter(heroes)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.hasFixedSize()
        recyclerView.adapter = adapter
    }

    companion object {

        fun newInstance(): HeroListFragment {

            return HeroListFragment()
        }
    }
}