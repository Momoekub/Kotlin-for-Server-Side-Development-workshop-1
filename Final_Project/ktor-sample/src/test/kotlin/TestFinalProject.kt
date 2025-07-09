import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import com.example.Movies
import com.example.Reviews

class TestFinalProject {

    // --- Tests for Movies data object ---

    @Test
    fun `test get all movies count`() {
        val allMovies = Movies.tasks
        assertEquals(3, allMovies.size, "ควรมีหนังทั้งหมด 3 เรื่อง")
    }

    @Test
    fun `test search movie by title`() {
        val searchTitle = "inception"
        val results = Movies.tasks.filter { it.title.contains(searchTitle, ignoreCase = true) }
        assertTrue(results.isNotEmpty(), "ควรเจอหนังที่มีชื่อ contain $searchTitle")
        assertEquals("Inception", results.first().title)
    }

    @Test
    fun `test get movie by id`() {
        val movie = Movies.tasks.find { it.id == 2 }
        assertEquals("Interstellar", movie?.title)
    }

    // --- Tests for Reviews data object ---

    @Test
    fun `test reviews count for movie id 1`() {
        val reviewsForMovie1 = Reviews.reviews.filter { it.movieID == 1 }
        assertTrue(reviewsForMovie1.size >= 5, "ควรมีรีวิวสำหรับหนัง id 1 อย่างน้อย 5 รายการ")
    }

    @Test
    fun `test average rating for movie id 1`() {
        val reviewsForMovie1 = Reviews.reviews.filter { it.movieID == 1 }
        val avg = reviewsForMovie1.map { it.rating }.average()
        assertTrue(avg >= 4.0, "คะแนนเฉลี่ยของหนัง id 1 ควรมากกว่าหรือเท่ากับ 4.0")
    }

    // --- Tests for adding new movie to list ---

    @Test
    fun `test add new movie`() {
        val originalSize = Movies.tasks.size
        val newMovie = com.example.Task(id = 100, title = "Test Movie", director = "Test Director", relaseYear = 2025)
        Movies.tasks.add(newMovie)
        assertEquals(originalSize + 1, Movies.tasks.size)
        assertTrue(Movies.tasks.any { it.title == "Test Movie" })
        // cleanup
        Movies.tasks.removeIf { it.id == 100 }
    }

    // --- Tests for adding new review ---

    @Test
    fun `test add new review`() {
        val originalSize = Reviews.reviews.size
        val newReview = com.example.Review(id = 100, movieID = 1, reviewName = "Tester", rating = 5, comment = "Excellent!")
        Reviews.reviews.add(newReview)
        assertEquals(originalSize + 1, Reviews.reviews.size)
        assertTrue(Reviews.reviews.any { it.reviewName == "Tester" })
        // cleanup
        Reviews.reviews.removeIf { it.id == 100 }
    }
}
