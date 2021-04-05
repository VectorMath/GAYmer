package com.eugenebaturov.gaymer.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.eugenebaturov.gaymer.DotaRepository
import com.eugenebaturov.gaymer.R
import com.eugenebaturov.gaymer.ui.fragments.AboutFragment
import com.eugenebaturov.gaymer.ui.fragments.FavoriteListFragment
import com.eugenebaturov.gaymer.ui.fragments.HeroListFragment
import com.eugenebaturov.gaymer.ui.fragments.TeamListFragment
import com.eugenebaturov.gaymer.utils.Constants.Companion.KEY_TEAM_ID
import com.eugenebaturov.gaymer.viewmodels.hero.HeroListViewModel
import com.eugenebaturov.gaymer.viewmodels.hero.HeroListViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), TeamListFragment.Callbacks {

    private lateinit var navMenu: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.currentFragment)
        val fragment: Fragment

        navMenu = findViewById(R.id.bottom_nav_menu)

        setTitle(R.string.article_heroes)

        navMenu.apply {

            setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_heroes -> changeFragment(
                        HeroListFragment.newInstance(),
                        R.string.article_heroes
                    )
                    R.id.menu_teams -> changeFragment(TeamListFragment.newInstance(), R.string.article_teams)
                    R.id.menu_favorite -> changeFragment(FavoriteListFragment.newInstance(), R.string.article_favorite)
                    R.id.menu_about -> changeFragment(AboutFragment.newInstance(), R.string.article_about)
                }
                true
            }
        }

        if (currentFragment == null) {
            fragment = HeroListFragment()

            supportFragmentManager
                .beginTransaction()
                .add(R.id.currentFragment, fragment)
                .commit()
        }

    }

    override fun onTeamSelected(id: Int) {
        val intent = Intent(this, TeamActivity::class.java)
        intent.putExtra(KEY_TEAM_ID, id)

        startActivity(intent)
    }

    private fun changeFragment(fragment: Fragment, resTitle: Int) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.currentFragment, fragment)
            .commit()

        setTitle(resTitle)
    }
}