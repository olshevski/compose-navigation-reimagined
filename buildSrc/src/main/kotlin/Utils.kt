import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

// version catalog access from precompiled scripts
val Project.libs: LibrariesForLibs get() = extensions.getByType()