package com.runesuite.cache.generated.dump

import com.runesuite.cache.content.def.ItemDefinition
import com.runesuite.cache.content.def.NpcDefinition
import com.runesuite.cache.content.def.ObjectDefinition
import com.runesuite.cache.format.BackedStore
import com.runesuite.cache.format.ReadableCache
import com.runesuite.cache.format.fs.FileSystemStore
import com.runesuite.cache.format.net.NetStore
import com.runesuite.general.updateRevision
import com.squareup.javapoet.*
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import java.nio.file.Paths
import java.util.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Modifier

@Mojo(name = "dump")
class Dump : AbstractMojo() {

    private companion object {
        val INDENT = "\t"
    }

    @Parameter(property = "outputPackage", required = true)
    lateinit var outputPackage: String

    @Parameter(defaultValue = "\${project}")
    lateinit var project: MavenProject

    private val outputDir by lazy { Paths.get(project.build.directory, "generated-sources") }

    lateinit var cache: ReadableCache

    override fun execute() {
        updateRevision()
        cache = ReadableCache(BackedStore(FileSystemStore.open(), NetStore.open()))

        try {
            npcs()
            objects()
            items()
        } finally {
            cache.close()
        }

        project.addCompileSourceRoot(outputDir.toString())
    }

    private fun npcs() {
        val map = TreeMap<Int, String>()
        for (def in NpcDefinition.Loader(cache).getDefinitions()) {
            val name = cleanName(def.getDefinition().name)
            if (name != null) {
                map[def.getId()] = name
            }
        }
        writeFile("NpcId", map)
    }

    private fun objects() {
        val map = TreeMap<Int, String>()
        for (def in ObjectDefinition.Loader(cache).getDefinitions()) {
            val name = cleanName(def.getDefinition().name)
            if (name != null) {
                map[def.getId()] = name
            }
        }
        writeFile("ObjectId", map)
    }

    private fun items() {
        val map = TreeMap<Int, String>()
        for (def in ItemDefinition.Loader(cache).getDefinitions()) {
            val name = cleanName(def.getDefinition().name)
            if (name != null) {
                map[def.getId()] = name
            }
        }
        writeFile("ItemId", map)
    }

    private val REMOVE_REGEX = "([']|<.*?>)".toRegex()

    private val REPLACE_UNDERSCORE_REGEX = "[- /)(.,!]".toRegex()

    private val REPLACE_DOLLARSIGN_REGEX = "[%&+?]".toRegex()

    private val MULTI_UNDERSCORE_REGEX = "_{2,}".toRegex()

    private val ENDS_UNDERSCORES_REGEX = "(^_+|_+$)".toRegex()

    private fun cleanName(name: String): String? {
        if (name.equals("null", true)) return null
        if (name.isBlank()) return null
        var n = name.toUpperCase()
                .replace(REMOVE_REGEX, "")
                .replace(REPLACE_UNDERSCORE_REGEX, "_")
                .replace(REPLACE_DOLLARSIGN_REGEX, "\\$")
                .replace(ENDS_UNDERSCORES_REGEX, "")
                .replace(MULTI_UNDERSCORE_REGEX, "_")
        if (!SourceVersion.isName(n)) {
            n = '_' + n
        }
        if (!SourceVersion.isName(n)) {
            log.warn(name)
            return null
        }
        return n
    }

    private fun writeFile(
            className: String,
            names: SortedMap<Int, String>
    ) {
        val typeBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build())

        val namesSet = HashSet<String>()

        names.forEach { id, name ->
            val finalName = if (name in namesSet) {
                "${name}_$id"
            } else {
                namesSet.add(name)
                name
            }

            typeBuilder.addField(
                    FieldSpec.builder(TypeName.INT, finalName, Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                            .initializer(id.toString())
                            .build()
            )
        }

        JavaFile.builder(outputPackage, typeBuilder.build())
                .indent(INDENT)
                .build()
                .writeTo(outputDir)
    }
}