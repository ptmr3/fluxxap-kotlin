package com.ptmr3.fluxxap

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toTypeName
import java.io.OutputStreamWriter

class KspActionAnnotationProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val actionSymbols = resolver.getSymbolsWithAnnotation(ACTION_ANNOTATION_CLASS)
        val reactionSymbols = resolver.getSymbolsWithAnnotation(REACTION_ANNOTATION_CLASS)
        val failureReactionSymbols = resolver.getSymbolsWithAnnotation(FAILURE_REACTION_ANNOTATION_CLASS)

        val symbols = actionSymbols + reactionSymbols + failureReactionSymbols
        symbols.filterIsInstance<KSClassDeclaration>().forEach { classDeclaration ->
            generateClass(classDeclaration)
        }

        return emptyList()
    }

    private fun generateClass(classDeclaration: KSClassDeclaration) {
        val packageName = classDeclaration.packageName.asString()
        val className = classDeclaration.simpleName.asString()
        val fileName = "${className}Generated"

        val typeSpecBuilder = TypeSpec.classBuilder(fileName)
        val constructor = classDeclaration.primaryConstructor
        if (constructor != null) {
            val constructorSpec = FunSpec.constructorBuilder()
            constructor.parameters.forEach { param ->
                constructorSpec.addParameter(param.name!!.asString(), param.type.resolve().toTypeName())
            }
            typeSpecBuilder.primaryConstructor(constructorSpec.build())
        }

        classDeclaration.getAllFunctions().forEach { function ->
            val funSpecBuilder = FunSpec.builder(function.simpleName.asString())
            function.parameters.forEach { param ->
                funSpecBuilder.addParameter(param.name!!.asString(), param.type.resolve().toTypeName())
            }
            funSpecBuilder.addStatement("println(\"Function \${function.simpleName.asString()} called\")")
            typeSpecBuilder.addFunction(funSpecBuilder.build())
        }

        val fileSpec = FileSpec.builder(packageName, fileName)
            .addType(typeSpecBuilder.build())
            .build()

        val file = codeGenerator.createNewFile(
            Dependencies(false, classDeclaration.containingFile!!),
            packageName,
            fileName
        )
        OutputStreamWriter(file).use { writer ->
            fileSpec.writeTo(writer)
        }
    }

    companion object {
        const val ACTION_ANNOTATION_CLASS = "com.ptmr3.fluxx.annotation.Action"
        const val REACTION_ANNOTATION_CLASS = "com.ptmr3.fluxx.annotation.Reaction"
        const val FAILURE_REACTION_ANNOTATION_CLASS = "com.ptmr3.fluxx.annotation.FailureReaction"
    }
}