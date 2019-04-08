# PopularMoviesApp
Antes de inicar nos debemos dirigir al archivo buil.gradle y agregar las claves API donde se necesitan en el nivel de aplicacion:

    buildTypes.each {
        it.buildConfigField 'String', 'OPEN_THE_MOVIE_DB_API_KEY', "API_MOVIES"
        it.buildConfigField 'String', 'YOUTUBE_API', "API_YOUTUBE"
    }

Se debe reemplazar API_MOVIES o API_YOUTUBE por una cadena de la siguiente forma Ejm:  \"15ca5050ed87e560dz45205968f51a0d\"

## Caracteristicas
* La aplicacion solo esta diseñada para smarthphone. Tablet no esta disponible por el tiempo.<br>
* Busqueda por voz o por texto(online u offline).<br>
* A pesar de que no hay "paging" la busqueda puede requerir nuevas peliculas ademas a las que se muestran en el adapter.<br>
* Ajuste de preferencias de tipo de peliculas en el menu.<br>
* Para salir de modo offline se debe refrescar cambiando en el menu el tipo de pelicula. Se plantea corregir esto con un BroadcastReciever que nos ayude a actualizar el estado de la red cuando este cambia.

## Respuestas
### 1-) Capas
1. Room: Se utiliza esta libreria para guardar los datos descargados desde internet. Room hace parte de  los Android Architecture Components de Android y permite el llamado de comandos SQLite de manera sencilla.
1. Retrofit: Cliente Http para la descarga de datos desde el servidor de peliculas.
1. LiveData: Para cada llamado al servidor desde Retrofit o a la base de datos se utiliza para detectar los cambios de los datos para actualizarlos en la UI correspondiente de la aplicacion. 
1. ViewModel: Los datos o requerimientos se encapsulan en los ViewModels para que sobrevivan a los cambios de orientacion de pantalla y no se solicite la informacion dos veces ya sea desde la base de datos o desde el servidor.
### 2-) Clases

1. data/
   1. AppDatabase
   1. AppExecutors
   1. CacheMovieData
   1. FavoritesDao
   1. MovieData
   1. Result
   1. ResultReviews
   1. Results
   1. ResultTrailers
   1. Review
   1. Reviews
   1. Trailer
   1. Trailers
   
1. network/
   1. MovieService
   1. RetroClassMainListView
   
1. BooleanJ
1. DetailActivity
1. DetailActivityViewModel
1. DetailActivityViewModelFactory
1. FavoriteMovieAdapter
1. FetchViewModel
1. MainActivity
1. MovieAdapter
1. MoviePagedAdapter
1. ResultDataSource
1. ResultDataSourceFactory
1. ResultViewModel
1. ReviewsAdapter
1. SettingsActivity
1. SettingsFragment
1. TrailersAdapter
1. YoutubeActivityPlayer
   


   


      
