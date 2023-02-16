package com.otaz.imdbmovieapp.interactors.movie

import com.otaz.imdbmovieapp.cache.model.MovieSpecEntityMapper
import com.otaz.imdbmovieapp.domain.data.DataState
import com.otaz.imdbmovieapp.domain.model.Movie
import com.otaz.imdbmovieapp.domain.model.MovieSpecs
import com.otaz.imdbmovieapp.network.MovieService
import com.otaz.imdbmovieapp.network.model.MovieSpecDao
import com.otaz.imdbmovieapp.network.model.MovieSpecDtoMapper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * This use case handles all data conversions from the network to the cache so that
 * a view model only ever sees the [Movie] domain model.
 */

class GetMovie(
    private val movieSpecDao: MovieSpecDao,
    private val movieService: MovieService,
    private val entityMapper: MovieSpecEntityMapper,
    private val dtoMapper: MovieSpecDtoMapper,
){
    fun execute(
        apikey: String,
        id: String,
        isNetworkAvailable: Boolean,
    ): Flow<DataState<MovieSpecs>> = flow {
        try {
            emit(DataState.loading())

            var movie = getMovieSpecsFromCache(movieId = id)

            if (movie != null){
                emit(DataState.success(movie))
                // If the movie is null, it means it was not in the cache for some reason.
                // So get it from the network. Theoretically, this is very unlikely because the data is coming from the cache
            } else {
                if (isNetworkAvailable){
                    // Get movie from network and dto -> domain
                    val networkMovieSpecs = getMovieSpecsFromNetwork(
                        apikey = apikey,
                        id = id,
                    )

                    //Insert into cache
                    movieSpecDao.insertMovie(
                        // Map domain -> entity
                        entityMapper.mapFromDomainModel(networkMovieSpecs)
                    )
                }

                // Get from the cache
                movie = getMovieSpecsFromCache(movieId = id)

                // emit and finish
                if (movie != null){
                    emit(DataState.success(movie))
                }else{
                    throw Exception("Unable to get the movie from the cache")
                }
            }
        }catch (e: Exception){
            emit(DataState.error(e.message ?: "Unknown error"))
        }
    }

    // This can throw an exception if there is no network connection
    // This function gets DTOs from the network and converts them to Movie Objects
    private suspend fun getMovieSpecsFromNetwork(
        apikey: String,
        id: String,
    ): MovieSpecs{
        return dtoMapper.mapToDomainModel(
            movieService.get(
                apikey = apikey,
                id = id,
            )
        )
    }

    private suspend fun getMovieSpecsFromCache(
        movieId: String,
    ): MovieSpecs?{
        return movieSpecDao.getMovieById(movieId)?.let { movieSpecEntity ->
            entityMapper.mapToDomainModel(movieSpecEntity)
        }
    }
}