plugins {
    id("java-library")
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies{
    //implementation("io.github.whitemagic2014:tts-edge-java:1.2.6")
    implementation(libs.fastjson2)
    implementation(libs.java.websocket.java.websocket)
    implementation(libs.commons.lang3)
    implementation(libs.commons.text)
}
