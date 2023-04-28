object Dependencies {

    val arrowKt = "io.arrow-kt"
    val arrowVersion = "2.0.0-SNAPSHOT"
    val arrowCore = "$arrowKt:arrow-core:$arrowVersion"
    val arrowFxCoroutines = "$arrowKt:arrow-fx-coroutines:$arrowVersion"
    val arrowFxStm = "$arrowKt:arrow-fx-stm:$arrowVersion"

    val kolinx = "org.jetbrains.kotlinx"
    val kotlinxSerializationVersion = "1.4.1"
    val kotlinXSerializationJson = "$kolinx:kotlinx-serialization-json:$kotlinxSerializationVersion"
    val kotlinxCoroutinesVersion = "1.6.4"
    val kotlinXCoroutinesCore = "$kolinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion"

    val kotest = "io.kotest"
    val kotestVersion = "5.5.5"
    val kotestRunnerJunit = "$kotest:kotest-runner-junit5:$kotestVersion"

    val ktorVersion = "2.2.2"
    val ktorServerCore = "io.ktor:ktor-server-core-jvm:$ktorVersion"
    val ktorServerNetty = "io.ktor:ktor-server-netty-jvm:$ktorVersion"
    val ktorTest = "io.ktor:ktor-server-test-host:$ktorVersion"

    val ktorClient = "io.ktor:ktor-client-core:$ktorVersion"
    val ktorClientCio = "io.ktor:ktor-client-cio:$ktorVersion"
    val ktorContentNegotiation = "io.ktor:ktor-client-content-negotiation:$ktorVersion"

    val orgPostgresql = "org.postgresql"
    val postgresqlVersion ="42.5.3"
    val postgresql = "$orgPostgresql:postgresql:$postgresqlVersion"

    val zaxxer = "com.zaxxer"
    val hikariCpVersion = "5.0.1"
    val hikariCp = "$zaxxer:HikariCP:$hikariCpVersion"

    val mockito = "org.mockito.kotlin:mockito-kotlin:4.0.0"

}
