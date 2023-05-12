package ch.romix.stripe.graalvm

import com.google.common.reflect.ClassPath
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.stripe.model.StripeCollection
import java.io.File
import java.io.IOException
import java.lang.reflect.Field
import java.net.URISyntaxException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

fun main() {
    val content = getReflectConfigContent()
    writeReflectConfigFile(content)
}

private const val PACKAGE_ROOT = "com.stripe"

@Throws(URISyntaxException::class, ClassNotFoundException::class)
private fun getReflectConfigContent(): String {
    val allClassDescriptions = getAllClasses()
    val gson = GsonBuilder().setPrettyPrinting().create()
    return gson.toJson(allClassDescriptions)
}

@Throws(IOException::class)
private fun writeReflectConfigFile(serialized: String) {
    val reflectConfigPath = Paths
        .get("./src/main/resources/META-INF/native-image/ch.romix/stripe-java/reflect-config.json")
    val file = reflectConfigPath.toFile()
    file.parentFile.mkdirs()
    file.createNewFile()
    Files.newBufferedWriter(reflectConfigPath).use { writer -> writer.write(serialized) }
}

@Throws(ClassNotFoundException::class)
private fun getAllClasses(): ArrayList<ReflectionClassDescription> {
    val cp: ClassPath = ClassPath.from(Thread.currentThread().contextClassLoader)
    val classes = ArrayList<ReflectionClassDescription>()
    for (info in cp.getTopLevelClassesRecursive(PACKAGE_ROOT)) {
        val potentialClass = info.load()
        if (isUsedWithReflection(potentialClass)) {
            classes.add(ReflectionClassDescription(info.name))
            handleInnerClasses(potentialClass, classes);
        }
    }
    return classes
}
@Throws(ClassNotFoundException::class)
private fun handleInnerClasses(clazz: Class<*>, classes:ArrayList<ReflectionClassDescription>) {
    for (declaredClass in clazz.declaredClasses ) {
        classes.add(ReflectionClassDescription(declaredClass.name))
        // Handle Recursive Inner classes
        handleInnerClasses(declaredClass, classes);
    }
}

private fun isUsedWithReflection(classToInspect: Class<*>): Boolean {
    val fieldWithSerializationAnnotation = Arrays
        .stream(classToInspect.declaredFields)
        .filter { field: Field ->
            field.isAnnotationPresent(
                SerializedName::class.java
            )
        }
        .findAny()
    val subClassOfStripeCollection = classToInspect.superclass == StripeCollection::class.java
    return fieldWithSerializationAnnotation.isPresent || subClassOfStripeCollection
}