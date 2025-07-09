package com.example

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable


@Serializable
data class Task(val id: Int, val title: String, val director: String, val relaseYear: Int)


object Movies {
    val tasks = mutableListOf<Task>(
        Task(id = 1, title = "Inception", director = "Christopher Nolan", relaseYear = 2010),
        Task(id = 2, title = "Interstellar", director = "Christopher Nolan", relaseYear = 2014),
        Task(id = 3, title = "The Matrix", director = "Lana Wachowski, Lilly Wachowski", relaseYear = 1999)
    )
}


fun Application.configureRouting() {
    routing {
        route("/Movies") {
            get("/search") {
                val rawTitle = call.request.queryParameters["title"]
                val rawDirector = call.request.queryParameters["director"]
                val title = rawTitle?.takeIf { it.isNotBlank() }?.lowercase()
                val director = rawDirector?.takeIf { it.isNotBlank() }?.lowercase()

                if (title == null && director == null) {
                    call.respondText("Not found", status = HttpStatusCode.BadRequest)
                    return@get
                }

                val results = Movies.tasks.filter { task ->
                    val matchTitle = title?.let { task.title.lowercase().contains(it) } ?: false
                    val matchDirector = director?.let { task.director.lowercase().contains(it) } ?: false
                    matchTitle || matchDirector
                }

                if (results.isEmpty()) {
                    call.respondText("Not found", status = HttpStatusCode.NotFound)
                } else {
                    call.respond(results)
                }
            }
            get {

                if (Movies.tasks.isNotEmpty()) {
                    call.respond(Movies.tasks)
                } else {
                    call.respondText("No task found", status = HttpStatusCode.NotFound)
                }
            }
            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                val task = Movies.tasks.find { it.id == id }
                if (task != null) {
                    call.respond(task)
                } else
                    call.respondText("Task not found", status = HttpStatusCode.NotFound)
            }
            post {
                val request = call.receive<Task>()
                val newId = (Movies.tasks.maxByOrNull { it.id }?.id ?: 0) + 1
                val task = Task(id = newId, title = request.title, director = request.director , relaseYear = request.relaseYear)
                Movies.tasks.add(task)
                println("Task list now: ${Movies.tasks}")
                call.respondText("Created", status = HttpStatusCode.Created)
            }
            put("/{id}") {
                val id = call.parameters["id"]!!.toIntOrNull()
                if (id == null) {
                    call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
                    return@put
                }
                val request = call.receive<Task>()
                val index = Movies.tasks.indexOfFirst { it.id == id }
                if (index != -1) {
                    val updatedTask = Task(id = id, title = request.title, director = request.director, relaseYear = request.relaseYear)

                    Movies.tasks[index] = updatedTask
                    call.respondText("Updated", status = HttpStatusCode.OK)
                } else
                    call.respondText("Task not found", status = HttpStatusCode.NotFound)

            }
            delete("/{id}") {
                val id = call.parameters["id"]!!.toIntOrNull()
                if (id == null) {
                    call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
                    return@delete
                }
                val removeTask = Movies.tasks.removeIf { it.id == id }
                if (removeTask) {
                    call.respondText("Deleted", status = HttpStatusCode.OK)
                } else
                    call.respondText("Deleted", status = HttpStatusCode.NotFound)
            }
        }

        get("/{movieId}/reviews") {
            val movieId = call.parameters["movieId"]?.toIntOrNull()
            if (movieId == null) {
                call.respondText("Invalid movie ID", status = HttpStatusCode.BadRequest)
                return@get
            }

            val movie = Movies.tasks.find { it.id == movieId }
            if (movie == null) {
                call.respondText("Movie not found", status = HttpStatusCode.NotFound)
                return@get
            }

            val relatedReviews = Reviews.reviews.filter { it.movieID == movieId }
            val avgScore = if (relatedReviews.isNotEmpty()) {
                relatedReviews.map { it.rating }.average()
            } else 0.0

            call.respond(ReviewsWithAvg(relatedReviews, avgScore))
        }



        route("/reviews") {

            get {
                if (Reviews.reviews.isNotEmpty()) {
                    call.respond(Reviews.reviews)
                } else {
                    call.respondText("No reviews found", status = HttpStatusCode.NotFound)
                }
            }


            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                val review = Reviews.reviews.find { it.id == id }
                if (review != null) {
                    call.respond(review)
                } else {
                    call.respondText("Review not found", status = HttpStatusCode.NotFound)
                }
            }


            post {
                val request = call.receive<Review>()

                // เช็กว่าหนังมีอยู่ใน Movies.tasks หรือไม่
                val movieExists = Movies.tasks.any { it.id == request.movieID }

                if (!movieExists) {
                    call.respondText("Movie with ID ${request.movieID} not found", status = HttpStatusCode.BadRequest)
                    return@post
                }

                // สร้าง review ใหม่
                val newId = (Reviews.reviews.maxByOrNull { it.id }?.id ?: 0) + 1
                val newReview = request.copy(id = newId)

                Reviews.reviews.add(newReview)
                call.respondText("Review created", status = HttpStatusCode.Created)
            }

            put("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
                    return@put
                }
                val request = call.receive<Review>()
                val index = Reviews.reviews.indexOfFirst { it.id == id }
                if (index != -1) {
                    Reviews.reviews[index] = request.copy(id = id)
                    call.respondText("Review updated", status = HttpStatusCode.OK)
                } else {
                    call.respondText("Review not found", status = HttpStatusCode.NotFound)
                }
            }

            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
                    return@delete
                }
                val removed = Reviews.reviews.removeIf { it.id == id }
                if (removed) {
                    call.respondText("Review deleted", status = HttpStatusCode.OK)
                } else {
                    call.respondText("Review not found", status = HttpStatusCode.NotFound)
                }
            }
        }

    }


}
@Serializable
data class Review(val id: Int, val movieID: Int,val reviewName: String,val rating: Int,val comment: String)

