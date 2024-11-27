package com.ptmr3.fluxxap

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class KspActionAnnotationProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return KspActionAnnotationProcessor(
            codeGenerator = environment.codeGenerator,
            logger = environment.logger
        )
    }
}