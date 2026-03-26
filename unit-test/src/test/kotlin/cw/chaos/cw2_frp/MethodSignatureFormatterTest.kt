package cw.chaos.cw2_frp

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MethodSignatureFormatterTest {

    private val formatter = MethodSignatureFormatter()

    @Nested
    @DisplayName("方法签名格式化")
    inner class FormatMethodSignature {

        @Test
        @DisplayName("无参数方法")
        fun `should format method with no parameters`() {
            val result = formatter.format("main", emptyList())

            assertEquals("main()", result)
        }

        @Test
        @DisplayName("单参数方法")
        fun `should format method with single parameter`() {
            val result = formatter.format("process", listOf("String"))

            assertEquals("process(String)", result)
        }

        @Test
        @DisplayName("多参数方法")
        fun `should format method with multiple parameters`() {
            val result = formatter.format("resolveValue", listOf("String", "int"))

            assertEquals("resolveValue(String, int)", result)
        }

        @Test
        @DisplayName("复杂泛型类型参数")
        fun `should format method with generic type parameters`() {
            val result = formatter.format("process", listOf("List<String>", "Map<String, Object>"))

            assertEquals("process(List<String>, Map<String, Object>)", result)
        }

        @Test
        @DisplayName("Kotlin 类型参数")
        fun `should format method with kotlin types`() {
            val result = formatter.format("calculate", listOf("Int", "Double", "String"))

            assertEquals("calculate(Int, Double, String)", result)
        }

        @Test
        @DisplayName("数组类型参数")
        fun `should format method with array type parameter`() {
            val result = formatter.format("process", listOf("String[]", "int[]"))

            assertEquals("process(String[], int[])", result)
        }
    }
}
