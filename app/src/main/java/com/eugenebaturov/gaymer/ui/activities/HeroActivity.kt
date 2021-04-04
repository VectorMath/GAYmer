package com.eugenebaturov.gaymer.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.eugenebaturov.gaymer.R
import com.eugenebaturov.gaymer.utils.Constants.Companion.KEY_HERO_AGI
import com.eugenebaturov.gaymer.utils.Constants.Companion.KEY_HERO_ATTACK_TYPE
import com.eugenebaturov.gaymer.utils.Constants.Companion.KEY_HERO_HEALTH_POINT
import com.eugenebaturov.gaymer.utils.Constants.Companion.KEY_HERO_ICON
import com.eugenebaturov.gaymer.utils.Constants.Companion.KEY_HERO_IMG
import com.eugenebaturov.gaymer.utils.Constants.Companion.KEY_HERO_INT
import com.eugenebaturov.gaymer.utils.Constants.Companion.KEY_HERO_MANA_POINT
import com.eugenebaturov.gaymer.utils.Constants.Companion.KEY_HERO_MAX_DAMAGE
import com.eugenebaturov.gaymer.utils.Constants.Companion.KEY_HERO_MIN_DAMAGE
import com.eugenebaturov.gaymer.utils.Constants.Companion.KEY_HERO_NAME
import com.eugenebaturov.gaymer.utils.Constants.Companion.KEY_HERO_PRM_ATR
import com.eugenebaturov.gaymer.utils.Constants.Companion.KEY_HERO_ROLES
import com.eugenebaturov.gaymer.utils.Constants.Companion.KEY_HERO_STR
import com.eugenebaturov.gaymer.utils.Constants.Companion.URL_FOR_PICASSO
import com.squareup.picasso.Picasso

class HeroActivity : AppCompatActivity() {

    private lateinit var avatarImageView: ImageView
    private lateinit var iconImageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var attackTypeTextView: TextView
    private lateinit var prmAttrTextView: TextView
    private lateinit var rolesTextView: TextView
    private lateinit var minDamageTextView: TextView
    private lateinit var maxDamageTextView: TextView
    private lateinit var healthTextView: TextView
    private lateinit var manaTextView: TextView
    private lateinit var strTextView: TextView
    private lateinit var agiTextView: TextView
    private lateinit var intTextView: TextView

    private lateinit var avatarURL: String
    private lateinit var iconURL: String
    private lateinit var name: String
    private lateinit var attackType: String
    private lateinit var prmAttr: String
    private lateinit var roles: String
    private var minDamage: Int = 0
    private var maxDamage: Int = 0
    private var health: Int = 0
    private var mana: Int = 0
    private var str: Int = 0
    private var agi: Int = 0
    private var int: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hero)

        findViewsById()
        getInfoAboutHero()
        updateUI()
    }

    private fun findViewsById() {
        avatarImageView = findViewById(R.id.hero_avatar)
        nameTextView = findViewById(R.id.hero_name)
        attackTypeTextView = findViewById(R.id.hero_attack_type)
        prmAttrTextView = findViewById(R.id.hero_attribute)
        rolesTextView = findViewById(R.id.hero_role)
        minDamageTextView = findViewById(R.id.hero_minDamage)
        maxDamageTextView = findViewById(R.id.hero_maxDamage)
        healthTextView = findViewById(R.id.hero_health)
        manaTextView = findViewById(R.id.hero_mana)
        strTextView = findViewById(R.id.hero_str)
        agiTextView = findViewById(R.id.hero_agi)
        intTextView = findViewById(R.id.hero_int)
    }

    private fun getInfoAboutHero() {
        avatarURL = intent.getStringExtra(KEY_HERO_IMG).toString()
        iconURL = intent.getStringExtra(KEY_HERO_ICON).toString()
        name = intent.getStringExtra(KEY_HERO_NAME).toString()
        prmAttr = intent.getStringExtra(KEY_HERO_PRM_ATR).toString()
        attackType = intent.getStringExtra(KEY_HERO_ATTACK_TYPE).toString()
        roles = intent.getStringExtra(KEY_HERO_ROLES).toString()
        minDamage = intent.getIntExtra(KEY_HERO_MIN_DAMAGE, 0)
        maxDamage = intent.getIntExtra(KEY_HERO_MAX_DAMAGE, 0)
        health = intent.getIntExtra(KEY_HERO_HEALTH_POINT, 0)
        mana = intent.getIntExtra(KEY_HERO_MANA_POINT, 0)
        str = intent.getIntExtra(KEY_HERO_STR, 0)
        agi = intent.getIntExtra(KEY_HERO_AGI, 0)
        int = intent.getIntExtra(KEY_HERO_INT, 0)

        health += 5 * str
        mana += 5 * int
    }

    private fun updateUI() {
        Picasso.get().load(URL_FOR_PICASSO+avatarURL).into(avatarImageView)
        nameTextView.text = name
        prmAttrTextView.text = prmAttr
        attackTypeTextView.text = attackType
        rolesTextView.text = roles
        minDamageTextView.text = minDamage.toString()
        maxDamageTextView.text = maxDamage.toString()
        healthTextView.text = health.toString()
        manaTextView.text = mana.toString()
        strTextView.text = str.toString()
        agiTextView.text = agi.toString()
        intTextView.text = int.toString()
    }
}