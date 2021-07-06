plugins {
    id("com.android.library")
    kotlin("android")
    `maven-publish`
}

android {
    compileSdkVersion(30)

    defaultConfig {
        minSdkVersion(16)
        targetSdkVersion(30)
        versionCode = Constants.VERSION_CODE
        versionName = Constants.VERSION_NAME
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Constants.KOTLIN_VERSION}")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:${Constants.KOTLIN_VERSION}")
    testImplementation("junit:junit:4.13.2")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {

                from(components["release"])

                groupId = "dev.hirogakatageri.rtp"
                artifactId = "rtmp"
                version = Constants.VERSION_NAME

                pom {
                    name.set("RTMP")
                    description.set("RTMP Module by pedroSG94")

                    licenses {
                        license {
                            name.set("Apache License 2.0")
                            url.set("https://github.com/pedroSG94/rtmp-rtsp-stream-client-java/blob/master/LICENSE.txt")
                        }
                    }

                    developers {
                        developer {
                            id.set("pedroSG94")
                            name.set("Pedro Sánchez")
                            email.set("pedrossgg1994@gmail.com")
                        }
                    }
                }
            }
        }
        repositories {
            maven {
                name = "GithubPackages"
                url = uri("https://maven.pkg.github.com/HirogaKatageri/rtmp-rtsp-stream-client-java")
                credentials {
                    username = System.getenv("GITHUB_USER")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
}
