$jdkHome = 'C:\Program Files\Java\jdk-21.0.12'

if (-not (Test-Path -LiteralPath (Join-Path $jdkHome 'bin\java.exe'))) {
    throw "JDK 21 was not found at: $jdkHome"
}

$previousJavaHome = $env:JAVA_HOME
$previousPath = $env:Path
$mavenExitCode = 1

try {
    $env:JAVA_HOME = $jdkHome
    $env:Path = "$jdkHome\bin;$previousPath"
    & mvn @args
    $mavenExitCode = $LASTEXITCODE
}
finally {
    $env:JAVA_HOME = $previousJavaHome
    $env:Path = $previousPath
}

exit $mavenExitCode

