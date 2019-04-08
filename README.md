# PopularMoviesApp
Antes de iniciar nos debemos dirigir al archivo buil.gradle y agregar las claves API donde se necesitan en el nivel de aplicación:

    buildTypes.each {
        it.buildConfigField 'String', 'OPEN_THE_MOVIE_DB_API_KEY', "API_MOVIES"
        it.buildConfigField 'String', 'YOUTUBE_API', "API_YOUTUBE"
    }

Se debe reemplazar API_MOVIES o API_YOUTUBE por una cadena de la siguiente forma Ejm:  \"15ca5050ed87e560dz45205968f51a0d\"

## características
* La aplicación solo esta diseñada para smarthphone. Tablet no esta disponible por el tiempo.<br>
* Búsqueda por voz o por texto(online u offline).<br>
* A pesar de que no hay "paging" la busqueda puede requerir nuevas peliculas ademas a las que se muestran en el adapter.<br>
* Ajuste de preferencias de tipo de películas en el menú.<br>
* Para salir de modo offline se debe refrescar cambiando en el menu el tipo de pelicula. Se plantea corregir esto con un BroadcastReciever que nos ayude a actualizar el estado de la red cuando este cambia.

## Respuestas
### 1-) Capas
1. Room: Se utiliza esta libreria para guardar los datos descargados desde internet. Room hace parte de  los Android Architecture Components de Android y permite el llamado de comandos SQLite de manera sencilla.
1. Retrofit: Cliente Http para la descarga de datos desde el servidor de peliculas.
1. LiveData: Para cada llamado al servidor desde Retrofit o a la base de datos se utiliza para detectar los cambios de los datos para actualizarlos en la UI correspondiente de la aplicacion. 
1. ViewModel: Los datos o requerimientos se encapsulan en los ViewModels para que sobrevivan a los cambios de orientación de pantalla y no se solicite la informacion dos veces ya sea desde la base de datos o desde el servidor.
### 2-) Clases

1. data/
   1. AppDatabase: Clase encardada de crear una instancia singleton para gestionar la base de datos utilizando Room.
   1. AppExecutors: Clase encargada de generar Executors para ejecutar las operaciones CRUD para la base de datos en hilos.
   1. CacheMovieData: Tabla "cache_data" representada en Clase para ser cargada en Room
   1. FavoritesDao: Interfaz de la Base de datos para asociar metodos a los comandos SQLite requeridos en la app
   1. MovieData: Tabla "favorites" representada en Clase para ser cargada en Room
   1. Result: POJO para asociar JSON correspondiente a cada película descargada a través de Retrofit
   1. ResultReviews: POJO para asociar respuesta JSON principal obtenida desde Retrofit cuando se piden los comentarios de la película
   1. Results:POJO para asociar respuesta JSON principal obtenida desde Retrofit cuando se piden las películas por categoría
   1. ResultTrailers: POJO para asociar respuesta JSON principal obtenida desde Retrofit cuando se piden los trailers de la película
   1. Review: POJO para asociar JSON correspondiente a cada comentario descargado a través de Retrofit
   1. Reviews: tabla "cache_reviews" Cache para guardar Reviews en Room.
   1. Trailer: POJO para asociar JSON correspondiente a cada comentario descargado a través de Retrofit
   1. Trailers:tabla "cache_trailers" Cache para guardar Reviews en Room.
   
1. network/
   1. MovieService: Interfaz que define los URL que solicitara Retrofit del servidos asociándolos a métodos
   1. RetroClassMainListView: Clase encargada de solicitar la instancia singleton Retrofit para ser generada una única vez.
   
1. BooleanJ: Clase utilizada para utilizar boolean como objeto para hacerlo compatible con LiveData
1. DetailActivity: Actividad que muestra los detalles de la película.
1. DetailActivityViewModel: Datos y operaciones encapsulados en ViewModel que serán llamados desde DetailActivity y que sobreviran a los ciclos de vida de la actividad .
1. DetailActivityViewModelFactory: Constructor de ViewModel para DetailsActivity
1. FavoriteMovieAdapter: Adapter para mostrar las peliculas favoritas.
1. FetchViewModel:Datos y operaciones encapsulados en ViewModel que serán llamados desde MainActivity y que sobreviran a los ciclos de vida de la actividad .
1. MainActivity: Actividad principal
1. MovieAdapter: Adapter en donde se garga la respuesta online (Retrofit) u offlinwe desde base de datos (Room)
1. MoviePagedAdapter: Adapter para realizar pagination (No se alcanzo a utilizar)
1. ResultDataSource:Fuente de datos para MoviePagerAdapter para realizar paging (No se alcanzo a utilizar)
1. ResultDataSourceFactory:Constructor de LiveData para MoviePagerAdapter para realizar pagination (No se alcanzo a utilizar)
1. ResultViewModel:ViewModel de reemplazo a  FetchViewModel para paging (No se alcanzo a utilizar)
1. ReviewsAdapter: Adapter para comentarios en DetailsActivity
1. SettingsActivity: Actividad para preferencias
1. SettingsFragment: fragmento de preferencias cargado a SettingFragment
1. TrailersAdapter: Adapter para el recyclerView de Trailers en DetailsActivity
1. YoutubeActivityPlayer: Actividad que reproduce videos de Youtube cuando se seleccionan desde el recyclerView en DetailsActivity.

### 3-) Principio de responsabilidad única
Es un principio de programación que establece que cada clase o función debe tener una responsabilidad especifica sobre una parte única del software, y que la responsabilidad debe estar completamente encapsulada por la clase.

### 4-) características de un buen código o código limpio
1.  Legibilidad: el código debe ser comodo para leer. Muy bien organizado.
1.  Simple: Debe estar modularizado y que cada clase haga una cosa de acuerdo con el principio de responsabilidad única.
1.  Testeable: El código debe tener pruebas que permitan dar cuenta de las respuestas deseadas del código
1.  Nombres relevantes para las variables y funciones
1.  Evitar duplicidad en el código.





   


   


      
