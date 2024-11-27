package com.ptmr3.fluxxap

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class KspActionAnnotationProcessorTest {

    @OptIn(ExperimentalCompilerApi::class)
    @Test
    fun testKspActionAnnotationProcessor() {
        val code = """
            package com.ptmr3.fluxxap

            import com.ptmr3.fluxx.annotation.Action
            import com.ptmr3.fluxx.annotation.Reaction
            import com.ptmr3.fluxx.annotation.FailureReaction

            class TestClass {
                @Action
                fun testAction() {}

                @Reaction
                fun testReaction() {}

                @FailureReaction
                fun testFailureReaction() {}
            }
        """.trimIndent()

        val testProcessor = KspActionAnnotationProcessorProvider()
        val result = KotlinCompilation().apply {
            sources = listOf(SourceFile.kotlin("Test.kt", code))
            symbolProcessorProviders = listOf(testProcessor)
            inheritClassPath = true
            messageOutputStream = System.out
        }.compile()

        assertEquals(result.exitCode, KotlinCompilation.ExitCode.OK)
    }
}

class KspActionAnnotationProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return KspActionAnnotationProcessor(
            codeGenerator = environment.codeGenerator,
            logger = environment.logger
        )
    }
}