plugins {
    application
}

repositories {
    jcenter()
    maven(url = "https://m2.dv8tion.net/releases")
}

dependencies {
    implementation("net.dv8tion:JDA:4.2.1_262")
    implementation("io.github.classgraph:classgraph:4.8.102")
}

application {
    mainClass.set("gg.samantha.lily.startup.Main")
}