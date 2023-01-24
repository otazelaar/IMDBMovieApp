package com.otaz.imdbmovieapp.presentation.movie_list

sealed class MovieListEvent {

    object NewSearchEvent: MovieListEvent()
    object NextPageEvent: MovieListEvent()

    // Restore after process death
    object RestoreStateEvent: MovieListEvent()

}