import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.util.Properties
import java.io.FileInputStream

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val kmongo_version: String by project
val kgraphql_version: String by project
//import at.favre.lib.crypto.bcrypt.BCrypt
val bcrypt_version: String by project
val bcrypt_version_toxi: String by project
val koin_version: String by project

plugins {
    application
    kotlin("jvm") version "1.5.31"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.0"
    id("com.github.johnrengelman.shadow") version "5.2.0"
}


group = "cut.the.crap"
version = "0.0.1"
application {
    mainClass.set("io.ktor.server.netty.EngineMain")
    project.setProperty("mainClassName", mainClass.get())
//    applicationDefaultJvmArgs = listOf("-Dfoooooo=ttttt")
//    run {
//
//        val p = Properties()
//        p.load(FileInputStream(file("../keys/vars")))
//        applicationDefaultJvmArgs = listOf("-Djwt.secrete=secret")
//    }
}



repositories {
    mavenCentral()
    gradlePluginPortal()
    maven {
        url = uri("https://jitpack.io")
    }

}




val sshAntTask = configurations.create("sshAntTask")

dependencies {

    implementation ("com.github.leiter:data_contract:0.3-SNAPSHOT")

    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")

    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-websockets:$ktor_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")
    implementation("io.ktor:ktor-server-sessions:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation ("io.ktor:ktor-auth:$ktor_version")
    implementation ("io.ktor:ktor-auth-jwt:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    testImplementation(kotlin("test"))

    // KMongo
    implementation("org.litote.kmongo:kmongo:$kmongo_version")
    implementation("org.litote.kmongo:kmongo-coroutine:$kmongo_version")

    implementation("com.apurebase:kgraphql:$kgraphql_version")
    implementation("com.apurebase:kgraphql-ktor:$kgraphql_version")

    implementation("at.favre.lib:bcrypt:$bcrypt_version")
//    implementation ("com.ToxicBakery.library.bcrypt:bcrypt:$bcrypt_version_toxi")

    // https://mvnrepository.com/artifact/io.ktor/ktor-network-tls-certificates-jvm
    implementation("io.ktor:ktor-network-tls-certificates:$ktor_version")


    // Koin core features
    implementation("io.insert-koin:koin-core:$koin_version")
    implementation("io.insert-koin:koin-ktor:$koin_version")
    implementation("io.insert-koin:koin-logger-slf4j:$koin_version")

    sshAntTask("org.apache.ant:ant-jsch:1.9.2")
}

tasks.withType<ShadowJar> {
    manifest {
        attributes(
            "Main-Class" to application.mainClass.get()
        )
    }
}

//tasks.register("myTask") {
//
//    dependsOn(mutableListOf("build"))
//
//    doLast {
//        print("sdfasdfasdfasdfsadfsadfasdfsadf")
//        tasks.create<JavaExec>("myTaskExec") {
//            main = "io.ktor.server.netty.EngineMain"
//            args = mutableListOf("foo", "bar")
//            classpath = sourceSets.main.get().runtimeClasspath
//        }.exec()
//    }
//
//}

ant.withGroovyBuilder {
    "taskdef"(
        "name" to "scp",
        "classname" to "org.apache.tools.ant.taskdefs.optional.ssh.Scp",
        "classpath" to configurations.get("sshAntTask").asPath
    )
    "taskdef"(
        "name" to "ssh",
        "classname" to "org.apache.tools.ant.taskdefs.optional.ssh.SSHExec",
        "classpath" to configurations.get("sshAntTask").asPath
    )
}

task("deploy") {
    dependsOn("clean", "shadowJar")
    val p = Properties()
    p.load(FileInputStream(file("../keys/vars")))
    val hostAddress = p["hostname"]
    ant.withGroovyBuilder {
        doLast {
            val knownHosts = File.createTempFile("knownhosts", "txt")
            val user = "root"
            val pk = file("../keys/ktor_1")
            val jarFileName = "cut.the.crap.ktor-server-$version-all.jar"
            try {
                "scp"(
                    "file" to file("build/libs/$jarFileName"),
                    "todir" to "$user@$hostAddress:/root/chat",
                    "keyfile" to pk,
                    "trust" to true,
                    "knownhosts" to knownHosts
                )
                "ssh"(
                    "host" to hostAddress,
                    "username" to user,
                    "keyfile" to pk,
                    "trust" to true,
                    "knownhosts" to knownHosts,
                    "command" to "mv /root/chat/$jarFileName /root/chat/chat-server.jar"
                )
                "ssh"(
                    "host" to hostAddress,
                    "username" to user,
                    "keyfile" to pk,
                    "trust" to true,
                    "knownhosts" to knownHosts,
                    "command" to "systemctl stop ktor1"
                )
                "ssh"(
                    "host" to hostAddress,
                    "username" to user,
                    "keyfile" to pk,
                    "trust" to true,
                    "knownhosts" to knownHosts,
                    "command" to "systemctl start ktor1"
                )
            } finally {
                knownHosts.delete()
            }
        }
    }
}
