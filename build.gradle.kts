plugins {
    application
}

repositories {
    jcenter()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("com.mewna:catnip:8a3ffef")
    implementation("io.github.classgraph:classgraph:4.8.102")
}

application {
    mainClass.set("io.github.samophis.lily.startup.Main")
}