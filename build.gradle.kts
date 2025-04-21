plugins {
    id("java")
}

group = "org.returnoh"
version = "1.0-SNAPSHOT"

val lwjglVersion = "3.3.3"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.joml:joml:1.10.5")

    implementation("org.lwjgl:lwjgl:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-opengl:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-glfw:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-stb:$lwjglVersion")
    implementation("org.lwjgl:lwjgl:${lwjglVersion}:natives-windows")
    implementation("org.lwjgl:lwjgl-opengl:${lwjglVersion}:natives-windows")
    implementation("org.lwjgl:lwjgl-glfw:${lwjglVersion}:natives-windows")
    implementation("org.lwjgl:lwjgl-stb:${lwjglVersion}:natives-windows")
}

tasks.test {
    useJUnitPlatform()
}