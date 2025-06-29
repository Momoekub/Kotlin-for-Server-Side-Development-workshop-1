import org.example.Product
import kotlin.test.Test
import kotlin.test.assertEquals
import org.example.celsiusToFahrenheit
import org.example.countProductsByCategory
import org.example.kilometersToMiles
import org.example.products
import kotlin.test.assertFailsWith
import org.example.testcategorymore500
class WorkshopTest {

    // --- Tests for Workshop #1: Unit Converter ---

    // celsius input: 20.0
    // expected output: 68.0
    @Test
    fun `test celsiusToFahrenheit with positive value`() {
        // Arrange: ตั้งค่า input และผลลัพธ์ที่คาดหวัง
        val celsiusInput = 20.0
        val expectedFahrenheit = 68.0

        // Act: เรียกใช้ฟังก์ชันที่ต้องการทดสอบ
        val actualFahrenheit = celsiusToFahrenheit(celsiusInput)

        // Assert: ตรวจสอบว่าผลลัพธ์ที่ได้ตรงกับที่คาดหวัง
        assertEquals(expectedFahrenheit, actualFahrenheit, 0.001, "20°C should be 68°F")
    }

    // celsius input: 0.0
    // expected output: 32.0
    @Test
    fun `test celsiusToFahrenheit with zero`() {
        val celsiusInput = 0.0
        val expectedFahrenheit = 32.0

        assertEquals(expectedFahrenheit, celsiusToFahrenheit(celsiusInput),"0 องศาเซลเซียส ควรจะ 32 ฟาเรนไฮต์")
    }

    // celsius input: -10.0
    // expected output: 14.0
    @Test
    fun `test celsiusToFahrenheit with negative value`() {
        val celsiusInput = -10.0
        val expectedFahrenheit = 14.0

        assertEquals(expectedFahrenheit, celsiusToFahrenheit(celsiusInput),"-10 องศาเซลเซียส ควรจะ 14 ฟาเรนไฮต์")
    }

    // test for kilometersToMiles function
    // kilometers input: 1.0
    // expected output: 0.621371
    @Test
    fun `test kilometersToMiles with one kilometer`() {
        val kilometersInput = 1.0
        val expectedMiles = 0.621371

        val actualMiles = kilometersToMiles(kilometersInput)
        assertEquals(expectedMiles, actualMiles, 0.001,"1กิโลเมตรควรจะเท่ากับ0.621371")
    }

    // --- Tests for Workshop #1: Unit Converter End ---

    // --- Tests for Workshop #2: Data Analysis Pipeline ---
    // ทำการแก้ไขไฟล์ Workshop2.kt ให้มีฟังก์ชันที่ต้องการทดสอบ
    // เช่น ฟังก์ชันที่คำนวณผลรวมราคาสินค้า Electronics ที่ราคา > 500 บาท
    // ในที่นี้จะสมมุติว่ามีฟังก์ชันชื่อ calculateTotalElectronicsPriceOver500 ที่รับ List<Product> และคืนค่า Double
    // จงเขียน test cases สำหรับฟังก์ชันนี้ โดยตรวจสอบผลรวมราคาสินค้า Electronics ที่ราคา > 500 บาท
    @Test
    fun `test total electronics price over 500 from Workshop2 products`() {
        val expected = 35000.0 + 25000.0 + 7500.0 + 1800.0  // จากรายการสินค้าที่คุณมี
        val actual = testcategorymore500(products)

        assertEquals(expected, actual, 0.001, "ราคาสินค้า Electronics ที่ราคา > 500 รวมควรตรงกัน")
    }


    // จงเขียน test cases เช็คจำนวนสินค้าที่อยู่ในหมวด 'Electronics' และมีราคามากกว่า 500 บาท
    //
    @Test
    fun `test countProductsByCategory`() {
        val expected = 4  // จากข้อมูลที่คุณให้ มี 5 ตัวใน Electronics
        val actual = countProductsByCategory(products, "Electronics")

        assertEquals(expected, actual, "ควรมีสินค้าในหมวด Electronics ทั้งหมด 4 ชิ้น")
    }


    // --- Tests for Workshop #2: Data Analysis Pipeline End ---


}