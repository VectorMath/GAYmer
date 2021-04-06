# GAYmer

## Оглавление
- [GAYmer](#gaymer)
  - [Оглавление](#оглавление)
  - [<a name="description">Описание проекта</a>](#описание-проекта)
    - [Что использовалось при разработке](#что-использовалось-при-разработке)
  - [<a name="patterns">Паттерны</a>](#паттерны)
    - [<a name="mvvm">MVVVM</a>](#mvvvm)
      - [<a name="model">Model</a>](#model)
        - [<a name="api">api</a>](#api)
        - [<a name="local">local</a>](#local)
        - [<a name="entities">entities</a>](#entities)
      - [<a name="view">View</a>](#view)
        - [<a name="activities">activities</a>](#activities)
        - [<a name="fragments">fragments</a>](#fragments)
      - [<a name="viewmodel">ViewModel</a>](#viewmodel)
    - [<a name="singletone">Singletone</a>](#singletone)
  - [<a name="constants">Константы (package utils)</a>](#константы-package-utils)
  - [<a name="screenshots">Скриншоты приложения</a>](#скриншоты-приложения)

------------

## <a name="description">Описание проекта</a>

**GAYmer** - это мобильное приложение по игре Dota 2, которая основана на OpenDotaAPI, способной брать информацию о всех героях и киберспортивных командах по данной дисциплине.

### Что использовалось при разработке

- **Kotlin**


- **Отображение данных**
  - RecyclerView
  - CardView

- **Создание локальной БД**
  - Room
  - LiveData

- **Удалённый запрос к данным в интернете**
   - Retrofit2

- **Ассинхронные запросы**
  - Coroutines

- **Паттерны**
  - MVVM (основной)
  - Singletone (репозиторий)

- **SOLID**
  
------------

## <a name="patterns">Паттерны</a>

При создание приложения использовались паттерны проектирования **MVVM** (Model-View-ViewModel) и **Singletone**.

### <a name="mvvm">MVVVM</a>
В MVVM была реализовано большая часть работы над приложением:
- **Model** - реализует всю бизнес-логику приложения.
- **View** - Отображает весь UI на экране пользователя.
- **ViewModel** - Является посредником между Model и Views, необходим для передачи корректных данных в наш пользовательский интерфейс.

Принцип работы следующий:

**Представление**(View) запрашивает данные у ViewModel -> **ViewModel** определяет, какие конкретно данные необходимы после чего отсылает этот запрос в репозиторий -> **Репозиторий** принимает данный запрос и определяет из какой части **Model** должен придти результат работы, после чего даёт команду для выполнения.


------------

#### <a name="model">Model</a>

Данный модуль отвечает за бизнес-логику, а именно получения данных с HTTP, с помощью API **OpenDotaAPI** и манипуляциями полученными данными в локальной базе данных SQLite с помощью библиотеки **Room**

Он состоит из следующих пакетов:
- **api** 
- **local**
- **entities**

##### <a name="api">api</a>

Пакет отвечающий за работу с HTTP, с помощью библиотеки Retrofit2. 

Хранит в себе:

- **HeroApi.kt** - Интерфейс который включает в себя запросы к персонажам.

Код HeroApi.kt
```kotlin
interface HeroApi {

    @GET("heroStats")
    suspend fun getHeroList(): Response<List<Hero>>

}
```

P.S Как вы могли заметить здесь лишь один запрос с общей базе героев, "ручки", которая получает информацию отдельного героя нет, поэтому пришлось брать эту информацию самостоятельно.

- **TeamApi.kt** - Интерфейс который включает в себя запросы к киберспортивным командам.

Код TeamApi.kt
```kotlin
interface TeamApi {

    @GET("teams")
    suspend fun getTeamList(): Response<List<Team>>

    @GET("teams/{id}")
    suspend fun getTeam(@Path("id")id: Int) : Response<Team>
}
```

- **RetrofitInstance.kt** - Объект, который создаёт экземпляр retrofit, в дальнейшем используется в репозитории.

Код RetrofitInstance.kt
```kotlin
object RetrofitInstance {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val heroApi : HeroApi by lazy {
        retrofit.create(HeroApi::class.java)
    }

    val teamApi: TeamApi by lazy {
        retrofit.create(TeamApi::class.java)
    }
}
```

##### <a name="local">local</a>

Пакет отвечающий за работу с локальной базой данных, с помощью библиотеки Room. 

Хранит в себе:

- **HeroDao.kt** - Интерфейс DAO, предоставленный библиотекой Room, хранит в себе запросы к локальной БД для героев.

Код HeroDao.kt
```kotlin
@Dao
interface HeroDao {

    @Query("SELECT * FROM heroes")
    fun getHeroes(): LiveData<List<LocalHero>>

    @Query("SELECT * FROM heroes WHERE id=(:id)")
    fun getHeroById(id: Int): LiveData<LocalHero?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addHero(hero: LocalHero)

    @Delete
    fun removeHero(hero: LocalHero)
}
```

- **HeroDatabase.kt** - Абстрактный класс, который с помощью аннотации Database создаёт нам базу данных с героями.

Код HeroDatabase.kt
```kotlin
@Database(entities = [LocalHero::class], version = 1)
abstract class HeroDatabase: RoomDatabase() {
    abstract fun heroDao(): HeroDao
}
```

##### <a name="entities">entities</a>

Пакет entities хранит в себе сущности(они же data-классы), которые используются в пакетах api и local

- **Hero.kt**

Сущность персонажа, которая хранит в себе информацию, полученною с помощью метода запроса getHeroList() в интерфейсе HeroApi.

Код Hero.kt

```kotlin
data class Hero(

    @SerializedName(value = "id")
    val id: Int,

    @SerializedName(value = "localized_name")
    val name: String,

    @SerializedName(value = "primary_attr")
    val primaryAttribute: String,

    @SerializedName(value = "attack_type")
    val attackType: String,

    @SerializedName(value = "roles")
    val roles: List<String>,

    @SerializedName(value = "img")
    val urlImage: String,

    @SerializedName(value = "icon")
    val urlIcon: String,

    @SerializedName(value = "base_health")
    val baseHealth: Int,

    @SerializedName(value = "base_mana")
    val baseMana: Int,

    @SerializedName(value = "base_armor")
    val baseArmor: Double,

    @SerializedName(value = "base_attack_min")
    val minDamage: Int,

    @SerializedName(value = "base_attack_max")
    val maxDamage: Int,

    @SerializedName(value = "move_speed")
    val moveSpeed: Int,

    @SerializedName(value = "base_str")
    var str: Int,

    @SerializedName(value = "base_agi")
    val agi: Int,

    @SerializedName(value = "base_int")
    val int: Int

)
```

- **Team.kt**

Сущность команды, которая хранит в себе информацию, полученною с помощью метода запроса getTeamList и getTeam в интерфейсе TeamApi.

Код Team.kt

```kotlin
data class Team(

    @SerializedName(value = "team_id")
    val id: Int,

    @SerializedName(value = "rating")
    val rating: Double,

    @SerializedName(value = "wins")
    val winsMatch: Int,

    @SerializedName(value = "losses")
    val loseMatch: Int,

    @SerializedName(value = "name")
    val teamName: String,

    @SerializedName(value = "logo_url")
    val logoUrl: String
)
```

- **LocalHero.kt**

Сущность персонажа, которая создаёт таблицу героев, в последствие использующаяся в локальной базе данных класса HeroDatabase

Код LocalHero.kt

```kotlin
@Entity(tableName = "heroes")
data class LocalHero(

    @PrimaryKey
    var id: Int,

    var name: String,

    var primaryAttribute: String,

    var attackType: String,

    var roles: String,

    var urlImage: String,

    var urlIcon: String,

    var baseHealth: Int,

    var baseMana: Int,

    var baseArmor: Double,

    var minDamage: Int,

    var maxDamage: Int,

    var moveSpeed: Int,

    var str: Int,

    var agi: Int,

    var intHero: Int
)
```

------------


#### <a name="view">View</a>


**Примечание:** Приложение выполняет всю техническую составляющию, которую я хотел реализовать, однако полный рефакторинг кода ещё не произведён. Например, вы можете увидеть, что data binding не был использован, это не говорит о том, что я не знаю, как его сделать, в дальнейшем я введу его. Тоже самое можно сказать и об адаптарах героев, которые в целом похожи лишь немногим отличаются, в дальнейшем я всё это исправлю. Однако есть момент с передачей данных героя HeroListFragment в HeroActivity, который осуществляется через интент, по другому сделать было невозможно, т.к ручки с конкретным героем нет.

Модуль, отвечающий за отображение изображений и данных на экране пользователя. Весь модуль реализован в пакете **ui**, который в свою очередь хранит пакет **activities** и пакет **fragments**.

##### <a name="activities">activities</a>

Данный пакет хранит в себе все активити приложения.

- **MainActivity.kt**
  Главная активити приложения, которая запускается вместе с приложением. Позволяет переключаться между всеми фрагментами приложения с помощью **BottomNavigationView**.


Код MainActivity.kt

  ```kotlin
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
  ```

- **TeamActivity.kt**

Активити которая отвечает за отображение интерфейса конкретной команды.

Код TeamActivity.kt
```kotlin
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
        if (team.logoUrl != null) {
            Picasso.get().load(team.logoUrl).into(logoImageView)
        } else {
            logoImageView.setImageResource(R.drawable.team_unknown)
        }
        titleImageView.text = team.teamName
        ratingTextView.text = team.rating.toString()
        winMatchTextView.text = team.winsMatch.toString()
        loseMatchTextView.text = team.loseMatch.toString()
    }
}
```

- **HeroActivity.kt**

Активити, которая отвечает за отображение информации о конкретном герое.

Код HeroActivity.kt:

```kotlin
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
    private lateinit var armorTextView: TextView
    private lateinit var speedTextView: TextView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var fabRemove: FloatingActionButton

    private lateinit var avatarURL: String
    private lateinit var iconURL: String
    private lateinit var name: String
    private lateinit var attackType: String
    private lateinit var prmAttr: String
    private lateinit var roles: String
    private var id: Int = 0
    private var minDamage: Int = 0
    private var maxDamage: Int = 0
    private var health: Int = 0
    private var mana: Int = 0
    private var str: Int = 0
    private var agi: Int = 0
    private var int: Int = 0
    private var speed: Int = 0
    private var armor: Double = 0.0

    private lateinit var viewModel: HeroViewModel
    private lateinit var viewModelFactory: HeroViewModelFactory
    private lateinit var hero: LocalHero

    private var isLocal: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hero)

        val repository = DotaRepository.get()
        viewModelFactory = HeroViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(HeroViewModel::class.java)

        findViewsById()
        checkIsLocalDb()
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
        fabAdd = findViewById(R.id.add_hero_to_favorite)
        fabRemove = findViewById(R.id.remove_hero_to_favorite)
        armorTextView = findViewById(R.id.armor_count)
        speedTextView = findViewById(R.id.speed_count)
    }

    private fun getInfoAboutHero() {
        id = intent.getIntExtra(KEY_HERO_ID, 0)
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
        armor = intent.getDoubleExtra(KEY_HERO_ARMOR, 1.5)
        speed = intent.getIntExtra(KEY_HERO_SPEED, 280)

        health += 5 * str
        mana += 5 * int

        hero = LocalHero(
            id = id,
            name = name,
            primaryAttribute = prmAttr,
            attackType = attackType,
            roles = roles,
            minDamage = minDamage,
            maxDamage = maxDamage,
            baseHealth = health,
            baseMana = mana,
            str = str,
            agi = agi,
            intHero = int,
            urlImage = avatarURL,
            urlIcon = iconURL,
            baseArmor = armor,
            moveSpeed = speed
        )
    }

    private fun updateUI() {
        Picasso.get().load(URL_FOR_PICASSO + avatarURL).into(avatarImageView)
        nameTextView.text = name
        prmAttrTextView.text = prmAttr.toUpperCase()

        when(prmAttr) {
            "str" -> prmAttrTextView.setTextColor(resources.getColor(R.color.str_color))
            "agi" -> prmAttrTextView.setTextColor(resources.getColor(R.color.agi_color))
            "int" -> prmAttrTextView.setTextColor(resources.getColor(R.color.int_color))
        }

        attackTypeTextView.text = attackType
        rolesTextView.text = roles
        minDamageTextView.text = minDamage.toString()
        maxDamageTextView.text = maxDamage.toString()
        healthTextView.text = health.toString()
        manaTextView.text = mana.toString()
        strTextView.text = str.toString()
        agiTextView.text = agi.toString()
        intTextView.text = int.toString()
        speedTextView.text = speed.toString()
        armorTextView.text = armor.toString()
    }

    private fun checkIsLocalDb() {
        isLocal = intent.getBooleanExtra(KEY_HERO_LOCAL_DB, false)

        if (isLocal) {
            fabAdd.visibility = View.GONE
            fabRemove.visibility = View.VISIBLE
        }
    }

    fun addHero(view: View) {
            viewModel.insert(hero)
            Toast.makeText(this, "Hero was added!", Toast.LENGTH_SHORT).show()
    }

    fun removeHero(view: View) {
        viewModel.remove(hero)
        Toast.makeText(this, "Hero was removed!", Toast.LENGTH_SHORT).show()
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
    }
}
```

##### <a name="fragments">fragments</a>

Фрагменты используются для отображения такой информации как:
- Список всех персонажей (HeroListFragment.kt)
- Список избранных персонажей (FavoriteListFragment.kt)
- Список всех киберспортивных команд (TeamListFragment.kt)
- Страница с информацией о приложение (AboutFragment.kt)

 **HeroListFragment.kt**

 Отвечает за отображение героев с помощью RecyclerView

 ```kotlin
 class HeroListFragment : Fragment() {

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

        return view
    }

    override fun onStart() {
        super.onStart()

        viewModel.myResponse.observe(this, Observer { response ->
            if (response.isSuccessful) {
                val heroes = response.body()!!
                Log.d(TAG_SUCCESS, heroes[2].urlImage.toString()) // Axe
                setAdapter(heroes)
            } else {
                Log.e(TAG_RESPONSE, response.errorBody().toString())
            }

        })

    }

     private inner class HeroHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        private lateinit var hero: Hero

        private val heroImageView: ImageView = itemView.findViewById(R.id.hero_avatar)
        private val heroName: TextView = itemView.findViewById(R.id.hero_name)
        private val heroRole: TextView = itemView.findViewById(R.id.hero_role)
        private val heroClassIcon: ImageView = itemView.findViewById(R.id.type_damage_icon)

        fun bind(hero: Hero) {
            this.hero = hero
            val url = URL_FOR_PICASSO + this.hero.urlImage

            Picasso.get().load(url).into(heroImageView)
            heroName.text = this.hero.name
            heroRole.text = "${this.hero.roles[0]} and ${this.hero.roles.size - 1}+"

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
            putHeroExtras(intent, hero)
            startActivity(intent)
        }
    }

    private inner class HeroAdapter(var heroes: List<Hero>) : RecyclerView.Adapter<HeroHolder>() {

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

    private fun putHeroExtras(intent: Intent, hero: Hero) {
        intent.putExtra(KEY_HERO_ID, hero.id)
        intent.putExtra(KEY_HERO_NAME, hero.name)
        intent.putExtra(KEY_HERO_ICON, hero.urlIcon)
        intent.putExtra(KEY_HERO_IMG, hero.urlImage)
        intent.putExtra(KEY_HERO_PRM_ATR, hero.primaryAttribute)
        intent.putExtra(KEY_HERO_ATTACK_TYPE, hero.attackType)
        intent.putExtra(Constants.KEY_HERO_ARMOR, hero.baseArmor)
        intent.putExtra(Constants.KEY_HERO_SPEED, hero.moveSpeed)

        var roles = ""
        for (role in hero.roles) {
            roles += "$role "
        }
        intent.putExtra(KEY_HERO_ROLES, roles)
        intent.putExtra(KEY_HERO_MIN_DAMAGE, hero.minDamage)
        intent.putExtra(KEY_HERO_MAX_DAMAGE, hero.maxDamage)
        intent.putExtra(KEY_HERO_MANA_POINT, hero.baseMana)
        intent.putExtra(KEY_HERO_HEALTH_POINT, hero.baseHealth)
        intent.putExtra(KEY_HERO_STR, hero.str)
        intent.putExtra(KEY_HERO_AGI, hero.agi)
        intent.putExtra(KEY_HERO_INT, hero.int)
    }

    companion object {

        fun newInstance(): HeroListFragment {

            return HeroListFragment()
        }
    }
}
 ```

**TeamListFragment.kt**

 Отвечает за отображение команд с помощью RecyclerView


Код TeamListFragment.kt
```kotlin
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
```

**FavoriteListFragment.kt**
Работает идентично HeroListFragment, однако вместо запроса к ручке получает информацию о персонажах из локальной бд. Ниже привел только отличительный код.

```kotlin
class HeroListFragment : Fragment() {

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

        return view
    }

    override fun onStart() {
        super.onStart()

        viewModel.myResponse.observe(this, Observer { response ->
            if (response.isSuccessful) {
                val heroes = response.body()!!
                Log.d(TAG_SUCCESS, heroes[2].urlImage.toString()) // Axe
                setAdapter(heroes)
            } else {
                Log.e(TAG_RESPONSE, response.errorBody().toString())
            }

        })

    }

    companion object {

        fun newInstance(): HeroListFragment {

            return HeroListFragment()
        }
    }
}

```

**FavoriteListFragment.kt** 
Дефолтный фрагмент, ничего не инициализируется. Код показывать смысла нет.

------------

#### <a name="viewmodel">ViewModel</a>

Данный модуль как говорилось ранее, является посредником между View и Model, если говорить простыми словами, этот модуль сообщает View какие данные должны быть отрисованы.

Весь модуль реализован в пакете **viewmodels**, который хранит в себе пакеты с моделями представления для героев и команд.


В целом, реализация модели-представления делится на 2 этапе:
1) Непосредственно создаёт модель
2) Создаём для неё factory


**HeroListViewModel**

Модель, которая передаёт во фрагмент список всех персонажей

```kotlin
class HeroListViewModel(private val repository: DotaRepository): ViewModel() {

    val myResponse: MutableLiveData<Response<List<Hero>>> = MutableLiveData()

    // Coroutines
    fun getHeroes() {
        viewModelScope.launch {
            val response = repository.getHeroList()
            myResponse.value = response
        }
    }
}
```

HeroListViewModelFactory
```kotlin
class HeroListViewModelFactory(
    private val repository: DotaRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HeroListViewModel(repository) as T
    }
}
```

**HeroViewModel.kt**

Модель, которая передаёт в FavoriteListFragment информацию о персонажах из локальной бд, а так же позволяет удалять героев из избранного листа и добавлять их. 

```kotlin
class HeroViewModel(private val repository: DotaRepository) : ViewModel() {

    var allHeroes: LiveData<List<LocalHero>> = repository.getHeroes()

    fun insert(hero: LocalHero) {
        repository.addHero(hero)
    }

    fun remove(hero: LocalHero) {
        repository.removeHero(hero)
    }
}
```

HeroViewModelFactory
```kotlin
class HeroViewModelFactory(
    private val repository: DotaRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HeroViewModel(repository) as T
    }
}
```

**TeamListViewModel**

Модель отвечающая за киберспортивные команды, позволяет передать активити список всех команд и одной конкретной.

```kotlin
class TeamListViewModel(private val repository: DotaRepository): ViewModel() {

    val myResponse: MutableLiveData<Response<List<Team>>> = MutableLiveData()
    val currentTeamResponse: MutableLiveData<Response<Team>> = MutableLiveData()

    fun getTeams() {
        viewModelScope.launch {
            val response = repository.getTeamList()
            myResponse.value = response
        }
    }

    fun getCurrentTeam(teamId: Int) {
        viewModelScope.launch {
            val response = repository.getTeam(teamId)
            currentTeamResponse.value = response
        }
    }
}
```

TeamListViewModelFactory

```kotlin
class TeamListViewModelFactory(
    private val repository: DotaRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TeamListViewModel(repository) as T
    }
}
```

------------

### <a name="singletone">Singletone</a>

Паттерн Одиночка(он же Singletone) используется для репозитория, чтобы в приложение находился ЛИШЬ ОДИН экхемпляр класса **DotaRepository**. 

Репозиторий является посредником между модулем Model и ViewModel, предоставляя последнему доступ к необходимой информации.

В себе содержит все методы для работы с героями и командами.

Код DotaRepository.kt

```kotlin
class DotaRepository private constructor(context: Context) {

    // Room DB
    private val database: HeroDatabase = Room.databaseBuilder(
        context.applicationContext,
        HeroDatabase::class.java,
        DATABASE_NAME
    ).build()


    private val heroDao = database.heroDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun getHeroes(): LiveData<List<LocalHero>> = heroDao.getHeroes()
    fun getHero(id: Int): LiveData<LocalHero?> = heroDao.getHeroById(id)

    fun addHero(hero: LocalHero) {
        executor.execute {
            heroDao.addHero(hero)
        }
    }

    fun removeHero(hero: LocalHero) {
        executor.execute {
            heroDao.removeHero(hero)
        }
    }


    // Retrofit
    suspend fun getHeroList(): Response<List<Hero>> =
        RetrofitInstance.heroApi.getHeroList()

    suspend fun getTeamList(): Response<List<Team>> =
        RetrofitInstance.teamApi.getTeamList()

    suspend fun getTeam(id: Int): Response<Team> =
        RetrofitInstance.teamApi.getTeam(id)

    companion object {
        private var INSTANCE: DotaRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) INSTANCE = DotaRepository(context)
        }

        fun get(): DotaRepository {
            return INSTANCE ?: throw IllegalStateException(REPOSITORY_ERROR_MESSAGE)
        }
    }
}
```

Создание экземпляра репозитория происходит в классе **GAYmerApp.kt**. Тот в свою очередь передаётся в манифест приложения, тем самым экземпляр репозитория создаётся автоматически при первом запуске приложения и существует во всех жизненных циклах.

GAYmerApp.kt
```kotlin
class GAYmerApp: Application() {

    override fun onCreate() {
        super.onCreate()
        DotaRepository.initialize(this)
    }
}
```

------------

## <a name="constants">Константы (package utils)</a>

Здесь представлен класс Constants, который хранит в себе констатны значений такие как базовый url, название тегов, название ключей при передаче аргументов и тд.

**Примечание:** при рефакторинге было бы тоже неплохо разбить этот файл на несколько других.

```kotlin
class Constants {

    companion object {

        const val DATABASE_NAME = "hero-database"

        const val BASE_URL = "https://api.opendota.com/api/"
        const val URL_FOR_PICASSO = "https://api.opendota.com"
        const val REPOSITORY_ERROR_MESSAGE = "DotaRepository must be initialized"
        const val TAG_SUCCESS = "Success!"
        const val TAG_RESPONSE = "Response:"

        const val KEY_TEAM_ID = "teamId"

        const val KEY_HERO_LOCAL_DB = "local"
        const val KEY_HERO_ID = "heroId"
        const val KEY_HERO_NAME = "heroName"
        const val KEY_HERO_ICON = "heroIcon"
        const val KEY_HERO_IMG = "heroImg"
        const val KEY_HERO_PRM_ATR = "heroPrmAtr"
        const val KEY_HERO_ARMOR = "heroArmor"
        const val KEY_HERO_SPEED = "heroSpeed"
        const val KEY_HERO_ATTACK_TYPE = "heroAttackType"
        const val KEY_HERO_ROLES = "heroRoles"
        const val KEY_HERO_MIN_DAMAGE = "heroMinDamage"
        const val KEY_HERO_MAX_DAMAGE = "heroMaxDamage"
        const val KEY_HERO_MANA_POINT = "heroManaPoint"
        const val KEY_HERO_HEALTH_POINT = "heroHealthPoint"
        const val KEY_HERO_STR = "heroSTR"
        const val KEY_HERO_AGI = "heroAGI"
        const val KEY_HERO_INT = "heroINT"

    }
}
```


------------

## <a name="screenshots">Скриншоты приложения</a>

![fragment_hero_list](/docs/hero_list.jpg)
![current_hero](/docs/current_hero_from_hero_list.jpg)
![fragment_team_list](/docs/team_list.jpg)
![current_team](/docs/current_team.jpg)
![favorite_list](/docs/favorite_list.jpg)
![current_favorite](/docs/current_hero_from_favorite_list.jpg)
![about_page](/docs/about_page.jpg)