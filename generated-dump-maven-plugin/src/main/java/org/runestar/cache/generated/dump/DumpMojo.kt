package org.runestar.cache.generated.dump

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.squareup.javapoet.*
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import org.runestar.cache.content.def.ItemDefinition
import org.runestar.cache.content.def.NpcDefinition
import org.runestar.cache.content.def.ObjectDefinition
import org.runestar.cache.content.def.SpriteSheetDefinition
import org.runestar.cache.format.BackedStore
import org.runestar.cache.format.ReadableCache
import org.runestar.cache.format.fs.FileSystemStore
import org.runestar.cache.format.net.NetStore
import java.nio.file.Paths
import java.util.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Modifier

@Mojo(name = "dump")
class DumpMojo : AbstractMojo() {

    private companion object {
        const val INDENT = "\t"
    }

    @Parameter(property = "outputPackage", required = true)
    lateinit var outputPackage: String

    @Parameter(defaultValue = "\${project}")
    lateinit var project: MavenProject

    private val outputDir by lazy { Paths.get(project.build.directory, "generated-sources") }

    lateinit var cache: ReadableCache

    override fun execute() {
        val file = Paths.get(project.build.directory).parent.parent.resolve("known-names.json").toFile()
        val names = jacksonObjectMapper().readValue<Set<String>>(file)
        cache = ReadableCache(BackedStore(FileSystemStore.open(), NetStore.open("oldschool1.runescape.com", 172)), names)

        try {
            npcs()
            objects()
            items()
//            sprites()
        } finally {
            cache.close()
        }

        project.addCompileSourceRoot(outputDir.toString())
    }

    private fun npcs() {
        val map = TreeMap<Int, String>()
        for (def in NpcDefinition.Loader(cache).getDefinitions()) {
            map[def.getId()] = def.getDefinition().name
        }
        writeIdsFile("NpcId", map)
    }

    private fun objects() {
        val map = TreeMap<Int, String>()
        for (def in ObjectDefinition.Loader(cache).getDefinitions()) {
            map[def.getId()] = def.getDefinition().name
        }
        writeIdsFile("ObjectId", map)
    }

    private fun items() {
        val map = TreeMap<Int, String>()
        for (def in ItemDefinition.Loader(cache).getDefinitions()) {
            map[def.getId()] = def.getDefinition().name
        }
        writeIdsFile("ItemId", map)
    }

    private val REMOVE_REGEX = "([']|<.*?>)".toRegex()

    private val REPLACE_UNDERSCORE_REGEX = "[- /)(.,!]".toRegex()

    private val REPLACE_DOLLARSIGN_REGEX = "[%&+?]".toRegex()

    private val MULTI_UNDERSCORE_REGEX = "_{2,}".toRegex()

    private val ENDS_UNDERSCORES_REGEX = "(^_+|_+$)".toRegex()

    private fun stringToIdentifier(name: String): String? {
        if (name.equals("null", true)) return null
        if (name.isBlank()) return null
        var n = name.toUpperCase()
                .replace(REMOVE_REGEX, "")
                .replace(REPLACE_UNDERSCORE_REGEX, "_")
                .replace(REPLACE_DOLLARSIGN_REGEX, "\\$")
                .replace(ENDS_UNDERSCORES_REGEX, "")
                .replace(MULTI_UNDERSCORE_REGEX, "_")
        if (!SourceVersion.isName(n)) {
            n = "_$n"
        }
        if (!SourceVersion.isName(n)) {
            log.warn(name)
            return null
        }
        return n
    }

    private fun writeIdsFile(
            className: String,
            names: SortedMap<Int, String>
    ) {
        val typeBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build())

        names.forEach { id, name ->
            val identifierName: String = stringToIdentifier(name) ?: return@forEach
            val finalName = "${identifierName}_$id"

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

    private fun sprites() {
        val map = TreeMap<Int, String>()
        for (def in SpriteSheetDefinition.Loader(cache).getDefinitions()) {
            if (def == null) continue
            val name = def.archiveIdentifier.name ?: continue
            map[def.getId()] = name
        }
        writeIdsFile("SpriteId", map)
    }
}