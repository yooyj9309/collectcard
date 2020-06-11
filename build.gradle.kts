import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.2.5.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    id("org.jmailen.kotlinter") version "2.3.2"
    kotlin("jvm") version "1.3.72"
    kotlin("plugin.spring") version "1.3.72"
    kotlin("plugin.jpa") version "1.3.72"
    jacoco
}

group = "com.rainist"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

sourceSets {
    main {
        java.srcDir("${projectDir}/idl/gen/java")
    }
}

repositories {
    mavenCentral()
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/rainist/kotlin-banksalad")
        credentials {
            username = System.getenv("GH_USERNAME")
            password = System.getenv("GH_DAAS_PACKAGES_TOKEN")
        }
    }
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/rainist/java-banksalad")
        credentials {
            username = System.getenv("GH_USERNAME")
            password = System.getenv("GH_DAAS_PACKAGES_TOKEN")
        }
    }
}

val grpc_spring_boot_version = "3.5.2"

dependencies {
    // TODO(sangmin): java-banksalad github package 구성 후 주석 해제
    // implementation("com.rainist:java-banksalad:1.0.0.RELEASE")
    implementation("com.rainist:kotlin-banksalad:1.0.11.RELEASE")
    implementation("com.rainist:collect:1.0.00.RELEASE")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    runtimeOnly("mysql:mysql-connector-java")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }

    // grpc
    implementation("io.github.lognet:grpc-spring-boot-starter:$grpc_spring_boot_version")

    // db
    implementation("com.vladmihalcea:hibernate-types-52:2.9.2")
    testImplementation("com.h2database:h2")

    // logback json
    implementation("ch.qos.logback.contrib:logback-json-classic:0.1.5")
    implementation("ch.qos.logback.contrib:logback-jackson:0.1.5")

    // sentry
    implementation("io.sentry:sentry-logback:1.7.30")

    // application monitoring
    implementation("io.micrometer:micrometer-registry-statsd:1.4.0")
    implementation("io.micrometer:micrometer-core:1.4.0")


}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

jacoco {
    toolVersion = "0.8.4"
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
        csv.isEnabled = true
        html.isEnabled = true
    }
}
