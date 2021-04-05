package com.eugenebaturov.gaymer.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.eugenebaturov.gaymer.model.entities.LocalHero
import com.eugenebaturov.gaymer.ui.activities.HeroActivity
import com.eugenebaturov.gaymer.utils.Constants
import com.eugenebaturov.gaymer.utils.Constants.Companion.KEY_HERO_LOCAL_DB
import com.eugenebaturov.gaymer.utils.Constants.Companion.KEY_HERO_SPEED
import com.eugenebaturov.gaymer.viewmodels.hero.HeroViewModel
import com.eugenebaturov.gaymer.viewmodels.hero.HeroViewModelFactory
import com.squareup.picasso.Picasso

class FavoriteListFragment: Fragment() {

    private lateinit var emptyRecView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HeroAdapter

    private lateinit var viewModel: HeroViewModel
    private lateinit var viewModelFactory: HeroViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater
            .inflate(R.layout.fragment_hero_list_favorite, container, false)

        recyclerView = view.findViewById(R.id.recyclerView_heroList)
        emptyRecView = view.findViewById(R.id.rv_empty)

        val repository = DotaRepository.get()
        viewModelFactory = HeroViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(HeroViewModel::class.java)

        return view
    }

    override fun onStart() {
        super.onStart()

        viewModel.allHeroes.observe(this, Observer { heroes ->
            setAdapter(heroes)
        })
    }

    private inner class HeroHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        private lateinit var hero: LocalHero

        private val heroImageView: ImageView = itemView.findViewById(R.id.hero_avatar)
        private val heroName: TextView = itemView.findViewById(R.id.hero_name)
        private val heroRole: TextView = itemView.findViewById(R.id.hero_role)
        private val heroClassIcon: ImageView = itemView.findViewById(R.id.type_damage_icon)

        fun bind(hero: LocalHero) {
            this.hero = hero
            val url = Constants.URL_FOR_PICASSO + this.hero.urlImage

            Picasso.get().load(url).into(heroImageView)
            heroName.text = this.hero.name
            heroRole.text = this.hero.roles

            when (this.hero.attackType) {

                "Melee" -> {

                    when (this.hero.primaryAttribute) {
                        "str" -> heroClassIcon.setImageResource(R.drawable.ic_str_melee)
                        "agi" -> heroClassIcon.setImageResource(R.drawable.ic_melee)
                        "int" -> heroClassIcon.setImageResource(R.drawable.ic_int_range)
                    }
                }

                "Ranged" -> {

                    when (this.hero.primaryAttribute) {
                        "str" -> heroClassIcon.setImageResource(R.drawable.ic_str_melee)
                        "agi" -> heroClassIcon.setImageResource(R.drawable.ic_range)
                        "int" -> heroClassIcon.setImageResource(R.drawable.ic_int_range)
                    }
                }
            }
        }

        override fun onClick(v: View?) {
            val intent = Intent(context, HeroActivity::class.java)
            val isLocal = true
            intent.putExtra(KEY_HERO_LOCAL_DB, isLocal)
            putHeroExtras(intent, hero)
            startActivity(intent)
        }
    }

    private inner class HeroAdapter(var heroes: List<LocalHero>) : RecyclerView.Adapter<HeroHolder>() {

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

    private fun setAdapter(heroes: List<LocalHero>) {
        if (heroes.size != 0) {
            emptyRecView.visibility = View.GONE
            adapter = HeroAdapter(heroes)
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.hasFixedSize()
            recyclerView.adapter = adapter
        } else {
            emptyRecView.visibility = View.VISIBLE
        }

    }

    private fun putHeroExtras(intent: Intent, hero: LocalHero) {
        intent.putExtra(Constants.KEY_HERO_ID, hero.id)
        intent.putExtra(Constants.KEY_HERO_NAME, hero.name)
        intent.putExtra(Constants.KEY_HERO_ICON, hero.urlIcon)
        intent.putExtra(Constants.KEY_HERO_IMG, hero.urlImage)
        intent.putExtra(Constants.KEY_HERO_ARMOR, hero.baseArmor)
        intent.putExtra(KEY_HERO_SPEED, hero.moveSpeed)
        intent.putExtra(Constants.KEY_HERO_PRM_ATR, hero.primaryAttribute)
        intent.putExtra(Constants.KEY_HERO_ATTACK_TYPE, hero.attackType)
        intent.putExtra(Constants.KEY_HERO_ROLES, hero.roles)
        intent.putExtra(Constants.KEY_HERO_MIN_DAMAGE, hero.minDamage)
        intent.putExtra(Constants.KEY_HERO_MAX_DAMAGE, hero.maxDamage)
        intent.putExtra(Constants.KEY_HERO_MANA_POINT, hero.baseMana)
        intent.putExtra(Constants.KEY_HERO_HEALTH_POINT, hero.baseHealth)
        intent.putExtra(Constants.KEY_HERO_STR, hero.str)
        intent.putExtra(Constants.KEY_HERO_AGI, hero.agi)
        intent.putExtra(Constants.KEY_HERO_INT, hero.intHero)
    }

    companion object {

        fun newInstance(): FavoriteListFragment {
            return FavoriteListFragment()
        }
    }
}