object Reviews {
    val reviews = mutableListOf<Review>(
        // 🎬 Inception (id = 1)
        Review(id = 1, movieID = 1, reviewName = "Alice", rating = 5, comment = "Absolutely mind-blowing. Nolan at his best."),
        Review(id = 2, movieID = 1, reviewName = "Bob", rating = 4, comment = "Great concept, a bit confusing at times."),
        Review(id = 3, movieID = 1, reviewName = "Samantha", rating = 5, comment = "Watched it three times and still love it."),
        Review(id = 4, movieID = 1, reviewName = "LeoFan99", rating = 5, comment = "Leonardo DiCaprio nailed it."),
        Review(id = 5, movieID = 1, reviewName = "Thinker", rating = 4, comment = "Made me question my dreams for days."),

        // 🎬 Interstellar (id = 2)
        Review(id = 6, movieID = 2, reviewName = "Marcus", rating = 5, comment = "Visually stunning and deeply emotional."),
        Review(id = 7, movieID = 2, reviewName = "Elena", rating = 4, comment = "Loved the science and music score."),
        Review(id = 8, movieID = 2, reviewName = "AstroFan", rating = 5, comment = "Perfect blend of space and story."),
        Review(id = 9, movieID = 2, reviewName = "CritiqueGuy", rating = 3, comment = "Too long but powerful message."),
        Review(id = 10, movieID = 2, reviewName = "MurphFan", rating = 5, comment = "That scene with Murph... chills."),

        // 🎬 The Matrix (id = 3)
        Review(id = 11, movieID = 3, reviewName = "Neo", rating = 5, comment = "A total game-changer. Welcome to the real world."),
        Review(id = 12, movieID = 3, reviewName = "TrinityX", rating = 5, comment = "The action sequences are legendary."),
        Review(id = 13, movieID = 3, reviewName = "Morpheus", rating = 4, comment = "What if I told you... it's still awesome."),
        Review(id = 14, movieID = 3, reviewName = "Coder42", rating = 5, comment = "As a programmer, I loved every minute."),
        Review(id = 15, movieID = 3, reviewName = "MatrixFan", rating = 5, comment = "Iconic. Changed sci-fi forever.")
    )

}

@Serializable
data class ReviewsWithAvg(
    val reviews: List<Review>,
    val avgScore: Double
)




